package hu.bsstudio.raktr.errorhandling;

import hu.bsstudio.raktr.exception.PdfGenerationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsDataIntegrityViolationToConflict() {
        var response = handler.handleDataIntegrityViolation(new DataIntegrityViolationException("duplicate key"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .isEqualTo(new ErrorResponse("CONSTRAINT_VIOLATION", "The request conflicts with existing data."));
    }

    @Test
    void mapsPdfGenerationFailureToInternalServerError() {
        var ex = new PdfGenerationException("Failed to generate rent PDF", new IllegalStateException("font missing"));

        var response = handler.handlePdfGeneration(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .isEqualTo(new ErrorResponse("PDF_GENERATION_FAILED", "Failed to generate rent PDF"));
    }

}
