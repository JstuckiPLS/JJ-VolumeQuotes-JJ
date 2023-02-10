package com.pls.core.service.fileimport.parser.core.excel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.excel.ExcelField} class.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelFieldTest {

    private static final String TEST_SHEET_NAME = "TestSheet";

    @Test(expected = NullPointerException.class)
    public void testExcelRecordFieldWithNull() throws Exception {
        ExcelField sut = new ExcelField(null);

        sut.getName();
    }

    @Test
    public void testGetBigDecimalWithBlank() throws Exception {
        ExcelField sut = new ExcelField(prepareBlankCell());

        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBigDecimal());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBigDecimalWithInvalidData() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
        sut.getBigDecimal();
    }

    @Test
    public void testGetBigDecimalWithNormalStringValue() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("0.3"));

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new BigDecimal("0.3"), sut.getBigDecimal());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBooleanWithBlank() throws Exception {
        ExcelField sut = new ExcelField(prepareBlankCell());

        Assert.assertTrue(sut.isEmpty());
        sut.getBoolean();
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBooleanWithInvalidData() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
        sut.getBoolean();
    }

    @Test
    public void testGetBooleanWithNormalCase() throws Exception {
        ExcelField sut1 = new ExcelField(prepareCell("true"));

        Assert.assertFalse(sut1.isEmpty());
        Assert.assertTrue(sut1.getBoolean());

        ExcelField sut2 = new ExcelField(prepareCell("TRUE"));

        Assert.assertFalse(sut2.isEmpty());
        Assert.assertTrue(sut2.getBoolean());

        ExcelField sut3 = new ExcelField(prepareCell("YES"));

        Assert.assertFalse(sut3.isEmpty());
        Assert.assertTrue(sut3.getBoolean());

        ExcelField sut4 = new ExcelField(prepareCell("false"));

        Assert.assertFalse(sut4.isEmpty());
        Assert.assertFalse(sut4.getBoolean());

        ExcelField sut5 = new ExcelField(prepareCell("FALSE"));

        Assert.assertFalse(sut5.isEmpty());
        Assert.assertFalse(sut5.getBoolean());

        ExcelField sut6 = new ExcelField(prepareCell("NO"));

        Assert.assertFalse(sut6.isEmpty());
        Assert.assertFalse(sut6.getBoolean());

        ExcelField sut7 = new ExcelField(prepareCell(Boolean.FALSE));

        Assert.assertFalse(sut7.isEmpty());
        Assert.assertFalse(sut7.getBoolean());

        ExcelField sut8 = new ExcelField(prepareCell(Boolean.TRUE));

        Assert.assertFalse(sut8.isEmpty());
        Assert.assertTrue(sut8.getBoolean());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetDateWithBlank() throws Exception {
        ExcelField sut = new ExcelField(prepareBlankCell());

        Assert.assertTrue(sut.isEmpty());
        sut.getDate();
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetDateWithInvalidData() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
        sut.getDate();
    }

    @Test
    public void testGetDateWithNormalStringDate() throws Exception {
        ExcelField sut = new ExcelField(
                prepareCell(new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-01")));

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-01"), sut.getDate());
    }

    @Test
    public void testGetDateWithNormalStringValue() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("02/01/2012"));

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-01"), sut.getDate());
    }

    @Test
    public void testGetLongWithBlank() throws Exception {
        ExcelField sut = new ExcelField(prepareBlankCell());

        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getLong());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetLongWithInvalidData() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
        sut.getLong();
    }

    @Test
    public void testGetLongWithNormalStringValue() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("3"));

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new Long(3), sut.getLong());
    }

    @Test
    public void testGetNameWithNormalCase() throws Exception {
        ExcelField sut = new ExcelField(prepareCell(""));

        Assert.assertEquals("TestSheet:A1", sut.getName());
    }

    @Test
    public void testGetStringWithBlank() throws Exception {
        ExcelField sut = new ExcelField(prepareBlankCell());

        Assert.assertTrue(sut.isEmpty());
        Assert.assertEquals(StringUtils.EMPTY, sut.getString());
    }

    @Test
    public void testGetStringWithNormalCase() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals("Test", sut.getString());
    }

    @Test
    public void testIsEmptyWithNonEmptyData() throws Exception {
        ExcelField sut = new ExcelField(prepareCell("Test"));

        Assert.assertFalse(sut.isEmpty());
    }

    @Test
    public void testIsEmptyWithNormalCase() throws Exception {
        ExcelField sut = new ExcelField(prepareCell(""));

        Assert.assertTrue(sut.isEmpty());
    }

    private CellStyle getDateCellStyle(Sheet sheet) {
        DataFormat dataFormat = sheet.getWorkbook().getCreationHelper().createDataFormat();
        CellStyle result = sheet.getWorkbook().createCellStyle();
        result.setDataFormat(dataFormat.getFormat("m/d/yyyy h:mm"));

        return result;
    }

    private Cell prepareBlankCell() {
        return prepareRow().createCell(0, CellType.BLANK);
    }

    private Cell prepareCell(Boolean booleanValue) {
        Cell result = prepareRow().createCell(0, CellType.BOOLEAN);
        result.setCellValue(booleanValue);
        return result;
    }

    private Cell prepareCell(Date value) {
        Cell result = prepareRow().createCell(0);
        result.setCellValue(DateUtils.toCalendar(value));
        result.setCellStyle(getDateCellStyle(result.getSheet()));
        return result;
    }

    private Cell prepareCell(String value) {
        Cell result = prepareRow().createCell(0, CellType.STRING);
        result.setCellValue(value);
        return result;
    }

    private Row prepareRow() {
        Workbook workbook = new SXSSFWorkbook();
        Row row = workbook.createSheet(TEST_SHEET_NAME).createRow(0);
        return row;
    }
}
