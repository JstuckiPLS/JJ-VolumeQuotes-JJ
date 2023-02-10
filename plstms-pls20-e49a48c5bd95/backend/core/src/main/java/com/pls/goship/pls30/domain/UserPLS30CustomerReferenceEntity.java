package com.pls.goship.pls30.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Entity for the reference table matching GoShip user and PLS30 customer
 * @author Kircicegi Korkmaz
 */
@Entity
@Table(name = "ftl_user_pls30_customer")
public class UserPLS30CustomerReferenceEntity implements Identifiable<Long> {

    @Id
    @Column(name = "person_id", nullable = false)
    private Long id;

    @Column(name = "pls30_customer_id", nullable = false)
    private Long pls30CustomerId ;
    
    @Column(name = "ax_account_number", nullable = true)
    private String axAccountNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPls30CustomerId() {
        return pls30CustomerId;
    }

    public void setPls30CustomerId(Long pls30CustomerId) {
        this.pls30CustomerId = pls30CustomerId;
    }

    public String getAxAccountNumber() {
        return axAccountNumber;
    }

    public void setAxAccountNumber(String axAccountNumber) {
        this.axAccountNumber = axAccountNumber;
    }

}
