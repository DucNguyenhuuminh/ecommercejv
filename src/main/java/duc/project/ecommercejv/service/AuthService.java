package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.auth.AuthResponse;
import duc.project.ecommercejv.dto.auth.LoginRequest;
import duc.project.ecommercejv.dto.auth.RegisterRequest;
import duc.project.ecommercejv.entity.auth.*;
import duc.project.ecommercejv.exception.auth.EmailNotVerifiedException;
import duc.project.ecommercejv.repository.auth.*;
import duc.project.ecommercejv.security.JwtService;
import duc.project.ecommercejv.security.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS=5;
    private static final long LOCKED_MINUTES=15;
    private static final int MAX_SESSIONS_PER_USER=5;
    private static final String REFRESH_COOKIE_NAME="refreshToken";

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalStateException("Email is already used");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow(() -> new IllegalStateException("Role CUSTOMER has not seeded in DB"));

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setRoles(Set.of(customerRole));
        user = userRepository.save(user);

        Credential credential = new Credential();
        credential.setUser(user);
        credential.setPassword(passwordEncoder.encode(req.getPassword()));
        credential.setIsVerified(false);
        credentialRepository.save(credential);

        createAndSendAuthToken(user, AuthToken.Type.VERIFY_EMAIL, 24*60);
    }

    @Transactional
    public void verifyEmail(String plainToken) {
        AuthToken token = authTokenRepository.findByTokenHashAndTypeAndExpiresAtAfter(TokenUtils.sha256(plainToken),
                AuthToken.Type.VERIFY_EMAIL, LocalDateTime.now()).orElseThrow(() -> new BadCredentialsException("Token is invalid or expired"));
        Credential credential = credentialRepository.findByUserId(token.getUser().getId()).orElseThrow(() -> new IllegalStateException("Cannot find account"));

        credential.setIsVerified(true);
        credentialRepository.save(credential);
        authTokenRepository.delete(token);
    }

    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new BadCredentialsException("Email or password is not correct"));
        Credential credential = credentialRepository.findByUserId(user.getId()).orElseThrow(() -> new BadCredentialsException("Email or password is not correct"));

        if (credential.getLockedUntil() != null && credential.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("Account is temporary locked for incorrect logging too many times, retry later");
        }

        if (!passwordEncoder.matches(req.getPassword(), credential.getPassword())) {
            registerFailedLogin(credential);
            throw new BadCredentialsException("Email or password is not correct");
        }

        if (!Boolean.TRUE.equals(credential.getIsVerified())) {
            throw new EmailNotVerifiedException("Please verify email before login");
        }

        credential.setLoginAttempts(0);
        credential.setLockedUntil(null);
        credentialRepository.save(credential);

        String accessToken = jwtService.generateAccessToken(user.getId());
        issueRefreshToken(user, request, response);

        return new AuthResponse(accessToken, user.getId(), user.getEmail());
    }

    @Transactional
    public AuthResponse refresh(String plainToken, HttpServletRequest request, HttpServletResponse response) {
        if (plainToken == null) {
            throw new BadCredentialsException("Cannot find refresh token");
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHashAndExpiresAtAfter(TokenUtils.sha256(plainToken),
                LocalDateTime.now()).orElseThrow(() -> new BadCredentialsException("Refresh token is invalid or out of ordered"));

        User user = stored.getUser();
        refreshTokenRepository.delete(stored);
        String accessToken = jwtService.generateAccessToken(user.getId());
        issueRefreshToken(user, request, response);

        return new AuthResponse(accessToken, user.getId(), user.getEmail());
    }

    public void logout(String plainToken, HttpServletResponse response) {
        if (plainToken != null) {
            refreshTokenRepository.deleteByTokenHash(TokenUtils.sha256(plainToken));
        }
        clearRefreshCookie(response);
    }

    @Transactional
    public void logoutAllDevices(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> createAndSendAuthToken(user, AuthToken.Type.RESET_PASSWORD, 15));
    }

    @Transactional
    public void resetPassword(String plainToken, String newPassword) {
        AuthToken token = authTokenRepository.findByTokenHashAndTypeAndExpiresAtAfter(TokenUtils.sha256(plainToken),
                AuthToken.Type.RESET_PASSWORD, LocalDateTime.now()).orElseThrow(() -> new BadCredentialsException("Token is invalid or expired"));

        User user = token.getUser();
        Credential credential = credentialRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalStateException("Cannot find account"));
        credential.setPassword(passwordEncoder.encode(newPassword));
        credential.setLoginAttempts(0);
        credential.setLockedUntil(null);
        credentialRepository.save(credential);

        authTokenRepository.delete(token);
        refreshTokenRepository.deleteByUserId(user.getId());
    }

//    ==========HELPER==========
    private void createAndSendAuthToken(User user, AuthToken.Type type, long expiresInMinutes) {
        authTokenRepository.deleteByUserIdAndType(user.getId(), type);
        String plainToken = TokenUtils.generateToken();
        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setType(type);
        token.setTokenHash(TokenUtils.sha256(plainToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expiresInMinutes));
        authTokenRepository.save(token);

        if (type == AuthToken.Type.VERIFY_EMAIL) {
            emailService.sendVerificationEmail(user.getEmail(), plainToken);
        } else {
            emailService.sendResetPasswordEmail(user.getEmail(), plainToken);
        }
    }

    private void registerFailedLogin(Credential credential) {
        int attempts = credential.getLoginAttempts() + 1;
        credential.setLoginAttempts(attempts);
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            credential.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKED_MINUTES));
        }
        credentialRepository.save(credential);
    }

    private void issueRefreshToken(User user, HttpServletRequest request, HttpServletResponse response) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String plainToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenUtils.sha256(plainToken));
        refreshToken.setUserAgent(request.getHeader("User-Agent"));
        refreshToken.setIp(request.getRemoteAddr());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        pruneOldSessions(user.getId());
        setRefreshCookie(response, plainToken);
    }

    private void pruneOldSessions(Long userId) {
        List<RefreshToken> sessions = refreshTokenRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (sessions.size() > MAX_SESSIONS_PER_USER) {
            sessions.subList(MAX_SESSIONS_PER_USER, sessions.size()).forEach(refreshTokenRepository::delete);
        }
    }

    private void setRefreshCookie(HttpServletResponse response, String plainToken) {
        String cookie = String.format("%s=%s; HttpOnly; Path=/api/auth; Max-Age=%d; SameSite=Strict", REFRESH_COOKIE_NAME, plainToken, 7*24*60*60);
        response.addHeader("Set-Cookie", cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        String cookie = String.format("%s=; HttpOnly; Path=/api/auth; Max-Age=0; SameSite=Strict", REFRESH_COOKIE_NAME);
        response.addHeader("Set-Cookie", cookie);
    }


}
