package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * The entity mapped to finan_billers.
 * @author Hima Bindu Challa
 *
 */
@Entity
@Table(name = "finan_billers")
public class FinanBillersEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -1016494551137035986L;

    @Id
    @Column(name = "FINAN_BILLER_ID")
    private Long id;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "BILLER_TYPE")
    private String billerType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getBillerType() {
        return billerType;
    }

    public void setBillerType(String billerType) {
        this.billerType = billerType;
    }


}
