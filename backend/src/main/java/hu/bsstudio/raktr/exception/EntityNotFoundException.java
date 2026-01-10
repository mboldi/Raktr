package hu.bsstudio.raktr.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends EntityException {

    public EntityNotFoundException(Class<?> resource, Object key) {
        super(
                String.format("Resource [%s] not found with key [%s]", resource.getSimpleName(), key),
                resource.getSimpleName(),
                key.toString()
        );
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
