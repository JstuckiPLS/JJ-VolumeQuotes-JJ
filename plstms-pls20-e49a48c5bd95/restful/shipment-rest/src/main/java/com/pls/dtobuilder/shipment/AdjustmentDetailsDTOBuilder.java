package com.pls.dtobuilder.shipment;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.enums.AdjustmentReason;

/**
 * Adjustment DTO builder.
 * 
 * @author Dmitry Nikolaenko
 */
public class AdjustmentDetailsDTOBuilder extends AbstractDTOBuilder<CostDetailItemEntity, AdjustmentBO> {

    @Override
    public AdjustmentBO buildDTO(CostDetailItemEntity entity) {
        FinancialAccessorialsEntity adjustmentEntity = entity.getFinancialAccessorials();
        AdjustmentBO adjustmentDTO = new AdjustmentBO();
        adjustmentDTO.setFinancialAccessorialsId(adjustmentEntity.getId());
        adjustmentDTO.setVersion(adjustmentEntity.getVersion());
        adjustmentDTO.setRefType(entity.getAccessorialType());
        if (entity.getOwner() == CostDetailOwner.S) {
            adjustmentDTO.setRevenue(entity.getSubtotal());
            adjustmentDTO.setRevenueNote(entity.getNote());
        } else {
            adjustmentDTO.setCost(entity.getSubtotal());
            adjustmentDTO.setCostNote(entity.getNote());
        }
        if (entity.getReason() != null) {
            adjustmentDTO.setReason(entity.getReason().getId());
        }
        if (entity.getBillTo() != null) {
            adjustmentDTO.setBillToName(entity.getBillTo().getName());
        }
        if (entity.getReason().getId().intValue() == AdjustmentReason.WRONG_CARRIER.getReason()) {
            adjustmentDTO.setCarrierName(entity.getCarrier().getName());
        }
        adjustmentDTO.setInvoiceNumber(adjustmentEntity.getInvoiceNumber());
        adjustmentDTO.setInvoiceDate(adjustmentEntity.getGeneralLedgerDate());
        adjustmentDTO.setNotInvoice(adjustmentEntity.getShortPay() != null && adjustmentEntity.getShortPay());
        return adjustmentDTO;
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto {@link AdjustmentBO}
     * @return {@link CostDetailItemEntity}
     */
    @Override
    public CostDetailItemEntity buildEntity(AdjustmentBO dto) {
        throw new UnsupportedOperationException();
    }
}
