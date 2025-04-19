package hong.postService.web.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hong.postService.domain.UserRole;
import hong.postService.exception.ErrorResponse;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    //화이트 리스트 경로 (jwt 검사 제외)
    private final List<String> WHITE_LIST = List.of(
            "/v2/users",                   // 회원가입
            "/v2/users/login",            // 로그인
            "/swagger-ui/**",             // Swagger UI
            "/v3/api-docs/**",            // Swagger API docs
            "/swagger-ui.html"            // Swagger entry
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 1. 화이트리스트 경로는 필터 통과 - 모든 요청에 대해 jwtFilter가 적용되기 때문
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // 2. Authorization 헤더가 없거나 잘못된 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            handleJwtError(response, 401, "JWT_MISSING", "Authorization 헤더가 없거나 올바르지 않습니다.");
            return;
        }

        String token = authorization.split(" ")[1];

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            handleJwtError(response, 401, "JWT_EXPIRED", "토큰이 만료되었습니다.");
            return;
        }

        // 4. JWT 토큰에서 사용자 정보 추출
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String roleStr = jwtUtil.getRole(token);
        UserRole role = roleStr.equals("ROLE_ADMIN") ? UserRole.ADMIN : UserRole.USER;

        // 5. 인증 객체 생성 및 설정
        CustomUserDetails userDetails = new CustomUserDetails(userId, username, null, role);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path) {
        return WHITE_LIST.stream().anyMatch(whitelistPattern -> pathMatcher.match(whitelistPattern, path));
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
