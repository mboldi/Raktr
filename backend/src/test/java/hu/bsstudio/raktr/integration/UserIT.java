package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/user/test-data.sql")
public class UserIT extends RaktrIT {

    @Test
    void testListUsers() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/user")
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
                .get("/v1/user?canIssueRent=true")
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
                .get("/v1/user?canIssueRent=false")
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
                .get("/v1/user/candidate_user")
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
                .get("/v1/user/not_found_user")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/user/get-not-found-response.json"));
    }

}
