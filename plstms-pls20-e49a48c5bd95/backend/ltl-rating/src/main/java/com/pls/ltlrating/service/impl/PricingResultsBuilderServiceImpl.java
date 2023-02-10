package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.shared.StatusYesNo;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.ltlrating.dao.LtlCarrierLiabilitiesDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.bo.LTLDayAndRossRateBO;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.domain.enums.UnitType;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO.AccessorialChargeDTO;
import com.pls.ltlrating.service.PricingResultsBuilderService;
import com.pls.ltlrating.shared.AccessorialPricingVO;
import com.pls.ltlrating.shared.CarrierPricingProfilesVO;
import com.pls.ltlrating.shared.CarrierRatingVO;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingGuaranteedResult;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.LtlRatingAccessorialsVO;
import com.pls.ltlrating.shared.LtlRatingFSTriggerVO;
import com.pls.ltlrating.shared.LtlRatingGuaranteedVO;
import com.pls.ltlrating.shared.LtlRatingMarginVO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * Implementation of {@link PricingResultsBuilderService}.
 *
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional(readOnly = true)
public class PricingResultsBuilderServiceImpl implements PricingResultsBuilderService {
    private static final Logger LOG = LoggerFactory.getLogger(PricingResultsBuilderServiceImpl.class);

    private static final String YES = "Y";

    @Autowired
    private LtlCarrierLiabilitiesDao carrLiabilitiesDao;

    @Override
    public List<LtlPricingResult> getPricingResults(GetOrderRatesCO ratesCO, OrganizationPricingEntity orgPricing,
            LtlRatingVO carrierProfileVO, LtlRatingVO benchmarkProfileVO, LtlRatingMarginVO margin) throws InterruptedException {
        List<LtlPricingResult> finalRates = new ArrayList<LtlPricingResult>();
        
        if (!carrierProfileVO.getCarrierPricingDetails().isEmpty()) {
            // Iterate over valid list of carrier rates and calculate the cost and margin.
            for (Entry<Long, List<CarrierRatingVO>> carrierPricing : carrierProfileVO.getCarrierPricingDetails().entrySet()) {
                CarrierRatingVO carrierRating = carrierPricing.getValue().get(0);
                Long orgnId = carrierPricing.getKey();
                
                // there can be multiple quotes for the same carrier via LTLLC
                if(carrierRating.getRate().getPricingDetails().getLtllcResponses() != null) {
                    for(QuoteResultDTO ltllcResponse : carrierRating.getRate().getPricingDetails().getLtllcResponses()) {
                        carrierRating.getRate().getPricingDetails().setLtllcResponse(ltllcResponse);
                        LtlPricingResult pricingResult = getPricingResult(orgnId, carrierRating, margin, orgPricing, benchmarkProfileVO, ratesCO);
                        if (pricingResult != null) {
                            finalRates.add(pricingResult);
                        }
                    }
                } else {
                    LtlPricingResult pricingResult = getPricingResult(orgnId, carrierRating, margin, orgPricing, benchmarkProfileVO, ratesCO);
                    if (pricingResult != null) {
                        finalRates.add(pricingResult);
                    }
                }
            }
        }
        LOG.info("******** END PRICING ********");
        
        return finalRates;
    }

    private LtlPricingResult getPricingResult(Long carrierOrgId, CarrierRatingVO carrierRateMap,
            LtlRatingMarginVO marginVO, OrganizationPricingEntity orgPricing, LtlRatingVO benchmarkProfileVO, GetOrderRatesCO ratesCO) {
        
        //ltllc profile rates are special, can contain ltl and vltl quotes
        if(LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType()) && carrierRateMap.getRate().getPricingDetails().getLtllcResponse() == null) {
            return null;
        }
        
        //This is the final pricing VO we return to the user and display the same on the "Select Carrier" screen.
        LtlPricingResult pricingResult = new LtlPricingResult();

        //If the Customer account is GainShare account, then we dont need to worry about margin set on carrier profile.
        //We calculate margin differently for Gainshare account.
        if (orgPricing.getGainshare().equals(StatusYesNo.YES)) {
            pricingResult.setGainshareCustomer(true);
        }

        pricingResult.setCarrierOrgId(carrierOrgId);
        pricingResult.setDefaultMarginAmt(marginVO.getMinMarginFlatAmount());
        pricingResult.setIncludeBenchmarkAcc(orgPricing.getIncludeBenchmarkAcc().getCode());

        //Step 1: Calculate and set line haul
        setPricingDetails(carrierRateMap, benchmarkProfileVO, orgPricing, ratesCO, marginVO, pricingResult);

        if (ratesCO.getGuaranteedTime() != null) {
            setFinalGuaranteedRates(carrierRateMap, benchmarkProfileVO, orgPricing, marginVO, pricingResult);
        }
        
        pricingResult.setRatingCarrierType(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType());

        if (LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType())) {
            // no benchmarks are handled so far for API accessorials
            buildApiLtlAccessorials(ratesCO, carrierRateMap, pricingResult, marginVO);
        } else if (LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType())) {
            buildLTLLCAccessorials(ratesCO, carrierRateMap, pricingResult, marginVO);

            // add additional accessorials like transaction fee, etc...
            setAccessorialResultByPricingDetail(ratesCO, carrierRateMap, pricingResult, marginVO, benchmarkProfileVO, orgPricing, true);
        } else {
            if (CollectionUtils.isNotEmpty(ratesCO.getAccessorialTypes())
                    || LtlRatingEngineServiceImpl.isImplicitOverDimentionalAccessorial(ratesCO)) {
                setAccessorialResultByPricingDetail(ratesCO, carrierRateMap, pricingResult, marginVO, benchmarkProfileVO, orgPricing, false);
            }

            setAccessorialResultByPricingDetail(ratesCO, carrierRateMap, pricingResult, marginVO, benchmarkProfileVO, orgPricing, true);
        }

        pricingResult.setTotalCarrierCost(pricingResult.getTotalCarrierCost()
                .add(getAccessorialsTotal(pricingResult, LtlPricingAccessorialResult::getCarrierAccessorialCost)));
        pricingResult.setTotalShipperCost(pricingResult.getTotalShipperCost()
                .add(getAccessorialsTotal(pricingResult, LtlPricingAccessorialResult::getShipperAccessorialCost)));
        if (isPositive(pricingResult.getTotalBenchmarkCost())) {
            pricingResult.setTotalBenchmarkCost(pricingResult.getTotalBenchmarkCost()
                    .add(getAccessorialsTotal(pricingResult, LtlPricingAccessorialResult::getBenchmarkAccessorialCost)));
        }

        if (!LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType())) {
            setFuelSurcharge(carrierRateMap, benchmarkProfileVO, orgPricing, marginVO, pricingResult, ratesCO);
        }
        // No need to calculate Benchmark values as these are additional accessorials and Benchmark will not
        // be set to additional accessorials. These should be pass through values.

        pricingResult.setTotalMiles(pricingResult.getShipperTotalMiles());
        if (isLowRevenueMargin(orgPricing, pricingResult)) {
            recalculateShipperCostWithDefaultMargin(pricingResult);
        }

        LOG.info(" ********* Pricing Result *********** " + pricingResult.toString());
        return pricingResult;
    }

    private BigDecimal getAccessorialsTotal(LtlPricingResult pricingResult, Function<LtlPricingAccessorialResult, BigDecimal> mapper) {
        return pricingResult.getAccessorials().stream().map(mapper).filter(it->it!=null).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Determines if the shipper cost(revenue) is not hitting the default margin amount. */
    private boolean isLowRevenueMargin(OrganizationPricingEntity orgPricing, LtlPricingResult pricingResult) {
        return orgPricing.getGainshare().equals(StatusYesNo.NO)
                && PricingType.BUY_SELL != pricingResult.getPricingType()
                && pricingResult.getDefaultMarginAmt() != null
                && pricingResult.getMinLinehaulMarginAmt() == null
                && pricingResult.getTotalShipperCost().compareTo(pricingResult.getTotalCarrierCost().add(pricingResult.getDefaultMarginAmt())) < 0;
    }

    /** Recalculates the shipper costs (revenue) to be cost + defaultMarginAmount.
     * Applies adjusted amount to linehaul shipperCost (revenue), while makes all accessorial & fuel to have revenue==cost */
    private void recalculateShipperCostWithDefaultMargin(LtlPricingResult pricingResult) {
        pricingResult.setShipperFinalLinehaul(pricingResult.getCarrierFinalLinehaul().add(pricingResult.getDefaultMarginAmt()));
        pricingResult.setShipperInitialLinehaul(pricingResult.getShipperFinalLinehaul().multiply(pricingResult.getCarrierInitialLinehaul())
                .divide(pricingResult.getCarrierFinalLinehaul(), 2, BigDecimal.ROUND_HALF_UP));
        pricingResult.setShipperLinehaulDiscount(pricingResult.getShipperInitialLinehaul().subtract(pricingResult.getShipperFinalLinehaul()));

        pricingResult.setShipperFuelSurcharge(pricingResult.getCarrierFuelSurcharge());
        if (pricingResult.getGuaranteed() != null) {
            pricingResult.getGuaranteed().setShipperAccessorialCost(round(pricingResult.getGuaranteed().getCarrierAccessorialCost()));
        }

        if (pricingResult.getGuaranteedAddlInfo() != null) {
            for (LtlPricingGuaranteedResult addlGuaranInfo : pricingResult.getGuaranteedAddlInfo()) {
                addlGuaranInfo.setShipperGuaranteedCost(addlGuaranInfo.getCarrierGuaranteedCost());
            }
        }
        for (LtlPricingAccessorialResult accessorial : pricingResult.getAccessorials()) {
            accessorial.setShipperAccessorialCost(accessorial.getCarrierAccessorialCost());
        }

        pricingResult.setTotalShipperCost(pricingResult.getTotalCarrierCost().add(pricingResult.getDefaultMarginAmt()));
    }

    private void setPricingDetails(CarrierRatingVO carrierRateMap, LtlRatingVO benchmarkProfileVO,
            OrganizationPricingEntity orgPricing, GetOrderRatesCO ratesCO, LtlRatingMarginVO marginVO, LtlPricingResult pricingResult) {
        LtlRatingProfileVO carrierRate = carrierRateMap.getRate().getPricingDetails();
        LtlRatingProfileVO shipperRate = null;
        if (carrierRateMap.getShipperRate() != null) {
            shipperRate = carrierRateMap.getShipperRate().getPricingDetails();
        }

        setPricingBaseInfo(pricingResult, carrierRate);
        
        //set mileage to result
        pricingResult.setCarrierTotalMiles(getMileage(ratesCO, carrierRate));
        pricingResult.setShipperTotalMiles(getMileage(ratesCO, shipperRate != null ? shipperRate : carrierRate));

        calculateCarrierCosts(carrierRate, ratesCO, pricingResult);

        setBenchmarkCosts(benchmarkProfileVO, ratesCO, pricingResult);

        LtlRatingProfileVO rate = getShipperRateForRevenueCalculation(marginVO, shipperRate, carrierRate);
        calculateShipperCosts(rate, marginVO, orgPricing, ratesCO, pricingResult);

        pricingResult.setTotalMiles(pricingResult.getShipperTotalMiles());
        pricingResult.setTotalShipperCost(pricingResult.getShipperFinalLinehaul());
        pricingResult.setTotalBenchmarkCost(ObjectUtils.defaultIfNull(pricingResult.getBenchmarkFinalLinehaul(), BigDecimal.ZERO));

        setSMC3Details(ratesCO, pricingResult, carrierRate.getSmc3Response());

        //Set Carrier Liabilities, if available.
        setLiabilitiesInformation(ratesCO, carrierRate, pricingResult);
    }

    private LtlRatingProfileVO getShipperRateForRevenueCalculation(LtlRatingMarginVO marginVO, LtlRatingProfileVO shipperRate,
            LtlRatingProfileVO carrierRate) {
        PricingType pricingType = carrierRate.getPricingType();
        if (shipperRate != null) {
            return shipperRate;
        } else if ((pricingType == PricingType.BLANKET_CSP || pricingType == PricingType.CSP)
                && carrierRate.getMarginType() != null && carrierRate.getUnitMargin() != null) {
            return carrierRate;
        } else {
            return marginVO.getPricingDetail();
        }
    }

    private void calculateShipperCosts(LtlRatingProfileVO shipperRate, LtlRatingMarginVO marginVO,
            OrganizationPricingEntity orgPricing, GetOrderRatesCO ratesCO, LtlPricingResult pricingResult) {
        BigDecimal shipperLinehaul = BigDecimal.ZERO;
        if (pricingResult.isGainshareCustomer()) {
            // If the Customer account is GainShare account, then we don't need to worry about margin set on
            // carrier profile.
            // We calculate margin differently for Gainshare account.
            BigDecimal accDifference = pricingResult.getBenchmarkFinalLinehaul().subtract(pricingResult.getCarrierFinalLinehaul());
            BigDecimal margin = accDifference.multiply(orgPricing.getGsPlsPct()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            shipperLinehaul = pricingResult.getCarrierFinalLinehaul().add(nonNegative(margin));
        } else if (shipperRate != null) {
            shipperLinehaul = calculateShipperCostsFromPricingProfile(shipperRate, ratesCO, pricingResult);
        } else if (marginVO.getDefaultMarginPercent() != null) {
            // Customer Margin
            shipperLinehaul = AmountCalculator.getCalcRevByDefaultMarginPerc(marginVO.getDefaultMarginPercent(),
                    pricingResult.getCarrierFinalLinehaul());
            pricingResult.setLinehaulMarginPerc(marginVO.getDefaultMarginPercent());
        }
        pricingResult.setShipperFinalLinehaul(shipperLinehaul);
        if (shipperRate == null || shipperRate.getPricingType() != PricingType.BUY_SELL) {
            pricingResult.setShipperInitialLinehaul(shipperLinehaul.multiply(pricingResult.getCarrierInitialLinehaul())
                    .divide(pricingResult.getCarrierFinalLinehaul(), 2, BigDecimal.ROUND_HALF_UP));
        }
        pricingResult.setShipperLinehaulDiscount(pricingResult.getShipperInitialLinehaul().subtract(pricingResult.getShipperFinalLinehaul()));
    }

    private BigDecimal calculateShipperCostsFromPricingProfile(LtlRatingProfileVO shipperRate, GetOrderRatesCO ratesCO,
            LtlPricingResult pricingResult) {
        BigDecimal shipperLinehaul = BigDecimal.ZERO;
        BigDecimal baseRate = pricingResult.getCarrierFinalLinehaul();
        if (shipperRate.getPricingType() == PricingType.BUY_SELL) {
            // For Buy/Sell, we calculate carrierLinehaul differently using Sell record values.
            BigDecimal initialCost = getBasePriceFromSMC3(shipperRate);

            BigDecimal calculatedCost = AmountCalculator.getCalculatedCost(shipperRate.getCostType(), shipperRate.getUnitCost(), initialCost,
                   pricingResult.getShipperTotalMiles(), ratesCO.getTotalWeight(), ratesCO.getTotalPieces());
            baseRate = calculatedCost.max(ObjectUtils.defaultIfNull(shipperRate.getMinCost(), BigDecimal.ZERO));
            shipperLinehaul = baseRate;
            pricingResult.setShipperInitialLinehaul(initialCost);
            pricingResult.setCarrierLinehaulForMargin(baseRate);
        }
        if (shipperRate.getMarginType() != null) {
            shipperLinehaul = AmountCalculator.getCalculatedRevenue(shipperRate.getMarginType(), shipperRate.getUnitMargin(),
                    baseRate, pricingResult.getShipperTotalMiles(), ratesCO.getTotalWeight(), ratesCO.getTotalPieces());

            if (UnitType.MC == shipperRate.getMarginType()) {
                pricingResult.setLinehaulMarginPerc(shipperRate.getUnitMargin());
            }
        }
        if (shipperRate.getMinMarginFlat() != null && shipperLinehaul.compareTo(baseRate.add(shipperRate.getMinMarginFlat())) < 0) {
            shipperLinehaul = baseRate.add(shipperRate.getMinMarginFlat());
        }
        pricingResult.setMinLinehaulMarginAmt(shipperRate.getMinMarginFlat());
        pricingResult.setAppliedLinehaulMarginAmt(shipperLinehaul.subtract(baseRate));
        return shipperLinehaul;
    }
    
    private BigDecimal calculateShipperCostFromPricingProfile(LtlRatingProfileVO shipperRate, GetOrderRatesCO ratesCO, Integer totalMiles, BigDecimal cost) {
        BigDecimal shipperLinehaul = BigDecimal.ZERO;
        if (shipperRate.getPricingType() == PricingType.BUY_SELL) {
            // For Buy/Sell, we calculate carrierLinehaul differently using Sell record values.
            BigDecimal initialCost = getBasePriceFromSMC3(shipperRate);

            BigDecimal calculatedCost = AmountCalculator.getCalculatedCost(shipperRate.getCostType(), shipperRate.getUnitCost(), initialCost,
                   totalMiles, ratesCO.getTotalWeight(), ratesCO.getTotalPieces());
            cost = calculatedCost.max(ObjectUtils.defaultIfNull(shipperRate.getMinCost(), BigDecimal.ZERO));
            shipperLinehaul = cost;
        }
        if (shipperRate.getMarginType() != null) {
            shipperLinehaul = AmountCalculator.getCalculatedRevenue(shipperRate.getMarginType(), shipperRate.getUnitMargin(),
                    cost, totalMiles, ratesCO.getTotalWeight(), ratesCO.getTotalPieces());
        }
        if (shipperRate.getMinMarginFlat() != null && shipperLinehaul.compareTo(cost.add(shipperRate.getMinMarginFlat())) < 0) {
            shipperLinehaul = cost.add(shipperRate.getMinMarginFlat());
        }

        return shipperLinehaul;
    }

    private void setBenchmarkCosts(LtlRatingVO benchmarkProfileVO, GetOrderRatesCO ratesCO, LtlPricingResult pricingResult) {
        CarrierRatingVO benchmarkProfile = getBenchmarkProfile(benchmarkProfileVO, pricingResult.getCarrierOrgId());
        if (benchmarkProfile != null) {
            LtlRatingProfileVO benchmarkPricingProfile = benchmarkProfile.getRate().getPricingDetails();
            pricingResult.setBenchmarkPricingProfileId(benchmarkPricingProfile.getProfileId());
            pricingResult.setBenchmarkFinalLinehaul(getBenchmarkFinalLinehaul(ratesCO, benchmarkPricingProfile));
        }
    }

    private Integer getMileage(GetOrderRatesCO ratesCO, LtlRatingProfileVO rate) {
        return rate == null || MileageCalculatorType.MILE_MAKER.name().equalsIgnoreCase(rate.getMileageType()) ? ratesCO.getMilemakerMiles()
                : ratesCO.getPcmilerMiles();
    }

    private void setPricingBaseInfo(LtlPricingResult pricingResult, LtlRatingProfileVO carrierRate) {
        pricingResult.setProfileId(carrierRate.getProfileId());
        pricingResult.setCarrierOrgId(carrierRate.getCarrierOrgId());
        pricingResult.setPricingType(carrierRate.getPricingType());
        pricingResult.setScac(carrierRate.getScac());
        pricingResult.setCarrierName(carrierRate.getCarrierName());
        pricingResult.setCarrierNote(carrierRate.getNote());
        pricingResult.setProfileDetailId(carrierRate.getProfileDetailId());
        pricingResult.setProhibitedCommodities(carrierRate.getProhibitedCommodities());
        pricingResult.setServiceTypeEnum(carrierRate.getServiceType());
        pricingResult.setHideDetails(carrierRate.getHideDetails());
        pricingResult.setHideTerminalDetails(carrierRate.getHideTerminalDetails());
        pricingResult.setCurrencyCode(carrierRate.getCurrencyCode());
        if (carrierRate.getDayAndRossResponse() != null) {
            pricingResult.setTransitDate(carrierRate.getDayAndRossResponse().getEstimatedDeliveryDate());
            pricingResult.setTransitTime(carrierRate.getDayAndRossResponse().getTransitDays());
        } else if (carrierRate.getLtllcResponse() != null) {
            QuoteResultDTO ltllcResponse = carrierRate.getLtllcResponse();
            pricingResult.setShipmentType(ltllcResponse.getShipmentType());
            if (ltllcResponse.getTransitTime() != null && ltllcResponse.getDeliveryDate() != null) {
                pricingResult.setTransitTime(ltllcResponse.getTransitTime().intValue());
                pricingResult.setTransitDate(Date.from(ltllcResponse.getDeliveryDate().atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                // fall back to CarrierConnect based transit time.
                pricingResult.setTransitTime(carrierRate.getTransitTime());
                pricingResult.setTransitDate(carrierRate.getTransitDate());
            }
            if (ltllcResponse.getServiceType() != null) {
                pricingResult.setServiceTypeEnum(ltllcResponse.getServiceType() == com.pls.ltlrating.integration.ltllifecycle.dto.LtlServiceType.DIRECT ? LtlServiceType.DIRECT : LtlServiceType.INDIRECT);
            }
            pricingResult.setExternalQuoteUuid(ltllcResponse.getUuid());
            pricingResult.setCarrierQuoteNumber(ltllcResponse.getQuoteNumber());
            pricingResult.setServiceLevelCode(ltllcResponse.getServiceLevelCode());
            pricingResult.setServiceLevelDescription(ltllcResponse.getServiceLevelDescription());
        } else {
            pricingResult.setTransitDate(carrierRate.getTransitDate());
            pricingResult.setTransitTime(carrierRate.getTransitTime());
        }
        pricingResult.setTariffName(carrierRate.getSmc3Tariff());
        pricingResult.setCarrierPricingDetailId(carrierRate.getPricingDetailId());
        pricingResult.setMovementType(carrierRate.getMovementType());
        pricingResult.setEffectiveDate(carrierRate.getEffectiveDate());
        pricingResult.setBlockedFrmBkng(carrierRate.getBlockedFrmBkng());
        pricingResult.setIntegrationType(carrierRate.getIntegrationType());
    }

    private BigDecimal getBenchmarkFinalLinehaul(GetOrderRatesCO ratesCO, LtlRatingProfileVO benchmarkPricingProfile) {
        BigDecimal finalLinehaul = BigDecimal.ZERO;
        if (benchmarkPricingProfile.getUnitCost() != null) {
            BigDecimal basePrice = getBasePriceFromSMC3(benchmarkPricingProfile);
            BigDecimal calculatedCost = AmountCalculator.getCalculatedCost(benchmarkPricingProfile.getCostType(),
                    benchmarkPricingProfile.getUnitCost(), basePrice, getMileage(ratesCO, benchmarkPricingProfile), ratesCO.getTotalWeight(),
                    ratesCO.getTotalPieces());
            finalLinehaul = calculatedCost.max(ObjectUtils.defaultIfNull(benchmarkPricingProfile.getMinCost(), BigDecimal.ZERO));
        }
        return finalLinehaul;
    }

    private void setSMC3Details(GetOrderRatesCO ratesCO, LtlPricingResult pricingResult, LTLRateShipmentDTO smc3Rates) {
        if (smc3Rates != null) {
            pricingResult.setCostDetails(smc3Rates.getDetails());
            for (int i = 0; i < pricingResult.getCostDetails().size(); i++) {
                pricingResult.getCostDetails().get(i).setEnteredNmfcClass(ratesCO.getMaterials().get(i).getCommodityClassEnum().getDbCode());
            }
            pricingResult.setTotalChargeFromSmc3(smc3Rates.getTotalChargeFromDetails());
            if (smc3Rates.getDeficitCharge().compareTo(BigDecimal.ZERO) != 0) {
                pricingResult.setDeficitChargeFromSmc3(smc3Rates.getDeficitCharge());
            }
            if (smc3Rates.getTotalChargeFromDetails().add(smc3Rates.getDeficitCharge()).compareTo(smc3Rates.getMinimumCharge()) < 0) {
                pricingResult.setSmc3MinimumCharge(smc3Rates.getMinimumCharge());
            }
        }
    }

    private BigDecimal nonNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0 ? value : BigDecimal.ZERO;
    }

    private void setFuelSurcharge(CarrierRatingVO carrierRateMap, LtlRatingVO benchmarkProfileVO, OrganizationPricingEntity orgPricing,
            LtlRatingMarginVO marginVO, LtlPricingResult pricingResult, GetOrderRatesCO ratesCO) {
        LtlRatingFSTriggerVO carrierFSRate = carrierRateMap.getRate().getFuelSurcharge();
        setCarrierFuelSurcharge(carrierFSRate, pricingResult, carrierRateMap.getRate().getPricingDetails());
        setBenchmarkFuelSurcharge(benchmarkProfileVO, pricingResult);

        if(LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(carrierRateMap.getRate().getPricingDetails().getRatingCarrierType())){
            //apply the accessorial/profile margin here in case of ltllc (and such setting exists)
            Map<String, AccessorialPricingVO> carrAccRates = getAccessorials(carrierRateMap.getRate(), true);
            Map<String, AccessorialPricingVO> shipperAccRates = null;
            if (carrierRateMap.getShipperRate() != null) {
                shipperAccRates = getAccessorials(carrierRateMap.getShipperRate(), true);
            }
            
            AccessorialPricingVO shipperRate = getShipperAccessorialApiRate(carrAccRates, shipperAccRates, "FS");
            if (shipperRate != null) {
                populateBestAccessorialRevenue(pricingResult, pricingResult.getCarrierFuelSurcharge(), shipperRate, ratesCO);
                pricingResult.setShipperFuelSurcharge(shipperRate.getBestCost());
            } else if(carrierRateMap.getRate().getPricingDetails() != null && carrierRateMap.getRate().getPricingDetails().getMarginType() != null) {
                // Apply the pricing margin if available, and no accessorial specific margin specified
                pricingResult.setShipperFuelSurcharge(
                        calculateShipperCostFromPricingProfile(carrierRateMap.getRate().getPricingDetails(), ratesCO, pricingResult.getShipperTotalMiles(), pricingResult.getCarrierFuelSurcharge())
                        );
            } else {
                pricingResult.setShipperFuelSurcharge(getShipperFS(orgPricing, pricingResult, null, marginVO.getDefaultMarginPercent()));
            }
        } else {
            //non-LTLLC rates
            LtlRatingFSTriggerVO shipperRate = getShipperFSRate(marginVO, carrierRateMap.getShipperRate(), carrierFSRate);
            pricingResult.setShipperFuelSurcharge(getShipperFS(orgPricing, pricingResult, shipperRate, marginVO.getDefaultMarginPercent()));
        }
        
        if (pricingResult.getShipperFuelSurcharge() != null) {
            pricingResult.setTotalShipperCost(pricingResult.getTotalShipperCost().add(pricingResult.getShipperFuelSurcharge()));
        }
    }

    private LtlRatingFSTriggerVO getShipperFSRate(LtlRatingMarginVO marginVO, CarrierPricingProfilesVO carrierPricingProfilesVO,
            LtlRatingFSTriggerVO carrierRate) {
        // this null check was added for CARRIER API tariff, but left here just in case
        PricingType pricingType = carrierRate == null ? PricingType.BLANKET : carrierRate.getPricingType();
        if (carrierPricingProfilesVO != null && carrierPricingProfilesVO.getFuelSurcharge() != null) {
            return carrierPricingProfilesVO.getFuelSurcharge();
        } else if (pricingType == PricingType.BLANKET_CSP || pricingType == PricingType.CSP) {
            return carrierRate;
        } else {
            return marginVO.getFuelSurcharge();
        }
    }

    private void setCarrierFuelSurcharge(LtlRatingFSTriggerVO carrierFSRate, LtlPricingResult pricingResult, LtlRatingProfileVO ltlRatingProfileVO) {
        BigDecimal carrierCost = null;
        if (LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(ltlRatingProfileVO.getRatingCarrierType())) {
            BigDecimal carrierFSAmount = null;
            LtlPricingAccessorialResult fsAccessorialResult = ltlRatingProfileVO.getDayAndRossResponse().getCharges().get("FS");
            if (fsAccessorialResult != null && fsAccessorialResult.getCarrierAccessorialCost() != null) {
                carrierFSAmount = fsAccessorialResult.getCarrierAccessorialCost();
            } else {
                carrierFSAmount = BigDecimal.ZERO;
            }
            populateCarrierFS(null, pricingResult, BigDecimal.ZERO, carrierFSAmount);
        } else if(LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(ltlRatingProfileVO.getRatingCarrierType())) {
            populateCarrierFS(null, pricingResult, ltlRatingProfileVO.getLtllcResponse().getFuelSurchargeRate(), ltlRatingProfileVO.getLtllcResponse().getFuelSurcharge());
        } else if (BooleanUtils.isNotTrue(carrierFSRate.getIsExcludeFuel())) {
            carrierCost = getCarrierCostForFuel(pricingResult);
            BigDecimal carrierFSAmount = AmountCalculator.getCalculatedCarrierFSAmount(carrierCost, carrierFSRate.getSurcharge());
            populateCarrierFS(carrierFSRate, pricingResult, carrierFSRate.getSurcharge(), carrierFSAmount);
        }
    }

    private void populateCarrierFS(LtlRatingFSTriggerVO carrierFSRate, LtlPricingResult pricingResult,
            BigDecimal fuelDiscount, BigDecimal carrierFSAmount) {
        if (carrierFSRate != null) {
            pricingResult.setCarrierFSId(carrierFSRate.getLtlFuelSurchargeId());
        }
        pricingResult.setCarrierFuelDiscount(fuelDiscount);
        pricingResult.setCarrierFuelSurcharge(carrierFSAmount);
        pricingResult.setTotalCarrierCost(pricingResult.getTotalCarrierCost().add(carrierFSAmount));
    }

    private void setBenchmarkFuelSurcharge(LtlRatingVO benchmarkProfileVO, LtlPricingResult pricingResult) {
        CarrierRatingVO benchmarkProfile = getBenchmarkProfile(benchmarkProfileVO, pricingResult.getCarrierOrgId());
        if (benchmarkProfile != null) {
            LtlRatingFSTriggerVO fsBenchmarkRate = benchmarkProfile.getRate().getFuelSurcharge();
            if (BooleanUtils.isNotTrue(fsBenchmarkRate.getIsExcludeFuel())) {
                BigDecimal benchmarkCost = getBenchmarkCostForFuel(pricingResult);
                BigDecimal benchmarkFSAmount = AmountCalculator.getCalculatedCarrierFSAmount(benchmarkCost, fsBenchmarkRate.getSurcharge());
                pricingResult.setBenchmarkFuelSurcharge(benchmarkFSAmount);
                if (pricingResult.getTotalBenchmarkCost() != null) {
                    pricingResult.setTotalBenchmarkCost(pricingResult.getTotalBenchmarkCost().add(benchmarkFSAmount));
                }
            }
        }
    }

    private BigDecimal getShipperCostForFuel(LtlPricingResult pricingResult) {
        BigDecimal cost = pricingResult.getCarrierLinehaulForMargin();
        for (LtlPricingAccessorialResult accessorial : pricingResult.getAccessorials()) {
            if (accessorial.isApplyShipperCostBeforeFuel()) {
                cost = cost.add(accessorial.getShipperAccessorialCost());
            }
        }
        if (pricingResult.getGuaranteed() != null && pricingResult.getGuaranteed().isApplyShipperCostBeforeFuel()) {
            cost = cost.add(pricingResult.getGuaranteed().getShipperAccessorialCost());
        }
        return cost;
    }

    private BigDecimal getCarrierCostForFuel(LtlPricingResult pricingResult) {
        BigDecimal cost = pricingResult.getCarrierFinalLinehaul();

        for (LtlPricingAccessorialResult accessorial : pricingResult.getAccessorials()) {
            if (accessorial.isApplyCarrierCostBeforeFuel()) {
                cost = cost.add(accessorial.getCarrierAccessorialCost());
            }
        }
        if (pricingResult.getGuaranteed() != null && pricingResult.getGuaranteed().isApplyCarrierCostBeforeFuel()) {
            cost = cost.add(pricingResult.getGuaranteed().getCarrierAccessorialCost());
        }
        return cost;
    }

    private BigDecimal getBenchmarkCostForFuel(LtlPricingResult pricingResult) {
        BigDecimal cost = pricingResult.getBenchmarkFinalLinehaul();
        for (LtlPricingAccessorialResult accessorial : pricingResult.getAccessorials()) {
            if (accessorial.isApplyBenchmarkCostBeforeFuel()) {
                cost = cost.add(accessorial.getBenchmarkAccessorialCost());
            }
        }
        if (pricingResult.getGuaranteed() != null && pricingResult.getGuaranteed().isApplyBenchmarkCostBeforeFuel()) {
            cost = cost.add(pricingResult.getGuaranteed().getBenchmarkAccessorialCost());
        }
        return cost;
    }

    private BigDecimal getShipperFS(OrganizationPricingEntity orgPricing, LtlPricingResult pricingResult, LtlRatingFSTriggerVO shipperRate,
            BigDecimal defaultMarginPercent) {
        //If the Customer account is GainShare account, then we don't need to worry about margin set on carrier profile.
        //We calculate margin differently for Gainshare account.
        if (pricingResult.isGainshareCustomer()) {
            if (pricingResult.getBenchmarkFuelSurcharge() != null) {
                BigDecimal carrierFuelSurcharge = ObjectUtils.defaultIfNull(pricingResult.getCarrierFuelSurcharge(), BigDecimal.ZERO);
                if (orgPricing.getGsPlsPct() != null) {
                    BigDecimal fsDifference = pricingResult.getBenchmarkFuelSurcharge().subtract(carrierFuelSurcharge);
                    BigDecimal margin = fsDifference.multiply(orgPricing.getGsPlsPct()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    return carrierFuelSurcharge.add(nonNegative(margin));
                }
                return carrierFuelSurcharge;
            }
        } else if (shipperRate != null) {
            // Buy/Sell or Margin or CSP or Blanket/CSP profile
            if (BooleanUtils.isNotTrue(shipperRate.getIsExcludeFuel())) {
                BigDecimal fuelSurcharge = pricingResult.getCarrierFuelSurcharge();
                if (shipperRate.getPricingType() == PricingType.BUY_SELL) {
                    BigDecimal shipperCostForFuel = getShipperCostForFuel(pricingResult);
                    fuelSurcharge = AmountCalculator.getCalculatedCarrierFSAmount(shipperCostForFuel, shipperRate.getSurcharge());
                }
                return AmountCalculator.getCalculatedShipperFSAmount(fuelSurcharge, shipperRate.getUpchargeType(), shipperRate.getUpchargeFlat(),
                        shipperRate.getUpchargePercent());
            }
        } else if (defaultMarginPercent != null) {
            if (pricingResult.getCarrierFuelSurcharge() != null) {
                // Customer Margin
                return AmountCalculator.getCalcRevByDefaultMarginPerc(defaultMarginPercent, pricingResult.getCarrierFuelSurcharge());
            }
        } else {
            return pricingResult.getCarrierFuelSurcharge();
        }
        return null;
    }

    private CarrierRatingVO getBenchmarkProfile(LtlRatingVO benchmarkProfileVO, Long carrierOrgId) {
        if (benchmarkProfileVO != null) {
            List<CarrierRatingVO> details = benchmarkProfileVO.getCarrierPricingDetails().get(carrierOrgId);
            if (details == null) {
                details = benchmarkProfileVO.getCarrierPricingDetails().get(-1L);
            }
            if (CollectionUtils.isNotEmpty(details)) {
                return details.get(0); // no problem, there is definitely only one (Benchmark) profile in the list
            }
        }
        return null;
    }

    private void setFinalGuaranteedRates(CarrierRatingVO carrierRateMap, LtlRatingVO benchmarkProfileVO, OrganizationPricingEntity orgPricing,
            LtlRatingMarginVO marginVO, LtlPricingResult pricingResult) {

        Map<Integer, LtlRatingGuaranteedVO> benchmarkGuaranRateMap = getGuaranteedBenchmarkRates(benchmarkProfileVO, orgPricing, pricingResult);

        Map<Integer, LtlRatingGuaranteedVO> guaranCarrierRates = carrierRateMap.getRate().getGuaranteedPricing();
        Map<Integer, LtlRatingGuaranteedVO> guaranShipperRates = null;
        if (carrierRateMap.getShipperRate() != null) {
            guaranShipperRates = carrierRateMap.getShipperRate().getGuaranteedPricing();
        }
        List<LtlPricingGuaranteedResult> guaranteedAddlResults = new ArrayList<LtlPricingGuaranteedResult>();
        //Now since the first one is the exact/closest match, show that one the screen
        //and then get other ones and place them in the list, so that they can be shown on tooltip.
        for (LtlRatingGuaranteedVO carrierRate : guaranCarrierRates.values()) {
            BigDecimal benchmarkGuaranteedCost = BigDecimal.ZERO;
            //Calculate and Set benchmark guaranteed price if available
            LtlRatingGuaranteedVO benchmarkRate = null;
            if (benchmarkGuaranRateMap != null && benchmarkGuaranRateMap.containsKey(carrierRate.getTime())) {
                benchmarkRate = benchmarkGuaranRateMap.get(carrierRate.getTime());
                benchmarkGuaranteedCost = getGuaranteedPrice(benchmarkRate, pricingResult.getBenchmarkFinalLinehaul());
            }

            BigDecimal carrierGuaranteedCost = getGuaranteedPrice(carrierRate, pricingResult.getCarrierFinalLinehaul());

            LtlRatingGuaranteedVO shipperRate = getShipperGuaranteedRate(marginVO, guaranShipperRates, carrierRate,
                    carrierRateMap.getPricingType());
            BigDecimal shipperGuaranteedCost = getShipperGuaranteedPrice(orgPricing, marginVO, pricingResult, shipperRate, benchmarkGuaranteedCost,
                    carrierGuaranteedCost);

            // Check if the first one is not set, then set it.
            if (pricingResult.getGuaranteedTime() == null) {
                setPricingGuaranteedResult(pricingResult, carrierRate, benchmarkGuaranteedCost, carrierGuaranteedCost, shipperGuaranteedCost);
                setGuaranteedApplyBeforeFuelFlags(carrierRate, benchmarkRate, shipperRate, pricingResult.getGuaranteed());
            } else {
                guaranteedAddlResults.add(getAdditionalGuaranteedResult(carrierRate, benchmarkGuaranteedCost, carrierGuaranteedCost,
                        shipperGuaranteedCost, pricingResult.getBenchmarkFinalLinehaul()));
            }
        }
        pricingResult.setGuaranteedAddlInfo(guaranteedAddlResults);
    }

    private void setGuaranteedApplyBeforeFuelFlags(LtlRatingGuaranteedVO carrierRate, LtlRatingGuaranteedVO benchmarkRate,
            LtlRatingGuaranteedVO shipperRate, LtlPricingAccessorialResult guaranteed) {
        guaranteed.setApplyCarrierCostBeforeFuel(YES.equals(carrierRate.getApplyBeforeFuel()));
        if (shipperRate != null) {
            guaranteed.setApplyShipperCostBeforeFuel(YES.equals(shipperRate.getApplyBeforeFuel()));
        } else if (guaranteed.isApplyCarrierCostBeforeFuel()) {
            guaranteed.setApplyShipperCostBeforeFuel(true);
        }
        if (benchmarkRate != null) {
            guaranteed.setApplyBenchmarkCostBeforeFuel(YES.equals(benchmarkRate.getApplyBeforeFuel()));
        }
    }

    private BigDecimal getGuaranteedPrice(LtlRatingGuaranteedVO guaranteedRate, BigDecimal finalLinehaul) {
        return AmountCalculator.getCalculatedGuaranteedAmount(guaranteedRate.getChargeRuleType(), guaranteedRate.getUnitCost(), finalLinehaul,
                guaranteedRate.getMinCost(), guaranteedRate.getMaxCost());
    }

    private LtlPricingGuaranteedResult getAdditionalGuaranteedResult(LtlRatingGuaranteedVO guaranteedRate, BigDecimal benchmarkGuaranteedPrice,
            BigDecimal carrierGuaranteedPrice, BigDecimal shipperGuaranteedPrice, BigDecimal benchmarkFinalLinehaul) {
        LtlPricingGuaranteedResult guaranAddlResult = new LtlPricingGuaranteedResult();
        guaranAddlResult.setGuaranteedTime(guaranteedRate.getTime());
        guaranAddlResult.setCarrierGuaranteedCost(carrierGuaranteedPrice);
        guaranAddlResult.setShipperGuaranteedCost(shipperGuaranteedPrice);
        if (isPositive(benchmarkFinalLinehaul)) {
            if (isPositive(benchmarkGuaranteedPrice)) {
                guaranAddlResult.setBenchmarkGuaranteedCost(benchmarkGuaranteedPrice);
            } else {
                guaranAddlResult.setBenchmarkGuaranteedCost(guaranAddlResult.getShipperGuaranteedCost());
            }
        }
        return guaranAddlResult;
    }

    private Map<Integer, LtlRatingGuaranteedVO> getGuaranteedBenchmarkRates(LtlRatingVO benchmarkProfileVO,
            OrganizationPricingEntity orgPricing, LtlPricingResult pricingResult) {
        Map<Integer, LtlRatingGuaranteedVO> benchmarkGuaranRateMap = null;
        if (orgPricing.getIncludeBenchmarkAcc() == StatusYesNo.YES) {
            CarrierRatingVO benchmarkProfile = getBenchmarkProfile(benchmarkProfileVO, pricingResult.getCarrierOrgId());
            if (benchmarkProfile != null) {
                benchmarkGuaranRateMap = benchmarkProfile.getRate().getGuaranteedPricing();
            }
        }
        return benchmarkGuaranRateMap;
    }

    private void setPricingGuaranteedResult(LtlPricingResult pricingResult, LtlRatingGuaranteedVO guaranteedRate, BigDecimal benchmarkGuaranteedPrice,
            BigDecimal carrierGuaranteedPrice, BigDecimal shipperGuaranteedPrice) {
        pricingResult.setGuaranteedTime(guaranteedRate.getTime());
        pricingResult.setBolCarrierName(guaranteedRate.getBolCarrierName());
        pricingResult.setGuaranteed(new LtlPricingAccessorialResult());
        pricingResult.getGuaranteed().setCarrierAccessorialCost(round(carrierGuaranteedPrice));
        pricingResult.getGuaranteed().setShipperAccessorialCost(round(shipperGuaranteedPrice));
        if (isPositive(pricingResult.getBenchmarkFinalLinehaul())) {
            if (isPositive(benchmarkGuaranteedPrice)) {
                pricingResult.getGuaranteed().setBenchmarkAccessorialCost(benchmarkGuaranteedPrice);
            } else {
                pricingResult.getGuaranteed().setBenchmarkAccessorialCost(shipperGuaranteedPrice);
            }
        }

        pricingResult.setTotalCarrierCost(pricingResult.getTotalCarrierCost().add(carrierGuaranteedPrice));
        pricingResult.setTotalShipperCost(pricingResult.getTotalShipperCost().add(shipperGuaranteedPrice));
        if (isPositive(pricingResult.getTotalBenchmarkCost()) && isPositive(pricingResult.getGuaranteed().getBenchmarkAccessorialCost())) {
            pricingResult.setTotalBenchmarkCost(
                    pricingResult.getTotalBenchmarkCost().add(pricingResult.getGuaranteed().getBenchmarkAccessorialCost()));
        }
    }

    private LtlRatingGuaranteedVO getShipperGuaranteedRate(LtlRatingMarginVO marginVO, Map<Integer, LtlRatingGuaranteedVO> guaranShipperRates,
            LtlRatingGuaranteedVO carrierRate, PricingType pricingType) {
        LtlRatingGuaranteedVO shipperRate = null;
        if (guaranShipperRates != null) {
            shipperRate = guaranShipperRates.get(carrierRate.getTime());
        } else if ((pricingType == PricingType.BLANKET_CSP || pricingType == PricingType.CSP)
                && carrierRate.getChargeRuleType() != null && carrierRate.getUnitMargin() != null) {
            shipperRate = carrierRate;
        } else if (marginVO != null) {
            shipperRate = marginVO.getGuaranteed();
        }
        return shipperRate;
    }

    private BigDecimal getShipperGuaranteedPrice(OrganizationPricingEntity orgPricing, LtlRatingMarginVO marginVO, LtlPricingResult pricingResult,
            LtlRatingGuaranteedVO shipperRate, BigDecimal benchmarkGuaranteedPrice, BigDecimal carrierGuaranteedPrice) {
        BigDecimal shipperGuaranteedPrice;
        if (pricingResult.isGainshareCustomer()) {
            BigDecimal guaranteedDifference = benchmarkGuaranteedPrice.subtract(carrierGuaranteedPrice);
            BigDecimal margin = guaranteedDifference.multiply(orgPricing.getGsPlsPct()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            shipperGuaranteedPrice = carrierGuaranteedPrice.add(nonNegative(margin));
        } else if (shipperRate != null) {
            // Buy/Sell or Margin or CSP or Blanket/CSP profile
            BigDecimal cost = carrierGuaranteedPrice;
            if (shipperRate.getPricingType() == PricingType.BUY_SELL) {
                // for Buy/Sell pricing we should first calculate Sell Rate Cost and then apply margin on it
                cost = getGuaranteedPrice(shipperRate, pricingResult.getCarrierLinehaulForMargin());
            }
            shipperGuaranteedPrice = cost.add(AmountCalculator.getCalculatedGuaranteedAmount(shipperRate.getChargeRuleType(),
                    shipperRate.getUnitMargin(), cost, shipperRate.getMinMarginFlat(), null));
        } else if (marginVO.getDefaultMarginPercent() != null) {
            // Customer Margin
            shipperGuaranteedPrice = AmountCalculator.getCalcRevByDefaultMarginPerc(marginVO.getDefaultMarginPercent(), carrierGuaranteedPrice);
        } else {
            shipperGuaranteedPrice = carrierGuaranteedPrice;
        }
        return shipperGuaranteedPrice;
    }
    
    private void buildLTLLCAccessorials(GetOrderRatesCO ratesCO, CarrierRatingVO carrierRateMap, LtlPricingResult pricingResult,
            LtlRatingMarginVO marginVO) {
    
        // we need both ltl and additional accessorials here - as we need to apply margin to both types
        Map<String, AccessorialPricingVO> carrAccRates = getAccessorials(carrierRateMap.getRate(), true);
        carrAccRates.putAll(getAccessorials(carrierRateMap.getRate(), false));
        
        Map<String, AccessorialPricingVO> shipperAccRates = null;
        if (carrierRateMap.getShipperRate() != null) {
            shipperAccRates = getAccessorials(carrierRateMap.getShipperRate(), true);
            shipperAccRates.putAll(getAccessorials(carrierRateMap.getShipperRate(), false));
        }
        
        for(AccessorialChargeDTO accCharge : carrierRateMap.getRate().getPricingDetails().getLtllcResponse().getAccessorialCharges()) {
            String accType = accCharge.getAccessorialCode();
            
            LtlPricingAccessorialResult accResult = new LtlPricingAccessorialResult();
            accResult.setAccessorialType(accType);
            accResult.setCarrierAccessorialCost(accCharge.getCost());
            accResult.setApplyCarrierCostBeforeFuel(false);
            accResult.setApplyShipperCostBeforeFuel(false);
            accResult.setAccessorialDescription(accCharge.getName());
            
            AccessorialPricingVO shipperRate = getShipperAccessorialApiRate(carrAccRates, shipperAccRates, accType);
            if (shipperRate != null) {
                populateBestAccessorialRevenue(pricingResult, accResult.getCarrierAccessorialCost(), shipperRate, ratesCO);
                accResult.setShipperAccessorialCost(shipperRate.getBestCost());
            } else if(carrierRateMap.getRate().getPricingDetails() != null && carrierRateMap.getRate().getPricingDetails().getMarginType() != null) {
                // Apply the pricing margin if available, and no accessorial specific margin specified
                BigDecimal shipperCost = calculateShipperCostFromPricingProfile(carrierRateMap.getRate().getPricingDetails(), ratesCO, pricingResult.getShipperTotalMiles(), accResult.getCarrierAccessorialCost());
                accResult.setShipperAccessorialCost(shipperCost);
            } else if (marginVO.getDefaultMarginPercent() != null) {
                // Customer Margin
                accResult.setShipperAccessorialCost(
                        AmountCalculator.getCalcRevByDefaultMarginPerc(marginVO.getDefaultMarginPercent(), accResult.getCarrierAccessorialCost()));
            } else {
                accResult.setShipperAccessorialCost(accResult.getCarrierAccessorialCost());
            }
            accResult.setBenchmarkAccessorialCost(accResult.getShipperAccessorialCost());
            
            if(ratesCO.getGuaranteedTime() != null && accType.equals("GD")) {
                pricingResult.setGuaranteed(accResult); // guaranteed needs to go to separate property.
                pricingResult.setGuaranteedTime(ratesCO.getGuaranteedTime());
                pricingResult.setBolCarrierName(carrierRateMap.getRate().getPricingDetails().getLtllcResponse().getServiceLevelDescription());
            } else {
                pricingResult.getAccessorials().add(accResult);
            }
        }
    }

    private void buildApiLtlAccessorials(GetOrderRatesCO ratesCO, CarrierRatingVO carrierRateMap, LtlPricingResult pricingResult,
            LtlRatingMarginVO marginVO) {
        LTLDayAndRossRateBO dayAndRossResponse = carrierRateMap.getRate().getPricingDetails().getDayAndRossResponse();
        Map<String, AccessorialPricingVO> carrAccRates = getAccessorials(carrierRateMap.getRate(), false);
        Map<String, AccessorialPricingVO> shipperAccRates = null;
        if (carrierRateMap.getShipperRate() != null) {
            shipperAccRates = getAccessorials(carrierRateMap.getShipperRate(), false);
        }

        for (Entry<String, LtlPricingAccessorialResult> accessorial : dayAndRossResponse.getCharges().entrySet()) {
            String accType = accessorial.getKey();
            LtlPricingAccessorialResult accResult = accessorial.getValue();

            AccessorialPricingVO shipperRate = getShipperAccessorialApiRate(carrAccRates, shipperAccRates, accType);
            if (shipperRate != null) {
                populateBestAccessorialRevenue(pricingResult, accResult.getCarrierAccessorialCost(), shipperRate, ratesCO);
                accResult.setShipperAccessorialCost(shipperRate.getBestCost());
            } else if (marginVO.getDefaultMarginPercent() != null) {
                // Customer Margin
                accResult.setShipperAccessorialCost(
                        AmountCalculator.getCalcRevByDefaultMarginPerc(marginVO.getDefaultMarginPercent(), accResult.getCarrierAccessorialCost()));
            } else {
                accResult.setShipperAccessorialCost(accResult.getCarrierAccessorialCost());
            }
            
            pricingResult.getAccessorials().add(accResult);
        }
    }

    private AccessorialPricingVO getShipperAccessorialApiRate(Map<String, AccessorialPricingVO> carrAccRates,
            Map<String, AccessorialPricingVO> shipperAccRates, String accType) {
        AccessorialPricingVO shipperRate = null;
        if (shipperAccRates != null && shipperAccRates.containsKey(accType)) {
            shipperRate = shipperAccRates.get(accType);
        }
        if (shipperRate == null && carrAccRates.containsKey(accType)) {
            AccessorialPricingVO carrierRate = carrAccRates.get(accType);
            shipperRate = getShipperAccessorialCSPRate(carrierRate);
        }
        return shipperRate;
    }

    private AccessorialPricingVO getShipperAccessorialCSPRate(AccessorialPricingVO carrierRate) {
        if (carrierRate.getPrices().get(0).getPricingType() == PricingType.CSP
                && carrierRate.getPrices().stream().anyMatch(rate -> rate.getMarginType() != null && rate.getUnitMargin() != null)) {
            AccessorialPricingVO shipperRate = new AccessorialPricingVO();
            shipperRate.getPrices().addAll(carrierRate.getPrices().stream()
                    .filter(rate -> rate.getMarginType() != null && rate.getUnitMargin() != null).collect(Collectors.toList()));
            return shipperRate;
        }
        return null;
    }

    private void setAccessorialResultByPricingDetail(GetOrderRatesCO ratesCO, CarrierRatingVO carrierRateMap, LtlPricingResult pricingResult,
            LtlRatingMarginVO marginVO, LtlRatingVO benchmarkProfileVO, OrganizationPricingEntity orgPricing, boolean addlAccessorial) {
        Map<String, AccessorialPricingVO> carrAccRates = getAccessorials(carrierRateMap.getRate(), addlAccessorial);
        Map<String, AccessorialPricingVO> shipperAccRates = null;
        if (carrierRateMap.getShipperRate() != null) {
            shipperAccRates = getAccessorials(carrierRateMap.getShipperRate(), addlAccessorial);
        }

        Map<String, AccessorialPricingVO> benchmarkAccRateMap = getBenchmarkAccessorialRates(benchmarkProfileVO,
                pricingResult.getCarrierOrgId(), orgPricing, addlAccessorial);

        for (AccessorialPricingVO carrierRate : carrAccRates.values()) {
            // Now calculate carrier accessorial cost.
            populateBestAccessorialCost(ratesCO, pricingResult, carrierRate);

            AccessorialPricingVO benchmarkRate = getBenchmarkAccessorialRate(orgPricing, benchmarkAccRateMap,
                    carrierRate.getBestPrice().getAccessorialType());
            BigDecimal benchmarkCost = BigDecimal.ZERO;
            if (benchmarkRate != null) {
                populateBestBenchmarkAccessorialCost(ratesCO, pricingResult, benchmarkRate, carrierRate.getBestPrice());
                benchmarkCost = benchmarkRate.getBestCost();
            }

            AccessorialPricingVO shipperRate = getShipperAccessorialRate(marginVO, shipperAccRates, carrierRate);
            BigDecimal shipperCost = getAccessorialRevenue(pricingResult, benchmarkCost, carrierRate.getBestCost(), orgPricing, shipperRate,
                    ratesCO, marginVO);

            LtlPricingAccessorialResult accResult = getAccessorialResult(carrierRate);
            setShipperAccessorialResults(shipperRate, shipperCost, accResult);
            setBenchmarkAccessorialResults(pricingResult, benchmarkRate, benchmarkCost, accResult);
            if (isValidAccessorial(ratesCO, carrierRate, shipperCost, accResult)) {
                pricingResult.getAccessorials().add(accResult);
            }
        }
    }

    private void setBenchmarkAccessorialResults(LtlPricingResult pricingResult, AccessorialPricingVO benchmarkRate, BigDecimal benchmarkCost,
            LtlPricingAccessorialResult accResult) {
        if (isPositive(pricingResult.getBenchmarkFinalLinehaul())) {
            if (isPositive(benchmarkCost)) {
                accResult.setBenchmarkAccessorialCost(benchmarkCost);
                accResult.setApplyBenchmarkCostBeforeFuel(YES.equals(benchmarkRate.getBestPrice().getApplyBeforeFuel()));
            } else {
                accResult.setBenchmarkAccessorialCost(accResult.getShipperAccessorialCost());
            }
        }
    }

    private void setShipperAccessorialResults(AccessorialPricingVO shipperRate, BigDecimal shipperCost, LtlPricingAccessorialResult accResult) {
        accResult.setShipperAccessorialCost(round(shipperCost));
        if (shipperRate != null) {
            accResult.setApplyShipperCostBeforeFuel(YES.equals(shipperRate.getBestPrice().getApplyBeforeFuel()));
        } else if (accResult.isApplyCarrierCostBeforeFuel()) {
            accResult.setApplyShipperCostBeforeFuel(true);
        }
    }

    private AccessorialPricingVO getShipperAccessorialRate(LtlRatingMarginVO marginVO, Map<String, AccessorialPricingVO> shipperAccRates,
            AccessorialPricingVO carrierRate) {
        String accessorialType = carrierRate.getPrices().get(0).getAccessorialType();
        PricingType pricingType = carrierRate.getPrices().get(0).getPricingType();
        AccessorialPricingVO shipperRate = null;
        if (shipperAccRates != null) {
            shipperRate = shipperAccRates.get(accessorialType);
        } else if ((pricingType == PricingType.BLANKET_CSP || pricingType == PricingType.CSP)
                && carrierRate.getPrices().stream().anyMatch(rate -> rate.getMarginType() != null && rate.getUnitMargin() != null)) {
            shipperRate = new AccessorialPricingVO();
            shipperRate.getPrices().addAll(carrierRate.getPrices().stream()
                    .filter(rate -> rate.getMarginType() != null && rate.getUnitMargin() != null).collect(Collectors.toList()));
        } else if (marginVO != null) {
            shipperRate = marginVO.getAccessorials().get(accessorialType);
        }
        return shipperRate;
    }

    private BigDecimal getAccessorialRevenue(LtlPricingResult pricingResult, BigDecimal benchmarkCost, BigDecimal carrierCost,
            OrganizationPricingEntity orgPricing, AccessorialPricingVO shipperRate, GetOrderRatesCO ratesCO, LtlRatingMarginVO marginVO) {
        BigDecimal accRevenue;
        if (pricingResult.isGainshareCustomer()) {
            // If the Customer account is GainShare account, then we don't need to worry about margin set on carrier profile.
            // We calculate margin differently for Gainshare account.
            BigDecimal accDifference = benchmarkCost.subtract(carrierCost);
            BigDecimal margin = accDifference.multiply(orgPricing.getGsPlsPct()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            accRevenue = carrierCost.add(nonNegative(margin));
        } else if (shipperRate != null) {
            // Buy/Sell or Margin or CSP or Blanket/CSP profile
            populateBestAccessorialRevenue(pricingResult, carrierCost, shipperRate, ratesCO);
            accRevenue = shipperRate.getBestCost();
        } else if (marginVO.getDefaultMarginPercent() != null) {
            // Customer Margin
            accRevenue = AmountCalculator.getCalcRevByDefaultMarginPerc(marginVO.getDefaultMarginPercent(), carrierCost);
        } else {
            accRevenue = carrierCost;
        }
        return accRevenue;
    }

    private void populateBestAccessorialRevenue(LtlPricingResult pricingResult, BigDecimal carrierCost, AccessorialPricingVO shipperRate,
            GetOrderRatesCO ratesCO) {
        for (LtlRatingAccessorialsVO rate : shipperRate.getPrices()) {
            BigDecimal cost = carrierCost;
            if (rate.getPricingType() == PricingType.BUY_SELL) {
                // for Buy/Sell pricing we should first calculate Sell Rate Cost and then apply margin on it
                cost = AmountCalculator.getAccCalculatedCost(rate.getCostType(), rate.getUnitCost(), pricingResult.getCarrierLinehaulForMargin(),
                        pricingResult.getShipperTotalMiles(), rate.getMinCost(), rate.getMaxCost(), rate.getMinLength(), rate.getMinWidth(), ratesCO,
                        rate.getAccessorialType());
            }
            BigDecimal accRevenue = AmountCalculator.getCalculatedRevenue(rate.getMarginType(), rate.getUnitMargin(), cost,
                    pricingResult.getShipperTotalMiles(), ratesCO.getTotalWeight(),
                    AmountCalculator.getAccessorialCostMultiplier(rate.getAccessorialType(), ratesCO));
            if (rate.getMinMarginFlat() != null) {
                accRevenue = accRevenue.max(rate.getMinMarginFlat().add(carrierCost));
            }
            if (shipperRate.getBestCost() == null || shipperRate.getBestCost().compareTo(accRevenue) < 0) {
                shipperRate.setBestCost(accRevenue);
                shipperRate.setBestPrice(rate);
            }
        }
    }

    private void populateBestAccessorialCost(GetOrderRatesCO ratesCO, LtlPricingResult pricingResult, AccessorialPricingVO carrierRate) {
        for (LtlRatingAccessorialsVO price : carrierRate.getPrices()) {
            BigDecimal carrierCost = AmountCalculator.getAccCalculatedCost(price.getCostType(), price.getUnitCost(),
                    pricingResult.getCarrierFinalLinehaul(), pricingResult.getCarrierTotalMiles(), price.getMinCost(), price.getMaxCost(),
                    price.getMinLength(), price.getMinWidth(), ratesCO, price.getAccessorialType());
            if (carrierRate.getBestCost() == null || carrierRate.getBestCost().compareTo(carrierCost) < 0) {
                carrierRate.setBestCost(carrierCost);
                carrierRate.setBestPrice(price);
            }
        }
    }

    private Map<String, AccessorialPricingVO> getAccessorials(CarrierPricingProfilesVO rate, boolean addlAccessorial) {
        return addlAccessorial ? rate.getAddlAccessorials() : rate.getLtlAccessorials();
    }

    private boolean isPositive(BigDecimal number) {
        return number != null && number.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isNotZero(BigDecimal cost) {
        return cost != null && cost.compareTo(BigDecimal.ZERO) != 0;
    }

    private boolean isValidAccessorial(GetOrderRatesCO ratesCO, AccessorialPricingVO carrierRate,
            BigDecimal shipperCost, LtlPricingAccessorialResult accResult) {
        return isNotZero(carrierRate.getBestCost()) || isNotZero(shipperCost) || isNotZero(accResult.getBenchmarkAccessorialCost())
                || !LtlRatingEngineServiceImpl.isImplicitOverDimentionalAccessorial(ratesCO)
                || !LtlAccessorialType.OVER_DIMENSION.getCode().equals(carrierRate.getBestPrice().getAccessorialType());
    }

    private AccessorialPricingVO getBenchmarkAccessorialRate(OrganizationPricingEntity orgPricing,
            Map<String, AccessorialPricingVO> benchmarkAccRateMap, String accessorialType) {
        if (orgPricing.getIncludeBenchmarkAcc() == StatusYesNo.YES && benchmarkAccRateMap != null) {
            return benchmarkAccRateMap.get(accessorialType);
        }
        return null;
    }

    private void populateBestBenchmarkAccessorialCost(GetOrderRatesCO ratesCO, LtlPricingResult pricingResult, AccessorialPricingVO benchmarkRate,
            LtlRatingAccessorialsVO carrierRate) {
        for (LtlRatingAccessorialsVO rate : benchmarkRate.getPrices()) {
            BigDecimal cost = AmountCalculator.getAccCalculatedCost(rate.getCostType(), rate.getUnitCost(),
                    pricingResult.getBenchmarkFinalLinehaul(), pricingResult.getCarrierTotalMiles(), carrierRate.getMinCost(),
                    carrierRate.getMaxCost(), carrierRate.getMinLength(), rate.getMinWidth(), ratesCO, rate.getAccessorialType());
            if (benchmarkRate.getBestCost() == null || benchmarkRate.getBestCost().compareTo(cost) < 0) {
                benchmarkRate.setBestCost(cost);
                benchmarkRate.setBestPrice(rate);
            }
        }
    }

    private LtlPricingAccessorialResult getAccessorialResult(AccessorialPricingVO carrierRate) {
        LtlPricingAccessorialResult accResult = new LtlPricingAccessorialResult();
        accResult.setAccessorialType(carrierRate.getBestPrice().getAccessorialType());
        accResult.setAccessorialDescription(carrierRate.getBestPrice().getDescription());
        accResult.setAccessorialGroup(carrierRate.getBestPrice().getAccessorialGroup());
        accResult.setCarrierAccessorialCost(round(carrierRate.getBestCost()));
        accResult.setApplyCarrierCostBeforeFuel(YES.equals(carrierRate.getBestPrice().getApplyBeforeFuel()));
        return accResult;
    }

    private Map<String, AccessorialPricingVO> getBenchmarkAccessorialRates(LtlRatingVO benchmarkProfileVO, Long carrierOrgId,
            OrganizationPricingEntity orgPricing, boolean addlAccessorial) {
        Map<String, AccessorialPricingVO> benchmarkAccRateMap = null;
        if (orgPricing.getIncludeBenchmarkAcc() == StatusYesNo.YES) {
            CarrierRatingVO benchmarkProfile = getBenchmarkProfile(benchmarkProfileVO, carrierOrgId);
            if (benchmarkProfile != null) {
                Map<String, AccessorialPricingVO> benchmarkAccRateMapCO = addlAccessorial ? benchmarkProfile
                        .getRate().getAddlAccessorials() : benchmarkProfile.getRate().getLtlAccessorials();
                // Iterate Benchmark Accessorial Rate Map
                if (!benchmarkAccRateMapCO.isEmpty()) {
                    benchmarkAccRateMap = benchmarkAccRateMapCO;
                }
            }
        }
        return benchmarkAccRateMap;
    }

    private void calculateCarrierCosts(LtlRatingProfileVO pricingProfile, GetOrderRatesCO ratesCO, LtlPricingResult pricingResult) {
        if (LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(pricingProfile.getRatingCarrierType())) {
            BigDecimal initialCost = pricingProfile.getDayAndRossResponse().getInitialCost();
            pricingResult.setCarrierInitialLinehaul(round(initialCost));
            pricingResult.setCarrierFinalLinehaul(pricingResult.getCarrierInitialLinehaul());
            pricingResult.setTotalCarrierCost(pricingResult.getCarrierInitialLinehaul());
            pricingResult.setCarrierLinehaulDiscount(BigDecimal.ZERO);
        } else if(LtlRatingEngineServiceImpl.RATING_TYPE_LTLLC.equals(pricingProfile.getRatingCarrierType())) {
            QuoteResultDTO ltllcResponse = pricingProfile.getLtllcResponse();
            
            // Calculate discount using LTLLC provided discount + profile's discount
            BigDecimal initialCost = ltllcResponse.getInitialCost(); // the cost we get from LTLLC
            BigDecimal ltllcBaseRate = initialCost.subtract(ltllcResponse.getDiscount()); // rate after LTTLC Discount applied
            BigDecimal finalBaseRate = pricingProfile.getCostType() == null ? 
                    ltllcBaseRate : 
                    AmountCalculator.getCalculatedCost(pricingProfile.getCostType(), pricingProfile.getUnitCost(), ltllcBaseRate,
                            pricingResult.getCarrierTotalMiles(), ratesCO.getTotalWeight(), ratesCO.getTotalPieces()); // rate after PLS Discount applied
            
            // make sure minimum cost is met
            finalBaseRate = finalBaseRate.max(ObjectUtils.defaultIfNull(pricingProfile.getMinCost(), BigDecimal.ZERO));
            
            // recalculate discount value
            BigDecimal totalDiscount = ltllcResponse.getDiscount().add(ltllcBaseRate.subtract(finalBaseRate));
            BigDecimal totalDiscountPercentage = initialCost.compareTo(BigDecimal.ZERO)==0 ? BigDecimal.ZERO :
                totalDiscount.divide(initialCost, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            
            pricingResult.setCarrierInitialLinehaul(round(initialCost));
            pricingResult.setCarrierFinalLinehaul(round(finalBaseRate));
            pricingResult.setTotalCarrierCost(pricingResult.getCarrierFinalLinehaul());
            pricingResult.setCarrierLinehaulDiscount(round(totalDiscount));
            
            // we need to set these 3 to get them displayed correctly in details window
            pricingResult.setTotalChargeFromSmc3(round(initialCost)); // Total field
            pricingResult.setCostDiscount(totalDiscountPercentage); // discount percentage field
            pricingResult.setCostAfterDiscount(round(finalBaseRate)); // cost after discount field
        } else {
            BigDecimal basePrice = getBasePriceFromSMC3(pricingProfile);

            BigDecimal calculatedCost = AmountCalculator.getCalculatedCost(pricingProfile.getCostType(), pricingProfile.getUnitCost(), basePrice,
                   pricingResult.getCarrierTotalMiles(), ratesCO.getTotalWeight(), ratesCO.getTotalPieces());
            if (calculatedCost.compareTo(BigDecimal.ZERO) != 0) {
                pricingResult.setCostAfterDiscount(calculatedCost);
                pricingResult.setCostDiscount(round(pricingProfile.getUnitCost()));
            }

            BigDecimal finalLinehaul = calculatedCost.max(ObjectUtils.defaultIfNull(pricingProfile.getMinCost(), BigDecimal.ZERO));
            if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
                basePrice = finalLinehaul;
            }

            pricingResult.setCarrierInitialLinehaul(round(basePrice));
            pricingResult.setCarrierFinalLinehaul(round(finalLinehaul));
            pricingResult.setTotalCarrierCost(round(finalLinehaul));
            pricingResult.setCarrierLinehaulDiscount(round(basePrice.subtract(finalLinehaul)));
        }
    }

    private BigDecimal round(BigDecimal price) {
        return price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBasePriceFromSMC3(LtlRatingProfileVO pricingProfile) {
        BigDecimal basePrice = BigDecimal.ZERO;

        if (LtlRatingEngineServiceImpl.RATING_TYPE_SMC3.equals(pricingProfile.getRatingCarrierType())) {
            LTLRateShipmentDTO smc3Rates = pricingProfile.getSmc3Response();
            basePrice = smc3Rates.getTotalChargeFromDetails().add(smc3Rates.getDeficitCharge());
            if (basePrice.compareTo(smc3Rates.getMinimumCharge()) < 0) {
                basePrice = smc3Rates.getMinimumCharge();
            }
        }
        return basePrice;
    }

    private void setLiabilitiesInformation(GetOrderRatesCO ratesCO, LtlRatingProfileVO carrierRate, LtlPricingResult pricingResult) {
        BigDecimal usedProdLiability = BigDecimal.ZERO;
        BigDecimal newProdLiability = BigDecimal.ZERO;

        List<LtlCarrierLiabilitiesEntity> carrierLiabilities = carrLiabilitiesDao.findCarrierLiabilitiesByProfileId(carrierRate.getProfileId());
        Map<CommodityClass, LtlCarrierLiabilitiesEntity> carrLiabilitiesMap = carrierLiabilities.stream()
                .collect(Collectors.toMap(LtlCarrierLiabilitiesEntity::getFreightClass, Function.identity()));

        BigDecimal maxUsedProdLiabAmt = BigDecimal.ZERO;
        BigDecimal maxNewProdLiabAmt = BigDecimal.ZERO;
        for (RateMaterialCO materialCO : ratesCO.getMaterials()) {
            LtlCarrierLiabilitiesEntity carrierLiabEntity = carrLiabilitiesMap.get(getCommodityClassForLiability(materialCO, carrierRate));

            if (carrierLiabEntity != null) {
                maxUsedProdLiabAmt = maxUsedProdLiabAmt.max(ObjectUtils.defaultIfNull(carrierLiabEntity.getMaxUsedProdLiabAmt(), BigDecimal.ZERO));
                maxNewProdLiabAmt = maxNewProdLiabAmt.max(ObjectUtils.defaultIfNull(carrierLiabEntity.getMaxNewProdLiabAmt(), BigDecimal.ZERO));

                BigDecimal usedProdLiabilityResult = ObjectUtils.defaultIfNull(carrierLiabEntity.getUsedProdLiabAmt(), BigDecimal.ZERO).
                        multiply(materialCO.getWeight());
                BigDecimal newProdLiabilityResult = ObjectUtils.defaultIfNull(carrierLiabEntity.getNewProdLiabAmt(), BigDecimal.ZERO).
                        multiply(materialCO.getWeight());

                usedProdLiability = usedProdLiability.add(usedProdLiabilityResult);
                newProdLiability = newProdLiability.add(newProdLiabilityResult);
            }
        }

        usedProdLiability = usedProdLiability.compareTo(maxUsedProdLiabAmt) > 0 ? maxUsedProdLiabAmt : usedProdLiability;
        newProdLiability = newProdLiability.compareTo(maxNewProdLiabAmt) > 0 ? maxNewProdLiabAmt : newProdLiability;

        pricingResult.setUsedProdLiability(usedProdLiability);
        pricingResult.setNewProdLiability(newProdLiability);
    }

    private CommodityClass getCommodityClassForLiability(RateMaterialCO materialCO, LtlRatingProfileVO carrierRate) {
        CommodityClass commodityClass = null;
        if (carrierRate.getSingleFakMappingClass() != null) {
            commodityClass = carrierRate.getSingleFakMappingClass();
        } else if (carrierRate.getFakMapping() != null) {
            commodityClass = carrierRate.getFakMapping().get(materialCO.getCommodityClassEnum());
        }
        if (commodityClass != null && commodityClass.compareTo(materialCO.getCommodityClassEnum()) < 0) {
            return commodityClass;
        }
        return materialCO.getCommodityClassEnum();
    }
}
