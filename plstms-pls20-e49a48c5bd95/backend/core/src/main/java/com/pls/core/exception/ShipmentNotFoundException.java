package com.pls.core.exception;

/**
 * Signals that requested shipment was not found.
 * 
 * @author Pavani Challa
 */
public class ShipmentNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 8405454642647203345L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ShipmentNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with reference number and customer org id of the shipment that was not found.
     * 
     * @param shipmentNum
     *            reference number of the shipment not found
     * @param orgId
     *            Customer org for which the shipment was not found
     */
    public ShipmentNotFoundException(String shipmentNum, Long orgId) {
        super(String.format("Unable to find Shipment by Shipment Num# '%s' for Customer Org '%s'", shipmentNum, orgId.toString()));
    }

}
