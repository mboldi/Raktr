package hu.bsstudio.raktr;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties"
)
class RaktrApplicationTests {
/*
@Test
void contextLoads() {
}*/
}
