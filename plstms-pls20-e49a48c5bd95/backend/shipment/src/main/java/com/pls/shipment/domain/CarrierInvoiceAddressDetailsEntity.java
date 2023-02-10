package com.pls.shipment.domain;

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
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.address.ZipCodePK;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.shipment.domain.enums.CarrierInvoiceAddressType;

/**
 * Carrier Invoice Address Details entity.
 *
 * @author Mikhail Boldinov, 02/10/13
 */
@Entity
@Table(name = "CARRIER_INVOICE_ADDR_DETAILS")
public class CarrierInvoiceAddressDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 3727866935538626393L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrier_invoice_address_details_sequence")
    @SequenceGenerator(name = "carrier_invoice_address_details_sequence", sequenceName = "CARR_INV_ADDR_DET_SEQ", allocationSize = 1)
    @Column(name = "ADDR_DET_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_DET_ID", nullable = false)
    private CarrierInvoiceDetailsEntity carrierInvoiceDetails;

    @Column(name = "ADDRESS_TYPE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.shipment.domain.enums.CarrierInvoiceAddressType"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private CarrierInvoiceAddressType addressType;

    @Column(name = "ADDRESS_NAME")
    private String addressName;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "POSTAL_CODE", referencedColumnName = "ZIP_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "STATE", referencedColumnName = "STATE_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE", insertable = false, updatable = false),
        @JoinColumn(name = "CITY", referencedColumnName = "CITY", insertable = false, updatable = false)
    })
    private ZipCodeEntity zipCode;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CarrierInvoiceDetailsEntity getCarrierInvoiceDetails() {
        return carrierInvoiceDetails;
    }

    public void setCarrierInvoiceDetails(CarrierInvoiceDetailsEntity carrierInvoiceDetails) {
        this.carrierInvoiceDetails = carrierInvoiceDetails;
    }

    public CarrierInvoiceAddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(CarrierInvoiceAddressType addressType) {
        this.addressType = addressType;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public ZipCodeEntity getZipCode() {
        return zipCode == null ? fillZipCode() : zipCode;
    }

    private ZipCodeEntity fillZipCode() {
        ZipCodeEntity zipCode = new ZipCodeEntity();
        zipCode.setCity(city);
        zipCode.setStateCode(state);
        zipCode.setZipCode(postalCode);
        zipCode.setId(new ZipCodePK());
        zipCode.getId().setCountry(new CountryEntity());
        zipCode.getId().getCountry().setId(countryCode);
        return zipCode;
    }

    public void setZipCode(ZipCodeEntity zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
