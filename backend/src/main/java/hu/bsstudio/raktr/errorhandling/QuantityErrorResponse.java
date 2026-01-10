package hu.bsstudio.raktr.errorhandling;

public record QuantityErrorResponse(
        String error,
        String itemIdentifier,
        int requestedQuantity,
        int availableQuantity,
        String message
) {

}
