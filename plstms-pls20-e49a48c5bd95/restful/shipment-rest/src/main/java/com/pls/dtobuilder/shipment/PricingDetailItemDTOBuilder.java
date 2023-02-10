package com.pls.dtobuilder.shipment;

import java.util.HashSet;
import java.util.Set;

import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.shipment.domain.LoadPricMaterialDtlsEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;

/**
 * DTO Builder for pricing detail item.
 * 
 * @author Ashwini Neelgund
 */
public class PricingDetailItemDTOBuilder extends AbstractDTOBuilder<LoadPricingDetailsEntity, PricingDetailsBO> {

    @Override
    public PricingDetailsBO buildDTO(LoadPricingDetailsEntity loadPricingDetails) {
        PricingDetailsBO pricingDetailsDTO = new PricingDetailsBO();
        if (loadPricingDetails != null) {
            pricingDetailsDTO.setSmc3MinimumCharge(loadPricingDetails.getSmc3MinimumCharge());
            pricingDetailsDTO.setTotalChargeFromSmc3(loadPricingDetails.getTotalChargeFromSmc3());
            pricingDetailsDTO.setDeficitChargeFromSmc3(loadPricingDetails.getDeficitChargeFromSmc3());
            pricingDetailsDTO.setCostAfterDiscount(loadPricingDetails.getCostAfterDiscount());
            pricingDetailsDTO.setMinimumCost(loadPricingDetails.getMinimumCost());
            pricingDetailsDTO.setCostDiscount(loadPricingDetails.getCostDiscount());
            pricingDetailsDTO.setCarrierFSId(loadPricingDetails.getCarrierFSId());
            pricingDetailsDTO.setCarrierFuelDiscount(loadPricingDetails.getCarrierFuelDiscount());
            pricingDetailsDTO.setPricingType(loadPricingDetails.getPricingType());
            pricingDetailsDTO.setMovementType(loadPricingDetails.getMovementType());
            pricingDetailsDTO.setEffectiveDate(loadPricingDetails.getEffectiveDate());
            if (loadPricingDetails.getLoadPricMaterialDtls() != null) {
                pricingDetailsDTO.setSmc3CostDetails(getSmc3CostDetails(loadPricingDetails.getLoadPricMaterialDtls()));
            }
        }
        return pricingDetailsDTO;
    }

    private Set<Smc3CostDetailsDTO> getSmc3CostDetails(Set<LoadPricMaterialDtlsEntity> loadPricMaterialDtls) {
        Set<Smc3CostDetailsDTO> smc3CostDetails = new HashSet<Smc3CostDetailsDTO>();
        for (LoadPricMaterialDtlsEntity loadPricMatDtl : loadPricMaterialDtls) {
            Smc3CostDetailsDTO smc3CostDetail = new Smc3CostDetailsDTO();
            smc3CostDetail.setCharge(loadPricMatDtl.getCharge());
            smc3CostDetail.setNmfcClass(loadPricMatDtl.getNmfcClass());
            smc3CostDetail.setEnteredNmfcClass(loadPricMatDtl.getEnteredNmfcClass());
            smc3CostDetail.setRate(loadPricMatDtl.getRate());
            smc3CostDetail.setWeight(loadPricMatDtl.getWeight());
            smc3CostDetail.setDescription(loadPricMatDtl.getDescription());
            smc3CostDetail.setNmfc(loadPricMatDtl.getNmfc());
            smc3CostDetail.setQuantity(loadPricMatDtl.getQuantity());
            smc3CostDetails.add(smc3CostDetail);
        }
        return smc3CostDetails;
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
    public LoadPricingDetailsEntity buildEntity(PricingDetailsBO dto) {
        throw new UnsupportedOperationException();
    }

}
