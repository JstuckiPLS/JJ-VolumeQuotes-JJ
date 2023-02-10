package com.pls.core.service.fileimport.parser.core.csv;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.Document;
import com.pls.core.service.fileimport.parser.core.Page;
import com.pls.core.service.fileimport.parser.core.Record;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.csv.CsvDocument} class.
 * 
 * @author Artem Arapov
 *
 */
public class CsvDocumentTest {

    @Test
    public void testConstructorWithEmptyStream() throws Exception {
        CsvDocument sut = new CsvDocument(prepareEmpty());

        Iterator<Page> result = sut.iterator();

        Assert.assertNotNull(result);

        Assert.assertTrue(result.hasNext());
        Page page = result.next();
        Assert.assertNotNull(page);
        Assert.assertEquals(CsvPage.PAGE_NAME, page.getName());

        Assert.assertFalse(result.hasNext());
    }

    @Test(expected = ImportException.class)
    public void testConstructorWithNull() throws Exception {
        new CsvDocument(null);
    }

    @Test
    public void testIterator() throws Exception {
        CsvDocument sut = new CsvDocument(prepareNotEmpty());

        Iterator<Page> result = sut.iterator();

        Assert.assertNotNull(result);

        Assert.assertTrue(result.hasNext());
        Page page = result.next();
        Assert.assertNotNull(page);
        Assert.assertEquals(CsvPage.PAGE_NAME, page.getName());

        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testRemoveRecords() throws Exception {
        CsvDocument sut = new CsvDocument(prepareNotEmpty());

        Page page = sut.iterator().next();
        Iterator<Record> iterator = page.iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        checkDocumentPageSize(sut, 1);
    }

    private void checkDocumentPageSize(Document doc, int expectedRowsCount) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        doc.write(outStream);

        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        assertNotNull(inStream);

        CSVReader reader = null;
        List<String[]> rows = new ArrayList<String[]>();
        try {
            reader = new CSVReader(new InputStreamReader(inStream));
            rows = reader.readAll();

            Assert.assertNotNull(rows);
            Assert.assertEquals(expectedRowsCount, rows.size());
        } finally {
            IOUtils.closeQuietly(inStream);
            IOUtils.closeQuietly(reader);
        }
    }

    private InputStream prepareEmpty() {
        return IOUtils.toInputStream(StringUtils.EMPTY);
    }

    private InputStream prepareNotEmpty() {
        return IOUtils.toInputStream("testCol1, testCol2");
    }
}
