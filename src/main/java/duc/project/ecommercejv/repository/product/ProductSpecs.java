package duc.project.ecommercejv.repository.product;

import duc.project.ecommercejv.entity.product.Category;
import duc.project.ecommercejv.entity.product.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    private ProductSpecs() {
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return null;
            }
            query.distinct(true);
            Join<Product, Category> join = root.join("categories");
            return criteriaBuilder.equal(join.get("id"), categoryId);
        };
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, criteriaBuilder) -> (keyword == null || keyword.isBlank())
                ? null
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}
