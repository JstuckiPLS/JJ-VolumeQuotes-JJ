/**
 * 
 */
package com.pls.core.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.KPIDao;
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
import com.pls.core.service.KPIService;
import com.pls.core.service.file.XlsExporter;
import com.pls.core.service.impl.file.FreightSpendAnalysisExportReportHeader;
import com.pls.core.service.impl.file.GeographicSummaryExportReportHeader;
import com.pls.core.service.impl.file.KPICarrierSummaryExportHeader;
import com.pls.core.service.impl.file.KPIClassSummaryExportHeader;
import com.pls.core.service.impl.file.KPICustomerSummaryExportHeader;
import com.pls.core.service.impl.file.KPIDailyLoadActivityReportExportHeader;
import com.pls.core.service.impl.file.KPIDestinationReportExportHeader;
import com.pls.core.service.impl.file.KPISeasonalityExportHeader;
import com.pls.core.service.impl.file.KPIShipmentOverviewExportHeader;
import com.pls.core.service.impl.file.KPIVendorSummaryExportHeader;
import com.pls.core.service.impl.file.WeightAnalysisExportReportHeader;

/**
 * Report service impl.
 * 
 * @author Alexander Nalapko
 * 
 */
@Service
@Transactional
public class KPIServiceImpl implements KPIService {

    private static final String DAILY_LOAD_ACTIVITY_NAME = "KPI Daily Load Activity Report";
    private static final String FREIGHT_SPEND_ANALYSIS_EXPORT_NANE = "KPI Freight Spend Analysis Reports";

    private static final String DESTINATON_NAME = "KPI Destination Report";
    private static final String SHIPMENT_OVERVIEW = "KPI Shipment Overview Report";
    private static final String WEIGHT_ANALYSIS_EXPORT_NAME = "KPI Weight Analysis Report";
    private static final String GEOGRAPHIC_SUMMARY_EXPORT_NAME = "KPI Geographic Summary Report";
    private static final String CARRIER_SUMMARY = "KPI Carrier Summary Report";
    private static final String CLASS_SUMMARY = "KPI Class Summary Report";
    private static final String CUSTOMER_SUMMARY = "KPI Customer Summary Report";
    private static final String VENDOR_SUMMARY = "KPI Vendor Summary Report";
    private static final String SEASONALITY_SUMMARY = "KPI Seasonality Report";

    @Autowired
    private KPIDao dao;

    @Override
    public List<DestinationReportBO> getDestinationReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getDestinationReport(orgId, filters);
    }

    @Override
    public void exportDestinationReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<DestinationReportBO> entities = dao.getDestinationReport(orgId, filters);
        XlsExporter<DestinationReportBO> xlsExporter = new XlsExporter<DestinationReportBO>(
                KPIDestinationReportExportHeader.prepareColumns(), DESTINATON_NAME);
        xlsExporter.export(entities, outputStream);

    }

    @Override
    public List<DailyLoadActivityBO> getDailyLoadActivityReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getDailyLoadActivityReport(orgId, filters);
    }

    @Override
    public void exportDailyLoadActivityReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<DailyLoadActivityBO> entities = dao.getDailyLoadActivityReport(orgId, filters);
        XlsExporter<DailyLoadActivityBO> xlsExporter = new XlsExporter<DailyLoadActivityBO>(
                KPIDailyLoadActivityReportExportHeader.prepareColumns(), DAILY_LOAD_ACTIVITY_NAME);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public KPIReportFiltersBO getFilterValues(Long orgId) {
        return dao.getFilterValues(orgId);
    }

    @Override
    public List<FreightSpendAnalysisReportBO> getFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters) {
        return dao.getFreightSpendAnalysisReports(orgId, filters);
    }

    @Override
    public void exportShipmentOverviewReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<ShipmentOverviewReportBO> entities = dao.getShipmentOverviewReport(orgId, filters);
        XlsExporter<ShipmentOverviewReportBO> xlsExporter = new XlsExporter<ShipmentOverviewReportBO>(
                KPIShipmentOverviewExportHeader.prepareColumns(), SHIPMENT_OVERVIEW);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public List<ShipmentOverviewReportBO> getShipmentOverviewReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getShipmentOverviewReport(orgId, filters);
    }

    @Override
    public List<WeightAnalysisReportBO> getWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters) {
        return dao.getWeightAnalysisReports(orgId, filters);
    }

    @Override
    public void exportFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<FreightSpendAnalysisReportBO> reports = dao.getFreightSpendAnalysisReports(orgId, filters);
        XlsExporter<FreightSpendAnalysisReportBO> exporter = new XlsExporter<FreightSpendAnalysisReportBO>(
                FreightSpendAnalysisExportReportHeader.columnHeaders(), FREIGHT_SPEND_ANALYSIS_EXPORT_NANE);
        exporter.export(reports, outputStream);
    }

    @Override
    public void exportWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<WeightAnalysisReportBO> reports = dao.getWeightAnalysisReports(orgId, filters);
        XlsExporter<WeightAnalysisReportBO> exporter = new XlsExporter<WeightAnalysisReportBO>(
                WeightAnalysisExportReportHeader.columnHeaders(), WEIGHT_ANALYSIS_EXPORT_NAME);
        exporter.export(reports, outputStream);
    }

    @Override
    public List<GeographicSummaryReportBO> getGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters) {
        return dao.getGeographicSummaryReports(orgId, filters);
    }

    @Override
    public void exportGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<GeographicSummaryReportBO> reports = dao.getGeographicSummaryReports(orgId, filters);
        XlsExporter<GeographicSummaryReportBO> exporter = new XlsExporter<GeographicSummaryReportBO>(
                GeographicSummaryExportReportHeader.columnHeaders(), GEOGRAPHIC_SUMMARY_EXPORT_NAME);
        exporter.export(reports, outputStream);
    }


    @Override
    public List<CarrierSummaryReportBO> getCarrierSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getCarrierSummaryReport(orgId, filters);
    }

    @Override
    public void exportCarrierSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<CarrierSummaryReportBO> entities = dao.getCarrierSummaryReport(orgId, filters);
        XlsExporter<CarrierSummaryReportBO> xlsExporter = new XlsExporter<CarrierSummaryReportBO>(
                KPICarrierSummaryExportHeader.prepareColumns(), CARRIER_SUMMARY);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public List<ClassSummaryReportBO> getClassSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getClassSummaryReport(orgId, filters);
    }

    @Override
    public void exportClassSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<ClassSummaryReportBO> entities = dao.getClassSummaryReport(orgId, filters);
        XlsExporter<ClassSummaryReportBO> xlsExporter = new XlsExporter<ClassSummaryReportBO>(
                KPIClassSummaryExportHeader.prepareColumns(), CLASS_SUMMARY);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public List<CustomerSummaryReportBO> getCustomerSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getCustomerSummaryReport(orgId, filters);
    }

    @Override
    public void exportCustomerSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<CustomerSummaryReportBO> entities = dao.getCustomerSummaryReport(orgId, filters);
        XlsExporter<CustomerSummaryReportBO> xlsExporter = new XlsExporter<CustomerSummaryReportBO>(
                KPICustomerSummaryExportHeader.prepareColumns(), CUSTOMER_SUMMARY);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public List<VendorSummaryReportBO> getVendorSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getVendorSummaryReport(orgId, filters);
    }

    @Override
    public void exportVendorSummaryReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<VendorSummaryReportBO> entities = dao.getVendorSummaryReport(orgId, filters);
        XlsExporter<VendorSummaryReportBO> xlsExporter = new XlsExporter<VendorSummaryReportBO>(
                KPIVendorSummaryExportHeader.prepareColumns(), VENDOR_SUMMARY);
        xlsExporter.export(entities, outputStream);
    }

    @Override
    public List<SeasonalityReportBO> getSeasonalityReport(Long orgId, KPIReportFiltersBO filters) {
        return dao.getSeasonalityReport(orgId, filters);
    }

    @Override
    public void exportSeasonalityReport(Long orgId, KPIReportFiltersBO filters, OutputStream outputStream) throws IOException {
        List<SeasonalityReportBO> entities = dao.getSeasonalityReport(orgId, filters);
        XlsExporter<SeasonalityReportBO> xlsExporter = new XlsExporter<SeasonalityReportBO>(
                KPISeasonalityExportHeader.prepareColumns(), SEASONALITY_SUMMARY);
        xlsExporter.export(entities, outputStream);
    }
}
