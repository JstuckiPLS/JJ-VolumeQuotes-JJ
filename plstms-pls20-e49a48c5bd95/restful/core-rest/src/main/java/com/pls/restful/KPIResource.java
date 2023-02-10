package com.pls.restful;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.bo.dashboard.CarrierSummaryReportBO;
import com.pls.core.domain.bo.dashboard.ClassSummaryReportBO;
import com.pls.core.domain.bo.dashboard.CustomerSummaryReportBO;
import com.pls.core.domain.bo.dashboard.DailyLoadActivityBO;
import com.pls.core.domain.bo.dashboard.DestinationReportBO;
import com.pls.core.domain.bo.dashboard.FreightSpendAnalysisReportBO;
import com.pls.core.domain.bo.dashboard.GeographicSummaryReportBO;
import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;
import com.pls.core.domain.bo.dashboard.ScacBO;
import com.pls.core.domain.bo.dashboard.SeasonalityReportBO;
import com.pls.core.domain.bo.dashboard.ShipmentOverviewReportBO;
import com.pls.core.domain.bo.dashboard.VendorSummaryReportBO;
import com.pls.core.domain.bo.dashboard.WeightAnalysisReportBO;
import com.pls.core.service.KPIService;
import com.pls.core.service.file.FileInputStreamResponseEntity;

/**
 * KPI REST resource.
 * 
 * @author Aleksandr Nalapko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/kpireport")
public class KPIResource {
    private static final String DATE_FORMAT = "MM.dd.yyyy";
    private static final String XLS_EXTENSION = ".xls";

    @Autowired
    private KPIService service;

    /**
     * Get destination report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param status
     *            load status
     * 
     * @return destination report for customer id.
     */
    @RequestMapping(value = "/getDestinationReport", method = RequestMethod.GET)
    @ResponseBody
    public List<DestinationReportBO> getDestinationReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setScac(scac);
        filters.setStatus(status);

        return service.getDestinationReport(orgId, filters);
    }

    /**
     * Export Destination report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportDestinationReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportDestinationReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setScac(scac);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_DESTINATION_REPORT_" + stringDate + XLS_EXTENSION;
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportDestinationReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get daily load activity report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param weekday
     *            weekday
     * @param ioFlag
     *            inbound/outbound flag
     * @param status
     *            load status
     * @return daily load activity report for customer id.
     */
    @RequestMapping(value = "/getDailyLoadActivityReport", method = RequestMethod.GET)
    @ResponseBody
    public List<DailyLoadActivityBO> getDailyLoadActivityReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam(value = "weekday", required = false) List<String> weekday,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setIOFlag(ioFlag);
        filters.setWeekday(weekday);
        filters.setScac(scac);
        filters.setStatus(status);

        return service.getDailyLoadActivityReport(orgId, filters);
    }

    /**
     * Export Daily Load Activity report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param weekday
     *            weekday
     * @param ioFlag
     *            inbound/outbound flag
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportDailyLoadActivityReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportDailyLoadActivityReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "weekday", required = false) List<String> weekday,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setIOFlag(ioFlag);
        filters.setWeekday(weekday);
        filters.setScac(scac);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_DAILY_LOAD_ACTIVITY_REPORT_" + stringDate + XLS_EXTENSION;
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportDailyLoadActivityReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get shipment overview report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return shipment overview report for customer id.
     */
    @RequestMapping(value = "/getShipmentOverviewReport", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentOverviewReportBO> getShipmentOverviewReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setScac(scac);
        filters.setStatus(status);

        return service.getShipmentOverviewReport(orgId, filters);
    }

    /**
     * Export Shipment Overview report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportShipmentOverviewReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportShipmentOverviewReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setScac(scac);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_SHIPMENT_OVERVIEW_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportShipmentOverviewReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get Carrier Summary report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param month
     *            month
     * @param status
     *            load status
     * @return shipment overview report for customer id.
     */
    @RequestMapping(value = "/getCarrierSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public List<CarrierSummaryReportBO> getCarrierSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam(value = "month", required = false) List<String> month,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setClassCode(classCode);
        filters.setStatus(status);
        filters.setMonth(month);

        return service.getCarrierSummaryReport(orgId, filters);
    }

    /**
     * Export Carrier Summary report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param dest
     *            destination state
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportCarrierSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportCarrierSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "dest", required = false) List<String> dest,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setDest(dest);
        filters.setOrig(orig);
        filters.setClassCode(classCode);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_CARRIER_SUMMARY_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportCarrierSummaryReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get filters for customer id.
     * 
     * @param orgId
     *            organization ID
     * 
     * 
     * @return filters for customer id.
     */
    @RequestMapping(value = "/getFilterValues", method = RequestMethod.GET)
    @ResponseBody
    public KPIReportFiltersBO getFilterValues(@RequestParam(value = "orgId", required = false) Long orgId) {
        return service.getFilterValues(orgId);
    }

    /**
     * Fetches customers' freight spend analysis reports.
     * 
     * @param orgId
     *            - organization ID
     * @param classCode
     *            - class code
     * @param status
     *            load status
     * @return - list or requested reports
     */
    @ResponseBody
    @RequestMapping("/getFreightSpendReports")
    public List<FreightSpendAnalysisReportBO> getFreightSpendReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam("status") List<String> status) {
        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setStatus(status);
        return service.getFreightSpendAnalysisReports(orgId, filters);
    }

    /**
     * Fetches customer's weight analysis reports.
     * 
     * @param orgId
     *            - organization ID
     * @param weight
     *            - weight
     * @param status
     *            load status
     * @return - list of weight analysis reports
     */
    @ResponseBody
    @RequestMapping("/getWeightAnalysisReports")
    public List<WeightAnalysisReportBO> getWeightAnalysisReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "weight", required = false) List<String> weight,
            @RequestParam("status") List<String> status) {
        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setWeight(weight);
        filters.setStatus(status);
        return service.getWeightAnalysisReports(orgId, filters);
    }

    /**
     * Exports freight spend analysis reports to excel.
     * 
     * @param orgId
     *            - organization ID
     * @param classCode
     *            - class code
     * @param status
     *            - load status
     * @return - byte array response
     * @throws IOException
     *             exception
     */
    @ResponseBody
    @RequestMapping("/exportFreightSpendReports")
    public FileInputStreamResponseEntity exportFreightSpendReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam("status") List<String> status) throws IOException {
        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_FREIGHT_SPEND_ANALYSIS_REPORT_" + stringDate + XLS_EXTENSION;
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportFreightSpendAnalysisReports(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Exports weight analysis reports to excel.
     * 
     * @param orgId
     *            - organization ID
     * @param classCode
     *            - class code
     * @param status
     *            load status
     * @return - byte array response
     * @throws IOException
     *             - export exception
     */
    @ResponseBody
    @RequestMapping("/exportWeightAnalysisReports")
    public FileInputStreamResponseEntity exportWeightAnalysisReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam("status") List<String> status) throws IOException {
        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setClassCode(classCode);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_WEIGHT_ANALYSIS_REPORT_" + stringDate + XLS_EXTENSION;
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportWeightAnalysisReports(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Fetches customer's geographical reports.
     * 
     * @param orgId
     *            - organization ID
     * @param classCode
     *            - class code
     * @param destination
     *            - destination state
     * @param origin
     *            - origin state
     * @param ioFlag
     *            - inbound/outbound flag
     * @param status
     *            - load status
     * @return -list of requested reports
     */
    @ResponseBody
    @RequestMapping(value = "/getGeographicSummaryReports", method = RequestMethod.GET)
    public List<GeographicSummaryReportBO> getGeographicSummaryReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> origin,
            @RequestParam(required = false) List<String> destination,
            @RequestParam(required = false) List<String> ioFlag, @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setDest(destination);
        filters.setOrig(origin);
        filters.setClassCode(classCode);
        filters.setIOFlag(ioFlag);
        filters.setStatus(status);

        return service.getGeographicSummaryReports(orgId, filters);
    }

    /**
     * Exports geographical summary reports to excel.
     * 
     * @param orgId
     *            - organization ID
     * @param classCode
     *            - class code
     * @param destination
     *            - destination state
     * @param origin
     *            - origin state
     * @param ioFlag
     *            - inbound/outbound flag
     * @param status
     *            - load status
     * @return -byte array response
     * @throws IOException
     *             - export exception
     */
    @ResponseBody
    @RequestMapping("/exportGeographicSummaryReports")
    public FileInputStreamResponseEntity exportGeographicSummaryReports(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(required = false) List<String> classCode,
            @RequestParam(required = false) List<String> origin,
            @RequestParam(required = false) List<String> destination,
            @RequestParam(required = false) List<String> ioFlag, @RequestParam("status") List<String> status)
            throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setDest(destination);
        filters.setOrig(origin);
        filters.setClassCode(classCode);
        filters.setIOFlag(ioFlag);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_GEOGRAPHICAL_SUMMARY_REPORT_" + stringDate + XLS_EXTENSION;
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportGeographicSummaryReports(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get Class Summary report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param scac
     *            scac code
     * @param status
     *            load status
     * @return Class Summary overview report for customer id.
     */
    @RequestMapping(value = "/getClassSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public List<ClassSummaryReportBO> getClassSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setStatus(status);

        return service.getClassSummaryReport(orgId, filters);
    }

    /**
     * Export Class Summary report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param scac
     *            scac code
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportClassSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportClassSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_CLASS_SUMMARY_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportClassSummaryReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get Customer Summary report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return customer summary report for customer id.
     */
    @RequestMapping(value = "/getCustomerSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerSummaryReportBO> getCustomerSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setOrig(orig);
        filters.setClassCode(classCode);
        filters.setStatus(status);

        return service.getCustomerSummaryReport(orgId, filters);
    }

    /**
     * Export Customer Summary report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param orig
     *            origin state
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportCustomerSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportCustomerSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "orig", required = false) List<String> orig,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setOrig(orig);
        filters.setClassCode(classCode);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_CUSTOMER_SUMMARY_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportCustomerSummaryReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Export Vendor Summary report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportVendorSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportVendorSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setClassCode(classCode);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_VENDOR_SUMMARY_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportVendorSummaryReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get Vendor Summary report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param ioFlag
     *            inbound/outbound flag
     * @param classCode
     *            class code
     * @param scac
     *            scac code
     * @param status
     *            load status
     * @return vendor summary report for customer id.
     */
    @RequestMapping(value = "/getVendorSummaryReport", method = RequestMethod.GET)
    @ResponseBody
    public List<VendorSummaryReportBO> getVendorSummaryReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "ioFlag", required = false) List<String> ioFlag,
            @RequestParam(value = "scac", required = false) List<ScacBO> scac,
            @RequestParam(value = "classCode", required = false) List<String> classCode,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setIOFlag(ioFlag);
        filters.setScac(scac);
        filters.setClassCode(classCode);
        filters.setStatus(status);

        return service.getVendorSummaryReport(orgId, filters);
    }

    /**
     * Export Seasonality report to excel document and send it to user.
     * 
     * @param orgId
     *            organisation ID
     * @param year
     *            year
     * @param status
     *            load status
     * @return response with excel document
     * @throws IOException
     *             exception
     */
    @RequestMapping(value = "/exportSeasonalityReport", method = RequestMethod.GET)
    @ResponseBody
    public FileInputStreamResponseEntity exportSeasonalityReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "year", required = false) List<String> year,
            @RequestParam("status") List<String> status) throws IOException {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setYear(year);
        filters.setStatus(status);

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        String stringDate = formatter.format(new Date());
        String fileName = "KPI_SEASONALITY_REPORT_" + stringDate + ".xls";
        File tempFile = File.createTempFile("kpiData", "tmp");
        service.exportSeasonalityReport(orgId, filters, new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
    }

    /**
     * Get Seasonality report for customer id.
     * 
     * @param orgId
     *            organisation ID
     * @param year
     *            year
     * @param status
     *            load status
     * @return seasonality report for customer id.
     */
    @RequestMapping(value = "/getSeasonalityReport", method = RequestMethod.GET)
    @ResponseBody
    public List<SeasonalityReportBO> getSeasonalityReport(@RequestParam(value = "orgId", required = false) Long orgId,
            @RequestParam(value = "year", required = false) List<String> year,
            @RequestParam("status") List<String> status) {

        KPIReportFiltersBO filters = new KPIReportFiltersBO();
        filters.setYear(year);
        filters.setStatus(status);

        return service.getSeasonalityReport(orgId, filters);
    }

}
