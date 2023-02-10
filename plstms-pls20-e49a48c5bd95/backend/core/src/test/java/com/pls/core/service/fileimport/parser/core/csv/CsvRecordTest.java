package com.pls.core.service.fileimport.parser.core.csv;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.Field;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.csv.CsvRecord} class.
 * 
 * @author Artem Arapov
 *
 */
public class CsvRecordTest {

    @Test
    public void testCsvRecordItemWithNull() {
        CsvRecord sut = new CsvRecord(null, 3);

        Assert.assertEquals(0, sut.getFieldsCount());
    }

    @Test
    public void testGetFieldCount() {
        CsvRecord sut = new CsvRecord(prepareContent(3), 1);

        Assert.assertEquals(3, sut.getFieldsCount());
    }

    @Test
    public void testGetFieldWhitBigIndex() {
        CsvRecord sut = new CsvRecord(prepareContent(3), 1);

        Assert.assertEquals(3, sut.getFieldsCount());
        Field field = sut.getFieldByIdx(10);
        Assert.assertNotNull(field);
        Assert.assertTrue(field.isEmpty());
    }

    @Test
    public void testGetFieldWhitNegativeIndex() throws ImportException {
        CsvRecord sut = new CsvRecord(prepareContent(3), 1);

        Assert.assertEquals(3, sut.getFieldsCount());
        Field field = sut.getFieldByIdx(-1);
        Assert.assertNotNull(field);
        Assert.assertTrue(field.isEmpty());
    }

    @Test
    public void testGetFieldWhitNormalCase() throws ImportException {
        CsvRecord sut = new CsvRecord(prepareContent(3), 1);

        Assert.assertEquals(3, sut.getFieldsCount());
        Assert.assertEquals("Field0", sut.getFieldByIdx(0).getString());
        Assert.assertEquals("Field1", sut.getFieldByIdx(1).getString());
        Assert.assertEquals("Field2", sut.getFieldByIdx(2).getString());
    }

    @Test
    public void testGetName() {
        CsvRecord sut = new CsvRecord(null, 3);

        Assert.assertEquals("ROW#3", sut.getName());
    }

    @Test
    public void testEmptyFields() {
        CsvRecord sut = new CsvRecord(prepareContent(0), 1);

        Assert.assertEquals(0, sut.getFieldsCount());
        Assert.assertTrue(sut.isEmpty());
    }

    private String[] prepareContent(int fieldNumber) {
        String[] strings = new String[fieldNumber];
        for (int indx = 0; indx < fieldNumber; indx++) {
            strings[indx] = "Field" + indx;
        }
        return strings;
    }
}
