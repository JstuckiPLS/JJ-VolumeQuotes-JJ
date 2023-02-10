package com.pls.shipment.service.xls;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Shipment Creation Report Builder.
 * 
 * @author Yasaman Palumbo
 * 
 */
public class CreationReportExcelBuilder extends AbstractReportExcelBuilder {

    private int tableHeaderRowIndex = 9;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private Integer totalWeight = 0;

    /**
     * Constructor.
     * 
     * @param shipmentCreationReportTemplate
     *            template
     * @throws IOException
     *             exception
     */
    public CreationReportExcelBuilder(ClassPathResource shipmentCreationReportTemplate) throws IOException {
        super(shipmentCreationReportTemplate);
    }

    /**
     * Generates revenue report as xlsx file.
     */
    public FileInputStreamResponseEntity generateReport(List<CreationReportBO> reports, ReportParamsBO reportParams)
            throws IOException {
        fillSheet(reports, reportParams);
        return super.generateReport(ReportUtil.getSubjectName(reportParams), reportParams.getStartDate(), reportParams.getEndDate());
    }

    private void fillSheet(List<CreationReportBO> reports, ReportParamsBO reportParams) {

        CellStyle style = workbook.createCellStyle();
        CellStyle currencyStyle = workbook.createCellStyle();

        /**
         * Report title
         */
        fillCellValue(mainSheet.getRow(0), 0, SIMPLE_DATE_FORMAT.format(reportParams.getEndDate()), "{endDate}");
        fillCellValue(mainSheet.getRow(0), 0, SIMPLE_DATE_FORMAT.format(reportParams.getStartDate()), "{startDate}");

        /**
         * Report Criteria
         */
        Font font = workbook.createFont();
        fillCellValue(mainSheet.getRow(3), 0, ReportUtil.getSubjectType(reportParams), "{subjectType}");
        fillCellValue(mainSheet.getRow(3), 0, ReportUtil.getSubjectName(reportParams), "{subjectName}");
        fillCellValue(mainSheet.getRow(5), 0, SIMPLE_DATE_FORMAT.format(reportParams.getEndDate()), "{endDate}", null, font);
        fillCellValue(mainSheet.getRow(5), 0, SIMPLE_DATE_FORMAT.format(reportParams.getStartDate()), "{startDate}", null, font);
        // TODO hard coded date and time format
        fillCellValue(mainSheet.getRow(6), 0, new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US).format(new Date()), "{time}", null, font);

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
            buildRow(reports.get(i), i + tableHeaderRowIndex + 1, style);
        }

        fillCellValue(mainSheet.getRow(3), 6, String.valueOf(reports.size()), "{load}");
        fillCellValue(mainSheet.getRow(4), 6, totalCost.toString(), "{cost}");
        fillCellValue(mainSheet.getRow(5), 6, totalWeight != 0 ? totalCost.divide(new BigDecimal(totalWeight), 2, RoundingMode.HALF_UP).toString()
                : "0", "{costPerLB}");
    }

    private void buildRow(CreationReportBO report, int rowIndex, CellStyle style) {
        Row currentRow = mainSheet.createRow(rowIndex);
        int cellIndex = 0;
        cellIndex = this.builsSimpleCell(report, currentRow, cellIndex, style);
    }

    private int builsSimpleCell(CreationReportBO report, Row currentRow, int index, CellStyle style) {
        int cellIndex = index;
        fillDataCell(currentRow, cellIndex++, report.getLoadId(), style);
        fillDataCell(currentRow, cellIndex++, report.getInvoiceNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getName(), style);
        fillDataCell(currentRow, cellIndex++, report.getCreationMethod(), style);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(style);
        dateStyle.setDataFormat(dateCellStyle.getDataFormat());
        fillDataCell(currentRow, cellIndex++, report.getCreatedDate(), dateStyle);
        fillDataCell(currentRow, cellIndex++, report.getProNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getBol(), style);
        fillDataCell(currentRow, cellIndex++, report.getShipmentClass() == null ? "Multi" : report.getShipmentClass().getDbCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getPoNumber(), style);
        fillDataCell(currentRow, cellIndex++, report.getShipDate(), dateStyle);
        fillDataCell(currentRow, cellIndex++, report.getScacCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getShipperName(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginCity(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginSt(), style);
        fillDataCell(currentRow, cellIndex++, report.getOriginZip(), style);
        fillDataCell(currentRow, cellIndex++, report.getConsignee(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestCity(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestSt(), style);
        fillDataCell(currentRow, cellIndex++, report.getDestZip(), style);
        BigDecimal cost = report.getCost();
        Integer weight = report.getWeight();
        totalCost = totalCost.add(cost);
        totalWeight = totalWeight + weight;
        fillDataCell(currentRow, cellIndex++, weight, style);
        fillDataCell(currentRow, cellIndex++, cost, style);
        fillDataCell(currentRow, cellIndex++, weight != 0 ? cost.divide(new BigDecimal(weight), 2, RoundingMode.HALF_UP) : "0", style);
        return cellIndex;
    }
}
