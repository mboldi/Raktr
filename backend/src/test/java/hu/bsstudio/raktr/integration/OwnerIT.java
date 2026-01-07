package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/owner/test-data.sql")
public class OwnerIT extends RaktrIT {

    @Test
    void testListOwners() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/owner")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/owner/list-response.json"));
    }

    @Test
    void testCreateOwner() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/owner/create-request.json"))
                .when()
                .post("/v1/owner")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("id", "createdAt", "updatedAt")
                .equalTo(loadFileContent("/owner/create-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM owners WHERE name = 'test-owner-99'")
                .assertRowAsJson()
                .excluding("id", "created_at", "updated_at")
                .equalTo(loadFileContent("/owner/create-db.json"));
    }

    @Test
    void testCreateOwnerAlreadyExistsError() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/owner/create-already-exists-error-request.json"))
                .when()
                .post("/v1/owner")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/owner/create-already-exists-error-response.json"));
    }

    @Test
    void testUpdateOwner() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/owner/update-request.json"))
                .when()
                .put("/v1/owner/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/owner/update-response.json"));
    }

    @Test
    void testUpdateOwnerAlreadyExistsError() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/owner/update-already-exists-error-request.json"))
                .when()
                .put("/v1/owner/1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/owner/update-already-exists-error-response.json"));
    }

    @Test
    void testUpdateOwnerNotFoundError() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/owner/update-request.json"))
                .when()
                .put("/v1/owner/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/owner/update-not-found-error-response.json"));
    }

    @Test
    void testUpdateOwnerForbiddenError() {
        givenAuthenticatedCandidate()
                .body(loadFileContent("/owner/update-request.json"))
                .when()
                .put("/v1/owner/1")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testDeleteOwner() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/owner/2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM categories WHERE name = 'test-category-2'")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteOwnerNotEmpty() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/owner/1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/owner/delete-not-empty-error-response.json"));

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM owners WHERE id = 1")
                .assertRowCount()
                .isEqualTo(1);
    }

}
