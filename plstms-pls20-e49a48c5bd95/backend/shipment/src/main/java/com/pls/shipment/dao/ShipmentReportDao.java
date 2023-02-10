package com.pls.shipment.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.bo.LostSavingsReportBO;
import com.pls.core.domain.bo.ReportsBO;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.enums.DateTypeOption;

/**
 * DAO for reports.
 * 
 * @author Brichak Aleksandr
 */

public interface ShipmentReportDao {

    /**
     * Get freight analysis Unbilled report.
     * 
     * @param customerId - id of customer.
     * @param companyCode the code of company.
     * @param endDate the end of the date.
     * @return reports data BO.
     */
    List<ReportsBO> getUnbilledReport(Long customerId, String companyCode, Date endDate);

    /**
     * Get freight analysis Activity report.
     * 
     * @param customerId
     *            customer id.
     * @param networkId network id.
     * @param startDate
     *            the start of the date.
     * @param endDate
     *            the end of the date.
     * @param dateType
     *            {@link DateTypeOption}
     * 
     * @return list of the {@link FreightAnalysisReportBO}
     */
    List<FreightAnalysisReportBO> getActivityReport(Long customerId, Long networkId, Date startDate, Date endDate, DateTypeOption dateType);
    
    List<FreightAnalysisReportBO> getCarrierActivityReport(Long carrierId, Long customerId, Date startDate, Date endDate, DateTypeOption dateType);

    /**
     * Get freight analysis Savings report.
     * @param customerId customer id.
     * @param networkId network id.
     * @param startDate the start of the date.
     * @param endDate the end of the date.
     * @return list of the {@link FreightAnalysisReportBO}
     */
    List<FreightAnalysisReportBO> getSavingsReport(Long customerId, Long networkId, Date startDate, Date endDate);

    /**
     * Method returns Lost savings opportunity report data.
     * @param customerId - id of customer.
     * @param networkId - id of network.
     * @param startDate - start date of the report from when the data is pulled and analyzed.
     * @param endDate - end date of the report till when the data is pulled and analyzed.
     * @param sortOrder - sort order of the generated report
     * @return lost savings opportunity reports data BO.
     * @throws ParseException - date parse exception
     */
    List<LostSavingsReportBO> getLostSavingsReport(Long customerId, Long networkId, Date startDate, Date endDate, String sortOrder)
            throws ParseException;

    /**
     * Get shipment creation report.
     * @param customerId customer id.
     * @param networkId network id.
     * @param startDate the start of the date.
     * @param endDate the end of the date.
     * @param invoicedShipmentsOnly - true if only invoiced shipments are to be extracted.
     * @return list of the {@link CreationReportBO}
     */
    List<CreationReportBO> getCreationReport(Long customerId, Long networkId, Date startDate, Date endDate, boolean invoicedShipmentsOnly);
}