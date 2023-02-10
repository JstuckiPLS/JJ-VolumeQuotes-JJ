package com.pls.invoice.domain.bo.enums;

/**
 * Invoice Error codes.
 * 
 * @author Aleksandr Leshchenko
 */
public enum InvoiceErrorCode {
    CRN, // Cost or revenue null / not set
    TI, // Totals not equal to items
    BOL, // Missing BOL #
    PD, // No Confirm Pickup Date
    ZR, // zero revenue and costs
    BD, // Invalid Freight Bill Date
    USCN, // Cannot bill a USD load with a CND carrier
    CNUS, // Cannot bill a CND load with a USD carrier
    AL // Adjustment line item not calculating correctly
}
