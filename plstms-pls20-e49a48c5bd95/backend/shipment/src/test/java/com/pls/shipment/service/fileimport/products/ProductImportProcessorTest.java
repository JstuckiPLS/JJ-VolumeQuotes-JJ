package com.pls.shipment.service.fileimport.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
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
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportRecordsNumberExceededException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.validation.support.Validator;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.service.product.impl.fileimport.ProductImportProcessor;

/**
 * Test cases of {@link com.pls.shipment.service.product.impl.fileimport.ProductImportProcessor}.
 * 
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductImportProcessorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    @InjectMocks
    private ProductImportProcessor sut;

    @Mock
    private DocumentService documentService;

    @Mock
    private LtlProductDao productDao;

    @Mock
    private Validator<LtlProductEntity> validator;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;


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
    public void testParseWithClosedStream() throws Exception {
        InputStream data = loadResourceAsStream("1SheetEmpty.xlsx");
        data.close();

        sut.processImport(data, FileExtensionType.XLSX);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithEmptyCsv() throws Exception {
        InputStream data = loadResourceAsStream("EmptyCsv.csv");

        sut.processImport(data, FileExtensionType.CSV);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithEmptyXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetEmpty.xlsx");

        sut.processImport(data, FileExtensionType.XLSX);
    }

    @Test(expected = ImportException.class)
    public void testParseWithNullInput() throws Exception {
        sut.processImport(null, FileExtensionType.CSV);
    }

    @Test
    public void testParseWithOnlyHeaderCsv() throws Exception {
        InputStream data = loadResourceAsStream("CsvOnlyHeader.csv");

        ImportFileResults result = sut.processImport(data, FileExtensionType.CSV);

        assertNotNull(result);
    }

    @Test
    public void testParseWithOnlyHeaderXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetOnlyHeader.xlsx");

        ImportFileResults result = sut.processImport(data, FileExtensionType.XLSX);

        assertNotNull(result);
    }

    @Test
    public void testParseWithTwoRecordsCsv() throws Exception {
        Mockito.when(productDao.isProductUnique(anyLong(), anyLong(), anyLong(), anyString(), (CommodityClass) anyObject())).thenReturn(true);
        InputStream data = loadResourceAsStream("ValidDataCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testParseWithTwoRecordsXlsx() throws Exception {
        Mockito.when(productDao.isProductUnique(anyLong(), anyLong(), anyLong(), anyString(), (CommodityClass) anyObject())).thenReturn(true);
        InputStream data = loadResourceAsStream("1SheetValidData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testParseWithTwoRecordsXls() throws Exception {
        Mockito.when(productDao.isProductUnique(anyLong(), anyLong(), anyLong(), anyString(), (CommodityClass) anyObject())).thenReturn(true);
        InputStream data = loadResourceAsStream("1SheetValidData.xls");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLS);

        checkResult(expectedResult, actualResult);
    }

    @Test(expected = ImportException.class)
    public void testParseWithMaxRowsCountCsv() throws Exception {
        InputStream data = loadResourceAsStream("MaxRowsCountCsv.csv");

        sut.processImport(data, FileExtensionType.CSV);
    }

    @Test(expected = ImportRecordsNumberExceededException.class)
    public void testParseWithMaxRowsCountXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetMaxRowsCount.xlsx");

        sut.processImport(data, FileExtensionType.XLSX);
    }

    @Test
    public void testParseWithMixedColumnsCsv() throws Exception {
        Mockito.when(productDao.isProductUnique(anyLong(), anyLong(), anyLong(), anyString(), (CommodityClass) anyObject())).thenReturn(true);
        InputStream data = loadResourceAsStream("MixedColumnsDataCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testParseWithMixedColumnsXls() throws Exception {
        Mockito.when(productDao.isProductUnique(anyLong(), anyLong(), anyLong(), anyString(), (CommodityClass) anyObject())).thenReturn(true);
        InputStream data = loadResourceAsStream("1SheetMixedColumnsData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testParseWithInvalidDataXls() throws Exception {
        InputStream data = loadResourceAsStream("1SheetInValidData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(2);
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
    public void testParseWithInvalidDataCsv() throws Exception {
        InputStream data = loadResourceAsStream("InValidDataCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(2);
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
        tempFile.deleteOnExit();
    }

    private void checkResult(ImportFileResults expectedResult, ImportFileResults actualResult) {
        assertNotNull(actualResult);
        assertEquals(expectedResult.getSuccesRowsCount(), actualResult.getSuccesRowsCount());
        assertEquals(expectedResult.getFaiedRowsCount(), actualResult.getFaiedRowsCount());
    }

    private InputStream loadResourceAsStream(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("productImportM1" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }
}
