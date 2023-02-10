package com.pls.dto.address;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.pls.EdiSettingsDTO;
import com.pls.core.domain.enums.Currency;
import com.pls.dto.BillToRequiredFieldDTO;

/**
 * DTO for Bill To.
 *
 * @author Mikhail Boldinov, 30/05/13
 */
@XmlRootElement
public class BillToDTO {

    private Long id;

    private AddressBookEntryDTO address;

    private InvoicePreferencesDTO invoicePreferences;

    private boolean defaultNode;

    private List<BillToRequiredFieldDTO> billToRequiredFields;

    private boolean auditPrefReq;

    private Boolean emailAccountExecutive;

    private String auditInstructions;

    private Currency currency = Currency.USD;

    private Long payTermsId;

    private EdiSettingsDTO ediSettings;

    private BigDecimal creditLimit;

    private BigDecimal unpaidAmount;

    private BigDecimal availableAmount;

    private Boolean creditHold;

    private Boolean overrideCreditHold;

    private Boolean autoCreditHold;

    private Boolean customerAutoCreditHold;

    private Boolean networkAutoCreditHold;

    private Boolean customerOverrideCreditHold;

    private BillToThresholdSettingsDTO billToThresholdSettings;

    private BillToDefaultValuesDTO billToDefaultValues;

    private String paymentMethod;

    private String creditCardEmail;

    private Boolean sendInvoicesReports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AddressBookEntryDTO getAddress() {
        return address;
    }

    public void setAddress(AddressBookEntryDTO address) {
        this.address = address;
    }

    public InvoicePreferencesDTO getInvoicePreferences() {
        return invoicePreferences;
    }

    public void setInvoicePreferences(InvoicePreferencesDTO invoicePreferences) {
        this.invoicePreferences = invoicePreferences;
    }

    public boolean isDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(boolean defaultNode) {
        this.defaultNode = defaultNode;
    }

    public List<BillToRequiredFieldDTO> getBillToRequiredFields() {
        return billToRequiredFields;
    }

    public void setBillToRequiredFields(List<BillToRequiredFieldDTO> billToRequiredFields) {
        this.billToRequiredFields = billToRequiredFields;
    }

    public boolean isAuditPrefReq() {
        return auditPrefReq;
    }

    public void setAuditPrefReq(boolean auditPrefReq) {
        this.auditPrefReq = auditPrefReq;
    }

    public Boolean isEmailAccountExecutive() {
        return emailAccountExecutive;
    }

    public void setEmailAccountExecutive(Boolean emailAccountExecutive) {
        this.emailAccountExecutive = emailAccountExecutive;
    }

    public String getAuditInstructions() {
        return auditInstructions;
    }

    public void setAuditInstructions(String auditInstructions) {
        this.auditInstructions = auditInstructions;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getPayTermsId() {
        return payTermsId;
    }

    public void setPayTermsId(Long payTermsId) {
        this.payTermsId = payTermsId;
    }

    public EdiSettingsDTO getEdiSettings() {
        return ediSettings;
    }

    public void setEdiSettings(EdiSettingsDTO ediSettings) {
        this.ediSettings = ediSettings;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getUnpaidAmount() {
        return unpaidAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public Boolean getCreditHold() {
        return creditHold;
    }

    public Boolean getOverrideCreditHold() {
        return overrideCreditHold;
    }

    public Boolean getAutoCreditHold() {
        return autoCreditHold;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setCreditHold(Boolean creditHold) {
        this.creditHold = creditHold;
    }

    public void setOverrideCreditHold(Boolean overrideCreditHold) {
        this.overrideCreditHold = overrideCreditHold;
    }

    public void setAutoCreditHold(Boolean autoCreditHold) {
        this.autoCreditHold = autoCreditHold;
    }

    public Boolean getCustomerAutoCreditHold() {
        return customerAutoCreditHold;
    }

    public void setCustomerAutoCreditHold(Boolean customerAutoCreditHold) {
        this.customerAutoCreditHold = customerAutoCreditHold;
    }

    public Boolean getNetworkAutoCreditHold() {
        return networkAutoCreditHold;
    }

    public void setNetworkAutoCreditHold(Boolean networkAutoCreditHold) {
        this.networkAutoCreditHold = networkAutoCreditHold;
    }

    public Boolean getCustomerOverrideCreditHold() {
        return customerOverrideCreditHold;
    }

    public void setCustomerOverrideCreditHold(Boolean customerOverrideCreditHold) {
        this.customerOverrideCreditHold = customerOverrideCreditHold;
    }

    public BillToThresholdSettingsDTO getBillToThresholdSettings() {
        return billToThresholdSettings;
    }

    public void setBillToThresholdSettings(BillToThresholdSettingsDTO billToThresholdSettings) {
        this.billToThresholdSettings = billToThresholdSettings;
    }

    public BillToDefaultValuesDTO getBillToDefaultValues() {
        return billToDefaultValues;
    }

    public void setBillToDefaultValues(BillToDefaultValuesDTO billToDefaultValues) {
        this.billToDefaultValues = billToDefaultValues;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCreditCardEmail() {
        return creditCardEmail;
    }

    public void setCreditCardEmail(String creditCardEmail) {
        this.creditCardEmail = creditCardEmail;
    }

    public Boolean isSendInvoicesReports() {
        return sendInvoicesReports;
    }

    public void setSendInvoicesReports(Boolean sendInvoicesReports) {
        this.sendInvoicesReports = sendInvoicesReports;
    }
}
