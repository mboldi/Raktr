package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@Sql("/test-users.sql")
@Sql("/rent/test-data.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public class RentIT extends RaktrIT {

    @Test
    void testListRentsDefault() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/rents")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .equalTo(loadFileContent("/rent/list-active-response.json"));
    }

    @Test
    void testListRentsDeletedFalse() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", false)
                .when()
                .get("/v1/rents")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .equalTo(loadFileContent("/rent/list-active-response.json"));
    }

    @Test
    void testListRentsDeletedTrue() {
        var response = givenAuthenticatedAdmin()
                .queryParam("deleted", true)
                .when()
                .get("/v1/rents")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .equalTo(loadFileContent("/rent/list-deleted-response.json"));
    }

    @Test
    void testCreateRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/create-request.json"))
                .when()
                .post("/v1/rents")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/rent/create-response.json"));
    }

    @Test
    void testCreateRentIssuerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/create-issuer-not-found-request.json"))
                .when()
                .post("/v1/rents")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/issuer-not-found-response.json"));
    }

    @Test
    void testCreateRentForbidden() {
        givenAuthenticatedCandidate()
                .body(loadFileContent("/rent/create-request.json"))
                .when()
                .post("/v1/rents")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testGetRentById() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .equalTo(loadFileContent("/rent/get-by-id-response.json"));
    }

    @Test
    void testGetRentByIdNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/rents/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testUpdateRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-request.json"))
                .when()
                .put("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/rent/update-response.json"));
    }

    @Test
    void testUpdateRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-request.json"))
                .when()
                .put("/v1/rents/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testUpdateRentIssuerNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-issuer-not-found-request.json"))
                .when()
                .put("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/issuer-not-found-response.json"));
    }

    @Test
    void testUpdateRentForbidden() {
        givenAuthenticatedCandidate()
                .body(loadFileContent("/rent/update-request.json"))
                .when()
                .put("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testDeleteRent() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM rents WHERE id = 100")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/rent/delete-db.json"));
    }

    @Test
    void testDeleteRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testDeleteRentForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/rents/100")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testRestoreRent() {
        givenAuthenticatedAdmin()
                .when()
                .post("/v1/rents/101/restore")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM rents WHERE id = 101")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/rent/restore-db.json"));
    }

    @Test
    void testRestoreRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .post("/v1/rents/999/restore")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testRestoreRentForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .post("/v1/rents/101/restore")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testAddCommentToRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-comment-request.json"))
                .when()
                .post("/v1/rents/100/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/rent/add-comment-response.json"));
    }

    @Test
    void testAddCommentToRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-comment-request.json"))
                .when()
                .post("/v1/rents/999/comments")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testAddRentItemToRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/rent/add-item-response.json"));
    }

    @Test
    void testAddRentItemToRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-request.json"))
                .when()
                .post("/v1/rents/999/items")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testAddRentItemAlreadyExists() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-already-exists-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/add-item-already-exists-response.json"));
    }

    @Test
    void testAddRentItemScannableNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-scannable-not-found-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/scannable-not-found-response.json"));
    }

    @Test
    void testAddRentItemNotAvailableQuantity() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-not-available-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/add-item-not-available-response.json"));
    }

    @Test
    void testAddContainerItemToRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-container-item-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/rent/add-container-item-response.json"));
    }

    @Test
    void testAddRentItemToComplexRentHasPendingStatus() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-request.json"))
                .when()
                .post("/v1/rents/101/items")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("createdAt", "updatedAt")
                .equalTo(loadFileContent("/rent/add-item-complex-response.json"));
    }

    @Test
    @Sql("/rent/cross-rent-data.sql")
    void testAddRentItemCrossRentNotAvailable() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/add-item-cross-rent-request.json"))
                .when()
                .post("/v1/rents/100/items")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/add-item-cross-rent-not-available-response.json"));
    }

    @Test
    void testUpdateRentItem() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-request.json"))
                .when()
                .put("/v1/rents/100/items/100")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/rent/update-item-response.json"));
    }

    @Test
    void testUpdateRentItemRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-request.json"))
                .when()
                .put("/v1/rents/999/items/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testUpdateRentItemNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-request.json"))
                .when()
                .put("/v1/rents/100/items/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-item-not-found-response.json"));
    }

    @Test
    void testUpdateRentItemWrongRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-request.json"))
                .when()
                .put("/v1/rents/101/items/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-item-wrong-rent-not-found-response.json"));
    }

    @Test
    void testUpdateRentItemNotAvailableQuantity() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-not-available-request.json"))
                .when()
                .put("/v1/rents/100/items/100")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/update-item-not-available-response.json"));
    }

    @Test
    @Sql("/rent/close-rent-setup.sql")
    void testUpdateRentItemClosesRent() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/update-item-close-request.json"))
                .when()
                .put("/v1/rents/100/items/101")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("updatedAt")
                .equalTo(loadFileContent("/rent/update-item-close-response.json"));

        databaseQueryHelper.queryDatabase("SELECT * FROM rents WHERE id = 100")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/rent/close-rent-db.json"));
    }

    @Test
    void testDeleteRentItem() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/100/items/100")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT count(*) FROM rent_items WHERE id = 100")
                .assertRowCount()
                .isEmpty();
    }

    @Test
    void testDeleteRentItemRentNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/999/items/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

    @Test
    void testDeleteRentItemNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/100/items/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-item-not-found-response.json"));
    }

    @Test
    void testDeleteRentItemWrongRent() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/101/items/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-item-wrong-rent-not-found-response.json"));
    }

    @Test
    @Sql("/rent/close-rent-setup.sql")
    void testDeleteRentItemClosesRent() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/rents/100/items/101")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM rents WHERE id = 100")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/rent/close-rent-db.json"));
    }

}
