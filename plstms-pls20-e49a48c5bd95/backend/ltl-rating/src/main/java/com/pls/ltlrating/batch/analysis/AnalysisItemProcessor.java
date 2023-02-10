package com.pls.ltlrating.batch.analysis;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.ZipCodeDao;
import com.pls.core.domain.address.ZipCodeEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.analysis.FAAccessorialsEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FAMaterialsEntity;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FATariffsEntity;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.service.LtlSMC3Service;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.smc3.dto.LTLDetailDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * Item processor for Freight Analysis batch job.
 *
 * @author Aleksandr Leshchenko
 */
@Transactional(readOnly = true)
public class AnalysisItemProcessor implements ItemProcessor<AnalysisItem, List<FAOutputDetailsEntity>> {

    private SessionFactory sessionFactory;

    @Autowired
    private LtlRatingEngineService ratingService;

    @Autowired
    private LtlSMC3Service smc3Service;

    @Autowired
    private ZipCodeDao zipDao;

    @Autowired
    private LtlPricingProfileDao pricingDao;

    @Override
    public List<FAOutputDetailsEntity> process(AnalysisItem item) throws Exception {
        FAInputDetailsEntity input = sessionFactory.getCurrentSession().get(FAInputDetailsEntity.class, item.getRowId());
        List<FAOutputDetailsEntity> output = Collections.emptyList();

        if (item.getTariffId() != null) {
            LTLRateShipmentDTO ratesFromSMC3 = smc3Service.getRatesFromSMC3(getCriteriaForSMC3(input, item));
            output = processSMC3Results(ratesFromSMC3, input, item);
        } else {
            // TODO make some improvements for Benchmarks
            List<LtlPricingResult> rates = ratingService.getRates(getCriteriaForPricingCall(input, item));
            if (rates == null) {
                rates = Collections.emptyList();
            }
            if (BooleanUtils.isTrue(input.getAnalysis().getBlockIndirectServiceType())) {
                rates = rates.stream().filter(rate -> LtlServiceType.DIRECT == rate.getServiceType()).collect(Collectors.toList());
            }
            rates.sort((o1, o2) -> {
                return new CompareToBuilder()
                        .append(o1.getBlockedFrmBkng(), o2.getBlockedFrmBkng())
                        .append(getShipperCost(o1, input), getShipperCost(o2, input))
                        .append(getCarrierCost(o1, input), getCarrierCost(o2, input))
                        .toComparison();
            });
            output = processPricingResults(rates, input, item);
        }
        return output;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private List<FAOutputDetailsEntity> processPricingResults(List<LtlPricingResult> rates, FAInputDetailsEntity input, AnalysisItem item) {
        List<FATariffsEntity> tariffs = input.getAnalysis().getTariffs().stream()
                .filter(t -> (ObjectUtils.equals(t.getCustomerId(), item.getCustomerId()))
                        && t.getPricingProfileId() != null && t.getTariffType() != PricingType.SMC3)
                .collect(Collectors.toList());
        List<FAOutputDetailsEntity> results = new ArrayList<FAOutputDetailsEntity>();
        if (input.getCarrier() != null) {
            Optional<LtlPricingResult> rate = rates.stream().filter(r -> ObjectUtils.equals(r.getCarrierOrgId(), input.getCarrier().getId()))
                    .findFirst();
            if (rate.isPresent()) {
                results.addAll(getOutputDetails(rate.get(), input, tariffs, null));
            }
        } else if (StringUtils.isBlank(input.getCarrierScac())) {
            for (int i = 0; i < rates.size(); i++) {
                List<FAOutputDetailsEntity> outputDetails = getOutputDetails(rates.get(i), input, tariffs, i);
                results.addAll(outputDetails);
                removeProcessedBenchmarkTariffs(outputDetails, tariffs);
            }
        }
        addNonProcessedResults(tariffs, results, item.getCustomerId(), input);
        return results;
    }

    private void removeProcessedBenchmarkTariffs(List<FAOutputDetailsEntity> outputDetails, List<FATariffsEntity> tariffs) {
        for (FAOutputDetailsEntity outputDetail : outputDetails) {
            if (outputDetail.getTariff().getTariffType() == PricingType.BENCHMARK) {
                tariffs.remove(outputDetail.getTariff());
            }
        }
    }

    private void addNonProcessedResults(List<FATariffsEntity> tariffs, List<FAOutputDetailsEntity> results, Long customerId,
            FAInputDetailsEntity input) {
        List<Long> processedTariffs = results.stream().map(r -> r.getTariff().getId()).collect(Collectors.toList());
        List<FATariffsEntity> nonProcessedCustomerTariffs = tariffs.stream()
                .filter(t -> ObjectUtils.equals(t.getCustomerId(), customerId) && !processedTariffs.contains(t.getId()))
                .collect(Collectors.toList());
        if (!nonProcessedCustomerTariffs.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("No pricing was built for following selected customer tariff");
            if (nonProcessedCustomerTariffs.size() > 1) {
                errorMessage.append('s');
            }
            errorMessage.append(": ");
            errorMessage.append(nonProcessedCustomerTariffs.stream().map(FATariffsEntity::getTariffName).collect(Collectors.joining(", ")));
            nonProcessedCustomerTariffs.forEach(tariff -> {
                FAOutputDetailsEntity result = new FAOutputDetailsEntity();
                result.setInputDetails(input);
                result.setTariff(tariff);
                result.setErrorMessage(errorMessage.toString());
                results.add(result);
            });
        }
    }

    private List<FAOutputDetailsEntity> getOutputDetails(LtlPricingResult pricingResult, FAInputDetailsEntity input,
            List<FATariffsEntity> tariffs, Integer seq) {
        List<FAOutputDetailsEntity> results = new ArrayList<FAOutputDetailsEntity>();
        for (FATariffsEntity tariff : tariffs) {
            if (ObjectUtils.equals(pricingResult.getProfileId(), tariff.getPricingProfileId())
                    || (tariff.getTariffType() == PricingType.BENCHMARK
                            && ObjectUtils.equals(pricingResult.getBenchmarkPricingProfileId(), tariff.getPricingProfileId()))) {
                if (tariff.getTariffType() == PricingType.BENCHMARK) {
                    // save benchmark cost
                    results.add(getOutputDetail(input, tariff, pricingResult, CostDetailOwner.B, seq));
                } else {
                    // save carrier cost
                    results.add(getOutputDetail(input, tariff, pricingResult, CostDetailOwner.C, seq));
                    if (tariff.getCustomerId() != null) {
                        // save shipper cost
                        results.add(getOutputDetail(input, tariff, pricingResult, CostDetailOwner.S, seq));
                    }
                }
            }
        }
        return results;
    }

    private FAOutputDetailsEntity getOutputDetail(FAInputDetailsEntity input, FATariffsEntity tariff, LtlPricingResult pricingResult,
            CostDetailOwner owner, Integer seq) {
        FAOutputDetailsEntity result = new FAOutputDetailsEntity();
        result.setInputDetails(input);
        result.setTariff(tariff);
        result.setCarrierId(pricingResult.getCarrierOrgId());
        result.setTransitDays(pricingResult.getTransitTime());
        result.setOwner(owner);
        if (owner == CostDetailOwner.C) {
            result.setSubtotal(getCarrierCost(pricingResult, input));
        } else if (owner == CostDetailOwner.S) {
            result.setSubtotal(getShipperCost(pricingResult, input));
        } else if (owner == CostDetailOwner.B) {
            result.setSubtotal(getBenchmarkCost(pricingResult, input));
        }
        result.setSeq(seq);
        return result;
    }

    private BigDecimal getBenchmarkCost(LtlPricingResult pricingResult, FAInputDetailsEntity input) {
        if (StatusYesNo.NO.equals(input.getCalculateFSC()) && pricingResult.getBenchmarkFuelSurcharge() != null) {
            return pricingResult.getTotalBenchmarkCost().subtract(pricingResult.getBenchmarkFuelSurcharge());
        }
        return pricingResult.getTotalBenchmarkCost();
    }

    private BigDecimal getShipperCost(LtlPricingResult pricingResult, FAInputDetailsEntity input) {
        if (StatusYesNo.NO.equals(input.getCalculateFSC()) && pricingResult.getShipperFuelSurcharge() != null) {
            return pricingResult.getTotalShipperCost().subtract(pricingResult.getShipperFuelSurcharge());
        }
        return pricingResult.getTotalShipperCost();
    }

    private BigDecimal getCarrierCost(LtlPricingResult pricingResult, FAInputDetailsEntity input) {
        if (StatusYesNo.NO.equals(input.getCalculateFSC()) && pricingResult.getCarrierFuelSurcharge() != null) {
            return pricingResult.getTotalCarrierCost().subtract(pricingResult.getCarrierFuelSurcharge());
        }
        return pricingResult.getTotalCarrierCost();
    }

    private GetOrderRatesCO getCriteriaForPricingCall(FAInputDetailsEntity input, AnalysisItem item) throws ApplicationException {
        GetOrderRatesCO result = new GetOrderRatesCO();
        result.setShipperOrgId(item.getCustomerId());
        if (input.getCarrier() != null) {
            result.setCarrierOrgId(input.getCarrier().getId());
        }
        result.setShipDate(input.getShipmentDate());
        if (result.getShipDate() == null) {
            result.setShipDate(new Date());
        }
        AddressVO originAddress = new AddressVO();
        originAddress.setCountryCode(input.getOriginCountry());
        originAddress.setPostalCode(StringUtils.defaultIfBlank(input.getOriginOverrideZip(), input.getOriginZip()));
        originAddress.setCity(input.getOriginCity());
        originAddress.setStateCode(input.getOriginState());
        verifyAddress(originAddress);
        result.setOriginAddress(originAddress);
        AddressVO destAddress = new AddressVO();
        destAddress.setCountryCode(input.getDestCountry());
        destAddress.setPostalCode(StringUtils.defaultIfBlank(input.getDestOverrideZip(), input.getDestZip()));
        destAddress.setCity(input.getDestCity());
        destAddress.setStateCode(input.getDestState());
        verifyAddress(destAddress);
        result.setDestinationAddress(destAddress);
        result.setTotalWeight(getTotalWeight(input));
        result.setCommodityClassSet(input.getMaterials().stream().map(FAMaterialsEntity::getCommodityClass).collect(Collectors.toSet()));
        result.setAccessorialTypes(input.getAccessorials().stream().map(FAAccessorialsEntity::getAccessorial)
                .filter(this::isNotExcludeAccessorial)
                .collect(Collectors.toList()));
        result.setMaterials(input.getMaterials().stream().map(this::getMaterialsForPricing).collect(Collectors.toList()));
        if (BooleanUtils.isTrue(input.getAnalysis().getBlockIndirectServiceType())) {
            result.setServiceType(LtlServiceType.DIRECT);
        }
        result.setPricingProfileIDs(getPricingProfileIDs(input, item));
        return result;
    }

    private List<Long> getPricingProfileIDs(FAInputDetailsEntity input, AnalysisItem item) {
        List<Long> pricingProfileIDs = input.getAnalysis().getTariffs().stream()
                .filter(t -> (ObjectUtils.equals(t.getCustomerId(), item.getCustomerId())) && t.getPricingProfileId() != null
                        && t.getTariffType() != PricingType.SMC3 && t.getTariffType() != PricingType.BENCHMARK)
                .map(FATariffsEntity::getPricingProfileId)
                .collect(Collectors.toList());
        if (!pricingProfileIDs.isEmpty()) {
            // add blanket profiles IDs for selected Buy/Sell profiles with Use Blanket flag
            List<Long> useBlanketCarriers = input.getAnalysis().getTariffs().stream()
                    .filter(t -> isUseBlanketTariff(item, t))
                    .map(t -> t.getPricingProfile().getCarrierOrganization().getId())
                    .distinct()
                    .collect(Collectors.toList());
            if (!useBlanketCarriers.isEmpty()) {
                pricingProfileIDs.addAll(pricingDao.getBlanketProfileIDsForCarriers(useBlanketCarriers));
            }
        }
        return pricingProfileIDs;
    }

    private boolean isUseBlanketTariff(AnalysisItem item, FATariffsEntity t) {
        return ObjectUtils.equals(t.getCustomerId(), item.getCustomerId()) && t.getPricingProfileId() != null
                && t.getTariffType() == PricingType.BUY_SELL
                && t.getPricingProfile().getProfileDetails().stream().anyMatch(d -> StatusYesNo.YES.getCode().equals(d.getUseBlanket()));
    }

    private void verifyAddress(AddressVO address) throws ApplicationException {
        if (StringUtils.isBlank(address.getCity()) || StringUtils.isBlank(address.getStateCode())) {
            List<ZipCodeEntity> defaultZipCodes = zipDao.getDefault(address.getCountryCode(), address.getPostalCode());
            if (defaultZipCodes.size() == 1) {
                if (StringUtils.isBlank(address.getCity())) {
                    address.setCity(defaultZipCodes.get(0).getCity());
                }
                if (StringUtils.isBlank(address.getStateCode())) {
                    address.setStateCode(defaultZipCodes.get(0).getStateCode());
                }
            } else {
                throw new ApplicationException("City and/or State were not found for ZIP " + address.getPostalCode() + " and country "
                        + address.getCountryCode());
            }
        }
    }

    private void verifyAddressForSMC3(LTLRateShipmentDTO dto) throws ApplicationException {
        AddressVO address = createAddress(dto, true);
        verifyAddress(address);
        dto.setOriginCity(address.getCity());
        dto.setOriginState(address.getStateCode());
        address = createAddress(dto, false);
        verifyAddress(address);
        dto.setDestinationCity(address.getCity());
        dto.setDestinationState(address.getStateCode());
    }

    private AddressVO createAddress(LTLRateShipmentDTO dto, boolean isOrigin) {
        AddressVO addressVO = new AddressVO();
        if (isOrigin) {
            addressVO.setCity(dto.getOriginCity());
            addressVO.setStateCode(dto.getOriginState());
            addressVO.setPostalCode(dto.getOriginPostalCode());
            addressVO.setCountryCode(dto.getOriginCountry());
        } else {
            addressVO.setCity(dto.getDestinationCity());
            addressVO.setStateCode(dto.getDestinationState());
            addressVO.setPostalCode(dto.getDestinationPostalCode());
            addressVO.setCountryCode(dto.getDestinationCountry());
        }
        return addressVO;
    }

    private RateMaterialCO getMaterialsForPricing(FAMaterialsEntity material) {
        RateMaterialCO result = new RateMaterialCO();
        result.setWeight(material.getWeight());
        result.setWeightUnit("LBS");
        if (material.getInputDetails().getPallet() != null && material.getInputDetails().getPallet() > 0) {
            result.setPackageType("PLT");
            result.setQuantity(material.getInputDetails().getPallet().intValue());
        }
        result.setCommodityClassEnum(material.getCommodityClass());
        return result;
    }

    private boolean isNotExcludeAccessorial(String accessorial) {
        return !StringUtils.equalsIgnoreCase(accessorial, "P_BUS") && !StringUtils.equalsIgnoreCase(accessorial, "D_BUS");
    }

    private List<FAOutputDetailsEntity> processSMC3Results(LTLRateShipmentDTO ratesFromSMC3, FAInputDetailsEntity input, AnalysisItem item) {
        BigDecimal basePrice = null;
        if (ratesFromSMC3 != null && ratesFromSMC3.getDetails() != null && !ratesFromSMC3.getDetails().isEmpty()
                && (ratesFromSMC3.getErrorCode() == null || "0".equalsIgnoreCase(ratesFromSMC3.getErrorCode().trim()))
                && BigDecimal.ZERO.compareTo(ratesFromSMC3.getTotalChargeFromDetails()) != 0) {
            basePrice = ratesFromSMC3.getTotalChargeFromDetails().add(ratesFromSMC3.getDeficitCharge());
            if (basePrice.compareTo(ratesFromSMC3.getMinimumCharge()) < 0) {
                basePrice = ratesFromSMC3.getMinimumCharge();
            }
        }
        FAOutputDetailsEntity result = new FAOutputDetailsEntity();
        result.setInputDetails(input);
        result.setTariff(getTariff(input, item.getTariffId()));
        result.setOwner(CostDetailOwner.C);
        result.setSubtotal(basePrice);
        return Collections.singletonList(result);
    }

    private LTLRateShipmentDTO getCriteriaForSMC3(FAInputDetailsEntity input, AnalysisItem item) throws ApplicationException {
        LTLRateShipmentDTO result = getSMC3CriteriaForTariff(input, item);
        result.setDestinationCountry(input.getDestCountry());
        result.setDestinationPostalCode(StringUtils.defaultIfBlank(input.getDestOverrideZip(), input.getDestZip()));
        result.setDestinationCity(input.getDestCity());
        result.setDestinationState(input.getDestState());
        result.setOriginCountry(input.getOriginCountry());
        result.setOriginPostalCode(StringUtils.defaultIfBlank(input.getOriginOverrideZip(), input.getOriginZip()));
        result.setOriginCity(input.getOriginCity());
        result.setOriginState(input.getOriginState());
        verifyAddressForSMC3(result);
        result.setTotalWeight(getTotalWeight(input));
        result.setDetails(input.getMaterials().stream().map(this::getMaterialsForSMC3).collect(Collectors.toList()));
        return result;
    }

    private BigDecimal getTotalWeight(FAInputDetailsEntity input) {
        return input.getMaterials().stream().map(FAMaterialsEntity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LTLDetailDTO getMaterialsForSMC3(FAMaterialsEntity material) {
        LTLDetailDTO smc3RateDetail = new LTLDetailDTO();
        smc3RateDetail.setWeight(material.getWeight().toString());
        smc3RateDetail.setNmfcClass(material.getCommodityClass().getDbCode());
        smc3RateDetail.setEnteredNmfcClass(material.getCommodityClass().getDbCode());
        return smc3RateDetail;
    }

    private LTLRateShipmentDTO getSMC3CriteriaForTariff(FAInputDetailsEntity input, AnalysisItem item) throws ApplicationException {
        FATariffsEntity tariff = getTariff(input, item.getTariffId());
        if (tariff != null) {
            String tariffName = tariff.getTariffName();
            if (tariffName != null) {
                String[] tariffData = tariffName.split("_");
                if (tariffData.length >= 3) {
                    LTLRateShipmentDTO result = new LTLRateShipmentDTO();
                    result.setTariffName(tariffData[0]);
                    try {
                        result.setShipmentDate(DateUtility.stringToDate(tariffData[1], DateUtility.REVERSE_POSITIONAL_DATE));
                        return result;
                    } catch (ParseException e) {
                        throw new ApplicationException("Incorrect empty SMC3 tariff name", e);
                    }
                }
            }
        }
        throw new ApplicationException("Incorrect empty SMC3 tariff name");
    }

    private FATariffsEntity getTariff(FAInputDetailsEntity input, Long tariffId) {
        return input.getAnalysis().getTariffs().stream().filter(t -> ObjectUtils.equals(t.getId(), tariffId)).findFirst().orElse(null);
    }
}
