package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity object for the API Address criteria.
 *
 * @author Pavani Challa
 */
@Entity
@Table(name = "LTL_PRICING_API_ADDRESS_CRIT")
public class LtlPricingApiAddressCritEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1542701514194626625L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pricing_api_addr_crit_seq")
    @SequenceGenerator(name = "ltl_pricing_api_addr_crit_seq", sequenceName = "ltl_pricing_api_addr_crit_seq", allocationSize = 1)
    @Column(name = "LTL_PRICING_API_ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE_CODE")
    private String stateCode;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }
}
