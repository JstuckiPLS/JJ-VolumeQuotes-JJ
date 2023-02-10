/**
 * 
 */
package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

/**
 * BO for Booked tab.
 * 
 * @author Alexander Nalapko
 *
 */
public class ShipmentTrackingBoardBookedListItemBO extends ShipmentTrackingBoardListItemBO {

    private BigDecimal prepaidTotalAmount;

    public BigDecimal getPrepaidTotalAmount() {
        return prepaidTotalAmount;
    }

    public void setPrepaidTotalAmount(BigDecimal prepaidTotalAmount) {
        this.prepaidTotalAmount = prepaidTotalAmount;
    }

}
