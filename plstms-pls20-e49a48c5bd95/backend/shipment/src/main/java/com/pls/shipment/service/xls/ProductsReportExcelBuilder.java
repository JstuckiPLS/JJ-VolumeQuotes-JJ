package com.pls.shipment.service.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.service.xls.AbstractReportExcelBuilder;
import com.pls.shipment.domain.LtlProductEntity;

/**
 * Products Report Builder.
 * 
 * @author Alexander Nalapko
 * 
 */
public class ProductsReportExcelBuilder extends AbstractReportExcelBuilder {

    private static final String DASH = "-";

    /**
     * Constructor.
     * 
     * @param revenueReportTemplate
     *            template
     * @throws IOException
     *             exception
     */
    public ProductsReportExcelBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    /**
     * Generates products report as xlsx file.
     * 
     * @param reports
     *            objects.
     * @return report data as input stream at response.
     * @throws IOException
     *             if can't generate report
     */
    public FileInputStreamResponseEntity generateReport(List<LtlProductEntity> reports) throws IOException {
        fillSheet(reports);
        mainSheet.autoSizeColumn(13);
        File tempFile = File.createTempFile("reportData", "tmp");
        SimpleDateFormat date = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        try {
            String fileName = "Products Export " + date.format(new Date()) + ".xlsx";
            workbook.write(new FileOutputStream(tempFile));
            return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private void fillSheet(List<LtlProductEntity> reports) {
        /**
         * add products
         */
        for (int i = 0; i < reports.size(); i++) {
            builsSimpleCell(reports.get(i), i + 1, style);
        }
    }

    private int builsSimpleCell(LtlProductEntity report, int rowIndex, CellStyle style) {
        Row currentRow = mainSheet.createRow(rowIndex);
        int cellIndex = 0;
        fillDataCell(currentRow, cellIndex++, report.getDescription(), style);
        StringBuffer nmfc = new StringBuffer();
        if (report.getNmfcNum() != null) {
            nmfc.append(report.getNmfcNum());
            if (report.getNmfcSubNum() != null) {
                nmfc.append(DASH);
                nmfc.append(report.getNmfcSubNum());
            }
        }
        fillDataCell(currentRow, cellIndex++, nmfc.toString(), style);
        fillDataCell(currentRow, cellIndex++, report.getCommodityClass().getDbCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getProductCode(), style);
        fillDataCell(currentRow, cellIndex++, report.isShared(), style);
        fillDataCell(currentRow, cellIndex++, report.isHazmat(), style);
        if (report.getHazmatInfo() != null) {
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getUnNumber(), style);
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getPackingGroup(), style);
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getHazmatClass(), style);
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getEmergencyCompany(), style);
            String extension = null;
            if (report.getHazmatInfo().getEmergencyPhone() != null) {
                extension = report.getHazmatInfo().getEmergencyPhone().getExtension();
                report.getHazmatInfo().getEmergencyPhone().setExtension("");
            }
            fillDataCell(currentRow, cellIndex++, PhoneUtils.format(report.getHazmatInfo().getEmergencyPhone()), style);
            fillDataCell(currentRow, cellIndex++, extension, style);
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getEmergencyContract(), style);
            fillDataCell(currentRow, cellIndex++, report.getHazmatInfo().getInstructions(), style);
        } else {
            /**
             * added empty cells if hazmatInfo missing.
             */
            fillEmptyCells(currentRow, cellIndex, style, 8);
        }
        return cellIndex;
    }
}
