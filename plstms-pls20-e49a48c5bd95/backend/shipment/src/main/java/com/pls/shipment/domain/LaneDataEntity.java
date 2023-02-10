package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Raw entity that contains consolidated data of customer lane data
 * from different tables.
 * 
 * @author Viacheslav Vasianovych
 */
public class LaneDataEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 6431743722077185100L;

    private Long id;

    private String bol;

    private String carrier;

    private Date invoiceDate;

    private Date pickupDate;

    private BigDecimal class1;

    private BigDecimal weight1;

    private BigDecimal class2;

    private BigDecimal weight2;

    private String originZip;

    private String destinationZip;

    private BigDecimal cost;

    private BigDecimal fuel;

    private BigDecimal accessorials;

    private BigDecimal total;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public BigDecimal getClass1() {
        return class1;
    }

    public void setClass1(BigDecimal class1) {
        this.class1 = class1;
    }

    public BigDecimal getWeight1() {
        return weight1;
    }

    public void setWeight1(BigDecimal weight1) {
        this.weight1 = weight1;
    }

    public BigDecimal getClass2() {
        return class2;
    }

    public void setClass2(BigDecimal class2) {
        this.class2 = class2;
    }

    public BigDecimal getWeight2() {
        return weight2;
    }

    public void setWeight2(BigDecimal weight2) {
        this.weight2 = weight2;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getFuel() {
        return fuel;
    }

    public void setFuel(BigDecimal fuel) {
        this.fuel = fuel;
    }

    public BigDecimal getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(BigDecimal accessorials) {
        this.accessorials = accessorials;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
