package duc.project.ecommercejv.controller;

import duc.project.ecommercejv.dto.cart.AddCartItemRequest;
import duc.project.ecommercejv.dto.cart.CartResponse;
import duc.project.ecommercejv.dto.cart.UpdateCartItemRequest;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRequest {

    private final CartService cartService;

    @GetMapping
    public CartResponse getMyCart(@AuthenticationPrincipal  User user) {
        return cartService.getMyCart(user);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal User user,@Valid @RequestBody AddCartItemRequest req) {
        return ResponseEntity.ok(cartService.addItem(user,req));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal User user,@PathVariable Long itemId,@Valid @RequestBody UpdateCartItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(user, itemId, req));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeItem(@AuthenticationPrincipal User user,@PathVariable Long itemId) {
        cartService.removeItem(user, itemId);
        return ResponseEntity.ok(Map.of("message", "Deleted product out of the cart successfully"));
    }
}
