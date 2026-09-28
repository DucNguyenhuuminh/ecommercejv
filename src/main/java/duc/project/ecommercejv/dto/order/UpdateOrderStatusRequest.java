package duc.project.ecommercejv.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Status name cannot be blank")
    private String statusName;  // PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private String note;
}
