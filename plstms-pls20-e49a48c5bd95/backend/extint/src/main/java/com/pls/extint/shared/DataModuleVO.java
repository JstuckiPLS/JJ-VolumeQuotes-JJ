package com.pls.extint.shared;

/**
 * Stores the details about the available tariffs from SMC3.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class DataModuleVO {

    private String description;
    private String effectiveDate;
    private String productNumber;
    private String release;
    private String tariffName;

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setEffectiveDate(String value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the productNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getProductNumber() {
        return productNumber;
    }

    /**
     * Sets the value of the productNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setProductNumber(String value) {
        this.productNumber = value;
    }

    /**
     * Gets the value of the release property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRelease() {
        return release;
    }

    /**
     * Sets the value of the release property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRelease(String value) {
        this.release = value;
    }

    /**
     * Gets the value of the tariffName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTariffName() {
        return tariffName;
    }

    /**
     * Sets the value of the tariffName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setTariffName(String value) {
        this.tariffName = value;
    }

    @Override
    public String toString() {
        return "DataModuleVO [description=" + description + ", effectiveDate=" + effectiveDate + ", productNumber="
                + productNumber + ", release=" + release + ", tariffName=" + tariffName + "]";
    }

}
