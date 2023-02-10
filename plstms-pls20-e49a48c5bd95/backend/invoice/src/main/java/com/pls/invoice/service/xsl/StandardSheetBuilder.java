package com.pls.invoice.service.xsl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;

import com.google.common.collect.ImmutableList;

/**
 * Excel Sheet Builder for CBI Standard Excel Report.
 * 
 * @author Artem Arapov
 *
 */
public final class StandardSheetBuilder {

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private final CellStyle currencyCellStyle;
    private final CellStyle dateCellStyle;
    private final boolean isGainshare;
    private static final String CURRENCY_FORMAT = "\"$\"#,##0.00";
    private static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, new SimpleDateFormat("MM/dd/yyyy", Locale.US));
    private List<Integer> totalsColumns;

    private BigDecimal totalResidentialPickupCost = BigDecimal.ZERO;
    private BigDecimal totalLiftGatePickupCost = BigDecimal.ZERO;
    private BigDecimal totalInsidePickupCost = BigDecimal.ZERO;
    private BigDecimal totalOverDimensionCost = BigDecimal.ZERO;
    private BigDecimal totalBlindBolCost = BigDecimal.ZERO;
    private BigDecimal totalLimitedAccessPickupCost = BigDecimal.ZERO;
    private BigDecimal totalResidentialDeliveryCost = BigDecimal.ZERO;
    private BigDecimal totalLiftGateDeliveryCost = BigDecimal.ZERO;
    private BigDecimal totalInsideDeliveryCost = BigDecimal.ZERO;
    private BigDecimal totalSortSegregateCost = BigDecimal.ZERO;
    private BigDecimal totalNotifyCost = BigDecimal.ZERO;
    private BigDecimal totalLimitedAccessDeliveryCost = BigDecimal.ZERO;

    private BigDecimal totalLinehaul = BigDecimal.ZERO;
    private BigDecimal totalTransactionFee = BigDecimal.ZERO;
    private BigDecimal totalFuelSurcharge = BigDecimal.ZERO;
    private BigDecimal totalOtherAccessorials = BigDecimal.ZERO;
    private BigDecimal totalAllAccessorials = BigDecimal.ZERO;
    private BigDecimal totalInvoice = BigDecimal.ZERO;
    private BigDecimal totalInvoiceAdjusted = BigDecimal.ZERO;
    private BigDecimal totalGainShareSaving = BigDecimal.ZERO;
    private BigDecimal totalBMRate = BigDecimal.ZERO;

    private StandardSheetBuilder(Workbook workbook, SheetName sheetName, boolean isGainshare) {
        this.workbook = workbook;
        this.isGainshare = isGainshare;
        this.rowNumber = 1;
        workbook.setActiveSheet(sheetName.idx);
        sheet = workbook.getSheetAt(sheetName.idx);
        DataFormat df = workbook.createDataFormat();
        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat(df.getFormat(CURRENCY_FORMAT));
        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(df.getFormat(DATE_FORMAT));

        if (isGainshare) {
            totalsColumns = ImmutableList.of(24, 25, 26, 27, 28, 29, 35, 36, 37, 38, 39, 40, 44, 45, 46, 47, 48, 49, 50, 51, 52);
        } else {
            totalsColumns = ImmutableList.of(24, 25, 26, 27, 28, 29, 35, 36, 37, 38, 39, 40, 43, 44, 45, 46, 47, 48, 49);
        }
    }

    /**
     * Build Excel Sheet for Inbound loads.
     * 
     * @param workbook - Instance of {@link Workbook}
     * @param list - List of {@link CBIReportRowAdapter}
     * @return SheetTotals - totals of current Sheet.
     */
    public static SheetTotals buildInboundSheet(Workbook workbook, Iterable<CBIReportRowAdapter> list) {
        return buildSheet(workbook, list, SheetName.INBOUND, false);
    }

    /**
     * Build Excel Sheet for Outbound loads.
     * 
     * @param workbook - Instance of {@link Workbook}
     * @param list - List of {@link CBIReportRowAdapter}
     * @return SheetTotals - totals of current Sheet.
     */
    public static SheetTotals buildOutboundSheet(Workbook workbook, Iterable<CBIReportRowAdapter> list) {
        return buildSheet(workbook, list, SheetName.OUTBOUND, false);
    }

    /**
     * Build Excel Sheet for Inbound loads with Gainshare columns.
     * 
     * @param workbook - Instance of {@link Workbook}
     * @param list - List of {@link CBIReportRowAdapter}
     * @return SheetTotals - totals of current Sheet.
     */
    public static SheetTotals buildInboundGainshareSheet(Workbook workbook, Iterable<CBIReportRowAdapter> list) {
        return buildSheet(workbook, list, SheetName.INBOUND, true);
    }

    /**
     * Build Excel Sheet for Outbound loads with Gainshare columns.
     * 
     * @param workbook - Instance of {@link Workbook}
     * @param list - List of {@link CBIReportRowAdapter}
     * @return SheetTotals - totals of current Sheet.
     */
    public static SheetTotals buildOutboundGainshareSheet(Workbook workbook, Iterable<CBIReportRowAdapter> list) {
        return buildSheet(workbook, list, SheetName.OUTBOUND, true);
    }

    private static SheetTotals buildSheet(Workbook workbook, Iterable<CBIReportRowAdapter> list, SheetName sheetName, boolean isGainshare) {
        SheetTotals totals = new SheetTotals();
        StandardSheetBuilder builder = new StandardSheetBuilder(workbook, sheetName, isGainshare);
        builder.buildSheet(list);
        totals.totalFuelSurcharge = builder.totalFuelSurcharge;
        totals.totalLinehaul = builder.totalLinehaul;
        totals.totalOtherAccessorials = builder.totalOtherAccessorials;
        totals.totalAllAccessorials = builder.totalAllAccessorials;
        totals.totalTransactionFee = builder.totalTransactionFee;
        totals.totalGainshare = builder.totalGainShareSaving;
        totals.empty ^= (builder.rowNumber > 1);

        return totals;
    }

    private void buildSheet(Iterable<CBIReportRowAdapter> list) {
        for (CBIReportRowAdapter item: list) {
            buildSheetRow(item, rowNumber++);
        }
        addTotals(rowNumber);
    }

    private void buildSheetRow(CBIReportRowAdapter invoice, int rowIndex) {
        Row currentRow = sheet.createRow(rowIndex);

        int columnIndex = 0;
        fillDataCell(currentRow, columnIndex++, invoice.getShipperName());
        fillDataCell(currentRow, columnIndex++, invoice.getLoadId());
        fillDataCell(currentRow, columnIndex++, invoice.getInvoiceNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getBillToId());
        fillDataCell(currentRow, columnIndex++, "VANLTL");
        fillDataCell(currentRow, columnIndex++, invoice.getCommodityClass());
        fillDataCell(currentRow, columnIndex++, invoice.getShipmentDirection());
        fillDataCell(currentRow, columnIndex++, invoice.getPaymentTerms());
        fillDataCell(currentRow, columnIndex++, invoice.getShipDate());
        fillDataCell(currentRow, columnIndex++, invoice.getDeliveryDate());
        fillDataCell(currentRow, columnIndex++, invoice.getGlNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getRefNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getPuNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getBolNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getSoNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getPoNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getProNumber());
        fillDataCell(currentRow, columnIndex++, invoice.getCarrierSCAC());
        fillDataCell(currentRow, columnIndex++, invoice.getCarrierName());
        fillDataCell(currentRow, columnIndex++, invoice.getCarrierOriginAddressName());
        fillDataCell(currentRow, columnIndex++, invoice.getOriginAddress1());
        fillDataCell(currentRow, columnIndex++, invoice.getOriginCity());
        fillDataCell(currentRow, columnIndex++, invoice.getOriginState());
        fillDataCell(currentRow, columnIndex++, invoice.getOriginZip());
        fillDataCell(currentRow, columnIndex++, processResinedtialCost(invoice.getResidentialPickupCost()));
        fillDataCell(currentRow, columnIndex++, processLiftgateCost(invoice.getLiftGatePickupCost()));
        fillDataCell(currentRow, columnIndex++, processInsidePickupCost(invoice.getInsidePickupCost()));
        fillDataCell(currentRow, columnIndex++, processOverDimensionCost(invoice.getOverDimensionCost()));
        fillDataCell(currentRow, columnIndex++, processBlindBolCost(invoice.getBlindBolCost()));
        fillDataCell(currentRow, columnIndex++, processLimitedAccessPickupCost(invoice.getLimitedAccessPickupCost()));
        fillDataCell(currentRow, columnIndex++, invoice.getCarrierDestinationAddressName());
        fillDataCell(currentRow, columnIndex++, invoice.getDestinationAddress1());
        fillDataCell(currentRow, columnIndex++, invoice.getDestinationCity());
        fillDataCell(currentRow, columnIndex++, invoice.getDestinationState());
        fillDataCell(currentRow, columnIndex++, invoice.getDestinationZip());
        fillDataCell(currentRow, columnIndex++, processResidentialDeliveryCost(invoice.getResidentialDeliveryCost()));
        fillDataCell(currentRow, columnIndex++, processLiftGateDeliveryCost(invoice.getLiftGateDeliveryCost()));
        fillDataCell(currentRow, columnIndex++, processInsideDeliveryCost(invoice.getInsideDeliveryCost()));
        fillDataCell(currentRow, columnIndex++, processSortSegregateCost(invoice.getSortSegregateCost()));
        fillDataCell(currentRow, columnIndex++, processNotifyCost(invoice.getNotifyCost()));
        fillDataCell(currentRow, columnIndex++, processLimitedAccessDeliveryCost(invoice.getLimitedAccessDeliveryCost()));
        fillDataCell(currentRow, columnIndex++, invoice.getWeight());
        fillDataCell(currentRow, columnIndex++, invoice.getMileage());

        if (isGainshare) {
            fillDataCell(currentRow, columnIndex++, invoice.getCarrierRate());
        }

        fillDataCell(currentRow, columnIndex++, processLineHaul(invoice.getLineHaul()));
        fillDataCell(currentRow, columnIndex++, processFuelSurcharge(invoice.getFuelSurcharge()));
        fillDataCell(currentRow, columnIndex++, processTransactionFee(invoice.getTransactionFee()));
        fillDataCell(currentRow, columnIndex++, processOthersAccessorials(invoice.getOtherAccessorialsCost()));
        fillDataCell(currentRow, columnIndex++, processAllAccessorials(invoice.getAllAccessorialsCost()));
        fillDataCell(currentRow, columnIndex++, processTotalLoad(invoice.getTotalRevenue()));

        if (isGainshare) {
            fillDataCell(currentRow, columnIndex++, processBMRate(invoice.getBenchmark()));
            fillDataCell(currentRow, columnIndex++, processGainShareCost(invoice.getGainShareCost()));
            fillDataCell(currentRow, columnIndex, getTotalDue(invoice.getTotalRevenue(), invoice.getGainShareCost()));
        }
    }

    private void addTotals(int rowIndex) {
        Row currentRow = sheet.createRow(rowIndex);
        fillDataCellBoldValue(currentRow, 0, "Grand Total : ");
        int columnIdx = 0;

        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalResidentialPickupCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalLiftGatePickupCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalInsidePickupCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalOverDimensionCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalBlindBolCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalLimitedAccessPickupCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalResidentialDeliveryCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalLiftGateDeliveryCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalInsideDeliveryCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalSortSegregateCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalNotifyCost);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalLimitedAccessDeliveryCost);

        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalLinehaul);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalFuelSurcharge);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalTransactionFee);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalOtherAccessorials);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalAllAccessorials);
        fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalInvoice);

        if (isGainshare) {
            fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalBMRate);
            fillDataCell(currentRow, totalsColumns.get(columnIdx++), totalGainShareSaving);
            fillDataCell(currentRow, totalsColumns.get(columnIdx), totalInvoiceAdjusted);
        }
    }

    private BigDecimal processResinedtialCost(BigDecimal cost) {
        totalResidentialPickupCost = totalResidentialPickupCost.add(cost);
        return cost;
    }

    private BigDecimal processLiftgateCost(BigDecimal cost) {
        totalLiftGatePickupCost = totalLiftGatePickupCost.add(cost);
        return cost;
    }

    private BigDecimal processInsidePickupCost(BigDecimal cost) {
        totalInsidePickupCost = totalInsidePickupCost.add(cost);
        return cost;
    }

    private BigDecimal processOverDimensionCost(BigDecimal cost) {
        totalOverDimensionCost = totalOverDimensionCost.add(cost);
        return cost;
    }

    private BigDecimal processBlindBolCost(BigDecimal cost) {
        totalBlindBolCost = totalBlindBolCost.add(cost);
        return cost;
    }

    private BigDecimal processLimitedAccessPickupCost(BigDecimal cost) {
        totalLimitedAccessPickupCost = totalLimitedAccessPickupCost.add(cost);
        return cost;
    }

    private BigDecimal processResidentialDeliveryCost(BigDecimal cost) {
        totalResidentialDeliveryCost = totalResidentialDeliveryCost.add(cost);
        return cost;
    }

    private BigDecimal processLiftGateDeliveryCost(BigDecimal cost) {
        totalLiftGateDeliveryCost = totalLiftGateDeliveryCost.add(cost);
        return cost;
    }

    private BigDecimal processInsideDeliveryCost(BigDecimal cost) {
        totalInsideDeliveryCost = totalInsideDeliveryCost.add(cost);
        return cost;
    }

    private BigDecimal processSortSegregateCost(BigDecimal cost) {
        totalSortSegregateCost = totalSortSegregateCost.add(cost);
        return cost;
    }

    private BigDecimal processNotifyCost(BigDecimal cost) {
        totalNotifyCost = totalNotifyCost.add(cost);
        return cost;
    }

    private BigDecimal processLimitedAccessDeliveryCost(BigDecimal cost) {
        totalLimitedAccessDeliveryCost = totalLimitedAccessDeliveryCost.add(cost);
        return cost;
    }

    private BigDecimal processLineHaul(BigDecimal lineHaul) {
        totalLinehaul = totalLinehaul.add(lineHaul);
        return lineHaul;
    }

    private BigDecimal processTransactionFee(BigDecimal transactionFee) {
        totalTransactionFee = totalTransactionFee.add(transactionFee);
        return transactionFee;
    }

    private BigDecimal processOthersAccessorials(BigDecimal otherAccesorials) {
        totalOtherAccessorials = totalOtherAccessorials.add(otherAccesorials);
        return otherAccesorials;
    }

    private BigDecimal processAllAccessorials(BigDecimal cost) {
        totalAllAccessorials = totalAllAccessorials.add(cost);
        return cost;
    }

    private BigDecimal processFuelSurcharge(BigDecimal fuelSurcharge) {
        totalFuelSurcharge = totalFuelSurcharge.add(fuelSurcharge);
        return fuelSurcharge;
    }

    private BigDecimal processTotalLoad(BigDecimal totalLoad) {
        totalInvoice = totalInvoice.add(totalLoad);
        return totalLoad;
    }

    private BigDecimal processGainShareCost(BigDecimal gainShareCost) {
        totalGainShareSaving = totalGainShareSaving.add(gainShareCost);
        return gainShareCost;
    }

    private BigDecimal processBMRate(BigDecimal cost) {
        totalBMRate = totalBMRate.add(cost);
        return cost;
    }

    private BigDecimal getTotalDue(BigDecimal totalRevenue, BigDecimal gainShareCost) {
        BigDecimal result = totalRevenue.subtract(gainShareCost);
        totalInvoiceAdjusted = totalInvoiceAdjusted.add(result);
        return result;
    }

    private void fillDataCellBoldValue(Row currentRow, int columnIndex, String value) {
        Cell cell = currentRow.createCell(columnIndex);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        CellStyle boldStyle = workbook.createCellStyle();
        boldStyle.setFont(boldFont);

        cell.setCellValue(StringUtils.trimToEmpty(value));
        cell.setCellStyle(boldStyle);
    }

    private void fillDataCell(Row currentRow, int columnIndex, String value) {
        currentRow.createCell(columnIndex).setCellValue(StringUtils.trimToEmpty(value));
    }

    private void fillDataCell(Row currentRow, int columnIndex, Date value) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(DateUtils.toCalendar(value));
        }
        cell.setCellStyle(dateCellStyle);
    }

    private void fillDataCell(Row currentRow, int columnIndex, BigDecimal value) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        cell.setCellStyle(currencyCellStyle);
    }

    /**
     * Sheet Totals.
     * 
     * @author Artem Arapov
     *
     */
    public static class SheetTotals {
        private BigDecimal totalLinehaul = BigDecimal.ZERO;
        private BigDecimal totalTransactionFee = BigDecimal.ZERO;
        private BigDecimal totalFuelSurcharge = BigDecimal.ZERO;
        private BigDecimal totalOtherAccessorials = BigDecimal.ZERO;
        private BigDecimal totalAllAccessorials = BigDecimal.ZERO;
        private BigDecimal totalGainshare = BigDecimal.ZERO;
        private boolean empty = true;

        public BigDecimal getTotalLinehaul() {
            return totalLinehaul;
        }

        public BigDecimal getTotalTransactionFee() {
            return totalTransactionFee;
        }

        public BigDecimal getTotalFuelSurcharge() {
            return totalFuelSurcharge;
        }

        public BigDecimal getTotalOtherAccessorials() {
            return totalOtherAccessorials;
        }

        public BigDecimal getTotalAllAccessorials() {
            return totalAllAccessorials;
        }

        public BigDecimal getTotalGainshare() {
            return totalGainshare;
        }

        public BigDecimal getTotalInvoice() {
            return totalLinehaul.add(totalFuelSurcharge).add(totalAllAccessorials).add(totalTransactionFee);
        }

        public boolean isEmpty() {
            return empty;
        }
    }

    private enum SheetName {
        INBOUND(1),
        OUTBOUND(2);

        final int idx;

        SheetName(int idx) {
            this.idx = idx;
        }
    }
}
