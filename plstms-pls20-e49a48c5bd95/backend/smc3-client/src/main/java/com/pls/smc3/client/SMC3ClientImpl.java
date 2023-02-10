package com.pls.smc3.client;

import com.pls.smc3.client.model.ProductItemCommodityInfo;
import com.smc.carrierconnect.webservice.enums.Country;
import com.smc.carrierconnect.webservice.enums.Method;
import com.smc.carrierconnect.webservice.objects.enums.ServiceTypes;
import com.smc.carrierconnectxl.webservice.enums.ApiVersion;
import com.smc.carrierconnectxl.webservice.pojos.CarrierService;
import com.smc.carrierconnectxl.webservice.pojos.CarrierTerminalResult;
import com.smc.carrierconnectxl.webservice.pojos.Location;
import com.smc.carrierconnectxl.webservice.pojos.TerminalByPostalCode;
import com.smc.carrierconnectxl.webservice.v3.CarrierConnectXLPortTypeV3;
import com.smc.carrierconnectxl.webservice.v3.TerminalsByPostalCode;
import com.smc.carrierconnectxl.webservice.v3.Transit;
import com.smc.carrierconnectxl.webservice.v3.terminalsbypostalcode.TerminalsByPostalCodeRequest;
import com.smc.carrierconnectxl.webservice.v3.transit.TransitRequest;
import com.smc.carrierconnectxl.webservice.v3.transit.TransitResponse;
import com.smc.commons.shipments.ShipmentLocale;
import com.smc.ltl.web.ArrayOfLTLRequestDetail;
import com.smc.ltl.web.LTLRateShipmentRequest;
import com.smc.ltl.web.LTLRateShipmentResponse;
import com.smc.ltl.web.LTLRequestDetail;
import com.smc.webservices.AuthenticationToken;
import com.smc.webservices.DataModuleException;
import com.smc.webservices.RateWareWebServiceException;
import com.smc.webservices.RateWareXLPortType;
import com.smc.webservices.WebServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of the client interface to the SMC3 system.
 *
 * @author Sergey Kirichenko
 */
@Repository
public class SMC3ClientImpl implements SMC3Client {

    @Autowired
    private CarrierConnectXLPortTypeV3 carrierConnectClient;

    @Autowired
    private RateWareXLPortType rateWareClient;

    @Autowired
    private AuthenticationToken rateToken;

    @Autowired
    private AuthenticationToken carrierToken;

    @Override
    public TransitResponse getTransitRoutesForCarriers(List<String> carrierSCACCodes, String originZip,
                                                       String originCountry, String destinationZip, String destinationCountry) throws WebServiceException {
        if (carrierSCACCodes == null || carrierSCACCodes.isEmpty() || StringUtils.isBlank(originZip)
                || StringUtils.isBlank(originCountry) || StringUtils.isBlank(destinationZip)
                || StringUtils.isBlank(destinationCountry)) {
            throw new IllegalArgumentException("Not all parameters are set.");
        }
        return carrierConnectClient.transit(createTransitRequest(carrierSCACCodes, originZip, originCountry,
                destinationZip, destinationCountry), carrierToken, ApiVersion.V_3_1).getTransitResponse();
    }

    @Override
    public List<CarrierTerminalResult> getTerminalInfoForCarriers(List<String> carrierSCACCodes, String originZip,
                                                                  String originCountry, String destinationZip, String destinationCountry) throws WebServiceException {
        if (carrierSCACCodes == null || carrierSCACCodes.isEmpty() || StringUtils.isBlank(originZip)
                || StringUtils.isBlank(originCountry) || StringUtils.isBlank(destinationZip)
                || StringUtils.isBlank(destinationCountry)) {
            throw new IllegalArgumentException("Not all parameters are set.");
        }
        return carrierConnectClient.terminalsByPostalCode(createTerminalRequest(carrierSCACCodes, originZip,
                originCountry, destinationZip, destinationCountry), carrierToken, ApiVersion.V_3_1).getTerminalResponse().getCarrierTerminals();
    }

    @Override
    public LTLRateShipmentResponse getBaseRate(List<ProductItemCommodityInfo> items, String originZip,
                                               String originCountry, String destinationZip, String destinationCountry, Date shipmentDate)
            throws DataModuleException, RateWareWebServiceException {
        if (items == null || items.isEmpty() || StringUtils.isBlank(originZip)
                || StringUtils.isBlank(originCountry) || StringUtils.isBlank(destinationZip)
                || StringUtils.isBlank(destinationCountry)) {
            throw new IllegalArgumentException("Not all parameters are set.");
        }
        return rateWareClient.ltlRateShipment(createRateRequest(items, originZip, originCountry, destinationZip,
                destinationCountry, shipmentDate), rateToken);
    }

    private Transit createTransitRequest(List<String> carrierSCACCodes, String originZip, String originCountry,
                                         String destinationZip, String destinationCountry) {
        Transit transit = new Transit();
        TransitRequest result = new TransitRequest();
        Location origin = new Location();
        origin.setCountryCode(Country.fromValue(originCountry));
        origin.setPostalCode(originZip);
        result.setOrigin(origin);
        Location destination = new Location();
        destination.setCountryCode(Country.fromValue(destinationCountry));
        destination.setPostalCode(destinationZip);
        result.setDestination(destination);


        for (String scac : carrierSCACCodes) {
            CarrierService carrierService = new CarrierService();
            carrierService.setServiceType(ServiceTypes.STANDARD);
            carrierService.setServiceMethod(Method.LTL);
            carrierService.setSCAC(scac);
            result.getCarriers().add(carrierService);
        }
        transit.setTransitRequest(result);
        return transit;
    }

    private TerminalsByPostalCode createTerminalRequest(List<String> carrierSCACCodes,
                                                        String originZip, String originCountry, String destinationZip, String destinationCountry) {
        TerminalsByPostalCode terminalsByPostalCode = new TerminalsByPostalCode();
        TerminalsByPostalCodeRequest terminalsByPostalCodeRequest = new TerminalsByPostalCodeRequest();
        ShipmentLocale origin = new ShipmentLocale();
        origin.setCountryCode(originCountry);
        origin.setPostalCode(originZip);
        ShipmentLocale destination = new ShipmentLocale();
        destination.setCountryCode(destinationCountry);
        destination.setPostalCode(destinationZip);

        for (String scac : carrierSCACCodes) {
            TerminalByPostalCode originRequest = new TerminalByPostalCode();
            originRequest.setSCAC(scac);
            originRequest.setServiceMethod(Method.LTL);
            originRequest.setPostalCode(originZip);
            originRequest.setCountryCode(Country.fromValue(originCountry));
            terminalsByPostalCodeRequest.getTerminals().add(originRequest);
            TerminalByPostalCode destinationRequest = new TerminalByPostalCode();
            destinationRequest.setSCAC(scac);
            destinationRequest.setServiceMethod(Method.LTL);
            destinationRequest.setPostalCode(originZip);
            destinationRequest.setCountryCode(Country.fromValue(originCountry));
            terminalsByPostalCodeRequest.getTerminals().add(destinationRequest);
        }

        terminalsByPostalCode.setTerminalsByPostalCodeRequest(terminalsByPostalCodeRequest);
        return terminalsByPostalCode;
    }

    private LTLRateShipmentRequest createRateRequest(List<ProductItemCommodityInfo> items, String originZip,
                                                     String originCountry, String destinationZip, String destinationCountry, Date shipmentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#0");
        LTLRateShipmentRequest result = new LTLRateShipmentRequest();
        result.setOriginCountry(originCountry);
        result.setOriginPostalCode(originZip);
        result.setDestinationCountry(destinationCountry);
        result.setDestinationPostalCode(destinationZip);
        result.setShipmentDateCCYYMMDD(dateFormat.format(shipmentDate));
        result.setTariffName("DEMOLTLA");
        ArrayOfLTLRequestDetail details = new ArrayOfLTLRequestDetail();
        List<LTLRequestDetail> itemDetailsList = details.getLTLRequestDetail();
        for (ProductItemCommodityInfo item : items) {
            LTLRequestDetail itemDetail = new LTLRequestDetail();
            itemDetail.setNmfcClass(item.getCommodityClass());
            itemDetail.setWeight(decimalFormat.format(item.getWeight()));
            itemDetailsList.add(itemDetail);
        }
        result.setDetails(details);
        return result;
    }
}
