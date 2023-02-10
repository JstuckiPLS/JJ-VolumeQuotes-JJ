package com.pls.smc3.service.impl;

import com.pls.core.shared.AddressVO;
import com.pls.smc3.dto.CarrierTerminalDetailDTO;
import com.pls.smc3.dto.ShipmentAddressDTO;
import com.pls.smc3.dto.TerminalDetailsDTO;
import com.pls.smc3.dto.TerminalResponseDetailDTO;
import com.pls.smc3.service.TerminalDetailsClient;
import com.smc.carrierconnect.webservice.enums.Country;
import com.smc.carrierconnect.webservice.enums.Method;
import com.smc.carrierconnect.webservice.enums.TerminalType;
import com.smc.carrierconnect.webservice.objects.enums.ServiceTypes;
import com.smc.carrierconnectxl.webservice.enums.ApiVersion;
import com.smc.carrierconnectxl.webservice.pojos.CarrierTerminalResult;
import com.smc.carrierconnectxl.webservice.pojos.CarrierTerminals;
import com.smc.carrierconnectxl.webservice.pojos.Terminal;
import com.smc.carrierconnectxl.webservice.pojos.TerminalByPostalCode;
import com.smc.carrierconnectxl.webservice.pojos.TerminalDetail;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLPortTypeV3;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLV3;
import com.smc.carrierconnectxl.webservice.v3.TerminalByTerminalCode;
import com.smc.carrierconnectxl.webservice.v3.TerminalByTerminalCodeResponse;
import com.smc.carrierconnectxl.webservice.v3.TerminalsByPostalCode;
import com.smc.carrierconnectxl.webservice.v3.TerminalsByPostalCodeResponse;
import com.smc.carrierconnectxl.webservice.v3.terminalbyterminalcode.TerminalByTerminalCodeRequest;
import com.smc.carrierconnectxl.webservice.v3.terminalsbypostalcode.TerminalsByPostalCodeRequest;
import com.smc.webservices.AuthenticationToken;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * 
 * Implementation class of {@link TerminalDetailsClient}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
public class TerminalDetailsClientImpl extends BaseSMC3ServiceImpl implements TerminalDetailsClient {

    @Override
    public List<TerminalResponseDetailDTO> getTerminalsByTerminalCode(List<TerminalDetailsDTO> terminalDetails) throws Exception {
        TerminalByTerminalCode terminalParameters = getTerminalsByTerminalCodeParameters(terminalDetails);

        CarrierConnectXLPortTypeV3 portType = getServicePortType();
        AuthenticationToken authenticationToken = getCarrierConnectAuthToken();
        TerminalByTerminalCodeResponse terminalResponse = portType.terminalByTerminalCode(terminalParameters, authenticationToken, ApiVersion.V_3_1);
        return transformTerminalByTerminalCodeResponseToDTO(terminalResponse);
    }

    @Override
    public List<TerminalResponseDetailDTO> getTerminalDetailsByPostalCode(List<TerminalDetailsDTO> terminalDetails, Date shipmentDate) {
        try {
            TerminalsByPostalCode terminalsByPostalCode = new TerminalsByPostalCode();
            TerminalsByPostalCodeRequest request = prepareTerminalsByPostalCodeRequest(terminalDetails, shipmentDate);
            terminalsByPostalCode.setTerminalsByPostalCodeRequest(request);

            CarrierConnectXLPortTypeV3 portType = getServicePortType();
            AuthenticationToken authenticationToken = getCarrierConnectAuthToken();
            TerminalsByPostalCodeResponse response = portType.terminalsByPostalCode(terminalsByPostalCode, authenticationToken, ApiVersion.V_3_1);
            List<TerminalResponseDetailDTO> responseTerminalDetails = transformTerminalsByPostalCodeResponseToDTO(response);

            return responseTerminalDetails;
        } catch (Exception e) {
            //Do nothing here. because we dont want to take any action with exception as it is used in Rating engine.
            //If we do, then it will affect calculation of other carriers.
            e.printStackTrace();
        }
        return null;
    }

   /* @Override
    public com.smc.products.ArrayOfProductError lookupErrorCode(List<Integer> codes) throws Exception {
        ArrayOfInt errorCodes = new ArrayOfInt();
        for (Integer code : codes) {
            errorCodes.getInt().add(code);
        }

        CarrierConnectXLPortTypeV3 portType = getServicePortType();
        AuthenticationToken authenticationToken = getCarrierConnectAuthToken();
        ArrayOfProductError productErrors = portType.errorCodesLookup(errorCodes, authenticationToken);

        return productErrors;
    }*/

    /**
     * Gets the service ports to invoke the service call.
     * 
     * @return CarrierConnectWSPortType carrier connect web service port.
     */
    private CarrierConnectXLPortTypeV3 getServicePortType() {
        CarrierConnectXLV3 service = new CarrierConnectXLV3();
        return service.getCarrierConnectXLHttpPortV3();

    }

    /**
     * Transform the user DTOs into web service specific request object.
     * 
     * @param terminalDetails
     *            List of {@link TerminalDetailsDTO}.
     * @return ArrayOfTerminalRequest terminal requests.
     */
    private TerminalByTerminalCode getTerminalsByTerminalCodeParameters(List<TerminalDetailsDTO> terminalDetails) {
        TerminalByTerminalCodeRequest request = new TerminalByTerminalCodeRequest();
        TerminalByTerminalCode parameters = new TerminalByTerminalCode();

        //TODO Rose
        /*request.setSCAC(terminalDetails);
        TerminalRequest serviceRequest = null;

        for (TerminalDetailsDTO dto : terminalDetails) {
            serviceRequest = new TerminalRequest();
            ArrayOfString arrayOfString = new ArrayOfString();
            for (String scac : dto.getScacList()) {
                arrayOfString.getString().add(scac);
            }
            serviceRequest.setServiceMethod(dto.getMethod());
            serviceRequest.setSCAC(arrayOfString);
            requestList.getTerminalRequest().add(serviceRequest);
        }*/
        parameters.setTerminalByTerminalCodeRequest(request);
        return parameters;
    }

    /**
     * Converts the user's request to web service requests.
     * 
     * @param terminalRequests
     *            List of {@link TerminalDetailsDTO}.
     * @return ArrayOfCarrierTerminalRequestByPostalCode
     */
    private TerminalsByPostalCodeRequest prepareTerminalsByPostalCodeRequest(List<TerminalDetailsDTO> terminalRequests, Date shipmentDate) {
        TerminalsByPostalCodeRequest request = new TerminalsByPostalCodeRequest();
        boolean pickup = true;
        for (TerminalDetailsDTO dto : terminalRequests) {
            for (AddressVO address : dto.getShipmentAddresses()) {
                TerminalByPostalCode terminalByPostalCode = new TerminalByPostalCode();
                terminalByPostalCode.setCity(address.getCity());
                terminalByPostalCode.setCountryCode(Country.fromValue(address.getCountryCode()));
                terminalByPostalCode.setSCAC(dto.getScac());
                terminalByPostalCode.setPostalCode(address.getPostalCode());
                terminalByPostalCode.setServiceType(ServiceTypes.STANDARD);
                if (!isNull(shipmentDate)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    terminalByPostalCode.setShipmentDate(simpleDateFormat.format(shipmentDate));
                }
                terminalByPostalCode.setStateProvince(address.getStateCode());
                terminalByPostalCode.setTerminalType(pickup ? TerminalType.PICKUP : TerminalType.DELIVERY);
                pickup = !pickup;
                terminalByPostalCode.setServiceMethod(Method.fromValue(dto.getMethod()));
                request.getTerminals().add(terminalByPostalCode);
            }
        }
        return request;
    }

    /**
     * Converts the web service responses into user defined objects to use.
     *
     * @param response {@Link TerminalByTerminalCodeResponse}.
     * @return List of {@link TerminalResponseDetailDTO}.
     */
    private List<TerminalResponseDetailDTO> transformTerminalByTerminalCodeResponseToDTO(TerminalByTerminalCodeResponse response) {

        List<CarrierTerminals> carrierTerminals = response.getTerminalSearchResponse().getCarrierTerminals();
        TerminalResponseDetailDTO responseDto = null;
        List<TerminalResponseDetailDTO> terminalDetailList = new ArrayList<TerminalResponseDetailDTO>();

        for (CarrierTerminals terminal : carrierTerminals) {

            responseDto = new TerminalResponseDetailDTO();
            responseDto.setCarrierName(terminal.getCarrierName());
            responseDto.setServiceMethod(terminal.getServiceMethod().value());
            responseDto.setScac(terminal.getSCAC());
            List<Terminal> arrayOfCarrierTerminals = terminal.getTerminals();

            responseDto.setDetails(getCarrierTerminals(arrayOfCarrierTerminals));

            terminalDetailList.add(responseDto);
        }
        return terminalDetailList;
    }

    /**
     * Converts the web service responses into user defined objects to use.
     * 
     * @param response
     *            TerminalsByPostalCodeResponse.
     * @return List of {@link TerminalResponseDetailDTO}.
     */
    private List<TerminalResponseDetailDTO> transformTerminalsByPostalCodeResponseToDTO(TerminalsByPostalCodeResponse response) {

        List<CarrierTerminalResult> carrierTerminals = response.getTerminalResponse().getCarrierTerminals();
        TerminalResponseDetailDTO responseDto = null;
        List<TerminalResponseDetailDTO> terminalDetailList = new ArrayList<TerminalResponseDetailDTO>();

        for (CarrierTerminalResult terminal : carrierTerminals) {

            responseDto = new TerminalResponseDetailDTO();
            responseDto.setCarrierName(terminal.getCarrierName());
            responseDto.setServiceMethod(terminal.getServiceMethod().value());
            responseDto.setScac(terminal.getSCAC());
            List<TerminalDetail> carrierTerminalDetails = terminal.getTerminalDetails();

            responseDto.setDetails(getCarrierTerminalDetails(carrierTerminalDetails));

            terminalDetailList.add(responseDto);
        }
        return terminalDetailList;
    }

    /**
     * Helper method to convert the service response to List of {@link AddressVO}.
     *
     * @param carrierTerminals
     *            List of Carrier terminal response from service.
     * @return List of addresses.
     */
    private List<CarrierTerminalDetailDTO> getCarrierTerminals(List<Terminal> carrierTerminals) {
        List<CarrierTerminalDetailDTO> terminalDetails = new ArrayList<CarrierTerminalDetailDTO>();
        CarrierTerminalDetailDTO terminalDetail = null;
        for (Terminal detail : carrierTerminals) {
            terminalDetail = new CarrierTerminalDetailDTO();
            terminalDetail.setAddress1(detail.getAddress1());
            terminalDetail.setAddress2(detail.getAddress2());
            terminalDetail.setCity(detail.getCity());
            terminalDetail.setPostalcode(detail.getPostalCode());
            terminalDetail.setStateProvince(detail.getStateProvince());
            terminalDetail.setContact(detail.getContact().getName());
            terminalDetail.setContactEmail(detail.getContact().getEmail());
            terminalDetail.setPhone(detail.getContact().getPhone());
            terminalDetail.setName(detail.getTerminalName());
            terminalDetails.add(terminalDetail);
        }

        return terminalDetails;
    }

    /**
     * Helper method to convert the service response to List of {@link AddressVO}.
     * 
     * @param carrierTerminalDetail
     *            List of Carrier terminal Detail response from service.
     * @return List of addresses.
     */
    private List<CarrierTerminalDetailDTO> getCarrierTerminalDetails(List<TerminalDetail> carrierTerminalDetail) {
        List<CarrierTerminalDetailDTO> terminalDetails = new ArrayList<CarrierTerminalDetailDTO>();
        CarrierTerminalDetailDTO terminalDetail = null;
        for (TerminalDetail detail : carrierTerminalDetail) {
            terminalDetail = new CarrierTerminalDetailDTO();
            terminalDetail.setAddress1(detail.getAddress1());
            terminalDetail.setAddress2(detail.getAddress2());
            terminalDetail.setCity(detail.getCity());
            terminalDetail.setPostalcode(detail.getPostalCode());
            terminalDetail.setStateProvince(detail.getStateProvince());
            terminalDetail.setContact(detail.getContact().getName());
            terminalDetail.setContactEmail(detail.getContact().getEmail());
            terminalDetail.setPhone(detail.getContact().getPhone());
            terminalDetail.setName(detail.getTerminalName());
            terminalDetails.add(terminalDetail);
        }

        return terminalDetails;
    }

}