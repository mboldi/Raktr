package hu.bsstudio.raktr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The given amount cannot be rented of the item.")
public class NotAvailableQuantityException extends RuntimeException {
}
