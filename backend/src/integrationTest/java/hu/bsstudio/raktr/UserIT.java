package hu.bsstudio.raktr;

import hu.bsstudio.raktr.dependency.SsoProviderMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Sql("/user/test-data.sql")
public class UserIT extends RaktrIT {

    @Test
    void testListUsers() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/list-response.json"));
    }

    @Test
    void testListUsersWhoCanIssueRent() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/users?canIssueRent=true")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/list-can-issue-rent-response.json"));
    }

    @Test
    void testListUsersWhoCannotIssueRent() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/users?canIssueRent=false")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/list-cannot-issue-rent-response.json"));
    }

    @Test
    void testGetUser() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/users/candidate_user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/get-response.json"));
    }

    @Test
    void testGetUserNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/users/not_found_user")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/get-not-found-response.json"));
    }

    @Test
    void testGetCurrentUser() {
        var response = givenAuthenticatedCandidate()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/get-me-response.json"));
    }

    @Test
    void testGetCurrentUserOnFirstLogin() {
        var token = SsoProviderMock.generateJwt(
                "00000000-0000-0000-0000-000000000005",
                "new_user",
                "New",
                "User",
                List.of("Stúdiós")
        );

        var response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/get-me-first-login-response.json"));
    }

    @Test
    void testGetCurrentUserOnFirstLoginWithConcurrentRequests() throws Exception {
        var token = SsoProviderMock.generateJwt(
                "00000000-0000-0000-0000-000000000005",
                "new_user",
                "New",
                "User",
                List.of("Stúdiós")
        );
        var requestCount = 10;
        var start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(requestCount)) {
            List<Future<Integer>> statusCodes = IntStream.range(0, requestCount)
                    .mapToObj(i -> executor.submit(() -> {
                        start.await();
                        return given()
                                .header("Authorization", "Bearer " + token)
                                .when()
                                .get("/v1/users/me")
                                .statusCode();
                    }))
                    .toList();

            start.countDown();

            for (var statusCode : statusCodes) {
                assertThat(statusCode.get()).isEqualTo(HttpStatus.OK.value());
            }
        }

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM users WHERE uuid = '00000000-0000-0000-0000-000000000005'")
                .assertRowCount()
                .isEqualTo(1);
    }

    @Test
    void testGetCurrentUserUnauthenticated() {
        given()
                .when()
                .get("/v1/users/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void testUpdateOwnUser() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/user/update-request.json"))
                .when()
                .put("/v1/users/admin_user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/update-response.json"));
    }

    @Test
    void testUpdateOwnUserClearFields() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/user/update-clear-request.json"))
                .when()
                .put("/v1/users/admin_user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/update-clear-response.json"));
    }

    @Test
    void testUpdateOwnUserEmptyFieldsRejected() {
        givenAuthenticatedAdmin()
                .body(loadFileContent("/user/update-empty-request.json"))
                .when()
                .put("/v1/users/admin_user")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testUpdateOtherUserAsAdmin() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/user/update-request.json"))
                .when()
                .put("/v1/users/member_user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/update-other-user-as-admin-response.json"));
    }

    @Test
    void testUpdateOtherUserForbidden() {
        var response = givenAuthenticatedCandidate()
                .body(loadFileContent("/user/update-request.json"))
                .when()
                .put("/v1/users/member_user")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/update-forbidden-response.json"));
    }

    @Test
    void testUpdaterUserNo() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/user/update-request.json"))
                .when()
                .put("/v1/users/not_found_user")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/update-not-found-response.json"));
    }

}
