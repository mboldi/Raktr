package hu.bsstudio.raktr.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Files;
import java.nio.file.Path;

import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedAdmin;
import static hu.bsstudio.raktr.support.AuthenticationHelper.givenAuthenticatedCandidate;
import static hu.bsstudio.raktr.support.JsonAssert.assertJson;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("/test-users.sql")
@Sql("/rent/test-data.sql")
public class RentPdfIT extends RaktrIT {

    @Test
    @SneakyThrows
    void testGenerateRentPdf() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/pdf-request.json"))
                .when()
                .post("/v1/rents/100/pdf")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header("Content-Type", equalTo("application/pdf"))
                .header("Content-Disposition", equalTo("attachment; filename=\"rent-100.pdf\""))
                .header("Cache-Control", equalTo("no-cache, no-store, must-revalidate"))
                .extract()
                .asByteArray();

        assertTrue(response.length > 0, "PDF should not be empty");
        assertTrue(response[0] == '%' && response[1] == 'P' && response[2] == 'D' && response[3] == 'F',
                "Output should be a valid PDF (starts with %PDF)");

        var outputPath = Path.of("build", "test-output", "rent-100.pdf");
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, response);
    }

    @Test
    void testGenerateRentPdfAsCandidate() {
        givenAuthenticatedCandidate()
                .body(loadFileContent("/rent/pdf-request.json"))
                .when()
                .post("/v1/rents/100/pdf")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header("Content-Type", equalTo("application/pdf"));
    }

    @Test
    void testGenerateRentPdfNotFound() {
        var response = givenAuthenticatedAdmin()
                .body(loadFileContent("/rent/pdf-request.json"))
                .when()
                .post("/v1/rents/999/pdf")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .asString();

        assertJson(response).equalTo(loadFileContent("/rent/rent-not-found-response.json"));
    }

}
