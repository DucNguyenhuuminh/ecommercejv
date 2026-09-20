package duc.project.ecommercejv.repository.auth;

import duc.project.ecommercejv.entity.auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT DISTINCT p FROM User u JOIN u.roles r JOIN r.permissions p WHERE u.id = :userId")
    List<Permission> findPermissionByUserId(@Param("userId") Long userId);
}
