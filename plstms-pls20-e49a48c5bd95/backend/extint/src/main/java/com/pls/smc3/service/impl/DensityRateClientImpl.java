package com.pls.smc3.service.impl;

import com.pls.smc3.dao.SMCErrorCodeDao;
import com.pls.smc3.domain.SMCErrorCodeEntity;
import com.pls.smc3.dto.DensityDetailDTO;
import com.pls.smc3.dto.DensityRateResponseDTO;
import com.pls.smc3.dto.DensityRateShipmentRequestDTO;
import com.pls.smc3.dto.DensityRequestDetailDTO;
import com.pls.smc3.service.DensityRateClient;
import com.pls.smc3.validation.WebServiceValidationException;
import com.smc.density.web.ArrayOfDensityRequestDetail;
import com.smc.density.web.ArrayOfDensityResponseDetail;
import com.smc.density.web.DensityRateShipmentRequest;
import com.smc.density.web.DensityRateShipmentResponse;
import com.smc.density.web.DensityRequestDetail;
import com.smc.density.web.DensityResponseDetail;
import com.smc.webservices.AuthenticationToken;
import com.smc.webservices.DataModuleException;
import com.smc.webservices.RateWareWebServiceException;
import com.smc.webservices.RateWareXL;
import com.smc.webservices.RateWareXLPortType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementation class of {@link DensityRateClient}.
 * 
 * @author PAVANI CHALLA
 * 
 */
@Service
@Transactional
public class DensityRateClientImpl extends BaseSMC3ServiceImpl implements DensityRateClient {

    private static final int LENGTH_8 = 8;

    @Autowired
    private SMCErrorCodeDao smcErrorCodeDao;

    @Override
    public DensityRateResponseDTO getDensityRates(DensityRateShipmentRequestDTO requestDetail) throws WebServiceValidationException {

        RateWareXL service = new RateWareXL();

        RateWareXLPortType rateWareClient = service.getRateWareXLHttpPort();

        DensityRateResponseDTO resultDTO = null;

        DensityRateShipmentRequest densityRateShipmentRequest = transformDTOToServiceRequest(requestDetail);

        AuthenticationToken shipmentRateToken = getRatewareAuthToken();

        try {
            DensityRateShipmentResponse response = rateWareClient.densityRateShipment(densityRateShipmentRequest, shipmentRateToken);
            resultDTO = transformToResponse(response);
        } catch (RateWareWebServiceException rateWareException) {

            throw new WebServiceValidationException(rateWareException.getMessage());

        } catch (DataModuleException dataException) {

            throw new WebServiceValidationException(dataException.getMessage());
        }

        return resultDTO;
    }

    /**
     * Converts the response from the SMC service to DTO object.
     * 
     * @param response
     *            DensityRateShipmentResponse.
     * @return {@link DensityRateResponseDTO}
     * @throws WebServiceValidationException
     *             validation exceptions
     */
    private DensityRateResponseDTO transformToResponse(DensityRateShipmentResponse response) throws WebServiceValidationException {

        DensityRateResponseDTO responseDTO = new DensityRateResponseDTO();
        responseDTO.setErrorCode(response.getErrorCode());

        if (!"0".equals(responseDTO.getErrorCode())) {
            SMCErrorCodeEntity errorEntity = smcErrorCodeDao.getErrorCode(response.getErrorCode());
            responseDTO.setErrorEntity(errorEntity);
            throw new WebServiceValidationException(errorEntity.getDescription());
        }

        responseDTO.setDestinationCity(response.getDestinationCity());
        responseDTO.setDestinationCountry(response.getDestinationCountry());
        responseDTO.setDestinationPostalCode(response.getDestinationPostalCode());
        responseDTO.setDestinationState(response.getDestinationState());
        responseDTO.setEffectiveDate(response.getEffectiveDate());
        responseDTO.setLhGrossCharge(response.getLHGrossCharge());
        responseDTO.setMinimumCharge(response.getMinimumCharge());
        responseDTO.setOriginCity(response.getOriginCity());
        responseDTO.setOriginCountry(response.getOriginCountry());
        responseDTO.setOriginPostalCode(response.getOriginPostalCode());
        responseDTO.setOriginState(response.getOriginState());
        responseDTO.setTariffName(response.getTariffName());
        responseDTO.setTotalCharge(response.getTotalCharge());
        responseDTO.setShipmentDateCCYYMMDD(response.getShipmentDateCCYYMMDD());

        List<DensityDetailDTO> details = new ArrayList<DensityDetailDTO>();

        ArrayOfDensityResponseDetail responseDetails = response.getDetails();

        DensityDetailDTO detailDTO = null;
        for (DensityResponseDetail eachDetail : responseDetails.getDensityResponseDetail()) {

            detailDTO = new DensityDetailDTO();
            detailDTO.setCharge(eachDetail.getCharge());
            detailDTO.setDensity(eachDetail.getDensity());
            detailDTO.setDimensionUnits(eachDetail.getDimensionUnits());
            detailDTO.setError(eachDetail.getError());
            detailDTO.setHeight(eachDetail.getHeight());
            detailDTO.setLength(eachDetail.getLength());
            detailDTO.setPieces(eachDetail.getPieces());
            detailDTO.setRate(eachDetail.getRate());
            detailDTO.setUsedWeight(eachDetail.getUsedWeight());
            detailDTO.setWeight(eachDetail.getWeight());
            detailDTO.setWeightUnits(eachDetail.getWeightUnits());
            detailDTO.setWidth(eachDetail.getWidth());
            details.add(detailDTO);
        }

        responseDTO.setDetails(details);

        return responseDTO;
    }

    /**
     * This converts the DTO request to SMC request.
     * 
     * @param requestDTO
     *            {@link DensityRateShipmentRequestDTO}
     * @return DensityRateShipmentRequest. Request of SMC service.
     * @throws WebServiceValidationException
     *             validation exception.
     */
    public DensityRateShipmentRequest transformDTOToServiceRequest(DensityRateShipmentRequestDTO requestDTO) throws WebServiceValidationException {

        validateRequiredFields(requestDTO);
        validateTariffName(requestDTO);

        DensityRateShipmentRequest densityRateShipmentRequest = new DensityRateShipmentRequest();

        densityRateShipmentRequest.setOriginPostalCode(requestDTO.getOriginPostalCode());
        densityRateShipmentRequest.setOriginCountry(requestDTO.getOriginCountry());
        densityRateShipmentRequest.setOriginCity(requestDTO.getOriginCity());
        densityRateShipmentRequest.setOriginState(requestDTO.getOriginState());

        densityRateShipmentRequest.setDestinationPostalCode(requestDTO.getDestinationPostalCode());
        densityRateShipmentRequest.setDestinationCountry(requestDTO.getDestinationCountry());

        densityRateShipmentRequest.setDestinationCity(requestDTO.getDestinationCity());
        densityRateShipmentRequest.setDestinationState(requestDTO.getDestinationState());

        densityRateShipmentRequest.setTariffName(requestDTO.getTariffName());
        densityRateShipmentRequest.setShipmentDateCCYYMMDD(convertDateToString(requestDTO.getShipmentDateCCYYMMDD()));

        densityRateShipmentRequest.setDetailType(requestDTO.getDetailType());

        DensityRequestDetail detail;
        ArrayOfDensityRequestDetail arrayRequestDetail = new ArrayOfDensityRequestDetail();

        for (DensityRequestDetailDTO dto : requestDTO.getDetails()) {

            detail = new DensityRequestDetail();
            detail.setLength(dto.getLength());
            detail.setWidth(dto.getWidth());
            detail.setHeight(dto.getHeight());
            detail.setDimensionUnits(dto.getDimensionUnits());
            detail.setWeight(dto.getWeight());
            detail.setWeightUnits(dto.getWeightUnits());
            arrayRequestDetail.getDensityRequestDetail().add(detail);
        }

        densityRateShipmentRequest.setDetails(arrayRequestDetail);

        return densityRateShipmentRequest;
    }

    /**
     * Util method to convert date to string.
     * 
     * @param input
     *            {@link Date}.
     * @return {@link String}.
     */
    public String convertDateToString(Date input) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
        format.setLenient(false);
        return format.format(input);
    }

    /**
     * Validates if request has all the required fields or not.
     * 
     * @param shipmentDTO
     *            DensityRateShipmentRequestDTO.
     * 
     * @return boolean
     * 
     * @throws WebServiceValidationException
     *             validation exception.
     */
    public boolean validateRequiredFields(DensityRateShipmentRequestDTO shipmentDTO) throws WebServiceValidationException {

        if (shipmentDTO.getShipmentDateCCYYMMDD() == null) {
            throw new WebServiceValidationException("Shipment Date is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getOriginPostalCode())) {
            throw new WebServiceValidationException("Origin Postal Code is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getDestinationPostalCode())) {
            throw new WebServiceValidationException("Destination Postal Code is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getOriginCountry())) {
            throw new WebServiceValidationException("Origin Country is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getDestinationCountry())) {
            throw new WebServiceValidationException("Destination Country is required.");
        }
        if (StringUtils.isBlank(shipmentDTO.getDetailType())) {
            throw new WebServiceValidationException("Detail Type is required. Like LWH, DENSITY or CUBE.");
        }
        if (null == shipmentDTO.getDetails() || shipmentDTO.getDetails().isEmpty()) {
            throw new WebServiceValidationException("Request Detail is required.");
        }
        return true;
    }

    /**
     * Validates Tariff Name.
     * 
     * @param shipmentDTO
     *            {@link DensityRateShipmentRequestDTO}.
     * @return boolean.
     * @throws WebServiceValidationException
     *             validation exception.
     */
    public boolean validateTariffName(DensityRateShipmentRequestDTO shipmentDTO) throws WebServiceValidationException {

        if (StringUtils.isBlank(shipmentDTO.getTariffName())) {
            throw new WebServiceValidationException("Tariff Name is required.");
        }
        if (StringUtils.length(shipmentDTO.getTariffName()) < LENGTH_8) {
            throw new WebServiceValidationException("Minimum 8 characters required for tariff Name.");
        }
        return true;
    }

}
