package com.pls.dtobuilder.shipment;

import com.pls.dto.shipment.CarrierInvoiceCostItemDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;

/**
 * DTO builder for {@link CarrierInvoiceCostItemEntity}.
 *
 * @author Sergey Kirichenko
 */
public class CarrierInvoiceCostItemDTOBuilder extends AbstractDTOBuilder<CarrierInvoiceCostItemEntity, CarrierInvoiceCostItemDTO> {

    @Override
    public CarrierInvoiceCostItemDTO buildDTO(CarrierInvoiceCostItemEntity entity) {
        CarrierInvoiceCostItemDTO dto = new CarrierInvoiceCostItemDTO();
        dto.setId(entity.getId());
        dto.setRefType(entity.getAccessorialType());
        dto.setSubTotal(entity.getSubtotal());
        return dto;
    }

    @Override
    public CarrierInvoiceCostItemEntity buildEntity(CarrierInvoiceCostItemDTO dto) {
        CarrierInvoiceCostItemEntity entity = new CarrierInvoiceCostItemEntity();
        entity.setAccessorialType(dto.getRefType());
        entity.setSubtotal(dto.getSubTotal());
        return entity;
    }
}
