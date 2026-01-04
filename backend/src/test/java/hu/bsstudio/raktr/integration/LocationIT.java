package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/location/test-data.sql")
public class LocationIT extends RaktrIT {

    @Test
    void testListCategories() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/location")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/location/list-response.json"));
    }

    @Test
    void testCreateLocation() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/location/create-request.json"))
                .when()
                .post("/v1/location")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/location/create-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM locations WHERE name = 'test-location-99'")
                .assertRowAsJson()
                .excluding("created_at", "updated_at")
                .equalTo(loadFileContent("/location/create-db.json"));
    }

    @Test
    void testCreateLocationAlreadyExistsError() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/location/create-already-exists-error-request.json"))
                .when()
                .post("/v1/location")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/location/create-already-exists-error-response.json"));
    }

    @Disabled // TODO: Fix after full refactor
    @Test
    void testDeleteLocation() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/location/test-location-1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract()
                .asString();
    }

    @Disabled // TODO: Fix after full refactor
    @Test
    void testDeleteLocationNotEmptyError() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/location/test-location-1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();
    }

}
