package duc.project.ecommercejv.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartItemRequest {

    @NotNull(message = "Product Sku Id cannot be blank")
    private Long skuId;

    @NotNull(message = "Quantity cannot be blank")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
