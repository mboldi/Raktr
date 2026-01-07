package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;
import static org.hamcrest.Matchers.equalTo;

@Sql("/test-users.sql")
@Sql("/scannable/test-data.sql")
public class ScannableIT extends RaktrIT {

    @Test
    void testDeleteScannable() {
        givenAuthenticatedAdmin()
                .when()
                .delete("/v1/scannable/101")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 101")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/scannable/delete-db.json"));
    }

    @Test
    void testDeleteScannableNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .delete("/v1/scannable/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/scannable/delete-not-found-response.json"));
    }

    @Test
    void testDeleteScannableForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .delete("/v1/scannable/101")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testRestoreScannable() {
        givenAuthenticatedAdmin()
                .when()
                .post("/v1/scannable/102/restore")
                .then()
                .statusCode(HttpStatus.OK.value());

        databaseQueryHelper.queryDatabase("SELECT * FROM scannables WHERE id = 102")
                .assertRowAsJson()
                .excluding("updated_at")
                .equalTo(loadFileContent("/scannable/restore-db.json"));
    }

    @Test
    void testRestoreScannableNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .post("/v1/scannable/999/restore")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/scannable/restore-not-found-response.json"));
    }

    @Test
    void testRestoreScannableForbidden() {
        givenAuthenticatedCandidate()
                .when()
                .post("/v1/scannable/102/restore")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testGetCount() {
        givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannable/count")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("3"));
    }

    @Test
    void testGetScannableByBarcode() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannable/barcode/BARCODE-001")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/scannable/get-by-barcode-response.json"));
    }

    @Test
    void testGetScannableByBarcodeNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannable/barcode/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/scannable/get-by-barcode-not-found-response.json"));
    }

    @Test
    void testCheckBarcodeExistsReturnsOk() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannable/barcode/BARCODE-002")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCheckBarcodeExistsReturnsNotFound() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannable/barcode/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetScannableByAssetTag() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannable/asset-tag/ASSET-002")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/scannable/get-by-asset-tag-response.json"));
    }

    @Test
    void testGetScannableByAssetTagNotFound() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannable/asset-tag/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response)
                .excluding("timestamp")
                .equalTo(loadFileContent("/scannable/get-by-asset-tag-not-found-response.json"));
    }

    @Test
    void testCheckAssetTagExistsReturnsOk() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannable/asset-tag/ASSET-001")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCheckAssetTagExistsReturnsNotFound() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannable/asset-tag/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}