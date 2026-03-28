package hu.bsstudio.raktr.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class EntityException extends RuntimeException {

    private final String resourceName;

    private final String resourceKey;

    protected EntityException(String message, String resourceName, String resourceKey) {
        super(message);
        this.resourceName = resourceName;
        this.resourceKey = resourceKey;
    }

    public abstract HttpStatus getHttpStatus();

}
