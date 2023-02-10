package com.pls.dtobuilder.shipment;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.enums.WeightUnit;
import com.pls.dto.shipment.CarrierInvoiceLineItemDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;

/**
 * DTO builder for {@link CarrierInvoiceLineItemEntity}.
 *
 * @author Alexander Kirichenko
 */
public class CarrierInvoiceLineItemDTOBuilder extends AbstractDTOBuilder<CarrierInvoiceLineItemEntity, CarrierInvoiceLineItemDTO> {

    public static final BigDecimal LBS_IN_KG = new BigDecimal("2.20462262185");

    @Override
    public CarrierInvoiceLineItemDTO buildDTO(CarrierInvoiceLineItemEntity entity) {
        CarrierInvoiceLineItemDTO dto = new CarrierInvoiceLineItemDTO();
        dto.setId(entity.getId());
        if (entity.getWeight() != null) {
            dto.setWeight(entity.getWeight());
        }
        dto.setWeightUnit(WeightUnit.LBS);
        dto.setProductDescription(entity.getDescription());
        if (entity.getCommodityClass() != null) {
            dto.setCommodityClass(CommodityClassDTO.getByCode(entity.getCommodityClass().getDbCode()));
        }
        dto.setNmfc(entity.getNmfc());
        dto.setQuantity(entity.getQuantity());
        if (entity.getPackagingCode() != null) {
            dto.setPackageType(entity.getPackagingCode());
        }
        if (entity.getCharge() != null) {
            dto.setCost(entity.getCharge());
        }
        return dto;
    }

    @Override
    public CarrierInvoiceLineItemEntity buildEntity(CarrierInvoiceLineItemDTO dto) {
        CarrierInvoiceLineItemEntity entity = new CarrierInvoiceLineItemEntity();
        if (dto.getWeight() != null) {
            entity.setWeight(convertToLbs(dto.getWeight(), dto.getWeightUnit()));
        }
        entity.setDescription(dto.getProductDescription());
        if (dto.getCommodityClass() != null) {
            entity.setCommodityClass(CommodityClass.convertFromDbCode(dto.getCommodityClass().getLabel()));
        }
        entity.setNmfc(dto.getNmfc());
        entity.setQuantity(dto.getQuantity());
        if (dto.getPackageType() != null) {
            entity.setPackagingCode(dto.getPackageType());
        }
        if (dto.getCost() != null) {
            entity.setCharge(dto.getCost());
        }
        return entity;
    }

    private BigDecimal convertToLbs(BigDecimal weight, WeightUnit weightUnit) {
        BigDecimal calculatedWeight = BigDecimal.ZERO;
        if (weight != null) {
            calculatedWeight = weight;
            if (calculatedWeight.compareTo(BigDecimal.ZERO) > 0 && weightUnit == WeightUnit.KG) {
                calculatedWeight = calculatedWeight.multiply(LBS_IN_KG).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return calculatedWeight;
    }
}
