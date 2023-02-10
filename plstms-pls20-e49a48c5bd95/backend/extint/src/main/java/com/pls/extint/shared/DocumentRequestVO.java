package com.pls.extint.shared;

import java.util.Date;

/**
 * VO for the request data for calling document APIs.
 * 
 * @author Pavani Challa
 * 
 */
public class DocumentRequestVO extends ApiRequestVO {

    private static final long serialVersionUID = 6455343242317654352L;

    private Date pickupDate;

    private String originZip;

    private String destZip;

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getDestZip() {
        return destZip;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }


}
