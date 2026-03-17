package hu.bsstudio.raktr;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

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
