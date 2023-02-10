package com.pls.shipment.domain.enums;

/**
 * List of adjustment reasons.
 * 
 * @author Dmitriy Nefedchenko
 *
 */
public enum AdjustmentReason {
    INCORRECT_RATE(1),
    CO_LOAD(2),
    WRONG_ORIGIN(3),
    WRONG_DESTINATION(4),
    INCORRECT_WEIGHT(7),
    WRONG_CARRIER(8),
    DEDICATED(9),
    FUEL_SURCHARGE(10),
    INCORRECT_MILES(11),
    WRONG_SHIPPER(13),
    S_B_PREPAID(25),
    S_B_COLLECT(26),
    OVERPAID(30),
    DUPLICATE_BILLING(31),
    MINIMUM_WEIGHT(32),
    CARR_BILLED_CUST_DIRECT(33),
    VOIDED(34),
    ADJUSTMENT_ERROR(35),
    MINIMUM_CHARGE(36),
    S_N_BE_BILLED(41),
    NOT_PREV_BILLED(43),
    REBILL_SHIPPER(44);

    final int reason;

    AdjustmentReason(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return this.reason;
    }
}
