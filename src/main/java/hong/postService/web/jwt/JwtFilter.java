package hong.postService.web.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hong.postService.domain.UserRole;
import hong.postService.exception.ErrorResponse;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * JWT 검증 요청 필터링 로직에 대한 고민
     *      기본적으로는 모든 요청에 대해 JWT 검증 요구
     *      또한 인가에 대해서는 SecurityFilterChain에서 처리
     *      따라서, jWT가가 유효한지 검사만 하면 됨.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더 또는 쿠키에서 JWT 추출
        String token = extractTokenFromHeaderOrCookie(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // jwt 만료 검증
        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            handleJwtError(response, 401, "JWT_EXPIRED", "토큰이 만료되었습니다.");
            return;
        }

        // JWT 토큰에서 사용자 정보 추출
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String roleStr = jwtUtil.getRole(token);
        UserRole role = roleStr.equals("ROLE_ADMIN") ? UserRole.ADMIN : UserRole.USER;

        // 인증 객체 생성 및 설정
        CustomUserDetails userDetails = new CustomUserDetails(userId, username, null, role);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeaderOrCookie(HttpServletRequest request) {
        // 1. 헤더
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.split(" ")[1];
        }

        // 2. 쿠키
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }


    private void handleJwtError(HttpServletResponse response,
                                int status, String code, String message) throws IOException {

        ErrorResponse errorResponse = new ErrorResponse(status, code, message);

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
