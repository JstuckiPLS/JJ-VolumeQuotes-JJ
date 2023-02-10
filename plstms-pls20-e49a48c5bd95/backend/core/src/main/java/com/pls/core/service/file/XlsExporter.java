package com.pls.core.service.file;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * Main aim of this entity is to convert objects into XLS file. Client code should specify
 * {@link ColumnDescriptor}s and use {@link #export(List)} method.
 * 
 * @author Maxim Medvedev
 * 
 * @param <T>
 *            Type of data item.
 */
public class XlsExporter<T> {

    private final List<ColumnDescriptor<T>> columns = new ArrayList<ColumnDescriptor<T>>();

    private final String sheetName;
    private final ValueHelper valueHelper = new ValueHelper();

    /**
     * Constructor.
     * 
     * @param columnDescriptors
     *            Not <code>null</code> {@link Collection}.
     * @param pSheetName
     *            Not <code>null</code> {@link String}.
     */
    public XlsExporter(Collection<ColumnDescriptor<T>> columnDescriptors, String pSheetName) {
        columns.addAll(columnDescriptors);
        sheetName = pSheetName;
    }

    /**
     * Convert objects into XLS file.
     * 
     * @param items
     *            Not <code>null</code> {@link List}.
     * @return Not <code>null</code> bytes.
     * @throws IOException
     *             Unable to save data.
     */
    public byte[] export(List<T> items) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        exportToStream(items, result);
        return result.toByteArray();
    }

    /**
     * Convert objects into XLS file and write directly to stream.
     *
     * @param items
     *            Not <code>null</code> {@link List}.
     * @param outputStream output stream to write exported data
     * @throws IOException
     *             Unable to save data.
     */
    public void export(List<T> items, OutputStream outputStream) throws IOException {
        exportToStream(items, outputStream);
    }

    private void exportToStream(List<T> items, OutputStream outputStream) throws IOException {
        try {
            Workbook wb = new HSSFWorkbook();
            Sheet main = wb.createSheet(sheetName);
            if (!columns.isEmpty()) {
                addHeader(main.createRow(0));
                CellStyle dateStyle = prepareDateStyle(main.getWorkbook());
                if (items != null) {
                    for (int indx = 0; indx < items.size(); indx++) {
                        addRow(main.createRow(indx + 1), items.get(indx), dateStyle);
                    }
                }
            }
            wb.write(outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void addHeader(Row row) {
        CellStyle style = prepareHeaderStyle(row.getSheet().getWorkbook());
        for (int indx = 0; indx < columns.size(); indx++) {
            Cell cell = row.createCell(indx, CellType.STRING);
            cell.setCellValue(columns.get(indx).getTitle());
            cell.setCellStyle(style);
        }
    }

    private void addRow(Row row, T item, CellStyle dateStyle) {
        for (int indx = 0; indx < columns.size(); indx++) {
            Cell cell = row.createCell(indx);
            Object value = columns.get(indx).getValue(item);
            if (value == null) {
                cell.setCellType(CellType.BLANK);
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
                cell.setCellStyle(dateStyle);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else {
                cell.setCellValue(valueHelper.prepare(value));
            }
        }
    }

    private CellStyle prepareDateStyle(Workbook workbook) {
        CellStyle result = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        result.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
        return result;
    }

    private CellStyle prepareHeaderStyle(Workbook workbook) {
        CellStyle result = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        result.setFont(font);
        return result;
    }
}
