package com.pls.dto;

/**
 * Accessorials mapping data transfer object.
 * 
 * @author Dmitriy Davydenko.
 *
 */
public class LtlAccessorialsMappingDTO {

    private Long id;

    private String plsCode;

    private String description;

    private String carrierCode;

    private Boolean defaultAccessorial;

    private Long carrierId;

    public String getPlsCode() {
        return plsCode;
    }

    public void setPlsCode(String plsCode) {
        this.plsCode = plsCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Boolean getDefaultAccessorial() {
        return defaultAccessorial;
    }

    public void setDefaultAccessorial(Boolean defaultAccessorial) {
        this.defaultAccessorial = defaultAccessorial;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
