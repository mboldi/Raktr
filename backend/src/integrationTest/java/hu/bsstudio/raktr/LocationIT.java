package hu.bsstudio.raktr;

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
                .get("/v1/locations")
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
                .post("/v1/locations")
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
    void testCreateLocationAlreadyExists() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/location/create-already-exists-request.json"))
                .when()
                .post("/v1/locations")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/location/create-already-exists-response.json"));
    }

    @Test
    void testDeleteLocation() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/locations/test-location-2")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM locations WHERE name = 'test-location-2'")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteLocationNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/locations/non-existent-location")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/location/delete-not-found-response.json"));
    }

    @Test
    void testDeleteLocationNotEmpty() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/locations/test-location-1")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/location/delete-not-empty-response.json"));

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM locations WHERE name = 'test-location-1'")
                .assertRowCount()
                .isEqualTo(1);
    }

}
