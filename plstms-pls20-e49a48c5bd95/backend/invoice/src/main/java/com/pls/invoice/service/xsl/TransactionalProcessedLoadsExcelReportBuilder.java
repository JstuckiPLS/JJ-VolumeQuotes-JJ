package com.pls.invoice.service.xsl;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.invoice.domain.bo.InvoiceResultBO;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;

/**
 * Transactional processed Load report excel builder.
 * 
 * @author Alexander Nalapko
 *
 */
public class TransactionalProcessedLoadsExcelReportBuilder extends AbstractProcessedLoadsExcelReportBuilder {

    /**
     * Invoice CBI builder constructor.
     * 
     * @param resource
     *            - template.
     * @throws IOException
     *             exception
     */
    public TransactionalProcessedLoadsExcelReportBuilder(ClassPathResource resource) throws IOException {
        super(resource);
    }

    /**
     * Write data into rows and cells.
     * 
     * @param bo
     *            - ProcessedLoadsReportBO.
     */
    void buildReport(ProcessedLoadsReportBO bo) {
        buildHeader(bo);
        if (bo.getLoads() != null) {
            for (InvoiceResultBO invoice : bo.getLoads()) {
                buildRow(invoice, rowIndex++);
            }
        }
    }

    private void buildHeader(ProcessedLoadsReportBO bo) {
        Font font = workbook.createFont();
        font.setBold(true);
        fillCellValue(mainSheet.getRow(0), 2, bo.getFailed(), "{failed}", null, font);
        fillCellValue(mainSheet.getRow(1), 0, bo.getCustomer(), "{customer}", null, font);
        fillCellValue(mainSheet.getRow(1), 2, bo.getBillTo(), "{bill_to}", null, font);
        fillCellValue(mainSheet.getRow(1), 4, bo.getEmail(), "{email_to}", null, font);
        fillCellValue(mainSheet.getRow(0), 0, bo.getSuccessful(), "{successful}", null, font);
        fillCellValue(mainSheet.getRow(2), 0, bo.getSubject(), "{subject}", null, font);
        fillCellValue(mainSheet.getRow(3), 0, bo.getComments(), "{comments}", null, font);
    }

    private void buildRow(InvoiceResultBO rowData, int rowIndex) {
        workbook.setActiveSheet(0);
        int columnIndex = 0;
        Row currentRow = mainSheet.createRow(rowIndex);
        if (BooleanUtils.isTrue(rowData.getDoNotInvoice())) {
            fillDataCell(currentRow, columnIndex++, "Don't invoice");
        } else if (rowData.getAdjustmentId() != null) {
            fillDataCell(currentRow, columnIndex++, "Adj");
        } else {
            fillDataCell(currentRow, columnIndex++, "");
        }

        fillDataCell(currentRow, columnIndex++, rowData.getInvoiceNumber());
        fillDataCell(currentRow, columnIndex++, rowData.getLoadId().toString());
        fillDataCell(currentRow, columnIndex++, rowData.getBol());
        fillDataCell(currentRow, columnIndex++, rowData.getErrorMessage() != null ? "Failed" : "Successful");
        fillDataCell(currentRow, columnIndex++, rowData.getErrorMessage());
    }
}
