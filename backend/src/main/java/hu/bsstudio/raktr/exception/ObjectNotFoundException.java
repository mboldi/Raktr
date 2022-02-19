package hu.bsstudio.raktr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No object found with given parameter")
public class ObjectNotFoundException extends RuntimeException {
}
