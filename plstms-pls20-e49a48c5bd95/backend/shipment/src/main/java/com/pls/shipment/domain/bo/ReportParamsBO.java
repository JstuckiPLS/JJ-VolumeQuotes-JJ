package com.pls.shipment.domain.bo;

import java.util.Date;

import com.pls.shipment.domain.enums.DateTypeOption;

/**
 * Report parameters.
 *
 * @author Ashwini Neelgund
 */
public class ReportParamsBO {

    private String reportName;
    private Long customerId;
    private Long carrierId;
    private String customerName;
    private String carrierName;
    private Long businessUnitId;
    private String businessUnitName;
    private String companyCode;
    private String companyCodeDescription;
    private Date startDate;
    private Date endDate;
    private boolean invoicedShipmentsOnly;
    private String sortOrder;
    private DateTypeOption dateType;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Long getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(Long businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public String getBusinessUnitName() {
        return businessUnitName;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyCodeDescription() {
        return companyCodeDescription;
    }

    public void setCompanyCodeDescription(String companyCodeDescription) {
        this.companyCodeDescription = companyCodeDescription;
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

    public boolean isInvoicedShipmentsOnly() {
        return invoicedShipmentsOnly;
    }

    public void setInvoicedShipmentsOnly(boolean invoicedShipmentsOnly) {
        this.invoicedShipmentsOnly = invoicedShipmentsOnly;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public DateTypeOption getDateType() {
        return dateType;
    }

    public void setDateType(DateTypeOption dateType) {
        this.dateType = dateType;
    }


}
