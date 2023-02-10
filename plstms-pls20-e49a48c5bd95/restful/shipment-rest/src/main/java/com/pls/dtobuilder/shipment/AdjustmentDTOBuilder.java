package com.pls.dtobuilder.shipment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.pls.core.shared.AccessorialType;
import com.pls.dto.adjustment.AdjustmentDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.product.ShipmentMaterialDTOBuilder;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsAdditionalInfoEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.bo.AdjustmentLoadInfoBO;
import com.pls.shipment.domain.enums.AdjustmentReason;

/**
 * Adjustment DTO builder.
 * 
 * @author Aleksandr Leshchenko
 */
public class AdjustmentDTOBuilder extends AbstractDTOBuilder<LoadEntity, AdjustmentDTO> {

    private final CarrierDTOBuilder carrierBuilder = new CarrierDTOBuilder();
    private final AdjustmentDetailsDTOBuilder detailsBuilder = new AdjustmentDetailsDTOBuilder();
    private final ShipmentMaterialDTOBuilder materialBuilder = new ShipmentMaterialDTOBuilder();

    @Override
    public AdjustmentDTO buildDTO(LoadEntity entity) {
        AdjustmentDTO dto = null;
        if (CollectionUtils.isNotEmpty(entity.getAllFinancialAccessorials())) {
            dto = new AdjustmentDTO();
            dto.setCostItems(new ArrayList<>());
            for (FinancialAccessorialsEntity adjustmentEntity : entity.getAllFinancialAccessorials()) {
                List<AdjustmentBO> adjustments = detailsBuilder.buildList(adjustmentEntity.getCostDetailItems());
                dto.getCostItems().addAll(joinAdjustments(adjustments));
                setRollbackInfo(dto, adjustmentEntity);
            }
            dto.getCostItems().sort(Comparator.comparing(AdjustmentBO::getFinancialAccessorialsId));
        }
        return dto;
    }

    private List<AdjustmentBO> joinAdjustments(List<AdjustmentBO> adjustments) {
        return adjustments.stream().collect(Collectors.groupingBy(this::getRefType)).values().stream().filter(l -> l.size() == 2)
                .map(this::joinItems).collect(Collectors.toList());
    }

    private AdjustmentBO joinItems(List<AdjustmentBO> list) {
        AdjustmentBO bo = list.get(0);
        bo.setRefType(getRefType(bo));
        if (list.get(1).getRevenue() != null) {
            bo.setRevenue(list.get(1).getRevenue());
            bo.setRevenueNote(list.get(1).getRevenueNote());
        } else {
            bo.setCost(list.get(1).getCost());
            bo.setCostNote(list.get(1).getCostNote());
        }
        return bo;
    }

    private String getRefType(AdjustmentBO bo) {
        return AccessorialType.CARRIER_BASE_RATE.getType().equals(bo.getRefType()) ? AccessorialType.SHIPPER_BASE_RATE.getType()
                : bo.getRefType();
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            {@link AdjustmentDTO}
     * @return {@link LoadEntity}
     */
    @Override
    public LoadEntity buildEntity(AdjustmentDTO dto) {
        throw new UnsupportedOperationException();
    }

    private void setRollbackInfo(AdjustmentDTO dto, FinancialAccessorialsEntity adjustmentEntity) {
        if (dto.getLoadInfo() == null && adjustmentEntity.getGeneralLedgerDate() == null) {
            buildCarrier(dto, adjustmentEntity);
            buildAdditionalInfo(dto, adjustmentEntity);
        }
    }

    private void buildCarrier(AdjustmentDTO dto, FinancialAccessorialsEntity adjustmentEntity) {
        CostDetailItemEntity costItem = adjustmentEntity.getCostDetailItems().iterator().next();
        if (costItem.getReason().getId().intValue() == AdjustmentReason.WRONG_CARRIER.getReason()
                && !costItem.getCarrierId().equals(adjustmentEntity.getLoad().getCarriedId())) {
            dto.setLoadInfo(new AdjustmentLoadInfoBO());
            dto.getLoadInfo().setCarrier(carrierBuilder.buildDTO(costItem.getCarrier()));
        }
    }

    private void buildAdditionalInfo(AdjustmentDTO dto, FinancialAccessorialsEntity adjustmentEntity) {
        if (CollectionUtils.isNotEmpty(adjustmentEntity.getRollbackInfo())) {
            AdjustmentLoadInfoBO loadInfo = new AdjustmentLoadInfoBO();
            loadInfo.setBillToId(adjustmentEntity.getCostDetailItems().iterator().next().getBillTo().getId());
            loadInfo.setBolNumber(adjustmentEntity.getBol());
            FinancialAccessorialsAdditionalInfoEntity adjAddInfo = adjustmentEntity.getRollbackInfo().iterator().next();
            loadInfo.setPoNumber(adjAddInfo.getPoNumber());
            loadInfo.setRefNumber(adjAddInfo.getRefNumber());
            loadInfo.setSoNumber(adjAddInfo.getSoNumber());
            dto.setLoadInfo(loadInfo);

            List<ShipmentMaterialDTO> materials = adjustmentEntity.getAdjProductInfo().stream()
                    .map(i -> materialBuilder.buildDTO(i.getLoadMaterial())).collect(Collectors.toList());
            dto.setProducts(materials);
        }
    }
}
