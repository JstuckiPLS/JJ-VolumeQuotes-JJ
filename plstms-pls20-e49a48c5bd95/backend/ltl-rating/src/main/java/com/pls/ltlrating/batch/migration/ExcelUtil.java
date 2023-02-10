package com.pls.ltlrating.batch.migration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.core.io.Resource;

/**
 * Util class to work with excel file.
 *
 * @author Aleksandr Leshchenko
 */
public final class ExcelUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);

    private static final Integer HEADER_FONT_SIZE = 10;

    /**
     * Private constructor to prevent from instantiating and inheritance.
     */
    private ExcelUtil() {
    }

    /**
     * Get header style.
     *
     * @param workbook
     *            workbook
     * @return header style
     */
    public static CellStyle getHeaderStyle(final Workbook workbook) {
        final CellStyle cellStyle = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setFontHeightInPoints(HEADER_FONT_SIZE.shortValue());
        font.setFontName("Arial");
        font.setBold(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * Serialize workbook to the specified resource.
     *
     * @param workbook
     *            workbook to serialize
     * @param resource
     *            destination resource
     */
    public static void serializeWorkbook(final Workbook workbook, final Resource resource) {
        LOG.info("serializeWorkbook");
        try {
            if (workbook != null) {
                FileUtils.setUpOutputFile(resource.getFile(), false, false, true);
                FileOutputStream fileOutputStream = new FileOutputStream(resource.getFile());
                workbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            LOG.error("Exception on work book serialization", e);
        }
    }

    /**
     * Add headers to the workbook.
     *
     * @param headers
     *            headers to add
     * @param workbook
     *            workbook
     */
    public static void addHeaders(final Collection<String> headers, final Workbook workbook) {
        if (CollectionUtils.isNotEmpty(headers)) {
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle headerCellStyle = ExcelUtil.getHeaderStyle(workbook);
            Row row = sheet.createRow(0);
            int col = 0;
            for (String header : headers) {
                Cell cell = row.createCell(col++);
                cell.setCellStyle(headerCellStyle);
                cell.setCellValue(header);
            }
        }
    }
}
