package com.pls.extint.shared;

import java.util.Date;

/**
 * Request data for calling Tracking services.
 * 
 * @author Pavani Challa
 * 
 */
public class TrackingRequestVO extends ApiRequestVO {

    private static final long serialVersionUID = 7562342125672349871L;

    private Long weight;

    private Integer pieces;

    private Date pickupDate;

    private String originZip;

    private String destZip;

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getOriginZip() {
        return originZip;
    }

    /**
     * Sets the zip code for the origin. Trims any digits after the "-".
     * 
     * @param originZip
     *            zip code of the origin
     */
    public void setOriginZip(String originZip) {
        if (originZip != null && originZip.indexOf('-') > 0) {
            this.originZip = originZip.substring(0, originZip.indexOf('-'));
        } else {
            this.originZip = originZip;
        }
    }

    public String getDestZip() {
        return destZip;
    }

    /**
     * Sets the zip code for the destination. Trims any digits after the "-".
     * 
     * @param destZip
     *            Zip code of the destination
     */
    public void setDestZip(String destZip) {
        if (destZip != null && destZip.indexOf('-') > 0) {
            this.destZip = destZip.substring(0, destZip.indexOf('-'));
        } else {
            this.destZip = destZip;
        }
    }
}
