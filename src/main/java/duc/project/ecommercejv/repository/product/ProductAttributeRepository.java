package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> findByProductId(Long productId);
    Optional<ProductAttribute> findByProductIdAndName(Long productId, String name);
}
