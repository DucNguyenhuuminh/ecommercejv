package duc.project.ecommercejv.repository.order;

import duc.project.ecommercejv.entity.order.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByName(String name);
}
