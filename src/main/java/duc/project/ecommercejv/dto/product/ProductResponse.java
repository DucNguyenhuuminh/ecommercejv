package duc.project.ecommercejv.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String seri;
    private String name;
    private String description;
    private List<String> categoryNames;
    private List<String> imageUrl;
    private List<String> discountCodes;
    private List<ProductSkuResponse> skus;
}
