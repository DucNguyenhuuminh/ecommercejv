package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    Optional<ProductPrice> findTopByProductSkuIdOrderByPrice_CreatedAtDesc(Long skuId);
    List<ProductPrice> findByProductSkuIdOrderByPrice_CreatedAtDesc(Long skuId);
}
