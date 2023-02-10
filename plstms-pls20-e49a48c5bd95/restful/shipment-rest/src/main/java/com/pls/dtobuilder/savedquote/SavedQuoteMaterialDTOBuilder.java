package com.pls.dtobuilder.savedquote;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CommodityClassDTOBuilder;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;

/**
 * Saved Quote Material DTO builder.
 *
 * @author Mikhail Boldinov, 28/03/13
 */
public class SavedQuoteMaterialDTOBuilder extends AbstractDTOBuilder<SavedQuoteMaterialEntity, ShipmentMaterialDTO> {
    private static final CommodityClassDTOBuilder COMMODITY_CLASS_DTO_BUILDER = new CommodityClassDTOBuilder();

    private DataProvider dataProvider;

    @Override
    public ShipmentMaterialDTO buildDTO(SavedQuoteMaterialEntity entity) {
        ShipmentMaterialDTO dto = new ShipmentMaterialDTO();
        dto.setId(entity.getId());
        dto.setWeight(entity.getWeight());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setProductCode(entity.getProductCode());
        dto.setProductDescription(entity.getProductDescription());
        dto.setCommodityClass(entity.getCommodityClass() != null ? CommodityClassDTO.getByCode(entity.getCommodityClass().getDbCode()) : null);
        dto.setNmfc(entity.getNmfc());
        dto.setHazmat(entity.getHazmat());
        if (entity.getHazmatInfo() != null) {
            dto.setHazmatClass(entity.getHazmatInfo().getHazmatClass());
            dto.setPackingGroup(entity.getHazmatInfo().getPackingGroup());
            dto.setUnNum(entity.getHazmatInfo().getUnNumber());
            dto.setEmergencyResponseInstructions(entity.getHazmatInfo().getInstructions());
            dto.setEmergencyResponseCompany(entity.getHazmatInfo().getEmergencyCompany());
            dto.setEmergencyResponseContractNumber(entity.getHazmatInfo().getEmergencyContract());
            if (entity.getHazmatInfo().getEmergencyPhone() != null) {
                dto.setEmergencyResponsePhoneCountryCode(entity.getHazmatInfo().getEmergencyPhone().getCountryCode());
                dto.setEmergencyResponsePhoneAreaCode(entity.getHazmatInfo().getEmergencyPhone().getAreaCode());
                dto.setEmergencyResponsePhone(entity.getHazmatInfo().getEmergencyPhone().getNumber());

            }
        }
        if (entity.getPackageType() != null) {
            dto.setPackageType(entity.getPackageType().getId());
        }
        if (entity.getQuantity() != null) {
            dto.setQuantity(String.valueOf(entity.getQuantity()));
        }
        if (entity.getPieces() != null) {
            dto.setPieces(entity.getPieces().intValue());
        }
        dto.setStackable(entity.getStackable());
        return dto;
    }

    @Override
    public SavedQuoteMaterialEntity buildEntity(ShipmentMaterialDTO dto) {
        SavedQuoteMaterialEntity entity = new SavedQuoteMaterialEntity();
        updateEntity(entity, dto);
        return entity;
    }

    /**
     * Update existing entity with new values.
     *
     * @param entity to update
     * @param dto that contains new values
     */
    public void updateEntity(SavedQuoteMaterialEntity entity, ShipmentMaterialDTO dto) {
        entity.setWeight(dto.getWeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setProductCode(dto.getProductCode());
        entity.setProductDescription(dto.getProductDescription());
        entity.setCommodityClass(COMMODITY_CLASS_DTO_BUILDER.buildEntity(dto.getCommodityClass()));
        entity.setNmfc(dto.getNmfc());
        entity.setHazmat(dto.getHazmat() != null && dto.getHazmat());
        if (dto.getHazmat() != null && dto.getHazmat()) {
            entity.setHazmatInfo(getHazmatInfoEntity(dto));
        }
        entity.setPackageType(getPackageType(dto.getPackageType()));
        if (StringUtils.isNotBlank(dto.getQuantity())) {
            entity.setQuantity(Long.valueOf(dto.getQuantity()));
        }
        if (dto.getPieces() != null) {
            entity.setPieces(dto.getPieces().longValue());
        }
        entity.setStackable(dto.getStackable());
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

    private LtlProductHazmatInfo getHazmatInfoEntity(ShipmentMaterialDTO dto) {
        LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();
        hazmatInfo.setHazmatClass(dto.getHazmatClass());
        hazmatInfo.setPackingGroup(dto.getPackingGroup());
        hazmatInfo.setUnNumber(dto.getUnNum());
        hazmatInfo.setInstructions(dto.getEmergencyResponseInstructions());
        hazmatInfo.setEmergencyCompany(dto.getEmergencyResponseCompany());
        hazmatInfo.setEmergencyContract(dto.getEmergencyResponseContractNumber());
        PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();
        emergencyPhone.setCountryCode(dto.getEmergencyResponsePhoneCountryCode());
        emergencyPhone.setAreaCode(dto.getEmergencyResponsePhoneAreaCode());
        emergencyPhone.setNumber(dto.getEmergencyResponsePhone());
        hazmatInfo.setEmergencyPhone(emergencyPhone);
        return hazmatInfo;
    }

    /**
     * Data provider for package type.
     *
     * @author Sergey Kirichenko
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
