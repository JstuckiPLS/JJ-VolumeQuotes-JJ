package com.pls.invoice.service.xsl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Invoice report excel builder.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class CBIGroupedExcelReportBuilder {

    private static final String CURRENCY_FORMAT = "\"$\"#,##0.00";
    private static final String COST_FORMAT = "0.00";
    private static final String WEIGHT_FORMAT = "#,###";

    private static final String DATE_FORMAT = DateFormatConverter.convert(Locale.US, new SimpleDateFormat("MM/dd/yy", Locale.US));

    private final Workbook workbook;
    private final Sheet mainSheet;

    private final CellStyle currencyCellStyle;
    private final CellStyle costCellStyle;
    private final CellStyle weightCellStyle;
    private final CellStyle weightBoldCellStyle;
    private final CellStyle dateCellStyle;

    private Map<String, List<CBIReportRowAdapter>> loadAdjustmentMap = new TreeMap<String, List<CBIReportRowAdapter>>();

    private int rowIndex = 2;

    /**
     * Invoice CBI builder constructor.
     * 
     * @param invoiceTemplate - invoice template.
     * @throws IOException exception
     */
    public CBIGroupedExcelReportBuilder(ClassPathResource invoiceTemplate) throws IOException {
        InputStream template = invoiceTemplate.getInputStream();
        workbook = new XSSFWorkbook(template);
        mainSheet = workbook.getSheetAt(0);

        DataFormat dateFormat = workbook.createDataFormat();
        currencyCellStyle = workbook.createCellStyle();
        currencyCellStyle.setDataFormat(dateFormat.getFormat(CURRENCY_FORMAT));
        setBoldFontForCellStyle(currencyCellStyle);
        costCellStyle = workbook.createCellStyle();
        costCellStyle.setDataFormat(dateFormat.getFormat(COST_FORMAT));
        weightCellStyle = workbook.createCellStyle();
        weightCellStyle.setDataFormat(dateFormat.getFormat(WEIGHT_FORMAT));
        weightBoldCellStyle = workbook.createCellStyle();
        weightBoldCellStyle.setDataFormat(dateFormat.getFormat(WEIGHT_FORMAT));
        setBoldFontForCellStyle(weightBoldCellStyle);
        dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(dateFormat.getFormat(DATE_FORMAT));
    }

    /**
     * Generates invoice report as xlsx file.
     *
     * @param invoices
     *            loads and adjustments sorted in the order that they should appear in the invoice.
     * @param invoiceNumber
     *            generated invoice number
     * @param outputStream
     *            output stream of generated report
     * @throws IOException
     *             if can't generate report
     */
    public void generateReport(List<LoadAdjustmentBO> invoices, String invoiceNumber, OutputStream outputStream)
            throws IOException {
        buildFirstReportRow(invoiceNumber);
        fillLoadAdjustmentMap(invoices);
        buildReport();

        try {
            workbook.write(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void buildFirstReportRow(String invoiceNumber) {
        workbook.setActiveSheet(0);
        Row firstRow = mainSheet.createRow(1);
        fillDataCell(firstRow, 0, "Invoice#".concat(invoiceNumber));
        setBollCellStyle(firstRow, 0);
        fillDataCell(firstRow, 17, "VANLTL");
        setBollCellStyle(firstRow, 17);
    }

    /**
     * Gather objects and put them into collection(map)
     * 
     * @param loads - loads contain report data
     * @param adjustmentsByLoad adjustments sorted by loads
     */
    private void fillLoadAdjustmentMap(List<LoadAdjustmentBO> invoices) {
        for (LoadAdjustmentBO invoice : invoices) {
            String glNumber = invoice.getAdjustment() == null ? invoice.getLoad().getNumbers().getGlNumber() : invoice.getAdjustment().getLoad()
                    .getNumbers().getGlNumber();
            glNumber = StringUtils.defaultString(glNumber);
            CBIReportRowAdapter rowAdapter = invoice.getAdjustment() == null ? new CBIReportRowAdapter(invoice.getLoad())
                    : new CBIReportRowAdapter(invoice.getAdjustment());
            if (loadAdjustmentMap.containsKey(glNumber)) {
                loadAdjustmentMap.get(glNumber).add(rowAdapter);
            } else {
                List<CBIReportRowAdapter> loadList = new ArrayList<CBIReportRowAdapter>();
                loadList.add(rowAdapter);
                loadAdjustmentMap.put(glNumber, loadList);
            }
        }
    }

    /**
     * Write data into rows and cells.
     */
    private void buildReport() {
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (String loadAdjKey : loadAdjustmentMap.keySet()) {
            List<CBIReportRowAdapter> loadAdjList = loadAdjustmentMap.get(loadAdjKey);
            BigDecimal subtotalWeight = BigDecimal.ZERO;
            BigDecimal subtotalCost = BigDecimal.ZERO;

            if (!loadAdjList.isEmpty()) {
                buildGlNumberRow(loadAdjList.get(0).getGlNumber(), rowIndex++);
            }
            for (CBIReportRowAdapter row : loadAdjList) {
                subtotalWeight = subtotalWeight.add(new BigDecimal(row.getWeight()));
                subtotalCost = subtotalCost.add(row.getTotalRevenue());
                buildLoadRow(row, rowIndex++);
            }
            totalWeight = totalWeight.add(subtotalWeight);
            totalCost = totalCost.add(subtotalCost);
            buildSubtotalRow(loadAdjKey, rowIndex++, loadAdjList.size(), subtotalWeight, subtotalCost);
        }
        buildTotalRows(rowIndex++, totalWeight, totalCost);
    }

    private void buildLoadRow(CBIReportRowAdapter rowData, int rowIndex) {
        workbook.setActiveSheet(0);
        Row currentRow = mainSheet.createRow(rowIndex);
        int columnIndex = 0;
        fillDataCell(currentRow, columnIndex++, rowData.getProNumber());
        fillDataCell(currentRow, columnIndex++, rowData.getBolNumber());
        fillDataCell(currentRow, columnIndex++, rowData.getCommodityClass());
        fillDataCell(currentRow, columnIndex++, rowData.getPoNumber());
        fillDataCell(currentRow, columnIndex++, rowData.getShipDate());
        fillDataCell(currentRow, columnIndex++, rowData.getCarrierSCAC());
        fillDataCell(currentRow, columnIndex++, rowData.getCarrierOriginAddressName());
        fillDataCell(currentRow, columnIndex++, rowData.getOriginState());
        fillDataCell(currentRow, columnIndex++, rowData.getOriginZip());
        fillDataCell(currentRow, columnIndex++, rowData.getCarrierDestinationAddressName());
        fillDataCell(currentRow, columnIndex++, rowData.getDestinationState());
        fillDataCell(currentRow, columnIndex++, rowData.getDestinationZip());
        fillDataCell(currentRow, columnIndex++, Integer.valueOf(rowData.getWeight()), false);
        fillDataCell(currentRow, columnIndex++, rowData.getLineHaul().doubleValue());
        fillDataCell(currentRow, columnIndex++, rowData.getOtherAccessorialsCost().doubleValue());
        fillDataCell(currentRow, columnIndex++, rowData.getFuelSurcharge().doubleValue());
        fillDataCell(currentRow, columnIndex++, rowData.getTotalRevenue().doubleValue());
    }

    private void buildGlNumberRow(String glNumber, int rowIndex) {
        Row currentRow = mainSheet.createRow(rowIndex);
        fillDataCell(currentRow, 0, glNumber);
        setBollCellStyle(currentRow, 0);
        mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 13));
    }

    private void buildSubtotalRow(String glNumber, int rowIndex,
            int shipmentsCount, BigDecimal subtotalWeight, BigDecimal subtotalCost) {
        workbook.setActiveSheet(0);
        Row currentRow = mainSheet.createRow(rowIndex);
        int columnIndex = 0;
        fillDataCell(currentRow, columnIndex++, "Subtotal: ".concat(glNumber));
        mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 7));
        fillDataCell(currentRow, 8, shipmentsCount + " Shipment(s)");
        mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 8, 11));
        fillDataCell(currentRow, 12, subtotalWeight.intValue(), true);
        fillDataCell(currentRow, 16, subtotalCost);
        setBollCellStyle(currentRow, 0);
        setBollCellStyle(currentRow, 8);
        setBollCellStyle(currentRow, 12);
    }

    private void buildTotalRows(int rowIndex, BigDecimal totalWeight, BigDecimal totalCost) {
        workbook.setActiveSheet(0);
        Row currentRow = mainSheet.createRow(rowIndex);
        fillDataCell(currentRow, 12, "Total");
        mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 12, 13));
        setBollCellStyle(currentRow, 12);
        currentRow = mainSheet.createRow(rowIndex + 1);
        fillDataCell(currentRow, 12, totalWeight.intValue(), true);
        fillDataCell(currentRow, 16, totalCost);
    }

    private void fillDataCell(Row currentRow, int column, BigDecimal value) {
        Cell cell = currentRow.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        cell.setCellStyle(currencyCellStyle);
    }

    private void fillDataCell(Row currentRow, int column, Double value) {
        Cell cell = currentRow.createCell(column, CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        cell.setCellStyle(costCellStyle);
    }

    private void fillDataCell(Row currentRow, int column, Integer value, boolean isBold) {
        Cell cell = currentRow.createCell(column, CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue(BigDecimal.ZERO.intValue());
        }
        cell.setCellStyle(isBold ? weightBoldCellStyle : weightCellStyle);
    }

    private void fillDataCell(Row currentRow, int column, Date value) {
        Cell cell = currentRow.createCell(column);
        if (value != null) {
            cell.setCellValue(DateUtils.toCalendar(value));
        }
        cell.setCellStyle(dateCellStyle);
    }

    private void fillDataCell(Row currentRow, int column, String value) {
        currentRow.createCell(column).setCellValue(StringUtils.trimToEmpty(value));
    }

    private void setBollCellStyle(Row row, int cellIndex) {
        row.getCell(cellIndex).setCellStyle(getBoldStyle());
    }

    private void setBoldFontForCellStyle(CellStyle style) {
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
    }

    private CellStyle getBoldStyle() {
        CellStyle result = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        result.setFont(font);
        return result;
    }
}
