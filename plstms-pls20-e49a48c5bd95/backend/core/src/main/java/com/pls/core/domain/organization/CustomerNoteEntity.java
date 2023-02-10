package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Implementation of {@link NoteEntity} for customer.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("ORG")
public class CustomerNoteEntity extends NoteEntity {

    private static final long serialVersionUID = -8480470763369396395L;

    public static final String Q_BY_CUSTOMER_ID = "com.pls.core.domain.organization.CustomerNoteEntity.Q_BY_CUSTOMER_ID";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", nullable = false)
    private CustomerEntity customer;

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

}
