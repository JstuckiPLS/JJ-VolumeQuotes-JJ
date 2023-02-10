package com.pls.dtobuilder.proposal;

import static com.pls.shipment.service.ShipmentService.GUARANTEED_SERVICE_REF_TYPE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingGuaranteedResult;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.smc3.dto.LTLDetailDTO;

/**
 * Build {@link ShipmentProposalBO} from {@link LtlPricingResult}.
 * 
 * @author Aleksandr Leshchenko
 */
public class PricingProposalDTOBuilder extends AbstractDTOBuilder<LtlPricingResult, ShipmentProposalBO> {
    private static final String CARRIER_FINAL_LINEHAUL_REF_TYPE = "CRA";
    private static final String SHIPPER_FINAL_LINEHAUL_REF_TYPE = "SRA";
    private static final String BENCHMARK_REF_TYPE = "SBR";
    private static final String FUEL_SURCHARGE_REF_TYPE = "FS";

    @Override
    public ShipmentProposalBO buildDTO(LtlPricingResult bo) {
        ShipmentProposalBO dto = new ShipmentProposalBO();
        dto.setGuid(UUID.randomUUID().toString());
        dto.setExternalUuid(bo.getExternalQuoteUuid());
        dto.setCarrierQuoteNumber(bo.getCarrierQuoteNumber());
        dto.setServiceLevelCode(bo.getServiceLevelCode());
        dto.setServiceLevelDescription(bo.getServiceLevelDescription());
        dto.setPricingProfileId(bo.getProfileId());
        dto.setEstimatedTransitDate(bo.getTransitDate());
        if (bo.getTransitTime() != null) {
            dto.setEstimatedTransitTime((long) (bo.getTransitTime() * 24 * 60));
        }
        dto.setServiceType(bo.getServiceType());
        dto.setIntegrationType(bo.getIntegrationType());
        dto.setRatingCarrierType(bo.getRatingCarrierType());
        dto.setCarrier(getCarrier(bo));
        dto.setNewLiability(bo.getNewProdLiability());
        dto.setUsedLiability(bo.getUsedProdLiability());
        dto.setProhibited(bo.getProhibitedCommodities());

        dto.setGuaranteedNameForBOL(bo.getBolCarrierName());
        dto.setMileage(bo.getTotalMiles());
        dto.setCarrierInitialCost(bo.getCarrierInitialLinehaul());
        dto.setCarrierDiscount(bo.getCarrierLinehaulDiscount());
        dto.setShipperInitialCost(bo.getShipperInitialLinehaul());
        dto.setShipperDiscount(bo.getShipperLinehaulDiscount());
        dto.setCostDetailItems(getCostDetailItems(bo));
        dto.setHideCostDetails("Y".equalsIgnoreCase(bo.getHideDetails()));
        dto.setHideTerminalDetails("Y".equalsIgnoreCase(bo.getHideTerminalDetails()));
        dto.setIncludeBenchmarkAccessorials("Y".equalsIgnoreCase(bo.getIncludeBenchmarkAcc()));
        dto.setTariffName(bo.getTariffName());
        dto.setDefaultMarginAmt(bo.getDefaultMarginAmt());
        dto.setMinLinehaulMarginAmt(bo.getMinLinehaulMarginAmt());
        dto.setAppliedLinehaulMarginAmt(bo.getAppliedLinehaulMarginAmt());
        dto.setLinehaulMarginPerc(bo.getLinehaulMarginPerc());
        dto.setTotalCarrierAmt(getRoundedBigDecimal(bo.getTotalCarrierCost()));
        dto.setTotalShipperAmt(getRoundedBigDecimal(bo.getTotalShipperCost()));
        dto.setTotalBenchmarkAmt(getRoundedBigDecimal(bo.getTotalBenchmarkCost()));
        dto.setPricingDetails(getPricingDetails(bo));
        dto.setTotalCarrierAmt(bo.getTotalCarrierCost());
        dto.setTotalShipperAmt(bo.getTotalShipperCost());
        dto.setTotalBenchmarkAmt(bo.getTotalBenchmarkCost());
        dto.setBlockedFrmBkng(bo.getBlockedFrmBkng());
        dto.setShipmentType(bo.getShipmentType());
        return dto;
    }

    private PricingDetailsBO getPricingDetails(LtlPricingResult bo) {
        PricingDetailsBO pricDtls = new PricingDetailsBO();
        if (bo.getCostDetails() != null) {
            pricDtls.setSmc3CostDetails(getSmc3CostDetails(bo.getCostDetails()));
        }
        pricDtls.setSmc3MinimumCharge(getRoundedBigDecimal(bo.getSmc3MinimumCharge()));
        pricDtls.setTotalChargeFromSmc3(getRoundedBigDecimal(bo.getTotalChargeFromSmc3()));
        pricDtls.setDeficitChargeFromSmc3(getRoundedBigDecimal(bo.getDeficitChargeFromSmc3()));
        pricDtls.setCostAfterDiscount(getRoundedBigDecimal(bo.getCostAfterDiscount()));
        pricDtls.setMinimumCost(getRoundedBigDecimal(bo.getMinimumCost()));
        pricDtls.setCostDiscount(bo.getCostDiscount());
        pricDtls.setCarrierFSId(bo.getCarrierFSId());
        pricDtls.setCarrierFuelDiscount(bo.getCarrierFuelDiscount());
        pricDtls.setPricingType(bo.getPricingType());
        pricDtls.setMovementType(bo.getMovementType());
        pricDtls.setEffectiveDate(bo.getEffectiveDate());
        pricDtls.setBuyProfileDetailId(bo.getCarrierPricingDetailId());
        pricDtls.setSellProfileDetailId(bo.getShipperPricingDetailId());
        return pricDtls;
    }

    private BigDecimal getRoundedBigDecimal(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
    }

    private Set<Smc3CostDetailsDTO> getSmc3CostDetails(List<LTLDetailDTO> costDetails) {
        Set<Smc3CostDetailsDTO> smc3CostDetails = new HashSet<Smc3CostDetailsDTO>();
        for (LTLDetailDTO costDetail : costDetails) {
            Smc3CostDetailsDTO smc3CostDetail = new Smc3CostDetailsDTO();
            smc3CostDetail.setCharge(costDetail.getCharge());
            smc3CostDetail.setEnteredNmfcClass(costDetail.getEnteredNmfcClass());
            smc3CostDetail.setNmfcClass(costDetail.getNmfcClass());
            smc3CostDetail.setRate(costDetail.getRate());
            smc3CostDetail.setWeight(costDetail.getWeight());
            smc3CostDetails.add(smc3CostDetail);
        }
        return smc3CostDetails;
    }

    private List<CostDetailItemBO> getCostDetailItems(LtlPricingResult bo) {
        List<CostDetailItemBO> costDetailItems = new ArrayList<CostDetailItemBO>();
        boolean benchmarkExist = true;
        if (bo.getBenchmarkFinalLinehaul() == null || bo.getBenchmarkFinalLinehaul().equals(BigDecimal.ZERO)
                || bo.getBenchmarkFuelSurcharge() == null || bo.getBenchmarkFuelSurcharge().equals(BigDecimal.ZERO)) {
            benchmarkExist = false;
        }
        addCostDetailItem(costDetailItems, CARRIER_FINAL_LINEHAUL_REF_TYPE, CostDetailOwner.C, bo.getCarrierFinalLinehaul());
        addCostDetailItem(costDetailItems, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.C, bo.getCarrierFuelSurcharge());
        if (bo.getGuaranteed() != null) {
            addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.C,
                    bo.getGuaranteed().getCarrierAccessorialCost(), bo.getGuaranteedTime());
        }

        addCostDetailItem(costDetailItems, SHIPPER_FINAL_LINEHAUL_REF_TYPE, CostDetailOwner.S, bo.getShipperFinalLinehaul());
        addCostDetailItem(costDetailItems, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.S, bo.getShipperFuelSurcharge());
        if (bo.getGuaranteed() != null) {
            addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.S,
                    bo.getGuaranteed().getShipperAccessorialCost(), bo.getGuaranteedTime());
        }

        addCostDetailItem(costDetailItems, BENCHMARK_REF_TYPE, CostDetailOwner.B, bo.getBenchmarkFinalLinehaul());
        addCostDetailItem(costDetailItems, FUEL_SURCHARGE_REF_TYPE, CostDetailOwner.B, bo.getBenchmarkFuelSurcharge());
        if (benchmarkExist && bo.getGuaranteed() != null) {
            addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.B,
                    bo.getGuaranteed().getBenchmarkAccessorialCost(), bo.getGuaranteedTime());
        }

        addAccessorials(costDetailItems, bo.getAccessorials(), benchmarkExist);
        addGuaranteedAccessorials(costDetailItems, bo.getGuaranteedAddlInfo(), benchmarkExist);
        return costDetailItems;
    }

    private void addGuaranteedAccessorials(List<CostDetailItemBO> costDetailItems,
            List<LtlPricingGuaranteedResult> guaranteedAccessorials, boolean benchmarkExist) {
        if (guaranteedAccessorials != null && !guaranteedAccessorials.isEmpty()) {
            for (LtlPricingGuaranteedResult accessorial : guaranteedAccessorials) {
                addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.S,
                        accessorial.getShipperGuaranteedCost(), accessorial.getGuaranteedTime());
                addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.C,
                        accessorial.getCarrierGuaranteedCost(), accessorial.getGuaranteedTime());
                if (benchmarkExist) {
                    addGuaranteedCostDetailItem(costDetailItems, GUARANTEED_SERVICE_REF_TYPE, CostDetailOwner.B,
                            accessorial.getBenchmarkGuaranteedCost(), accessorial.getGuaranteedTime());
                }
            }
        }
    }

    private void addAccessorials(List<CostDetailItemBO> costDetailItems,
            List<LtlPricingAccessorialResult> accessorials, boolean benchmarkExist) {
        if (accessorials != null && !accessorials.isEmpty()) {
            for (LtlPricingAccessorialResult accessorial : accessorials) {
                addAccessorialCostDetailItem(costDetailItems, accessorial.getAccessorialType(), CostDetailOwner.S,
                        accessorial.getShipperAccessorialCost());
                addAccessorialCostDetailItem(costDetailItems, accessorial.getAccessorialType(), CostDetailOwner.C,
                        accessorial.getCarrierAccessorialCost());
                if (benchmarkExist) {
                    addAccessorialCostDetailItem(costDetailItems, accessorial.getAccessorialType(), CostDetailOwner.B,
                            accessorial.getBenchmarkAccessorialCost());
                }
            }
        }
    }

    private void addCostDetailItem(List<CostDetailItemBO> items, String refType, CostDetailOwner owner, BigDecimal total) {
        if (total == null || total.equals(BigDecimal.ZERO)) {
            return;
        }
        addCostDetailItem(items, refType, owner, total, null);
    }

    private void addGuaranteedCostDetailItem(List<CostDetailItemBO> items, String refType, CostDetailOwner owner,
            BigDecimal total, Integer guaranteedTime) {
        if ((total == null || total.equals(BigDecimal.ZERO)) && guaranteedTime == null) {
            return;
        }
        addCostDetailItem(items, refType, owner, total, guaranteedTime);
    }

    private void addAccessorialCostDetailItem(List<CostDetailItemBO> items, String refType, CostDetailOwner owner,
            BigDecimal total) {
        addCostDetailItem(items, refType, owner, total, null);
    }

    private void addCostDetailItem(List<CostDetailItemBO> items, String refType, CostDetailOwner owner,
            BigDecimal total, Integer guaranteedTime) {
        CostDetailItemBO costDetailItem = new CostDetailItemBO();
        costDetailItem.setCostDetailOwner(owner);
        costDetailItem.setRefType(refType);
        costDetailItem.setSubTotal(total == null ? BigDecimal.ZERO : total.setScale(2, RoundingMode.HALF_UP));
        if (guaranteedTime != null) {
            costDetailItem.setGuaranteedBy(guaranteedTime.longValue());
        }
        items.add(costDetailItem);
    }

    private CarrierDTO getCarrier(LtlPricingResult bo) {
        CarrierDTO carrier = new CarrierDTO();
        carrier.setId(bo.getCarrierOrgId());
        carrier.setLogoPath(CarrierDTOBuilder.getCarrierLogoPath(bo.getCarrierOrgId()));
        carrier.setScac(bo.getScac());
        carrier.setCurrencyCode(bo.getCurrencyCode());
        carrier.setName(bo.getCarrierName());
        carrier.setSpecialMessage(bo.getCarrierNote());
        carrier.setApiCapable(bo.getIntegrationType() == CarrierIntegrationType.API);
        return carrier;
    }

    @Override
    public LtlPricingResult buildEntity(ShipmentProposalBO dto) {
        throw new UnsupportedOperationException("Operation is not supported");
    }

}
