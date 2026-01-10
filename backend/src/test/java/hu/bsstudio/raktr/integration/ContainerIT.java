package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/container/test-data.sql")
public class ContainerIT extends RaktrIT {

    @Test
    void testListContainersDefaultOnlyActive() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/container")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/list-active-response.json"));
    }

    @Test
    void testListContainersDeletedFalse() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", false)
                .when()
                .get("/v1/container")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/list-active-response.json"));
    }

    @Test
    void testListContainersDeletedTrue() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", true)
                .when()
                .get("/v1/container")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/list-deleted-response.json"));
    }

    @Test
    void testCreateContainer() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/create-request.json"))
                .when()
                .post("/v1/container")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/container/create-response.json"));
    }

    @Test
    void testCreateContainerCategoryNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/create-category-not-found-request.json"))
                .when()
                .post("/v1/container")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/create-category-not-found-response.json"));
    }

    @Test
    void testCreateContainerLocationNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/create-location-not-found-request.json"))
                .when()
                .post("/v1/container")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/create-location-not-found-response.json"));
    }

    @Test
    void testCreateContainerOwnerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/create-owner-not-found-request.json"))
                .when()
                .post("/v1/container")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/create-owner-not-found-response.json"));
    }

    @Test
    void testGetContainerById() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/container/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/get-by-id-response.json"));
    }

    @Test
    void testGetContainerByIdNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/container/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/get-by-id-not-found-response.json"));
    }

    @Test
    void testUpdateContainer() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/update-request.json"))
                .when()
                .put("/v1/container/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/container/update-response.json"));
    }

    @Test
    void testUpdateContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/update-request.json"))
                .when()
                .put("/v1/container/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/update-not-found-response.json"));
    }

    @Test
    void testUpdateContainerCategoryNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/update-category-not-found-request.json"))
                .when()
                .put("/v1/container/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/update-category-not-found-response.json"));
    }

    @Test
    void testUpdateContainerLocationNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/update-location-not-found-request.json"))
                .when()
                .put("/v1/container/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/update-location-not-found-response.json"));
    }

    @Test
    void testUpdateContainerOwnerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/update-owner-not-found-request.json"))
                .when()
                .put("/v1/container/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/update-owner-not-found-response.json"));
    }

    @Test
    void testAddDevicesToContainer() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/add-devices-request.json"))
                .when()
                .post("/v1/container/100/devices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/add-devices-response.json"));
    }

    @Test
    void testAddDevicesToContainerAppend() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/add-devices-append-request.json"))
                .when()
                .post("/v1/container/101/devices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/add-devices-append-response.json"));
    }

    @Test
    void testAddDevicesToContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/add-devices-request.json"))
                .when()
                .post("/v1/container/999/devices")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/add-devices-not-found-response.json"));
    }

    @Test
    void testAddDevicesToContainerDeviceNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/container/add-devices-device-not-found-request.json"))
                .when()
                .post("/v1/container/100/devices")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/add-devices-device-not-found-response.json"));
    }

    @Test
    void testRemoveDeviceFromContainer() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/container/101/devices/202")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/remove-device-response.json"));
    }

    @Test
    void testRemoveDeviceFromContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/container/999/devices/202")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/remove-device-not-found-response.json"));
    }

    @Test
    void testRemoveDeviceFromContainerDeviceNotInContainer() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/container/100/devices/200")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/remove-device-device-not-found-response.json"));
    }

    @Test
    void testDeleteContainer() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/container/100")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 100")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/container/delete-db.json"));
    }

    @Test
    void testDeleteContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/container/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/delete-not-found-response.json"));
    }

    @Test
    void testDeleteContainerForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/container/100")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testRestoreContainer() {
        givenAuthenticatedAdmin()
                .when()
                .post("/v1/container/102/restore")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 102")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/container/restore-db.json"));
    }

    @Test
    void testRestoreContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .post("/v1/container/999/restore")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/restore-not-found-response.json"));
    }

    @Test
    void testRestoreContainerForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .post("/v1/container/102/restore")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testGetTicketsForContainer() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/container/100/tickets")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/get-tickets-response.json"));
    }

    @Test
    void testGetTicketsForContainerNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/container/999/tickets")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/container/get-tickets-not-found-response.json"));
    }

}