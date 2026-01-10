package hu.bsstudio.raktr.exception;

import lombok.Getter;

@Getter
public class NotAvailableQuantityException extends RuntimeException {

    private final String itemIdentifier;
    private final int requestedQuantity;
    private final int availableQuantity;

    public NotAvailableQuantityException(String itemIdentifier, int requestedQuantity, int availableQuantity) {
        super("Cannot rent [%d] of [%s], only [%d] available".formatted(requestedQuantity, itemIdentifier, availableQuantity));
        this.itemIdentifier = itemIdentifier;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

}
