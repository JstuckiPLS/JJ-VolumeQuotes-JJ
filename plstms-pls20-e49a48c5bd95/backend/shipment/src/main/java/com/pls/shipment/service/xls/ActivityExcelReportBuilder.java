package com.pls.shipment.service.xls;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.bo.AccessorialReportBO;
import com.pls.shipment.domain.bo.ActivityReportsBO;
import com.pls.shipment.domain.bo.ProductReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Excel Report Builder.
 * 
 * @author Sergii Belodon
 * 
 */
public class ActivityExcelReportBuilder extends AbstractReportExcelBuilder {
    private static final String INDEX_MARKER = "{index}";
    private static final int LAST_INDEX_OF_ACCESORIAL_TABLE = 9;
    private static final int NUMBER_OF_ACCESSORIALS_COLUMNS = 3;
    private static final int LOADS_TABLE_START_INDEX = 13;
    private static final int HEADER_ROW_INDEX = 9;
    private static final String REPORT_RUN_DATETIME_MARKER = "REPORT_RUN_DATETIME";
    private static final String END_DATE_MARKER = "END_DATE";
    private static final String START_DATE_MARKER = "START_DATE";
    private static final String SUBJECT_TYPE_MARKER = "SUBJECT_TYPE";
    private static final String SUBJECT_NAME_MARKER = "SUBJECT_NAME";
    private static final String TOTAL_COST_MARKER = "TOTAL_COST";
    private static final String LOAD_COUNT_MARKER = "LOAD_COUNT";
    private static final int FIRST_PRODUCTS_COLUMN = 29;
    private static final int PRODUCT_COLUMNS_QUANTITY = 4;
    protected static final String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm a";

    /**
     * Instantiates a new report excel builder2.
     *
     * @param revenueReportTemplate the revenue report template
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ActivityExcelReportBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    /**
     * Generates revenue report as xlsx file.
     */
    public FileInputStreamResponseEntity generateReport(List<ActivityReportsBO> items, Map<String, String> allAccessorials, ReportParamsBO reportParams, BigDecimal totalCost) throws IOException {
        fillSheet(items, allAccessorials, reportParams, totalCost);
        return super.generateReport(ReportUtil.getSubjectName(reportParams), reportParams.getStartDate(), reportParams.getEndDate());
    }

    private void fillSheet(List<ActivityReportsBO> reports, Map<String, String> allAccessorials, ReportParamsBO reportParams, BigDecimal totalCost) {
        workbook.setActiveSheet(0);
        fillHeader(reports, reportParams, totalCost);
        int maxProductSize = getMaxProductSize(reports);
        Row headerRow = mainSheet.getRow(HEADER_ROW_INDEX);
        int rowIndex = prepareLoadsTableForFilling(reports, maxProductSize, headerRow);
        rowIndex = fillLoadsTable(reports, rowIndex, maxProductSize);
        fillAccessorialLegend(allAccessorials, LOADS_TABLE_START_INDEX + reports.size());
    }

    private int fillLoadsTable(List<ActivityReportsBO> reports, int rowIndex, int maxProductSize) {
        int result = rowIndex;
        for (ActivityReportsBO report : reports) {
            buildRow(report, result, maxProductSize);
            result++;
        }
        return result;
    }

    private int prepareLoadsTableForFilling(List<ActivityReportsBO> reports, int maxProductSize, Row headerRow) {
        if (maxProductSize > 0) {
            //copying product header
            for (int i = maxProductSize - 1; i >= 0; i--) {
                for (int columnIndex = 0; columnIndex < PRODUCT_COLUMNS_QUANTITY; columnIndex++) {
                    copyCell(headerRow, getFirstProductColumn() + columnIndex,
                            getFirstProductColumn() + (i * PRODUCT_COLUMNS_QUANTITY) + columnIndex, new Integer(i + 1));
                }
            }
        }
        if (maxProductSize == 0) {
            for (int columnIndex = getFirstProductColumn(); columnIndex < getFirstProductColumn() + PRODUCT_COLUMNS_QUANTITY; columnIndex++) {
                Cell cell = headerRow.getCell(columnIndex);
                headerRow.removeCell(cell);
            }
        }
        int rowIndex = 10;
        if (!reports.isEmpty()) {
            mainSheet.shiftRows(rowIndex + 1, 20, reports.size());
        }
        return rowIndex;
    }
    
    protected int getFirstProductColumn() {
        return FIRST_PRODUCTS_COLUMN;
    }

    private int getMaxProductSize(List<ActivityReportsBO> reports) {
        int maxProductSize = 0;
        for (ActivityReportsBO report : reports) {
            int size = report.getProducts().size();
            if (maxProductSize < size) {
                maxProductSize = size;
            }
        }
        return maxProductSize;
    }

    private void fillHeader(List<ActivityReportsBO> reports, ReportParamsBO reportParams, BigDecimal totalCost) {
        fillDates(reportParams.getStartDate(), reportParams.getEndDate(), mainSheet.getRow(0).getCell(0));
        DateFormat slashedFormat = new SimpleDateFormat(DateUtility.SLASHED_DATE, Locale.US);
        fillCellWithReplace(mainSheet.getRow(3).getCell(0), SUBJECT_TYPE_MARKER, ReportUtil.getSubjectType(reportParams));
        fillCellWithReplace(mainSheet.getRow(3).getCell(0), SUBJECT_NAME_MARKER, ReportUtil.getSubjectName(reportParams));
        fillCellWithReplace(mainSheet.getRow(4).getCell(0), START_DATE_MARKER, slashedFormat.format(reportParams.getStartDate()));
        fillCellWithReplace(mainSheet.getRow(4).getCell(0), END_DATE_MARKER, slashedFormat.format(reportParams.getEndDate()));
        DateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
        fillCellWithReplace(mainSheet.getRow(5).getCell(0), REPORT_RUN_DATETIME_MARKER, dateTimeFormat.format(Calendar.getInstance().getTime()));
        fillCellWithReplace(mainSheet.getRow(3).getCell(5), LOAD_COUNT_MARKER, ((Integer) reports.size()).toString());
        fillCellWithReplace(mainSheet.getRow(4).getCell(5), TOTAL_COST_MARKER, totalCost.toString());
    }

    private void fillCellWithReplace(Cell customerCell, String marker, String value) {
        RichTextString richStringCellValue = customerCell.getRichStringCellValue();
        if (richStringCellValue instanceof XSSFRichTextString) {
            XSSFRichTextString richString = (XSSFRichTextString) richStringCellValue;
            if (richString.getString() != null) {
                //TODO:write common method
                int numFormattingRuns = richString.numFormattingRuns();
                int startIndex = 0;
                int length = 0;
                XSSFFont font = null;
                if (numFormattingRuns > 0) {
                    startIndex = richString.getIndexOfFormattingRun(0);
                    length = richString.getLengthOfFormattingRun(0);
                    font = richString.getFontAtIndex(startIndex);
                }
                richString.setString(richString.getString().replace(marker, value));
                if (numFormattingRuns > 0) {
                    richString.applyFont(startIndex, startIndex + length, font);
                }
            }
            customerCell.setCellValue(richStringCellValue);
        } else {
            String customerCellValue = customerCell.getStringCellValue();
            customerCellValue = customerCellValue.replace(marker, value);
            customerCell.setCellValue(customerCellValue);
        }
    }

    private void fillDates(Date startDate, Date endDate, Cell cell) {
        String cellValue = cell.getStringCellValue();
        DateFormat slashedFormat = new SimpleDateFormat(DateUtility.SLASHED_DATE, Locale.US);
        cellValue = cellValue.replace(START_DATE_MARKER, slashedFormat.format(startDate));
        cellValue = cellValue.replace(END_DATE_MARKER, slashedFormat.format(endDate));
        cell.setCellValue(cellValue);
    }

    private void copyCellStyle(Row srcRow, Row destRow, int columnIndex) {
        if (destRow.getCell(columnIndex) == null) {
            destRow.createCell(columnIndex);
        }
        destRow.getCell(columnIndex).setCellStyle(srcRow.getCell(columnIndex).getCellStyle());
    }

    private void fillAccessorialLegend(Map<String, String> allAccessorials, int startRowIndex) {
        if (allAccessorials.isEmpty()) {
            return;
        }
        int accesorialRowsNumber = getAccessorialRowsNumber(allAccessorials);
        prepareAccessorialsTableForFilling(startRowIndex, accesorialRowsNumber);
        fillAccessorialsTable(allAccessorials, startRowIndex, accesorialRowsNumber);
    }

    private void fillAccessorialsTable(Map<String, String> allAccessorials, int startRowIndex, int accesorialRowsNumber) {
        String [] accessorialKeys = {};
        accessorialKeys = allAccessorials.keySet().toArray(accessorialKeys);
        Arrays.sort(accessorialKeys);
        for (int colNumber = 0; colNumber < NUMBER_OF_ACCESSORIALS_COLUMNS; colNumber++) {
            for (int rowNumber = 0; rowNumber < accesorialRowsNumber; rowNumber++) {
                int arrayIndex = rowNumber + colNumber * accesorialRowsNumber;
                if (arrayIndex >= accessorialKeys.length) {
                    return;
                }
                Row currentRow = createRowIfDontExist(startRowIndex + rowNumber);
                Cell cell = currentRow.getCell(colNumber * NUMBER_OF_ACCESSORIALS_COLUMNS);
                if (cell == null) {
                    currentRow.createCell(colNumber * NUMBER_OF_ACCESSORIALS_COLUMNS);
                    cell = currentRow.getCell(colNumber * NUMBER_OF_ACCESSORIALS_COLUMNS);
                }
                cell.setCellValue(accessorialKeys[arrayIndex] + " - " + allAccessorials.get(accessorialKeys[arrayIndex]));
            }
        }
    }

    private void prepareAccessorialsTableForFilling(int startRowIndex, int accesorialRowsNumber) {
        if (accesorialRowsNumber > 2) {
            mainSheet.shiftRows(startRowIndex, startRowIndex + 1, accesorialRowsNumber - 2);
        }

        if (accesorialRowsNumber == 1) {
            mainSheet.shiftRows(startRowIndex + 1, startRowIndex + 2, -1);
        }

        if (accesorialRowsNumber > 0) {
            for (int i = startRowIndex + accesorialRowsNumber - 2; i > startRowIndex; i--) {
                Row srcRow = mainSheet.getRow(i);
                Row destRow = createRowIfDontExist(i - 1);
                copyCellStyle(srcRow, destRow, LAST_INDEX_OF_ACCESORIAL_TABLE);
                copyCellStyle(srcRow, destRow, 0);
            }
        }
    }

    private int getAccessorialRowsNumber(Map<String, String> allAccessorials) {
        int accesorialRowsNumber = allAccessorials.size() / NUMBER_OF_ACCESSORIALS_COLUMNS;
        if ((allAccessorials.size() % NUMBER_OF_ACCESSORIALS_COLUMNS) != 0) {
            accesorialRowsNumber++;
        }
        return accesorialRowsNumber;
    }

    private Row createRowIfDontExist(int i) {
        Row row = mainSheet.getRow(i);
        if (row == null) {
            mainSheet.createRow(i);
            row = mainSheet.getRow(i);
        }
        return row;
    }

    private void copyCell(Row headerRow, int srcIndex, int destIndex, Integer index) {
        Cell cell = headerRow.getCell(srcIndex);
        if (headerRow.getCell(destIndex) == null) {
            headerRow.createCell(destIndex);
        }
        Cell newCell = headerRow.getCell(destIndex);
        String stringCellValue = cell.getStringCellValue();
        if (index != null) {
            stringCellValue = stringCellValue.replace(INDEX_MARKER, index.toString());
        }
        newCell.setCellValue(stringCellValue);
        newCell.setCellStyle(cell.getCellStyle());
    }

    protected void buildRow(ActivityReportsBO report, int rowIndex, int maxProductSize) {
        Row currentRow = mainSheet.createRow(rowIndex);
        int index = 0;
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
        fillDataCell(currentRow, index++, report.getCustomerCostPerPound());
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

    /**
     * Generate dimension string.
     *
     * @param product
     *            the product
     * @return the string
     */
    protected String generateDimensionString(ProductReportBO product) {
        List<String> dimensionArray = new ArrayList<String>(3);
        if (product.getLength() != null) {
            dimensionArray.add("L" + product.getLength());
        }
        if (product.getWidth() != null) {
            dimensionArray.add("W" + product.getWidth());
        }
        if (product.getHeight() != null) {
            dimensionArray.add("H" + product.getHeight());
        }
        return StringUtils.join(dimensionArray, "x");
    }

    /**
     * Generate accessorials string.
     *
     * @param accessorials
     *            the accessorials
     * @return the string
     */
    protected String generateAccessorialsString(List<AccessorialReportBO> accessorials) {
        if (accessorials.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<String>(accessorials.size());
        for (AccessorialReportBO accessorialReportBO : accessorials) {
            result.add(accessorialReportBO.getAccessorialTypeCode());
        }
        return StringUtils.join(result, ",");
    }
}
