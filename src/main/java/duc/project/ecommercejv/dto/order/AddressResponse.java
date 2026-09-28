package duc.project.ecommercejv.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String recipientName;
    private String phone;
    private String detail;
    private String ward;
    private String district;
    private String province;
    private Boolean isDefault;
}
