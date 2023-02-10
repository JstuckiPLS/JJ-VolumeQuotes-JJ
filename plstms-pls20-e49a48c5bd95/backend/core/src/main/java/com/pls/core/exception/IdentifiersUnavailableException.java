package com.pls.core.exception;

/**
 * Signals that the load Identifiers such as BOL or Shipment number are missing.
 *
 * @author Yasaman Palumbo
 */
public class IdentifiersUnavailableException extends ApplicationException {

    private static final long serialVersionUID = 3902334718268354487L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public IdentifiersUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with customer org id of the load that needed to be processed.
     *
     * @param orgId Customer org for which the shipment was not found
     * @param field name of the missing field
     */
    public IdentifiersUnavailableException(Long orgId, String field) {
        super(String.format("Unable to process shipment without identifier '%s' for customer org '%s'", field, orgId.toString()));
    }

}
