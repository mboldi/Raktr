package hu.bsstudio.raktr;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;

@Slf4j
public class ApplicationHealthIT extends RaktrIT {

    @Test
    void contextLoads() {
        log.info("Application context loaded correctly");
    }

    @Test
    void testHealth() {
        when()
                .get("/actuator/health")
                .then()
                .statusCode(200);
    }

}
