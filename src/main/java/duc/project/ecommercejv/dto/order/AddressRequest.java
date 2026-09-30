package duc.project.ecommercejv.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    @NotBlank(message = "Recipient's name cannot be blank")
    private String recipientName;

    @NotBlank(message = "Phone's number cannot be blank")
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String detail;
    private Boolean isDefault;
}
