package hu.bsstudio.raktr.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityInUseException extends EntityException {

    private final String usedByType;

    public EntityInUseException(Class<?> resource, Object key, Class<?> usedBy) {
        super(
                String.format("Resource [%s] with key [%s] is in use by [%s]", resource.getSimpleName(), key, usedBy.getSimpleName()),
                resource.getSimpleName(),
                key.toString()
        );
        this.usedByType = usedBy.getSimpleName();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
