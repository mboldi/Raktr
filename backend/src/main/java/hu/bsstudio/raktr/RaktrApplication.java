package hu.bsstudio.raktr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableJpaAuditing
public class RaktrApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RaktrApplication.class, args);
    }

}
