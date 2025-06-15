package hong.postService.config;

import hong.postService.service.userDetailsService.CustomOAuth2UserService;
import hong.postService.web.jwt.JwtAuthenticationFilter;
import hong.postService.web.jwt.JwtFilter;
import hong.postService.web.jwt.JwtUtil;
import hong.postService.web.oauth2.CustomAuthenticationEntryPoint;
import hong.postService.web.oauth2.CustomSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf 비활성화
        http.csrf(c -> c.disable());

        //form 로그인 비활성화
        http.formLogin(form -> form.disable());

        //http basic 인증 비활성화
        http.httpBasic(basic -> basic.disable());

        //SecurityFilterChain에 대한 CORS 설정
        http.cors(cors -> cors.configurationSource(
                new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        //port 번호 - 프론 개발 서버는 주로 3000번 사용
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        //허용 메서드
                        configuration.setAllowedMethods(Collections.singletonList("*"));

                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }
        ));


        //경로 별 인가 매칭
        http.authorizeHttpRequests(auth -> auth
                // Swagger 관련 경로는 무조건 허용
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/health"
                ).permitAll()

                // 회원가입, 로그인
                .requestMatchers(HttpMethod.POST, "/v2/users", "/v2/users/admin","/v2/users/login").permitAll()

                // 게시글/댓글 조회는 전체 허용
                .requestMatchers(HttpMethod.GET, "/v2/posts/**", "/v2/comments/**").permitAll()

                // 게시글/댓글 작성, 수정, 삭제는 인증 필요
                .requestMatchers(HttpMethod.POST, "/v2/posts/**", "/v2/comments/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/v2/posts/**", "/v2/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/v2/posts/**", "/v2/comments/**").authenticated()

                // 회원 관련 조회 및 수정, 삭제는 인증 필요
                .requestMatchers("/v2/users/me/**").authenticated()

                // 어드민 전용 URI는 어드민 권한 필요
                .requestMatchers("/v2/admin/**").hasRole("ADMIN")

                // 그 외 모든 요청은 차단
                .anyRequest().denyAll()
        );

        //JwtAuthenticationFilter 등록
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        authenticationFilter.setFilterProcessesUrl("/v2/users/login");
        http.addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //JwtFilter 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), JwtAuthenticationFilter.class);

        //Session stateless로 설정
        http.sessionManagement((sc) -> sc
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //OAuth2.0 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userinfo ->
                        userinfo.userService(customOAuth2UserService))  //CustomOAuth2UserService 등록
                .successHandler(customSuccessHandler)   //CustomSucessHandler 등록 (JWT 발급 담당)
        );

        //JWT 검증 실패 시, OAuth 로그인 redirect 방지
        http.exceptionHandling(e -> e.authenticationEntryPoint(customAuthenticationEntryPoint));


        return http.build();
    }
}
