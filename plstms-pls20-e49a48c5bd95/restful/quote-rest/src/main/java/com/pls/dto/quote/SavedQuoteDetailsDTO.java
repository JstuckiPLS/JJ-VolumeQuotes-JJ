package com.pls.dto.quote;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pls.dto.address.ZipDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;

/**
 * DTO that is used to transfer data for saved quotes table item tooltip.
 *
 * @author Ivan Shapovalov
 */
public class SavedQuoteDetailsDTO {
    private String carrierName;
    private String carrierLogoUrl;

    private ZipDTO pickup;
    private ZipDTO delivery;

    /**
     * Estimated transit time in minutes
     */
    private Long estimatedTransitTime;
    private Date estimatedTransitDate;

    private BigDecimal carrierCost;
    private BigDecimal customerRevenue;
    private BigDecimal fuelSurcharge;
    private BigDecimal accessorials;

    private BigDecimal guaranteedCost;
    private Long guaranteedBy;

    private String serviceType;

    private List<ShipmentMaterialDTO> materials;

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getCarrierLogoUrl() {
        return carrierLogoUrl;
    }

    public void setCarrierLogoUrl(String carrierLogoUrl) {
        this.carrierLogoUrl = carrierLogoUrl;
    }

    public ZipDTO getPickup() {
        return pickup;
    }

    public void setPickup(ZipDTO pickup) {
        this.pickup = pickup;
    }

    public ZipDTO getDelivery() {
        return delivery;
    }

    public void setDelivery(ZipDTO delivery) {
        this.delivery = delivery;
    }

    public Long getEstimatedTransitTime() {
        return estimatedTransitTime;
    }

    public void setEstimatedTransitTime(Long estimatedTransitTime) {
        this.estimatedTransitTime = estimatedTransitTime;
    }

    public Date getEstimatedTransitDate() {
        return estimatedTransitDate;
    }

    public void setEstimatedTransitDate(Date estimatedTransitDate) {
        this.estimatedTransitDate = estimatedTransitDate;
    }

    public BigDecimal getCarrierCost() {
        return carrierCost;
    }

    public void setCarrierCost(BigDecimal carrierCost) {
        this.carrierCost = carrierCost;
    }

    public BigDecimal getCustomerRevenue() {
        return customerRevenue;
    }

    public void setCustomerRevenue(BigDecimal customerRevenue) {
        this.customerRevenue = customerRevenue;
    }

    public BigDecimal getFuelSurcharge() {
        return fuelSurcharge;
    }

    public void setFuelSurcharge(BigDecimal fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public BigDecimal getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(BigDecimal accessorials) {
        this.accessorials = accessorials;
    }

    public BigDecimal getGuaranteedCost() {
        return guaranteedCost;
    }

    public void setGuaranteedCost(BigDecimal guaranteedCost) {
        this.guaranteedCost = guaranteedCost;
    }

    public Long getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(Long guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<ShipmentMaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<ShipmentMaterialDTO> materials) {
        this.materials = materials;
    }
}
