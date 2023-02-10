package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * BO represents single line in table of shipment alerts.
 * 
 * @author Alexander Kirichenko
 */
public class ShipmentTrackingBoardListItemBO {

    private Long id;

    private String bol;

    private String pro;

    private String glNumber;

    private String soNumber;

    private String trailer;

    private String customerName;

    private String carrierName;

    private Date pickupDate;

    private Date pickupWindowStart;

    private ZonedDateTime pickupWindowEnd;

    private String originTimezone;

    private Integer pieces;

    private Integer weight;

    private Boolean apiCapable = Boolean.FALSE;

    private ShipmentStatus status;

    private Date estimatedDelivery;

    private String origin;

    private String destination;

    private String shipper;

    private String consignee;

    private Date createdDate;

    private String createdBy;

    private CityStateZip originAddress;

    private CityStateZip destinationAddress;

    private String srNumber;

    private String poNumber;

    private String puNumber;

    private BigDecimal revenue;

    private BigDecimal cost;

    private BigDecimal margin;

    private String indicators;

    private ZonedDateTime dispatchedDate;

    private String network;

    private CarrierIntegrationType integrationType;

    private String accountExecutive;

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

    public String getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getPickupWindowStart() {
        return pickupWindowStart;
    }

    public void setPickupWindowStart(Date pickupWindowStart) {
        this.pickupWindowStart = pickupWindowStart;
    }

    public ZonedDateTime getPickupWindowEnd() {
        return pickupWindowEnd;
    }

    public void setPickupWindowEnd(Date pickupWindowEnd) {
        this.pickupWindowEnd = pickupWindowEnd != null
                ? ZonedDateTime.ofInstant(pickupWindowEnd.toInstant(), ZoneOffset.UTC) : null;
    }

    public String getOriginTimezone() {
        return originTimezone;
    }

    public void setOriginTimezone(String originTimezone) {
        this.originTimezone = originTimezone;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getApiCapable() {
        return apiCapable;
    }

    public void setApiCapable(Boolean apiCapable) {
        this.apiCapable = apiCapable;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Date getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Date estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
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

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public CityStateZip getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(CityStateZip originAddress) {
        this.originAddress = originAddress;
    }

    public CityStateZip getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(CityStateZip destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSrNumber() {
        return srNumber;
    }

    public void setSrNumber(String srNumber) {
        this.srNumber = srNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getPuNumber() {
        return puNumber;
    }

    public void setPuNumber(String puNumber) {
        this.puNumber = puNumber;
    }

    public CarrierIntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(CarrierIntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public ZonedDateTime getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate == null ? null
                : ZonedDateTime.ofInstant(dispatchedDate.toInstant(), ZoneOffset.UTC);
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }
}
