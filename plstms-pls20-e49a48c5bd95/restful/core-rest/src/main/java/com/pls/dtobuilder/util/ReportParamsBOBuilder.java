package com.pls.dtobuilder.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.pls.dto.enums.DateRange;
import com.pls.shipment.domain.bo.ReportParamsBO;
import com.pls.shipment.domain.enums.DateTypeOption;

/**
 * Builder for {@link ReportParamsBO}.
 * 
 * @author Yasaman Palumbo
 */
public class ReportParamsBOBuilder {

    private HttpServletRequest request;

    /**
     * Constructor.
     * 
     * @param request
     *            {@link HttpServletRequest}.
     */
    public ReportParamsBOBuilder(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Build {@link ReportParamsBO}.
     * 
     * @return {@link ReportParamsBO}
     */
    public ReportParamsBO build() {
        ReportParamsBO bo = new ReportParamsBO();
        bo.setReportName(getStringParameter("reportName"));
        bo.setCustomerId(getLongParameter("customerId"));
        bo.setCarrierId(getLongParameter("carrierId"));
        bo.setCarrierName(getStringParameter("carrierName"));
        bo.setCustomerName(getStringParameter("customerName"));
        bo.setBusinessUnitId(getLongParameter("businessUnitId"));
        bo.setBusinessUnitName(getStringParameter("businessUnitName"));
        bo.setCompanyCode(getStringParameter("companyCode"));
        bo.setCompanyCodeDescription(getStringParameter("companyCodeDescription"));
        bo.setStartDate(getStartDateParameter());
        bo.setEndDate(getEndDateParameter());
        bo.setInvoicedShipmentsOnly(getBooleanParameter("invoicedShipmentsOnly"));
        bo.setSortOrder(getStringParameter("sortOrder"));
        bo.setDateType(getDateTypeParameter());
        return bo;
    }

    private boolean getBooleanParameter(String parameterName) {
        String result = request.getParameter(parameterName);
        return isNotBlank(result) ? Boolean.parseBoolean(result) : false;
    }

    private String getStringParameter(String parameterName) {
        String result = request.getParameter(parameterName);
        return isNotBlank(result) ? result : EMPTY;
    }

    private Long getLongParameter(String parameterName) {
        String result = getStringParameter(parameterName);
        return isNotBlank(result) ? Long.parseLong(result) : null;
    }

    private Date getStartDateParameter() {
        String result = getStringParameter("startDate");
        return isNotBlank(result) ? DateUtils.getFromDate(DateRange.DEFAULT, result) : null;
    }

    private Date getEndDateParameter() {
        String result = getStringParameter("endDate");
        return isNotBlank(result) ? DateUtils.getToDate(DateRange.DEFAULT, result) : null;
    }

    private DateTypeOption getDateTypeParameter() {
        String result = getStringParameter("dateType");
        return isNotBlank(result) ? DateTypeOption.valueOf(result) : null;
    }
}
