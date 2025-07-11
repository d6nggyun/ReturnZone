package _ITHON.ReturnZone.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI();

        // 토큰 재발급 API는 건너뜀
        if ("/api/auth/refresh".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt, "access")) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Security Context : " + authentication.getName() + "\nURI : " + requestURI);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            setErrorResponse(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", response.encodeRedirectURL("/"));

        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}
