package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    Optional<ProductAttributeValue> findByProductAttributeIdAndValue(Long attributeId, String value);
}
