package hu.bsstudio.raktr.support;

import hu.bsstudio.raktr.dependency.SsoProviderMock;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static io.restassured.RestAssured.given;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationHelper {

    private static final String ADMIN_TOKEN = SsoProviderMock.generateJwt(
            "00000000-0000-0000-0000-000000000001",
            "admin_user",
            List.of("Admin")
    );

    public static RequestSpecification givenAuthenticatedAdmin() {
        return given().header("Authorization", "Bearer " + ADMIN_TOKEN);
    }

}
