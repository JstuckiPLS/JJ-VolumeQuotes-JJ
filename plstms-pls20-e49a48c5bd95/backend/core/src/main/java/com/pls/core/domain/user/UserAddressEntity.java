package com.pls.core.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;

/**
 * User address data.
 * 
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "USER_ADDRESSES")
public class UserAddressEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -4698514025256433494L;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private AddressEntity address;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_addr_sequence")
    @SequenceGenerator(name = "usr_addr_sequence", sequenceName = "USRA_SEQ", allocationSize = 1)
    @Column(name = "USER_ADDRESS_ID")
    private Long id;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_USER_ID")
    private CustomerUserEntity customerUser;

    //Readonly field to check whether customer user associtaed with address or not
    @Column(name = "ORG_USER_ID", insertable = false, updatable = false)
    private Long customerUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", nullable = false, insertable = true, updatable = false)
    private UserEntity user;

    @Override
    public boolean equals(Object obj) {
        // Potentially it forces loading for all lazy relations. Good solution!
        boolean result = false;
        if (obj instanceof UserAddressEntity) {
            if (obj == this) {
                result = true;
            } else {
                final UserAddressEntity other = (UserAddressEntity) obj;
                result = new EqualsBuilder().append(getAddress(), other.getAddress())
                        .append(getCustomerUser(), other.getCustomerUser()).isEquals();
            }
        }
        return result;
    }

    public AddressEntity getAddress() {
        return address;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public CustomerUserEntity getCustomerUser() {
        return customerUser;
    }

    public Long getCustomerUserId() {
        return customerUserId;
    }

    @Override
    public int hashCode() {
     // Potentially it forces loading for all lazy relations. Good solution!
        return new HashCodeBuilder().append(getAddress()).append(getCustomerUser()).toHashCode();
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set {@link #getCustomerUser()} and {@link #getUser()} values.
     * 
     * @param customerUser
     *            Normally should be not <code>null</code> {@link CustomerUserEntity} value.
     */
    public void setCustomerUser(CustomerUserEntity customerUser) {
        this.customerUser = customerUser;
        if (customerUser != null) {
            user = customerUser.getUser();
        } else {
            user = null;
        }
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
