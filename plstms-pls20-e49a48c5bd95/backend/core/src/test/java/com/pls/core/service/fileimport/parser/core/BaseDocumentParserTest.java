package com.pls.core.service.fileimport.parser.core;

import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test cases for {@link com.pls.core.service.fileimport.parser.core.BaseDocumentParser} class.
 * 
 * @author Artem Arapov
 *
 */
public class BaseDocumentParserTest {

    private final class TestParser extends BaseDocumentParser<Long, String> {
        @Override
        protected String parseHeaderColumn(String headerString) {
            return H_DATA.equals(headerString) ? H_DATA : null;
        }

        @Override
        protected void validateHeader(Collection<String> headerData) throws ImportFileParseException {
            if (!headerData.contains(H_DATA)) {
                throw new ImportFileParseException(H_DATA + "column was not found");
            }
        }

        @Override
        protected Long parseRecord() throws ImportFileInvalidDataException {
            return getColumnData(H_DATA).getLong();
        }

    }

    private static final String H_DATA = "Data";

    @Test
    public void testParseWithEmptyPage() throws Exception {
        BaseDocumentParser<Long, String> sut = new TestParser();

        InputStream input = IOUtils.toInputStream(H_DATA + ",UnexpectedColumn");

        List<Long> result = sut.parse(input, FileExtensionType.CSV);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testParseWithInvalidData() throws Exception {
        BaseDocumentParser<Long, String> sut = new TestParser();

        InputStream input = IOUtils.toInputStream(H_DATA + "\nInvalidLongData");

        sut.parse(input, FileExtensionType.CSV);

        assertEquals(1, sut.getInvalidRecords().size());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParseWithInvalidHeader() throws Exception {
        BaseDocumentParser<Long, String> sut = new TestParser();

        InputStream input = IOUtils.toInputStream("UnexpectedColumn\n1\n3");

        sut.parse(input, FileExtensionType.CSV);
    }

    @Test
    public void testParseWithNormalCase() throws Exception {
        BaseDocumentParser<Long, String> sut = new TestParser();

        InputStream input = IOUtils.toInputStream(H_DATA + ",UnexpectedColumn\n1,2\n3,4");

        List<Long> result = sut.parse(input, FileExtensionType.CSV);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(1), result.get(0));
        assertEquals(Long.valueOf(3), result.get(1));
    }
}
