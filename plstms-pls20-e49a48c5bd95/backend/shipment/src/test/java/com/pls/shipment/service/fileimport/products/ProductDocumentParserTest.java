package com.pls.shipment.service.fileimport.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.pls.shipment.service.product.impl.fileimport.ProductsDocumentParser;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.shipment.domain.LtlProductEntity;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;

/**
 * Test cases of using {@link com.pls.shipment.service.product.impl.fileimport.ProductsDocumentParser}.
 * 
 * @author Artem Arapov
 *
 */
public class ProductDocumentParserTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    private ProductsDocumentParser sut = new ProductsDocumentParser();

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
        InputStream data = loadFile("1SheetEmpty.xlsx");
        data.close();

        sut.parse(data, FileExtensionType.XLSX);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithEmptyCsv() throws Exception {
        InputStream data = loadFile("EmptyCsv.csv");

        List<LtlProductEntity> result = sut.parse(data, FileExtensionType.CSV);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithEmptyXls() throws Exception {
        InputStream data = loadFile("1SheetEmpty.xlsx");

        List<LtlProductEntity> result = sut.parse(data, FileExtensionType.XLSX);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test(expected = ImportException.class)
    public void testParseWithNullInput() throws Exception {
        sut.parse(null, FileExtensionType.XLS);
    }

    @Test
    public void testParseWithOnlyHeaderCsv() throws Exception {
        InputStream data = loadFile("CsvOnlyHeader.csv");

        List<LtlProductEntity> result = sut.parse(data, FileExtensionType.CSV);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testParseWithOnlyHeaderXls() throws Exception {
        InputStream data = loadFile("1SheetOnlyHeader.xlsx");

        List<LtlProductEntity> result = sut.parse(data, FileExtensionType.XLSX);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("productImportM1" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }
}
