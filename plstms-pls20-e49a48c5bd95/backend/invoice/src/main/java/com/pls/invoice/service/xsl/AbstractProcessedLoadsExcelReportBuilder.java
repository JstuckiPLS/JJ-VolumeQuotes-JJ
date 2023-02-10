/**
 * 
 */
package com.pls.invoice.service.xsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.invoice.domain.bo.ProcessedLoadsReportBO;

/**
 * Abstract processed Load report excel builder.
 * 
 * @author Alexander Nalapko
 *
 */
public abstract class AbstractProcessedLoadsExcelReportBuilder {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Workbook workbook;
    protected final Sheet mainSheet;

    protected int rowIndex = 6;

    /**
     * Invoice builder constructor.
     * 
     * @param resource
     *            - template.
     * @throws IOException
     *             - if can't generate report
     */
    public AbstractProcessedLoadsExcelReportBuilder(ClassPathResource resource) throws IOException {
        InputStream template = resource.getInputStream();
        workbook = new XSSFWorkbook(template);
        mainSheet = workbook.getSheetAt(0);

    }

    /**
     * Generates invoice report as xlsx file.
     *
     * @param bo
     *            - ProcessedLoadsReportBO.
     * @throws IOException
     *             - if can't generate report
     * @return file - FileInputStreamResponseEntity
     */
    public FileInputStreamResponseEntity generateReport(ProcessedLoadsReportBO bo) throws IOException {
        File tempFile = File.createTempFile("reportData", "tmp");

        buildReport(bo);
        try {
            workbook.write(new FileOutputStream(tempFile));
            return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), "temp.xlsx");
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    /**
     * Write data into rows and cells.
     * 
     * @param bo
     *            - ProcessedLoadsReportBO.
     */
    abstract void buildReport(ProcessedLoadsReportBO bo);

    /**
     * Add or replace value in cell.
     * 
     * @param currentRow
     *            current row
     * @param columnIndex
     *            column index
     * @param newValue
     *            <String> value
     * @param subStr
     *            <String> string for replacing
     * @param style
     *            <CellStyle>
     * @param newValueFont
     *            Font for new text
     */
    protected void fillCellValue(Row currentRow, int columnIndex, String newValue, String subStr, CellStyle style,
            Font newValueFont) {
        String newValueString = "";
        if (newValue != null) {
            newValueString = newValue;
        }
        Cell cell = currentRow.getCell(columnIndex);
        XSSFRichTextString richTextString = (XSSFRichTextString) cell.getRichStringCellValue();
        if (subStr != null) {
            int index = richTextString.getString().indexOf(subStr);
            if (index == -1) {
                return;
            }
            Font font = null;
            try {
                font = richTextString.getFontAtIndex(index);
            } catch (Exception e) {
                log.error("Font not found");
            }
            richTextString.setString(richTextString.getString().replace(subStr, newValueString));
            if (newValueFont != null) {
                richTextString.applyFont(index, index + newValueString.length(), newValueFont);
            } else if (font != null) {
                richTextString.applyFont(index, index + newValueString.length(), font);
            }
        }
        cell.setCellValue(richTextString);
        if (style != null) {
            cell.setCellStyle(style);
        }

    }

    /**
     * Fill data cell.
     *
     * @param currentRow
     *            the current row
     * @param column
     *            the column index
     * @param value
     *            the value
     */
    protected void fillDataCell(Row currentRow, int column, String value) {
        currentRow.createCell(column).setCellValue(StringUtils.trimToEmpty(value));
    }
}
