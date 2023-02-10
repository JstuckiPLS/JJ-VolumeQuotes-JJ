package com.pls.dtobuilder.product;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.ProductDTO;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dtobuilder.product.ProductDTOBuilder.DataProvider;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;


/**
 * Test cases for {@link ProductDTOBuilder} class.
 * 
 * @author Aleksandr Leshchenko
 */
public class ProductDTOBuilderTest {
    @Test
    public void shouldBuildDTO() {
        LtlProductEntity entity = createEntity(true);

        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);

        assertNotNull(dto);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDTOWithoutHazmat() {
        LtlProductEntity entity = createEntity(false);

        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);

        assertNotNull(dto);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDtoWithBlankNmfc() {
        LtlProductEntity entity = createEntity(true);
        entity.setNmfcNum(null);
        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);

        assertNotNull(dto);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDtoWithBlankNmfcSubNumber() {
        LtlProductEntity entity = createEntity(true);
        entity.setNmfcSubNum(null);
        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);

        assertNotNull(dto);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shoudBuildDtoWithBothBlankNmfcFields() {
        LtlProductEntity entity = createEntity(true);
        entity.setNmfcNum(null);
        entity.setNmfcSubNum(null);
        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);

        assertNotNull(dto);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntityForUpdate() {
        ProductDTO dto = createDTO(true);

        DataProvider provider = mock(DataProvider.class);
        LtlProductEntity foundEntity = new LtlProductEntity();
        foundEntity.setId(dto.getId());
        when(provider.getProductById(dto.getId())).thenReturn(foundEntity);
        PackageTypeEntity packageTypeEntity = new PackageTypeEntity();
        packageTypeEntity.setId(dto.getPackageType());
        when(provider.findPackageType(dto.getPackageType())).thenReturn(packageTypeEntity);

        LtlProductEntity entity = new ProductDTOBuilder(provider).buildEntity(dto);

        assertSame(foundEntity, entity);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntityForUpdateWithoutHazmat() {
        ProductDTO dto = createDTO(false);

        DataProvider provider = mock(DataProvider.class);
        LtlProductEntity foundEntity = new LtlProductEntity();
        foundEntity.setId(dto.getId());
        when(provider.getProductById(dto.getId())).thenReturn(foundEntity);
        PackageTypeEntity packageTypeEntity = new PackageTypeEntity();
        packageTypeEntity.setId(dto.getPackageType());
        when(provider.findPackageType(dto.getPackageType())).thenReturn(packageTypeEntity);

        LtlProductEntity entity = new ProductDTOBuilder(provider).buildEntity(dto);

        assertSame(foundEntity, entity);
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildEntityForCreate() {
        ProductDTO dto = createDTO(true);
        dto.setId(null);

        LtlProductEntity entity = new ProductDTOBuilder(null).buildEntity(dto);

        assertNull(entity.getId());
        compareDTOAndEntity(dto, entity);
    }

    @Test
    public void shouldBuildDTOWithSharedOption() {
        LtlProductEntity entity = createEntityWithSharedOption(true);

        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);
        assertNotNull(dto);
        assertTrue(dto.isSharedProduct());
    }

    @Test
    public void shouldBuildDTOWithoutSharedOption() {
        LtlProductEntity entity = createEntityWithSharedOption(false);

        ProductDTO dto = new ProductDTOBuilder(null).buildDTO(entity);
        assertNotNull(dto);
        assertFalse(dto.isSharedProduct());
    }

    private void compareDTOAndEntity(ProductDTO dto, LtlProductEntity entity) {
        assertSame(dto.getId(), entity.getId());
        assertSame(dto.getPackageType(), entity.getPackageType().getId());
        assertSame(dto.getWeight(), entity.getWeight());

        assertSame(dto.isHazmat(), entity.isHazmat());
        assertNotNull(entity.getHazmatInfo());
        assertSame(dto.getHazmatClass(), entity.getHazmatInfo().getHazmatClass());
        assertSame(dto.getHazmatPackingGroup(), entity.getHazmatInfo().getPackingGroup());
        assertSame(dto.getHazmatUnNumber(), entity.getHazmatInfo().getUnNumber());
        assertSame(dto.getHazmatEmergencyCompany(), entity.getHazmatInfo().getEmergencyCompany());
        assertSame(dto.getHazmatEmergencyContract(), entity.getHazmatInfo().getEmergencyContract());
        assertSame(dto.getHazmatInstructions(), entity.getHazmatInfo().getInstructions());

        if (dto.getHazmatEmergencyPhone() == null) {
            assertNull(entity.getHazmatInfo().getEmergencyPhone().getCountryCode());
            assertNull(entity.getHazmatInfo().getEmergencyPhone().getAreaCode());
            assertNull(entity.getHazmatInfo().getEmergencyPhone().getNumber());
        } else {
            assertSame(dto.getHazmatEmergencyPhone().getCountryCode(), entity.getHazmatInfo().getEmergencyPhone().getCountryCode());
            assertSame(dto.getHazmatEmergencyPhone().getAreaCode(), entity.getHazmatInfo().getEmergencyPhone().getAreaCode());
            assertSame(dto.getHazmatEmergencyPhone().getNumber(), entity.getHazmatInfo().getEmergencyPhone().getNumber());
        }

        assertSame(dto.getPieces(), entity.getPieces());
        assertSame(dto.getNmfc(), entity.getNmfcNum());
        assertSame(dto.getNmfcSubNum(), entity.getNmfcSubNum());
        assertSame(dto.getProductCode(), entity.getProductCode());
        assertSame(dto.getCommodityClass().name(), entity.getCommodityClass().name());
        assertSame(dto.getDescription(), entity.getDescription());
    }

    private LtlProductEntity createEntity(boolean hazmat) {
        LtlProductEntity product = new LtlProductEntity();
        product.setId((long) (Math.random() * 100));
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setId("PLT");
        packageType.setId("Pallet");
        product.setPackageType(packageType);
        product.setWeight(BigDecimal.valueOf(Math.random()));

        LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();
        if (hazmat) {
            hazmatInfo.setHazmatClass("hazmatClass" + Math.random());
            hazmatInfo.setPackingGroup("packingGroup" + Math.random());
            hazmatInfo.setUnNumber("unNumber" + Math.random());
            hazmatInfo.setEmergencyCompany("emergencyCompany" + Math.random());
            hazmatInfo.setEmergencyContract("emergencyContract" + Math.random());
            PhoneEmbeddableObject phone = new PhoneEmbeddableObject();
            phone.setCountryCode("countryCode" + Math.random());
            phone.setAreaCode("areaCode" + Math.random());
            phone.setNumber("number" + Math.random());
            hazmatInfo.setEmergencyPhone(phone);
            hazmatInfo.setInstructions("instructions" + Math.random());
        }
        product.setHazmatInfo(hazmatInfo);

        product.setPieces((long) (Math.random() * 100));
        product.setNmfcNum("nmfcNum" + Math.random());
        product.setNmfcSubNum("nmfcSubNum" + Math.random());
        product.setProductCode("productCode" + Math.random());
        product.setCommodityClass(CommodityClass.values()[(int) (Math.random() * (CommodityClass.values().length - 1))]);
        product.setDescription("description" + Math.random());
        return product;
    }

    private LtlProductEntity createEntityWithSharedOption(boolean isShared) {
        LtlProductEntity product = createEntity(false);
        if (!isShared) {
            product.setPersonId((long) Math.random() * 100);
        }

        return product;
    }

    private ProductDTO createDTO(boolean hazmat) {
        ProductDTO dto = new ProductDTO();
        dto.setId((long) (Math.random() * 100));

        dto.setPackageType("PLT");
        dto.setWeight(BigDecimal.valueOf(Math.random()));

        if (hazmat) {
            dto.setHazmat(hazmat);
            dto.setHazmatClass("hazmatClass" + Math.random());
            dto.setHazmatPackingGroup("packingGroup" + Math.random());
            dto.setHazmatUnNumber("unNumber" + Math.random());
            dto.setHazmatEmergencyCompany("emergencyCompany" + Math.random());
            dto.setHazmatEmergencyContract("emergencyContract" + Math.random());
            PhoneBO phone = new PhoneBO();
            phone.setCountryCode("countryCode" + Math.random());
            phone.setAreaCode("areaCode" + Math.random());
            phone.setNumber("number" + Math.random());
            dto.setHazmatEmergencyPhone(phone);
            dto.setHazmatInstructions("instructions" + Math.random());
        }

        dto.setPieces((long) (Math.random() * 100));
        dto.setNmfc("nmfcNum" + Math.random());
        dto.setNmfcSubNum("nmfcSubNum" + Math.random());
        dto.setProductCode("productCode" + Math.random());
        dto.setCommodityClass(CommodityClassDTO.values()[(int) (Math.random() * (CommodityClassDTO.values().length - 1))]);
        dto.setDescription("description" + Math.random());

        return dto;
    }
}
