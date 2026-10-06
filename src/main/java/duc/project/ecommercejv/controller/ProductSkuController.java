package duc.project.ecommercejv.controller;

import duc.project.ecommercejv.dto.product.ProductSkuRequest;
import duc.project.ecommercejv.dto.product.ProductSkuResponse;
import duc.project.ecommercejv.service.ProductSkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductSkuController {
    private final ProductSkuService productSkuService;

    @GetMapping("/{productId}/skus")
    public List<ProductSkuResponse> listByProduct(@PathVariable Long productId) {
        return productSkuService.listByProduct(productId);
    }

    @PostMapping("/{productId}/skus")
    public ResponseEntity<ProductSkuResponse> create(@PathVariable Long productId, @Valid @RequestBody ProductSkuRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productSkuService.create(productId,req));
    }

    @PutMapping("/skus/{skuId}")
    public ResponseEntity<ProductSkuResponse> update(@PathVariable Long skuId, @Valid @RequestBody ProductSkuRequest req) {
        return ResponseEntity.ok(productSkuService.update(skuId,req));
    }

    @DeleteMapping("/skus/{skuId}")
    public ResponseEntity<?> delete(@PathVariable Long skuId) {
        productSkuService.delete(skuId);
        return ResponseEntity.ok(Map.of("message", "Deleted SKU successfully"));
    }
}
