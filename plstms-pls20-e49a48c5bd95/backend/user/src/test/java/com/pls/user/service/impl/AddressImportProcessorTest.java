package com.pls.user.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.CountryDao;
import com.pls.core.dao.CustomerUserDao;
import com.pls.core.dao.UserAddressBookDao;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.validation.support.Validator;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.user.address.service.impl.AddressImportProcessor;

/**
 * Test cases for {@link AddressImportProcessorTest}.
 * 
 * @author Brichak Aleksandr
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressImportProcessorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    @InjectMocks
    private AddressImportProcessor sut;

    @Mock
    private DocumentService documentService;

    @Mock
    private UserAddressBookDao userAddressBookDao;

    @Mock
    private Validator<UserAddressBookEntity> validator;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @Mock
    private CustomerUserDao customerUserDao;
    @Mock
    private CountryDao countryDao;

    @After
    public void tearDown() {
        for (InputStream stream : openedFiles) {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("Unable to close file");
            }
        }

    }

    @Test(expected = ImportException.class)
    public void testParseWithNullInput() throws Exception {
        sut.processImport(null, FileExtensionType.XLSX);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithEmptyXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetEmptyXls.xls");

        sut.processImport(data, FileExtensionType.XLS);
    }

    // @Test(expected = ImportFileInvalidDataException.class)
    @Ignore
    public void testParseWithEmptyCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetEmptyCSV.csv");
        sut.processImport(data, FileExtensionType.CSV);
    }

    @Test(expected = ImportException.class)
    public void testParseWithClosedStream() throws Exception {
        InputStream data = loadResourceAsStream("1SheetEmpty.xlsx");
        data.close();

        sut.processImport(data, FileExtensionType.XLSX);
    }

    @Test
    public void testParseWithOnlyHeaderXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetOnlyHeaderXls.xlsx");

        ImportFileResults result = sut.processImport(data, FileExtensionType.XLSX);

        assertNotNull(result);
    }

    @Test
    public void testParseWithOnlyHeaderCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetOnlyHeaderCsv.csv");

        ImportFileResults result = sut.processImport(data, FileExtensionType.CSV);

        assertNotNull(result);
    }

    @Test
    public void testImport_1SheetInvalid() throws Exception {
        InputStream data = loadResourceAsStream("1SheetInvalidHeader.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(3);
        expectedResult.setSuccesRowsCount(0);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.XLSX)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        Mockito.verify(documentService).prepareTempDocument((String) Matchers.isNull(), Matchers.eq(MimeTypes.XLSX));
        Mockito.verify(docFileNamesResolver).buildDirectPath(Matchers.eq(path));
        Mockito.verify(documentService).savePreparedDocument(Matchers.eq(document));

        checkResult(expectedResult, actualResult);

    }

    @Test
    public void testImport_1SheetInvalidCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetInvalidCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(3);
        expectedResult.setSuccesRowsCount(0);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.CSV)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        Mockito.verify(documentService).prepareTempDocument((String) Matchers.isNull(), Matchers.eq(MimeTypes.CSV));
        Mockito.verify(docFileNamesResolver).buildDirectPath(Matchers.eq(path));
        Mockito.verify(documentService).savePreparedDocument(Matchers.eq(document));

        checkResult(expectedResult, actualResult);

    }

    @Test
    public void testParseValidDataXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetValidData.xls");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(3);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.XLS)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLS);

        checkResult(expectedResult, actualResult);

    }

    @Test
    public void testParseValidDataCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetValidData.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(3);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.CSV)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);

    }

    @Test(expected = ImportRecordsNumberExceededException.class)
    public void testParseWithMaxRowsCountXlsx() throws Exception {
        InputStream data = loadResourceAsStream("1SheetLargeData.xlsx");

        sut.processImport(data, FileExtensionType.XLSX);
    }

    @Test(expected = ImportRecordsNumberExceededException.class)
    public void testParseWithMaxRowsCountCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetLargeDataCsv.csv");

        sut.processImport(data, FileExtensionType.CSV);
    }

    @Test
    public void testValidInvalidDataXlsx() throws Exception {
        InputStream data = loadResourceAsStream("1SheetValidInvalidData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(2);
        expectedResult.setSuccesRowsCount(2);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.XLSX)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testValidInvalidDataCsv() throws Exception {
        InputStream data = loadResourceAsStream("1SheetValidInvalidDataCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(2);
        expectedResult.setSuccesRowsCount(2);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.CSV)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testValidDataCsvSaveOfExcel() throws Exception {
        InputStream data = loadResourceAsStream("sheetValidDataCSVSaveOfExcel.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(3);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.CSV)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testInvalidDataCsvSaveOfExcel() throws Exception {
        InputStream data = loadResourceAsStream("sheetInvalidDataCsvSaveOfExcel.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(3);
        expectedResult.setSuccesRowsCount(0);

        File tempFile = File.createTempFile("importTemp", "tmp");

        LoadDocumentEntity document = new LoadDocumentEntity();
        String path = tempFile.getParent();
        document.setDocumentPath(path);
        document.setDocFileName(tempFile.getName());
        long expectedId = 777L;
        document.setId(expectedId);

        Mockito.when(documentService.prepareTempDocument(null, MimeTypes.CSV)).thenReturn(document);
        Mockito.when(docFileNamesResolver.buildDirectPath(path)).thenReturn(path);
        Mockito.when(documentService.savePreparedDocument(document)).thenReturn(document);
        sut.setCustomerId(2L);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }
    private void checkResult(ImportFileResults expectedResult, ImportFileResults actualResult) {
        assertNotNull(actualResult);
        assertEquals(expectedResult.getSuccesRowsCount(), actualResult.getSuccesRowsCount());
        assertEquals(expectedResult.getFaiedRowsCount(), actualResult.getFaiedRowsCount());
    }

    private InputStream loadResourceAsStream(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("addressImport" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }

}
