package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

public class ConfigIT extends RaktrIT {

    @Test
    void testListConfigs() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/configs")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/list-response.json"));
    }

    @Test
    void testUpdateStringEntry() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/config/update-string-request.json"))
                .when()
                .put("/v1/configs/RENT_TEAM_NAME")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/update-string-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM configs WHERE key = 'RENT_TEAM_NAME'")
                .assertRowAsJson()
                .equalTo(loadFileContent("/config/update-string-db.json"));
    }

    @Test
    void testUpdateBooleanEntry() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/config/update-boolean-request.json"))
                .when()
                .put("/v1/configs/FORCE_EAN8")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/update-boolean-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM configs WHERE key = 'FORCE_EAN8'")
                .assertRowAsJson()
                .equalTo(loadFileContent("/config/update-boolean-db.json"));
    }

    @Test
    void testUpdateInvalidValue() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/config/update-invalid-value-request.json"))
                .when()
                .put("/v1/configs/FORCE_EAN8")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/update-invalid-value-response.json"));
    }

    @Test
    void testUpdateNonExistentKey() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/config/update-string-request.json"))
                .when()
                .put("/v1/configs/NON_EXISTENT_KEY")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/update-not-found-response.json"));
    }

    @Test
    @Sql("/config/delete-force-ean8.sql")
    void testUpdateValidKeyNotInDb() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/config/update-boolean-request.json"))
                .when()
                .put("/v1/configs/FORCE_EAN8")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/config/update-valid-key-not-in-db-response.json"));
    }

}
