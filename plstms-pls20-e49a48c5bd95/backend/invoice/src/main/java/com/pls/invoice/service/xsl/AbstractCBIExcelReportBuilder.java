package com.pls.invoice.service.xsl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Abstract report builder for CBI reports.
 * 
 * @author Alexander Nalapko
 *
 */
abstract class AbstractCBIExcelReportBuilder extends AbstractReportExcelBuilder {

    private DataFormat dataFormat;

    /**
     * Constructor.
     * 
     * @param revenueReportTemplate
     *            resource
     * @throws IOException
     *             error
     */
    protected AbstractCBIExcelReportBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
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
    public void generateReport(BillToEntity billTo, List<LoadAdjustmentBO> invoices, Date invoiceDate,
            String invoiceNumber, OutputStream outputStream) throws IOException {

        dataFormat = workbook.createDataFormat();

        int dueDays = billTo.getPlsCustomerTerms() != null ? billTo.getPlsCustomerTerms().getDueDays() : 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(invoiceDate);
        calendar.add(Calendar.DAY_OF_YEAR, dueDays);
        Date dueDate = calendar.getTime();

        fillMainSheet(billTo, invoiceNumber, invoiceDate, dueDate);
        buildReport(billTo, invoices);

        try {
            workbook.write(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Transform {@link LoadEntity} or {@link FinancialAccessorialsEntity} into {@link CBIReportRowAdapter}.
     * 
     * @param invoice - invoice data
     * @return data - CBIReportRowAdapter
     */
    public CBIReportRowAdapter getCBIReportAdapter(LoadAdjustmentBO invoice) {
        return invoice.getAdjustment() == null
                ? new CBIReportRowAdapter(invoice.getLoad()) : new CBIReportRowAdapter(invoice.getAdjustment());
    }

    /**
     * Fill information in some page.
     * 
     * @param beenList
     *            list of data
     * @param sheetIndex
     *            number of sheet in document.
     */
    protected void fillSheet(List<List<Object>> beenList, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int rowIndex = 1;
        for (List<Object> been : beenList) {
            int cellIndex = 0;
            for (Object field : been) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                fillDataCell(row, cellIndex, field);
                cellIndex++;
            }
            rowIndex++;
        }
    }

    /**
     * Build body of report here.
     *
     * @param billTo
     *            billing data
     * @param invoices
     *            - loads and adjustments sorted in the order that they should appear in the invoice.
     */
    abstract void buildReport(BillToEntity billTo, List<LoadAdjustmentBO> invoices);

    /**
     * Fill information on main page.
     */
    private void fillMainSheet(BillToEntity billTo, String invoiceNumber, Date invoiceDate, Date dueDate) {
        if (workbook != null) {
            workbook.setActiveSheet(0);
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
            dateCellStyle.setDataFormat(dataFormat.getFormat(DATE_FORMAT));

            fillDataCell(mainSheet.getRow(3), 10, invoiceNumber, style); // invoice number
            fillDataCell(mainSheet.getRow(4), 10, invoiceDate, dateCellStyle); // invoice date
            fillDataCell(mainSheet.getRow(5), 10, dueDate, dateCellStyle); // due date
            fillDataCell(mainSheet.getRow(19), 7, invoiceDate, dateCellStyle); // invoice date

            fillDataCell(mainSheet.getRow(12), 3, billTo.getName(), style); // BillTo name
            fillDataCell(mainSheet.getRow(13), 3, billTo.getBillingInvoiceNode().getAddress().getAddress1(), style); // BillTo
                                                                                                                     // address
            fillDataCell(mainSheet.getRow(14), 3, getAddressString(billTo.getBillingInvoiceNode().getAddress()), style); // BillTo
                                                                                                                         // zip
        }
    }

    private String getAddressString(AddressEntity address) {
        String state = StringUtils.isNotBlank(address.getStateCode()) ? ", " + address.getStateCode() : "";
        return StringUtils.join(address.getCity(), state, ", ", address.getZip());
    }
}
