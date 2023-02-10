package com.pls.restful.shipment.product;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.dto.ProductDTO;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.service.ProductService;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * Tests for {@link ProductResource}.
 * 
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductResourceTest {

    @InjectMocks
    private ProductResource sut;

    @Mock
    private UserPermissionsService userPermissionsService;

    @Mock
    private ProductService productService;

    @Mock
    private PackageTypeDictionaryService packageTypeDictionaryService;

    private static final Long ORG_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final Long INCORRECT_USER_ID = 2L;

    private static final Long PRODUCT_ID = 1L;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();
    }

    @Test(expected = ApplicationException.class)
    public void testArchiveDifferentUserProduct() throws Exception {
        SecurityTestUtils.login("Test", INCORRECT_USER_ID);
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(false));

        sut.archive(ORG_ID, PRODUCT_ID);
    }

    @Test
    public void testArchive() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(true));

        sut.archive(ORG_ID, PRODUCT_ID);

        verify(productService, times(1)).archiveProduct(eq(USER_ID), eq(PRODUCT_ID));
    }

    @Test(expected = ApplicationException.class)
    public void testGetProductCreatedNyDifferentUser() throws Exception {
        SecurityTestUtils.login("Test", INCORRECT_USER_ID);
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(false));

        sut.get(ORG_ID, PRODUCT_ID);
    }

    @Test
    public void testGetMyProduct() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(false));

        ProductDTO dto = sut.get(ORG_ID, PRODUCT_ID);
        Assert.assertNotNull(dto);
    }

    @Test
    public void testGetSharedProduct() throws Exception {
        SecurityTestUtils.login("Test", INCORRECT_USER_ID);
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(true));

        ProductDTO dto = sut.get(ORG_ID, PRODUCT_ID);
        Assert.assertNotNull(dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveSelfProductWithoutSelfPermission() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        PackageTypeEntity pack = createTestPackageType();
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(true));
        when(packageTypeDictionaryService.getById("BAG")).thenReturn(pack);
        when(userPermissionsService.hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name())).thenReturn(false);

        ProductDTO dto = createTestProductDTO(false);
        sut.save(ORG_ID, dto);
    }

    @Test(expected = ApplicationException.class)
    public void testSaveDifferentUserProductWithSelfPermission() throws Exception {
        PackageTypeEntity pack = createTestPackageType();
        when(productService.findById(PRODUCT_ID)).thenReturn(createTestProductEntity(false));
        when(packageTypeDictionaryService.getById("BAG")).thenReturn(pack);
        when(userPermissionsService.hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name())).thenReturn(true);

        ProductDTO dto = createTestProductDTO(false);
        sut.save(ORG_ID, dto);
    }

    @Test
    public void testSaveSelfProductWithSelfPermission() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        PackageTypeEntity pack = createTestPackageType();
        when(productService.findById(1L)).thenReturn(createTestProductEntity(true));
        when(packageTypeDictionaryService.getById("BAG")).thenReturn(pack);
        when(userPermissionsService.hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name())).thenReturn(true);

        ProductDTO dto = createTestProductDTO(false);
        sut.save(ORG_ID, dto);

        verify(userPermissionsService, times(1)).checkCapabilityAndOrganization(eq(ORG_ID),
                eq(Capabilities.ADD_EDIT_PRODUCT.name()));
        verify(userPermissionsService, times(1)).hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name());
        verify(productService, times(1)).saveOrUpdateProduct(notNull(LtlProductEntity.class), eq(ORG_ID), eq(USER_ID), eq(false));
    }

    @Test
    public void testSaveSharedProduct() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
        PackageTypeEntity pack = createTestPackageType();
        when(productService.findById(1L)).thenReturn(createTestProductEntity(true));
        when(packageTypeDictionaryService.getById("BAG")).thenReturn(pack);
        when(userPermissionsService.hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name())).thenReturn(true);

        ProductDTO dto = createTestProductDTO(true);
        sut.save(ORG_ID, dto);

        verify(userPermissionsService, times(1)).checkCapabilityAndOrganization(eq(ORG_ID),
                eq(Capabilities.ADD_EDIT_PRODUCT.name()));
        verify(userPermissionsService, times(0)).hasCapability(Capabilities.PRODUCT_LIST_CREATE_SELF.name());
        verify(productService, times(1)).saveOrUpdateProduct(notNull(LtlProductEntity.class), eq(ORG_ID), eq(USER_ID), eq(true));
    }

    private ProductDTO createTestProductDTO(boolean isShared) {
        ProductDTO dto = new ProductDTO();
        dto.setId(PRODUCT_ID);
        dto.setDescription("Product Description " + Math.random());
        dto.setSharedProduct(isShared);
        dto.setPackageType("BAG");

        return dto;
    }

    private LtlProductEntity createTestProductEntity(boolean isShared) {
        LtlProductEntity entity = new LtlProductEntity();
        entity.getModification().setCreatedBy(USER_ID);
        if (!isShared) {
            entity.setPersonId(USER_ID);
        }

        return entity;
    }

    private PackageTypeEntity createTestPackageType() {
        PackageTypeEntity pack = new PackageTypeEntity();
        pack.setId("BAG");
        pack.setDescription("Bags");

        return pack;
    }
}
