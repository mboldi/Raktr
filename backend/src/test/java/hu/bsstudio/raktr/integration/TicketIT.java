package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/ticket/test-data.sql")
public class TicketIT extends RaktrIT {

    @Test
    void testListTicketsNoFilter() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/tickets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/list-all-response.json"));
    }

    @Test
    void testListTicketsByStatus() {
        var response = givenAuthenticatedAdmin()
                .queryParam("status", "OPEN")
                .when()
                .get("/v1/tickets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/list-by-status-response.json"));
    }

    @Test
    void testListTicketsBySeverity() {
        var response = givenAuthenticatedAdmin()
                .queryParam("severity", "MINOR")
                .when()
                .get("/v1/tickets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/list-by-severity-response.json"));
    }

    @Test
    void testListTicketsByStatusAndSeverity() {
        var response = givenAuthenticatedAdmin()
                .queryParam("status", "OPEN")
                .queryParam("severity", "MINOR")
                .when()
                .get("/v1/tickets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/list-by-status-and-severity-response.json"));
    }

    @Test
    void testCreateTicket() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/create-request.json"))
                .when()
                .post("/v1/tickets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/ticket/create-response.json"));
    }

    @Test
    void testCreateTicketScannableNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/create-scannable-not-found-request.json"))
                .when()
                .post("/v1/tickets")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/create-scannable-not-found-response.json"));
    }

    @Test
    void testGetTicketById() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/tickets/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/get-by-id-response.json"));
    }

    @Test
    void testGetTicketByIdNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/tickets/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/get-by-id-not-found-response.json"));
    }

    @Test
    void testUpdateTicket() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/update-request.json"))
                .when()
                .put("/v1/tickets/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/ticket/update-response.json"));
    }

    @Test
    void testUpdateTicketNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/update-request.json"))
                .when()
                .put("/v1/tickets/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/update-not-found-response.json"));
    }

    @Test
    void testAddCommentToTicket() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/add-comment-request.json"))
                .when()
                .post("/v1/tickets/1/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/ticket/add-comment-response.json"));
    }

    @Test
    void testAddCommentToTicketNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/ticket/add-comment-request.json"))
                .when()
                .post("/v1/tickets/999/comments")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/ticket/add-comment-not-found-response.json"));
    }

}
