package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pls.core.dao.CountryDao;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.dao.LtlAccessorialsMappingDao;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;
import com.pls.ltlrating.domain.bo.LTLDayAndRossRateBO;
import com.pls.ltlrating.service.LtlCarrierAPIService;
import com.pls.ltlrating.shared.CarrierRatingVO;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.ltlrating.soap.client.DayAndRossSOAPClient;
import com.pls.ltlrating.soap.proxy.ArrayOfServiceLevels;
import com.pls.ltlrating.soap.proxy.ArrayOfShipmentItem;
import com.pls.ltlrating.soap.proxy.ArrayOfShipmentSpecialService;
import com.pls.ltlrating.soap.proxy.Division;
import com.pls.ltlrating.soap.proxy.GetRate2Response;
import com.pls.ltlrating.soap.proxy.LengthUnit;
import com.pls.ltlrating.soap.proxy.MeasurementSystem;
import com.pls.ltlrating.soap.proxy.PaymentType;
import com.pls.ltlrating.soap.proxy.ServiceLevels;
import com.pls.ltlrating.soap.proxy.Shipment;
import com.pls.ltlrating.soap.proxy.ShipmentAddress;
import com.pls.ltlrating.soap.proxy.ShipmentCharge;
import com.pls.ltlrating.soap.proxy.ShipmentItem;
import com.pls.ltlrating.soap.proxy.ShipmentSpecialService;
import com.pls.ltlrating.soap.proxy.ShipmentType;
import com.pls.ltlrating.soap.proxy.WeightUnit;

/**
 * Implementation of {@link LtlCarrierAPIService}.
 *
 * @author Brichak Aleksandr
 */
@Service
public class LtlDayAndRossCarrierAPIServiceImpl implements LtlCarrierAPIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LtlDayAndRossCarrierAPIServiceImpl.class);

    private static final String DISCOUNT_CODE = "CEXCHU";

    @Autowired
    private DayAndRossSOAPClient dayAndRossSOAPClient;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private LtlAccessorialsMappingDao ltlAccessorialsMappingDao;

    @Value("${dayAndRoss.billToAccount}")
    private String billToAccount;

    @Value("${dayAndRoss.dayAndRossSCAC}")
    private String dayAndRossSCAC;

    private ThreadLocal<List<LtlAccessorialsMappingEntity>> mapping = new ThreadLocal<>();

    @Override
    public void populateRates(GetOrderRatesCO ratesCO, LtlRatingVO ratingProfile) {
        List<LtlRatingProfileVO> apiProfiles = getApiProfiles(ratingProfile);
        if (!apiProfiles.isEmpty()) {
            Shipment request = getDayAndRossCriteria(ratesCO);
            if (request != null) {
                GetRate2Response response = dayAndRossSOAPClient.getDayAndRossRate(request);
                populateResponseResultToProfiles(apiProfiles, response);
            }
        }
        removeInvalidProfiles(ratingProfile);
    }

    private void populateResponseResultToProfiles(List<LtlRatingProfileVO> apiProfiles, GetRate2Response response) {
        if (isCorrectDayAndRossResult(response)) {
            LTLDayAndRossRateBO bo = buildDayAndRossResponse(response);
            apiProfiles.forEach(profile -> {
                profile.setDayAndRossResponse(bo);
            });
        }
    }

    private List<LtlRatingProfileVO> getApiProfiles(LtlRatingVO ratingProfile) {
        return ratingProfile.getCarrierPricingDetails().values().stream().flatMap(List::stream)
                .flatMap(this::getPricingDetailsStream)
                .filter(this::isValidApiProfile)
                .collect(Collectors.toList());
    }

    private Stream<LtlRatingProfileVO> getPricingDetailsStream(CarrierRatingVO r) {
        return Stream.of(r.getRate().getPricingDetails(), r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails());
    }

    private boolean isValidApiProfile(LtlRatingProfileVO profile) {
        return profile != null && LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(profile.getRatingCarrierType())
                && StringUtils.equals(dayAndRossSCAC, profile.getScac());
    }

    private LTLDayAndRossRateBO buildDayAndRossResponse(GetRate2Response response) {
        LTLDayAndRossRateBO result = new LTLDayAndRossRateBO();

        List<ServiceLevels> serviceLevels = response.getGetRate2Result().getServiceLevels();
        ServiceLevels serviceLevel = serviceLevels.stream().findFirst().get();
        setCharges(serviceLevel, result);
        result.setEstimatedDeliveryDate(serviceLevel.getExpectedDeliveryDate() != null
                ? serviceLevel.getExpectedDeliveryDate().toGregorianCalendar().getTime() : null);
        try {
            result.setTransitDays(Integer.parseInt(serviceLevel.getTransitTime()));
        } catch (NumberFormatException e) {
            LOGGER.warn("No transit time in the response. " + e.getMessage());
        }
        return result;
    }

    private void setCharges(ServiceLevels serviceLevel, LTLDayAndRossRateBO result) {
        Map<String, LtlAccessorialsMappingEntity> accessorialsMap = getAccessorialsMap();

        BigDecimal initialCost = serviceLevel.getTotalAmount();
        BigDecimal totalAmount = serviceLevel.getTotalAmount();
        Map<String, LtlPricingAccessorialResult> charges = new HashMap<>();
        for (ShipmentCharge charge : serviceLevel.getShipmentCharges().getShipmentCharge()) {
            LtlAccessorialsMappingEntity plsAccessorial = accessorialsMap.get(StringUtils.upperCase(charge.getChargeCode()));
            if (DISCOUNT_CODE.equalsIgnoreCase(charge.getChargeCode())) {
                initialCost = initialCost.subtract(charge.getAmount());
                totalAmount = totalAmount.subtract(charge.getAmount());
            } else if (plsAccessorial != null && StringUtils.isNotBlank(plsAccessorial.getPlsCode())) {
                LtlPricingAccessorialResult accessorial = new LtlPricingAccessorialResult();
                accessorial.setAccessorialType(plsAccessorial.getPlsCode());
                accessorial.setCarrierAccessorialCost(charge.getAmount());
                accessorial.setAccessorialDescription(plsAccessorial.getAccessorialType().getDescription());
                if (plsAccessorial.getAccessorialType().getAccessorialGroup() != null) {
                    accessorial.setAccessorialGroup(plsAccessorial.getAccessorialType().getAccessorialGroup().name());
                }
                charges.put(plsAccessorial.getPlsCode(), accessorial);
                initialCost = initialCost.subtract(charge.getAmount());
            }
        }

        result.setCharges(charges);

        // all accessorials which don't have mapping for 2.0 are added to initial cost
        result.setInitialCost(initialCost);
        result.setTotalCarrierCost(totalAmount);
    }

    private Map<String, LtlAccessorialsMappingEntity> getAccessorialsMap() {
        Map<String, LtlAccessorialsMappingEntity> accessorialsMap = mapping.get().stream()
                .collect(Collectors.toMap(LtlAccessorialsMappingEntity::getCarrierCode, Function.identity(), (a1, a2) -> a1));

        // If key contains comma, it is considered composite and should be split to simple keys.
        List<String> compositeKeys = accessorialsMap.keySet().stream().filter(key -> StringUtils.contains(key, ",")).collect(Collectors.toList());
        for (String key : compositeKeys) {
            LtlAccessorialsMappingEntity value = accessorialsMap.remove(key);
            Stream.of(key.split(",")).filter(StringUtils::isNotBlank).forEach(newKey -> accessorialsMap.put(newKey.trim(), value));
        }
        return accessorialsMap;
    }

    private void removeInvalidProfiles(LtlRatingVO ratingProfile) {
        Iterator<Entry<Long, List<CarrierRatingVO>>> iterator = ratingProfile.getCarrierPricingDetails().entrySet().iterator();
        while (iterator.hasNext()) {
            Optional<LtlRatingProfileVO> invalidProfile = iterator.next().getValue().stream().flatMap(r -> getPricingDetailsStream(r))
                    .filter(Objects::nonNull).filter(profile -> LtlRatingEngineServiceImpl.RATING_TYPE_API.equals(profile.getRatingCarrierType())
                            && profile.getDayAndRossResponse() == null)
                    .findAny();
            if (invalidProfile.isPresent()) {
                LOGGER.warn("No carrier API response found for following ProfileDetailId: {}", invalidProfile.get().getProfileDetailId());
                iterator.remove();
            }
        }
    }

    private boolean isCorrectDayAndRossResult(GetRate2Response response) {
        return Optional.ofNullable(response).map(GetRate2Response::getGetRate2Result)
                .map(ArrayOfServiceLevels::getServiceLevels)
                .flatMap(serviceLevels -> serviceLevels.stream().findFirst())
                .map(serviceLevel -> serviceLevel.getTotalAmount())
                .filter(amount -> amount != null && BigDecimal.ZERO.compareTo(amount) != 0).isPresent();
    }

    private Shipment getDayAndRossCriteria(GetOrderRatesCO ratesCO) {
        Shipment shipment = new Shipment();
        shipment.setShipperAddress(populateAddress(ratesCO.getOriginAddress()));
        shipment.setConsigneeAddress(populateAddress(ratesCO.getDestinationAddress()));
        shipment.setBillToAccount(billToAccount);
        shipment.setShipmentType(ShipmentType.QUOTE);
        shipment.setPaymentType(PaymentType.PREPAID);
        shipment.setMeasurementSystem(MeasurementSystem.IMPERIAL);
        shipment.setDivision(Division.GENERAL_FREIGHT);
        shipment.setItems(populateProducts(ratesCO));
        try {
            shipment.setSpecialServices(populateAccessorials(ratesCO));
        } catch (Exception e) {
            return null;
        }
        return shipment;
    }

    private ArrayOfShipmentSpecialService populateAccessorials(GetOrderRatesCO ratesCO) throws ApplicationException {
        List<String> accessorialTypes = ratesCO.getAccessorialTypes();
        ArrayOfShipmentSpecialService result = null;
        List<LtlAccessorialsMappingEntity> accessorialsMapping = ltlAccessorialsMappingDao.getAccessorialsMappingBySCAC(dayAndRossSCAC);
        mapping.set(accessorialsMapping);
        if (CollectionUtils.isNotEmpty(accessorialTypes)) {
            Map<String, LtlAccessorialsMappingEntity> accessorialsMap = accessorialsMapping.stream()
                    .collect(Collectors.toMap(LtlAccessorialsMappingEntity::getPlsCode, Function.identity(), (a1, a2) -> a1));
            result = new ArrayOfShipmentSpecialService();
            List<ShipmentSpecialService> shipmentSpecialServiceList = result.getShipmentSpecialService();

            for (String plsCode : accessorialTypes) {
                LtlAccessorialsMappingEntity mappingEntity = accessorialsMap.get(plsCode);
                if (mappingEntity != null && StringUtils.isNotBlank(getFirstCarrierCode(mappingEntity))) {
                    ShipmentSpecialService shipmentService = new ShipmentSpecialService();
                    shipmentService.setCode(getFirstCarrierCode(mappingEntity));
                    shipmentSpecialServiceList.add(shipmentService);
                } else if (mappingEntity == null || BooleanUtils.isNotTrue(mappingEntity.getDefaultAccessorial())) {
                    LOGGER.warn("No accessorial mapping found for '{}' pls accesoial.", plsCode);
                    throw new ApplicationException(
                            "Unable to find the appropriate DayAndRoss accessorial for " + plsCode + " pls accesoial.");
                }
            }
        }
        return result;
    }

    private String getFirstCarrierCode(LtlAccessorialsMappingEntity mappingEntity) {
        String carrierCode = mappingEntity.getCarrierCode();
        if (StringUtils.contains(carrierCode, ",")) {
            carrierCode = carrierCode.substring(0, carrierCode.indexOf(','));
        }
        return carrierCode;
    }

    private ArrayOfShipmentItem populateProducts(GetOrderRatesCO ratesCO) {
        ArrayOfShipmentItem items = new ArrayOfShipmentItem();
        List<ShipmentItem> shipmentItems = items.getShipmentItem();
        for (RateMaterialCO rateMaterial : ratesCO.getMaterials()) {
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setDescription("DummyProduct"); // TODO use real product description as for DayAndRoss API this field is required.
            shipmentItem.setHeight(toIntValue(rateMaterial.getHeight()));
            shipmentItem.setLength(toIntValue(rateMaterial.getLength()));
            shipmentItem.setLengthUnit(LengthUnit.INCHES);
            // shipmentItem.setPieces(Optional.ofNullable(rateMaterial.getPieces()).orElse(0).intValue());
            shipmentItem.setPieces(1); // always set 1 per request from Nick
            shipmentItem.setWeight(toIntValue(rateMaterial.getWeight()));
            shipmentItem.setWeightUnit(WeightUnit.POUNDS);
            shipmentItem.setWidth(toIntValue(rateMaterial.getWidth()));
            shipmentItems.add(shipmentItem);
        }
        return items;
    }

    private int toIntValue(BigDecimal bigDecimal) {
        return (bigDecimal != null) ? bigDecimal.intValue() : 0;
    }

    private ShipmentAddress populateAddress(AddressVO address) {
        ShipmentAddress result = null;
        if (address != null) {
            result = new ShipmentAddress();
            result.setCountry(countryDao.findShortCountryCode(address.getCountryCode()));
            result.setPostalCode(StringUtils.deleteWhitespace(address.getPostalCode()));
            result.setProvince(address.getStateCode());
            result.setCity(address.getCity());
            return result;
        }
        return result;
    }

}
