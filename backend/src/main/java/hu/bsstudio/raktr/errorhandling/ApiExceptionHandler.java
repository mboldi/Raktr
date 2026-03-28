package hu.bsstudio.raktr.errorhandling;

import hu.bsstudio.raktr.exception.AccessDeniedException;
import hu.bsstudio.raktr.exception.EntityException;
import hu.bsstudio.raktr.exception.EntityInUseException;
import hu.bsstudio.raktr.exception.PdfGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityException.class)
    public ResponseEntity<EntityErrorResponse> handleEntityException(EntityException ex) {
        log.debug("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        var errorCode = toErrorCode(ex.getClass());
        var response = ex instanceof EntityInUseException inUseEx
                ? new EntityErrorResponse(errorCode, ex.getResourceName(), ex.getResourceKey(), inUseEx.getUsedByType(), ex.getMessage())
                : new EntityErrorResponse(errorCode, ex.getResourceName(), ex.getResourceKey(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.debug("Access denied: {}", ex.getMessage());
        var response = new ErrorResponse("ACCESS_DENIED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.debug("Invalid argument: {}", ex.getMessage());
        var response = new ErrorResponse("INVALID_VALUE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ErrorResponse> handlePdfGeneration(PdfGenerationException ex) {
        log.error("PDF generation failed: {}", ex.getMessage(), ex);
        var response = new ErrorResponse("PDF_GENERATION_FAILED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String toErrorCode(Class<?> exceptionClass) {
        return exceptionClass.getSimpleName()
                .replaceAll("Exception$", "")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }

}
