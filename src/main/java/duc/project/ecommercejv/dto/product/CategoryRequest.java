package duc.project.ecommercejv.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category's name cannot be blank")
    private String name;
    private String description;
    private Long parentId;
}
