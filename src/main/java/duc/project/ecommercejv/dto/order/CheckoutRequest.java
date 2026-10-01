package duc.project.ecommercejv.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotNull(message = "Address Id cannot be blank")
    private Long addressId;
    private String shippingMethod;
    private String note;
    private String discountCode;
}
