package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    List<ProductDiscount> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
