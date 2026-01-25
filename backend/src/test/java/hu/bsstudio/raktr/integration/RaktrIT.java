package hu.bsstudio.raktr.integration;

import hu.bsstudio.raktr.RaktrApplication;
import hu.bsstudio.raktr.dependency.SsoProviderMock;
import hu.bsstudio.raktr.support.DatabaseQueryHelper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@ActiveProfiles("it")
@SpringBootTest(classes = RaktrApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class RaktrIT {

    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    static {
        postgres.start();
    }

    @LocalServerPort
    protected int port;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    protected DatabaseQueryHelper databaseQueryHelper;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", SsoProviderMock::getBaseUrl);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", SsoProviderMock::getJwksUrl);
    }

    @BeforeEach
    void initialize() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        RestAssured.requestSpecification.port(port);
    }

    @AfterEach
    void teardown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/db-cleanup.sql"));
        }
        cacheManager.getCacheNames().forEach(
                cacheName -> Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(Cache::clear)
        );
    }

}
