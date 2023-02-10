package com.pls.ltlrating.domain.enums;

/**
 * Enum for move types.
 * 
 * @author Aleksandr Leshchenko
 */
public enum MoveType {
    INTRA, // Intra State - means shipment within one state
    INTER, // Inter State - means shipment to another state or even country
    BOTH
}
