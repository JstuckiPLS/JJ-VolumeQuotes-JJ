package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.Status;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * BO for shipment report.
 * 
 * @author Brichak Aleksandr
 */
public class FreightAnalysisReportBO {

    private String customerName;
    private Date departure;
    private Date earlyScheduledArrival;
    private Long loadId;
    private String bol;
    private String shipperRef;
    private String poNumber;
    private String glNumber;
    private String proNumber;
    private String scacCode;
    private String carrierName;
    private ShipmentDirection shipmentDirection;
    private String originContact;
    private String originAddress;
    private String originCity;
    private String originStateCode;
    private String originZip;
    private String destinationContact;
    private String destinationAddress;
    private String destinationCity;
    private String destinationStateCode;
    private String destinationZip;
    private BigDecimal totalRevenue;
    private BigDecimal custLhCost;
    private BigDecimal custFsCost;
    private BigDecimal benchmarkAmount;
    private String businessUnit;
    private Status opsStatus;
    private String billingStatus;
    private Long pricingProfileId;
    private PricingType ltlPricingType;
    
    private List<AuditReasonReportBO> auditReasons;
    private List<ProductReportBO> products;
    private List<AccessorialReportBO> accessorials;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getShipperRef() {
        return shipperRef;
    }

    public void setShipperRef(String shipperRef) {
        this.shipperRef = shipperRef;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getScacCode() {
        return scacCode;
    }

    public void setScacCode(String scacCode) {
        this.scacCode = scacCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getOriginContact() {
        return originContact;
    }

    public void setOriginContact(String originContact) {
        this.originContact = originContact;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginStateCode() {
        return originStateCode;
    }

    public void setOriginStateCode(String originStateCode) {
        this.originStateCode = originStateCode;
    }

    public String getDestinationContact() {
        return destinationContact;
    }

    public void setDestinationContact(String destinationContact) {
        this.destinationContact = destinationContact;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationStateCode() {
        return destinationStateCode;
    }

    public void setDestinationStateCode(String destinationStateCode) {
        this.destinationStateCode = destinationStateCode;
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

    public ShipmentDirection getShipmentDirection() {
        return shipmentDirection;
    }

    public void setShipmentDirection(ShipmentDirection shipmentDirection) {
        this.shipmentDirection = shipmentDirection;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getCustLhCost() {
        return custLhCost;
    }

    public void setCustLhCost(BigDecimal custLhCost) {
        this.custLhCost = custLhCost;
    }

    public BigDecimal getCustFsCost() {
        return custFsCost;
    }

    public void setCustFsCost(BigDecimal custFsCost) {
        this.custFsCost = custFsCost;
    }

    public BigDecimal getBenchmarkAmount() {
        return benchmarkAmount;
    }

    public void setBenchmarkAmount(BigDecimal benchmarkAmount) {
        this.benchmarkAmount = benchmarkAmount;
    }

    public List<ProductReportBO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductReportBO> products) {
        this.products = products;
    }

    public List<AccessorialReportBO> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(List<AccessorialReportBO> accessorials) {
        this.accessorials = accessorials;
    }

    public Date getEarlyScheduledArrival() {
        return earlyScheduledArrival;
    }

    public void setEarlyScheduledArrival(Date earlyScheduledArrival) {
        this.earlyScheduledArrival = earlyScheduledArrival;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public Status getOpsStatus() {
        return opsStatus;
    }

    public void setOpsStatus(Status opsStatus) {
        this.opsStatus = opsStatus;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public PricingType getLtlPricingType() {
        return ltlPricingType;
    }

    public void setLtlPricingType(PricingType ltlPricingType) {
        this.ltlPricingType = ltlPricingType;
    }

    public List<AuditReasonReportBO> getAuditReasons() {
        return auditReasons;
    }

    public void setAuditReasons(List<AuditReasonReportBO> auditReasons) {
        this.auditReasons = auditReasons;
    }
}
