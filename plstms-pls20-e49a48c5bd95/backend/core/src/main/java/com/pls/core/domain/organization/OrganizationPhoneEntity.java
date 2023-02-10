package com.pls.core.domain.organization;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Organization phone.
 * 
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "ORGANIZATION_PHONES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PHONE_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class OrganizationPhoneEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 3577779233915413737L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgp_sequence")
    @SequenceGenerator(name = "orgp_sequence", sequenceName = "ORGP_SEQ", allocationSize = 1)
    @Column(name = "ORG_PHONE_ID")
    private Long id;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "DIALING_CODE", nullable = false)
    private String dialingCode = "001";

    @Column(name = "EXTENSION")
    private String extension;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private OrganizationEntity organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID")
    private OrganizationLocationEntity location;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get area code.
     *
     * @return the area code.
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Set area code.
     *
     * @param areaCode the area code.
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Get phone number.
     *
     * @return the phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set phone number.
     *
     * @param phoneNumber the phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public OrganizationLocationEntity getLocation() {
        return location;
    }

    public void setLocation(OrganizationLocationEntity location) {
        this.location = location;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.areaCode).
                append(this.dialingCode).
                append(this.extension).
                append(this.phoneNumber).
                append(this.getOrganization()).
                append(this.getLocation()).
                append(this.getModification()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrganizationPhoneEntity other = (OrganizationPhoneEntity) obj;
        return new EqualsBuilder().append(this.areaCode, other.areaCode).
                append(this.dialingCode, other.dialingCode).
                append(this.extension, other.extension).
                append(this.phoneNumber, other.phoneNumber).
                append(this.getOrganization(), other.getOrganization()).
                append(this.getLocation(), other.getLocation()).
                append(this.getModification(), other.getModification()).isEquals();
    }
}
