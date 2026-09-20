package duc.project.ecommercejv.repository.auth;

import duc.project.ecommercejv.entity.auth.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByUserId(Long userId);
}
