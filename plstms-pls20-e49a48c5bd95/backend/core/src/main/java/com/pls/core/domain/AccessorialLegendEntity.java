package com.pls.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for Accessorial Legend containing code and its description.
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LTL_ACCESSORIAL_LEGEND")
public class AccessorialLegendEntity implements Identifiable<String> {

    private static final long serialVersionUID = 5245463658439345611L;

    @Id
    @Column(name = "ACCESSORIAL_CODE", nullable = false)
    String accCode;

    @Column(name = "ACCESSORIAL_NAME", nullable = false)
    String accName;

    /**
     * Default Constructor.
     */
    public AccessorialLegendEntity() {
    }

    /**
     * Constructor with ACCESSORIAL_CODE as a parameter.
     * 
     * @param accCode
     *            to set as ID
     */
    public AccessorialLegendEntity(String accCode) {
        this.accCode = accCode;
    }

    public String getId() {
        return accCode;
    }
    public void setId(String accCode) {
        this.accCode = accCode;
    }
    public String getAccName() {
        return accName;
    }
    public void setAccName(String accName) {
        this.accName = accName;
    }

}
