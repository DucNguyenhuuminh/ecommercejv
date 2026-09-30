package duc.project.ecommercejv.controller;

import duc.project.ecommercejv.dto.order.AddressRequest;
import duc.project.ecommercejv.dto.order.AddressResponse;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> myAddresses(@AuthenticationPrincipal User user) {
        return addressService.getMyAddresses(user);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@AuthenticationPrincipal User user,@Valid @RequestBody AddressRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(user,req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@AuthenticationPrincipal User user,@PathVariable Long id,@Valid @RequestBody AddressRequest req) {
        return ResponseEntity.ok(addressService.update(user,id,req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user,@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}
