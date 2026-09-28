package duc.project.ecommercejv.repository.order;

import duc.project.ecommercejv.entity.order.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}
