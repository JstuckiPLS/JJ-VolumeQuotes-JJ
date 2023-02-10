package com.pls.core.domain.address;


import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.shared.Status;

/**
 * Physical Address.
 *
 * @author Viacheslav Krot
 */
@Entity
@Table(name = "ADDRESSES")
public class AddressEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -5512109329978187942L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_sequence")
    @SequenceGenerator(name = "address_sequence", sequenceName = "ADDRESSES_SEQ", allocationSize = 1)
    @Column(name = "ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();
    @Column(name = "POSTAL_CODE", nullable = false)
    private String zip;

    @Column(name = "STATE_CODE")
    private String stateCode;

    @Column(name = "COUNTRY_CODE", updatable = false, insertable = false)
    private String countryCode;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "COUNTRY_CODE", insertable = false, updatable = false),
            @JoinColumn(name = "STATE_CODE", insertable = false, updatable = false)})
    private StateEntity state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_CODE")
    private CountryEntity country;

    @Column(name = "LATITUDE")
    private BigDecimal latitude;

    @Column(name = "LONGITUDE")
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumns({
        @JoinColumn(name = "POSTAL_CODE", referencedColumnName = "ZIP_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "STATE_CODE", referencedColumnName = "STATE_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "CITY", referencedColumnName = "CITY", insertable = false, updatable = false)
    })
    private ZipCodeEntity zipCode;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Get address1.
     *
     * @return address1.
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Set address 1.
     *
     * @param address1 address1 value.
     */
    public void setAddress1(String address1) {
        this.address1 = StringUtils.upperCase(address1);
    }

    /**
     * Get address2.
     *
     * @return address2.
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Set address2.
     *
     * @param address2 address2 value.
     */
    public void setAddress2(String address2) {
        this.address2 = StringUtils.upperCase(address2);
    }

    /**
     * Get city.
     *
     * @return city value.
     */
    public String getCity() {
        return city;
    }

    /**
     * Set city.
     *
     * @param city city value.
     */
    public void setCity(String city) {
        this.city = StringUtils.upperCase(city);
    }

    /**
     * Get zip.
     *
     * @return zip value.
     */
    public String getZip() {
        return zip;
    }

    /**
     * Set zip.
     *
     * @param zip zip value.
     */
    public void setZip(String zip) {
        this.zip = StringUtils.upperCase(zip);
    }

    /**
     * Get state code.
     *
     * @return state code value.
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Set state code.
     *
     * @param stateCode state code value.
     */
    public void setStateCode(String stateCode) {
        this.stateCode = StringUtils.upperCase(stateCode);
    }

    /**
     * Get status.
     *
     * @return status value.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set status.
     *
     * @param status status value
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get state.
     *
     * @return state value.
     */
    public StateEntity getState() {
        return state;
    }

    /**
     * Set state.
     *
     * @param state state value.
     */
    public void setState(StateEntity state) {
        this.state = state;
        if (state != null && state.getStatePK() != null) {
            stateCode = state.getStatePK().getStateCode();
        } else {
            stateCode = null;
        }
    }

    /**
     * Get country.
     *
     * @return country value.
     */
    public CountryEntity getCountry() {
        return country;
    }

    /**
     * Set country.
     *
     * @param country value.
     */
    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    /** Set the Country Code for this address. */
    public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof AddressEntity)) {
            return false;
        }

        AddressEntity obj = (AddressEntity) other;

        EqualsBuilder equalsBuilder = new EqualsBuilder();
        if (id != null && obj.id != null) {
            equalsBuilder.append(id, obj.id);
        } else {
            equalsBuilder.append(this.address1, obj.getAddress1())
                    .append(this.address2, obj.getAddress2())
                    .append(this.latitude, obj.getLatitude())
                    .append(this.longitude, obj.getLongitude())
                    .append(this.city, obj.getCity())
                    .append(this.zip, obj.getZip())
                    .append(this.status, obj.getStatus())
                    .append(this.country, obj.getCountry())
                    .append(this.stateCode, obj.getStateCode());
        }
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        if (id != null) {
            hashCodeBuilder.append(id);
        } else {
            hashCodeBuilder.append(this.address1)
                    .append(this.address2)
                    .append(this.latitude)
                    .append(this.longitude)
                    .append(this.city)
                    .append(this.zip)
                    .append(this.stateCode)
                    .append(this.status);
        }
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public ZipCodeEntity getZipCode() {
        return zipCode == null ? fillZipCode() : zipCode;
    }

    private ZipCodeEntity fillZipCode() {
        ZipCodeEntity zipCode = new ZipCodeEntity();
        zipCode.setCity(city);
        zipCode.setStateCode(stateCode);
        zipCode.setZipCode(zip);
        zipCode.setId(new ZipCodePK());
        zipCode.getId().setCountry(country);
        return zipCode;
    }

    public void setZipCode(ZipCodeEntity zipCode) {
        this.zipCode = zipCode;
    }
}
