package com.pls.ltlrating.soap.client;

import com.pls.ltlrating.soap.proxy.GetRate2Response;
import com.pls.ltlrating.soap.proxy.Shipment;

/**
 * SOAP client for DayAndRoss.
 * 
 * @author Brichak Aleksandr
 *
 */
public interface DayAndRossSOAPClient {

    /**
     * Get DayAndRoss rates.
     * 
     * @param shipment
     *            shipment to send
     * @return {@link GetRate2Response}
     */
    GetRate2Response getDayAndRossRate(Shipment shipment);
}
