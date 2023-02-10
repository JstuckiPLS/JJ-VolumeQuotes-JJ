package com.pls.dtobuilder.shipment;

import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.shipment.domain.CostDetailItemEntity;

/**
 * DTO Builder for cost detail item.
 * 
 * @author Aleksandr Leshchenko
 */
public class CostDetailItemDTOBuilder extends AbstractDTOBuilder<CostDetailItemEntity, CostDetailItemBO> {

    @Override
    public CostDetailItemBO buildDTO(CostDetailItemEntity entity) {
        CostDetailItemBO dto = new CostDetailItemBO();
        dto.setRefType(entity.getAccessorialType());
        dto.setSubTotal(entity.getSubtotal());
        dto.setCostDetailOwner(entity.getOwner());
        dto.setNote(entity.getNote());
        return dto;
    }

    /**
     * Method is not supported. We shouldn't build entities related to money from DTO.
     * 
     * @param dto
     *            dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public CostDetailItemEntity buildEntity(CostDetailItemBO dto) {
        throw new UnsupportedOperationException();
    }

}
