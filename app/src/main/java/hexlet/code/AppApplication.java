package hexlet.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;

import net.datafaker.Faker;

@SpringBootApplication
@RestController
@EnableJpaAuditing
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    public Faker getFaker() {
        return new Faker();
    }
}
