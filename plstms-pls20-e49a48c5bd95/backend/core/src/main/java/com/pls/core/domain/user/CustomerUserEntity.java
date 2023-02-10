package com.pls.core.domain.user;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pls.core.domain.organization.CustomerEntity;

/**
 * Customer user.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("SHIPPER")
public class CustomerUserEntity extends OrganizationUserEntity {
    public static final String Q_LIST_ACTIVE_BY_CUSTOMER_ID = "com.pls.core.domain.user.CustomerUserEntity.Q_LIST_ACTIVE_BY_CUSTOMER_ID";
    public static final String Q_GET_USER_CUSTOMERS_BY_NAME = "com.pls.core.domain.user.CustomerUserEntity.Q_GET_USER_CUSTOMERS_BY_NAME";
    public static final String Q_GET_BY_PERSON_ID_AND_ORG_ID = "com.pls.core.domain.user.CustomerUserEntity.Q_GET_BY_PERSON_ID_AND_ORG_ID";

    private static final long serialVersionUID = -4027550370522982210L;

    @JoinColumn(name = "ORG_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerEntity customer;

    @Column(name = "ORG_ID", insertable = false, updatable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", nullable = false)
    protected UserEntity user;

    @Column(name = "PERSON_ID", insertable = false, updatable = false)
    private Long personId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    public CustomerEntity getCustomer() {
        return customer;
    }

    /**
     * Sets customer.
     *
     * @param customer {@link CustomerEntity}
     */
    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
        if (customerId == null && customer != null) {
            customerId = customer.getId();
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
