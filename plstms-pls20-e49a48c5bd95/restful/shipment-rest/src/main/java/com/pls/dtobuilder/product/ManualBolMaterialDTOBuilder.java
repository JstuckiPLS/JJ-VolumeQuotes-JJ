package com.pls.dtobuilder.product;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.dto.shipment.ManualBolMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CommodityClassDTOBuilder;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;

/**
 * Builder for conversion between {@link ManualBolMaterialEntity} and {@link ManualBolMaterialDTO}.
 * 
 * @author Sergey Vovchuk
 */
public class ManualBolMaterialDTOBuilder extends AbstractDTOBuilder<ManualBolMaterialEntity, ManualBolMaterialDTO> {
    private static final CommodityClassDTOBuilder COMMODITY_BUILDER = new CommodityClassDTOBuilder();

    private DataProvider dataProvider;

    @Override
    public ManualBolMaterialDTO buildDTO(ManualBolMaterialEntity entity) {
        ManualBolMaterialDTO dto = new ManualBolMaterialDTO();

        dto.setHazmat(entity.isHazmat());
        dto.setHazmatClass(entity.getHazmatClass());
        dto.setHeight(entity.getHeight());
        dto.setWidth(entity.getWidth());
        dto.setLength(entity.getLength());
        dto.setId(entity.getId());

        dto.setReferencedProductId(entity.getReferencedProductId());
        dto.setCommodityClass(COMMODITY_BUILDER.buildDTO(entity.getCommodityClass()));
        if (entity.getPackageType() != null) {
            dto.setPackageType(entity.getPackageType().getId());
        }

        dto.setProductDescription(entity.getProductDescription());
        dto.setProductCode(entity.getProductCode());
        dto.setNmfc(entity.getNmfc());

        dto.setStackable(entity.isStackable());
        dto.setQuantity(entity.getQuantity());
        dto.setWeight(entity.getWeight());
        dto.setPieces(entity.getPieces());
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
        return dto;
    }

    @Override
    public ManualBolMaterialEntity buildEntity(ManualBolMaterialDTO dto) {
        ManualBolMaterialEntity entity = new ManualBolMaterialEntity();
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
    public void updateEntity(ManualBolMaterialEntity entity, ManualBolMaterialDTO dto) {
        entity.setProductCode(dto.getProductCode());
        entity.setProductDescription(dto.getProductDescription());
        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        entity.setWeight(dto.getWeight());
        entity.setPieces(dto.getPieces());
        entity.setQuantity(dto.getQuantity());
        entity.setNmfc(dto.getNmfc());
        entity.setStackable(dto.getStackable() != null && dto.getStackable());
        entity.setReferencedProductId(dto.getReferencedProductId());
        entity.setPackageType(getPackageType(dto.getPackageType()));
        entity.setUnNumber(dto.getUnNum());
        entity.setPackingGroup(dto.getPackingGroup());

        entity.setCommodityClass(COMMODITY_BUILDER.buildEntity(dto.getCommodityClass()));

        buildHazmatProperties(entity, dto);
        buildEmergencyPhone(entity, dto);
    }

    private void buildEmergencyPhone(ManualBolMaterialEntity entity, ManualBolMaterialDTO dto) {
        PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();
        emergencyPhone.setCountryCode(dto.getEmergencyResponsePhoneCountryCode());
        emergencyPhone.setAreaCode(dto.getEmergencyResponsePhoneAreaCode());
        emergencyPhone.setNumber(dto.getEmergencyResponsePhone());
        entity.setEmergencyPhone(emergencyPhone);
    }

    private void buildHazmatProperties(ManualBolMaterialEntity entity, ManualBolMaterialDTO dto) {
        entity.setHazmat(dto.getHazmat() != null && dto.getHazmat());
        entity.setHazmatClass(dto.getHazmatClass());
        entity.setHazmatInstruction(dto.getEmergencyResponseInstructions());
        entity.setEmergencyCompany(dto.getEmergencyResponseCompany());
        entity.setEmergencyContract(dto.getEmergencyResponseContractNumber());
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
