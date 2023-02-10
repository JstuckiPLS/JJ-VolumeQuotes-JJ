package com.pls.core.domain.bo;

import com.pls.core.domain.organization.OrganizationFaxPhoneEntity;
import com.pls.core.domain.organization.OrganizationVoicePhoneEntity;

import java.math.BigDecimal;

/**
 * Customer Credit info.
 *
 * @author Mikhail Boldinov, 21/01/14
 */
public class CustomerCreditInfoBO {
    private String taxId;
    private BigDecimal creditLimit;
    private BigDecimal unpaid;
    private BigDecimal available;
    private String accExecName;
    private OrganizationVoicePhoneEntity accExecPhone;
    private OrganizationFaxPhoneEntity accExecFax;
    private String accExecEmail;

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Credit Limit setter.
     *
     * @param creditLimit credit limit
     */
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
        if (creditLimit != null && this.unpaid != null) {
            this.available = creditLimit.subtract(this.unpaid);
        }
    }

    public BigDecimal getUnpaid() {
        return unpaid;
    }

    /**
     * Unpaid amount setter.
     *
     * @param unpaid unpaid amount
     */
    public void setUnpaid(BigDecimal unpaid) {
        this.unpaid = unpaid;
        if (unpaid != null && this.creditLimit != null) {
            this.available = this.creditLimit.subtract(unpaid);
        }
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public String getAccExecName() {
        return accExecName;
    }

    public void setAccExecName(String accExecName) {
        this.accExecName = accExecName;
    }

    public OrganizationVoicePhoneEntity getAccExecPhone() {
        return accExecPhone;
    }

    public void setAccExecPhone(OrganizationVoicePhoneEntity accExecPhone) {
        this.accExecPhone = accExecPhone;
    }

    public OrganizationFaxPhoneEntity getAccExecFax() {
        return accExecFax;
    }

    public void setAccExecFax(OrganizationFaxPhoneEntity accExecFax) {
        this.accExecFax = accExecFax;
    }

    public String getAccExecEmail() {
        return accExecEmail;
    }

    public void setAccExecEmail(String accExecEmail) {
        this.accExecEmail = accExecEmail;
    }
}
