package duc.project.ecommercejv.service;

import duc.project.ecommercejv.dto.auth.RegisterRequest;
import duc.project.ecommercejv.entity.auth.Credential;
import duc.project.ecommercejv.entity.auth.Role;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.repository.auth.*;
import duc.project.ecommercejv.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CredentialRepository credentialRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private AuthTokenRepository authTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;

    @InjectMocks private AuthService authService;

    @Mock private HttpServletRequest httpRequest;
    @Mock private HttpServletResponse httpResponse;

    private User user;
    private Credential credential;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("vana@gmail.com");
        user.setRoles(Set.of());

        credential = new Credential();
        credential.setId(1L);
        credential.setUser(user);
        credential.setPassword("hashed_user_password");
        credential.setIsVerified(true);
        credential.setLoginAttempts(0);
    }

//    ========REGISTER=========

    @Test
    void register_emailNotExist_createUserAndSendEmailVerify() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@gmail.com");
        req.setPassword("Password@123");
        req.setFirstName("A");

        Role customerRole = new Role();
        customerRole.setId(1L);
        customerRole.setName("CUSTOMER");

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L);
            return u;
        });
        when(passwordEncoder.encode("Password@123")).thenReturn("hashed");

        authService.register(req);
        verify(credentialRepository).save(argThat(c -> !c.getIsVerified()));
        verify(emailService).sendVerificationEmail(eq("new@gmail.com"),anyString());
    }

    void register_emailAlreadyExists_throwErrors() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@gmail.com");
        req.setPassword("Password@123");

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(IllegalStateException.class)
                .hashMessageContaining("Email is already used");
    }
}
