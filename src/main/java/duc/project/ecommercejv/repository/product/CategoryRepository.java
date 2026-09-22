package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
