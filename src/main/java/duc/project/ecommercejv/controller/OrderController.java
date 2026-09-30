package duc.project.ecommercejv.controller;

import duc.project.ecommercejv.dto.order.CheckoutRequest;
import duc.project.ecommercejv.dto.order.OrderResponse;
import duc.project.ecommercejv.dto.order.UpdateOrderStatusRequest;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal User user,@Valid @RequestBody CheckoutRequest req) {
        return ResponseEntity.ok(orderService.checkout(user, req));
    }

    @GetMapping
    public Page<OrderResponse> listOrders(@AuthenticationPrincipal User user,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size) {
        return orderService.listOrders(user, PageRequest.of(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@AuthenticationPrincipal User user,@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(user,id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(User user, Long id, UpdateOrderStatusRequest req) {
        return ResponseEntity.ok(orderService.updateStatus(user, id, req));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@AuthenticationPrincipal User user,@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(user,id));
    }
}
