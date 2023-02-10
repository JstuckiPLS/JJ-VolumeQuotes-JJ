package com.pls.invoice.service.xsl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.invoice.service.xsl.StandardSheetBuilder.SheetTotals;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * CBI Report builder.
 *
 * @author Sergey Kirichenko
 */
public class CBIStandardExcelReportBuilder {
    private static final int IO_COL_START_IN_MAIN_PAGE = 7;

    private static final String CURRENCY_FORMAT = "\"$\"#,##0.00";

    private static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, new SimpleDateFormat("MM/dd/yyyy", Locale.US));

    private final boolean gainShare;

    private final Workbook workbook;

    private final Sheet mainInfoSheet;

    private final CellStyle currencyCellStyle;
    private final CellStyle dateCellStyle;

    /**
     * CBI report builder constructor.
     *
     * @param gainShare - boolean parameter show whether it common or gainshare report
     * @param commonTemplate - common template
     * @param gainShareTemplate - gain/share template
     * @throws IOException if can't generate report
     */
    public CBIStandardExcelReportBuilder(boolean gainShare, ClassPathResource commonTemplate, ClassPathResource gainShareTemplate)
            throws IOException {
        this.gainShare = gainShare;
        InputStream template = this.gainShare ? gainShareTemplate.getInputStream() : commonTemplate.getInputStream();
        workbook = new XSSFWorkbook(template);

        mainInfoSheet = workbook.getSheetAt(0);
        mainInfoSheet.setDisplayGridlines(false);

        DataFormat df = workbook.createDataFormat();
        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat(df.getFormat(CURRENCY_FORMAT));
        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(df.getFormat(DATE_FORMAT));
    }

    /**
     * Generates CBI report as xls file.
     *
     * @param billTo
     *            billing data
     * @param invoices
     *            - loads and adjustments sorted in the order that they should appear in the invoice.
     * @param invoiceDate
     *            - date of invoice
     * @param invoiceNumber
     *            - generated invoice number
     * @param outputStream
     *            output stream of generated report
     * @throws IOException
     *             if can't generate report
     */
    public void generateReport(BillToEntity billTo, List<LoadAdjustmentBO> invoices, Date invoiceDate, String invoiceNumber,
            OutputStream outputStream) throws IOException {
        int dueDays = billTo.getPlsCustomerTerms() != null ? billTo.getPlsCustomerTerms().getDueDays() : 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(invoiceDate);
        calendar.add(Calendar.DAY_OF_YEAR, dueDays);
        Date dueDate = calendar.getTime();

        List<CBIReportRowAdapter> cbiReportList = transformList(invoices);

        SheetTotals inboundTotals = buildInboundSheet(cbiReportList);
        SheetTotals outboundTotals = buildOutboundSheet(cbiReportList);

        fillMainSheet(billTo, invoiceNumber, invoiceDate, dueDate, inboundTotals, outboundTotals);

        if (inboundTotals.isEmpty() && workbook.getSheetAt(1) != null) {
            workbook.setSheetHidden(1, 1);
        } else if (outboundTotals.isEmpty() && workbook.getSheetAt(2) != null) {
            workbook.setSheetHidden(2, 1);
        }

        try {
            workbook.write(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private List<CBIReportRowAdapter> transformList(List<LoadAdjustmentBO> invoices) {
        Function<LoadAdjustmentBO, CBIReportRowAdapter> transformFunction = new Function<LoadAdjustmentBO, CBIReportRowAdapter>() {

            @Override
            public CBIReportRowAdapter apply(LoadAdjustmentBO input) {
                CBIReportRowAdapter rowAdapter = input.getAdjustment() == null ? new CBIReportRowAdapter(input.getLoad())
                : new CBIReportRowAdapter(input.getAdjustment());

                return rowAdapter;
            }
        };

        return Lists.transform(invoices, transformFunction);
    }

    private Iterable<CBIReportRowAdapter> getInboundList(List<CBIReportRowAdapter> list) {
        Predicate<CBIReportRowAdapter> inboundPredicate = new Predicate<CBIReportRowAdapter>() {

            @Override
            public boolean apply(CBIReportRowAdapter input) {
                return input.getShipmentDirection().equalsIgnoreCase(ShipmentDirection.INBOUND.getDescription());
            }
        };

        return Iterables.filter(list, inboundPredicate);
    }

    private Iterable<CBIReportRowAdapter> getOutboundList(List<CBIReportRowAdapter> list) {
        Predicate<CBIReportRowAdapter> outboundPredicate = new Predicate<CBIReportRowAdapter>() {

            @Override
            public boolean apply(CBIReportRowAdapter input) {
                return input.getShipmentDirection().equalsIgnoreCase(ShipmentDirection.OUTBOUND.getDescription());
            }
        };

        return Iterables.filter(list, outboundPredicate);
    }

    private SheetTotals buildInboundSheet(List<CBIReportRowAdapter> invoices) {
        Iterable<CBIReportRowAdapter> inboundsList = getInboundList(invoices);
        return gainShare ? StandardSheetBuilder.buildInboundGainshareSheet(workbook, inboundsList)
                : StandardSheetBuilder.buildInboundSheet(workbook, inboundsList);
    }

    private SheetTotals buildOutboundSheet(List<CBIReportRowAdapter> invoices) {
        Iterable<CBIReportRowAdapter> outboundList = getOutboundList(invoices);
        return gainShare ? StandardSheetBuilder.buildOutboundGainshareSheet(workbook, outboundList)
                : StandardSheetBuilder.buildOutboundSheet(workbook, outboundList);
    }

    private void fillMainSheet(BillToEntity billTo, String invoiceNumber, Date invoiceDate, Date dueDate,
            SheetTotals inboundTotals, SheetTotals outboundTotals) {
        if (workbook != null) {
            workbook.setActiveSheet(0);

            fillDataCell(mainInfoSheet.getRow(3), 10, invoiceNumber); //invoice number
            fillDataCell(mainInfoSheet.getRow(4), 10, invoiceDate); //invoice date
            fillDataCell(mainInfoSheet.getRow(5), 10, dueDate); //due date

            fillDataCell(mainInfoSheet.getRow(12), 3, billTo.getName()); //BillTo name
            fillDataCell(mainInfoSheet.getRow(13), 3, billTo.getBillingInvoiceNode().getAddress().getAddress1()); //BillTo address
            fillDataCell(mainInfoSheet.getRow(14), 3, getAddressString(billTo.getBillingInvoiceNode().getAddress())); //BillTo zip

            fillDataCell(mainInfoSheet.getRow(19), 7, invoiceDate);

            calculateTotalsAndFillMainSheet(IO_COL_START_IN_MAIN_PAGE, inboundTotals, outboundTotals);

            workbook.setActiveSheet(0);
        }
    }

    private void calculateTotalsAndFillMainSheet(int inboundOutboundColumnPosition, SheetTotals inboundTotals, SheetTotals outboundTotals) {
        int tempInboundOutboundColumnPosition = inboundOutboundColumnPosition;

        if (!inboundTotals.isEmpty()) {
            fillDataCellBoldUnderlineCenterValue(mainInfoSheet.getRow(24),
                    tempInboundOutboundColumnPosition, workbook.getSheetName(1).toUpperCase()); // Inbound Label
            populateSheetTotals(tempInboundOutboundColumnPosition, inboundTotals);

            tempInboundOutboundColumnPosition = tempInboundOutboundColumnPosition + 1;
        }

        if (!outboundTotals.isEmpty()) {
            fillDataCellBoldUnderlineCenterValue(mainInfoSheet.getRow(24),
                    tempInboundOutboundColumnPosition, workbook.getSheetName(2).toUpperCase()); // Outbound

            populateSheetTotals(tempInboundOutboundColumnPosition, outboundTotals);

            tempInboundOutboundColumnPosition = tempInboundOutboundColumnPosition + 1;
        }

        populateInvoiceTotals(tempInboundOutboundColumnPosition, inboundTotals, outboundTotals);
    }

    private void populateSheetTotals(int inboundOutboundColumnPosition, SheetTotals totals) {
        fillDataCell(mainInfoSheet.getRow(26), inboundOutboundColumnPosition, totals.getTotalLinehaul());
        fillDataCell(mainInfoSheet.getRow(27), inboundOutboundColumnPosition, totals.getTotalFuelSurcharge());
        fillDataCell(mainInfoSheet.getRow(28), inboundOutboundColumnPosition, totals.getTotalTransactionFee());
        fillDataCell(mainInfoSheet.getRow(29), inboundOutboundColumnPosition, totals.getTotalAllAccessorials());
        if (gainShare) {
            fillDataCell(mainInfoSheet.getRow(30), inboundOutboundColumnPosition, totals.getTotalGainshare());
            fillDataCell(mainInfoSheet.getRow(32), inboundOutboundColumnPosition, totals.getTotalInvoice());
        } else {
            fillDataCell(mainInfoSheet.getRow(31), inboundOutboundColumnPosition, totals.getTotalInvoice());
        }
    }

    private void populateInvoiceTotals(int columndIdx, SheetTotals inbound, SheetTotals outbound) {
        fillDataCellBoldUnderlineCenterValue(mainInfoSheet.getRow(24), columndIdx, "TOTAL"); // Total Label

        BigDecimal totalLinehaul = inbound.getTotalLinehaul().add(outbound.getTotalLinehaul());
        BigDecimal totalFuelSurcharge = inbound.getTotalFuelSurcharge().add(outbound.getTotalFuelSurcharge());
        BigDecimal totalTransactionFee = inbound.getTotalTransactionFee().add(outbound.getTotalTransactionFee());
        BigDecimal totalAllAccessorials = inbound.getTotalAllAccessorials().add(outbound.getTotalAllAccessorials());
        BigDecimal totalGainshare = inbound.getTotalGainshare().add(outbound.getTotalGainshare());
        BigDecimal totalInvoice = totalLinehaul.add(totalFuelSurcharge).add(totalAllAccessorials).add(totalTransactionFee);

        fillDataCell(mainInfoSheet.getRow(26), columndIdx, totalLinehaul);
        fillDataCell(mainInfoSheet.getRow(27), columndIdx, totalFuelSurcharge);
        fillDataCell(mainInfoSheet.getRow(28), columndIdx, totalTransactionFee);
        fillDataCell(mainInfoSheet.getRow(29), columndIdx, totalAllAccessorials);
        if (gainShare) {
            fillDataCell(mainInfoSheet.getRow(30), columndIdx, totalGainshare);
            fillDataCell(mainInfoSheet.getRow(32), columndIdx, totalInvoice);
        } else {
            fillDataCell(mainInfoSheet.getRow(31), columndIdx, totalInvoice);
        }
    }

    private String getAddressString(AddressEntity address) {
        String state = StringUtils.isNotBlank(address.getStateCode()) ? ", " + address.getStateCode() : "";
        return address.getCity() + state + ", " + address.getZip();
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

    private void fillDataCell(Row currentRow, int columnIndex, Date value) {
        Cell cell = currentRow.createCell(columnIndex);
        if (value != null) {
            cell.setCellValue(DateUtils.toCalendar(value));
        }
        cell.setCellStyle(dateCellStyle);
    }

    private void fillDataCell(Row currentRow, int columnIndex, String value) {
        currentRow.createCell(columnIndex).setCellValue(StringUtils.trimToEmpty(value));
    }

    private void fillDataCellBoldUnderlineCenterValue(Row currentRow, int columnIndex, String value) {
        Cell cell = currentRow.createCell(columnIndex);

        Font boldUnderlineCenterFont = workbook.createFont();
        boldUnderlineCenterFont.setUnderline(HSSFFont.U_DOUBLE);
        boldUnderlineCenterFont.setBold(true);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(boldUnderlineCenterFont);

        cell.setCellValue(StringUtils.trimToEmpty(value));
        cell.setCellStyle(cellStyle);
    }
}
