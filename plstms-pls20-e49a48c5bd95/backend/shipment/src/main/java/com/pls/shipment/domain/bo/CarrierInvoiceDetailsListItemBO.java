package com.pls.shipment.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.pls.core.domain.enums.CarrierInvoiceReasons;

/**
 * Business object to populate Vendor Bills on UI side.
 * 
 * @author Artem Arapov
 *
 */

public class CarrierInvoiceDetailsListItemBO {

    private Long id;

    private String carrierName;

    private Long carrierId;

    private String customerName;

    private Long customerId;

    private CityStateZip originAddress = new CityStateZip();

    private CityStateZip destinationAddress = new CityStateZip();

    private Date actualPickupDate;

    private BigDecimal totalWeight;

    private String proNumber;

    private String bolNumber;

    private String poNumber;

    private BigDecimal netAmount;

    private String shipper;

    private String consignee;

    private String scac;

    private String note;

    private CarrierInvoiceReasons reasonCode;

    private String loadId;

    private String createdBy;

    private Date createdDate;

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Date getActualPickupDate() {
        return actualPickupDate;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public String getProNumber() {
        return proNumber;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setActualPickupDate(Date actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
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

    public String getOriginZip() {
        return originAddress.getZip();
    }

    public String getDestinationZip() {
        return destinationAddress.getZip();
    }

    /**
     * Set origin zip.
     * @param originZip zip code
     */
    public void setOriginZip(String originZip) {
        originAddress.setZip(originZip);
    }

    /**
     * Set destination zip.
     * @param destinationZip zip code
     */
    public void setDestinationZip(String destinationZip) {
        destinationAddress.setZip(destinationZip);
    }

    public String getOriginState() {
        return originAddress.getState();
    }

    public String getDestinationState() {
        return destinationAddress.getState();
    }

    /**
     * Set origin state.
     * @param originState state
     */
    public void setOriginState(String originState) {
        originAddress.setState(originState);
    }

    /**
     * Set destination state.
     * @param destinationState state
     */
    public void setDestinationState(String destinationState) {
        destinationAddress.setState(destinationState);
    }

    public String getOriginCity() {
        return originAddress.getCity();
    }

    public String getDestinationCity() {
        return destinationAddress.getCity();
    }

    /**
     * Set Origin city.
     * @param originCity city
     */
    public void setOriginCity(String originCity) {
        originAddress.setCity(originCity);
    }

    /**
     * Set destination city.
     * @param destinationCity city
     */
    public void setDestinationCity(String destinationCity) {
        destinationAddress.setCity(destinationCity);
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CarrierInvoiceReasons getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(CarrierInvoiceReasons reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

}
