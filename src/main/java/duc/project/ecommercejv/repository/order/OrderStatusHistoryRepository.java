package duc.project.ecommercejv.repository.order;

import duc.project.ecommercejv.entity.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
}
