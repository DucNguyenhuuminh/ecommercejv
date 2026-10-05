package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    List<ProductSku> findByProductId(Long productId);
    Optional<ProductSku> findBySkuCode(String skuCode);
}
