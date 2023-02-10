package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.ltlrating.dao.LtlFakMapDao;
import com.pls.ltlrating.domain.LtlFakMapEntity;
import com.pls.ltlrating.service.LtlSMC3Service;
import com.pls.ltlrating.shared.CarrierRatingVO;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.smc3.dto.LTLDetailDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;
import com.pls.smc3.service.LtlRateShipmentClient;

/**
 * Implementation of {@link LtlSMC3Service}.
 *
 * @author Aleksandr Leshchenko
 */
@Service
public class LtlSMC3ServiceImpl implements LtlSMC3Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(LtlSMC3ServiceImpl.class);

    private static final String YES = "Y";
    private static final String RATING_TYPE_SMC3 = "SMC3";

    @Autowired
    private LtlRateShipmentClient smc3Client;

    @Autowired
    private LtlFakMapDao fakMapDao;

    @Override
    public LTLRateShipmentDTO getRatesFromSMC3(LTLRateShipmentDTO smc3Crit) throws Exception {
        if (smc3Crit.getTotalWeight().compareTo(new BigDecimal(10000)) < 0) {
            return smc3Client.getLtlRateShipmentMultiple(smc3Crit);
        } else {
            return smc3Client.getLtlRateShipment(smc3Crit);
        }
    }

    @Override
    public void populateSMC3Rates(GetOrderRatesCO ratesCO, LtlRatingVO... ratingProfiles) {
        Map<LTLRateShipmentDTO, List<LtlRatingProfileVO>> requests = getSmc3Requests(ratesCO, ratingProfiles);
        try {
            if (!requests.isEmpty()) {
                ExecutorService resultsCalculationPool = Executors.newFixedThreadPool(requests.size());
                for (Entry<LTLRateShipmentDTO, List<LtlRatingProfileVO>> request : requests.entrySet()) {
                    resultsCalculationPool.execute(() -> {
                        try {
                            LTLRateShipmentDTO response = getRatesFromSMC3(request.getKey());
                            if (!isIncorrectSMC3Result(response)) {
                                request.getValue().forEach(p -> p.setSmc3Response(response));
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    });
                }
                resultsCalculationPool.shutdown();
                resultsCalculationPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        removeInvalidProfiles(ratingProfiles);
    }

    private void removeInvalidProfiles(LtlRatingVO... ratingProfiles) {
        for (LtlRatingVO ratingProfile : ratingProfiles) {
            if (ratingProfile == null) {
                continue;
            }
            Iterator<Entry<Long, List<CarrierRatingVO>>> iterator = ratingProfile.getCarrierPricingDetails().entrySet().iterator();
            while (iterator.hasNext()) {
                Optional<LtlRatingProfileVO> invalidProfile = iterator.next().getValue().stream()
                        .flatMap(r -> Stream.of(r.getRate().getPricingDetails(),
                                r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails()))
                        .filter(Objects::nonNull)
                        .filter(profile -> RATING_TYPE_SMC3.equals(profile.getRatingCarrierType()) && profile.getSmc3Response() == null)
                        .findFirst();
                if (invalidProfile.isPresent()) {
                    LOGGER.warn("SMC3 returned feed of $0 amount for following tariff: {}", invalidProfile.get().getSmc3Tariff());
                    iterator.remove();
                }
            }
        }
    }

    private Map<LTLRateShipmentDTO, List<LtlRatingProfileVO>> getSmc3Requests(GetOrderRatesCO ratesCO, LtlRatingVO... ratingProfiles) {
        Map<LTLRateShipmentDTO, List<LtlRatingProfileVO>> requests = new LinkedHashMap<LTLRateShipmentDTO, List<LtlRatingProfileVO>>();
        Stream.of(ratingProfiles).filter(Objects::nonNull).flatMap(p -> p.getCarrierPricingDetails().values().stream()).flatMap(List::stream)
                .flatMap(r -> Stream.of(r.getRate().getPricingDetails(),
                        r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails()))
                .filter(Objects::nonNull)
                .filter(p -> RATING_TYPE_SMC3.equals(p.getRatingCarrierType()) && p.getSmc3Tariff() != null)
                .forEach(profile -> {
                    LTLRateShipmentDTO smc3Criteria = getSMC3CriteriaSafe(profile, ratesCO);
                    if (smc3Criteria != null) {
                        Optional<LTLRateShipmentDTO> duplicateRequest = requests.keySet().stream()
                                .filter(request -> isDuplicateRequest(request, smc3Criteria)).findFirst();
                        if (duplicateRequest.isPresent()) {
                            requests.get(duplicateRequest.get()).add(profile);
                        } else {
                            ArrayList<LtlRatingProfileVO> profiles = new ArrayList<LtlRatingProfileVO>();
                            profiles.add(profile);
                            requests.put(smc3Criteria, profiles);
                        }
                    }
                });
        return requests;
    }

    private boolean isDuplicateRequest(LTLRateShipmentDTO request, LTLRateShipmentDTO newRequest) {
        if (!newRequest.getTariffName().equals(request.getTariffName()) || !newRequest.getShipmentDate().equals(request.getShipmentDate())
                || ObjectUtils.notEqual(newRequest.getDiscountPercent(), request.getDiscountPercent())
                || newRequest.getDetails().size() != request.getDetails().size()) {
            return false;
        }
        // compare details
        for (int i = 0; i < newRequest.getDetails().size(); i++) {
            if (!newRequest.getDetails().get(i).getNmfcClass().equals(request.getDetails().get(i).getNmfcClass())) {
                return false;
            }
        }
        return true;
    }

    private boolean isIncorrectSMC3Result(LTLRateShipmentDTO smc3Result) {
        return (smc3Result.getErrorCode() != null && !"0".equalsIgnoreCase(smc3Result.getErrorCode().trim()))
                || BigDecimal.ZERO.compareTo(smc3Result.getTotalChargeFromDetails()) == 0;
    }

    private LTLRateShipmentDTO getSMC3CriteriaSafe(LtlRatingProfileVO ratingProfile, GetOrderRatesCO ratesCO) {
        String[] tariffData = ratingProfile.getSmc3Tariff().split("_");
        if (tariffData.length < 3) {
            return null;
        }
        try {
            return getSMC3Criteria(ratingProfile, ratesCO, tariffData);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private LTLRateShipmentDTO getSMC3Criteria(LtlRatingProfileVO ratingProfile, GetOrderRatesCO ratesCO, String[] tariffData)
            throws ParseException {
        String tariffNameForSMC3Criteria = tariffData[0];
        Date shipmentDate = DateUtility.stringToDate(tariffData[1], DateUtility.REVERSE_POSITIONAL_DATE);

        LTLRateShipmentDTO smc3Crit = new LTLRateShipmentDTO();
        smc3Crit.setTariffName(tariffNameForSMC3Criteria);
        smc3Crit.setDestinationCountry(ratesCO.getDestinationAddress().getCountryCode());
        smc3Crit.setDestinationPostalCode(ratesCO.getDestinationAddress().getPostalCode());
        smc3Crit.setOriginCountry(ratesCO.getOriginAddress().getCountryCode());
        smc3Crit.setOriginPostalCode(ratesCO.getOriginAddress().getPostalCode());
        smc3Crit.setShipmentDate(shipmentDate);
        smc3Crit.setOriginCity(ratesCO.getOriginAddress().getCity());
        smc3Crit.setOriginState(ratesCO.getOriginAddress().getStateCode());
        smc3Crit.setDestinationCity(ratesCO.getDestinationAddress().getCity());
        smc3Crit.setDestinationState(ratesCO.getDestinationAddress().getStateCode());
        smc3Crit.setTotalWeight(ratesCO.getTotalWeight());
        smc3Crit.setDiscountPercent(ratingProfile.getUnitCost() != null ? ratingProfile.getUnitCost().toString() : null);
        smc3Crit.setDetails(getSmc3RateDetails(ratingProfile, ratesCO));
        return smc3Crit;
    }

    private List<LTLDetailDTO> getSmc3RateDetails(LtlRatingProfileVO ratingProfile, GetOrderRatesCO ratesCO) {
        Map<CommodityClass, CommodityClass> fakMappingClasses = new HashMap<CommodityClass, CommodityClass>();

        if (ratingProfile.getSingleFakMappingClass() == null && YES.equalsIgnoreCase(ratingProfile.getFakMappingAvailable())) {
            List<LtlFakMapEntity> fakMapList = fakMapDao.findByPricingDetailId(ratingProfile.getPricingDetailId());

            for (LtlFakMapEntity fakMapEntity : fakMapList) {
                fakMappingClasses.put(fakMapEntity.getActualClass(), fakMapEntity.getMappingClass());
            }
            ratingProfile.setFakMapping(fakMappingClasses);
        }

        List<LTLDetailDTO> smc3RateDetails = new ArrayList<LTLDetailDTO>();
        for (RateMaterialCO material : ratesCO.getMaterials()) {
            LTLDetailDTO smc3RateDetail = new LTLDetailDTO();
            if (material.getWeight() != null) {
                smc3RateDetail.setWeight(material.getWeight().toString());
            }
            if (ratingProfile.getSingleFakMappingClass() != null) {
                smc3RateDetail.setNmfcClass(ratingProfile.getSingleFakMappingClass().getDbCode());
            } else if (fakMappingClasses.get(material.getCommodityClassEnum()) != null) {
                smc3RateDetail.setNmfcClass(fakMappingClasses.get(material.getCommodityClassEnum()).getDbCode());
            } else {
                smc3RateDetail.setNmfcClass(material.getCommodityClassEnum().getDbCode());
            }
            smc3RateDetail.setEnteredNmfcClass(material.getCommodityClassEnum().getDbCode());
            // No need to add quantity
            smc3RateDetails.add(smc3RateDetail);
        }
        return smc3RateDetails;
    }
}
