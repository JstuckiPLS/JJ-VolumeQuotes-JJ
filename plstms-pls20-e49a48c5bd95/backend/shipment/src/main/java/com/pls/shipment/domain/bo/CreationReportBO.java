package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.core.domain.enums.CommodityClass;

/**
 * BO for shipment report.
 * 
 * @author Yasaman Palumbo
 */
public class CreationReportBO {

    private Long loadId;
    private String invoiceNumber;
    private String name;
    private String creationMethod;
    private Date createdDate;
    private String proNumber;
    private String bol;
    private CommodityClass shipmentClass;
    private String poNumber;
    private Date shipDate;
    private String scacCode;
    private String shipperName;
    private String originCity;
    private String originSt;
    private String originZip;
    private String consignee;
    private String destCity;
    private String destSt;
    private String destZip;
    private Integer weight;
    private BigDecimal cost;
    private BigDecimal costPerWeight;

    public Long getLoadId() {
        return loadId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getName() {
        return name;
    }

    public String getCreationMethod() {
        return creationMethod;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getProNumber() {
        return proNumber;
    }

    public String getBol() {
        return bol;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public String getScacCode() {
        return scacCode;
    }

    public String getShipperName() {
        return shipperName;
    }

    public String getOriginSt() {
        return originSt;
    }

    public String getOriginZip() {
        return originZip;
    }

    public String getConsignee() {
        return consignee;
    }

    public String getDestSt() {
        return destSt;
    }

    public String getDestZip() {
        return destZip;
    }

    public Integer getWeight() {
        return weight;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getCostPerWeight() {
        return costPerWeight;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationMethod(String creationMethod) {
        this.creationMethod = creationMethod;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public void setScacCode(String scacCode) {
        this.scacCode = scacCode;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public void setOriginSt(String originSt) {
        this.originSt = originSt;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public void setDestSt(String destSt) {
        this.destSt = destSt;
    }

    public void setDestZip(String destZip) {
        this.destZip = destZip;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setCostPerWeight(BigDecimal costPerWeight) {
        this.costPerWeight = costPerWeight;
    }

    public CommodityClass getShipmentClass() {
        return shipmentClass;
    }

    public void setShipmentClass(CommodityClass shipmentClass) {
        this.shipmentClass = shipmentClass;
    }

}
