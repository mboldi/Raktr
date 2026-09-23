package hu.bsstudio.raktr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
// Ahead of transaction management, which also defaults to LOWEST_PRECEDENCE, so that a
// result is cached only once its transaction committed.
@EnableCaching(order = Ordered.LOWEST_PRECEDENCE - 1)
public class RaktrApplication {

    static void main(final String[] args) {
        SpringApplication.run(RaktrApplication.class, args);
    }

}
