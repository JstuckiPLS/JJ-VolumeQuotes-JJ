package com.pls.shipment.service.xls;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.shipment.domain.bo.ActivityReportsBO;
import com.pls.shipment.domain.bo.AuditReasonReportBO;
import com.pls.shipment.domain.bo.ProductReportBO;

public class CarrierActivityExcelReportBuilder extends ActivityExcelReportBuilder {
    
    private static final int FIRST_PRODUCTS_COLUMN = 34;

    public CarrierActivityExcelReportBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    protected void buildRow(ActivityReportsBO report, int rowIndex, int maxProductSize) {
        Row currentRow = mainSheet.createRow(rowIndex);
        int index = 0;
        fillDataCell(currentRow, index++, report.getBusinessUnit(), style);
        fillDataCell(currentRow, index++, report.getCustomerName(), style);
        fillDataCell(currentRow, index++, report.getDeparture());
        fillDataCell(currentRow, index++, report.getEarlyScheduledArrival());
        fillDataCell(currentRow, index++, report.getLoadId(), style);
        fillDataCell(currentRow, index++, report.getBol(), style);
        fillDataCell(currentRow, index++, report.getShipperRef(), style);
        fillDataCell(currentRow, index++, report.getPoNumber(), style);
        fillDataCell(currentRow, index++, report.getGlNumber(), style);
        fillDataCell(currentRow, index++, report.getProNumber(), style);
        fillDataCell(currentRow, index++, report.getScacCode(), style);
        fillDataCell(currentRow, index++, report.getCarrierName(), style);
        fillDataCell(currentRow, index++, report.getShipmentDirection().getCode(), style);
        fillDataCell(currentRow, index++, report.getOriginContact(), style);
        fillDataCell(currentRow, index++, report.getOriginAddress(), style);
        fillDataCell(currentRow, index++, report.getOriginCity(), style);
        fillDataCell(currentRow, index++, report.getOriginStateCode(), style);
        fillDataCell(currentRow, index++, report.getOriginZip(), style);
        fillDataCell(currentRow, index++, report.getDestinationContact(), style);
        fillDataCell(currentRow, index++, report.getDestinationAddress(), style);
        fillDataCell(currentRow, index++, report.getDestinationCity(), style);
        fillDataCell(currentRow, index++, report.getDestinationStateCode(), style);
        fillDataCell(currentRow, index++, report.getDestinationZip(), style);
        fillDataCell(currentRow, index++, report.getTotalWeight(), style);
        fillDataCell(currentRow, index++, generateAccessorialsString(report.getAccessorials()), style);
        fillDataCell(currentRow, index++, report.getCustLhCost());
        fillDataCell(currentRow, index++, report.getCustFsCost());
        fillDataCell(currentRow, index++, report.getCustomerAccCost());
        fillDataCell(currentRow, index++, report.getCustomerTotalCost());
        fillDataCell(currentRow, index++, report.getOpsStatus().toString(), style);
        fillDataCell(currentRow, index++, report.getBillingStatus(), style);
        fillDataCell(currentRow, index++, report.getPricingProfileId(), style);
        fillDataCell(currentRow, index++, report.getLtlPricingType() != null ? report.getLtlPricingType().name() : "", style);
        fillDataCell(currentRow, index++, generateReasonsString(report.getAuditReasons()), style);
        for (ProductReportBO product : report.getProducts()) {
            fillDataCell(currentRow, index++, product.getWeight(), style);
            if (product.getCommodityClass() != null) {
                fillDataCell(currentRow, index++, product.getCommodityClass().getDbCode(), style);
            } else {
                fillDataCell(currentRow, index++, "", style);
            }
            fillDataCell(currentRow, index++, product.getPieces(), style);
            fillDataCell(currentRow, index++, generateDimensionString(product), style);
        }
        for (int i = report.getProducts().size(); i < maxProductSize; i++) {
            fillDataCell(currentRow, index++, "", style);
            fillDataCell(currentRow, index++, "", style);
            fillDataCell(currentRow, index++, "", style);
            fillDataCell(currentRow, index++, "", style);
        }
    }

    private String generateReasonsString(List<AuditReasonReportBO> auditReasons) {
        if(auditReasons == null) return "";
        List<String> reasonDescs = auditReasons.stream().map(it->it.getDescription()).collect(Collectors.toList());
        return StringUtils.join(reasonDescs, ", ");
    }

    @Override
    protected int getFirstProductColumn() {
        return FIRST_PRODUCTS_COLUMN;
    }
    
    
}
