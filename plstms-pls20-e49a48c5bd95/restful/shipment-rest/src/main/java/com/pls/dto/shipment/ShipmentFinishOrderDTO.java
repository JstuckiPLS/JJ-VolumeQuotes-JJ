package com.pls.dto.shipment;

import java.util.Date;
import java.util.List;

import com.pls.dto.ShipmentNotificationsDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;

/**
 * Shipment DTO for Finish order/Shipment details.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentFinishOrderDTO {
    private Integer originVersion;

    private Integer destinationVersion;

    private List<ShipmentNotificationsDTO> shipmentNotifications;

    private PickupAndDeliveryWindowDTO pickupWindowFrom;

    private PickupAndDeliveryWindowDTO pickupWindowTo;

    private PickupAndDeliveryWindowDTO deliveryWindowFrom;

    private PickupAndDeliveryWindowDTO deliveryWindowTo;

    private Date pickupDate;

    private Date estimatedDelivery;

    private Date actualPickupDate;

    private Date actualDeliveryDate;

    // ref, po, pu numbers
    private String ref;

    private String poNumber;

    private String puNumber;

    private String soNumber;

    private String glNumber;

    private String opBolNumber;

    private String partNumber;

    private String trailerNumber;

    private List<JobNumberDTO> jobNumbers;

    private String requestedBy;

    // notes
    private String pickupNotes;

    private String deliveryNotes;

    private String shippingLabelNotes;

    private List<ShipmentMaterialDTO> quoteMaterials;

    private Long originDetailsId;

    private Long destinationDetailsId;

    public Integer getOriginVersion() {
        return originVersion;
    }

    public void setOriginVersion(Integer originVersion) {
        this.originVersion = originVersion;
    }

    public Integer getDestinationVersion() {
        return destinationVersion;
    }

    public void setDestinationVersion(Integer destinationVersion) {
        this.destinationVersion = destinationVersion;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowFrom() {
        return pickupWindowFrom;
    }

    public void setPickupWindowFrom(PickupAndDeliveryWindowDTO pickupWindowFrom) {
        this.pickupWindowFrom = pickupWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowTo() {
        return pickupWindowTo;
    }

    public void setPickupWindowTo(PickupAndDeliveryWindowDTO pickupWindowTo) {
        this.pickupWindowTo = pickupWindowTo;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Date estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public Date getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(Date actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getShippingLabelNotes() {
        return shippingLabelNotes;
    }

    public void setShippingLabelNotes(String shippingLabelNotes) {
        this.shippingLabelNotes = shippingLabelNotes;
    }

    public List<ShipmentMaterialDTO> getQuoteMaterials() {
        return quoteMaterials;
    }

    public void setQuoteMaterials(List<ShipmentMaterialDTO> quoteMaterials) {
        this.quoteMaterials = quoteMaterials;
    }

    public Long getOriginDetailsId() {
        return originDetailsId;
    }

    public void setOriginDetailsId(Long originDetailsId) {
        this.originDetailsId = originDetailsId;
    }

    public Long getDestinationDetailsId() {
        return destinationDetailsId;
    }

    public void setDestinationDetailsId(Long destinationDetailsId) {
        this.destinationDetailsId = destinationDetailsId;
    }

    public List<ShipmentNotificationsDTO> getShipmentNotifications() {
        return shipmentNotifications;
    }

    public void setShipmentNotifications(List<ShipmentNotificationsDTO> shipmentNotifications) {
        this.shipmentNotifications = shipmentNotifications;
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

    public String getOpBolNumber() {
        return opBolNumber;
    }

    public void setOpBolNumber(String opBolNumber) {
        this.opBolNumber = opBolNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowFrom() {
        return deliveryWindowFrom;
    }

    public void setDeliveryWindowFrom(PickupAndDeliveryWindowDTO deliveryWindowFrom) {
        this.deliveryWindowFrom = deliveryWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowTo() {
        return deliveryWindowTo;
    }

    public void setDeliveryWindowTo(PickupAndDeliveryWindowDTO deliveryWindowTo) {
        this.deliveryWindowTo = deliveryWindowTo;
    }

    public List<JobNumberDTO> getJobNumbers() {
        return jobNumbers;
    }

    public void setJobNumbers(List<JobNumberDTO> jobNumbers) {
        this.jobNumbers = jobNumbers;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }


}