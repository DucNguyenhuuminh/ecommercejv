package duc.project.ecommercejv.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductSkuRequest {
    @NotBlank(message = "Sku code cannot be blank")
    private String skuCode;

    @NotNull(message = "Stock cannot be blank")
    @PositiveOrZero(message = "Stock cannot negative")
    private Integer stock;

    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;


    @Valid
    private List<AttributeValuePair> attributes;
}
