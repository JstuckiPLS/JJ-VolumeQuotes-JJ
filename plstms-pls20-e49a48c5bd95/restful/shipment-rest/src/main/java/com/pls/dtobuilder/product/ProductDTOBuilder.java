package com.pls.dtobuilder.product;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.ProductDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CommodityClassDTOBuilder;
import com.pls.dtobuilder.util.PhoneFaxDTOBuilder.PhoneDTOBuilder;
import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * DTO Builder for products.
 * 
 * @author Aleksandr Leshchenko
 */
public class ProductDTOBuilder extends AbstractDTOBuilder<LtlProductEntity, ProductDTO> {
    private static final CommodityClassDTOBuilder COMMODITY_CLASS_DTO_BUILDER = new CommodityClassDTOBuilder();
    private final DataProvider dataProvider;

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            {@link DataProvider}
     */
    public ProductDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public ProductDTO buildDTO(LtlProductEntity p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());

        if (p.getPackageType() != null) {
            dto.setPackageType(p.getPackageType().getId());
        }
        dto.setWeight(p.getWeight());

        dto.setHazmat(p.isHazmat());
        LtlProductHazmatInfo hazmatInfo = p.getHazmatInfo();
        if (hazmatInfo != null) {
            dto.setHazmatClass(hazmatInfo.getHazmatClass());
            dto.setHazmatPackingGroup(hazmatInfo.getPackingGroup());
            dto.setHazmatUnNumber(hazmatInfo.getUnNumber());
            dto.setHazmatEmergencyCompany(hazmatInfo.getEmergencyCompany());
            dto.setHazmatEmergencyContract(hazmatInfo.getEmergencyContract());
            PhoneBO hazmatEmergencyPhone = null;
            if (hazmatInfo.getEmergencyPhone() != null) {
                hazmatEmergencyPhone = new PhoneDTOBuilder().buildDTO(hazmatInfo.getEmergencyPhone());
            }
            dto.setHazmatEmergencyPhone(hazmatEmergencyPhone);
            dto.setHazmatInstructions(hazmatInfo.getInstructions());
        }

        dto.setPieces(p.getPieces());
        dto.setNmfc(p.getNmfcNum());
        dto.setNmfcSubNum(p.getNmfcSubNum());
        dto.setProductCode(p.getProductCode());
        dto.setCommodityClass(COMMODITY_CLASS_DTO_BUILDER.buildDTO(p.getCommodityClass()));
        dto.setDescription(p.getDescription());
        dto.setSharedProduct(p.getPersonId() == null);
        dto.setCreatedBy(p.getModification().getCreatedBy());

        return dto;
    }

    @Override
    public LtlProductEntity buildEntity(ProductDTO dto) {
        LtlProductEntity entity = getProduct(dto.getId());

        entity.setPackageType(getPackageType(dto.getPackageType()));
        entity.setWeight(dto.getWeight());

        if (entity.getHazmatInfo() == null) {
            entity.setHazmatInfo(new LtlProductHazmatInfo());
        }
        if (dto.isHazmat()) {
            entity.getHazmatInfo().setHazmatClass(dto.getHazmatClass());
            entity.getHazmatInfo().setPackingGroup(dto.getHazmatPackingGroup());
            entity.getHazmatInfo().setUnNumber(dto.getHazmatUnNumber());
            entity.getHazmatInfo().setEmergencyCompany(dto.getHazmatEmergencyCompany());
            entity.getHazmatInfo().setEmergencyContract(dto.getHazmatEmergencyContract());
            if (dto.getHazmatEmergencyPhone() != null) {
                entity.getHazmatInfo().setEmergencyPhone(new PhoneDTOBuilder().buildEntity(dto.getHazmatEmergencyPhone()));
            } else {
                entity.getHazmatInfo().setEmergencyPhone(null);
            }
            entity.getHazmatInfo().setInstructions(dto.getHazmatInstructions());
        } else {
            entity.getHazmatInfo().setHazmatClass(null);
            entity.getHazmatInfo().setPackingGroup(null);
            entity.getHazmatInfo().setUnNumber(null);
            entity.getHazmatInfo().setEmergencyCompany(null);
            entity.getHazmatInfo().setEmergencyContract(null);
            entity.getHazmatInfo().getEmergencyPhone().setAreaCode(null);
            entity.getHazmatInfo().getEmergencyPhone().setCountryCode(null);
            entity.getHazmatInfo().getEmergencyPhone().setNumber(null);
            entity.getHazmatInfo().setInstructions(null);
        }

        entity.setPieces(dto.getPieces());
        entity.setNmfcNum(dto.getNmfc());
        entity.setNmfcSubNum(dto.getNmfcSubNum());
        entity.setProductCode(dto.getProductCode());
        entity.setCommodityClass(COMMODITY_CLASS_DTO_BUILDER.buildEntity(dto.getCommodityClass()));
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private LtlProductEntity getProduct(Long id) {
        if (id == null) {
            return new LtlProductEntity();
        }
        return dataProvider.getProductById(id);
    }

    private PackageTypeEntity getPackageType(String id) {
        if (id == null) {
            return null;
        }
        if (dataProvider != null) {
            return dataProvider.findPackageType(id);
        }
        PackageTypeEntity entity = new PackageTypeEntity();
        entity.setId(id);
        return entity;
    }

    /**
     * Data provider for product.
     * 
     * @author Aleksandr Leshchenko
     */
    public interface DataProvider {
        /**
         * Get product by id.
         * 
         * @param id
         *            {@link LtlProductEntity#getId()}
         * @return {@link LtlProductEntity}
         */
        LtlProductEntity getProductById(Long id);

        /**
         * Provider of {@link PackageTypeEntity}.
         *
         * @param id
         *            {@link PackageTypeEntity#getId()}
         * @return entity or <code>null</code>s
         */
        PackageTypeEntity findPackageType(String id);
    }
}
