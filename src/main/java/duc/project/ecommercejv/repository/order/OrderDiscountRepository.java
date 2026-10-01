package duc.project.ecommercejv.repository.order;

import duc.project.ecommercejv.entity.order.OrderDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Long> {
    List<OrderDiscount> findByOrderId(Long orderId);
}
