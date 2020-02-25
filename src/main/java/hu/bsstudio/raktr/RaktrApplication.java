package hu.bsstudio.raktr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
public class RaktrApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaktrApplication.class, args);
	}

}
