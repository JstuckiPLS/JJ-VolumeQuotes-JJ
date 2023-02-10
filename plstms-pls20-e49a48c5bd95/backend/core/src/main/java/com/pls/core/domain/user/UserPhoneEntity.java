package com.pls.core.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;

/**
 * Phone/fax and other contact numbers for {@link CustomerUserEntity}s.
 * 
 * @author Maxim Medvedev
 */
@Entity
@Table(name = "USER_PHONES")
public class UserPhoneEntity implements Identifiable<Long>, PhoneNumber, HasModificationInfo {
    private static final long serialVersionUID = 745541726016986698L;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "DIALING_CODE")
    private String countryCode;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usrp_sequence")
    @SequenceGenerator(name = "usrp_sequence", sequenceName = "USRP_SEQ", allocationSize = 1)
    @Column(name = "USER_PHONE_ID")
    private Long id;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();
    @Column(name = "PHONE_NUMBER", nullable = false)
    private String number;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_USER_ID")
    private CustomerUserEntity customerUser;
    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHONE_TYPE", nullable = false)
    private PhoneType type;

    /**
     * Must be the same as in parent {@link CustomerUserEntity} entity. <b>Normally you should not use this
     * relation.</b>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", nullable = false, insertable = true, updatable = false)
    private UserEntity user;

    @Column(name = "EXTENSION")
    private String extension;

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof UserPhoneEntity) {
            if (obj == this) {
                result = true;
            } else {
                final UserPhoneEntity other = (UserPhoneEntity) obj;
                result = new EqualsBuilder().append(getCountryCode(), other.getCountryCode())
                        .append(getAreaCode(), other.getAreaCode()).append(getNumber(), other.getNumber())
                        .append(getType(), other.getType())
                        .append(getStatus(), other.getStatus()).isEquals();
            }
        }
        return result;
    }

    @Override
    public String getAreaCode() {
        return areaCode;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public CustomerUserEntity getCustomerUser() {
        return customerUser;
    }

    public Status getStatus() {
        return status;
    }

    public PhoneType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getCountryCode()).append(getAreaCode()).append(getNumber())
               .append(getType()).append(getStatus()).toHashCode();
    }

    @Override
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
