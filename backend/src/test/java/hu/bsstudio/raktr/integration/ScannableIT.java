package hu.bsstudio.raktr.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;
import static org.hamcrest.Matchers.equalTo;

@Sql("/test-users.sql")
@Sql("/scannable/test-data.sql")
public class ScannableIT extends RaktrIT {

    @Test
    void testGetCount() {
        givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannables/count")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("2"));
    }

    @Test
    void testGetScannableByBarcode() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannables/barcode/BARCODE-001")
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
                .get("/v1/scannables/barcode/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/scannable/get-by-barcode-not-found-response.json"));
    }

    @Test
    void testCheckBarcodeExistsReturnsOk() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannables/barcode/BARCODE-002")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCheckBarcodeExistsReturnsNotFound() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannables/barcode/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetScannableByAssetTag() {
        var response = givenAuthenticatedAdmin()
                .when()
                .get("/v1/scannables/asset-tag/ASSET-002")
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
                .get("/v1/scannables/asset-tag/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/scannable/get-by-asset-tag-not-found-response.json"));
    }

    @Test
    void testCheckAssetTagExistsReturnsOk() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannables/asset-tag/ASSET-001")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void testCheckAssetTagExistsReturnsNotFound() {
        givenAuthenticatedAdmin()
                .when()
                .head("/v1/scannables/asset-tag/NON-EXISTENT")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
