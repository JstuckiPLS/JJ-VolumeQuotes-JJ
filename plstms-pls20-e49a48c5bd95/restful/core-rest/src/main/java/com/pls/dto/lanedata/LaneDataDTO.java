package com.pls.dto.lanedata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Lane data DTO Object.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
@XmlRootElement
public class LaneDataDTO implements Serializable {

    private static final long serialVersionUID = 8699246155742294788L;

    private BigDecimal accessorials;

    private String bol;

    private String carrier;

    private BigDecimal class1;

    private BigDecimal class2;

    private BigDecimal cost;

    private String destinationZip;

    private BigDecimal fuel;

    private Long id;

    private Date invoiceDate;

    private String originZip;

    private Date pickupDate;

    private BigDecimal total;

    private BigDecimal weight1;

    private BigDecimal weight2;

    public BigDecimal getAccessorials() {
        return accessorials;
    }

    public String getBol() {
        return bol;
    }

    public String getCarrier() {
        return carrier;
    }

    public BigDecimal getClass1() {
        return class1;
    }

    public BigDecimal getClass2() {
        return class2;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public BigDecimal getFuel() {
        return fuel;
    }

    public Long getId() {
        return id;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public String getOriginZip() {
        return originZip;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getWeight1() {
        return weight1;
    }

    public BigDecimal getWeight2() {
        return weight2;
    }

    public void setAccessorials(BigDecimal accessorials) {
        this.accessorials = accessorials;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setClass1(BigDecimal class1) {
        this.class1 = class1;
    }

    public void setClass2(BigDecimal class2) {
        this.class2 = class2;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public void setFuel(BigDecimal fuel) {
        this.fuel = fuel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setWeight1(BigDecimal weight1) {
        this.weight1 = weight1;
    }

    public void setWeight2(BigDecimal weight2) {
        this.weight2 = weight2;
    }
}
