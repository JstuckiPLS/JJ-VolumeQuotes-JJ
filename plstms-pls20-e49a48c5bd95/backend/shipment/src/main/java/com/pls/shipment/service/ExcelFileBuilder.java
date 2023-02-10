package com.pls.shipment.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This builder builds excel file (stream) with lane data objects. First of all - you have to set left and top
 * margin. After that - all your coordinates should be shifted with values, passed in corresponding methods.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
public class ExcelFileBuilder {

    /**
     * Class represents Sheet row.
     * 
     * @author Viacheslav Vasianovych
     * 
     */
    public class XlRow implements Iterable<Object> {
        private List<Object> values = new ArrayList<Object>();

        /**
         * Add value.
         * 
         * @param value
         *            to add
         * @return this
         */
        public XlRow addValue(Object value) {
            values.add(value);
            return this;
        }

        /**
         * Get values iterator.
         * 
         * @return values iterator
         */
        public Iterator<Object> iterator() {
            return values.iterator();
        }
    }

    private List<String> headers = new ArrayList<String>();

    private List<XlRow> rows = new ArrayList<XlRow>();

    private int firstColumn = 0;

    private int firstRow = 0;

    /**
     * We do step over passed "columns", and next one will be our first column.
     * 
     * @param columns
     *            - count of "columns", that we need to skip before first column.
     */
    public void setLeftMargin(int columns) {
        firstColumn = columns;
    }

    /**
     * We do step over passed "rows", and next one will be our first row.
     * 
     * @param rows
     *            - count of rows, that we need to skip before first row.
     */
    public void setTopMargin(int rows) {
        firstRow = rows;
    }

    /**
     * Add column title to the excel document.
     * 
     * @param headerTitle
     *            - String title of the column
     */
    public void addHeader(String headerTitle) {
        headers.add(headerTitle);
    }

    /**
     * Add row to the excel document.
     * 
     * @param values
     *            array of values for new row.
     * @return {@link XlRow}
     */
    public XlRow addRow(Object... values) {
        XlRow row = new XlRow();
        for (Object value : values) {
            row.addValue(value);
        }
        rows.add(row);
        return row;
    }

    /**
     * Add skip columns and rows to the document.
     * 
     * @param sheet
     *            - current excel sheet.
     */
    private void addMargins(Sheet sheet) {
        sheet.createRow(firstRow).createCell(firstColumn);
    }

    /**
     * Appends header cell to the excel document. Skips neccessary count of rows and columns.
     * 
     * @param sheet
     *            - current excel sheet
     * @param column
     *            - column position (related)
     * @param title
     *            - Title string of the column
     */
    private void addHeaderCell(Sheet sheet, int column, String title) {
        sheet.getRow(firstRow).createCell(firstColumn + column).setCellValue(title);
    }

    /**
     * Appends row cells to the excel document.
     * 
     * @param sheet
     *            - current excel sheet
     * @param xlRow
     *            - excel row object
     */
    private void addRowCells(Sheet sheet, XlRow xlRow) {
        int nextRowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(nextRowNum);
        int nextCell = firstColumn;
        for (Object value : xlRow) {
            Cell cell = row.createCell(nextCell);
            if (value != null) {
                cell.setCellValue(value.toString());
            }
            nextCell++;
        }
    }

    /**
     * Writes result to output stream (build file).
     * 
     * @param stream
     *            - target stream
     * @throws IOException
     *             if can't write stream to excel file
     */
    public void buildExcelFile(OutputStream stream) throws IOException {
        Workbook w = new XSSFWorkbook();
        Sheet sheet = w.createSheet();
        addMargins(sheet);
        for (int column = 0; column < headers.size(); column++) {
            String title = headers.get(column);
            addHeaderCell(sheet, column, title);
        }
        for (XlRow row : rows) {
            addRowCells(sheet, row);
        }
        w.write(stream);
    }
}
