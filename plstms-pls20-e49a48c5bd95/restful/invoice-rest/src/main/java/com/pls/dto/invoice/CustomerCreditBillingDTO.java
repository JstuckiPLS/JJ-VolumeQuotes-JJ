package com.pls.dto.invoice;

import java.math.BigDecimal;
import java.util.List;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.BillToDTO;

/**
 * DTO for customer's Credit and Billing information.
 *
 * @author Mikhail Boldinov, 21/01/14
 */
public class CustomerCreditBillingDTO {
    private String taxId;
    private BigDecimal creditLimit;
    private BigDecimal unpaid;
    private BigDecimal available;
    private String accExecName;
    private PhoneBO accExecPhone;
    private PhoneBO accExecFax;
    private String accExecEmail;
    private List<BillToDTO> billToList;

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(BigDecimal unpaid) {
        this.unpaid = unpaid;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public String getAccExecName() {
        return accExecName;
    }

    public void setAccExecName(String accExecName) {
        this.accExecName = accExecName;
    }

    public PhoneBO getAccExecPhone() {
        return accExecPhone;
    }

    public void setAccExecPhone(PhoneBO accExecPhone) {
        this.accExecPhone = accExecPhone;
    }

    public PhoneBO getAccExecFax() {
        return accExecFax;
    }

    public void setAccExecFax(PhoneBO accExecFax) {
        this.accExecFax = accExecFax;
    }

    public String getAccExecEmail() {
        return accExecEmail;
    }

    public void setAccExecEmail(String accExecEmail) {
        this.accExecEmail = accExecEmail;
    }

    public List<BillToDTO> getBillToList() {
        return billToList;
    }

    public void setBillToList(List<BillToDTO> billToList) {
        this.billToList = billToList;
    }
}
