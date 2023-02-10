package com.pls.dtobuilder.shipment;

import com.pls.dto.lanedata.LaneDataDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.LaneDataEntity;

import java.util.Date;

/**
 * Map Entity object of Lane Data to DTO object.
 * 
 * @author Viacheslav Vasianovych
 * 
 */
public class LaneDataDTOBuilder extends AbstractDTOBuilder<LaneDataEntity, LaneDataDTO> {

    @Override
    public LaneDataDTO buildDTO(LaneDataEntity entity) {
        LaneDataDTO dto = new LaneDataDTO();
        dto.setId(entity.getId());
        dto.setAccessorials(entity.getAccessorials());
        dto.setBol(entity.getBol());
        dto.setCarrier(entity.getCarrier());
        dto.setClass1(entity.getClass1());
        dto.setClass2(entity.getClass2());
        dto.setCost(entity.getCost());
        dto.setDestinationZip(entity.getDestinationZip());
        dto.setFuel(entity.getFuel());
        if (entity.getInvoiceDate() != null) {
            dto.setInvoiceDate(new Date(entity.getInvoiceDate().getTime()));
        }

        dto.setOriginZip(entity.getOriginZip());
        if (entity.getPickupDate() != null) {
            dto.setPickupDate(new Date(entity.getPickupDate().getTime()));
        }
        dto.setTotal(entity.getTotal());
        dto.setWeight1(entity.getWeight1());
        dto.setWeight2(entity.getWeight2());
        return dto;
    }

    @Override
    public LaneDataEntity buildEntity(LaneDataDTO dto) {
        LaneDataEntity entity = new LaneDataEntity();
        entity.setId(dto.getId());
        entity.setAccessorials(dto.getAccessorials());
        entity.setBol(dto.getBol());
        entity.setCarrier(dto.getCarrier());
        entity.setClass1(dto.getClass1());
        entity.setClass2(dto.getClass2());
        entity.setCost(dto.getCost());
        entity.setDestinationZip(dto.getDestinationZip());
        entity.setFuel(dto.getFuel());
        entity.setInvoiceDate(dto.getInvoiceDate());
        entity.setOriginZip(dto.getOriginZip());
        entity.setPickupDate(dto.getPickupDate());
        entity.setTotal(dto.getTotal());
        entity.setWeight1(dto.getWeight1());
        entity.setWeight2(dto.getWeight2());
        return entity;
    }
}
