package com.pls.dtobuilder.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.exception.ApplicationException;
import com.pls.dto.enums.DateRange;
import com.pls.restful.util.ResourceParamsUtils;

/**
 * Builder for {@link RegularSearchQueryBO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class RegularSearchQueryBOBuilder {

    private HttpServletRequest request;

    /**
     * Constructor.
     * 
     * @param request
     *            {@link HttpServletRequest}.
     */
    public RegularSearchQueryBOBuilder(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Build {@link RegularSearchQueryBO}.
     * 
     * @return {@link RegularSearchQueryBO}
     * @throws ApplicationException
     *             if WildCard search parameter has wrong format
     */
    public RegularSearchQueryBO build() throws ApplicationException {
        RegularSearchQueryBO bo = new RegularSearchQueryBO();
        bo.setBol(getWildCardParameter("bol"));
        bo.setPro(getWildCardParameter("pro"));
        bo.setJob(getWildCardParameter("job"));
        bo.setCarrier(getLongParameter("carrier"));
        bo.setOriginZip(getStringParameter("origin"));
        bo.setDestinationZip(getStringParameter("destination"));
        bo.setFromDate(getFromDateParameter());
        bo.setToDate(getToDateParameter());
        bo.setLoadId(getLongParameter("loadId"));
        bo.setDateSearchField(getStringParameter("dateSearchField"));
        bo.setInvoiceNumber(getStringParameter("invoiceNumber"));
        bo.setCustomer(getLongParameter("customer"));
        bo.setAccountExecutive(getLongParameter("accountExecutiveId"));
        bo.setPo(getWildCardParameter("po"));
        return bo;
    }

    private String getStringParameter(String parameterName) {
        String result = request.getParameter(parameterName);
        return isNotBlank(result) ? result : null;
    }

    private String getWildCardParameter(String parameterName) throws ApplicationException {
        String result = getStringParameter(parameterName);
        return ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(result);
    }

    private Long getLongParameter(String parameterName) {
        String result = getStringParameter(parameterName);
        return isNotBlank(result) ? Long.parseLong(result) : null;
    }

    private Date getFromDateParameter() {
        String result = getStringParameter("fromDate");
        return isNotBlank(result) ? DateUtils.getFromDate(DateRange.DEFAULT, result) : null;
    }

    private Date getToDateParameter() {
        String result = getStringParameter("toDate");
        return isNotBlank(result) ? DateUtils.getToDate(DateRange.DEFAULT, result) : null;
    }
}
