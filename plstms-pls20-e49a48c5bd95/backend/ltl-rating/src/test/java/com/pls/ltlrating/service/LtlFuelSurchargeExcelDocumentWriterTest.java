package com.pls.ltlrating.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import com.pls.core.exception.file.ExportException;
import com.pls.core.exception.fileimport.ImportException;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.service.impl.file.LtlFuelSurchargeDocumentParser;
import com.pls.ltlrating.service.impl.file.LtlFuelSurchargeExcelDocumentWriter;

/**
 * Test for {@link LtlFuelSurchargeExcelDocumentWriter}.
 *
 * @author Stas Norochevskiy
 *
 */
public class LtlFuelSurchargeExcelDocumentWriterTest {

    private LtlFuelSurchargeExcelDocumentWriter writer = new LtlFuelSurchargeExcelDocumentWriter();

    private LtlFuelSurchargeDocumentParser parser = new LtlFuelSurchargeDocumentParser();

    @Test
    public void testCreateFileBody() throws ExportException, ImportException {
        List<LtlFuelSurchargeEntity> entities = new ArrayList<LtlFuelSurchargeEntity>();

        BigDecimal minRate1 = new BigDecimal("1");
        BigDecimal maxRate1 = new BigDecimal("50");
        BigDecimal surcharge1 = new BigDecimal("12");

        BigDecimal minRate2 = new BigDecimal("22.0566");
        BigDecimal maxRate2 = new BigDecimal("55.2314");
        BigDecimal surcharge2 = new BigDecimal("12.44401");

        LtlFuelSurchargeEntity entity = new LtlFuelSurchargeEntity();
        entity.setMinRate(minRate1);
        entity.setMaxRate(maxRate1);
        entity.setSurcharge(surcharge1);
        entities.add(entity);

        entity = new LtlFuelSurchargeEntity();
        entity.setMinRate(minRate2);
        entity.setMaxRate(maxRate2);
        entity.setSurcharge(surcharge2);
        entities.add(entity);

        byte[] fileBody = writer.createFileBody(entities);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBody);

        List<LtlFuelSurchargeEntity> parsedEntities = parser.parse(inputStream, FileExtensionType.XLSX);
        Assert.assertEquals(2, parsedEntities.size());

        Assert.assertEquals(minRate1, parsedEntities.get(0).getMinRate());
        Assert.assertEquals(maxRate1, parsedEntities.get(0).getMaxRate());
        Assert.assertEquals(surcharge1, parsedEntities.get(0).getSurcharge());

        Assert.assertEquals(minRate2, parsedEntities.get(1).getMinRate());
        Assert.assertEquals(maxRate2, parsedEntities.get(1).getMaxRate());
        Assert.assertEquals(surcharge2, parsedEntities.get(1).getSurcharge());
    }
}
