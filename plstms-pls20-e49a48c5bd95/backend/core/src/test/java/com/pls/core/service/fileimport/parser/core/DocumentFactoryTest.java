package com.pls.core.service.fileimport.parser.core;

import au.com.bytecode.opencsv.CSVWriter;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.fileimport.parser.core.csv.CsvDocument;
import com.pls.core.service.fileimport.parser.core.excel.ExcelDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test cases of using {@link com.pls.core.service.fileimport.parser.core.DocumentFactory}.
 * 
 * @author Artem Arapov
 *
 */
public class DocumentFactoryTest {

    private File testData;

    @Before
    public void setUp() {
        // We need real files to check behavior of {@link DocumentFactory} on closed stream.
        testData = new File(FileUtils.getTempDirectory(), getClass().getSimpleName() + System.nanoTime() + ".tmp");
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(testData);
    }

    @Test
    public void testPrepareWithXlsxData() throws Exception {
        prepareExcelFile();

        Document resut = DocumentFactory.create(FileUtils.openInputStream(testData), FileExtensionType.XLSX);

        Assert.assertTrue(resut instanceof ExcelDocument);
    }

    @Test
    public void testPrepareWithXlsData() throws Exception {
        prepareExcelFile();

        Document resut = DocumentFactory.create(FileUtils.openInputStream(testData), FileExtensionType.XLS);

        Assert.assertTrue(resut instanceof ExcelDocument);
    }

    @Test
    public void testPrepareWithCsvData() throws Exception {
        prepareCsvFile();

        Document result = DocumentFactory.create(FileUtils.openInputStream(testData), FileExtensionType.CSV);

        Assert.assertTrue(result instanceof CsvDocument);
    }

    @Test(expected = ImportException.class)
    public void testPrepareWithNull() throws Exception {
        DocumentFactory.create(null, FileExtensionType.XLSX);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullFileExtension() throws Exception {
        DocumentFactory.create(null, null);
    }

    private void prepareExcelFile() throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        workbook.createSheet("TetsSheet1").createRow(0);
        workbook.createSheet("TetsSheet2").createRow(0);

        OutputStream file = FileUtils.openOutputStream(testData);
        try {
            workbook.write(file);
        } finally {
            IOUtils.closeQuietly(file);
        }
    }

    private void prepareCsvFile() throws Exception {
        FileWriter writer = new FileWriter(testData);
        CSVWriter csvWriter = new CSVWriter(writer);
        String[] fields = {"One", "Two", "Three"};
        List<String[]> list = new ArrayList<String[]>();
        list.add(fields);

        OutputStream file = FileUtils.openOutputStream(testData);
        try {
            csvWriter.writeAll(list);
        } finally {
            IOUtils.closeQuietly(file);
            IOUtils.closeQuietly(csvWriter);
        }
    }
}
