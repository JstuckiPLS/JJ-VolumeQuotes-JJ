package com.pls.invoice.service.xsl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * CBI Customized excel report builder.
 * 
 * @author Alexander Nalapko
 *
 */
public class CBICustomizedExcelReportBuilder extends AbstractCBIExcelReportBuilder {

    /**
     * Constructor.
     * 
     * @param revenueReportTemplate
     *            resource
     * @throws IOException
     *             error
     */
    public CBICustomizedExcelReportBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    /**
     * Build report body.
     *
     * @param billTo
     *            billing data
     * @param invoices
     *            - loads and adjustments sorted in the order that they should appear in the invoice.
     */
    protected void buildReport(BillToEntity billTo, List<LoadAdjustmentBO> invoices) {

        List<List<Object>> cellIn = new ArrayList<List<Object>>();
        List<List<Object>> cellOu = new ArrayList<List<Object>>();

        BigDecimalBean inbound = new BigDecimalBean();
        BigDecimalBean outbound = new BigDecimalBean();

        for (LoadAdjustmentBO invoice : invoices) {
            if (invoice.getLoad().getShipmentDirection() == ShipmentDirection.INBOUND) {
                cellIn.add(getRow(invoice, inbound));
            } else {
                cellOu.add(getRow(invoice, outbound));
            }
        }

        int cellIndex = 7;
        if (!cellIn.isEmpty()) {
            fillSheet(cellIn, 1);
            addInfo("INBOUND", cellIndex++, inbound);
        }
        if (!cellOu.isEmpty()) {
            fillSheet(cellOu, 2);
            addInfo("OUTBOUND", cellIndex++, outbound);
        }

        BigDecimal totalLinehaul = BigDecimal.ZERO;
        BigDecimal totalTransactionFee = BigDecimal.ZERO;
        BigDecimal totalAllAccessorialsCost = BigDecimal.ZERO;
        BigDecimal totalFuelSurcharge = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        fillDataCellAndSaveStyle(mainSheet.getRow(24), cellIndex, "TOTAL");

        totalLinehaul = inbound.getLinehaul().add(outbound.getLinehaul());
        fillDataCellAndSaveStyle(mainSheet.getRow(26), cellIndex, totalLinehaul);

        totalTransactionFee = inbound.getTransactionFee().add(outbound.getTransactionFee());
        fillDataCellAndSaveStyle(mainSheet.getRow(27), cellIndex, totalTransactionFee);

        totalFuelSurcharge = inbound.getFuelSurcharge().add(outbound.getFuelSurcharge());
        fillDataCellAndSaveStyle(mainSheet.getRow(28), cellIndex, totalFuelSurcharge);

        totalAllAccessorialsCost = inbound.getAllAccessorialsCost().add(outbound.getAllAccessorialsCost());
        fillDataCellAndSaveStyle(mainSheet.getRow(29), cellIndex, totalAllAccessorialsCost);

        total = inbound.getTotal().add(outbound.getTotal());
        fillDataCellAndSaveStyle(mainSheet.getRow(32), cellIndex, total);

        // Delete unused from template
        if (cellOu.isEmpty()) {
            workbook.removeSheetAt(2);
            cleanCell(++cellIndex, 24, 34);
        }
        if (cellIn.isEmpty()) {
            workbook.removeSheetAt(1);
            cleanCell(++cellIndex, 24, 34);
        }
    }

    private void addInfo(String title, int cellIndex, BigDecimalBean bean) {
        fillDataCellAndSaveStyle(mainSheet.getRow(24), cellIndex, title);
        fillDataCellAndSaveStyle(mainSheet.getRow(26), cellIndex, bean.getLinehaul());
        fillDataCellAndSaveStyle(mainSheet.getRow(27), cellIndex, bean.getTransactionFee());
        fillDataCellAndSaveStyle(mainSheet.getRow(28), cellIndex, bean.getFuelSurcharge());
        fillDataCellAndSaveStyle(mainSheet.getRow(29), cellIndex, bean.getAllAccessorialsCost());
        fillDataCellAndSaveStyle(mainSheet.getRow(32), cellIndex, bean.getTotal());
    }

    private void cleanCell(int cellIndex, int rowStart, int rowEnd) {
        for (int i = rowStart; i < rowEnd; i++) {
            mainSheet.getRow(i).removeCell(mainSheet.getRow(i).getCell(cellIndex));
        }
    }

    private List<Object> getRow(LoadAdjustmentBO invoice, BigDecimalBean bean) {
        List<Object> row = new ArrayList<Object>();
        CBIReportRowAdapter adapter = getCBIReportAdapter(invoice);

        row.add(adapter.getBillToLocation());
        row.add(adapter.getBillToId());
        row.add("VANLTL");
        row.add(adapter.getCommodityClassString());
        row.add(adapter.getShipmentDirectionCode());
        row.add(adapter.getPaymentTermsCode());
        row.add(adapter.getShipDate());
        row.add(adapter.getDeliveryDate());
        row.add(adapter.getSoNumber());
        row.add(adapter.getPoNumber());
        row.add(adapter.getBolNumber());
        row.add(adapter.getRefNumber());
        row.add(adapter.getProNumber());
        row.add(adapter.getCarrierSCAC());
        row.add(adapter.getCarrierName());
        row.add(adapter.getOriginCity());
        row.add(adapter.getOriginState());
        row.add(adapter.getOriginZip());
        row.add(adapter.getDestinationCity());
        row.add(adapter.getDestinationState());
        row.add(adapter.getDestinationZip());
        row.add(adapter.getWeight());
        row.add(adapter.getMileage());

        bean.addLinehaul(adapter.getLineHaul());
        row.add(adapter.getLineHaul());

        bean.addTransactionFee(adapter.getTransactionFee());
        row.add(adapter.getTransactionFee());

        row.add(0); // will always be 0
        row.add(BigDecimal.ZERO); // will always be $0

        bean.addAllAccessorialsCost(adapter.getAllAccessorialsCost());
        row.add(adapter.getAllAccessorialsCost()); // Duplicates Acc
        row.add(adapter.getAllAccessorialsCost()); // Duplicates Acc

        bean.addFuelSurcharge(adapter.getFuelSurcharge());
        row.add(adapter.getFuelSurcharge());

        BigDecimal total = adapter.getLineHaul().add(adapter.getTransactionFee()).add(adapter.getAllAccessorialsCost())
                .add(adapter.getFuelSurcharge());
        row.add(total);
        return row;
    }

    /**
     * Container for cost calculation.
     * 
     * @author Alexander Nalapko
     *
     */
    class BigDecimalBean {
        BigDecimal linehaul = BigDecimal.ZERO;
        BigDecimal transactionFee = BigDecimal.ZERO;
        BigDecimal allAccessorialsCost = BigDecimal.ZERO;
        BigDecimal fuelSurcharge = BigDecimal.ZERO;

        public BigDecimal getLinehaul() {
            return linehaul;
        }

        /**
         * Concatenate Linehaul.
         * 
         * @param linehaul
         *            - PLS Carrier cost for LH
         */
        public void addLinehaul(BigDecimal linehaul) {
            this.linehaul = this.linehaul.add(linehaul);
        }

        public BigDecimal getTransactionFee() {
            return transactionFee;
        }

        /**
         * Concatenate transactionFee.
         * 
         * @param transactionFee
         *            - transaction fee
         */
        public void addTransactionFee(BigDecimal transactionFee) {
            this.transactionFee = this.transactionFee.add(transactionFee);
        }

        public BigDecimal getAllAccessorialsCost() {
            return allAccessorialsCost;
        }

        /**
         * Concatenate allAccessorialsCost.
         * 
         * @param allAccessorialsCost
         *            - accessorials
         */
        public void addAllAccessorialsCost(BigDecimal allAccessorialsCost) {
            this.allAccessorialsCost = this.allAccessorialsCost.add(allAccessorialsCost);
        }

        public BigDecimal getFuelSurcharge() {
            return fuelSurcharge;
        }

        /**
         * Concatenate fuelSurcharge.
         * 
         * @param fuelSurcharge
         *            - PLS Carrier cost for FS
         */
        public void addFuelSurcharge(BigDecimal fuelSurcharge) {
            this.fuelSurcharge = this.fuelSurcharge.add(fuelSurcharge);
        }

        /**
         * Concatenate total cost.
         * 
         * @return total
         */
        public BigDecimal getTotal() {
            return linehaul.add(transactionFee).add(allAccessorialsCost).add(fuelSurcharge);
        }

    }

}
