package com.pls.shipment.service.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.service.product.impl.ProductServiceImpl;

/**
 * Test cases for {@link ProductServiceImplTest} class.
 * 
 * @author Maxim Medvedev
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {
    private static final Long USER_ID = (long) (Math.random() * 100) + 1;
    private static final Long PARENT_USER_ID = (long) (Math.random() * 100) + 101;
    private static final Long INVALID_USER_ID = (long) (Math.random() * 100) + 202;
    private static final Long CUSTOMER_ID = (long) (Math.random() * 100) + 1;
    private static final Long PRODUCT_ID = (long) (Math.random() * 100) + 1;

    @Mock
    private LtlProductDao productDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private Validator<LtlProductEntity> validator;

    @InjectMocks
    private ProductServiceImpl sut;

    @After
    public void tearDown() {
        SecurityTestUtils.logout();
    }

    @Test
    public void shouldDeleteProductWithValidId() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        LtlProductEntity product = prepareProduct();
        when(productDao.find(PRODUCT_ID)).thenReturn(product);

        sut.archiveProduct(USER_ID, PRODUCT_ID);

        verify(productDao).markAsInactive(eq(PRODUCT_ID), eq(USER_ID), (Date) anyObject());
    }

    @Test
    public void shouldFindProductById() {
        sut.findById(PRODUCT_ID);

        verify(productDao).find(PRODUCT_ID);
    }

    @Test
    public void shouldUpdateSelfProduct() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, true);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
    }

    @Test
    public void shouldUpdateProductWithoutRole() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
    }

    @Test
    @Ignore("Check this test")
    // TODO It this logic correct? I am user USER_ID but ModifiedBy should be filled by PARENT_USER_ID. Why?
    public void shouldUpdateSubordinateUsersProduct() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertNotNull(product.getModification().getCreatedBy());
        assertSame(PARENT_USER_ID, product.getModification().getModifiedBy());
    }

    public void shouldUpdateSubordinateUsersProductWithSelfPrivilege() throws Exception {
        SecurityTestUtils.login("Test", PARENT_USER_ID);
        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertNotNull(product.getModification().getCreatedBy());
        assertSame(PARENT_USER_ID, product.getModification().getModifiedBy());
    }

    @Test
    public void shouldUpdateProductIfPLSUser() throws Exception {
        SecurityTestUtils.login("Test", INVALID_USER_ID, (Long) null);
        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertNotNull(product.getModification().getCreatedBy());
    }

    @Test
    public void shouldUpdateProductIfInTheSameOrganizationAndHasPermission() throws Exception {
        SecurityTestUtils.login("Test", INVALID_USER_ID, CUSTOMER_ID);
        LtlProductEntity product = prepareProduct();

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertNotNull(product.getModification().getCreatedBy());
    }

    @Test
    public void shouldCreateProductWithCurrentCustomer() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        LtlProductEntity product = prepareProduct();
        product.setCustomerId(null);

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertSame(CUSTOMER_ID, product.getCustomerId());
    }

    @Test
    public void shouldCreateProductWithoutPrivilege() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        LtlProductEntity product = prepareProduct();
        product.setCustomerId(null);

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertSame(CUSTOMER_ID, product.getCustomerId());
    }

    @Test
    public void shouldCreateProductWithSpecifiedCustomer() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);

        when(customerDao.checkCustomerExists(CUSTOMER_ID)).thenReturn(true);
        LtlProductEntity product = prepareProduct();
        product.setCustomerId(null);

        sut.saveOrUpdateProduct(product, CUSTOMER_ID, USER_ID, false);

        verify(validator).validate(product);
        verify(productDao).saveOrUpdate(product);
        assertSame(CUSTOMER_ID, product.getCustomerId());
    }

    @Test
    public void shouldFindAllProducts() throws Exception {

        sut.findAllProducts(CUSTOMER_ID, USER_ID);

        verify(productDao).getProductList(CUSTOMER_ID, USER_ID);
    }

    @Test
    public void shouldFindRecentProducts() {
        List<LtlProductEntity> expectedProductList = prepareRandomProductList();
        CommodityClass commodityClass = CommodityClass.CLASS_100;
        Boolean hazmat = Boolean.TRUE;
        Integer count = 15;
        String filter = "";

        when(productDao.findProducts(CUSTOMER_ID, USER_ID, filter, commodityClass, hazmat, count)).thenReturn(expectedProductList);

        List<LtlProductEntity> actualProductList = sut.findRecentProducts(CUSTOMER_ID, USER_ID, "",
                commodityClass, hazmat);

        assertEquals(expectedProductList, actualProductList);

        verify(productDao).findProducts(CUSTOMER_ID, USER_ID, filter, commodityClass, hazmat, count);
    }

    private List<LtlProductEntity> prepareRandomProductList() {
        List<LtlProductEntity> results = new ArrayList<LtlProductEntity>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            LtlProductEntity product = new LtlProductEntity();
            product.setId(random.nextLong());
            product.setCustomerId(random.nextLong());
            product.getModification().setCreatedBy(USER_ID);
            product.getModification().setModifiedBy((long) (Math.random() * 100 + 303));
            Date date = new Date();
            product.getModification().setCreatedDate(date);
            product.getModification().setModifiedDate(date);

            results.add(product);
        }

        return results;
    }

    private LtlProductEntity prepareProduct() {
        LtlProductEntity result = new LtlProductEntity();
        result.setDescription("TestDescription");

        result.setId(PRODUCT_ID);
        result.setCustomerId(CUSTOMER_ID);

        result.getModification().setCreatedBy(USER_ID);
        result.getModification().setModifiedBy((long) (Math.random() * 100 + 303));
        Date date = new Date();
        result.getModification().setCreatedDate(date);
        result.getModification().setModifiedDate(date);
        return result;
    }

}