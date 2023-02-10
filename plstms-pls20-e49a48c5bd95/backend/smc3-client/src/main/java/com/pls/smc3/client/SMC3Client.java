package com.pls.smc3.client;

import com.pls.smc3.client.model.ProductItemCommodityInfo;
import com.smc.carrierconnectxl.webservice.pojos.CarrierTerminalResult;
import com.smc.carrierconnectxl.webservice.v3.transit.TransitResponse;
import com.smc.ltl.web.LTLRateShipmentResponse;
import com.smc.webservices.DataModuleException;
import com.smc.webservices.RateWareWebServiceException;
import com.smc.webservices.WebServiceException;

import java.util.Date;
import java.util.List;

/**
 * Client interface to the SMC3 system.
 *
 * @author Sergey Kirichenko
 */
public interface SMC3Client {

    /**
     * Method returns transit information for selected carriers.
     *
     * @param carrierSCACCodes is the list of the carriers' SCAC codes
     * @param originZip is the postal code of the pickup transit place
     * @param originCountry is the country code of the pickup transit place
     * @param destinationZip is the postal code of the delivery transit place
     * @param destinationCountry is the country code of the delivery transit place
     * @return the list of the route information
     * 
     * @throws WebServiceException Server returns error.
     */
    TransitResponse getTransitRoutesForCarriers(List<String> carrierSCACCodes, String originZip,
                                                String originCountry, String destinationZip, String destinationCountry) throws WebServiceException;

    /**
     * Method returns terminal information for selected carriers both sides pickup and delivery.
     *
     * @param carrierSCACCodes is the list of the carriers' SCAC codes
     * @param originZip is the postal code of the pickup transit place
     * @param originCountry is the country code of the pickup transit place
     * @param destinationZip is the postal code of the delivery transit place
     * @param destinationCountry is the country code of the delivery transit place
     * @return the list of the terminal information
     * 
     * @throws WebServiceException Server returns error.
     */
    List<CarrierTerminalResult> getTerminalInfoForCarriers(List<String> carrierSCACCodes, String originZip,
                                                           String originCountry, String destinationZip, String destinationCountry) throws WebServiceException;

    /**
     * Method returns base rate information for the selected shipment.
     *
     *
     * @param items is the items from the shipment
     * @param originZip is the postal code of the pickup transit place
     * @param originCountry is the country code of the pickup transit place
     * @param destinationZip is the postal code of the delivery transit place
     * @param destinationCountry is the country code of the delivery transit place
     * @param shipmentDate is the date of the shipment
     * @return the base rate for selected shipment
     * 
     * @throws DataModuleException Some error in data module..
     * @throws RateWareWebServiceException  Some error in RateWare module.
     */
    LTLRateShipmentResponse getBaseRate(List<ProductItemCommodityInfo> items, String originZip, String originCountry,
                                        String destinationZip, String destinationCountry, Date shipmentDate)
            throws DataModuleException, RateWareWebServiceException;
}
