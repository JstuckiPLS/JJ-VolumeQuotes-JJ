package com.pls.core.service.fileimport.parser.core.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.csv.CsvPage} class.
 * 
 * @author Artem Arapov
 *
 */
public class CsvPageTest {

    @Test
    public void testConstructorWithEmptyData() throws Exception {
        Page sut = new CsvPage(new ArrayList<String[]>());

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());

        Record header1 = sut.getHeader();
        Record header2 = sut.getHeader();
        Assert.assertNotNull(header1);
        Assert.assertEquals(header1, header2);
    }

    @Test(expected = ImportFileParseException.class)
    public void testConstructorWithNull() throws Exception {
        new CsvPage(null);
    }

    @Test
    public void testConstructorWithSingleEmptyRecord() throws Exception {
        ArrayList<String[]> content = new ArrayList<String[]>();
        content.add(new String[0]);

        Page sut = new CsvPage(content);

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());

        Record header1 = sut.getHeader();
        Record header2 = sut.getHeader();
        Assert.assertNotNull(header1);
        Assert.assertEquals(header1, header2);
    }

    @Test
    public void testGetHeader() throws Exception {
        Page sut = new CsvPage(prepareContent(1));

        Assert.assertNotNull(sut);
        Assert.assertFalse(sut.isEmpty());

        Record header1 = sut.getHeader();
        Record header2 = sut.getHeader();
        Assert.assertNotNull(header1);
        Assert.assertEquals(header1, header2);
    }

    @Test
    public void testGetName() throws Exception {
        Page sut = new CsvPage(prepareContent(1));

        Assert.assertEquals(CsvPage.PAGE_NAME, sut.getName());
    }

    @Test
    public void testIsEmptyWithData() throws Exception {
        Page sut = new CsvPage(prepareContent(1));

        Assert.assertNotNull(sut);
        Assert.assertFalse(sut.isEmpty());
    }

    @Test
    public void testIsEmptyWithoutData() throws Exception {
        Page sut = new CsvPage(prepareContent(0));

        Assert.assertNotNull(sut);
        Assert.assertTrue(sut.isEmpty());
    }

    @Test
    public void testIteratorWithData() throws Exception {
        Page sut = new CsvPage(prepareContent(2));

        Assert.assertNotNull(sut);
        Iterator<Record> iterator = sut.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertTrue(iterator.hasNext());
        Record row1 = iterator.next();
        Assert.assertNotNull(row1);
        Assert.assertEquals(2, row1.getFieldsCount());
        Assert.assertEquals("item1-0", row1.getFieldByIdx(0).getString());
        Assert.assertEquals("item2-0", row1.getFieldByIdx(1).getString());

        Assert.assertTrue(iterator.hasNext());
        Record row2 = iterator.next();
        Assert.assertEquals(2, row2.getFieldsCount());
        Assert.assertEquals("item1-1", row2.getFieldByIdx(0).getString());
        Assert.assertEquals("item2-1", row2.getFieldByIdx(1).getString());

        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorWithoutData() throws Exception {
        Page sut = new CsvPage(prepareContent(0));

        Assert.assertNotNull(sut);
        Iterator<Record> iterator = sut.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertFalse(iterator.hasNext());
    }

    private List<String[]> prepareContent(int dataItems) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        result.add(new String[] { "Header1", "Header2" });

        for (int indx = 0; indx < dataItems; indx++) {
            result.add(new String[] { "item1-" + indx, "item2-" + indx });
        }

        Assert.assertEquals(1 + dataItems, result.size());
        return result;
    }
}
