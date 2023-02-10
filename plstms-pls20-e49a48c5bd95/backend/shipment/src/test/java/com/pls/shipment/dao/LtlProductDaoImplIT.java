package com.pls.shipment.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlProductEntity;

/**
 * Test cases for {@link com.pls.shipment.dao.impl.LtlProductDaoImpl} class.
 *
 * @author Maxim Medvedev
 */
public class LtlProductDaoImplIT extends AbstractDaoTest {

    /**
     * See test dataset.
     */
    private static final Long VALID_CUSTOMER_ID = 1L;

    /**
     * See test dataset.
     */
    private static final Long VALID_PRODUCT_ID = 1L;

    private static final Long USER_ID = 2L;

    private static final int PRODUCTS_COUNT = 5;

    private static final long INCORRECT_ORG_ID = -1L;

    private static final long CREATED_BY_USER_ID = 1L;

    private static final String DESCRIPTION_FILTER_WORD = "OFFICE";

    @Autowired
    private LtlProductDao sut;

    @Test
    public void testCheckProductExistsWithInvalidCustomerId() {
        Assert.assertFalse(sut.checkProductExists(2L, VALID_PRODUCT_ID));
    }

    @Test
    public void testCheckProductExistsWithInvalidProductId() {
        Assert.assertFalse(sut.checkProductExists(VALID_CUSTOMER_ID, 2L));
    }

    @Test
    public void testCheckProductExistsWithNullCustomer() {
        Assert.assertFalse(sut.checkProductExists(null, VALID_PRODUCT_ID));
    }

    @Test
    public void testCheckProductExistsWithNullProduct() {
        Assert.assertFalse(sut.checkProductExists(VALID_CUSTOMER_ID, null));
    }

    @Test
    public void testCheckProductExistsWithValidIds() {
        Assert.assertTrue(sut.checkProductExists(VALID_CUSTOMER_ID, VALID_PRODUCT_ID));
    }

    @Test
    public void testFindByCustomerIdWithInvalidId() {
        List<LtlProductEntity> result = sut.getProductList(-123L, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByCustomerIdWithNull() {
        List<LtlProductEntity> result = sut.getProductList(null, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByCustomerIdWithValidId() {
        List<LtlProductEntity> result = sut.getProductList(1L, null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testMarkAsInactiveWithInvalidId() {
        LtlProductEntity beforeUpdate = (LtlProductEntity) getSession().get(LtlProductEntity.class, VALID_PRODUCT_ID);
        Assert.assertNotNull(beforeUpdate);
        Assert.assertEquals(Status.ACTIVE, beforeUpdate.getStatus());
        Assert.assertEquals(Long.valueOf(1L), beforeUpdate.getModification().getModifiedBy());

        sut.markAsInactive(VALID_PRODUCT_ID, 2L, new Date());

        flushAndClearSession();

        LtlProductEntity afterUpdate = (LtlProductEntity) getSession().get(LtlProductEntity.class, VALID_PRODUCT_ID);
        Assert.assertNotNull(afterUpdate);
        Assert.assertEquals(Status.INACTIVE, afterUpdate.getStatus());
        Assert.assertEquals(Long.valueOf(2L), afterUpdate.getModification().getModifiedBy());
    }

    @Test
    public void testMarkAsInactiveWithNull() {
        // Method should update nothing and should not throw exception
        sut.markAsInactive(null, 2L, new Date());
    }

    @Test
    public void testMarkAsInactiveWithValidId() {
        // Method should update nothing and should not throw exception
        sut.markAsInactive(-123L, 2L, new Date());
    }

    @Test
    public void testSaveWithExistedEntity() {
        LtlProductEntity beforeUpdate = (LtlProductEntity) getSession().get(LtlProductEntity.class, VALID_PRODUCT_ID);
        Assert.assertNotNull(beforeUpdate);
        String descriptionBeforeUpdate = beforeUpdate.getDescription();

        String newDescription = descriptionBeforeUpdate + "Test";
        beforeUpdate.setDescription(newDescription);

        sut.save(beforeUpdate);

        flushAndClearSession();
        LtlProductEntity afterUpdate = (LtlProductEntity) getSession().get(LtlProductEntity.class, VALID_PRODUCT_ID);
        Assert.assertNotNull(afterUpdate);
        Assert.assertEquals(newDescription, afterUpdate.getDescription());
    }

    @Test
    public void testSaveWithNewEntity() {
        LtlProductEntity newEntity = prepareMinimalEntity();

        Assert.assertNull(newEntity.getId());

        newEntity = sut.save(newEntity);

        Assert.assertNotNull(newEntity);
        Assert.assertNotNull(newEntity.getId());

        flushAndClearSession();
        LtlProductEntity afterSave = (LtlProductEntity) getSession().get(LtlProductEntity.class, newEntity.getId());
        Assert.assertNotNull(afterSave);
        Assert.assertEquals(newEntity, afterSave);
    }

    @Test
    public void testGetProductList() {
        List<LtlProductEntity> products = sut.getProductList(VALID_CUSTOMER_ID, null);
        Assert.assertNotNull(products);
        Assert.assertFalse(products.isEmpty());
        for (LtlProductEntity product : products) {
            Assert.assertNotNull(product);
            Assert.assertEquals(Status.ACTIVE, product.getStatus());
            Assert.assertEquals(VALID_CUSTOMER_ID, product.getCustomerId());
        }
    }

    @Test
    public void testGetProductListForUser() {
        List<LtlProductEntity> products = sut.getProductList(VALID_CUSTOMER_ID, USER_ID);
        Assert.assertNotNull(products);
        Assert.assertFalse(products.isEmpty());

        for (LtlProductEntity product : products) {
            Assert.assertNotNull(product);
            Assert.assertEquals(Status.ACTIVE, product.getStatus());
            Assert.assertEquals(VALID_CUSTOMER_ID, product.getCustomerId());
            Assert.assertTrue(product.getPersonId() == null || product.getPersonId() == USER_ID);
        }
    }

    @Test
    public void testFindRecentProducts() {
        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, PRODUCTS_COUNT, null, false);
        Assert.assertFalse(products.isEmpty());
        Assert.assertTrue(products.size() == PRODUCTS_COUNT);
    }

    @Test
    public void testFindRecentProductsIncorrectOrgId() {
        List<LtlProductEntity> products = sut.findRecentProducts(INCORRECT_ORG_ID, USER_ID, PRODUCTS_COUNT, null, false);
        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void testFindProductsWithFilterProductCode() {
        String filterWord = "SKU";
        List<LtlProductEntity> products = sut.findProducts(VALID_CUSTOMER_ID, null, filterWord, null, false, PRODUCTS_COUNT);
        checkByFilterWord(products, filterWord);
    }

    @Test
    public void testFindProductsWithFilterDescription() {
        String filterWord = "TV";
        List<LtlProductEntity> products = sut.findProducts(VALID_CUSTOMER_ID, null, filterWord, null, false, PRODUCTS_COUNT);
        checkByFilterWord(products, filterWord);
    }

    @Test
    public void testFindRecentProductsWithCommodityClass() {
        CommodityClass commodityClass = CommodityClass.CLASS_100;
        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, 15, commodityClass, false);
        checkByCommodityClass(commodityClass, products);
    }

    @Test
    public void testFindProductsWithFilterAndCommodityClass() {
        CommodityClass commodityClass = CommodityClass.CLASS_50;
        List<LtlProductEntity> products = sut.findProducts(VALID_CUSTOMER_ID, null, DESCRIPTION_FILTER_WORD, commodityClass, true, PRODUCTS_COUNT);
        checkByCommodityClass(commodityClass, products);
    }

    @Test
    public void testFindRecentProductsWithHazmat() {
        boolean hazmat = true;
        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, 15, null, hazmat);
        Assert.assertFalse(products.isEmpty());
        for (LtlProductEntity product : products) {
            Assert.assertTrue(product.isHazmat() == hazmat);
        }
    }

    @Test
    public void testFindRecentProductsWithUserId() {
      List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, CREATED_BY_USER_ID, 15, null, false);
      Assert.assertNotNull(products);
      Assert.assertFalse(products.isEmpty());
      for (LtlProductEntity product : products) {
          Assert.assertTrue(product.getPersonId() == null || product.getPersonId() == CREATED_BY_USER_ID);
      }
    }

    @Test
    public void testFindProductsWithAllParameters() {
        List<Long> ids = getUserWithSubordinatorsIds();
        CommodityClass commodityClass = CommodityClass.CLASS_50;

        boolean hazmat = true;
        List<LtlProductEntity> products = sut.findProducts(VALID_CUSTOMER_ID, CREATED_BY_USER_ID, DESCRIPTION_FILTER_WORD,
                commodityClass, hazmat, PRODUCTS_COUNT);
        for (LtlProductEntity product : products) {
            Assert.assertEquals(VALID_CUSTOMER_ID, product.getCustomerId());
            Assert.assertTrue(ids.contains(product.getModification().getCreatedBy()));
            checkProductForFilterWord(DESCRIPTION_FILTER_WORD, product);
            Assert.assertTrue(product.getCommodityClass() == commodityClass);
            Assert.assertTrue(product.isHazmat() == hazmat);
            Assert.assertTrue(product.getPersonId() == null || product.getPersonId() == CREATED_BY_USER_ID);
        }
    }

    @Test
    public void shouldFindRecentProducts() {
        updateLoadMaterialWithReferencedProduct(22L, 16L);
        updateLoadMaterialWithReferencedProduct(15L, 20L);

        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, PRODUCTS_COUNT, null, false);
        Assert.assertEquals(PRODUCTS_COUNT, products.size());
        for (LtlProductEntity product: products) {
            Assert.assertEquals(VALID_CUSTOMER_ID, product.getCustomerId());
            Assert.assertFalse(product.isHazmat());
        }

        Assert.assertEquals(new Long(16), products.get(0).getId());
        Assert.assertEquals(new Long(20), products.get(1).getId());
    }

    @Test
    public void shouldFindRecentHazmatProducts() {
        updateLoadMaterialWithReferencedProduct(22L, 14L);
        updateLoadMaterialWithReferencedProduct(15L, 51L);

        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, 6, null, true);
        Assert.assertEquals(6, products.size());
        for (int i = 0; i < products.size(); i++) {
            Assert.assertEquals(VALID_CUSTOMER_ID, products.get(i).getCustomerId());
            Assert.assertTrue(products.get(i).isHazmat());
        }

        Assert.assertEquals(new Long(14), products.get(0).getId());
        Assert.assertEquals(new Long(51), products.get(1).getId());
    }

    @Test
    public void shouldFindRecentProductsBbyCommodityClass() {
        updateLoadMaterialWithReferencedProduct(22L, 30L);
        updateLoadMaterialWithReferencedProduct(15L, 21L);

        List<LtlProductEntity> products = sut.findRecentProducts(VALID_CUSTOMER_ID, USER_ID, 4, CommodityClass.CLASS_100, false);
        Assert.assertEquals(4, products.size());
        for (int i = 0; i < products.size(); i++) {
            Assert.assertEquals(VALID_CUSTOMER_ID, products.get(i).getCustomerId());
            Assert.assertEquals(USER_ID, products.get(i).getModification().getCreatedBy());
            Assert.assertEquals(CommodityClass.CLASS_100, products.get(i).getCommodityClass());
            Assert.assertFalse(products.get(i).isHazmat());
        }

        Assert.assertEquals(new Long(30), products.get(0).getId());
        Assert.assertEquals(new Long(21), products.get(1).getId());
    }

    @Test
    public void shouldCheckSelfProductUniqueness() {
        String description = "Self Product";

        Assert.assertTrue(sut.isProductUnique(VALID_CUSTOMER_ID, null, USER_ID, description, CommodityClass.CLASS_100));

        LtlProductEntity product = new LtlProductEntity();
        product.setDescription(description);
        product.setCommodityClass(CommodityClass.CLASS_100);
        product.setCustomerId(VALID_CUSTOMER_ID);
        product.setPersonId(USER_ID);

        LtlProductEntity newEntity = sut.save(product);
        Assert.assertNotNull(newEntity);
        Long productId = newEntity.getId();
        Assert.assertNotNull(productId);

        Assert.assertFalse(sut.isProductUnique(VALID_CUSTOMER_ID, null, USER_ID, description, CommodityClass.CLASS_100));
        Assert.assertFalse(sut.isProductUnique(VALID_CUSTOMER_ID, null, USER_ID, StringUtils.lowerCase(description), CommodityClass.CLASS_100));
        Assert.assertTrue(sut.isProductUnique(VALID_CUSTOMER_ID, productId, null, description, CommodityClass.CLASS_100));
    }

    @Test
    public void shouldCheckSharedProductUniqueness() {
        String description = "Shared Product";

        Assert.assertTrue(sut.isProductUnique(VALID_CUSTOMER_ID, null, null, description, CommodityClass.CLASS_50));

        LtlProductEntity product = new LtlProductEntity();
        product.setDescription(description);
        product.setCommodityClass(CommodityClass.CLASS_50);
        product.setCustomerId(VALID_CUSTOMER_ID);
        product.setPersonId(null);

        LtlProductEntity newEntity = sut.save(product);
        Assert.assertNotNull(newEntity);
        Long productId = newEntity.getId();
        Assert.assertNotNull(productId);

        Assert.assertFalse(sut.isProductUnique(VALID_CUSTOMER_ID, null, null, description, CommodityClass.CLASS_50));
        Assert.assertFalse(sut.isProductUnique(VALID_CUSTOMER_ID, null, null, StringUtils.lowerCase(description), CommodityClass.CLASS_50));
        Assert.assertTrue(sut.isProductUnique(VALID_CUSTOMER_ID, productId, USER_ID, StringUtils.lowerCase(description), CommodityClass.CLASS_50));
    }

    @Test
    public void testFindProductByNameAndCode() {
        String productCode = "9N48581-RGB-XXS";
        String productDesc = "Guinness";

        LtlProductEntity product = sut.findProductByDescAndName(productCode, productDesc);

        Assert.assertNotNull(product);
        Assert.assertEquals("CLASS_55", product.getCommodityClass().name());

        String unavailableDesc = "This Description Does Not Exist";

        LtlProductEntity unavailableProduct = sut.findProductByDescAndName(productCode, unavailableDesc);

        Assert.assertNull(unavailableProduct);
    }

    @Test
    public void testFindProductByInfo() {

        String productDesc = "Guinness";
        Long orgId = 1L;
        CommodityClass commodityClass = CommodityClass.CLASS_55;
        LtlProductEntity product = sut.findProductByInfo(productDesc, orgId, false, commodityClass);

        Assert.assertNotNull(product);
        Assert.assertEquals("CLASS_55", product.getCommodityClass().name());

        Long differentCustomer = 5L;

        LtlProductEntity unavailableProduct = sut.findProductByInfo(productDesc, differentCustomer, false, CommodityClass.CLASS_100);

        Assert.assertNull(unavailableProduct);
    }

    @Test
    public void testFindUniqueProductByInfo() {
        Long orgId = 1L;
        CommodityClass commodityClass = CommodityClass.CLASS_50;
        String productCode = "SEM-MR-SC-12-MN";
        LtlProductEntity product = sut.findUniqueProductByInfo(orgId, commodityClass, productCode);
        Assert.assertNotNull(product);
        Assert.assertEquals("OFFICE SUPPLIES", product.getDescription());
    }

    private void updateLoadMaterialWithReferencedProduct(Long materialId, Long productId) {
        LoadMaterialEntity material = (LoadMaterialEntity) getSession().get(LoadMaterialEntity.class, materialId);
        material.setReferencedProductId(productId);
        getSession().update(material);
    }

    private LtlProductEntity prepareMinimalEntity() {
        LtlProductEntity result = new LtlProductEntity();
        // There are set of mandatory fields
        result.setDescription("TestDescription");

        result.setTrackingId(-1L);
        result.setCustomerId(1L);
        result.setLocationId(1L);

        result.getModification().setCreatedBy(1L);
        result.getModification().setCreatedDate(new Date());
        result.getModification().setModifiedDate(new Date());
        return result;
    }

    private void checkByFilterWord(List<LtlProductEntity> products, String filterWord) {
        Assert.assertFalse(products.isEmpty());
        for (LtlProductEntity product : products) {
            if (checkProductForFilterWord(filterWord, product)) {
                continue;
            }
            Assert.fail();
        }
    }

    private boolean checkProductForFilterWord(String filterWord, LtlProductEntity product) {
        return StringUtils.containsIgnoreCase(product.getProductCode(), filterWord)
                || StringUtils.containsIgnoreCase(product.getDescription(), filterWord);
    }

    private void checkByCommodityClass(CommodityClass commodityClass, List<LtlProductEntity> products) {
        Assert.assertFalse(products.isEmpty());
        for (LtlProductEntity product : products) {
            Assert.assertEquals(commodityClass, product.getCommodityClass());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Long> getUserWithSubordinatorsIds() {
        String userQuery = "SELECT PERSON_ID AS person_id FROM USERS WHERE PERSON_ID = " + CREATED_BY_USER_ID;
        Query users = getSession().createSQLQuery(userQuery).addScalar("person_id", StandardBasicTypes.LONG);
        return users.list();
    }

}
