package com.pls.dto;

/**
 * Company code DTO.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class CompanyCodeDTO {

    private String companyCode;

    private String description;

    /**
     * Constructor.
     * 
     * @param companyCode field.
     * @param description field.
     */
    public CompanyCodeDTO(String companyCode, String description) {
        this.companyCode = companyCode;
        this.description = description;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
