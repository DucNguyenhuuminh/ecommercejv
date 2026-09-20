package duc.project.ecommercejv.repository.auth;

import duc.project.ecommercejv.entity.auth.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByTokenHashAndTypeAndExpiresAtAfter(
            String tokenHash, AuthToken.Type type, LocalDateTime now);
    void deleteByUserIdAndType(Long userId, AuthToken.Type type);
}
