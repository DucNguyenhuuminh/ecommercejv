package duc.project.ecommercejv.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private Long skuId;
    private String skuCode;
    private String productName;
    private String skuSnapshot;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subTotal;
}
