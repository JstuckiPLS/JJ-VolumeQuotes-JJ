package com.pls.organization.domain.bo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * BO for paperwork email.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class PaperworkEmailBO {

    private Long loadId;
    private String shipperRef;
    private String bol;
    private String origin;
    private String destination;
    private Date pickupDate;
    private BigDecimal weight;
    private String invoiceNumber;
    private String documents;
    private String note;
    private Long carrierId;
    private String carrierEmail;
    private String carrierName;

    public Long getLoadId() {
        return loadId;
    }
    public void setLoadId(BigDecimal loadId) {
        this.loadId = loadId.longValue();
    }
    public String getShipperRef() {
        return shipperRef;
    }
    public void setShipperRef(String shipperRef) {
        this.shipperRef = shipperRef;
    }
    public String getBol() {
        return bol;
    }
    public void setBol(String bol) {
        this.bol = bol;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public Date getPickupDate() {
        return pickupDate;
    }
    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    public String getDocuments() {
        return documents;
    }
    public void setDocuments(String documents) {
        this.documents = Arrays.stream(documents.split(",")).distinct().collect(Collectors.joining(","));
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Long getCarrierId() {
        return carrierId;
    }
    public void setCarrierId(BigDecimal carrierId) {
        this.carrierId = carrierId.longValue();
    }
    public String getCarrierEmail() {
        return carrierEmail;
    }
    public void setCarrierEmail(String carrierEmail) {
        this.carrierEmail = carrierEmail;
    }
    public String getCarrierName() {
        return carrierName;
    }
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
}
