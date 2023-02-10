package com.pls.dtobuilder.shipment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.StatusYesNo;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.LtlPricPropCostDetailsEntity;
import com.pls.shipment.domain.LtlPricingProposalsEntity;

/**
 * Builder for {@link LtlPricingProposalsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public class LtlPricingProposalEntityBuilder {
    private static final String GUARANTEED_DELIVERY = "GD";
    private static final String PALLET = "Pallet";
    private static final String LINEHAUL = "INIT_LH";
    private static final String DISCOUNT_REF_TYPE = "DISCOUNT";

    /**
     * Method to build the pricing proposal entities.
     * 
     * @param dto
     *            - Shipment DTO.
     * @param selectedCarrierId
     *            - Id of the carrier with whom the load was booked.
     * @return proposalEntities - {@link List} of {@link LtlPricingProposalsEntity}.
     */
    public List<LtlPricingProposalsEntity> buildPricingProposals(ShipmentDTO dto, Long selectedCarrierId) {
        List<LtlPricingProposalsEntity> proposalEntities = new ArrayList<LtlPricingProposalsEntity>();
        if (CollectionUtils.isNotEmpty(dto.getProposals())) {
            for (ShipmentProposalBO proposal : dto.getProposals()) {
                //The proposal which is excluded from booking should not be considered during generation of
                //Lost Savings Opportunity report.
                if (proposal.getBlockedFrmBkng() != StatusYesNo.YES) {
                    LtlPricingProposalsEntity proposalEntity = buildPricingProposalEntity(proposal, dto.getGuaranteedBy());
                    proposalEntity.setProposalSelected(ObjectUtils.equals(selectedCarrierId, proposal.getCarrier().getId()));
                    proposalEntities.add(proposalEntity);
                }
            }
            buildProposalDetails(proposalEntities, dto.getFinishOrder().getQuoteMaterials());
        }
        return proposalEntities;
    }

    private LtlPricingProposalsEntity buildPricingProposalEntity(ShipmentProposalBO proposal, Long guaranteedBy) {
        LtlPricingProposalsEntity proposalEntity = new LtlPricingProposalsEntity();
        proposalEntity.setLtlPricProfId(proposal.getPricingProfileId());
        proposalEntity.setTotalRevenue(proposal.getTotalShipperAmt());
        proposalEntity.setTotalCost(proposal.getTotalCarrierAmt());
        proposalEntity.setTotalBenchmark(proposal.getTotalBenchmarkAmt());
        proposalEntity.setTransitTime(TimeUnit.MINUTES.toDays(proposal.getEstimatedTransitTime()));
        proposalEntity.setServiceType(proposal.getServiceType());
        proposalEntity.setNewProdLiabAmt(proposal.getNewLiability());
        proposalEntity.setUsedProdLiabAmt(proposal.getUsedLiability());
        proposalEntity.setProhibitedCommodities(proposal.getProhibited());
        proposalEntity.setPricProfNote(proposal.getCarrier().getSpecialMessage());
        proposalEntity.setGuaranBolName(proposal.getGuaranteedNameForBOL());
        proposalEntity.setSmc3TariffName(proposal.getTariffName());
        if (proposal.getAddlGuaranInfo() != null && !proposal.getAddlGuaranInfo().isEmpty()) {
            proposalEntity.setAddlGuaranInfo(proposal.getAddlGuaranInfo().replace("&nbsp;", " "));
        }
        proposalEntity.setGuaranteedTime(guaranteedBy);
        proposalEntity.setPricPropDtlEntity(buildPricingProposalDetails(proposal, proposalEntity));
        proposalEntity.setCarrierQuoteNumber(proposal.getCarrierQuoteNumber());
        proposalEntity.setServiceLevelCode(proposal.getServiceLevelCode());
        proposalEntity.setServiceLevelDescription(proposal.getServiceLevelDescription());
        return proposalEntity;
    }

    private Set<LtlPricPropCostDetailsEntity> buildPricingProposalDetails(ShipmentProposalBO proposal, LtlPricingProposalsEntity entity) {
        Long personId = SecurityUtils.getCurrentPersonId();
        Set<LtlPricPropCostDetailsEntity> details = new HashSet<LtlPricPropCostDetailsEntity>();
        for (CostDetailItemBO costDetailItem : proposal.getCostDetailItems()) {
            if (!StringUtils.equalsIgnoreCase(costDetailItem.getRefType(), GUARANTEED_DELIVERY)
                    || ObjectUtils.equals(costDetailItem.getGuaranteedBy(), entity.getGuaranteedTime())) {
                details.add(getPricingProposalDetailEntity(costDetailItem.getLtlPricingId(), costDetailItem.getRefType(),
                        costDetailItem.getCostDetailOwner(), costDetailItem.getSubTotal(), personId, entity, true));
            }
        }
        details.add(getPricingProposalDetailEntity(LINEHAUL, CostDetailOwner.C, proposal.getCarrierInitialCost(), personId, entity));
        details.add(getPricingProposalDetailEntity(LINEHAUL, CostDetailOwner.S, proposal.getShipperInitialCost(), personId, entity));
        details.add(getPricingProposalDetailEntity(DISCOUNT_REF_TYPE, CostDetailOwner.C, proposal.getCarrierDiscount(), personId, entity));
        details.add(getPricingProposalDetailEntity(DISCOUNT_REF_TYPE, CostDetailOwner.S, proposal.getShipperDiscount(), personId, entity));
        return details;
    }

    private LtlPricPropCostDetailsEntity getPricingProposalDetailEntity(String refType, CostDetailOwner costDetailOwner, BigDecimal subTotal,
            Long personId, LtlPricingProposalsEntity proposalEntity) {
        return getPricingProposalDetailEntity(null, refType, costDetailOwner, subTotal, personId, proposalEntity, false);
    }

    private LtlPricPropCostDetailsEntity getPricingProposalDetailEntity(Long pricingId, String refType, CostDetailOwner costDetailOwner,
            BigDecimal subTotal, Long personId, LtlPricingProposalsEntity proposalEntity, boolean billable) {
        LtlPricPropCostDetailsEntity pricProDtlEntity = new LtlPricPropCostDetailsEntity();
        pricProDtlEntity.setLtlPricingId(pricingId);
        pricProDtlEntity.setRefType(refType);
        pricProDtlEntity.setShipCarr(costDetailOwner);
        pricProDtlEntity.setSubTotal(subTotal);
        pricProDtlEntity.setCreatedBy(personId);
        pricProDtlEntity.setLtlPricProposalEntity(proposalEntity);
        pricProDtlEntity.setBillable(billable);
        return pricProDtlEntity;
    }

    private void buildProposalDetails(List<LtlPricingProposalsEntity> proposalEntities, List<ShipmentMaterialDTO> quoteMaterials) {
        int totalQuantity = 0;
        int totalPieces = 0;
        boolean pallet = false;
        boolean hazmat = false;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (ShipmentMaterialDTO shipmentMaterialDTO : quoteMaterials) {
            if (StringUtils.isNotEmpty(shipmentMaterialDTO.getQuantity())) {
                totalQuantity += Integer.parseInt(shipmentMaterialDTO.getQuantity());
            }
            if (shipmentMaterialDTO.getPieces() != null) {
                totalPieces += shipmentMaterialDTO.getPieces();
            }
            if (StringUtils.equalsIgnoreCase(shipmentMaterialDTO.getPackageType(), PALLET)) {
                pallet = true;
            }
            if (BooleanUtils.isTrue(shipmentMaterialDTO.getHazmat())) {
                hazmat = true;
            }
            if (shipmentMaterialDTO.getWeight() != null) {
                totalWeight = totalWeight.add(shipmentMaterialDTO.getWeight());
            }
        }

        for (LtlPricingProposalsEntity proposalEntity : proposalEntities) {
            proposalEntity.setTotalQuantity(totalQuantity);
            proposalEntity.setTotalPieces(totalPieces);
            proposalEntity.setPalletPackageType(pallet);
            proposalEntity.setHazmat(hazmat);
            proposalEntity.setTotalWeight(totalWeight);
        }
    }
}
