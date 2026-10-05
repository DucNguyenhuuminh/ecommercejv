package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.cart.AddCartItemRequest;
import duc.project.ecommercejv.dto.cart.CartItemResponse;
import duc.project.ecommercejv.dto.cart.CartResponse;
import duc.project.ecommercejv.dto.cart.UpdateCartItemRequest;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.entity.cart.Cart;
import duc.project.ecommercejv.entity.cart.CartItem;
import duc.project.ecommercejv.entity.product.Product;
import duc.project.ecommercejv.entity.product.ProductSku;
import duc.project.ecommercejv.exception.BadRequestException;
import duc.project.ecommercejv.exception.NotFoundException;
import duc.project.ecommercejv.repository.cart.CartItemRepository;
import duc.project.ecommercejv.repository.cart.CartRepository;
import duc.project.ecommercejv.repository.product.ProductPriceRepository;
import duc.project.ecommercejv.repository.product.ProductRepository;
import duc.project.ecommercejv.repository.product.ProductSkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductPriceRepository productPriceRepository;

    @Transactional
    public CartResponse getMyCart(User user) {
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(User user, AddCartItemRequest req) {
        Cart cart = getOrCreateCart(user);

        ProductSku sku = productSkuRepository.findById(req.getSkuId()).orElseThrow(() -> new NotFoundException("Cannot find product's sku"));
        CartItem item = cartItemRepository.findByCartIdAndProductSkuId(cart.getId(), sku.getId()).orElse(null);
        int newQuantity = req.getQuantity() + (item != null ? item.getQuantity() : 0);
        if (newQuantity > sku.getStock()) {
            throw new BadRequestException("Quantity exceeds stock inventory(remaining " + sku.getStock() + ")");
        }

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductSku(sku);
        }
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(User user, Long itemId, UpdateCartItemRequest req) {
        CartItem item = getOwnerItem(user, itemId);
        if (req.getQuantity() > item.getProductSku().getStock()) {
            throw new BadRequestException("Quantity exceeds stock inventory(remaining " + item.getProductSku().getStock() + ")");
        }
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);

        return toResponse(item.getCart());
    }

    @Transactional
    public void removeItem(User user, Long itemId) {
        CartItem item = getOwnerItem(user, itemId);
        cartItemRepository.delete(item);
    }


//    ==========HELPER==========
    private CartItem getOwnerItem(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Cannot find product in the cart"));
        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Cannot find product in the cart");
        }
        return item;
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private BigDecimal currentPriceOf(Long productSkuId) {
        return productPriceRepository.findTopByProductSkuIdOrderByPrice_CreatedAtDesc(productSkuId)
                .map(pp -> pp.getPrice().getPrice())
                .orElse(BigDecimal.ZERO);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            ProductSku sku = item.getProductSku();
            BigDecimal unitPrice = currentPriceOf(sku.getId());
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(item.getId(), sku.getId(), sku.getSkuCode(), sku.getProduct().getName(), item.getQuantity(), unitPrice, subTotal);
        }).collect(Collectors.toList());

        BigDecimal total = itemResponses.stream().map(CartItemResponse::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), itemResponses, total);

    }
}
