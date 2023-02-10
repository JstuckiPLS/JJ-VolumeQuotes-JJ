package com.pls.core.service.fileimport.parser.core.excel;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.excel.ExcelDocument} class.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelDocumentTest {

    private static final String TEST_SHEET_NAME1 = "TestSheetName1";
    private static final String TEST_SHEET_NAME2 = "TestSheetName2";

    @Test(expected = ImportException.class)
    public void testConstructorWithEmptyStream() throws Exception {
        new ExcelDocument(prepareTextFile(StringUtils.EMPTY));
    }

    @Test(expected = InvalidFormatException.class)
    public void testConstructorWithInvalidContent() throws Exception {
        new ExcelDocument(prepareTextFile("testCol1, testCol2"));
    }

    @Test(expected = ImportException.class)
    public void testConstructorWithNull() throws Exception {
        new ExcelDocument(null);
    }

    @Test
    public void testIterator() throws Exception {
        ExcelDocument sut = new ExcelDocument(prepareExcelFile());

        Iterator<Page> result = sut.iterator();

        Assert.assertNotNull(result);

        Assert.assertTrue(result.hasNext());
        Page page1 = result.next();
        Assert.assertNotNull(page1);
        Assert.assertEquals(TEST_SHEET_NAME1, page1.getName());

        Assert.assertTrue(result.hasNext());
        Page page2 = result.next();
        Assert.assertNotNull(page2);
        Assert.assertEquals(TEST_SHEET_NAME2, page2.getName());

        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testRemoveRecord() throws Exception {
        ExcelDocument sut = new ExcelDocument(prepareExcelFileWithRecords());

        Page page = sut.iterator().next();
        Iterator<Record> iterator = page.iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        checkDocumentPageSize(sut, 1);
    }

    @Test
    public void testDoubleRemoveRecord() throws Exception {
        ExcelDocument sut = new ExcelDocument(prepareExcelFileWithRecords());

        Page page = sut.iterator().next();
        Iterator<Record> iterator = page.iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            iterator.remove();
        }

        checkDocumentPageSize(sut, 1);
    }

    private void checkDocumentPageSize(Document doc, int expectedRowsCount) throws Exception {
        int expectedSheetsCount = 1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        doc.write(outStream);

        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        assertNotNull(inStream);

        try {
            Workbook workbook = WorkbookFactory.create(inStream);
            assertNotNull(workbook);
            assertEquals(expectedSheetsCount, workbook.getNumberOfSheets());

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet currentSheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> rowIterator = currentSheet.iterator();

                assertEquals(expectedRowsCount, getActualRowsCount(rowIterator));
            }
        } finally {
            inStream.close();
        }
    }

    private InputStream prepareExcelFileWithRecords() throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(TEST_SHEET_NAME1);
        sheet.createRow(0).createCell(0).setCellValue("HEADER");
        sheet.createRow(1).createCell(0).setCellValue("FIRST");
        sheet.createRow(2).createCell(0).setCellValue("SECCOND");
        sheet.createRow(3).createCell(0).setCellValue("THIRD");
        sheet.createRow(4).createCell(0).setCellValue("FOURTH");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private int getActualRowsCount(Iterator<Row> iterator) {
        int count = 0;
        Row row = null;

        while (iterator.hasNext()) {
            row = iterator.next();

            if (!checkRowIsEmpty(row)) {
                count++;
            }
        }

        return count;
    }

    private boolean checkRowIsEmpty(Row row) {
        boolean isEmpty = true;

        for (Cell cell : row) {
            String cellValue = cell.getStringCellValue();
            if (StringUtils.isNotBlank(cellValue)) {
                isEmpty = false;
                break;
            }
        }

        return isEmpty;
    }

    private InputStream prepareExcelFile() throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        workbook.createSheet(TEST_SHEET_NAME1).createRow(0);
        workbook.createSheet(TEST_SHEET_NAME2).createRow(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private InputStream prepareTextFile(String text) {
        return IOUtils.toInputStream(text);
    }
}
