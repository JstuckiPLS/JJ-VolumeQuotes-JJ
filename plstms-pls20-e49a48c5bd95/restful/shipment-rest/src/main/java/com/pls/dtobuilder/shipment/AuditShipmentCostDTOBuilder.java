package com.pls.dtobuilder.shipment;

import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsBO;

/**
 * DTO Builder for carrier invoice additional detail item.
 * 
 * @author Brichak Aleksandr
 */
public class AuditShipmentCostDTOBuilder
        extends AbstractDTOBuilder<AuditShipmentCostDetailsEntity, AuditShipmentCostsBO> {

    @Override
    public AuditShipmentCostsBO buildDTO(AuditShipmentCostDetailsEntity entity) {
        if (entity == null) {
            return null;
        }
        AuditShipmentCostsBO bo = new AuditShipmentCostsBO();
        bo.setDisputeCost(entity.getDisputeCost());
        bo.setRequestPaperwork(entity.getRequestPaperwork());
        bo.setUpdateRevenue(entity.getUpdateRevenue());
        bo.setUpdateRevenueValue(entity.getUpdateRevenueValue());
        bo.setId(entity.getId());
        return bo;
    }

    @Override
    public AuditShipmentCostDetailsEntity buildEntity(AuditShipmentCostsBO bo) {
        AuditShipmentCostDetailsEntity entity = new AuditShipmentCostDetailsEntity();
        entity.setDisputeCost(bo.getDisputeCost());
        entity.setId(bo.getId());
        entity.setUpdateRevenue(bo.getUpdateRevenue());
        entity.setUpdateRevenueValue(bo.getUpdateRevenueValue());
        entity.setRequestPaperwork(bo.getRequestPaperwork());
        return entity;
    }
}
