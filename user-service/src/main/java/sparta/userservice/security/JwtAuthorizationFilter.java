package sparta.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        // 로그인 경로에 대한 예외 처리
        String loginPath = "/api/v1/user/login";
        if (req.getRequestURI().equals(loginPath)) {
            filterChain.doFilter(req, res);
            return;
        }

        // 헤더에서 userId와 role을 추출
        String userIdStr = req.getHeader("x-claim-userid");
        String role = req.getHeader("x-claim-role");

        if (StringUtils.hasText(userIdStr) && StringUtils.hasText(role)) {
            try {
                setAuthentication(userIdStr, role);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        } else {
            log.error("Missing userId or role in headers");
            return;
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username, role);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username, String role) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
