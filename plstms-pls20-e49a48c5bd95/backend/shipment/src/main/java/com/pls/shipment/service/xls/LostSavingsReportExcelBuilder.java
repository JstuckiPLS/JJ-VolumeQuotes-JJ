package com.pls.shipment.service.xls;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.bo.LostSavingsMaterialsBO;
import com.pls.core.domain.bo.LostSavingsReportBO;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.bo.ReportParamsBO;

/**
 * Lost Savings Opportunity Excel Report Builder.
 * 
 * @author Ashwini Neelgund
 */
public class LostSavingsReportExcelBuilder extends AbstractReportExcelBuilder {

    public static final String LOST_SAVINGS_REPORT_TITLE = "Lost Savings Opportunity Report for ";

    /**
     * Instantiates a new report excel builder.
     *
     * @param lsorReportTemplate
     *            Lost Savings Opportunity report template
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public LostSavingsReportExcelBuilder(ClassPathResource lsorReportTemplate) throws IOException {
        super(lsorReportTemplate);
    }

    private void fillLostSavingsReport(List<LostSavingsReportBO> reports, ReportParamsBO reportParams, List<AccessorialTypeEntity> accLegends) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String startDt = sdf.format(reportParams.getStartDate());
        String endDt = sdf.format(reportParams.getEndDate());
        String reportTitle = StringUtils.join(LOST_SAVINGS_REPORT_TITLE, startDt, " to ", endDt);
        fillDataCell(mainSheet.getRow(6), 0, reportTitle, rptHdngStyle);
        fillDataCell(mainSheet.getRow(10), 1, StringUtils.join(startDt, " to ", endDt), style);
        fillDataCell(mainSheet.getRow(11), 1, reportParams.getSortOrder(), style);
        fillDataCell(mainSheet.getRow(9), 0, ReportUtil.getSubjectType(reportParams) + ":");
        fillDataCell(mainSheet.getRow(9), 1, ReportUtil.getSubjectName(reportParams), style);
        if (!reports.isEmpty()) {
            fillDataCell(mainSheet.getRow(12), 1, reports.get(0).getDateCreated(), dateCellStyle);
        }

        double totalCost = 0;
        double totalLeastCost = 0;
        double potSavings = 0;
        int numOfShipments = 0;
        MultiMap potSavSummary = new MultiValueMap();
        int rowIndex = 16;

        int maxPropMatListSize = 0;
        for (LostSavingsReportBO report : reports) {
            maxPropMatListSize = report.getLostSavingsMaterials().size() > maxPropMatListSize ? report
                    .getLostSavingsMaterials().size() : maxPropMatListSize;
        }
        int reqColSize = 15;
        if (maxPropMatListSize > 1) {
            reqColSize = buildRptColHdng(maxPropMatListSize);
        }

        for (LostSavingsReportBO report : reports) {
            totalCost = totalCost + report.getCarrAmt();
            totalLeastCost = totalLeastCost + report.getLeastCostAmt();
            potSavings = potSavings + report.getPotentialSavings();
            buildLSORow(report, rowIndex, reqColSize);
            rowIndex++;
            numOfShipments++;
            potSavSummary = updatePotentialSavings(report, potSavSummary);
        }
        mainSheet.createRow(rowIndex++);
        rowIndex = createAccessorialLegend(rowIndex, accLegends);
        fillDataCell(mainSheet.getRow(9), 6, round(totalCost, 2), currencyCellStyle);
        fillDataCell(mainSheet.getRow(10), 6, round(totalLeastCost, 2), currencyCellStyle);
        fillDataCell(mainSheet.getRow(11), 6, round(potSavings, 2), currencyCellStyle);
        fillDataCell(mainSheet.getRow(12), 6, round(potSavings / totalCost, 4), percCellStyle);
        fillDataCell(mainSheet.getRow(13), 6, numOfShipments, style);
        fillLsorSummary(potSavSummary);
    }

    @SuppressWarnings("rawtypes")
    private MultiMap updatePotentialSavings(LostSavingsReportBO report, MultiMap potSavSummary) {
        if (report.getPotentialSavings() > 0) {
            if (potSavSummary.containsKey(report.getLeastCostCarr())) {
                List carrSavSummList = (List) potSavSummary.get(report.getLeastCostCarr());
                potSavSummary.remove(report.getLeastCostCarr());
                potSavSummary.put(report.getLeastCostCarr(), (int) carrSavSummList.get(0) + 1);
                potSavSummary.put(report.getLeastCostCarr(),
                        (double) carrSavSummList.get(1) + report.getPotentialSavings());
                potSavSummary.put(report.getLeastCostCarr(), (double) carrSavSummList.get(2) + report.getCarrAmt());
                potSavSummary.put(
                        report.getLeastCostCarr(),
                        ((double) carrSavSummList.get(1) + report.getPotentialSavings())
                                / ((double) carrSavSummList.get(2) + report.getCarrAmt()));
            } else {
                potSavSummary.put(report.getLeastCostCarr(), 1);
                potSavSummary.put(report.getLeastCostCarr(), report.getPotentialSavings());
                potSavSummary.put(report.getLeastCostCarr(), report.getCarrAmt());
                potSavSummary.put(report.getLeastCostCarr(), report.getPotentialSavings() / report.getCarrAmt());
            }
        } else if (report.getPotentialSavings() == 0) {
            if (potSavSummary.containsKey("Other Scacs")) {
                List carrSavSummList = (List) potSavSummary.get("Other Scacs");
                potSavSummary.remove("Other Scacs");
                potSavSummary.put("Other Scacs", (int) carrSavSummList.get(0) + 1);
                potSavSummary.put("Other Scacs", (double) carrSavSummList.get(1) + report.getPotentialSavings());
                potSavSummary.put("Other Scacs", (double) carrSavSummList.get(2) + report.getCarrAmt());
                potSavSummary.put("Other Scacs", ((double) carrSavSummList.get(1) + report.getPotentialSavings())
                        / ((double) carrSavSummList.get(2) + report.getCarrAmt()));
            } else {
                potSavSummary.put("Other Scacs", 1);
                potSavSummary.put("Other Scacs", report.getPotentialSavings());
                potSavSummary.put("Other Scacs", report.getCarrAmt());
                potSavSummary.put("Other Scacs", report.getPotentialSavings() / report.getCarrAmt());
            }
        }
        return potSavSummary;
    }

    private int buildRptColHdng(int maxPropMatListSize) {
        int colIndex = 16;
        int productNum = 2;
        int reqColSize = 15 + ((maxPropMatListSize - 1) * 3);
        while (colIndex <= reqColSize) {
            fillDataCell(mainSheet.getRow(15), colIndex++,
                    StringUtils.join("Product ", Integer.toString(productNum)), colHdngStyle);
            fillDataCell(mainSheet.getRow(15), colIndex++,
                    StringUtils.join("Weight ", Integer.toString(productNum)), colHdngStyle);
            fillDataCell(mainSheet.getRow(15), colIndex++,
                    StringUtils.join("Class ", Integer.toString(productNum)), colHdngStyle);
            productNum++;
        }
        fillDataCell(mainSheet.getRow(15), colIndex++, "Total weight", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Created Date", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Created Day", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Est. Pickup Date", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Actual Pickup Date", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Accessorials", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Carrier Selected", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Carrier Amount", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Carrier Time(Days)", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Least Cost Carrier", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Least Cost Amount", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Least Cost Time", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex++, "Potential Savings", colHdngStyle);
        fillDataCell(mainSheet.getRow(15), colIndex, "Potential Savings %", colHdngStyle);
        for (int colNum = 16; colNum <= colIndex; colNum++) {
            mainSheet.autoSizeColumn(colNum);
        }
        return reqColSize;
    }

    /**
     * Updates the Summary sheet of Lost Savings Opportunity Report with the list of loads with potential
     * savings.
     * 
     * @param potSavSummary
     *            : MultiMap containing summarized data of potential savings.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void fillLsorSummary(MultiMap potSavSummary) {
        Sheet summarySheet = workbook.getSheetAt(1);
        summarySheet.setDisplayGridlines(false);
        Set<String> keys = potSavSummary.keySet();
        int rowIndex = 2;
        int totalLoads = 0;
        double totalSavAmt = 0;
        double totalCarrAmt = 0;
        Row currentRow;
        for (String key : keys) {
            rowIndex++;
            List carrSavSummList = (List) potSavSummary.get(key);
            currentRow = summarySheet.createRow(rowIndex);
            totalLoads = totalLoads + (int) carrSavSummList.get(0);
            totalSavAmt = totalSavAmt + (double) carrSavSummList.get(1);
            totalCarrAmt = totalCarrAmt + (double) carrSavSummList.get(2);
            fillDataCell(currentRow, 0, key, style);
            fillDataCell(currentRow, 1, (int) carrSavSummList.get(0), style);
            fillDataCell(currentRow, 2, round((double) carrSavSummList.get(1), 2), currencyCellStyle);
            fillDataCell(currentRow, 3, round((double) carrSavSummList.get(3), 4), percCellStyle);
        }
        currentRow = summarySheet.createRow(++rowIndex);
        fillDataCell(currentRow, 0, "Total", summHdngStyle);
        fillDataCell(currentRow, 1, totalLoads, summHdngStyle);
        fillDataCell(currentRow, 2, totalSavAmt, summHdngStyleCurr);
        double totalSavingPerc = 0;
        if (totalCarrAmt != 0) {
            totalSavingPerc = totalSavAmt / totalCarrAmt;
        }
        fillDataCell(currentRow, 3, totalSavingPerc, summHdngStylePerc);
    }

    /**
     * updates the Accessorial legend in Lost Savings Opportunity report.
     * 
     * @param rIndex
     *            : row Index.
     * @param accLegends
     *            : list containing the accessorial code and corresponding name.
     * @return rIndex : row Index.
     */
    private int createAccessorialLegend(int rIndex, List<AccessorialTypeEntity> accLegends) {
        int rowIndex = rIndex;
        Row accLegendHdng = mainSheet.createRow(rowIndex);
        mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));
        Cell accLgndHdng = accLegendHdng.createCell(0);
        accLgndHdng.setCellValue("Accessorial Legend");
        accLgndHdng.setCellStyle(footerHdngStyle);
        rowIndex++;
        int k = 0;
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        for (int j = 0; j < Math.ceil((double) accLegends.size() / 3); j++, rowIndex++) {
            Row currentRow = mainSheet.createRow(rowIndex);
            for (int i = 0; i < 6 && k < accLegends.size(); i = i + 2, k++) {
                mainSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, i, i + 1));
                Cell accCell = currentRow.createCell(i);
                accCell.setCellValue(StringUtils.trimToEmpty(StringUtils.join(accLegends.get(k).getId(), " - ",
                        accLegends.get(k).getDescription())));
                accCell.setCellStyle(style);
            }
            mainSheet.autoSizeColumn(2, true);
            mainSheet.autoSizeColumn(3, true);
        }
        return rowIndex;
    }

    private void buildLSORow(LostSavingsReportBO report, int rowIndex, int reqColSize) {
        Row currentRow = mainSheet.createRow(rowIndex);
        fillDataCell(currentRow, 0, report.getUserName(), style);
        fillDataCell(currentRow, 1, report.getBol(), style);
        fillDataCell(currentRow, 2, report.getPoNum(), style);
        fillDataCell(currentRow, 3, report.getSoNum(), style);
        fillDataCell(currentRow, 4, report.getShipperRefNum(), style);
        fillDataCell(currentRow, 5, report.getShipperName(), style);
        fillDataCell(currentRow, 6, report.getOriginState(), style);
        fillDataCell(currentRow, 7, report.getOriginCity(), style);
        fillDataCell(currentRow, 8, report.getOriginZip(), style);
        fillDataCell(currentRow, 9, report.getConsigneeName(), style);
        fillDataCell(currentRow, 10, report.getDestState(), style);
        fillDataCell(currentRow, 11, report.getDestCity(), style);
        fillDataCell(currentRow, 12, report.getDestZip(), style);
        int index = 13;
        for (LostSavingsMaterialsBO lsmBo : report.getLostSavingsMaterials()) {
            fillDataCell(currentRow, index++, lsmBo.getProductDescription(), style);
            fillDataCell(currentRow, index++, lsmBo.getWeight(), style);
            fillDataCell(currentRow, index++, lsmBo.getClassType(), style);
        }
        while (index <= reqColSize) {
            fillDataCell(currentRow, index++, "", style);
        }
        fillDataCell(currentRow, index++, report.getTotalWeight(), style);
        fillDataCell(currentRow, index++, report.getLoadCreatedDate(), dateCellStyle);
        fillDataCell(currentRow, index++, report.getLoadCreatedDay(), style);
        fillDataCell(currentRow, index++, report.getEstPickupDate(), dateCellStyle);
        fillDataCell(currentRow, index++, report.getPickupDate(), dateCellStyle);
        fillDataCell(currentRow, index++, report.getAccessorials(), style);
        fillDataCell(currentRow, index++, report.getCarrSelected(), style);
        fillDataCell(currentRow, index++, round(report.getCarrAmt(), 2), currencyCellStyle);
        fillDataCell(currentRow, index++, report.getCarrTransitTime(), style);
        fillDataCell(currentRow, index++, report.getLeastCostCarr(), style);
        fillDataCell(currentRow, index++, round(report.getLeastCostAmt(), 2), currencyCellStyle);
        fillDataCell(currentRow, index++, report.getLeastCostTransitTime(), style);
        fillDataCell(currentRow, index++, round(report.getPotentialSavings(), 2), currencyCellStyle);
        fillDataCell(currentRow, index, round(report.getPotSavingsPerc(), 2), style);
    }

    /**
     * Generates Lost Savings Opportunity report as xlsx file.
     */
    public FileInputStreamResponseEntity generateLostSavingsReport(List<LostSavingsReportBO> reports, ReportParamsBO reportParams, List<AccessorialTypeEntity> accLegend) throws IOException {
        fillLostSavingsReport(reports, reportParams, accLegend);
        return super.generateReport(ReportUtil.getSubjectName(reportParams), reportParams.getStartDate(), reportParams.getEndDate());
    }
}
