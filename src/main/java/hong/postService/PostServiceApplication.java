package hong.postService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class PostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostServiceApplication.class, args);
	}

	}
