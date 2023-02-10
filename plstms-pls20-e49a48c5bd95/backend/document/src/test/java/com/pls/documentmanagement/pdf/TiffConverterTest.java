package com.pls.documentmanagement.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Test for {@link TiffConverter}.
 * 
 * @author Alexander Nalapko
 *
 */
public class TiffConverterTest {

    private static final Long LOAD_ID = 1L;
    private static final String WRONG_NAME = "lol.pdf";
    private static final String PDF_NAME = "bol.pdf";
    private static final String PNG_NAME = "bol.png";
    private static final String BMP_NAME = "bol.bmp";
    private static final String GIF_NAME = "bol.gif";
    private static final String JPG_NAME = "bol.jpg";
    private static final String TIF_NAME = "bol.tif";
    private static final String OUTPUT_DIR = "invoiceDocuments";
    private static final Long INVOICE_ID = 1L;
    private static final String FILE_PATH = "documents";
    private static final String FILE_NAME_FORMAT = "%s%s.tiff";
    private String documentsPath = null;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        URL dirUrl = ClassLoader.getSystemResource(FILE_PATH);
        File dir = new File(dirUrl.toURI());
        documentsPath = dir.getPath();
    }

    @After
    public void clean() throws IOException {
        File outputDir = new File(documentsPath + File.separator + OUTPUT_DIR);
        if (outputDir.exists()) {
            FileUtils.deleteDirectory(outputDir);
        }
    }

    @Test(expected = ApplicationException.class)
    public void shouldValidateWrongFileName() throws Exception {
        LoadDocumentEntity document = getLoadDocument(WRONG_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);
    }

    @Test
    public void shouldConvertPdfToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(PDF_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    @Test
    public void shouldConvertPngToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(PNG_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    @Test
    public void shouldConvertBmpToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(BMP_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    @Test
    public void shouldConvertJpegToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(JPG_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    @Test
    public void shouldConvertGifToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(GIF_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    @Test
    public void shouldConvertTifToTiff() throws Exception {
        LoadDocumentEntity document = getLoadDocument(TIF_NAME, "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);

        File actualFile = new File(expectedFilePath);
        assertNotNull(actualFile);
        assertTrue(actualFile.exists());
        assertEquals(expectedFileName, actualFile.getName());
    }

    private LoadDocumentEntity getLoadDocument(String fileName, String documentType) {
        LoadDocumentEntity entity = new LoadDocumentEntity();
        entity.setLoadId(LOAD_ID);
        entity.setDocumentPath(documentsPath);
        entity.setDocFileName(fileName);
        entity.setDocumentType(documentType);

        return entity;
    }

    private static String buildDocumentPath(String documentsPath, String... path) {
        Assert.assertNotNull(documentsPath);

        String filePath = documentsPath;
        for (String item : path) {
            filePath = FilenameUtils.concat(filePath, item);
        }

        return filePath;
    }

    /**
     * Test with special pdf. See <a
     * href="https://issues.apache.org/jira/browse/PDFBOX-2737">https://issues.apache
     * .org/jira/browse/PDFBOX-2737</a>
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testWithSpecialPdf() throws Exception {
        LoadDocumentEntity document = getLoadDocument("test2.pdf", "BOL");
        TiffConverter.convert(documentsPath, INVOICE_ID, document);

        String expectedFileName = String.format(FILE_NAME_FORMAT, document.getLoadId(), document.getDocumentType());
        String expectedFilePath = buildDocumentPath(documentsPath, OUTPUT_DIR, INVOICE_ID.toString(), expectedFileName);
        File file = new File(expectedFilePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
}
