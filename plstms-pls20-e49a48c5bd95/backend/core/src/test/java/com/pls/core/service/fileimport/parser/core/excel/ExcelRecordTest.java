package com.pls.core.service.fileimport.parser.core.excel;

import com.pls.core.service.fileimport.parser.core.Field;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.excel.ExcelRecord} class.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelRecordTest {

    private static final String TEST_SHEET_NAME = "TestSheet";

    @Test(expected = NullPointerException.class)
    public void testExcelRecordWithNull() {
        ExcelRecord sut = new ExcelRecord(null);

        sut.getName();
    }

    @Test
    public void testGetFieldCount() {
        ExcelRecord sut = new ExcelRecord(prepareRow(3));

        Assert.assertEquals(3, sut.getFieldsCount());
    }

    @Test
    public void testGetFieldWithBigIndex() {
        ExcelRecord sut = new ExcelRecord(prepareRow(3));

        Assert.assertEquals(3, sut.getFieldsCount());
        Field field = sut.getFieldByIdx(10);
        Assert.assertNotNull(field);
        Assert.assertTrue(field.isEmpty());
    }

    @Test
    public void testGetFieldWithNegativeIndex() throws Exception {
        ExcelRecord sut = new ExcelRecord(prepareRow(3));

        Assert.assertEquals(3, sut.getFieldsCount());
        Field field = sut.getFieldByIdx(-1);
        Assert.assertNotNull(field);
        Assert.assertTrue(field.isEmpty());
    }

    @Test
    public void testGetFieldNormalCase() throws Exception {
        ExcelRecord sut = new ExcelRecord(prepareRow(3));

        Assert.assertEquals(3, sut.getFieldsCount());
        Assert.assertEquals("TestSheet:A1", sut.getFieldByIdx(0).getName());
        Assert.assertEquals("TestSheet:B1", sut.getFieldByIdx(1).getName());
        Assert.assertEquals("TestSheet:C1", sut.getFieldByIdx(2).getName());
    }

    @Test
    public void testGetName() {
        ExcelRecord sut = new ExcelRecord(prepareRow(0));

        Assert.assertEquals(TEST_SHEET_NAME + ":1", sut.getName());
    }

    @Test
    public void testEmptyRecord() throws Exception {
        ExcelRecord sut = new ExcelRecord(prepareRow(3));

        Assert.assertEquals(3, sut.getFieldsCount());
        Assert.assertTrue(sut.isEmpty());
    }

    @Test
    public void testNotEmptyRecord() throws Exception {
        ExcelRecord sut = new ExcelRecord(prepareRowWithValues(3));

        Assert.assertEquals(3, sut.getFieldsCount());
        Assert.assertFalse(sut.isEmpty());
    }

    private Row prepareRow(int fieldNumber) {
        Workbook workbook = new SXSSFWorkbook();
        Row result = workbook.createSheet(TEST_SHEET_NAME).createRow(0);
        for (int cellIndx = 0; cellIndx < fieldNumber; cellIndx++) {
            result.createCell(cellIndx);
        }
        return result;
    }

    private Row prepareRowWithValues(int fieldNumber) {
        Workbook workbook = new SXSSFWorkbook();
        Row result = workbook.createSheet(TEST_SHEET_NAME).createRow(0);
        for (int cellIndx = 0; cellIndx < fieldNumber; cellIndx++) {
            result.createCell(cellIndx).setCellValue(cellIndx);
        }
        return result;
    }
}
