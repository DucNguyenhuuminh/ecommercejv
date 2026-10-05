package duc.project.ecommercejv.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ProductSkuResponse {
    private Long id;
    private String skuCode;

    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    private BigDecimal currentPrice;
    private Map<String, String> attributes;
}
