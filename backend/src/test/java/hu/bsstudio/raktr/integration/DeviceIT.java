package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/device/test-data.sql")
public class DeviceIT extends RaktrIT {

    @Test
    void testListDevicesDefaultOnlyActive() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/device")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/device/list-active-response.json"));
    }

    @Test
    void testListDevicesDeletedFalse() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", false)
                .when()
                .get("/v1/device")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/device/list-active-response.json"));
    }

    @Test
    void testListDevicesDeletedTrue() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", true)
                .when()
                .get("/v1/device")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/device/list-deleted-response.json"));
    }

    @Test
    void testCreateDevice() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/create-request.json"))
                .when()
                .post("/v1/device")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/device/create-response.json"));
    }

    @Test
    void testCreateDeviceCategoryNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/create-category-not-found-request.json"))
                .when()
                .post("/v1/device")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/create-category-not-found-response.json"));
    }

    @Test
    void testCreateDeviceLocationNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/create-location-not-found-request.json"))
                .when()
                .post("/v1/device")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/create-location-not-found-response.json"));
    }

    @Test
    void testCreateDeviceOwnerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/create-owner-not-found-request.json"))
                .when()
                .post("/v1/device")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/create-owner-not-found-response.json"));
    }

    @Test
    void testGetDeviceById() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/device/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/device/get-by-id-response.json"));
    }

    @Test
    void testGetDeviceByIdNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/device/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/get-by-id-not-found-response.json"));
    }

    @Test
    void testUpdateDevice() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/update-request.json"))
                .when()
                .put("/v1/device/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/device/update-response.json"));
    }

    @Test
    void testUpdateDeviceNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/update-request.json"))
                .when()
                .put("/v1/device/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/update-not-found-response.json"));
    }

    @Test
    void testUpdateDeviceCategoryNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/update-category-not-found-request.json"))
                .when()
                .put("/v1/device/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/update-category-not-found-response.json"));
    }

    @Test
    void testUpdateDeviceLocationNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/update-location-not-found-request.json"))
                .when()
                .put("/v1/device/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/update-location-not-found-response.json"));
    }

    @Test
    void testUpdateDeviceOwnerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/device/update-owner-not-found-request.json"))
                .when()
                .put("/v1/device/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/update-owner-not-found-response.json"));
    }

    @Test
    void testDeleteDevice() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/device/100")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 100")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/device/delete-db.json"));
    }

    @Test
    void testDeleteDeviceNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/device/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/delete-not-found-response.json"));
    }

    @Test
    void testDeleteDeviceForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/device/100")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testRestoreDevice() {
        givenAuthenticatedAdmin()
                .when()
                .post("/v1/device/103/restore")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 103")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/device/restore-db.json"));
    }

    @Test
    void testRestoreDeviceNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .post("/v1/device/999/restore")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/device/restore-not-found-response.json"));
    }

    @Test
    void testRestoreDeviceForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .post("/v1/device/103/restore")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testListManufacturers() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/device/manufacturers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/device/manufacturers-response.json"));
    }

}