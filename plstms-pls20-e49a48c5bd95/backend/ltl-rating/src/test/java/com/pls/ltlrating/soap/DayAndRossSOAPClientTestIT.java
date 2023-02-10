package com.pls.ltlrating.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.exception.ApplicationException;
import com.pls.ltlrating.soap.client.DayAndRossSOAPClient;
import com.pls.ltlrating.soap.proxy.ArrayOfShipmentItem;
import com.pls.ltlrating.soap.proxy.Division;
import com.pls.ltlrating.soap.proxy.GetRate2Response;
import com.pls.ltlrating.soap.proxy.LengthUnit;
import com.pls.ltlrating.soap.proxy.MeasurementSystem;
import com.pls.ltlrating.soap.proxy.PaymentType;
import com.pls.ltlrating.soap.proxy.ServiceLevels;
import com.pls.ltlrating.soap.proxy.Shipment;
import com.pls.ltlrating.soap.proxy.ShipmentAddress;
import com.pls.ltlrating.soap.proxy.ShipmentItem;
import com.pls.ltlrating.soap.proxy.ShipmentType;
import com.pls.ltlrating.soap.proxy.WeightUnit;


/**
 * SOAP client for DayAndRoss.
 * 
 * @author Brichak Aleksandr
 *
 */
@Ignore // ignore this test because it is using production destination
public class DayAndRossSOAPClientTestIT extends BaseServiceITClass {

    @Autowired
    private DayAndRossSOAPClient dayAndRossSOAPClient;

    @Before
    public void setUp() throws ApplicationException {
        // TODO here should be mock for dayAndRossSOAPClient.getDayAndRossRate !!!
        // Now we are using a real server for this call.
    }

    @Test
    public void shouldGetSOAPResponce() throws Exception {

        Shipment shipment = new Shipment();
        shipment.setBillToAccount("144633");
        shipment.setShipmentType(ShipmentType.QUOTE);
        shipment.setPaymentType(PaymentType.PREPAID);
        shipment.setMeasurementSystem(MeasurementSystem.IMPERIAL);
        shipment.setDivision(Division.GENERAL_FREIGHT);

       ShipmentAddress consigneeAddress = new ShipmentAddress();
       consigneeAddress.setCity("STONEY CREEK");
       consigneeAddress.setCountry("CA");
       consigneeAddress.setPostalCode("L8E2N9");
       consigneeAddress.setProvince("ON");

       ShipmentAddress shipperAddress = new ShipmentAddress();
       shipperAddress.setCity("HAMILTON");
       shipperAddress.setCountry("CA");
       shipperAddress.setPostalCode("L8L7W9");
       shipperAddress.setProvince("ON");
       shipment.setShipperAddress(shipperAddress);
       shipment.setConsigneeAddress(consigneeAddress);

       ArrayOfShipmentItem items = new ArrayOfShipmentItem();

       ShipmentItem shipmentItem = new ShipmentItem();
       shipmentItem.setDescription("Rate Shipping");
       shipmentItem.setHeight(46);
       shipmentItem.setLength(128);
       shipmentItem.setLengthUnit(LengthUnit.INCHES);
       shipmentItem.setPieces(1);
       shipmentItem.setWeight(918);
       shipmentItem.setWeightUnit(WeightUnit.POUNDS);
       shipmentItem.setWidth(38);
       items.getShipmentItem().add(shipmentItem);
       shipment.setItems(items);

        GetRate2Response resp = dayAndRossSOAPClient.getDayAndRossRate(shipment);

        List<ServiceLevels> serviceLevels = resp.getGetRate2Result().getServiceLevels();
        assertNotNull(serviceLevels);

        for (ServiceLevels serviceLevel : serviceLevels) {
            assertEquals(serviceLevel.getDivision(), Division.FASTRAX);
            assertEquals(serviceLevel.getDescription(), "GENERAL LTL");
            assertEquals(serviceLevel.getTransitTime(), "1");
            assertEquals(serviceLevel.getTotalAmount(), new BigDecimal("161.38"));
        }
    }

}
