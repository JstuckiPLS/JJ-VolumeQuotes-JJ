package com.pls.smc3.service.impl;

import com.pls.smc3.dao.SMCErrorCodeDao;
import com.pls.smc3.domain.SMCErrorCodeEntity;
import com.pls.smc3.dto.LTLDetailDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;
import com.pls.smc3.service.LtlRateShipmentClient;
import com.pls.smc3.validation.WebServiceValidationException;
import com.smc.ltl.web.ArrayOfLTLRateShipmentRequest;
import com.smc.ltl.web.ArrayOfLTLRateShipmentResponse;
import com.smc.ltl.web.ArrayOfLTLRequestDetail;
import com.smc.ltl.web.LTLRateShipmentRequest;
import com.smc.ltl.web.LTLRateShipmentResponse;
import com.smc.ltl.web.LTLRequestDetail;
import com.smc.ltl.web.LTLResponseDetail;
import com.smc.webservices.AuthenticationToken;
import com.smc.webservices.RateWareXL;
import com.smc.webservices.RateWareXLPortType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation class of {@link LtlRateShipmentClient}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
public class LtlRateShipmentClientImpl extends BaseSMC3ServiceImpl implements LtlRateShipmentClient {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(LtlRateShipmentClientImpl.class);
    private static final Pattern ID_PATTERN = Pattern.compile("(\\d+).*");

    private static final BigDecimal[] WEIGHT_BREAKS = new BigDecimal[]{
            new BigDecimal(500),
            new BigDecimal(1000),
            new BigDecimal(2000),
            new BigDecimal(5000),
            new BigDecimal(10000),
            new BigDecimal(20000),
            new BigDecimal(30000),
            new BigDecimal(40000)
    };

    @Autowired
    private SMCErrorCodeDao smcErrorCodeDao;

    private static List<String> supportedCountries = Arrays.asList("USA", "CAN", "MEX");

    private RateWareXLPortType smc3Service;
    private AuthenticationToken authenticationToken;

    /**
     * Init method.
     */
    @PostConstruct
    protected void init() {
        RateWareXL service = new RateWareXL();
        smc3Service = service.getRateWareXLHttpPort();
        authenticationToken = getRatewareAuthToken();
    }

    @Override
    public LTLRateShipmentDTO getLtlRateShipment(LTLRateShipmentDTO requestDTO) throws Exception {
        LTLRateShipmentRequest request = buildRequest(requestDTO);
        LTLRateShipmentResponse response = smc3Service.ltlRateShipment(request, authenticationToken);
        return buildResponseDTOFromSMC3Response(response);
    }

    @Override
    public LTLRateShipmentDTO getLtlRateShipmentMultiple(LTLRateShipmentDTO requestDTO) throws Exception {
        ArrayOfLTLRateShipmentRequest request = buildMultipleShipmentRequest(requestDTO);
        ArrayOfLTLRateShipmentResponse responseArray = smc3Service.ltlRateShipmentMultiple(request, authenticationToken);

        List<LTLRateShipmentDTO> responses = responseArray.getLTLRateShipmentResponse().stream().map(this::buildResponseDTOFromSMC3Response)
                .collect(Collectors.toList());
        return getResult(responses);
    }

    private LTLRateShipmentDTO getResult(List<LTLRateShipmentDTO> responses) {
        LTLRateShipmentDTO finalRespWithCorrectWtBreak = null;
        LTLRateShipmentDTO finalRespWithNextWtBreak = null;
        LTLRateShipmentDTO finalRespInclDeficitWeight = null;
        for (LTLRateShipmentDTO response : responses) {
            if ("ACT".equalsIgnoreCase(response.getShipmentID())) {
                finalRespWithCorrectWtBreak = response;
            } else if ("NXT".equalsIgnoreCase(response.getShipmentID())) {
                finalRespWithNextWtBreak = response;
            } else {
                finalRespInclDeficitWeight = response;
            }
        }

        if (finalRespWithCorrectWtBreak != null
                && finalRespInclDeficitWeight != null
                && finalRespWithCorrectWtBreak.getTotalChargeFromDetails().doubleValue() >= finalRespInclDeficitWeight
                        .getTotalChargeFromDetails().doubleValue()) {
            return finalRespWithNextWtBreak;
        }
        return finalRespWithCorrectWtBreak;
    }

    private LTLRateShipmentDTO buildResponseDTOFromSMC3Response(LTLRateShipmentResponse result) {
        LTLRateShipmentDTO responseShipmentDTO = new LTLRateShipmentDTO();
        responseShipmentDTO.setShipmentDateCCYYMMDD(result.getShipmentDateCCYYMMDD());
        responseShipmentDTO.setShipmentID(StringUtils.right(result.getShipmentID(), 3));
        responseShipmentDTO.setId(getId(result.getShipmentID()));
        responseShipmentDTO.setTariffName(result.getTariffName());
        responseShipmentDTO.setTotalCharge(result.getTotalCharge());
        responseShipmentDTO.setErrorCode(result.getErrorCode());
        responseShipmentDTO.setDestinationCountry(result.getDestinationCountry());
        responseShipmentDTO.setOriginCountry(result.getOriginCountry());
        responseShipmentDTO.setOriginPostalCode(result.getOriginPostalCode());
        responseShipmentDTO.setOriginCity(result.getOriginCity());
        responseShipmentDTO.setOriginState(result.getOriginState());
        responseShipmentDTO.setDestinationCity(result.getDestinationCity());
        responseShipmentDTO.setDestinationState(result.getDestinationState());
        responseShipmentDTO.setDestinationPostalCode(result.getDestinationPostalCode());
        responseShipmentDTO.setEffectiveDate(result.getEffectiveDate());
        responseShipmentDTO.setDeficitCharge(toBigDecimal(result.getDeficitCharge()));
        responseShipmentDTO.setDeficitWeight(toBigDecimal(result.getDeficitWeight()));
        responseShipmentDTO.setDeficitRate(toBigDecimal(result.getDeficitRate()));
        responseShipmentDTO.setActualWeight(toBigDecimal(result.getActualWgt()));
        responseShipmentDTO.setBilledWeight(toBigDecimal(result.getBilledWgt()));
        responseShipmentDTO.setMinimumCharge(toBigDecimal(result.getMinimumCharge()));

        if (!"0".equals(responseShipmentDTO.getErrorCode())) {
            SMCErrorCodeEntity errorEntity = smcErrorCodeDao.getErrorCode(result.getErrorCode());
            responseShipmentDTO.setErrorCodeEntity(errorEntity);
        }

        responseShipmentDTO.setDetails(result.getDetails().getLTLResponseDetail().stream().map(this::getDetailDTO).collect(Collectors.toList()));
        responseShipmentDTO.setTotalChargeFromDetails(result.getDetails().getLTLResponseDetail().stream().map(LTLResponseDetail::getCharge)
                .filter(StringUtils::isNotBlank).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add));
        responseShipmentDTO.setDeficitNmfcClass(result.getDetails().getLTLResponseDetail().stream()
                .filter(detail -> detail.getRate().equals(result.getDeficitRate())).findFirst().orElse(new LTLResponseDetail()).getNmfcClass());
        return responseShipmentDTO;
    }

    private LTLDetailDTO getDetailDTO(LTLResponseDetail detail) {
        LTLDetailDTO detailDTO = new LTLDetailDTO();
        detailDTO.setNmfcClass(detail.getNmfcClass());
        detailDTO.setRate(detail.getRate());
        detailDTO.setWeight(detail.getWeight());
        detailDTO.setCharge(detail.getCharge());
        detailDTO.setError(detail.getError());
        return detailDTO;
    }

    private BigDecimal toBigDecimal(String strValue) {
        return strValue != null ? new BigDecimal(strValue) : BigDecimal.ZERO;
    }

    private static Integer getId(String shipmentID) {
        Matcher matcher = ID_PATTERN.matcher(shipmentID);
        if (matcher.matches()) {
            return Integer.valueOf(matcher.group(1));
        }
        return null;
    }

    private ArrayOfLTLRateShipmentRequest buildMultipleShipmentRequest(LTLRateShipmentDTO requestDTO) throws WebServiceValidationException {
        validateShipmentRequest(requestDTO);

        ArrayOfLTLRateShipmentRequest ltlRateShipmentMultipleReq = new ArrayOfLTLRateShipmentRequest();
        ltlRateShipmentMultipleReq.getLTLRateShipmentRequest().add(createLTLRateShipmentRequest(requestDTO, false, false));
        ltlRateShipmentMultipleReq.getLTLRateShipmentRequest().add(createLTLRateShipmentRequest(requestDTO, true, true));
        ltlRateShipmentMultipleReq.getLTLRateShipmentRequest().add(createLTLRateShipmentRequest(requestDTO, false, true));
        return ltlRateShipmentMultipleReq;
    }

    private LTLRateShipmentRequest createLTLRateShipmentRequest(LTLRateShipmentDTO requestDTO, boolean addDeficitWeightDtlObj,
                                                                boolean useHigherWtBreak) {
        LTLRateShipmentRequest request = new LTLRateShipmentRequest();
        request.setShipmentID(getShipmentId(addDeficitWeightDtlObj, useHigherWtBreak, requestDTO.getId()));
        request.setOriginPostalCode(requestDTO.getOriginPostalCode());
        request.setOriginCountry(requestDTO.getOriginCountry());
        request.setOriginCity(requestDTO.getOriginCity());
        request.setOriginState(requestDTO.getOriginState());
        request.setDestinationCity(requestDTO.getDestinationCity());
        request.setDestinationState(requestDTO.getDestinationState());
        request.setDestinationPostalCode(requestDTO.getDestinationPostalCode());
        request.setDestinationCountry(requestDTO.getDestinationCountry());
        request.setTariffName(requestDTO.getTariffName());
        request.setShipmentDateCCYYMMDD(convertDateToString(requestDTO.getShipmentDate()));
        setWeightBreakDiscount(requestDTO, request, useHigherWtBreak);

        request.setDetails(getDetailsForLTLRateShipmentRequest(requestDTO, addDeficitWeightDtlObj));

        return request;
    }

    private ArrayOfLTLRequestDetail getDetailsForLTLRateShipmentRequest(LTLRateShipmentDTO requestDTO, boolean addDeficitWeightDtlObj) {
        ArrayOfLTLRequestDetail arrayRequestDetail = new ArrayOfLTLRequestDetail();

        for (LTLDetailDTO requestDetail : requestDTO.getDetails()) {
            LTLRequestDetail detail = new LTLRequestDetail();
            detail.setWeight(requestDetail.getWeight());
            detail.setNmfcClass(requestDetail.getNmfcClass());
            arrayRequestDetail.getLTLRequestDetail().add(detail);
        }

        if (addDeficitWeightDtlObj) {
            LTLRequestDetail detail = new LTLRequestDetail();
            BigDecimal totalWeight = getNextWeightBreakMinWeight(requestDTO.getTotalWeight());
            BigDecimal deficitWeight = totalWeight.subtract(requestDTO.getTotalWeight());
            detail.setWeight(deficitWeight.toString());
            detail.setNmfcClass(requestDTO.getDetails().stream().map(LTLDetailDTO::getNmfcClass).map(BigDecimal::new).min(BigDecimal::compareTo)
                    .get().toString());
            arrayRequestDetail.getLTLRequestDetail().add(detail);
        }
        return arrayRequestDetail;
    }

    private String getShipmentId(boolean addDeficitWeightDtlObj, boolean useHigherWtBreak, Integer requestId) {
        String shipmentId = null;
        if (!addDeficitWeightDtlObj && !useHigherWtBreak) {
            shipmentId = ObjectUtils.toString(requestId) + "ACT";
        } else if (addDeficitWeightDtlObj && useHigherWtBreak) {
            shipmentId = ObjectUtils.toString(requestId) + "DEF";
        } else if (!addDeficitWeightDtlObj && useHigherWtBreak) {
            shipmentId = ObjectUtils.toString(requestId) + "NXT";
        }
        return shipmentId;
    }

    private LTLRateShipmentRequest buildRequest(LTLRateShipmentDTO requestDTO) throws WebServiceValidationException {
        validateShipmentRequest(requestDTO);

        LTLRateShipmentRequest simpleRequest = new LTLRateShipmentRequest();

        simpleRequest.setOriginPostalCode(requestDTO.getOriginPostalCode());
        simpleRequest.setOriginCountry(requestDTO.getOriginCountry());
        simpleRequest.setOriginCity(requestDTO.getOriginCity());
        simpleRequest.setOriginState(requestDTO.getOriginState());
        simpleRequest.setDestinationCity(requestDTO.getDestinationCity());
        simpleRequest.setDestinationState(requestDTO.getDestinationState());
        simpleRequest.setDestinationPostalCode(requestDTO.getDestinationPostalCode());
        simpleRequest.setDestinationCountry(requestDTO.getDestinationCountry());
        simpleRequest.setTariffName(requestDTO.getTariffName());
        simpleRequest.setShipmentID(String.valueOf(requestDTO.getId()));
        simpleRequest.setShipmentDateCCYYMMDD(convertDateToString(requestDTO.getShipmentDate()));
        setWeightBreakDiscount(requestDTO, simpleRequest, false);

        LTLRequestDetail simpleRequestDetail = null;
        ArrayOfLTLRequestDetail arrayRequestDetail = new ArrayOfLTLRequestDetail();

        for (LTLDetailDTO requestDetail : requestDTO.getDetails()) {
            simpleRequestDetail = new LTLRequestDetail();
            simpleRequestDetail.setWeight(requestDetail.getWeight());
            simpleRequestDetail.setNmfcClass(requestDetail.getNmfcClass());
            arrayRequestDetail.getLTLRequestDetail().add(simpleRequestDetail);
        }
        simpleRequest.setDetails(arrayRequestDetail);
        return simpleRequest;
    }

    /**
     * 
     * Validates if request has all the required fields or not.
     * 
     * @param requestDTO
     *            {@link LTLRateShipmentDTO}.
     * @return boolean
     * 
     * @throws WebServiceValidationException
     *             validation exception.
     */
    private boolean validateRequiredFields(LTLRateShipmentDTO requestDTO) throws WebServiceValidationException {

        if (requestDTO.getShipmentDate() == null) {
            throw new WebServiceValidationException("Shipment Date is required.");
        }
        if (StringUtils.isBlank(requestDTO.getOriginPostalCode())) {
            throw new WebServiceValidationException("Origin Postal Code is required.");
        }
        if (StringUtils.isBlank(requestDTO.getDestinationPostalCode())) {
            throw new WebServiceValidationException("Destination Postal Code is required.");
        }
        if (StringUtils.isBlank(requestDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Origin Country is required.");
        }
        if (StringUtils.isBlank(requestDTO.getDestinationCountry())) {
            throw new WebServiceValidationException("Destination Country is required.");
        }
        if (null == requestDTO.getDetails() || requestDTO.getDetails().isEmpty()) {
            throw new WebServiceValidationException("LTL Request Detail is required.");
        }
        return true;
    }

    /**
     * Validates Tariff Name.
     * 
     * @param shipmentDTO
     *            {@link LTLRateShipmentDTO}.
     * @return boolean.
     * @throws WebServiceValidationException
     *             validation exception.
     */
    private boolean validateTariffName(LTLRateShipmentDTO shipmentDTO) throws WebServiceValidationException {

        if (StringUtils.isBlank(shipmentDTO.getTariffName())) {
            throw new WebServiceValidationException("Tariff Name is required.");
        }
        return true;
    }

    private void validateShipmentRequest(LTLRateShipmentDTO requestDTO) throws WebServiceValidationException {

        if (requestDTO == null) {
            throw new WebServiceValidationException("Empty requestDTO");
        }
        validateTariffName(requestDTO);
        validateRequiredFields(requestDTO);

        if (!validateCountry(requestDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Invalid Origin Country. Supported countries are USA, MEX and CAN.");
        }
        if (!validateCountry(requestDTO.getDestinationCountry())) {

            throw new WebServiceValidationException(
                    "Invalid Destination Country. Supported countries are USA, MEX and CAN.");
        }

    }

    /**
     * Validates the allowed country.
     * 
     * @param countryCode
     *            {@link String}.
     * @return boolean.
     */
    private boolean validateCountry(String countryCode) {

        return supportedCountries.contains(countryCode);
    }

    /**
     * Enter the discount amount to be applied to each weight break during the rating process. Weight breaks are
     *
     * governed by each individual tariff, but generally reflect the standard weight break structure below:
     * Weight break 1 = L5C - Applicable on LTL shipments weighing less than 500 lbs.
     * Weight break 2 = M5C - Applicable on LTL shipments weighing 500 lbs. or more, but less than 1,000 lbs.
     * Weight break 3 = M1M - Applicable on LTL shipments weighing 1,000 lbs. or more, but less than 2,000 lbs.
     * Weight break 4 = M2M - Applicable on LTL shipments weighing 2,000 lbs. or more, but less than 5,000 lbs.
     * Weight break 5 = M5M - Applicable on LTL shipments weighing 5,000 lbs. or more, but less than 10,000 lbs.
     * Weight break 6 = M10M - Applicable on LTL shipments weighing 10,000 lbs. or more, but less than 20,000 lbs.
     * Weight break 7 = M20M - Applicable on TL shipments weighing 20,000 lbs. or more, but less than 30,000 lbs.
     * Weight break 8 = M30M - Applicable on TL shipments weighing 30,000 lbs. or more, but less than 40,000 lbs.
     * Weight break 9 = M40M - Applicable on TL shipments weighing 40,000 lbs. or more.
     */
    private void setWeightBreakDiscount(LTLRateShipmentDTO requestDTO, LTLRateShipmentRequest request, boolean useHigherWtBreak) {
        if (requestDTO.getDiscountPercent() == null) {
            return;
        }
        int index = getIndexOfWeightBreak(requestDTO.getTotalWeight());
        index++;
        if (useHigherWtBreak && index < 9) {
            index++;
        }
        try {
            Field t = LTLRateShipmentRequest.class.getDeclaredField("weightBreakDiscount" + index);
            t.setAccessible(true);
            t.set(request, requestDTO.getDiscountPercent());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private int getIndexOfWeightBreak(BigDecimal weight) {
        Optional<BigDecimal> weightBreak = Stream.of(WEIGHT_BREAKS).filter(item -> weight.compareTo(item) < 0).findFirst();
        return weightBreak.isPresent() ? ArrayUtils.indexOf(WEIGHT_BREAKS, weightBreak.get()) : WEIGHT_BREAKS.length;
    }

    private BigDecimal getNextWeightBreakMinWeight(BigDecimal weight) {
        int index = getIndexOfWeightBreak(weight);
        return index == WEIGHT_BREAKS.length ? WEIGHT_BREAKS[WEIGHT_BREAKS.length - 1] : WEIGHT_BREAKS[index];
    }
}
