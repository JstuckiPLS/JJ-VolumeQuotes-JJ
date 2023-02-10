package com.pls.export;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pls.core.exception.ApplicationException;

/**
 * Data builder for export.
 * 
 * @author Sergey Kirichenko
 * @author Mykola Teslenko
 */
public class ExcelDataBuilder {

    private static final String CURRENCY_FORMAT = "\"$\"#,##0.00";
    private static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, new SimpleDateFormat("EEE MM/dd/yyyy", Locale.US));

    private final Workbook workbook = new XSSFWorkbook();
    private MessageFormat exportFileName;

    private final ExportData exportData;
    private Row currentRow;
    private Font fontForSelection;
    private CellStyle styleForSelection;

    /**
     * Default constructor.
     * 
     * @param exportData
     *            ExportData object with file name, column names and data
     */
    public ExcelDataBuilder(ExportData exportData) {
        this.exportData = exportData;

        exportFileName = new MessageFormat(exportData.getFileName());
        CellStyle currencyCellStyle = workbook.createCellStyle();
        DataFormat df = workbook.createDataFormat();
        currencyCellStyle.setDataFormat(df.getFormat(CURRENCY_FORMAT));
    }

    /**
     * Build Excel file for specified shipment orders.
     * 
     * @param outputStream
     *            output stream
     * @throws java.io.IOException
     *             Unable to opener data.
     * @throws ApplicationException
     *             when can't prepare document
     */
    public void buildExcelData(OutputStream outputStream) throws IOException, ApplicationException {
        try {
            Sheet sheet = workbook.createSheet(exportData.getSheetName());

            DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(dataFormat.getFormat(DATE_FORMAT));

            CellStyle styleWithBorder = getTableCellStyle();

            int currentRowIndex = 0;
            int headerDataSize = 0;
            int footerDataSize = 0;

            if (exportData.getHeaderData() != null) {
                headerDataSize = exportData.getHeaderData().size();
            }
            currentRowIndex = biuldHeader(sheet, currentRowIndex, headerDataSize);
            int nuberOfEmptyRowsAfterHeader = 0;
            if (exportData.getHeaderData() != null) {
                nuberOfEmptyRowsAfterHeader = 1;
                currentRowIndex += nuberOfEmptyRowsAfterHeader;
            }
            currentRow = sheet.createRow(currentRowIndex);
            fillHeaderRow(styleWithBorder);
            currentRowIndex++;
            int dataSize = 0;
            if (exportData.getData() != null) {
                dataSize = exportData.getData().size();
            }
            currentRowIndex = buildTable(sheet, styleWithBorder, currentRowIndex, headerDataSize, nuberOfEmptyRowsAfterHeader, dataSize);
            int nuberOfEmptyRowsAfterTable = 1;
            currentRowIndex += nuberOfEmptyRowsAfterTable;
            if (exportData.getFooterData() != null) {
                footerDataSize = exportData.getFooterData().size();
            }
            buildFooter(sheet, currentRowIndex, headerDataSize, footerDataSize, nuberOfEmptyRowsAfterHeader, dataSize, nuberOfEmptyRowsAfterTable);
            workbook.write(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private CellStyle getTableCellStyle() {
        CellStyle styleWithBorder = workbook.createCellStyle();
        styleWithBorder.setBorderBottom(BorderStyle.THIN);
        styleWithBorder.setBorderTop(BorderStyle.THIN);
        styleWithBorder.setBorderLeft(BorderStyle.THIN);
        styleWithBorder.setBorderRight(BorderStyle.THIN);
        return styleWithBorder;

    }

    private int buildTable(Sheet sheet, CellStyle styleWithBorder, int rowIndex, int headerDataSize, int nuberOfEmptyRowsAfterHeader,
            int dataSize) {
        int currentRowIndex = rowIndex;
        for (; currentRowIndex < dataSize + headerDataSize + 1 + nuberOfEmptyRowsAfterHeader; currentRowIndex++) {
            currentRow = sheet.createRow(currentRowIndex);
            ExcelRow data = exportData.getData().get(currentRowIndex - headerDataSize - 1 - nuberOfEmptyRowsAfterHeader);
            CellStyle style = styleWithBorder;
            if (data.isMarked()) {
                if (fontForSelection == null) {
                    Font normalFont = workbook.getFontAt(styleWithBorder.getFontIndex());
                    fontForSelection = workbook.createFont();
                    fontForSelection.setFontHeightInPoints(normalFont.getFontHeightInPoints());
                    fontForSelection.setFontName(normalFont.getFontName());
                    fontForSelection.setColor(normalFont.getColor());
                    fontForSelection.setBold(true);
                    styleForSelection = getTableCellStyle();
                    styleForSelection.setFont(fontForSelection);
                    //http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
                    //Changing background color of the cell is a bit tricky
                    styleForSelection.setFillForegroundColor(HSSFColor.YELLOW.index);
                    styleForSelection.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                style = styleForSelection;
            }
            fillDataRow(data, style);
        }
        return currentRowIndex;
    }

    private void buildFooter(Sheet sheet, int rowIndex, int headerDataSize, int footerDataSize, int nuberOfEmptyRowsAfterHeader, int dataSize,
            int nuberOfEmptyRowsAfterTable) throws ApplicationException {
        int currentRowIndex = rowIndex;
        int upperBoundary = headerDataSize + dataSize + 1 + nuberOfEmptyRowsAfterHeader + nuberOfEmptyRowsAfterTable + footerDataSize;
        for (; currentRowIndex < upperBoundary; currentRowIndex++) {
            currentRow = sheet.createRow(currentRowIndex);
            fillFooterRow(exportData.getFooterData().get(currentRowIndex - dataSize - headerDataSize - 1
                    - nuberOfEmptyRowsAfterTable - nuberOfEmptyRowsAfterHeader), null);
        }
    }

    private int biuldHeader(Sheet sheet, int rowIndex, int headerDataSize) {
        int currentRowIndex = rowIndex;
        for (; currentRowIndex < headerDataSize; currentRowIndex++) {
            currentRow = sheet.createRow(currentRowIndex);
            List<String> data = exportData.getHeaderData().get(currentRowIndex);
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null) {
                    fillDataCell(i, data.get(i), null);
                }
            }

        }
        return currentRowIndex;
    }

    /**
     * Build file name for result document.
     * 
     * @return Not <code>null</code> {@link String}.
     */
    public String buildFileName() {
        Date date = new Date();
        return buildFileName(date);
    }

    /**
     * Build file name for specified date.
     * 
     * @param date
     *            Not <code>null</code> {@link java.util.Date}.
     * @return Not <code>null</code> {@link String}.
     */
    public String buildFileName(Date date) {
        return getExportFileName().format(new Object[] { date });
    }

    /**
     * Fill row of exported document with data.
     * 
     * @param data
     *            data to fill at row
     */
    private void fillDataRow(ExcelRow data, CellStyle style) {
        fillRow(data.getRowData(), style);
    }

    /**
     * Fill row with totals information if needed.
     * 
     * @param data
     *            data to fill at row
     * @throws ApplicationException
     *             when can't prepare footer row
     */
    private void fillFooterRow(List<String> data, CellStyle style) throws ApplicationException {
        fillRow(data, style);
    }

    private void fillRow(List<String> data, CellStyle style) {
        if (!data.isEmpty()) {
            for (int i = 0; i < exportData.getColumnNames().size(); i++) {
                if (data.get(i) != null) {
                    fillDataCell(i, data.get(i), style);
                } else {
                    fillDataCell(i, "", style);
                }
            }
        }
    }

    /**
     * Fill exported document header.
     */
    private void fillHeaderRow(CellStyle style) {
        for (int i = 0; i < exportData.getColumnNames().size(); i++) {
            Cell cell = currentRow.createCell(i);
            if (style != null) {
                cell.setCellStyle(style);
            }
            cell.setCellValue(exportData.getColumnNames().get(i));
        }
    }

    /**
     * Fill data cell with value of type {@link String}.
     * 
     * @param columnIndex
     *            index of column
     * @param value
     *            value to set
     * @param style
     *            cell style
     */
    private void fillDataCell(int columnIndex, String value, CellStyle style) {
        Cell cell = currentRow.createCell(columnIndex);
        if (style != null) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(StringUtils.trimToEmpty(value));
    }

    public void setExportFileName(MessageFormat exportFileName) {
        this.exportFileName = exportFileName;
    }

    public MessageFormat getExportFileName() {
        return exportFileName;
    }

}
