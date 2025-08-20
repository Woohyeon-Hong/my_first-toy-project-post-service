package hong.postService;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestS3Config {

    @Bean
    public com.amazonaws.services.s3.AmazonS3Client amazonS3Client() {
        return Mockito.mock(com.amazonaws.services.s3.AmazonS3Client.class);
    }
}