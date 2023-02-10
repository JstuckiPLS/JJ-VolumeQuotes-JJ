package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.pls.core.dao.CountryDao;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.dao.LtlAccessorialsMappingDao;
import com.pls.ltlrating.domain.bo.LTLDayAndRossRateBO;
import com.pls.ltlrating.service.impl.LtlDayAndRossCarrierAPIServiceImpl;
import com.pls.ltlrating.shared.CarrierPricingProfilesVO;
import com.pls.ltlrating.shared.CarrierRatingVO;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.ltlrating.soap.client.DayAndRossSOAPClient;
import com.pls.ltlrating.soap.proxy.ArrayOfServiceLevels;
import com.pls.ltlrating.soap.proxy.ArrayOfShipmentCharge;
import com.pls.ltlrating.soap.proxy.GetRate2Response;
import com.pls.ltlrating.soap.proxy.ServiceLevels;
import com.pls.ltlrating.soap.proxy.Shipment;
import com.pls.ltlrating.soap.proxy.ShipmentCharge;

/**
 * Test class to test DayAndRoss API logic.
 *
 * @author Brichak Aleksandr
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlDayAndRossCarrierAPIServiceImplTest {
    private static final String SCAC = "TEST";

    @Mock
    private DayAndRossSOAPClient dayAndRossSOAPClient;

    @Mock
    private CountryDao countryDao;

    @Mock
    private LtlAccessorialsMappingDao ltlAccessorialsMappingDao;

    @InjectMocks
    private LtlDayAndRossCarrierAPIServiceImpl carrierAPIService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(carrierAPIService, "dayAndRossSCAC", SCAC);
    }

    @Test
    public void shouldGetDayAndRossRates() {
        LtlRatingVO ratingProfiles = getRatingProfile();
        GetOrderRatesCO ratesCO = getCriteriaWithCanadianAddress();

        Mockito.when(countryDao.findShortCountryCode("CAN")).thenReturn("CA");

        GetRate2Response dayAndRossResponse = new GetRate2Response();
        ArrayOfServiceLevels serviceLevels = new ArrayOfServiceLevels();
        ServiceLevels serviceLevel = new ServiceLevels();
        serviceLevel.setTotalAmount(new BigDecimal(Math.random() + 1));
        ArrayOfShipmentCharge shipmentCharges = new ArrayOfShipmentCharge();
        ShipmentCharge shipmentCharge = new ShipmentCharge();
        shipmentCharges.getShipmentCharge().add(shipmentCharge);
        serviceLevel.setShipmentCharges(shipmentCharges);
        serviceLevels.getServiceLevels().add(serviceLevel);
        dayAndRossResponse.setGetRate2Result(serviceLevels);
        Mockito.when(dayAndRossSOAPClient.getDayAndRossRate(Mockito.argThat(new ArgumentMatcher<Shipment>() {
            @Override
            public boolean matches(Object argument) {
                Shipment shipment = (Shipment) argument;
                Assert.assertEquals("CA", shipment.getShipperAddress().getCountry());
                Assert.assertEquals("L8E2N9", shipment.getShipperAddress().getPostalCode());
                Assert.assertEquals("ON", shipment.getShipperAddress().getProvince());
                Assert.assertEquals("STONEY CREEK", shipment.getShipperAddress().getCity());
                return true;
            }
        }))).thenReturn(dayAndRossResponse);

        carrierAPIService.populateRates(ratesCO, ratingProfiles);

        Mockito.verify(dayAndRossSOAPClient).getDayAndRossRate(Mockito.any());
        Assert.assertNotNull(ratingProfiles);
        LtlRatingProfileVO ratingProfile = ratingProfiles.getCarrierPricingDetails().entrySet().iterator().next()
                .getValue().iterator().next().getRate().getPricingDetails();
        Assert.assertNotNull(ratingProfile);
        LTLDayAndRossRateBO response = ratingProfile.getDayAndRossResponse();
        Assert.assertNotNull(response);
        Assert.assertNotSame(response.getTotalCarrierCost().intValue(), 0);
    }

    @Test
    public void shouldNOTGetDayAndRossRates() throws Exception {
        LtlRatingVO ratingProfiles = getRatingProfile();
        GetOrderRatesCO ratesCO = getCriteriaWithCanadianAddress();
        ratesCO.setDestinationAddress(null);

        carrierAPIService.populateRates(ratesCO, ratingProfiles);

        Assert.assertNotNull(ratingProfiles);
        Map<Long, List<CarrierRatingVO>> ratingProfile = ratingProfiles.getCarrierPricingDetails();
        Assert.assertTrue(ratingProfile.isEmpty());
    }

    private LtlRatingVO getRatingProfile() {
        LtlRatingVO ratingProfiles = new LtlRatingVO();
        Map<Long, List<CarrierRatingVO>> carrierPricingDetails = new HashMap<Long, List<CarrierRatingVO>>();
        List<CarrierRatingVO> carrierRatingVOs = new LinkedList<CarrierRatingVO>();
        CarrierRatingVO carrierRatingVO = new CarrierRatingVO();
        CarrierPricingProfilesVO carrierPricingProfiles = new CarrierPricingProfilesVO();
        LtlRatingProfileVO pricingDetails = new LtlRatingProfileVO();
        pricingDetails.setRatingCarrierType("CARRIER_API");
        pricingDetails.setScac(SCAC);
        carrierPricingProfiles.setPricingDetails(pricingDetails);
        carrierRatingVO.setRate(carrierPricingProfiles);
        carrierRatingVOs.add(carrierRatingVO);
        carrierPricingDetails.put(1L, carrierRatingVOs);
        ratingProfiles.setCarrierPricingDetails(carrierPricingDetails);
        return ratingProfiles;
    }

    private GetOrderRatesCO getCriteriaWithCanadianAddress() {
        GetOrderRatesCO ratesCO = new GetOrderRatesCO();

        ratesCO.setShipperOrgId(1L);
        ratesCO.setShipDate(new Date());

        AddressVO originAddress = new AddressVO();
        originAddress.setCity("STONEY CREEK");
        originAddress.setCountryCode("CAN");
        originAddress.setPostalCode("L8E 2N9");
        originAddress.setStateCode("ON");

        AddressVO destinationAddress = new AddressVO();
        destinationAddress.setCity("HAMILTON");
        destinationAddress.setCountryCode("CAN");
        destinationAddress.setPostalCode("L8L 7W9");
        destinationAddress.setStateCode("ON");

        List<RateMaterialCO> materials = new ArrayList<RateMaterialCO>();
        RateMaterialCO material = new RateMaterialCO();
        material.setWeight(new BigDecimal(918));
        material.setQuantity(1);
        material.setPieces(1);
        materials.add(material);

        ratesCO.setOriginAddress(originAddress);
        ratesCO.setDestinationAddress(destinationAddress);
        ratesCO.setMaterials(materials);


        return ratesCO;
    }
}
