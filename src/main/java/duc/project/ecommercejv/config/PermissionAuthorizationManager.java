package duc.project.ecommercejv.config;

import duc.project.ecommercejv.entity.auth.Permission;
import duc.project.ecommercejv.entity.auth.User;
import duc.project.ecommercejv.repository.auth.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PermissionRepository permissionRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthorizationDecision authorize(Supplier<? extends Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
            return new AuthorizationDecision(false);
        }

        List<Permission> permissions = permissionRepository.findPermissionByUserId(user.getId());
        String method = context.getRequest().getMethod();
        String path = context.getRequest().getRequestURI();

        boolean granted = permissions.stream().anyMatch(p -> p.getMethod().equalsIgnoreCase(method) && pathMatcher.match(p.getUrl(),path));
        return new AuthorizationDecision(granted);
    }
}
