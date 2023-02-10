package com.pls.dto;

/**
 * DTO for package type.
 *
 * @author Sergey Kirichenko
 */
public class PackageTypeDTO {

    private String code;

    private String label;

    /**
     * Default constructor.
     */
    public PackageTypeDTO() {
    }

    /**
     * Constructor with all fields.
     *
     * @param code package type code
     * @param label package type description
     */
    public PackageTypeDTO(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
