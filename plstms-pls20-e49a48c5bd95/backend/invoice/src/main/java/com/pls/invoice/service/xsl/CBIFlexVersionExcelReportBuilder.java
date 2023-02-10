package com.pls.invoice.service.xsl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.organization.BillToEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Invoice flex version report excel builder.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class CBIFlexVersionExcelReportBuilder extends AbstractCBIExcelReportBuilder {

    /**
     * Invoice flex version constructor.
     * 
     * @param invoiceTemplate - invoice template.
     * @throws IOException exception
     */
    public CBIFlexVersionExcelReportBuilder(ClassPathResource invoiceTemplate) throws IOException {
        super(invoiceTemplate);
    }

    @Override
    void buildReport(BillToEntity billTo, List<LoadAdjustmentBO> invoices) {
        fillSheet(getRows(invoices), 1);
        fillTotalInfo(invoices);
    }

    /**
     * List of load with adjustment as CBI report row data.
     * 
     * @param invoices - list of loads with adjustment data.
     * @return rows data for report.
     */
    private List<List<Object>> getRows(List<LoadAdjustmentBO> invoices) {
        List<List<Object>> row = new ArrayList<List<Object>>();
        for (LoadAdjustmentBO invoice : invoices) {
            List<Object> column = new ArrayList<Object>();
            CBIReportRowAdapter adapter = getCBIReportAdapter(invoice);
            column.add(adapter.getBillToLocation());
            column.add(adapter.getBillToId());
            column.add("VANLTL");
            column.add(adapter.getCommodityClassString());
            column.add(adapter.getShipmentDirectionCode());
            column.add(adapter.getPaymentTermsCode());
            column.add(adapter.getShipDate());
            column.add(adapter.getDeliveryDate());
            column.add(adapter.getPoNumber());
            column.add(adapter.getSoNumber());
            column.add(adapter.getBolNumber());
            column.add(adapter.getRefNumber());
            column.add(adapter.getProNumber());
            column.add(adapter.getCarrierSCAC());
            column.add(adapter.getCarrierName());
            column.add(adapter.getCarrierOriginAddressName());
            column.add(adapter.getOriginCity());
            column.add(adapter.getOriginState());
            column.add(adapter.getOriginZip());
            column.add(adapter.getCarrierDestinationAddressName());
            column.add(adapter.getDestinationCity());
            column.add(adapter.getDestinationState());
            column.add(adapter.getDestinationZip());
            column.add(adapter.getWeight());
            column.add(adapter.getMileage());
            column.add(adapter.getLineHaul());
            column.add(0); // hardcord, will always be 0
            column.add(BigDecimal.ZERO); // hardcord, will always be 0
            column.add(adapter.getAllAccessorialsCost());
            column.add(adapter.getAllAccessorialsCost());
            column.add(adapter.getFuelSurcharge());

            BigDecimal total = adapter.getLineHaul().add(adapter.getAllAccessorialsCost()).add(adapter.getFuelSurcharge());
            column.add(total);
            row.add(column);
        }
        return Collections.unmodifiableList(row);
    }

    private void fillTotalInfo(List<LoadAdjustmentBO> invoices) {
        BigDecimal linehaul = BigDecimal.ZERO;
        BigDecimal fuelSurcharge = BigDecimal.ZERO;
        BigDecimal otherAccessorials = BigDecimal.ZERO;

        for (LoadAdjustmentBO invoice : invoices) {
            CBIReportRowAdapter adapter = getCBIReportAdapter(invoice);
            linehaul = linehaul.add(adapter.getLineHaul());
            fuelSurcharge = fuelSurcharge.add(adapter.getFuelSurcharge());
            otherAccessorials = otherAccessorials.add(adapter.getAllAccessorialsCost());
        }
        BigDecimal totalInvoice = linehaul.add(fuelSurcharge).add(otherAccessorials);

        workbook.setActiveSheet(0);
        fillDataCellAndSaveStyle(mainSheet.getRow(26), 9, linehaul);
        fillDataCellAndSaveStyle(mainSheet.getRow(27), 9, fuelSurcharge);
        fillDataCellAndSaveStyle(mainSheet.getRow(28), 9, otherAccessorials);
        fillDataCellAndSaveStyle(mainSheet.getRow(31), 9, totalInvoice);
    }
}
