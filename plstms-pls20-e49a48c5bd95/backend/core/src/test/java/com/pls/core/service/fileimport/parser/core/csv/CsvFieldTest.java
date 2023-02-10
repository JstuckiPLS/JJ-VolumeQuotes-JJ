package com.pls.core.service.fileimport.parser.core.csv;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.fileimport.parser.core.BaseField;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.csv.CsvField} class.
 * 
 * @author Artem Arapov
 *
 */
public class CsvFieldTest {

    @Test
    public void testCsvRecordFieldWithNull() throws Exception {
        CsvField sut = new CsvField(null, null, -1);

        Assert.assertEquals("null; Col#-1", sut.getName());
        Assert.assertTrue(sut.isEmpty());
        Assert.assertEquals(StringUtils.EMPTY, sut.getString());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBigDecimalWithInvalidData() throws Exception {
        BaseField sut = new CsvField("TestData", null, -1);

        Assert.assertFalse(sut.isEmpty());
        sut.getBigDecimal();
    }

    @Test
    public void testGetBigDecimalWithNormalCase() throws Exception {
        BaseField sut = new CsvField("0.3", null, -1);

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new BigDecimal("0.3"), sut.getBigDecimal());
    }

    @Test
    public void testGetBigDecimalWithNull() throws Exception {
        BaseField sut = new CsvField(null, null, -1);

        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBigDecimal());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBooleanWithInvalidData() throws Exception {
        BaseField sut = new CsvField("TestData", null, -1);

        Assert.assertFalse(sut.isEmpty());
        sut.getBoolean();
    }

    @Test
    public void testGetBooleanWithNormalCase() throws Exception {
        BaseField sut1 = new CsvField("true", null, -1);

        Assert.assertFalse(sut1.isEmpty());
        Assert.assertTrue(sut1.getBoolean());

        BaseField sut2 = new CsvField("TRUE", null, -1);

        Assert.assertFalse(sut2.isEmpty());
        Assert.assertTrue(sut2.getBoolean());

        BaseField sut3 = new CsvField("YES", null, -1);

        Assert.assertFalse(sut3.isEmpty());
        Assert.assertTrue(sut3.getBoolean());

        BaseField sut4 = new CsvField("false", null, -1);

        Assert.assertFalse(sut4.isEmpty());
        Assert.assertFalse(sut4.getBoolean());

        BaseField sut5 = new CsvField("FALSE", null, -1);

        Assert.assertFalse(sut5.isEmpty());
        Assert.assertFalse(sut5.getBoolean());

        BaseField sut6 = new CsvField("NO", null, -1);

        Assert.assertFalse(sut6.isEmpty());
        Assert.assertFalse(sut6.getBoolean());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetBooleanWithNull() throws Exception {
        BaseField sut = new CsvField(null, null, -1);

        Assert.assertTrue(sut.isEmpty());
        sut.getBoolean();
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetDateWithInvalidData() throws Exception {
        BaseField sut = new CsvField("TestData", null, -1);

        Assert.assertFalse(sut.isEmpty());
        sut.getDate();
    }

    @Test
    public void testGetDateWithNormalCase() throws Exception {
        BaseField sut = new CsvField("02/01/2012", null, -1);

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-01"), sut.getDate());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetDateWithNull() throws Exception {
        BaseField sut = new CsvField(null, null, -1);

        Assert.assertTrue(sut.isEmpty());
        sut.getDate();
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testGetLongWithInvalidData() throws Exception {
        BaseField sut = new CsvField("TestData", null, -1);

        Assert.assertFalse(sut.isEmpty());
        sut.getLong();
    }

    @Test
    public void testGetLongWithNormalCase() throws Exception {
        BaseField sut = new CsvField("3", null, -1);

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(new Long(3), sut.getLong());
    }

    @Test
    public void testGetLongWithNull() throws Exception {
        BaseField sut = new CsvField(null, null, -1);

        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getLong());
    }

    @Test
    public void testGetNameWithNormalCase() throws Exception {
        BaseField sut = new CsvField(null, "TestRowName", 3);

        Assert.assertEquals("TestRowName; Col#3", sut.getName());
    }

    @Test
    public void testGetStringWithNormalCase() throws Exception {
        BaseField sut = new CsvField("Test", null, -1);

        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals("Test", sut.getString());
    }

    @Test
    public void testGetStringWithNull() throws Exception {
        BaseField sut = new CsvField(null, null, -1);

        Assert.assertTrue(sut.isEmpty());
        Assert.assertEquals(StringUtils.EMPTY, sut.getString());
    }

    @Test
    public void testIsEmptyWithNonEmptyData() throws Exception {
        BaseField sut = new CsvField("Test", null, -1);

        Assert.assertFalse(sut.isEmpty());
    }

    @Test
    public void testIsEmptyWithNormalCase() throws Exception {
        BaseField sut = new CsvField(StringUtils.EMPTY, null, -1);

        Assert.assertTrue(sut.isEmpty());
    }
}
