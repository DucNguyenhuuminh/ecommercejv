package duc.project.ecommercejv.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private String discountCode;
    private BigDecimal discountAmount;
    private String shippingMethod;
    private String trackingNumber;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
