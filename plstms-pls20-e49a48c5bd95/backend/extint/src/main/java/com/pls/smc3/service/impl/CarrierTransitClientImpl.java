package com.pls.smc3.service.impl;

import com.pls.core.shared.AddressVO;
import com.pls.smc3.dto.ScacRequest;
import com.pls.smc3.dto.ScacResponse;
import com.pls.smc3.dto.TransitRequestDTO;
import com.pls.smc3.dto.TransitResponseDTO;
import com.pls.smc3.service.CarrierTransitClient;
import com.pls.smc3.validation.WebServiceValidationException;
import com.smc.carrierconnect.webservice.enums.Country;
import com.smc.carrierconnect.webservice.enums.Method;
import com.smc.carrierconnect.webservice.objects.enums.ServiceTypes;
import com.smc.carrierconnectxl.webservice.enums.ApiVersion;
import com.smc.carrierconnectxl.webservice.enums.Status;
import com.smc.carrierconnectxl.webservice.pojos.CarrierService;
import com.smc.carrierconnectxl.webservice.pojos.Location;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLPortTypeV3;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLV3;
import com.smc.carrierconnectxl.webservice.v3.Transit;
import com.smc.carrierconnectxl.webservice.v3.TransitResponse;
import com.smc.carrierconnectxl.webservice.v3.transit.TransitRequest;
import com.smc.carrierconnectxl.webservice.v3.transit.TransitResult;
import com.smc.webservices.AuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation class of {@link CarrierTransitClient}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
public class CarrierTransitClientImpl extends BaseSMC3ServiceImpl implements CarrierTransitClient {

    private static List<String> supportedCountries = Arrays.asList("USA", "CAN", "MEX");

    private CarrierConnectXLPortTypeV3 portType;
    private AuthenticationToken authenticationToken;

    /**
     * Init method.
     */
    @PostConstruct
    protected void init() {
        CarrierConnectXLV3 service = new CarrierConnectXLV3();
        portType = service.getCarrierConnectXLHttpPortV3();
        authenticationToken = getCarrierConnectAuthToken();
    }

    @Override
    public TransitResponseDTO getTransitInformation(TransitRequestDTO requestDTO) {
        try {
            validateRequiredFields(requestDTO);

            Transit transitRequest = buildRequest(requestDTO);
            TransitResponse response = portType.transit(transitRequest, authenticationToken, ApiVersion.V_3_1);
            TransitResponseDTO responseDTO = transformResponseToDTO(response);

            return responseDTO;
        } catch (WebServiceValidationException wsve) {
            return null;
        }
    }

    private Transit buildRequest(TransitRequestDTO requestDTO) {

        Transit transit = new Transit();
        TransitRequest request = new TransitRequest();
        for (ScacRequest scacRequest:requestDTO.getScacRequests()) {
            CarrierService carrierService = new CarrierService();
            carrierService.setSCAC(scacRequest.getScac());
            carrierService.setServiceMethod(Method.LTL);
            carrierService.setServiceType(ServiceTypes.STANDARD);
            request.getCarriers().add(carrierService);
        }

        request.setShipmentId(requestDTO.getShipmentId());
        request.setDestination(getLocationFromAddressVO(requestDTO.getDestination()));
        request.setOrigin(getLocationFromAddressVO(requestDTO.getOrigin()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        request.setPickupDate(simpleDateFormat.format(requestDTO.getPickupDate()));
        transit.setTransitRequest(request);
        return transit;

    }

    private Location getLocationFromAddressVO(AddressVO address) {
        if (address != null) {

            Location locale = new Location();

            locale.setAddress1(address.getAddress1());
            locale.setAddress2(address.getAddress2());
            locale.setCity(address.getCity());
            locale.setCountryCode(Country.fromValue(address.getCountryCode()));

            if (address.getPostalCode().contains("-")) {
                String[] codes = spiltPostalCode(address.getPostalCode());

                locale.setPostalCode(codes[0]);
            } else {
                locale.setPostalCode(address.getPostalCode());
            }
            locale.setStateProvince(address.getStateCode());
            return locale;
        }
        return null;
    }

    private AddressVO getAddressVOFromLocation(Location address) {
        if (address != null) {

            AddressVO addressVO = new AddressVO();
            addressVO.setAddress1(address.getAddress1());
            addressVO.setAddress2(address.getAddress2());
            addressVO.setCity(address.getCity());
            addressVO.setCountryCode(address.getCountryCode().value());

            addressVO.setPostalCode(address.getPostalCode());

            addressVO.setStateCode(address.getStateProvince());
            return addressVO;
        }
        return null;
    }

    /**
     * Method to extract the postal code extension if any. Expected format is NNNNN-NNNN. Postal code
     * extension is not a must have.
     * 
     * @param postalCode
     *            PostalCode in format NNNNN-NNNN
     * @return String array where [0] - will give the postal code and [1] -- will be the postal code extension
     */
    private String[] spiltPostalCode(String postalCode) {

        return StringUtils.split(postalCode, "-");

    }

    private TransitResponseDTO transformResponseToDTO(TransitResponse response) {

        TransitResponseDTO responseDTO = new TransitResponseDTO();
        List<ScacResponse> scacs = new ArrayList<ScacResponse>();
        responseDTO.setDestination(getAddressVOFromLocation(response.getTransitResponse().getDestination()));
        responseDTO.setOrigin(getAddressVOFromLocation(response.getTransitResponse().getOrigin()));
        List<ScacResponse> scacResponses = new ArrayList<>();
        for (TransitResult result:response.getTransitResponse().getCarriers()) {
            if (Status.FAIL.equals(result.getMessageStatus().getStatus()))
                continue;
            ScacResponse scacResponse = new ScacResponse();
            scacResponse.setDays(result.getTransitDays());
            scacResponse.setDestinationServiceType(result.getServiceDetail().getDestination().value());
            scacResponse.setOriginServiceType(result.getServiceDetail().getOrigin().value());
            scacResponse.setMethod(result.getCarrierServiceDetail().getServiceMethod().value());
            scacResponse.setErrorCode(result.getMessageStatus().getCode());
            scacResponse.setName(result.getCarrierServiceDetail().getCarrierName());
            scacResponse.setScac(result.getCarrierServiceDetail().getSCAC());
            scacResponses.add(scacResponse);
        }
        responseDTO.setScacResponses(scacResponses);
        return responseDTO;
    }

    /**
     * Validates the request to ensure the required fields are available.
     * 
     * @param requestDTO
     *            {@link TransitRequestDTO} request object.
     * @throws WebServiceValidationException
     *             Validation exception.
     */
    public void validateRequiredFields(TransitRequestDTO requestDTO) throws WebServiceValidationException {

        if (requestDTO == null) {
            throw new WebServiceValidationException("Request is null.");
        }

        if (requestDTO.getScacRequests() == null || requestDTO.getScacRequests().isEmpty()) {

            throw new WebServiceValidationException("ScacRequest is required.");
        }
        for (ScacRequest scacRequest : requestDTO.getScacRequests()) {
            if (scacRequest.getMethod() == null) {
                throw new WebServiceValidationException("Method is required.");
            }
        }
        validateDestinationAddress(requestDTO);
        validateOriginAddress(requestDTO);
    }

    private void validateDestinationAddress(TransitRequestDTO requestDTO) throws WebServiceValidationException {
        if (requestDTO.getDestination() == null) {
            throw new WebServiceValidationException(
                    "Destination address not available. Atleast country and Postal code required.");
        }

        if (requestDTO.getDestination().getCountryCode() == null) {
            throw new WebServiceValidationException("Country code is required.");
        }

        if (!validateCountry(requestDTO.getDestination().getCountryCode())) {

            throw new WebServiceValidationException("Country code not supported.");
        }

        if (requestDTO.getDestination().getPostalCode() == null) {
            throw new WebServiceValidationException("Postal code is required.");
        }
    }

    private void validateOriginAddress(TransitRequestDTO requestDTO) throws WebServiceValidationException {
        if (requestDTO.getOrigin() == null) {
            throw new WebServiceValidationException(
                    "Origin address not available. Atleast country and Postal code required.");
        }

        if (requestDTO.getOrigin().getCountryCode() == null) {
            throw new WebServiceValidationException("Country code is required.");
        }

        if (!validateCountry(requestDTO.getOrigin().getCountryCode())) {

            throw new WebServiceValidationException("Country code not supported.");
        }

        if (requestDTO.getOrigin().getPostalCode() == null) {
            throw new WebServiceValidationException("Postal code is required.");
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
}
