package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.Status;

/**
 * Active Shipment List Item DTO.
 * 
 * @author Artem Arapov
 */
public class ShipmentListItemBO {

    private String bolNumber;
    private String carrier;
    private Long carrierId;
    private Date createdDate;
    private Long customerId;
    private String customerName;
    private Date deliveryDate;
    private String glNumber;
    private Date pickupDate;
    private String poNumber;
    private String proNumber;
    private String puNumber;
    private String refNumber;
    private Long shipmentId;
    private String soNumber;
    private Status status;
    private String billingStatus;
    private String network;

    private BigDecimal total;
    private String trailer;
    private Integer weight;
    private String shipper;
    private String consignee;
    private String createdBy;
    private ZonedDateTime arrivalWindowEnd;
    private ZipBO origin;
    private ZipBO destination;
    private Boolean requirePermissionChecking;
    private Integer billToRecieved;
    private Boolean invoiced;
    private String jobNumber;

    @JsonIgnore
    private String destinationCity;
    @JsonIgnore
    private String destinationState;
    @JsonIgnore
    private String destinationZip;
    @JsonIgnore
    private String originCity;
    @JsonIgnore
    private String originState;
    @JsonIgnore
    private String originZip;
    private BigDecimal revenue;
    private BigDecimal margin;
    private boolean manualBol;
    private String scac;
    private CarrierIntegrationType integrationType;
    private String indicators;
    private BigDecimal cost;
    private String noteComment;
    private ZonedDateTime noteCreatedDate;
    private String noteModifiedBy;
    private Long numberOfNotes;
    private String originAddress;
    private String destinationAddress;
    private boolean adjustment;
    private ZonedDateTime dispatchedDate;
    private String accountExecutive;
    private Integer pieces;

    /**
     * Init calculated fields.
     */
    public void init() {
        origin = new ZipBO(originZip, originState, originCity);
        destination = new ZipBO(destinationZip, destinationState, destinationCity);
    }


    public boolean isAdjustment() {
        return adjustment;
    }


    public void setAdjustment(boolean adjustment) {
        this.adjustment = adjustment;
    }


    public String getBolNumber() {
        return bolNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public String getNetwork() {
        return network;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public ZipBO getDestination() {
        return destination;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public String getGlNumber() {
        return glNumber;
    }

    public ZipBO getOrigin() {
        return origin;
    }

    public String getOriginCity() {
        return originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public String getOriginZip() {
        return originZip;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public String getProNumber() {
        return proNumber;
    }

    public String getPuNumber() {
        return puNumber;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setDestination(ZipBO destination) {
        this.destination = destination;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public void setGlNumber(String glNumber) {
        this.glNumber = glNumber;
    }

    public void setOrigin(ZipBO origin) {
        this.origin = origin;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public void setPuNumber(String puNumber) {
        this.puNumber = puNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public void setShipmentId(Long id) {
        shipmentId = id;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getArrivalWindowEnd() {
        return arrivalWindowEnd;
    }

    public void setArrivalWindowEnd(Date arrivalWindowEnd) {
        this.arrivalWindowEnd = arrivalWindowEnd == null ? null
                : ZonedDateTime.ofInstant(arrivalWindowEnd.toInstant(), ZoneOffset.UTC);
    }

    public Boolean getRequirePermissionChecking() {
        return requirePermissionChecking;
    }

    public void setRequirePermissionChecking(Boolean requirePermissionChecking) {
        this.requirePermissionChecking = requirePermissionChecking;
    }

    public Integer getBillToRecieved() {
        return billToRecieved;
    }

    public void setBillToRecieved(Integer billToRecieved) {
        this.billToRecieved = billToRecieved;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public Boolean getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(Boolean invoiced) {
        this.invoiced = invoiced;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public CarrierIntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(CarrierIntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public boolean isManualBol() {
        return manualBol;
    }

    public void setManualBol(boolean isManualBol) {
        this.manualBol = isManualBol;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(String noteComment) {
        this.noteComment = noteComment;
    }

    public ZonedDateTime getNoteCreatedDate() {
        return noteCreatedDate;
    }

    public void setNoteCreatedDate(Date noteCreatedDate) {
        this.noteCreatedDate = noteCreatedDate == null ? null
                : ZonedDateTime.ofInstant(noteCreatedDate.toInstant(), ZoneOffset.UTC);
    }

    public String getNoteModifiedBy() {
        return noteModifiedBy;
    }

    public void setNoteModifiedBy(String noteModifiedBy) {
        this.noteModifiedBy = noteModifiedBy;
    }

    public Long getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(Long numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public ZonedDateTime getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate == null ? null
                : ZonedDateTime.ofInstant(dispatchedDate.toInstant(), ZoneOffset.UTC);
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }
}
