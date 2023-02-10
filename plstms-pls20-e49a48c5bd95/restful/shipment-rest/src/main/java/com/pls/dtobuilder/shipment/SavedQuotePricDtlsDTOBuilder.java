package com.pls.dtobuilder.shipment;

import java.util.HashSet;
import java.util.Set;

import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;
import com.pls.shipment.domain.SavedQuotePricMatDtlsEntity;

/**
 * DTO Builder for saved quote pricing detail item.
 * 
 * @author Ashwini Neelgund
 */
public class SavedQuotePricDtlsDTOBuilder extends AbstractDTOBuilder<SavedQuotePricDtlsEntity, PricingDetailsBO> {

    @Override
    public PricingDetailsBO buildDTO(SavedQuotePricDtlsEntity savedQuotePricDtls) {
        PricingDetailsBO pricingDetailsDTO = new PricingDetailsBO();
        if (savedQuotePricDtls != null) {
            pricingDetailsDTO.setSmc3MinimumCharge(savedQuotePricDtls.getSmc3MinimumCharge());
            pricingDetailsDTO.setTotalChargeFromSmc3(savedQuotePricDtls.getTotalChargeFromSmc3());
            pricingDetailsDTO.setDeficitChargeFromSmc3(savedQuotePricDtls.getDeficitChargeFromSmc3());
            pricingDetailsDTO.setCarrierFSId(savedQuotePricDtls.getCarrierFSId());
            pricingDetailsDTO.setCarrierFuelDiscount(savedQuotePricDtls.getCarrierFuelDiscount());
            pricingDetailsDTO.setPricingType(savedQuotePricDtls.getPricingType());
            pricingDetailsDTO.setCostAfterDiscount(savedQuotePricDtls.getCostAfterDiscount());
            pricingDetailsDTO.setMinimumCost(savedQuotePricDtls.getMinimumCost());
            pricingDetailsDTO.setCostDiscount(savedQuotePricDtls.getCostDiscount());
            pricingDetailsDTO.setMovementType(savedQuotePricDtls.getMovementType());
            pricingDetailsDTO.setEffectiveDate(savedQuotePricDtls.getEffectiveDate());
            if (savedQuotePricDtls.getSavedQuotePricMatDtls() != null) {
                pricingDetailsDTO.setSmc3CostDetails(getSmc3CostDetails(savedQuotePricDtls.getSavedQuotePricMatDtls()));
            }
        }
        return pricingDetailsDTO;
    }

    /**
     * Saved quote pricing detail entity builder from DTO.
     * 
     * @param pricingDetails
     *            pricing details dto
     * @return savedQuotePricDtls - saved quote pricing detail entity
     */
    @Override
    public SavedQuotePricDtlsEntity buildEntity(PricingDetailsBO pricingDetails) {
        SavedQuotePricDtlsEntity savedQuotePricDtls = new SavedQuotePricDtlsEntity();
        savedQuotePricDtls.setSmc3MinimumCharge(pricingDetails.getSmc3MinimumCharge());
        savedQuotePricDtls.setTotalChargeFromSmc3(pricingDetails.getTotalChargeFromSmc3());
        savedQuotePricDtls.setDeficitChargeFromSmc3(pricingDetails.getDeficitChargeFromSmc3());
        savedQuotePricDtls.setCostAfterDiscount(pricingDetails.getCostAfterDiscount());
        savedQuotePricDtls.setMinimumCost(pricingDetails.getMinimumCost());
        savedQuotePricDtls.setCostDiscount(pricingDetails.getCostDiscount());
        savedQuotePricDtls.setCarrierFSId(pricingDetails.getCarrierFSId());
        savedQuotePricDtls.setCarrierFuelDiscount(pricingDetails.getCarrierFuelDiscount());
        savedQuotePricDtls.setPricingType(pricingDetails.getPricingType());
        savedQuotePricDtls.setMovementType(pricingDetails.getMovementType());
        savedQuotePricDtls.setEffectiveDate(pricingDetails.getEffectiveDate());
        Set<SavedQuotePricMatDtlsEntity> savedQuotePricMatDtls = new HashSet<SavedQuotePricMatDtlsEntity>();
        if (pricingDetails.getSmc3CostDetails() != null) {
            for (Smc3CostDetailsDTO smc3CostDetails : pricingDetails.getSmc3CostDetails()) {
                SavedQuotePricMatDtlsEntity savedQuotePricMatDtl = new SavedQuotePricMatDtlsEntity();
                savedQuotePricMatDtl.setCharge(smc3CostDetails.getCharge());
                savedQuotePricMatDtl.setNmfcClass(smc3CostDetails.getNmfcClass());
                savedQuotePricMatDtl.setEnteredNmfcClass(smc3CostDetails.getEnteredNmfcClass());
                savedQuotePricMatDtl.setRate(smc3CostDetails.getRate());
                savedQuotePricMatDtl.setWeight(smc3CostDetails.getWeight());
                savedQuotePricMatDtl.setSavedQuotePricDtls(savedQuotePricDtls);
                savedQuotePricMatDtls.add(savedQuotePricMatDtl);
            }
        }
        savedQuotePricDtls.setSavedQuotePricMatDtls(savedQuotePricMatDtls);
        return savedQuotePricDtls;
    }

    private Set<Smc3CostDetailsDTO> getSmc3CostDetails(Set<SavedQuotePricMatDtlsEntity> savedQuotePricMatDtls) {
        Set<Smc3CostDetailsDTO> smc3CostDetails = new HashSet<Smc3CostDetailsDTO>();
        for (SavedQuotePricMatDtlsEntity savedQuotePricMatDtl : savedQuotePricMatDtls) {
            Smc3CostDetailsDTO smc3CostDetail = new Smc3CostDetailsDTO();
            smc3CostDetail.setCharge(savedQuotePricMatDtl.getCharge());
            smc3CostDetail.setRate(savedQuotePricMatDtl.getRate());
            smc3CostDetail.setWeight(savedQuotePricMatDtl.getWeight());
            smc3CostDetail.setNmfcClass(savedQuotePricMatDtl.getNmfcClass());
            smc3CostDetail.setEnteredNmfcClass(savedQuotePricMatDtl.getEnteredNmfcClass());
            smc3CostDetails.add(smc3CostDetail);
        }
        return smc3CostDetails;
    }

}
