package com.pls.shipment.service.fileimport.products;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.bo.ImportFileResults;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.fileimport.parser.core.DocumentFactory.FileExtensionType;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.service.product.impl.fileimport.ProductImportProcessor;

/**
 * Tests for {@link com.pls.shipment.service.product.impl.fileimport.ProductImportProcessor}.
 * 
 * @author Artem Arapov
 */
public class ProductImportProcessorIT extends BaseServiceITClass {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Long CUSTOMER_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String USER_NAME = "userName" + Math.random();

    private final List<InputStream> openedFiles = new ArrayList<InputStream>();

    @Autowired
    private ProductImportProcessor sut;

    @Autowired
    private LtlProductDao dao;

    @Before
    public void init() {
        SecurityTestUtils.login(USER_NAME);
    }

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
    public void testParseWithTwoRecordsCsv() throws Exception {
        InputStream data = loadFile("ValidDataCsv.csv");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        sut.setCustomerId(CUSTOMER_ID);
        sut.setUserId(USER_ID);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.CSV);

        checkResult(expectedResult, actualResult);
    }

    @Test
    public void testParseWithTwoRecordsXlsx() throws Exception {
        InputStream data = loadFile("1SheetValidData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        sut.setCustomerId(CUSTOMER_ID);
        sut.setUserId(USER_ID);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);


    }

    @Test
    public void testParseWithTwoRecordsXls() throws Exception {
        InputStream data = loadFile("1SheetValidData.xls");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        sut.setCustomerId(CUSTOMER_ID);
        sut.setUserId(USER_ID);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);

        List<LtlProductEntity> products = dao.findProducts(CUSTOMER_ID, USER_ID, "TestProd1", CommodityClass.CLASS_50, true, 2);

        assertTrue(products.size() == 1);
        LtlProductEntity entity = products.get(0);
        assertEquals(entity.getDescription(), "TestProd1");
        assertEquals(entity.getHazmatInfo().getInstructions(), "instruction1");
        assertEquals(entity.getHazmatInfo().getEmergencyContract(), "contract1");
        assertEquals(entity.getHazmatInfo().getEmergencyCompany(), "company1");
        PhoneEmbeddableObject phone = new PhoneEmbeddableObject();
        phone.setAreaCode("123");
        phone.setCountryCode("001");
        phone.setNumber("4567890");
        assertEquals(entity.getHazmatInfo().getEmergencyPhone(), phone);
    }

    @Test
    public void testParseWithInvalidDataXls() throws Exception {
        InputStream data = loadFile("1SheetInValidData.xlsx");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(2);
        expectedResult.setSuccesRowsCount(0);

        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
        assertNotNull(actualResult.getFailedDocumentId());
        assertTrue(actualResult.getFailedDocumentId() > 0);
    }

    @Test
    public void testParseWithPersonId() throws Exception {
        InputStream data = loadFile("1SheetValidData.xls");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        sut.setCustomerId(CUSTOMER_ID);
        sut.setUserId(USER_ID);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
        getSession().flush();
        checkProductPersonId("sku1", USER_ID);
        checkProductPersonId("sku2", USER_ID);
    }

    @Test
    public void testParseWithoutPersonId() throws Exception {
        InputStream data = loadFile("1SheetValidData.xls");

        ImportFileResults expectedResult = new ImportFileResults();
        expectedResult.setFaiedRowsCount(0);
        expectedResult.setSuccesRowsCount(2);

        sut.setCustomerId(CUSTOMER_ID);
        sut.setUserId(null);
        ImportFileResults actualResult = sut.processImport(data, FileExtensionType.XLSX);

        checkResult(expectedResult, actualResult);
        getSession().flush();
        checkProductPersonId("sku1", null);
        checkProductPersonId("sku2", null);
    }

    private void checkResult(ImportFileResults expectedResult, ImportFileResults actualResult) {
        assertNotNull(actualResult);
        assertEquals(expectedResult.getSuccesRowsCount(), actualResult.getSuccesRowsCount());
        assertEquals(expectedResult.getFaiedRowsCount(), actualResult.getFaiedRowsCount());
    }

    private InputStream loadFile(String string) {
        InputStream result = ClassLoader.getSystemResourceAsStream("productImportM1" + File.separator + string);
        assertNotNull(result);
        openedFiles.add(result);
        return result;
    }

    private void checkProductPersonId(final String sku, Long expectedPersonId) {
        BigInteger personId = (BigInteger) getSession().createSQLQuery("select PERSON_ID from LTL_PRODUCT where PRODUCT_CODE = ?")
                .setParameter(0, sku).uniqueResult();
        if (expectedPersonId != null) {
            Assert.assertNotNull(personId);
            Assert.assertEquals(expectedPersonId, Long.valueOf(personId.longValue()));
        } else {
            Assert.assertNull(personId);
        }
    }
}
