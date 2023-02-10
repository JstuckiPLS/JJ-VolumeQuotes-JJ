package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.service.impl.file.LtlFuelSurchargeDocumentParser;

/**
 * Test importing {@link LtlFuelSurchargeEntity} object from excel.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeDocumentParserTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    private LtlFuelSurchargeDocumentParser parser = new LtlFuelSurchargeDocumentParser();

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

    @Test
    public void testParseWithOnlyHeaderXls() throws Exception {
        InputStream data = loadFile("ltl_fuel_surcharge.xlsx");

        List<LtlFuelSurchargeEntity> result = parser.parse(data, FileExtensionType.XLSX);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(new BigDecimal("1.2"), result.get(0).getMinRate());
        assertEquals(new BigDecimal("55.01"), result.get(0).getMaxRate());
        assertEquals(new BigDecimal("23.12"), result.get(0).getSurcharge());
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("ltl_fuel_surcharge" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }
}
