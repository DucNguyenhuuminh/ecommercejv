package duc.project.ecommercejv.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {

    private String seri;

    @NotBlank(message = "Product's name cannot be blank")
    private String name;
    private String description;

    @PositiveOrZero(message = "Stock cannot negative")
    private Integer stock;

    @Positive(message = "Price must greater than 0")
    private BigDecimal price;

    private List<Long> categoryIds;
    private List<String> imageUrls;
    private List<Long> discountIds;
}
