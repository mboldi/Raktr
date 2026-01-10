package hu.bsstudio.raktr.errorhandling;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EntityErrorResponse(
        String error,
        String resourceName,
        String resourceKey,
        String usedByType,
        String message
) {

    public EntityErrorResponse(String error, String resourceName, String resourceKey, String message) {
        this(error, resourceName, resourceKey, null, message);
    }

}
