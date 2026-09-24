package duc.project.ecommercejv.config;

import duc.project.ecommercejv.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final PermissionAuthorizationManager permissionAuthorizationManager;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/**").permitAll()
                                                                                .requestMatchers(
                                                                                        "/api/auth/register",
                                                                                        "/api/auth/login",
                                                                                        "/api/auth/refresh",
                                                                                        "/api/auth/verify-email",
                                                                                        "/api/auth/forgot-password",
                                                                                        "/api/auth/reset-password").permitAll()
                                                                                .requestMatchers(HttpMethod.GET,
                                                                                        "/api/products/**",
                                                                                        "/api/categories/**").permitAll()
                                                                                .anyRequest().access(permissionAuthorizationManager)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req,res,e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"Did not login yet\"}");
                        })
                        .accessDeniedHandler((req,res,e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"Have not permission to access\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
