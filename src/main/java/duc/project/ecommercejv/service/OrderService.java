package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.order.CheckoutRequest;
import duc.project.ecommercejv.dto.order.OrderItemResponse;
import duc.project.ecommercejv.dto.order.OrderResponse;
import duc.project.ecommercejv.dto.order.UpdateOrderStatusRequest;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.entity.cart.Cart;
import duc.project.ecommercejv.entity.cart.CartItem;
import duc.project.ecommercejv.entity.order.*;
import duc.project.ecommercejv.entity.product.Product;
import duc.project.ecommercejv.exception.BadRequestException;
import duc.project.ecommercejv.exception.NotFoundException;
import duc.project.ecommercejv.repository.cart.CartItemRepository;
import duc.project.ecommercejv.repository.cart.CartRepository;
import duc.project.ecommercejv.repository.order.*;
import duc.project.ecommercejv.repository.product.ProductPriceRepository;
import duc.project.ecommercejv.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Set<String> PRIVILEGED_ROLES = Set.of("MANAGER","STAFF","SYSTEM_ADMIN");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final StatusRepository statusRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;

    @Transactional
    public OrderResponse checkout(User user, CheckoutRequest req) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new BadCredentialsException("Cart is empty"));
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Address address = addressRepository.findById(req.getAddressId()).orElseThrow(() -> new NotFoundException("Cannot find address"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Cannot find address");
        }

        Status pendingStatus = statusRepository.findByName("PENDING").orElseThrow(() -> new IllegalStateException("Missing status PENDING in DB, recheck seed"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(pendingStatus);
        order.setAddress(address);
        order.setShippingMethod(req.getShippingMethod());
        order.setNote(req.getNote());
        order.setShippingFee(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem: cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId()).orElseThrow(() -> new NotFoundException("Product is not exist"));
            if (cartItem.getQuantity() > product.getStock()) {
                throw new BadRequestException("Product \"" + product.getName() + "\" is not enough stock (remaining " + product.getStock() + ")");
            }

            BigDecimal unitPrice = productPriceRepository.findTopByProductIdOrderByPrice_CreatedAtDesc(product.getId())
                    .map(pp -> pp.getPrice().getPrice())
                    .orElseThrow(() -> new BadRequestException("Product \"" + product.getName() + "\" do not have price, cannot get order"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(unitPrice);
            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total.add(order.getShippingFee()));
        orderRepository.save(order);

        saveHistory(order, pendingStatus, user, "Order has been created");
        cartItemRepository.deleteByCartId(cart.getId());
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> listOrders(User user, Pageable pageable) {
        Page<Order> orders = isPrivileged(user) ? orderRepository.findAll(pageable) : orderRepository.findByUserId(user.getId(), pageable);
        return orders.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(User user, Long orderId) {
        Order order = getOrderOr404(orderId);
        if (!isPrivileged(user) && !order.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Cannot find order");
        }
        return toResponse(order);
    }

    public OrderResponse updateStatus(User user, Long orderId, UpdateOrderStatusRequest req) {
        Order order = getOrderOr404(orderId);

        Status newStatus = statusRepository.findByName(req.getStatusName().toUpperCase()).orElseThrow(() -> new BadRequestException("Status is invalid: " + req.getStatusName()));
        order.setStatus(newStatus);
        orderRepository.save(order);
        saveHistory(order, newStatus, user, req.getNote());

        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(User user, Long orderId) {
        Order order = getOrderOr404(orderId);

        if (!isPrivileged(user) && !order.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Cannot find order");
        }
        if (!"PENDING".equals(order.getStatus().getName())) {
            throw new BadRequestException("Only cancel the order in pending status");
        }

        Status cancelledStatus = statusRepository.findByName("CANCELLED").orElseThrow(() -> new IllegalStateException("Lack of status CANCELLED in DB"));

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item: items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(cancelledStatus);
        orderRepository.save(order);
        saveHistory(order, cancelledStatus, user, "Customer cancel the order");

        return toResponse(order);
    }

//    HELPER
    private Order getOrderOr404(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Cannot find order"));
    }

    private boolean isPrivileged(User user) {
        return user.getRoles().stream().anyMatch(r -> PRIVILEGED_ROLES.contains(r.getName()));
    }

    private void saveHistory(Order order, Status status, User changedBy, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedBy(changedBy);
        history.setNoted(note);
        historyRepository.save(history);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(oi -> new OrderItemResponse(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity(),
                        oi.getPriceAtPurchase(),
                        oi.getPriceAtPurchase().multiply(BigDecimal.valueOf(oi.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponse(order.getId(), order.getStatus().getName(), order.getTotalAmount(), order.getShippingFee(), order.getShippingMethod(), order.getTrackingNumber(), order.getNote(), items, order.getCreatedAt());
    }
}
