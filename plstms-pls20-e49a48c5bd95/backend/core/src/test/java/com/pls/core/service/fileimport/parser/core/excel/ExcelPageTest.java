package com.pls.core.service.fileimport.parser.core.excel;

import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.excel.ExcelPage} class.
 * 
 * @author Artem Arapov
 *
 */
public class ExcelPageTest {

    private static final String TEST_SHEET_NAME = "TestSheet";

    @Test
    public void testConstructorWithEmptyData() throws Exception {
        Page sut = new ExcelPage(prepareContent(0));

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());

        Record header1 = sut.getHeader();
        Record header2 = sut.getHeader();
        Assert.assertNotNull(header1);
        Assert.assertEquals(header1, header2);
    }

    @Test(expected = ImportFileParseException.class)
    public void testConstructorWithNull() throws Exception {
        new ExcelPage(null);
    }

    @Test
    public void testGetHeader() throws Exception {
        Page sut = new ExcelPage(prepareContent(1));

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());

        Record header1 = sut.getHeader();
        Record header2 = sut.getHeader();
        Assert.assertNotNull(header1);
        Assert.assertEquals(header1, header2);
    }

    @Test
    public void testGetName() throws Exception {
        Page sut = new ExcelPage(prepareContent(1));

        Assert.assertEquals(TEST_SHEET_NAME, sut.getName());
    }

    @Test
    public void testIsEmptyWithData() throws Exception {
        Page sut = new ExcelPage(prepareContent(2));

        Assert.assertNotNull(sut);
        Assert.assertFalse(sut.isEmpty());
    }

    @Test
    public void testIsEmptyWithoutData() throws Exception {
        Page sut = new ExcelPage(prepareContent(0));

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());
    }

    @Test
    public void testIsEmptyWithSingleHeader() throws Exception {
        Page sut = new ExcelPage(prepareContent(1));

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());
    }

    @Test
    public void testIteratorWithData() throws Exception {
        Page sut = new ExcelPage(prepareContent(3));

        Assert.assertNotNull(sut);
        Iterator<Record> iterator = sut.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertTrue(iterator.hasNext());
        Record row1 = iterator.next();
        Assert.assertNotNull(row1);
        Assert.assertEquals("TestSheet:2", row1.getName());

        Assert.assertTrue(iterator.hasNext());
        Record row2 = iterator.next();
        Assert.assertEquals("TestSheet:3", row2.getName());

        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorWithoutData() throws Exception {
        Page sut = new ExcelPage(prepareContent(1));

        Assert.assertNotNull(sut);
        Iterator<Record> iterator = sut.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertFalse(iterator.hasNext());
    }

    private Sheet prepareContent(int dataItems) {
        Workbook workbook = new SXSSFWorkbook();
        Sheet result = workbook.createSheet(TEST_SHEET_NAME);
        for (int rowNum = 0; rowNum < dataItems; rowNum++) {
            result.createRow(rowNum);
        }
        return result;
    }
}
