package hu.bsstudio.raktr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Object in database with given ID")
public class ObjectConflictException extends RuntimeException {
}
