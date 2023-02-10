package com.pls.core.service.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.io.ClassPathResource;

import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.service.file.FileInputStreamResponseEntity;
import com.pls.core.service.util.PhoneUtils;

/**
 * Addresses Report Builder.
 * 
 * @author Alexander Nalapko
 * 
 */
public class AddressesReportExcelBuilder extends AbstractReportExcelBuilder {
    private static final SimpleDateFormat TIME = new SimpleDateFormat("hh:mm a", Locale.US);

    /**
     * Constructor.
     * 
     * @param revenueReportTemplate
     *            template
     * @throws IOException
     *             exception
     */
    public AddressesReportExcelBuilder(ClassPathResource revenueReportTemplate) throws IOException {
        super(revenueReportTemplate);
    }

    /**
     * Generates addresses report as xlsx file.
     * 
     * @param reports
     *            objects.
     * @return report data as input stream at response.
     * @throws IOException
     *             if can't generate report
     */
    public FileInputStreamResponseEntity generateReport(List<UserAddressBookEntity> reports) throws IOException {
        fillSheet(reports);
        mainSheet.autoSizeColumn(18);
        mainSheet.autoSizeColumn(19);
        File tempFile = File.createTempFile("reportData", "tmp");
        SimpleDateFormat date = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        try {
            String fileName = "Addresses Export " + date.format(new Date()) + ".xlsx";
            workbook.write(new FileOutputStream(tempFile));
            return new FileInputStreamResponseEntity(new FileInputStream(tempFile), tempFile.length(), fileName);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private void fillSheet(List<UserAddressBookEntity> reports) {
        /**
         * add products
         */
        for (int i = 0; i < reports.size(); i++) {
            builsSimpleCell(reports.get(i), i + 1, style);
        }
    }

    private int builsSimpleCell(UserAddressBookEntity report, int rowIndex, CellStyle style) {
        Row currentRow = mainSheet.createRow(rowIndex);
        int cellIndex = 0;
        fillDataCell(currentRow, cellIndex++, report.getAddressName(), style);
        fillDataCell(currentRow, cellIndex++, report.getAddressCode(), style);
        fillDataCell(currentRow, cellIndex++, report.getContactName(), style);
        if (report.getAddress() != null) {
            fillDataCell(currentRow, cellIndex++, report.getAddress().getCountryCode(), style);
            fillDataCell(currentRow, cellIndex++, report.getAddress().getAddress1(), style);
            fillDataCell(currentRow, cellIndex++, report.getAddress().getAddress2(), style);
            fillDataCell(currentRow, cellIndex++, report.getAddress().getCity(), style);
            fillDataCell(currentRow, cellIndex++, report.getAddress().getStateCode(), style);
            fillDataCell(currentRow, cellIndex++, report.getAddress().getZip(), style);
        } else {
            /**
             * added empty cells if hazmatInfo missing.
             */
            fillEmptyCells(currentRow, cellIndex, style, 6);
        }
        String extension = null;
        if (report.getPhone() != null) {
            extension = report.getPhone().getExtension();
            report.getPhone().setExtension("");
        }
        fillDataCell(currentRow, cellIndex++, PhoneUtils.format(report.getPhone()), style);
        fillDataCell(currentRow, cellIndex++, extension, style);
        if (report.getFax() != null) {
            report.getFax().setExtension("");
        }
        fillDataCell(currentRow, cellIndex++, PhoneUtils.format(report.getFax()), style);
        fillDataCell(currentRow, cellIndex++, report.getEmail(), style);
        fillDataCell(currentRow, cellIndex++, report.getPersonId() == null, style);

        fillDataCell(currentRow, cellIndex++, writeTime(report.getPickupFrom()), style);
        fillDataCell(currentRow, cellIndex++, writeTime(report.getPickupTo()), style);
        fillDataCell(currentRow, cellIndex++, writeTime(report.getDeliveryFrom()), style);
        fillDataCell(currentRow, cellIndex++, writeTime(report.getDeliveryTo()), style);
        fillDataCell(currentRow, cellIndex++, report.getPickupNotes(), style);
        fillDataCell(currentRow, cellIndex++, report.getDeliveryNotes(), style);
        return cellIndex;
    }

    private Date toDate(Time time) {
        Date date = new Date();
        if (time != null) {
            date.setTime(time.getTime());
        }
        return date;
    }

    private String writeTime(Time time) {
        if (time != null) {
            synchronized (this) {
                return TIME.format(toDate(time));
            }
        } else {
            return "";
        }
    }
}
