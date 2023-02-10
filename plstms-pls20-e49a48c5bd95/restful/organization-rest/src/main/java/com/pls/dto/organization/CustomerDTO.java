package com.pls.dto.organization;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.enums.CustomerStatusReason;


/**
 * Customer DTO.
 *
 * @author Denis Zhupinsky
 */
@XmlRootElement
public class CustomerDTO {

    private Long id;

    private String name;

    private String ediAccount;

    private CustomerStatus status;

    private Date startDate;

    private Date endDate;

    private boolean contract;

    private String federalTaxId;

    private String contactFirstName;

    private String contactLastName;

    private String contactEmail;

    private AddressBookEntryDTO address;

    private PhoneBO contactPhone;

    private PhoneBO contactFax;

    private BillToDTO billTo;

    private Long accountExecutive;

    private String accountExecutiveName;

    private Date accountExecutiveStartDate;

    private Date accountExecutiveEndDate;

    private CustomerStatusReason statusReason;

    private Long creditLimit;

    private Integer version;

    private Boolean createOrdersFromVendorBills;

    private Boolean generateConsigneeInvoice;

    private Long logoId;

    private Boolean displayLogoOnBol;

    private Boolean displayLogoOnShipLabel;

    private String location;

    private Long locationId;

    private String network;

    private Long networkId;

    private String companyCode;

    private String unitCode;

    private String description;

    private String costCenterCode;

    private String internalNote;

    private Boolean printBarcode;

    /**
     * Get id.
     *
     * @return id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id the id value.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get customer name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set- customer name.
     *
     * @param name the customer name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get EDI account.
     *
     * @return the EDI account.
     */
    public String getEdiAccount() {
        return ediAccount;
    }

    /**
     * Set EDI account.
     *
     * @param ediAccount the EDI account.
     */
    public void setEdiAccount(String ediAccount) {
        this.ediAccount = ediAccount;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    /**
     * Return if organization is a "Contract" organization.
     *
     * @return true if organization is a "Contract" organization.
     */
    public boolean isContract() {
        return contract;
    }

    /**
     * Set if organization is a "Contract" organization.
     *
     * @param contract the value of organization "Contract" indication.
     */
    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * Get federal tax id.
     *
     * @return federal tax id.
     */
    public String getFederalTaxId() {
        return federalTaxId;
    }

    /**
     * Set federal tax id.
     * @param federalTaxId the federal tax id.
     */
    public void setFederalTaxId(String federalTaxId) {
        this.federalTaxId = federalTaxId;
    }


    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public AddressBookEntryDTO getAddress() {
        return address;
    }

    public void setAddress(AddressBookEntryDTO address) {
        this.address = address;
    }

    public PhoneBO getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(PhoneBO contactPhone) {
        this.contactPhone = contactPhone;
    }

    public PhoneBO getContactFax() {
        return contactFax;
    }

    public void setContactFax(PhoneBO contactFax) {
        this.contactFax = contactFax;
    }

    public BillToDTO getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToDTO billTo) {
        this.billTo = billTo;
    }

    public Long getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(Long accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public String getAccountExecutiveName() {
        return accountExecutiveName;
    }

    public void setAccountExecutiveName(String accountExecutiveName) {
        this.accountExecutiveName = accountExecutiveName;
    }

    public Date getAccountExecutiveStartDate() {
        return accountExecutiveStartDate;
    }

    public void setAccountExecutiveStartDate(Date accountExecutiveStartDate) {
        this.accountExecutiveStartDate = accountExecutiveStartDate;
    }

    public Date getAccountExecutiveEndDate() {
        return accountExecutiveEndDate;
    }

    public void setAccountExecutiveEndDate(Date accountExecutiveEndDate) {
        this.accountExecutiveEndDate = accountExecutiveEndDate;
    }

    public CustomerStatusReason getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(CustomerStatusReason statusReason) {
        this.statusReason = statusReason;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    /**
     * Get version.
     * 
     * @return version field.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Set version.
     * 
     * @param version
     *            field.
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getCreateOrdersFromVendorBills() {
        return createOrdersFromVendorBills;
    }

    public void setCreateOrdersFromVendorBills(Boolean createOrdersFromVendorBills) {
        this.createOrdersFromVendorBills = createOrdersFromVendorBills;
    }

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public Boolean getDisplayLogoOnBol() {
        return displayLogoOnBol;
    }

    public void setDisplayLogoOnBol(Boolean displayLogoOnBol) {
        this.displayLogoOnBol = displayLogoOnBol;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * Customer status.
     */
    public enum CustomerStatus {
        ACTIVE, INACTIVE, HOLD
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public Boolean getDisplayLogoOnShipLabel() {
        return displayLogoOnShipLabel;
    }

    public void setDisplayLogoOnShipLabel(Boolean displayLogoOnShipLabel) {
        this.displayLogoOnShipLabel = displayLogoOnShipLabel;
    }

    public Boolean isGenerateConsigneeInvoice() {
        return generateConsigneeInvoice;
    }

    public void setGenerateConsigneeInvoice(Boolean generateConsigneeInvoice) {
        this.generateConsigneeInvoice = generateConsigneeInvoice;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public Boolean isPrintBarcode() {
        return printBarcode;
    }

    public void setPrintBarcode(Boolean printBarcode) {
        this.printBarcode = printBarcode;
    }

}