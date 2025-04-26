package hong.postService;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;

//서비스 및 레포지토리 테스트에서는 시큐리티 없이 수행하기 위해 SecurityFilterChain Mock 할당
@TestConfiguration
public class TestSecurityConfig {

    @MockBean
    private SecurityFilterChain securityFilterChain;

}
