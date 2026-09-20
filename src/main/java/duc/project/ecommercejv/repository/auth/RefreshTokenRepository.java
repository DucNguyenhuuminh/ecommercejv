package duc.project.ecommercejv.repository.auth;

import duc.project.ecommercejv.entity.auth.RefreshToken;
import duc.project.ecommercejv.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndExpiresAtAfter(String tokenHash, LocalDateTime now);
    List<RefreshToken> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);

    Long user(User user);
}
