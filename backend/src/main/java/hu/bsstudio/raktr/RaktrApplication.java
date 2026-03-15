package hu.bsstudio.raktr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class RaktrApplication {

    static void main(final String[] args) {
        SpringApplication.run(RaktrApplication.class, args);
    }

}
