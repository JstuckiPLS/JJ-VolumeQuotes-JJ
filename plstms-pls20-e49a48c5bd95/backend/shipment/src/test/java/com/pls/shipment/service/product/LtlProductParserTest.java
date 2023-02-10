package com.pls.shipment.service.product;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.exception.fileimport.ImportFileParseException;
import com.pls.shipment.service.product.impl.LtlProductParser;
import org.junit.Assert;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.shipment.domain.LtlProductEntity;

/**
 * Test cases for {@link com.pls.shipment.service.product.impl.LtlProductParser} class.
 * 
 * @author Maxim Medvedev
 */
public class LtlProductParserTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    private final LtlProductParser sut = new LtlProductParser();

    @After
    public void tearDown() {
        for (InputStream stream : openedFiles) {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("Unable to close stream", e);
            }
        }
    }

    @Test
    public void testParse_1SheetEmpty() throws Exception {
        InputStream data = loadFile("1SheetEmpty.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testParse_1SheetHazmatFlags() throws Exception {
        InputStream data = loadFile("1SheetHazmatFlags.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.size());

        Assert.assertTrue(result.get(0).isHazmat());
        Assert.assertFalse(result.get(1).isHazmat());
        Assert.assertTrue(result.get(2).isHazmat());
        Assert.assertFalse(result.get(3).isHazmat());
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParse_1SheetInvalidHeader() throws Exception {
        InputStream data = loadFile("1SheetInvalidHeader.xlsx");

        sut.parse(data);
    }

    @Test
    public void testParse_1SheetShuffleColumns() throws Exception {
        InputStream data = loadFile("1SheetShuffleColumns.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());

        checkBaseFields(result.get(0), "TestProd1", "nmfc1", "sub1", CommodityClass.CLASS_50, "sku1");
        checkHazmatProduct(result.get(0), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");

        checkBaseFields(result.get(1), "TestProd2", "nmfc2", "sub2", CommodityClass.CLASS_55, "sku2");
        checkProductisNotHazmat(result.get(1));

        checkBaseFields(result.get(2), "TestProd3", null, null, CommodityClass.CLASS_500, null);
        checkProductisNotHazmat(result.get(2));
    }

    @Test
    public void testParse_1SheetUnexpectedColumns() throws Exception {
        InputStream data = loadFile("1SheetUnexpectedColumns.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        checkBaseFields(result.get(0), "TestProd1", "TestNmfc1", "TestSub1", CommodityClass.CLASS_50, "TestSku1");
        checkHazmatProduct(result.get(0), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");
    }

    @Test
    public void testParse_1SheetValidData() throws Exception {
        InputStream data = loadFile("1SheetValidData.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());

        checkBaseFields(result.get(0), "TestProd1", "nmfc1", "sub1", CommodityClass.CLASS_50, "sku1");
        checkHazmatProduct(result.get(0), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");

        checkBaseFields(result.get(1), "TestProd2", "nmfc2", "sub2", CommodityClass.CLASS_55, "sku2");
        checkProductisNotHazmat(result.get(1));

        checkBaseFields(result.get(2), "TestProd3", null, null, CommodityClass.CLASS_500, null);
        checkProductisNotHazmat(result.get(2));
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParse_1SheetWithoutClass() throws Exception {
        InputStream data = loadFile("1SheetWithoutClass.xlsx");

        sut.parse(data);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParse_1SheetWithoutDescription() throws Exception {
        InputStream data = loadFile("1SheetWithoutDescription.xlsx");

        sut.parse(data);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParse_1SheetWithoutHazmat() throws Exception {
        InputStream data = loadFile("1SheetWithoutHazmat.xlsx");

        sut.parse(data);
    }

    @Test(expected = ImportFileInvalidDataException.class)
    public void testParse_1SheetWithoutMandatoryColumns() throws Exception {
        InputStream data = loadFile("1SheetWithoutMandatoryColumns.xlsx");

        sut.parse(data);
    }

    @Test
    public void testParse_1SheetWithoutOptionalColumns() throws Exception {
        InputStream data = loadFile("1SheetWithoutOptionalColumns.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        checkBaseFields(result.get(0), "TestProd1", null, null, CommodityClass.CLASS_50, null);
        checkHazmatProduct(result.get(0), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");
    }

    @Test
    public void testParse_2SheetValidData() throws Exception {
        InputStream data = loadFile("2SheetValidData.xlsx");

        List<LtlProductEntity> result = sut.parse(data);

        Assert.assertNotNull(result);
        Assert.assertEquals(8, result.size());

        checkBaseFields(result.get(0), "TestProd1", "TestNmfc1", "TestSub1", CommodityClass.CLASS_50, "TestSku1");
        checkHazmatProduct(result.get(0), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");

        checkBaseFields(result.get(1), "TestProd2", "TestNmfc2", "TestSub2", CommodityClass.CLASS_55, "TestSku2");
        checkProductisNotHazmat(result.get(1));

        checkBaseFields(result.get(2), "TestProd3", "TestNmfc3", "TestSub3", CommodityClass.CLASS_500, "TestSku3");
        checkProductisNotHazmat(result.get(2));

        checkBaseFields(result.get(3), "TestProd4", null, null, CommodityClass.CLASS_100, null);
        checkProductisNotHazmat(result.get(3));

        checkBaseFields(result.get(4), "TestProd12", "TestNmfc1", "TestSub1", CommodityClass.CLASS_50, "TestSku1");
        checkHazmatProduct(result.get(4), "UN1", "PG1", "HazmatCompany1", "HazmatPhone1", "HazmatContract1");

        checkBaseFields(result.get(5), "TestProd22", "TestNmfc2", "TestSub2", CommodityClass.CLASS_55, "TestSku2");
        checkProductisNotHazmat(result.get(5));

        checkBaseFields(result.get(6), "TestProd32", "TestNmfc3", "TestSub3", CommodityClass.CLASS_500, "TestSku3");
        checkProductisNotHazmat(result.get(6));

        checkBaseFields(result.get(7), "TestProd42", null, null, CommodityClass.CLASS_100, null);
        checkProductisNotHazmat(result.get(7));
    }

    @Test(expected = ImportFileParseException.class)
    public void testParse_ClosedStream() throws Exception {
        InputStream data = loadFile("1SheetEmpty.xlsx");
        data.close();

        sut.parse(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_NullInput() throws Exception {
        sut.parse(null);
    }

    private void checkBaseFields(LtlProductEntity product, String description, String nmfc, String nmfcSub,
            CommodityClass commodityClass, String sku) {
        Assert.assertNotNull(product);
        Assert.assertEquals(description, product.getDescription());
        Assert.assertEquals(nmfc, product.getNmfcNum());
        Assert.assertEquals(nmfcSub, product.getNmfcSubNum());
        Assert.assertEquals(commodityClass, product.getCommodityClass());
        Assert.assertEquals(sku, product.getProductCode());
    }

    private void checkHazmatProduct(LtlProductEntity product, String hazmatUn, String hazmatPackagingGroup,
            String hazmatCompany, String hazmatPhone, String hazmatContract) {
        Assert.assertTrue(product.isHazmat());
        Assert.assertNotNull(product.getHazmatInfo());
        Assert.assertEquals(hazmatUn, product.getHazmatInfo().getUnNumber());
        Assert.assertEquals(hazmatPackagingGroup, product.getHazmatInfo().getPackingGroup());
        Assert.assertEquals(hazmatCompany, product.getHazmatInfo().getEmergencyCompany());
        Assert.assertEquals(hazmatPhone, product.getHazmatInfo().getEmergencyPhone().getNumber());
        Assert.assertEquals(hazmatContract, product.getHazmatInfo().getEmergencyContract());
    }

    private void checkProductisNotHazmat(LtlProductEntity product) {
        Assert.assertFalse(product.isHazmat());
        Assert.assertNull(product.getHazmatInfo());
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("productImport" + File.separator + string);
        Assert.assertNotNull(result);
        openedFiles.add(result);
        return result;
    }

}
