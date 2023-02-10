package com.pls.shipment.service.xls;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.bo.AccessorialReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.bo.ProductReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Savings Report Builder.
 * 
 * @author Alexander Nalapko
 * 
 */
public class SavingsReportExcelBuilder extends AbstractReportExcelBuilder {

    private static final int NUMBER_OF_ACCESSORIALS_COLUMNS = 3;
    private static final int LAST_INDEX_OF_ACCESORIAL_TABLE = 9;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalBenchmark = BigDecimal.ZERO;
    private BigDecimal totalSavings = BigDecimal.ZERO;
    private int footerHeaderCellIndex = 14;
    private int tableHeaderRowIndex = 10;
    private int tableHeaderCellIndex = 31;
    private CellStyle currencyStyle;
    private Map<String, String> accessorialMap = new TreeMap<String, String>(new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    });

    /**
     * Constructor.
     * 
     * @param revenueReportTemplate
     *            template
     * @throws IOException
     *             exception
     */
    public SavingsReportExcelBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    /**
     * Generates revenue report as xlsx file.
     */
    public FileInputStreamResponseEntity generateReport(List<FreightAnalysisReportBO> reports, ReportParamsBO reportParams) throws IOException {
        fillSheet(reports, reportParams);
        return super.generateReport(ReportUtil.getSubjectName(reportParams), reportParams.getStartDate(), reportParams.getEndDate());
    }

    private void fillSheet(List<FreightAnalysisReportBO> reports, ReportParamsBO reportParams) {

        /**
         * Report title
         */
        CellStyle style = workbook.createCellStyle();
        currencyStyle = workbook.createCellStyle();

        fillCellValue(mainSheet.getRow(0), 0, SIMPLE_DATE_FORMAT.format(reportParams.getEndDate()), "{endDate}");
        fillCellValue(mainSheet.getRow(0), 0, SIMPLE_DATE_FORMAT.format(reportParams.getStartDate()), "{startDate}");

        /**
         * Report Criteria
         */
        Font font = workbook.createFont();

        fillCellValue(mainSheet.getRow(3), 0, ReportUtil.getSubjectType(reportParams), "{subjectType}");
        fillCellValue(mainSheet.getRow(3), 0, ReportUtil.getSubjectName(reportParams), "{subjectName}");
        fillCellValue(mainSheet.getRow(4), 0, SIMPLE_DATE_FORMAT.format(reportParams.getEndDate()), "{endDate}", null, font);
        fillCellValue(mainSheet.getRow(4), 0, SIMPLE_DATE_FORMAT.format(reportParams.getStartDate()), "{startDate}", null, font);
        fillCellValue(mainSheet.getRow(5), 0, new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US).format(new Date()),
                "{time}");

        /**
         * move Accessorial Legend
         */
        if (!reports.isEmpty()) {
            mainSheet.shiftRows(footerHeaderCellIndex, 19, reports.size());
            footerHeaderCellIndex = footerHeaderCellIndex + reports.size() + 1;
        }

        /*
         * calculate max products
         */
        int productSize = 0;
        for (FreightAnalysisReportBO freightAnalysisReportBO : reports) {
            if (freightAnalysisReportBO.getProducts().size() > productSize) {
                productSize = freightAnalysisReportBO.getProducts().size();
            }
        }

        /*
         * add product title
         */
        this.buildProductHeader(productSize);

        /**
         * style for grid element from template
         */
        style = mainSheet.getRow(tableHeaderRowIndex + 1).getCell(0).getCellStyle();
        currencyStyle.cloneStyleFrom(style);
        currencyStyle.setDataFormat(currencyCellStyle.getDataFormat());
        /**
         * add products
         */
        for (int i = 0; i < reports.size(); i++) {
            buildRow(reports.get(i), i + 11, style, productSize);
        }

        /**
         * Report Summary Information
         */
        fillCellValue(mainSheet.getRow(3), 5, String.valueOf(reports.size()), "{load}");
        fillCellValue(mainSheet.getRow(4), 5, StringUtils.join(" $", totalCost.toString()), "{revenue}");
        fillCellValue(mainSheet.getRow(5), 5, StringUtils.join(" $", totalBenchmark.toString()), "{benchmark}");
        fillCellValue(mainSheet.getRow(6), 5, StringUtils.join(" $", totalSavings.toString()), "{savings}");

        BigDecimal totalSavingsPercent = BigDecimal.ZERO;
        if (totalBenchmark != null && totalBenchmark.signum() > 0) {
            totalSavingsPercent = totalSavingsPercent.add(totalSavings);
            totalSavingsPercent = totalSavingsPercent.multiply(new BigDecimal(100))
                    .divide(totalBenchmark, new MathContext(3));
        }
        fillCellValue(mainSheet.getRow(7), 5, StringUtils.join(totalSavingsPercent.toString(), "%"), "{percentage}");

        /**
         * Accessorial Legend
         */
        this.buildAccessorial();
    }

    private void buildProductHeader(int productSize) {
        Row tableHeaderRow = mainSheet.getRow(tableHeaderRowIndex);
        Cell cell = tableHeaderRow.getCell(0);

        for (int i = 1; i <= productSize; i++) {
            Cell cellWeight = tableHeaderRow.createCell(tableHeaderCellIndex++);
            cellWeight.setCellStyle(cell.getCellStyle());
            cellWeight.setCellValue(StringUtils.join("Weight ", String.valueOf(i)));

            Cell cellClass = tableHeaderRow.createCell(tableHeaderCellIndex++);
            cellClass.setCellStyle(cell.getCellStyle());
            cellClass.setCellValue(StringUtils.join("Class ", String.valueOf(i)));

            Cell cellPieces = tableHeaderRow.createCell(tableHeaderCellIndex++);
            cellPieces.setCellStyle(cell.getCellStyle());
            cellPieces.setCellValue(StringUtils.join("Pieces ", String.valueOf(i)));

            Cell cellDimensions = tableHeaderRow.createCell(tableHeaderCellIndex++);
            cellDimensions.setCellStyle(cell.getCellStyle());
            cellDimensions.setCellValue(StringUtils.join("Dimensions ", String.valueOf(i)));
        }
    }

    private void buildAccessorial() {
        if (accessorialMap.isEmpty()) {
            return;
        }

        int accessorialRowsCount = getAccessorialRowsNumber(accessorialMap);

        prepareAccessorialsTableForFilling(footerHeaderCellIndex, accessorialRowsCount);
        populateAccessorialTable(accessorialRowsCount);
    }

    private void populateAccessorialTable(int rowCount) {
        int topBorder = footerHeaderCellIndex;
        int belowBorder = footerHeaderCellIndex + rowCount;
        int currRowIdx = topBorder;
        int colIdx = 0;

        for (Entry<String, String> item : accessorialMap.entrySet()) {
            Row currentRow = createRowIfDontExist(currRowIdx++);
            Cell cell  = currentRow.getCell(colIdx);
            if (cell == null) {
                cell = currentRow.createCell(colIdx);
            }
            cell.setCellValue(StringUtils.join(item.getKey(), " - ", item.getValue()));
            if (currRowIdx >= belowBorder) {
                currRowIdx = topBorder;
                colIdx += 3;
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

    private void prepareAccessorialsTableForFilling(int startIndex, int accesorialNumber) {
        if (accesorialNumber > 2) {
            mainSheet.shiftRows(startIndex, startIndex + 1, accesorialNumber - 2);
        }

        if (accesorialNumber == 1) {
            mainSheet.shiftRows(startIndex + 1, startIndex + 2, -1);
        }

        if (accesorialNumber > 0) {
            for (int i = startIndex + accesorialNumber - 2; i > startIndex; i--) {
                Row srcRow = mainSheet.getRow(i);
                Row destRow = createRowIfDontExist(i - 1);
                copyCellStyle(srcRow, destRow, LAST_INDEX_OF_ACCESORIAL_TABLE);
                copyCellStyle(srcRow, destRow, 0);
            }
        }
    }

    private Row createRowIfDontExist(int i) {
        Row row = mainSheet.getRow(i);
        if (row == null) {
            mainSheet.createRow(i);
            row = mainSheet.getRow(i);
        }
        return row;
    }

    private void copyCellStyle(Row srcRow, Row destRow, int columnIndex) {
        if (destRow.getCell(columnIndex) == null) {
            destRow.createCell(columnIndex);
        }
        destRow.getCell(columnIndex).setCellStyle(srcRow.getCell(columnIndex).getCellStyle());
    }

    private void buildRow(FreightAnalysisReportBO report, int rowIndex, CellStyle style, int productSize) {
        Row currentRow = mainSheet.createRow(rowIndex);

        int cellIndex = 0;

        cellIndex = this.builsSimpleCell(report, currentRow, cellIndex, style);

        /**
         * total weight section.
         */
        List<ProductReportBO> productList = report.getProducts();
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getWeight() != null) {
                totalWeight = totalWeight.add(productList.get(i).getWeight());
            }
        }
        fillDataCell(currentRow, cellIndex++, totalWeight, style);

        List<String> accessorials = new ArrayList<String>();
        for (AccessorialReportBO accessorialReportBO : report.getAccessorials()) {
            if (accessorialReportBO.getAccessorialTypeCode() != null && accessorialReportBO.getDescription() != null) {
                accessorialMap.put(accessorialReportBO.getAccessorialTypeCode(), accessorialReportBO.getDescription());
                accessorials.add(accessorialReportBO.getAccessorialTypeCode());
            }
        }

        fillDataCell(currentRow, cellIndex++, StringUtils.join(accessorials, ", "), style);

        fillDataCell(currentRow, cellIndex++, report.getCustLhCost(), currencyStyle);

        fillDataCell(currentRow, cellIndex++, report.getCustFsCost(), currencyStyle);

        BigDecimal totalAccCust = BigDecimal.ZERO;
        totalAccCust = totalAccCust.add(report.getTotalRevenue());
        if (report.getCustLhCost() != null) {
            totalAccCust = totalAccCust.subtract(report.getCustLhCost());
        }
        if (report.getCustFsCost() != null) {
            totalAccCust = totalAccCust.subtract(report.getCustFsCost());
        }
        fillDataCell(currentRow, cellIndex++, totalAccCust, currencyStyle);

        fillDataCell(currentRow, cellIndex++, report.getTotalRevenue(), currencyStyle);
        if (report.getTotalRevenue() != null) {
            this.totalCost = this.totalCost.add(report.getTotalRevenue()); // increase total Cost
        }

        /**
         * Cust Cost per Pound section.
         */
        cellIndex = this.builsCustPerPound(report, totalWeight, currentRow, cellIndex, style);

        /**
         * Savings section.
         */
        cellIndex = this.builsSaving(report, currentRow, cellIndex, style);

        /**
         * Product section.
         */
        cellIndex = this.builsProduct(productList, productSize, currentRow, cellIndex, style);
    }

    private int builsSimpleCell(FreightAnalysisReportBO report, Row currentRow, int index, CellStyle style) {
        int cellIndex = index;
        fillDataCell(currentRow, cellIndex++, report.getCustomerName(), style);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(style);
        dateStyle.setDataFormat(dateCellStyle.getDataFormat());
        fillDataCell(currentRow, cellIndex++, report.getDeparture(), dateStyle);
        fillDataCell(currentRow, cellIndex++, report.getLoadId(), style);
        fillDataCell(currentRow, cellIndex++, report.getBol(), style);
        fillDataCell(currentRow, cellIndex++, report.getShipperRef(), style);
        fillDataCell(currentRow, cellIndex++, report.getPoNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getGlNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getProNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getScacCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getCarrierName(), style);
        fillDataCell(currentRow, cellIndex++, report.getShipmentDirection().getCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginContact(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginAddress(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginCity(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginStateCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginZip(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestinationContact(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestinationAddress(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestinationCity(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestinationStateCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestinationZip(), style);
        return cellIndex;
    }

    private int builsCustPerPound(FreightAnalysisReportBO report, BigDecimal totalWeight, Row currentRow, int index,
            CellStyle style) {
        int cellIndex = index;
        if (totalWeight.signum() > 0) {
            BigDecimal custPerPound = BigDecimal.ZERO;
            if (report.getCustLhCost() != null) {
                custPerPound = custPerPound.add(report.getCustLhCost());
            }
            if (report.getCustFsCost() != null) {
                custPerPound = custPerPound.add(report.getCustFsCost());
            }
            custPerPound = custPerPound.divide(totalWeight, new MathContext(3));
            fillDataCell(currentRow, cellIndex++, custPerPound, currencyStyle);
        } else {
            fillEmptyCell(currentRow, cellIndex++, style);
        }

        fillDataCell(currentRow, cellIndex++, report.getBenchmarkAmount(), currencyStyle);
        if (report.getBenchmarkAmount() != null) {
            this.totalBenchmark = this.totalBenchmark.add(report.getBenchmarkAmount()); // increase total
                                                                                        // Benchmark
        }
        return cellIndex;
    }

    private int builsSaving(FreightAnalysisReportBO report, Row currentRow, int index, CellStyle style) {
        int cellIndex = index;
        BigDecimal savings = BigDecimal.ZERO;
        if (report.getBenchmarkAmount() != null) {
            savings = savings.add(report.getBenchmarkAmount());
        }
        if (report.getCustLhCost() != null) {
            savings = savings.subtract(report.getCustLhCost());
        }
        if (report.getCustFsCost() != null) {
            savings = savings.subtract(report.getCustFsCost());
        }
        fillDataCell(currentRow, cellIndex++, savings, currencyStyle);
        this.totalSavings = this.totalSavings.add(savings); // increase total Savings

        /**
         * Benchmark % section.
         */
        if (report.getBenchmarkAmount() != null && report.getBenchmarkAmount().signum() > 0) {
            BigDecimal savingsPercent = BigDecimal.ZERO;
            savingsPercent = savingsPercent.add(savings);

            savingsPercent = savingsPercent.divide(report.getBenchmarkAmount(), new MathContext(4));
            CellStyle prcStyle = workbook.createCellStyle();
            prcStyle.cloneStyleFrom(style);
            prcStyle.setDataFormat(percCellStyle.getDataFormat());
            fillDataCell(currentRow, cellIndex++, savingsPercent, prcStyle);
        } else {
            fillEmptyCell(currentRow, cellIndex++, style);
        }
        return cellIndex;
    }

    private int builsProduct(List<ProductReportBO> list, int productSize, Row currentRow, int index, CellStyle style) {
        int cellIndex = index;
        List<ProductReportBO> productList = list;
        for (int i = 0; i < productSize; i++) {
            if (i >= productList.size()) {
                fillEmptyCell(currentRow, cellIndex++, style);
                fillEmptyCell(currentRow, cellIndex++, style);
                fillEmptyCell(currentRow, cellIndex++, style);
                fillEmptyCell(currentRow, cellIndex++, style);
            } else {
                ProductReportBO product = productList.get(i);
                fillDataCell(currentRow, cellIndex++, product.getWeight(), style);
                if (product.getCommodityClass() != null) {
                    fillDataCell(currentRow, cellIndex++,
                            StringUtils.substringAfter(product.getCommodityClass().name(), "_"), style);
                } else {
                    fillEmptyCell(currentRow, cellIndex++, style);
                }
                fillDataCell(currentRow, cellIndex++, product.getPieces(), style);
                if (product.getLength() != null && product.getWidth() != null && product.getHeight() != null) {
                    String dimentions = StringUtils.join("L", product.getLength().toString(), "xW", product.getWidth().toString(), "xH",
                            product.getHeight().toString());
                    fillDataCell(currentRow, cellIndex++, dimentions, style);
                } else {
                    fillEmptyCell(currentRow, cellIndex++, style);
                }
            }
        }
        return cellIndex;
    }
}
