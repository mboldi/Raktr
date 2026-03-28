package hu.bsstudio.raktr.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends EntityException {

    public EntityAlreadyExistsException(Class<?> resource, Object key) {
        super(
                String.format("Resource [%s] already exists with key [%s]", resource.getSimpleName(), key),
                resource.getSimpleName(),
                key.toString()
        );
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
