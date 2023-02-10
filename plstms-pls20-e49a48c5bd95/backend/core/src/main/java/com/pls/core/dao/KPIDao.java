/**
 * 
 */
package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.dashboard.CarrierSummaryReportBO;
import com.pls.core.domain.bo.dashboard.ClassSummaryReportBO;
import com.pls.core.domain.bo.dashboard.CustomerSummaryReportBO;
import com.pls.core.domain.bo.dashboard.DailyLoadActivityBO;
import com.pls.core.domain.bo.dashboard.DestinationReportBO;
import com.pls.core.domain.bo.dashboard.FreightSpendAnalysisReportBO;
import com.pls.core.domain.bo.dashboard.GeographicSummaryReportBO;
import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;
import com.pls.core.domain.bo.dashboard.SeasonalityReportBO;
import com.pls.core.domain.bo.dashboard.ShipmentOverviewReportBO;
import com.pls.core.domain.bo.dashboard.VendorSummaryReportBO;
import com.pls.core.domain.bo.dashboard.WeightAnalysisReportBO;

/**
 * Report DAO.
 * 
 * @author Alexander Nalapko
 * 
 */
public interface KPIDao {

    /**
     * Destination Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<DestinationReportBO>
     */
    List<DestinationReportBO> getDestinationReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * Daily Load Activity Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<DailyLoadActivityBO>
     */
    List<DailyLoadActivityBO> getDailyLoadActivityReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * Get filters list for Reports.
     * 
     * @param orgId
     *            organization ID
     * @return KPIReportFiltersBO
     */
    KPIReportFiltersBO getFilterValues(Long orgId);

    /**
     * Retrieves freight reports.
     * @param orgId -
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return - list of freight reports
     */
    List<FreightSpendAnalysisReportBO> getFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters);

    /**
     * Retrieves weight analysis reports.
     * 
     * @param orgId -
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return - list of weight reports
     */
    List<WeightAnalysisReportBO> getWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters);

    /**
     * Shipment Overview Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<ShipmentOverviewReportBO>
     */
    List<ShipmentOverviewReportBO> getShipmentOverviewReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * 
     * Retrieves geographical summary reports.
     * @param orgId -
     *            organization ID
     * @param filters filters
     * @return - list of freight reports
     */
    List<GeographicSummaryReportBO> getGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters);

    /**
     * Carrier Summary Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<CarrierSummaryReportBO>
     */
    List<CarrierSummaryReportBO> getCarrierSummaryReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * Class Summary Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<ClassSummaryReportBO>
     */
    List<ClassSummaryReportBO> getClassSummaryReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * Customer Summary Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<CustomerSummaryReportBO>
     */
    List<CustomerSummaryReportBO> getCustomerSummaryReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * Vendor Summary Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<VendorSummaryReportBO>
     */
    List<VendorSummaryReportBO> getVendorSummaryReport(Long orgId, KPIReportFiltersBO filters);

     /**
      * Seasonality Report.
      * 
      * @param orgId
      *            organization ID
      * @param filters
      *            KPIReportFiltersBO
      * @return List<SeasonalityReportBO>
      */
    List<SeasonalityReportBO> getSeasonalityReport(Long orgId, KPIReportFiltersBO filters);
}