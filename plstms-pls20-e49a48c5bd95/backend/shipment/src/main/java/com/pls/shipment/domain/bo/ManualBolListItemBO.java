package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pls.core.domain.enums.Status;

/**
 * Manual BOL List Item DTO.
 * 
 * @author Alexander Nalapko
 */
public class ManualBolListItemBO {

    private Long id;
    private String bolNumber;
    private String soNumber;
    private String glNumber;
    private String proNumber;
    private String refNumber;
    private String poNumber;
    private String puNumber;
    private String shipper;
    private String consignee;
    private Long customerId;
    private String carrier;
    private Status status;
    private Date pickupDate;
    private ZipBO destination;
    private ZipBO origin;
    private Long carrierId;
    private String scac;
    private String jobNumber;
    private String indicators;
    private String network;

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

    private String accountExecutive;

    private BigDecimal pieces;

    private BigDecimal weight;

    /**
     * Init calculated fields.
     */
    public void init() {
        origin = new ZipBO(originZip, originState, originCity);
        setDestination(new ZipBO(destinationZip, destinationState, destinationCity));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
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

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public ZipBO getOrigin() {
        return origin;
    }

    public void setOrigin(ZipBO origin) {
        this.origin = origin;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public ZipBO getDestination() {
        return destination;
    }

    public void setDestination(ZipBO destination) {
        this.destination = destination;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getIndicators() {
        return indicators;
    }

    public void setIndicators(String indicators) {
        this.indicators = indicators;
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

    public BigDecimal getPieces() {
        return pieces;
    }

    public void setPieces(BigDecimal pieces) {
        this.pieces = pieces;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
