package com.pls.shipment.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.bo.LostSavingsReportBO;
import com.pls.core.domain.bo.ReportsBO;
import com.pls.core.domain.enums.ExcelReportName;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.shipment.dao.ShipmentReportDao;
import com.pls.shipment.domain.bo.AccessorialReportBO;
import com.pls.shipment.domain.bo.ActivityReportsBO;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.bo.ProductReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;
import com.pls.shipment.service.ShipmentReportService;
import com.pls.shipment.service.xls.ActivityExcelReportBuilder;
import com.pls.shipment.service.xls.CarrierActivityExcelReportBuilder;
import com.pls.shipment.service.xls.CreationReportExcelBuilder;
import com.pls.shipment.service.xls.LostSavingsReportExcelBuilder;
import com.pls.shipment.service.xls.SavingsReportExcelBuilder;
import com.pls.shipment.service.xls.UnbilledReportExcelBuilder;

/**
 * Report Service implementation.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Service
@Transactional
public class ShipmentReportServiceImpl implements ShipmentReportService {

    @Value("/templates/RevenueRpt.xlsx")
    private ClassPathResource revenueReport;

    @Value("/templates/SavingsRpt.xlsx")
    private ClassPathResource savingsReportResource;

    @Value("/templates/ActivityRpt.xlsx")
    private ClassPathResource activityReportResource;
    
    @Value("/templates/CarrierActivityRpt.xlsx")
    private ClassPathResource carrierActivityReportResource;

    @Value("/templates/SavOpptyRpt.xlsx")
    private ClassPathResource lostSavingsReport;

    @Value("/templates/ShipmentCreationRpt.xlsx")
    private ClassPathResource shipmentCreationReport;

    @Autowired
    private ShipmentReportDao shipmentReportDao;

    @Autowired
    private AccessorialTypeDao accessorialTypeDao;

    @Override
    public FileInputStreamResponseEntity exportReport(ReportParamsBO reportParams) throws IOException, ParseException {

        ExcelReportName name = ExcelReportName.getValue(reportParams.getReportName());
        switch (name) {
        case UNBILLED:
            List<ReportsBO> reports = shipmentReportDao.getUnbilledReport(reportParams.getCustomerId(),
                    reportParams.getCompanyCode(), reportParams.getEndDate());
            return new UnbilledReportExcelBuilder(revenueReport).generateReport(reports,
                    reportParams.getBusinessUnitName(), reportParams.getCompanyCodeDescription(),
                    reportParams.getCustomerName(), reportParams.getStartDate(), reportParams.getEndDate());
        case SAVINGS:
            List<FreightAnalysisReportBO> savingsReport = shipmentReportDao.getSavingsReport(
                    reportParams.getCustomerId(), reportParams.getBusinessUnitId(), reportParams.getStartDate(), reportParams.getEndDate());
            return new SavingsReportExcelBuilder(savingsReportResource).generateReport(savingsReport, reportParams);
        case ACTIVITY:
            List<FreightAnalysisReportBO> reportData = shipmentReportDao.getActivityReport(reportParams.getCustomerId(), reportParams.getBusinessUnitId(), 
                    reportParams.getStartDate(), reportParams.getEndDate(), reportParams.getDateType());
            List<ActivityReportsBO> result = new ArrayList<ActivityReportsBO>(reportData.size());
            Map<String, String> allAccessorials = new HashMap<String, String>();
            BigDecimal totalCost = fillDataForActivityReport(reportData, result, allAccessorials);
            return new ActivityExcelReportBuilder(activityReportResource).generateReport(result, allAccessorials, reportParams, totalCost);
        case CARRIER_ACTIVITY:
            List<FreightAnalysisReportBO> carrierReportData = shipmentReportDao.getCarrierActivityReport(reportParams.getCarrierId(), reportParams.getCustomerId(), 
                    reportParams.getStartDate(), reportParams.getEndDate(), reportParams.getDateType());
            List<ActivityReportsBO> carrierReportResult = new ArrayList<ActivityReportsBO>(carrierReportData.size());
            Map<String, String> carrierReportallAccessorials = new HashMap<String, String>();
            BigDecimal carrierReportTotalCost = fillDataForActivityReport(carrierReportData, carrierReportResult, carrierReportallAccessorials);
            return new CarrierActivityExcelReportBuilder(carrierActivityReportResource).generateReport(carrierReportResult, carrierReportallAccessorials, reportParams, carrierReportTotalCost);
        case SHIPMENT_CREATION:
            List<CreationReportBO> creationData = shipmentReportDao.getCreationReport(reportParams.getCustomerId(), reportParams.getBusinessUnitId(),
                    reportParams.getStartDate(), reportParams.getEndDate(), reportParams.isInvoicedShipmentsOnly());
            return new CreationReportExcelBuilder(shipmentCreationReport).generateReport(creationData, reportParams);
        case LOST_SAVINGS:
            List<LostSavingsReportBO> lostSvngsOppRptData = shipmentReportDao.getLostSavingsReport(
                    reportParams.getCustomerId(), reportParams.getBusinessUnitId(), reportParams.getStartDate(), reportParams.getEndDate(),
                    reportParams.getSortOrder());
            List<AccessorialTypeEntity> accLegend = accessorialTypeDao.getLtlAccessorialTypes();
            return new LostSavingsReportExcelBuilder(lostSavingsReport).generateLostSavingsReport(lostSvngsOppRptData, reportParams, accLegend);
        default:
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Fill data for activity report.
     *
     * @param items load items from dao layer
     * @param result - result to fill
     * @param allAccessorials the all accessorials for all loads
     * @return total cost of loads
     */
    private BigDecimal fillDataForActivityReport(List<FreightAnalysisReportBO> items, List<ActivityReportsBO> result, Map<String,
            String> allAccessorials) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (FreightAnalysisReportBO item : items) {
            ActivityReportsBO reportItem = new ActivityReportsBO(item);
            reportItem.setTotalWeight(getTotalWeight(item));
            Set<String> accessorials = new HashSet<String>();
            fillAccessorials(allAccessorials, item, accessorials);

            BigDecimal accCost = fillAccCost(item);

            reportItem.setCustomerAccCost(accCost);
            reportItem.setCustomerTotalCost(item.getTotalRevenue());

            BigDecimal customerCostPerPound = null;
            customerCostPerPound = fillCustomerCostPerPound(item, reportItem.getTotalWeight(), customerCostPerPound);
            reportItem.setCustomerCostPerPound(customerCostPerPound);
            result.add(reportItem);
            if (item.getTotalRevenue() != null) {
                totalCost = totalCost.add(item.getTotalRevenue());
            }
        }
        return totalCost;
    }

    private BigDecimal fillCustomerCostPerPound(FreightAnalysisReportBO item, BigDecimal totalWeight, BigDecimal customerCostPerPound) {
        BigDecimal result = customerCostPerPound;
        if (totalWeight != null && BigDecimal.ZERO.compareTo(totalWeight) != 0) {
            if (item.getCustLhCost() == null) {
                item.setCustLhCost(BigDecimal.ZERO);
            }
            if (item.getCustFsCost() == null) {
                item.setCustFsCost(BigDecimal.ZERO);
            }
            result = item.getCustLhCost().add(item.getCustFsCost()).divide(totalWeight, 2, RoundingMode.HALF_EVEN);
        }
        return result;
    }

    private void fillAccessorials(Map<String, String> allAccessorials, FreightAnalysisReportBO item, Set<String> accessorials) {
        for (AccessorialReportBO accessorial : item.getAccessorials()) {
            accessorials.add(accessorial.getAccessorialTypeCode());
            if (accessorial.getAccessorialTypeCode() != null) {
                allAccessorials.put(accessorial.getAccessorialTypeCode(), accessorial.getDescription());
            }
        }
    }

    private BigDecimal getTotalWeight(FreightAnalysisReportBO item) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (ProductReportBO product : item.getProducts()) {
            if (product.getWeight() != null) {
                totalWeight = totalWeight.add(product.getWeight());
            }
        }
        return totalWeight;
    }

    private BigDecimal fillAccCost(FreightAnalysisReportBO item) {
        BigDecimal accCost = item.getTotalRevenue();
        if (item.getCustLhCost() != null) {
            accCost = accCost.subtract(item.getCustLhCost());
        }
        if (item.getCustFsCost() != null) {
            accCost = accCost.subtract(item.getCustFsCost());
        }
        return accCost;
    }
}
