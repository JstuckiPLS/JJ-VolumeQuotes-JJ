package com.pls.shipment.service.xls;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.bo.ReportsBO;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.xls.AbstractReportExcelBuilder;

/**
 * Unbilled Report Excel Builder.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class UnbilledReportExcelBuilder extends AbstractReportExcelBuilder {

    public static final String UNBILLED_BY_CUSTOMER_TITLE = "<All> Unbilled by Customer";
    public static final String UNBILLED_BY_BUSINESS_TITLE = "Unbilled by Business Unit";
    private static final String CURRENT_DATE = new SimpleDateFormat("MMM dd, yyy", Locale.US).format(new Date());

    /**
     * Instantiates a new report excel builder.
     *
     * @param revenueReportTemplate the revenue report template
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public UnbilledReportExcelBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    private void fillSheet(List<ReportsBO> reports, String businessUnitName, String companyCodeDescription) {
        workbook.setActiveSheet(0);
        int rowIndex = 8;

        String reportTitle;
        if (StringUtils.isNotBlank(businessUnitName)) {
            reportTitle = StringUtils.join("<", businessUnitName, "> ", UNBILLED_BY_BUSINESS_TITLE);
        } else if (StringUtils.isNotBlank(companyCodeDescription)) {
            reportTitle = StringUtils.join("<", companyCodeDescription, "> ", UNBILLED_BY_BUSINESS_TITLE);
        } else {
            reportTitle = UNBILLED_BY_CUSTOMER_TITLE;
        }
        fillDataCell(mainSheet.getRow(5), 0, reportTitle, rptHdngStyle);
        fillDataCell(mainSheet.getRow(6), 0, CURRENT_DATE, dateCellStyle);

        for (ReportsBO report : reports) {
            buildRow(report, rowIndex);
            rowIndex++;
        }

        fillDataCell(mainSheet.getRow(rowIndex - 1), 7, "", style);
        Row currentRow = mainSheet.createRow(rowIndex);
        fillDataCell(currentRow, 0, CURRENT_DATE, dateCellStyle);
        for (int columnNum = 1; columnNum < 7; columnNum++) {
            fillDataCell(currentRow, columnNum, "", style);
        }
        fillDataCell(currentRow, 7, DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()), dateCellStyle);
    }

    private void buildRow(ReportsBO report, int rowIndex) {
        Row currentRow = mainSheet.createRow(rowIndex);
        fillDataCell(currentRow, 0, report.getShipperCode(), style);
        fillDataCell(currentRow, 1, report.getOwnerName(), style);
        fillDataCell(currentRow, 2, report.getLoadId(), style);
        fillDataCell(currentRow, 3, report.getShipDate());
        fillDataCell(currentRow, 4, report.getGlDate());
        fillDataCell(currentRow, 5, report.getRevenue());
        fillDataCell(currentRow, 6, report.getCost());
        fillDataCell(currentRow, 7, report.getMargin(), style);
    }

    /**
     * Generates revenue report as xlsx file.
     * 
     * @param reports objects.
     * @param businessUnitName the business unit name of report.
     * @param companyCodeDescription the description of company code.
     * @return report data as input stream at response.
     * @param customerName name of customer.
     * @param startDate the start date of the report.
     * @param endDate the end date of report.
     * @throws IOException if can't generate report
     */
    public FileInputStreamResponseEntity generateReport(List<ReportsBO> reports,
            String businessUnitName, String companyCodeDescription, String customerName, Date startDate, Date endDate) throws IOException {
        fillSheet(reports, businessUnitName, companyCodeDescription);
        return super.generateReport(customerName, startDate, endDate);
    }
}
