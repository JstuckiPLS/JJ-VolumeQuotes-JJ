package com.pls.shipment.service.xls;

import org.springframework.util.StringUtils;

import com.pls.shipment.domain.bo.ReportParamsBO;

public class ReportUtil {

    static String getSubjectType(ReportParamsBO reportParams) {
        if (reportParams.getCarrierId() != null)
            return "Carrier";
        if (reportParams.getCustomerId() != null)
            return "Customer";

        return StringUtils.isEmpty(reportParams.getCompanyCode()) ? "Business Unit" : "Company Code";
    }

    static String getSubjectName(ReportParamsBO reportParams) {
        if (reportParams.getCarrierId() != null)
            return reportParams.getCarrierName();
        if (reportParams.getCustomerId() != null)
            return reportParams.getCustomerName();

        return StringUtils.isEmpty(reportParams.getCompanyCode()) ? reportParams.getBusinessUnitName()
                : reportParams.getCompanyCodeDescription();
    }
}
