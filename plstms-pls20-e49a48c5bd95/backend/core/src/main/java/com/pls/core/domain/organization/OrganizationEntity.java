package com.pls.core.domain.organization;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.OrganizationStatus;

/**
 * Base class for all organizations - Customers and Carriers.
 * Possible discriminatory values are SHIPPER for customer, CARRIER for carrier and so forth.
 *
 * <p>
 *     <b>Note:</b>ACCOUNT_EXECUTIVE - this column contains trash and should not be used. Account executives are stored in USER_CUSTOMER table.<br/>
 *     Each organization in PLS 2.0 has only one location, that needs to be created during organization creation.
 *     This is mandatory, otherwise may reports will fail.
 *
 * </p>
 *
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "ORGANIZATIONS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ORG_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
public class OrganizationEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = 2718077523206419893L;
    private static final String ORGANIZATION_COLUMN = "organization";

    public static final String Q_GET_PLS_PRO_ORGANIZATION = "com.pls.core.domain.organization.OrganizationEntity.Q_GET_PLS_PRO_ORGANIZATION";
    public static final String Q_FIND_CUSTOMERS_BY_VALUES = "com.pls.core.domain.organization.OrganizationEntity.Q_FIND_CUSTOMERS_BY_VALUES_QUERY";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_sequence")
    @SequenceGenerator(name = "org_sequence", sequenceName = "ORG_SEQ", allocationSize = 1)
    @Column(name = "ORG_ID")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.OrganizationStatus"),
            @Parameter(name = "identifierMethod", value = "getOrganizationStatus"),
            @Parameter(name = "valueOfMethod", value = "getOrganizationStatusBy")})
    private OrganizationStatus status;

    @Column(name = "STATUS_REASON")
    private String statusReason;

    @Column(name = "IS_CONTRACT")
    @Type(type = "yes_no")
    private Boolean contract;

    @Column(name = "EFF_DATE")
    private Date effectiveDate;

    @Column(name = "EXP_DATE")
    private Date expirationDate;

    @Column(name = "EMPLOYER_NUM")
    private String federalTaxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NETWORK_ID", insertable = false, updatable = false)
    private NetworkEntity network;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @Column(name = "CONTACT_FIRST_NAME")
    private String contactFirstName;

    @Column(name = "CONTACT_LAST_NAME")
    private String contactLastName;

    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;

    @Column(name = "LOGO_ID")
    private Long logoId;

    // lazy loading doesn't work on OneToOne mapping for optional entity
    @OneToOne(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private OrganizationVoicePhoneEntity phone;

    // lazy loading doesn't work on OneToOne mapping for optional entity
    @OneToOne(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private OrganizationFaxPhoneEntity fax;

    @Column(name = "CREDIT_LIMIT")
    private Long creditLimit;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @OneToMany(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrganizationLocationEntity> locations;

    @OneToMany(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "IS_DEFAULT='Y'")
    private Set<OrganizationLocationEntity> defaultLocations;

    @OneToMany(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BillToEntity> billTos;

    @OneToMany(mappedBy = ORGANIZATION_COLUMN, fetch = FetchType.LAZY)
    @Where(clause = "IS_DEFAULT='Y'")
    private Set<BillToEntity> defaultBillTo;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "org_id", name = "org_id")
    private Set<OrganizationPricingEntity> organizationsPricing;

    @Column
    private String scac;
    
    @Column(name = "ACT_SCAC")
    private String actualScac;

    @Column(name = "EDI_ACCOUNT")
    private String ediAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "COMPANY_CODE", insertable = false, updatable = false)
    private CompanyCodeEntity companyCodeEntity;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "ORG_TYPE", insertable = false, updatable = false)
    private String orgType;

    @Column(name = "AUTO_CREDIT_HOLD", insertable = false, updatable = false)
    @Type(type = "yes_no")
    private Boolean autoCreditHold;

    @Column(name = "OVERRIDE_CREDIT_HOLD", insertable = false, updatable = false)
    @Type(type = "yes_no")
    private Boolean overrideCreditHold;

    public CompanyCodeEntity getCompanyCodeEntity() {
        return companyCodeEntity;
    }

    public void setCompanyCodeEntity(CompanyCodeEntity companyCodeEntity) {
        this.companyCodeEntity = companyCodeEntity;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }
    
    public String getActualScac() {
        return actualScac != null ? actualScac : scac ;
    }

    public String setActualScac(String scac) {
        return actualScac = scac;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Get organization name.
     *
     * @return the organization name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set organization name.
     *
     * @param name the organization name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get organization status.
     *
     * @return organization status.
     */
    public OrganizationStatus getStatus() {
        return status;
    }

    /**
     * Set organization status {@link com.pls.core.domain.enums.OrganizationStatus}.
     *
     * @param status the organization status.
     */
    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    /**
     * Get reason of current status value.
     *
     * @return the statusReason
     */
    public String getStatusReason() {
        return statusReason;
    }

    /**
     * Set reason of current status value.
     *
     * @param statusReason
     *            the statusReason to set
     */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * Return if organization is a "Contract" organization.
     *
     * @return true if organization is a "Contract" organization.
     */
    public boolean isContract() {
        return contract != null && contract;
    }

    /**
     * Set if organization is a "Contract" organization.
     *
     * @param contract the value of organization "Contract" indication.
     */
    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Get federal tax id.
     *
     * @return the federal tax id.
     */
    public String getFederalTaxId() {
        return federalTaxId;
    }

    /**
     * Set federal tax id.
     *
     * @param federalTaxId the federal tax id.
     */
    public void setFederalTaxId(String federalTaxId) {
        this.federalTaxId = federalTaxId;
    }

    /**
     * Get BillTO addresses.
     *
     * @return the BillTO addresses.
     */
    public Set<BillToEntity> getBillToAddresses() {
        return getBillTos();
    }

    /**
     * Set BillTO addresses.
     *
     * @param billToAddresses the BillTO addresses.
     */
    public void setBillToAddresses(Set<BillToEntity> billToAddresses) {
        this.setBillTos(billToAddresses);
    }

    /**
     * Get Address.
     *
     * @return the address
     */
    public AddressEntity getAddress() {
        return address;
    }

    /**
     * Set Address.
     *
     * @param address the address to set
     */
    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    /**
     * Get Contact First Name.
     *
     * @return the contactFirstName
     */
    public String getContactFirstName() {
        return contactFirstName;
    }

    /**
     * Set Contact First Name.
     *
     * @param contactFirstName the contactFirstName to set
     */
    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    /**
     * Get Contact Last Name.
     *
     * @return the contactLastName
     */
    public String getContactLastName() {
        return contactLastName;
    }

    /**
     * Set Contact Last Name.
     *
     * @param contactLastName the contactLastName to set
     */
    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    /**
     * Get Contact Email.
     *
     * @return the contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Set Contact Email.
     *
     * @param contactEmail the contactEmail to set
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public OrganizationVoicePhoneEntity getPhone() {
        return phone;
    }

    /**
     * Set {@link OrganizationVoicePhoneEntity} relation.
     * 
     * @param phone {@link OrganizationVoicePhoneEntity} value.
     */
    public void setPhone(OrganizationVoicePhoneEntity phone) {
        if (phone != null) {
            // TODO is this set really needed? it must be set in DTO Builders
            phone.setOrganization(this);
        }
        this.phone = phone;
    }

    public OrganizationFaxPhoneEntity getFax() {
        return fax;
    }

    /**
     * Set {@link OrganizationFaxPhoneEntity} relation.
     * 
     * @param fax  {@link OrganizationFaxPhoneEntity} object.
     */
    public void setFax(OrganizationFaxPhoneEntity fax) {
        if (fax != null) {
            // TODO is this set really needed? it must be set in DTO Builders
            fax.setOrganization(this);
        }
        this.fax = fax;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public Set<OrganizationLocationEntity> getLocations() {
        return locations;
    }

    public Set<OrganizationLocationEntity> getDefaultLocations() {
        return defaultLocations;
    }

    /**
     * Set locations. For PLS 2.0 organization may have only one location.
     * 
     * @param locations
     *            set of locations.
     */
    public void setLocations(Set<OrganizationLocationEntity> locations) {
        this.locations = locations;
    }

    public void setDefaultLocations(Set<OrganizationLocationEntity> defaultLocations) {
        this.defaultLocations = defaultLocations;
    }

    public Set<BillToEntity> getBillTos() {
        return billTos;
    }

    public void setBillTos(Set<BillToEntity> billTos) {
        this.billTos = billTos;
    }

    public String getEdiAccount() {
        return ediAccount;
    }

    public void setEdiAccount(String ediAccount) {
        this.ediAccount = ediAccount;
    }

    public Set<BillToEntity> getDefaultBillTo() {
        return defaultBillTo;
    }

    public void setDefaultBillTo(Set<BillToEntity> defaultBillTo) {
        this.defaultBillTo = defaultBillTo;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Set<OrganizationPricingEntity> getOrganizationsPricing() {
        return organizationsPricing;
    }

    public void setOrganizationsPricing(Set<OrganizationPricingEntity> organizationsPricing) {
        this.organizationsPricing = organizationsPricing;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(getName()).
                append(getStatus()).
                append(getFederalTaxId()).
                append(getContactFirstName()).
                append(getContactLastName()).
                append(getContactEmail()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrganizationEntity) {
            final OrganizationEntity other = (OrganizationEntity) obj;
            return new EqualsBuilder().
                    append(getName(), other.getName()).
                    append(getStatus(), other.getStatus()).
                    append(getFederalTaxId(), other.getFederalTaxId()).
                    append(getContactFirstName(), other.getContactFirstName()).
                    append(getContactLastName(), other.getContactLastName()).
                    append(getContactEmail(), other.getContactEmail()).isEquals();
        } else {
            return false;
        }
    }

    public NetworkEntity getNetwork() {
        return network;
    }

    public void setNetwork(NetworkEntity network) {
        this.network = network;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getOrgType() {
        return orgType;
    }

    public Boolean getAutoCreditHold() {
        return autoCreditHold;
    }

    public void setAutoCreditHold(Boolean autoCreditHold) {
        this.autoCreditHold = autoCreditHold;
    }

    public Boolean getOverrideCreditHold() {
        return overrideCreditHold;
    }

    public void setOverrideCreditHold(Boolean overrideCreditHold) {
        this.overrideCreditHold = overrideCreditHold;
    }
}
