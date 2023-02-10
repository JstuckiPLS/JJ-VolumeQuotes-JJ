package com.pls.dtobuilder.product;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CommodityClassDTOBuilder;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * Builder for conversion between {@link LoadMaterialEntity} and {@link ShipmentMaterialDTO}.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentMaterialDTOBuilder extends AbstractDTOBuilder<LoadMaterialEntity, ShipmentMaterialDTO> {
    private static final CommodityClassDTOBuilder COMMODITY_CLASS_DTO_BUILDER = new CommodityClassDTOBuilder();

    private DataProvider dataProvider;

    @Override
    public ShipmentMaterialDTO buildDTO(LoadMaterialEntity entity) {
        ShipmentMaterialDTO dto = new ShipmentMaterialDTO();

        dto.setHazmat(entity.isHazmat());
        dto.setHazmatClass(entity.getHazmatClass());
        dto.setHeight(entity.getHeight());
        dto.setWidth(entity.getWidth());
        dto.setLength(entity.getLength());
        dto.setId(entity.getId());

        dto.setProductId(entity.getReferencedProductId());
        dto.setCommodityClass(COMMODITY_CLASS_DTO_BUILDER.buildDTO(entity.getCommodityClass()));
        if (entity.getPackageType() != null) {
            dto.setPackageType(entity.getPackageType().getId());
        }

        dto.setProductDescription(entity.getProductDescription());
        dto.setProductCode(entity.getProductCode());
        dto.setNmfc(entity.getNmfc());
        dto.setUnNum(entity.getUnNumber());
        dto.setPackingGroup(entity.getPackingGroup());

        dto.setEmergencyResponseCompany(entity.getEmergencyCompany());
        dto.setEmergencyResponseContractNumber(entity.getEmergencyContract());
        dto.setEmergencyResponseInstructions(entity.getHazmatInstruction());
        PhoneNumber emergencyPhone = entity.getEmergencyPhone();
        if (emergencyPhone != null) {
            dto.setEmergencyResponsePhoneCountryCode(emergencyPhone.getCountryCode());
            dto.setEmergencyResponsePhoneAreaCode(emergencyPhone.getAreaCode());
            dto.setEmergencyResponsePhone(emergencyPhone.getNumber());
        }
        dto.setStackable(entity.isStackable());
        dto.setQuantity(entity.getQuantity());
        dto.setWeight(entity.getWeight());
        dto.setPieces(entity.getPieces());
        return dto;
    }

    @Override
    public LoadMaterialEntity buildEntity(ShipmentMaterialDTO dto) {
        LoadMaterialEntity entity = new LoadMaterialEntity();
        updateEntity(entity, dto);
        return entity;
    }

    /**
     * Update existing entity with new values.
     * 
     * @param entity
     *            to update
     * @param dto
     *            that contains new values
     */
    public void updateEntity(LoadMaterialEntity entity, ShipmentMaterialDTO dto) {
        entity.setCommodityClass(COMMODITY_CLASS_DTO_BUILDER.buildEntity(dto.getCommodityClass()));
        entity.setPackageType(getPackageType(dto.getPackageType()));

        entity.setHazmat(dto.getHazmat() != null && dto.getHazmat());
        entity.setHazmatClass(dto.getHazmatClass());
        entity.setHazmatInstruction(dto.getEmergencyResponseInstructions());
        entity.setEmergencyCompany(dto.getEmergencyResponseCompany());
        entity.setEmergencyContract(dto.getEmergencyResponseContractNumber());

        PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();
        emergencyPhone.setCountryCode(dto.getEmergencyResponsePhoneCountryCode());
        emergencyPhone.setAreaCode(dto.getEmergencyResponsePhoneAreaCode());
        emergencyPhone.setNumber(dto.getEmergencyResponsePhone());
        emergencyPhone.setExtension(dto.getEmergencyResponsePhoneExtension());
        entity.setEmergencyPhone(emergencyPhone);

        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        entity.setWeight(dto.getWeight());
        entity.setPieces(dto.getPieces());
        entity.setQuantity(dto.getQuantity());
        entity.setNmfc(dto.getNmfc());
        entity.setProductCode(dto.getProductCode());
        entity.setProductDescription(dto.getProductDescription());
        entity.setUnNumber(dto.getUnNum());
        entity.setPackingGroup(dto.getPackingGroup());
        entity.setStackable(dto.getStackable() != null && dto.getStackable());

        entity.setReferencedProductId(dto.getProductId());
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
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
     * Data provider to build entity.
     */
    public interface DataProvider {
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
