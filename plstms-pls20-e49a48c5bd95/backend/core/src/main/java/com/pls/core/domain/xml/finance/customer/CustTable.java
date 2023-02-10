package com.pls.core.domain.xml.finance.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;

/**
 * CustTable JAXB-oriented object.
 * 
 * @author Alexander Nalapko
 */
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustTable implements IntegrationMessageBO {

    private static final long serialVersionUID = 2240747302620693341L;

    @XmlTransient
    private Long customerOrgId;

    @XmlTransient
    private Long personId;

    @XmlElement(name = "Operation")
    private String operation;

    @XmlElement(name = "AccountNumber")
    private String accountNumber;

    @XmlElement(name = "Currency")
    private String currency;

    @XmlElement(name = "CustomerGroup")
    private String customerGroup;

    @XmlElement(name = "BusinessUnit")
    private String businessUnit;

    @XmlElement(name = "CostCenter")
    private String costCenter;

    @XmlElement(name = "Department")
    private String department;

    @XmlElement(name = "IdentificationNumber")
    private String identificationNumber;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Address")
    private BillToAddress billToAddress;

    public BillToAddress getAddress() {
        return billToAddress;
    }

    public void setAddress(BillToAddress billToAddress) {
        this.billToAddress = billToAddress;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getScac() {
        return null;
    }

    /**
     * Scac value.
     * 
     * @param scac
     *            scac val
     */
    public void setScac(String scac) {
        // set scac val
    }

    public String getShipmentNo() {
        return null;
    }

    /**
     * shipmentNo value.
     * 
     * @param shipmentNo
     *            shipmentNo val
     */
    public void setShipmentNo(String shipmentNo) {
        // set shipmentNo val
    }

    public Long getLoadId() {
        return null;
    }

    /**
     * loadId value.
     * 
     * @param loadId
     *            loadId val
     */
    public void setLoadId(Long loadId) {
        // set load id
    }

    public String getBol() {
        return null;
    }

    /**
     * bol value.
     * 
     * @param bol
     *            bol val
     */
    public void setBol(String bol) {
        // set bol val
    }

    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;

    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getPersonId()).append(getScac()).append(getShipmentNo()).append(getLoadId())
                .append(getBol()).append(getCustomerOrgId()).append(getOperation()).append(getAccountNumber()).append(getCurrency())
                .append(getCustomerGroup()).append(getIdentificationNumber()).append(getBusinessUnit()).append(getCostCenter())
                .append(getDepartment()).append(getName());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof CustTable) {
            if (obj == this) {
                result = true;
            } else {
                CustTable other = (CustTable) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getPersonId(), other.getPersonId()).append(getScac(), other.getScac())
                        .append(getShipmentNo(), other.getShipmentNo()).append(getLoadId(), other.getLoadId()).append(getBol(), other.getBol())
                        .append(getCustomerOrgId(), other.getCustomerOrgId()).append(getOperation(), other.getOperation())
                        .append(getAccountNumber(), other.getAccountNumber()).append(getCurrency(), other.getCurrency())
                        .append(getCustomerGroup(), other.getCustomerGroup()).append(getIdentificationNumber(), other.getIdentificationNumber())
                        .append(getBusinessUnit(), other.getBusinessUnit()).append(getCostCenter(), other.getCostCenter())
                        .append(getDepartment(), other.getDepartment()).append(getName(), other.getName());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String getMessageType() {
        return EDIMessageType.CUSTOMER.getCode();
    }

}
