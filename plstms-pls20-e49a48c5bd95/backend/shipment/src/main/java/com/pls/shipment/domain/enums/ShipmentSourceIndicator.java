package com.pls.shipment.domain.enums;

/**
 * Source indicator for shipments.
 * 
 * @author Yasaman Palumbo
 *
 */
public enum ShipmentSourceIndicator {
    EDI, //Load is created by EDI
    SYS, //Load is created by system
    EDI_M, //Load is created manually but based on data received by EDI
    LBD,
    CAR,
    GS // Load is created from Go Ship website
}
