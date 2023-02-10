package com.pls.core.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * .
 *
 * @author Alexander Kirichenko
 */
public class VendorBillListItemBO {
    private Long id;
    private String bol;
    private String pro;
    private String po;
    private String originZip;
    private String destinationZip;
    private String carrier;
    private Date actualPickupDate;
    private BigDecimal weight;
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getOriginZip() {
        return originZip;
    }

    /**
     * Method sets origin zip and clean if empty.
     * @param originZip - origin zip
     */
    public void setOriginZip(String originZip) {
        this.originZip = originZip;
        if (", , ".equals(this.originZip)) {
            this.originZip = "";
        }
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    /**
     * Method sets destination zip and clean if empty.
     * @param destinationZip - destination zip
     */
    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
        if (", , ".equals(this.destinationZip)) {
            this.destinationZip = "";
        }
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Date getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(Date actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
