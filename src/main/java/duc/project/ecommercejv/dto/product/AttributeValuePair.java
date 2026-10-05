package duc.project.ecommercejv.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValuePair {

    @NotBlank(message = "Attribute's name cannot be blank")
    private String name;

    @NotBlank(message = "Attribute's value cannot be blank")
    private String value;
}
