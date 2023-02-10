package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.dao.LtlPricingApiCriteriaDao;
import com.pls.ltlrating.domain.LtlPricingApiAddressCritEntity;
import com.pls.ltlrating.domain.LtlPricingApiCriteriaEntity;
import com.pls.ltlrating.domain.LtlPricingApiMaterialsCritEntity;
import com.pls.ltlrating.service.LtlPricingEngineWSService;
import com.pls.ltlrating.service.LtlRatingEngineService;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingAccWSResult;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingAddlCostsWSResult;
import com.pls.ltlrating.shared.LtlPricingCostDetailsWSResult;
import com.pls.ltlrating.shared.LtlPricingDetailsWSResult;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.LtlPricingWSResult;
import com.pls.ltlrating.shared.RateMaterialCO;

/**
 * Class where we calculate LTL rates for the given parameters for the customer by making WS calls.
 *
 * @author Hima Bindu Challa
 *
 */
@Service
@Transactional
public class LtlPricingEngineWSServiceImpl implements LtlPricingEngineWSService {

    @Autowired
    private LtlRatingEngineService ltlRatingEngineService;

    @Autowired
    private LtlPricingApiCriteriaDao pricingApiDao;

    @Autowired
    private CarrierDao carrierDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(LtlPricingEngineWSServiceImpl.class);

    /**
     * Get rates for customer by customer credentials and criteria.
     * @param criteria - Order Rates Criteria.
     * @return return the final rates in ascending order.
     */
    @Override
    public LtlPricingWSResult getRatesForCustomer(GetOrderRatesCO criteria) {
        long startTime = System.nanoTime();

        LtlPricingWSResult wsResult = new LtlPricingWSResult();
        wsResult.setProfileCount(0);

        try {
            prepareCriteria(criteria, wsResult);
            if (wsResult.getCode() != null) {
                return wsResult;
            }

            List<LtlPricingResult> pricingResults = ltlRatingEngineService.getRates(criteria);
            wsResult.setCode(0);

            //Sort the results by shipper cost in ascending order.
            if (CollectionUtils.isNotEmpty(pricingResults)) {
                Collections.sort(pricingResults, Comparator.comparing(LtlPricingResult::getTotalShipperCost));
            } else {
                wsResult.setProfileCount(0);
                return wsResult;
            }

            //Based on request type, set maximum results to be returned to user and return only that number of results.
            if (StringUtils.isBlank(criteria.getRequestType()) || "LCC".equalsIgnoreCase(criteria.getRequestType())) {
                pricingResults = pricingResults.subList(0, 1);
            } else if ("TL5".equalsIgnoreCase(criteria.getRequestType())) {
                pricingResults = pricingResults.subList(0, Integer.min(5, pricingResults.size()));
            }

            wsResult.setProfiles(pricingResults.stream().map(this::convertPricingResultToWSResult).collect(Collectors.toList()));
            wsResult.setProfileCount(wsResult.getProfiles().size());
        } catch (Exception e) {
            wsResult.setCode(1);
            wsResult.setError("PROCESSING_ERROR");
        }

        saveApiCriteria(criteria, new BigDecimal(System.nanoTime() - startTime).divide(new BigDecimal(1000000000), 2, BigDecimal.ROUND_HALF_UP));
        return wsResult;
    }

    private void prepareCriteria(GetOrderRatesCO criteria, LtlPricingWSResult wsResult) {
        //If Shipper Org ID is not available in the criteria, then return error code to user.
        //Shipper Org ID is either passed with WS criteria in JSON object or retrieved from system using credentials.
        if (criteria.getShipperOrgId() == null) {
            wsResult.setCode(1);
            wsResult.setError("NOT_FOUND_ERROR");
        }

        if (criteria.getScac() != null) {
            criteria.setCarrierOrgId(getCarrierOrgId(criteria.getScac()));
        }

        //If the required criteria is missing or wrong codes are sent in criteria, then return error code to user.
        if (isInvalidCriteria(criteria)) {
            wsResult.setCode(1);
            wsResult.setError("INPUT_ERROR");
        }

        //Convert Commodity Class and Hazmat values to enum and boolean values for pricing logic to process.
        for (RateMaterialCO material : criteria.getMaterials()) {
            material.setCommodityClassEnum(CommodityClass.convertFromDbCode(material.getCommodityClass()));
            material.setHazmatBool(material.getHazmat() != null && "Yes".equalsIgnoreCase(material.getHazmat().trim()));
        }
    }

    private Long getCarrierOrgId(String scac) {
        CarrierEntity carrier = carrierDao.findByScac(scac);
        if (carrier == null) {
            return null;
        }
        return carrier.getId();
    }

    //Convert the pricing results to webservice result object.
    private LtlPricingDetailsWSResult convertPricingResultToWSResult(LtlPricingResult pricingResult) {
        LtlPricingDetailsWSResult pricingDetailsWSResult = new LtlPricingDetailsWSResult();
        pricingDetailsWSResult.setProfileId(pricingResult.getProfileId());
        pricingDetailsWSResult.setScac(pricingResult.getScac());
        pricingDetailsWSResult.setCarrierName(pricingResult.getCarrierName());
        pricingDetailsWSResult.setCurrencyCode(pricingResult.getCurrencyCode());
        pricingDetailsWSResult.setTotalCost(roundSafe(pricingResult.getTotalShipperCost()));
        pricingDetailsWSResult.setTotalMiles(pricingResult.getTotalMiles());
        pricingDetailsWSResult.setTransitDate(pricingResult.getTransitDate());
        pricingDetailsWSResult.setTransitDays(pricingResult.getTransitTime());
        pricingDetailsWSResult.setProhibitedCommodities(pricingResult.getProhibitedCommodities());
        pricingDetailsWSResult.setServiceType(pricingResult.getServiceType());
        pricingDetailsWSResult.setNewProdLiability(roundSafe(pricingResult.getNewProdLiability()));
        pricingDetailsWSResult.setUsedProdLiability(roundSafe(pricingResult.getUsedProdLiability()));
        pricingDetailsWSResult.setCostDetails(getCostDetails(pricingResult));
        return pricingDetailsWSResult;
    }

    private LtlPricingCostDetailsWSResult getCostDetails(LtlPricingResult pricingResult) {
        LtlPricingCostDetailsWSResult costDetailsWSResult = new LtlPricingCostDetailsWSResult();
        costDetailsWSResult.setInitialLinehaul(roundSafe(pricingResult.getShipperInitialLinehaul()));
        costDetailsWSResult.setDiscount(roundSafe(pricingResult.getShipperLinehaulDiscount()));
        costDetailsWSResult.setFinalLinehaul(roundSafe(pricingResult.getShipperFinalLinehaul()));
        costDetailsWSResult.setFuelSurcharge(roundSafe(pricingResult.getShipperFuelSurcharge()));

        List<LtlPricingAccWSResult> accessorialsWSResults = new ArrayList<LtlPricingAccWSResult>();
        List<LtlPricingAddlCostsWSResult> addlCostsWSResults = new ArrayList<LtlPricingAddlCostsWSResult>();

        if (pricingResult.getAccessorials() != null) {
            for (LtlPricingAccessorialResult accessorial : pricingResult.getAccessorials()) {
                if ("LTL".equalsIgnoreCase(accessorial.getAccessorialGroup())) {
                    LtlPricingAccWSResult accessorialWSResult = new LtlPricingAccWSResult();
                    accessorialWSResult.setAccessorialType(accessorial.getAccessorialType());
                    accessorialWSResult.setDescription(accessorial.getAccessorialDescription());
                    accessorialWSResult.setTotalCost(roundSafe(accessorial.getShipperAccessorialCost()));
                    accessorialsWSResults.add(accessorialWSResult);
                } else {
                    LtlPricingAddlCostsWSResult addlCostWSResult = new LtlPricingAddlCostsWSResult();
                    addlCostWSResult.setAddlCostType(accessorial.getAccessorialType());
                    addlCostWSResult.setDescription(accessorial.getAccessorialDescription());
                    addlCostWSResult.setTotalCost(roundSafe(accessorial.getShipperAccessorialCost()));
                    addlCostsWSResults.add(addlCostWSResult);
                }
            }
        }
        costDetailsWSResult.setAccessorials(accessorialsWSResults);
        costDetailsWSResult.setAdditionalCosts(addlCostsWSResults);
        return costDetailsWSResult;
    }

    private BigDecimal roundSafe(BigDecimal val) {
        return val == null ? null : val.setScale(2, BigDecimal.ROUND_UP);
    }

    private boolean isInvalidCriteria(GetOrderRatesCO criteria) {
        return criteria.getShipDate() == null || isInvalidAddress(criteria.getOriginAddress())
                || isInvalidAddress(criteria.getDestinationAddress()) || isInvalidMaterials(criteria.getMaterials())
                || (criteria.getScac() != null && criteria.getCarrierOrgId() == null);
    }

    private boolean isInvalidAddress(AddressVO address) {
        return address == null || StringUtils.isBlank(address.getPostalCode()) || StringUtils.isBlank(address.getCity())
                || StringUtils.isBlank(address.getStateCode());
    }

    private boolean isInvalidCommodityClass(String commodityClass) {
        try {
            if (commodityClass == null || CommodityClass.convertFromDbCode(commodityClass) == null) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    private boolean isInvalidMaterials(List<RateMaterialCO> materials) {
        return CollectionUtils.isEmpty(materials) || materials.get(0).getWeight() == null
                || materials.get(0).getWeight().compareTo(BigDecimal.ZERO) == 0 || isInvalidCommodityClass(materials.get(0).getCommodityClass());
    }

    private void saveApiCriteria(GetOrderRatesCO criteria, BigDecimal timeTaken) {
        try {
            LtlPricingApiCriteriaEntity apiCriteria = getPricingApiCriteria(criteria, timeTaken);
            pricingApiDao.saveOrUpdate(apiCriteria);
        } catch (Exception ex) {
            LOGGER.info("Error occurred while saving the API criteria", ex);
        }
    }

    private LtlPricingApiCriteriaEntity getPricingApiCriteria(GetOrderRatesCO criteria, BigDecimal timeTaken) {
        LtlPricingApiCriteriaEntity apiCrit = new LtlPricingApiCriteriaEntity();
        apiCrit.setShipperOrgId(criteria.getShipperOrgId());
        apiCrit.setCarrierOrgId(criteria.getCarrierOrgId());
        apiCrit.setShipDate(criteria.getShipDate());
        apiCrit.setAccessorialTypes(StringUtils.join(criteria.getAccessorialTypes(), ","));
        apiCrit.setGuaranteedTime(criteria.getGuaranteedTime());
        apiCrit.setPalletType(criteria.isPalletType() ? "Y" : "N");
        apiCrit.setGainShareAccount(criteria.isGainshareAccount() ? "Y" : "N");
        apiCrit.setMovementType(criteria.getMovementType());
        apiCrit.setRequestType(criteria.getRequestType());
        apiCrit.setScac(criteria.getScac());
        apiCrit.setUserId(criteria.getUserId());
        apiCrit.setApiReturnTime(timeTaken);

        apiCrit.setOrigin(getApiAddressCriteria(criteria.getOriginAddress()));
        apiCrit.setDestination(getApiAddressCriteria(criteria.getDestinationAddress()));
        apiCrit.setLoadMaterials(getApiMaterialsCriteria(criteria.getMaterials()));

        return apiCrit;
    }

    private LtlPricingApiAddressCritEntity getApiAddressCriteria(AddressVO criteria) {
        if (criteria != null) {
            LtlPricingApiAddressCritEntity addressCrit = new LtlPricingApiAddressCritEntity();
            addressCrit.setAddress1(criteria.getAddress1());
            addressCrit.setAddress2(criteria.getAddress2());
            addressCrit.setCity(criteria.getCity());
            addressCrit.setStateCode(criteria.getStateCode());
            addressCrit.setPostalCode(criteria.getPostalCode());
            addressCrit.setCountryCode(criteria.getCountryCode());

            return addressCrit;
        }

        return null;
    }

    private List<LtlPricingApiMaterialsCritEntity> getApiMaterialsCriteria(List<RateMaterialCO> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return null;
        }

        List<LtlPricingApiMaterialsCritEntity> apiMaterials = new ArrayList<LtlPricingApiMaterialsCritEntity>();
        for (RateMaterialCO material : criteria) {
            LtlPricingApiMaterialsCritEntity apiMaterialCrit = new LtlPricingApiMaterialsCritEntity();
            apiMaterialCrit.setCommodityClass(material.getCommodityClass());
            apiMaterialCrit.setWeight(material.getWeight());
            apiMaterialCrit.setHeight(material.getHeight());
            apiMaterialCrit.setWidth(material.getWidth());
            apiMaterialCrit.setLength(material.getLength());
            apiMaterialCrit.setDimensionUnit(material.getDimensionUnit());
            apiMaterialCrit.setQuantity(material.getQuantity());
            apiMaterialCrit.setPackageType(material.getPackageType());
            apiMaterialCrit.setHazmat(material.getHazmatBool() ? "Y" : "N");

            apiMaterials.add(apiMaterialCrit);
        }

        return apiMaterials;
    }

}
