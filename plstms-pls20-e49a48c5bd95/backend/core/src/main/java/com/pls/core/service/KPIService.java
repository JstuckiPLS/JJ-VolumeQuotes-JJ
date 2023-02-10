/**
 * 
 */
package com.pls.core.service;

import java.io.IOException;
import java.io.OutputStream;
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
 * Report service.
 * 
 * @author Alexander Nalapko
 * 
 */
public interface KPIService {

    /**
     * Destination Report.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @return List<DRBeen>
     */
    List<DestinationReportBO> getDestinationReport(Long orgId, KPIReportFiltersBO filters);

    /**
     * This method calls getDestinationReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportDestinationReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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
     * This method calls getDailyLoadActivityReport() method and then uses those values and exports the same
     * to excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportDailyLoadActivityReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

    /**
     * Get filters list for Reports.
     * 
     * @param orgId
     *            organization ID
     * @return KPIReportFiltersBO
     */
    KPIReportFiltersBO getFilterValues(Long orgId);

    /**
     * Fetches customer's freight report.
     * 
     * @param orgId
     *            - organization ID
     * @param filters
     *            - report filters
     * @return - requested freight reports
     */
    List<FreightSpendAnalysisReportBO> getFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters);

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
     * This method calls getShipmentOverviewReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportShipmentOverviewReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

    /**
     * Fetches customer's weight report.
     * 
     * @param orgId
     *            -  organization ID
     * @param filters
     *            - report filters
     * @return - requested weight reports
     */
    List<WeightAnalysisReportBO> getWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters);

    /**
     * Retrieves binary data for freight spend reports.
     * 
     * @param orgId
     *            -  organization ID
     * @param filters
     *            - report filters
     * @param outputStream
     *            to write - binary data to be imported to excel
     * @throws IOException
     *             - export exception
     */
    void exportFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

    /**
     * Retrieves binary data for weight analysis reports.
     * 
     * @param orgId
     *            -  organization ID
     * @param filters
     *            - report filters
     * @param outputStream
     *            to write - binary data to be imported to excel
     * @throws IOException
     *             - export exception
     */
    void exportWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

    /**
     * Fetches customer's geographical summary report.
     * 
     * @param orgId
     *            -  organization ID
     * @param filters
     *            - filters to be applied while fetch reports
     * @return - list of geographical summary reports
     */
    List<GeographicSummaryReportBO> getGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters);

    /**
     * Retrieves binary data for geographical summary reports.
     * 
     * @param orgId
     *            -  organization ID
     * @param filters
     *            - report filters
     * @param outputStream
     *            to write - binary data to be imported to excel
     * @throws IOException
     *             - export exception
     */
    void exportGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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
     * This method calls getCarrierSummaryReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportCarrierSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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
     * This method calls getClassSummaryReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportClassSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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
     * This method calls getCustomerSummaryReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportCustomerSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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
     * This method calls getVendorSummaryReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportVendorSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;

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

    /**
     * This method calls exportSeasonalityReport() method and then uses those values and exports the same to
     * excel sheet.
     * 
     * @param orgId
     *            organization ID
     * @param filters
     *            KPIReportFiltersBO
     * @param outputStream
     *            to write body of excel document
     * @throws IOException
     *             export exception
     */
    void exportSeasonalityReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream)
            throws IOException;
}
