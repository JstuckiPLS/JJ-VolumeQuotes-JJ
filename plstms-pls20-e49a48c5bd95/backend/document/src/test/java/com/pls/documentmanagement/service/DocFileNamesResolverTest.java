package com.pls.documentmanagement.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;

/**
 *  Test cases for {@link DocFileNamesResolver}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class DocFileNamesResolverTest {

    public static final String TESTPATH_DOCS = "testpathstart";

    public static final long LOAD_ID = 1L;

    public static final Long MANUAL_BOL_ID = ((long) Math.random() * 100);

    public static final MimeTypes FILE_TYPE = MimeTypes.PDF;

    public static final String DOCUMENT_TYPE = DocumentTypes.BOL.getDbValue();

    private DocFileNamesResolver sut;

    @Before
    public void setUp() {
        sut = new DocFileNamesResolver();
        sut.setDocumentsPath(TESTPATH_DOCS);
    }

    @Test
    public void testBuildDocumentPath() throws ParseException {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setFileType(FILE_TYPE);
        document.setLoadId(LOAD_ID);
        document.setDocumentType(DOCUMENT_TYPE);

        String expectedPath =
                "loadDocument" + DocFileNamesResolver.FILE_PATH_SEPARATOR + LOAD_ID + DocFileNamesResolver.FILE_PATH_SEPARATOR + DOCUMENT_TYPE;

        String path = sut.buildDocumentPath(document);
        assertNotNull(path);
        assertEquals(expectedPath, path);
    }

    @Test
    public void testBuildManualBolDocumentPath() {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setFileType(FILE_TYPE);
        document.setManualBol(MANUAL_BOL_ID);
        document.setDocumentType(DOCUMENT_TYPE);

        String expectedPath =
                "manualBolDocument" + DocFileNamesResolver.FILE_PATH_SEPARATOR + MANUAL_BOL_ID
                                    + DocFileNamesResolver.FILE_PATH_SEPARATOR + DOCUMENT_TYPE;

        String actualPath = sut.buildManualBolPath(document);
        assertNotNull(actualPath);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testBuildDocumentFileName() throws ParseException {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setFileType(FILE_TYPE);
        document.setLoadId(LOAD_ID);
        document.setDocumentType(DOCUMENT_TYPE);

        String fileName = sut.buildDocumentFileName(document);
        assertNotNull(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        assertEquals(FILE_TYPE.name().toLowerCase(), extension);
        String startsWith = LOAD_ID + "-" + DOCUMENT_TYPE + "-";
        assertTrue(fileName.startsWith(startsWith));
        checkVersion(fileName, extension, startsWith);
    }

    @Test
    public void testBuildManualBolDocumentFileName() throws ParseException {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setFileType(FILE_TYPE);
        document.setManualBol(MANUAL_BOL_ID);
        document.setDocumentType(DOCUMENT_TYPE);

        String fileName = sut.buildManualBolDocumentFileName(document);
        assertNotNull(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        assertEquals(FILE_TYPE.name().toLowerCase(), extension);
        String startsWith = MANUAL_BOL_ID + "-" + DOCUMENT_TYPE + "-";
        assertTrue(fileName.startsWith(startsWith));
        checkVersion(fileName, extension, startsWith);
    }

    @Test
    public void testBuildDocumentFileNamePrefixFileType() throws ParseException {
        String prefix = "testprefix";
        String fileName = sut.buildDocumentFileName(prefix, FILE_TYPE);
        assertNotNull(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        assertEquals(FILE_TYPE.name().toLowerCase(), extension);
        String startsWith = prefix + "-";
        assertTrue(fileName.startsWith(startsWith));
        checkVersion(fileName, extension, startsWith);
    }

    @Test
    public void testBuildTempDocumentPath() throws ParseException {
        String path = sut.buildTempDocumentPath(DOCUMENT_TYPE);
        assertNotNull(path);

        String expectedPathStart = "tempDocument" + DocFileNamesResolver.FILE_PATH_SEPARATOR + DOCUMENT_TYPE;

        assertNotNull(path);
        assertTrue(path.startsWith(expectedPathStart));

        String version = path.substring(expectedPathStart.length() + 1);
        Date versionDate = new SimpleDateFormat(DocFileNamesResolver.DATE_FORMAT).parse(version);
        assertNotNull(versionDate);
    }

    @Test
    public void testBuildDirectPath() throws ParseException {
        String expectedPath = TESTPATH_DOCS + DocFileNamesResolver.FILE_PATH_SEPARATOR + "testPath";
        String path = sut.buildDirectPath("testPath");
        assertNotNull(path);
        assertEquals(expectedPath, path);
    }

    @Test
    public void testBuildDocumentTempFileName() throws ParseException {
        String fileName = sut.buildTempFileName(DOCUMENT_TYPE, FILE_TYPE);
        assertNotNull(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        assertEquals(FILE_TYPE.name().toLowerCase(), extension);
        String startsWith = DOCUMENT_TYPE + "-";
        assertTrue(fileName.startsWith(startsWith));
        checkVersion(fileName, extension, startsWith);
    }

    private void checkVersion(String fileName, String extension, String startsWith) throws ParseException {
        String version = fileName.substring(startsWith.length(), fileName.indexOf(extension) - 1);
        Date versionDate = new SimpleDateFormat(DocFileNamesResolver.DATE_FORMAT).parse(version);
        assertNotNull(versionDate);
    }
}
