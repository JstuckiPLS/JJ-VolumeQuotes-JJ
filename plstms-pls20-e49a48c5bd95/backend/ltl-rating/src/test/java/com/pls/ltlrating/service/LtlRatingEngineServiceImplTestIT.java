package com.pls.ltlrating.service;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.LtlPricNwDfltMarginDAO;
import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlBlkServCarrierType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.organization.LtlPricNwDfltMarginEntity;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.extint.shared.MileageCalculatorType;
import com.pls.ltlrating.dao.LtlAccessorialsDao;
import com.pls.ltlrating.dao.LtlBlockLaneDao;
import com.pls.ltlrating.dao.LtlFuelDao;
import com.pls.ltlrating.dao.LtlPalletPricingDetailsDao;
import com.pls.ltlrating.dao.LtlPricingBlockedCustomersDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlRatingEngineDao;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.LtlBlockCarrierGeoServDetailsEntity;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingResult;
import com.pls.ltlrating.shared.LtlRatingProfileVO;
import com.pls.ltlrating.shared.LtlRatingVO;
import com.pls.ltlrating.shared.RateMaterialCO;
import com.pls.mileage.service.MileageService;
import com.pls.smc3.dto.LTLDetailDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;
import com.pls.smc3.dto.ScacRequest;
import com.pls.smc3.dto.ScacResponse;
import com.pls.smc3.dto.TransitRequestDTO;
import com.pls.smc3.dto.TransitResponseDTO;
import com.pls.smc3.service.CarrierTransitClient;

/**
 * Test class to test Pricing Engine logic.
 *
 * @author Hima Bindu Challa.
 * @author Ashwini Neelgund
 */
@ActiveProfiles("PricingTest")
public class LtlRatingEngineServiceImplTestIT extends BaseServiceITClass {
    private static final String YRC_FREIGHT_SCAC = "RDWY";
    private static final String USF_HOLLAND_SCAC = "HMES";
    private static final String UPS_FREIGHT_A_SCAC = "UPGF";
    private static final String ESTES_EXPRESS_LINES_SCAC = "EXLA";
    private static final String FEDEX_FREIGHT_ECONOMY = "FXNL";
    private static final String OLD_DOMINION_FREIGHT_LINES = "ODFL";

    @Autowired
    private LtlPricingDetailsService ltlPricDetServ;
    @Autowired
    private OrganizationPricingDao organizationPricingDao;
    @Autowired
    private LtlAccessorialsDao ltlAccessorialsDao;
    @Autowired
    private LtlPricNwDfltMarginDAO ltlPricNwDfltMarginDAO;
    @Autowired
    private LtlPricingProfileDao ltlPricingProfileDao;
    @Autowired
    private LtlProfileDetailsService profileDetailsService;
    @Autowired
    private LtlRatingEngineDao ratingEngineDao;
    @Autowired
    private LtlBlockLaneDao ltlBlockLaneDao;
    @Autowired
    private LtlPricingBlockedCustomersDao blockedCustomerDao;
    @Autowired
    private LtlPalletPricingDetailsDao palletPricingDetailsDao;
    @Autowired
    private LtlFuelDao ltlFuelDao;
    @Autowired
    private LtlSMC3Service smc3Service;
    @Autowired
    private CarrierTransitClient transitClient;
    @Autowired
    private MileageService mileageService;

    @Autowired
    private LtlRatingEngineService pricingEng;

    @Before
    public void setUp() throws ApplicationException {
        setUpSmc3Mock(null, null);

        Mockito.when(transitClient.getTransitInformation(Mockito.any())).thenAnswer(new Answer<TransitResponseDTO>() {
            @Override
            public TransitResponseDTO answer(InvocationOnMock invocation) throws Throwable {
                if (invocation.getArguments().length == 0 || invocation.getArguments()[0] == null) {
                    return null;
                }
                TransitResponseDTO transitDTO = new TransitResponseDTO();
                List<ScacResponse> scacResponses = new ArrayList<ScacResponse>();
                for (ScacRequest scacRequest : ((TransitRequestDTO) invocation.getArguments()[0]).getScacRequests()) {
                    ScacResponse scacResponse = new ScacResponse();
                    scacResponse.setDays(3);
                    scacResponse.setOriginServiceType(scacRequest.getScac().equalsIgnoreCase("LAXV") ? "I" : "D");
                    scacResponse.setScac(scacRequest.getScac());
                    scacResponses.add(scacResponse);
                }
                transitDTO.setScacResponses(scacResponses);
                return transitDTO;
            }
        });

        Mockito.when(mileageService.getMileage(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(500);
    }

    private void setUpSmc3Mock(String smcTariff, LTLRateShipmentDTO specificResponse) {
        LTLRateShipmentDTO rateDTO = new LTLRateShipmentDTO();
        List<LTLDetailDTO> rateDetails = new ArrayList<LTLDetailDTO>();
        LTLDetailDTO rateDetailDTO = new LTLDetailDTO();
        rateDTO.setTotalCharge("1000.00");
        rateDTO.setMinimumCharge(new BigDecimal("1000"));
        rateDTO.setDeficitCharge(BigDecimal.ZERO);
        rateDTO.setDeficitNmfcClass("");
        rateDTO.setDeficitRate(BigDecimal.ZERO);
        rateDetailDTO.setCharge("1000.00");
        rateDetails.add(rateDetailDTO);
        rateDTO.setDetails(rateDetails);
        rateDTO.setTotalChargeFromDetails(new BigDecimal("1000"));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Stream.of((LtlRatingVO) invocation.getArguments()[1], (LtlRatingVO) invocation.getArguments()[2])
                        .filter(Objects::nonNull)
                        .flatMap(p -> p.getCarrierPricingDetails().values().stream()).flatMap(List::stream)
                        .flatMap(r -> Stream.of(r.getRate().getPricingDetails(),
                                r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails()))
                        .filter(Objects::nonNull).forEach(profile -> {
                            if (smcTariff != null && smcTariff.equals(profile.getSmc3Tariff())) {
                                profile.setSmc3Response(specificResponse);
                            } else {
                                profile.setSmc3Response(rateDTO);
                            }
                        });
                return null;
            }
        }).when(smc3Service).populateSMC3Rates(Mockito.any(), Mockito.any(), Mockito.any());
    }

    /**
     * TestCase to test if pricing results are returned for shipper if inactive pricing set up.
     *
     * @throws Exception
     */
    @Test
    public void testShipperWithActivePricing() throws Exception {

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());

        // Shipper with shipper org id = 1 has pricing status set to "Active"
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());

        // Shipper with shipper org id = 3 has no pricing status set.
        GetOrderRatesCO criteria = getCriteria();
        criteria.setShipperOrgId(3L);
        expectedResults = pricingEng.getRates(criteria);
        Assert.assertNull(expectedResults);
    }

    /**
     * TestCase to test simple pricing calculation. Which means, no accessorials, Hazmat, Guaranteed, Pallets,
     * Gainshare, Benchmark included
     *
     * @throws Exception
     */
    @Test
    public void testSimplePricingCalculation() throws Exception {

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());

        Assert.assertNotNull(expectedResults);

        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);

        Assert.assertNotNull(expResToEvaluate);

        /*
         * The pricing was set with RDWY for Origin = USA and Detination = USA and with discount = 80% and
         * another one with Origin = 16066 and Destination = 61760 and with discount = 72%. The pricing with
         * Origin = 16066 and Destination = 61760 should show up as it is set with more detailed
         * origin/destination values.
         *
         * Below are the settings in testdata. Pricing Type = Blanket Discount % = 72% Minimum Cost = 140
         * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
         * "PLS Shipper") Fuel Surcharge % = 24% Fuel on Fuel table = 3.640
         *
         *
         * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
         * 1111.11 Carrier Discount = 720.00 Shipper Discount = 800.00 Carrier Final Linehaul = 280.00 Shipper
         * Final Linehaul = 311.11 Carrier FS = 67.20 Shipper FS = 74.67 Carrier Total Cost = 347.20 Shipper
         * Total Cost = 385.78
         */
        Assert.assertEquals(new Integer(3), expResToEvaluate.getTransitTime());
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.11"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("720.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("800.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("280.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("311.11"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("61.60"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("68.44"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("341.60"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("379.55"), expResToEvaluate.getTotalShipperCost());
    }

    @Test
    public void shouldFilterResultsByProfileIds() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        criteria.setPricingProfileIDs(Arrays.asList(38L));
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);

        Assert.assertNotNull(expectedResults);
        Assert.assertEquals(1, expectedResults.size());

        LtlPricingResult yrcRate = getYrcRate(expectedResults);
        Assert.assertNotNull(yrcRate);
    }

    @Test
    public void shouldGetPricingResultWithMissingTransitTime() throws Exception {
        getSession().createQuery("update LtlPricingProfileDetailsEntity set carrierType='MANUAL' where id=40").executeUpdate();
        getSession().createQuery("delete from LtlPricingTerminalInfoEntity where priceProfileId=40").executeUpdate();

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());

        LtlPricingResult yrcRate = getYrcRate(expectedResults);
        Assert.assertNotNull(yrcRate);

        Assert.assertEquals(new Integer(0), yrcRate.getTransitTime());
        Assert.assertNull(yrcRate.getTransitDate());

        Assert.assertEquals(new BigDecimal("140.00"), yrcRate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("155.56"), yrcRate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("0.00"), yrcRate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("0.00"), yrcRate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("140.00"), yrcRate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("155.56"), yrcRate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("30.80"), yrcRate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("34.22"), yrcRate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("170.80"), yrcRate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("189.78"), yrcRate.getTotalShipperCost());
    }

    /**
     * TestCase to test carrier specific pricing calculation. If the criteria contains the carrier id specified
     * then we should return quote for just that single carrier if applies.
     *
     * @throws Exception
     */
    @Test
    public void testCarrierSpecificQuoteCalculation() throws Exception {

        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setCarrierOrgId(58L); // carrier id for RDWY
        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        Assert.assertEquals(expectedResults.size(), 1);
        LtlPricingResult expResForSpecifiedCarrier = getYrcRate(expectedResults);
        Assert.assertNotNull(expResForSpecifiedCarrier);

    }

    /**
     * TestCase to test carrier specific pricing calculation for pallets. If the criteria contains the carrier id
     * specified then we should return quote for just that single carrier if applies.
     *
     * @throws Exception
     */
    @Test
    public void testCarrierSpecificQuoteCalcForPallets() throws Exception {

        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setCarrierOrgId(58L); // carrier id for RDWY
        ratesCO.getMaterials().get(0).setPackageType("PLT");
        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        Assert.assertEquals(expectedResults.size(), 1);
        LtlPricingResult expResForSpecifiedCarrier = getYrcRate(expectedResults);
        Assert.assertNotNull(expResForSpecifiedCarrier);

    }

    /**
     * TestCase to test if profile with matching settings except Service type is returned or not.
     *
     * @throws Exception
     */
    @Test
    public void testServiceTypePricingCalculation() throws Exception {

        LtlPricingDetailsEntity ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(43L);
        ltlPricDetailsEntity.setServiceType(LtlServiceType.INDIRECT);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        getSession().flush();
        getSession().clear();

        GetOrderRatesCO criteria = getCriteria();
        criteria.setServiceType(LtlServiceType.DIRECT);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);

        Assert.assertNotNull(expectedResults);

        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);

        Assert.assertNotNull(expResToEvaluate);

        /*
         * The pricing was set with RDWY for Origin = USA and Destination = USA should show up as it is of
         * direct service type and the one with Origin = 16066 and Destination = 61760 should not show up as
         * it is of indirect service type. Below are the settings in testdata. Pricing Type = Blanket Discount
         * % = 80% Minimum Cost = 140 Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default
         * Margin on Customer "PLS Shipper") Fuel Surcharge % = 24% Fuel on Fuel table = 3.640
         *
         *
         * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
         * 1111.11 Carrier Discount = 800.00 Shipper Discount = 888.89 Carrier Final Linehaul = 200.00 Shipper
         * Final Linehaul = 222.22 Carrier FS = 48.00 Shipper FS = 53.33 Carrier Total Cost = 248.00 Shipper
         * Total Cost = 275.55
         */
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.10"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("800.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("888.88"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("200.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("222.22"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("44.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("48.89"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("244.00"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("271.11"), expResToEvaluate.getTotalShipperCost());

    }

    /**
     * TestCase to test movement types on carrier pricing profile.
     *
     * @throws Exception
     */
    @Test
    public void testMovementTypeInPricingCalculation() throws Exception {
        /*
         * As the origin and destination belong to different states the movement type is "INTER".
         * Hence the carriers with movement type INTER and BOTH have to be displayed in the proposals.
         * Below as we have set the movement type for carrier USF Holland to "INTRA", it would not be
         * displayed in the proposals list.While the carrier UPS Freight A has been set to "INTER", hence
         * it would be displayed in the proposals list.
         */
        LtlPricingDetailsEntity ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(50L);
        ltlPricDetailsEntity.setMovementType(MoveType.INTRA);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        getSession().flush();
        ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(41L);
        ltlPricDetailsEntity.setMovementType(MoveType.INTER);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        getSession().flush();
        getSession().clear();
        GetOrderRatesCO criteria = getCriteria();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        LtlPricingResult expResToEvaluate1 = null;
        for (LtlPricingResult result : expectedResults) {
            if ("HMES".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            } else if ("UPGF".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate1 = result;
            }
        }
        Assert.assertNull(expResToEvaluate);
        Assert.assertNotNull(expResToEvaluate1);
    }

    /**
     * TestCase to test effective and expiration dates on a carrier pricing profile.
     *
     * @throws Exception
     */
    @Test
    public void testEffDateAndExpDateInPricingCalculation() throws Exception {

        /*
         * The effective date for RDWY is 11/24/2013 and the expiration date is 11/01/3015.
         * For shipping date in between the effective and expiration date, the carrier must be
         * displayed in the pricing proposals.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);

        //For ship date before the effective date the carrier must not appear in the quote proposals.
        LtlPricingProfileEntity pricProf = ltlPricingProfileDao.get(38L);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        pricProf.setEffDate(DateUtils.addDays(c.getTime(), 1));
        ltlPricingProfileDao.saveOrUpdate(pricProf);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);

      //For ship date after the expiration date the carrier must not appear in the quote proposals.
        pricProf = ltlPricingProfileDao.get(38L);
        c.setTime(new Date());
        pricProf.setExpDate(DateUtils.addDays(c.getTime(), -1));
        ltlPricingProfileDao.saveOrUpdate(pricProf);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * TestCase to test if a carrier should appear in the pricing proposals based
     * on whether the entered weight lies between the minimum and maximum weight
     * range set on its pricing profile.
     *
     * @throws Exception
     */
    @Test
    public void testMinAndMaxWeightInPricingCalculation() throws Exception {

        /*
         * The min and max weight(in lbs) for RDWY is set as 10 and 1000 respectively.
         * For weight(here 180lbs) between the weight range of min and max, it should
         * return the RDWY in the proposals.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);

        //For weight(here 1500lbs) greater than the max weight, RDWY must not be returned in the proposals.
        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.getMaterials().get(0).setWeight(new BigDecimal("1500"));
        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);

        //For weight(here 5lbs) less than the min weight, RDWY must not be returned in the proposals.
        ratesCO.getMaterials().get(0).setWeight(new BigDecimal("5"));
        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);

    }

    /**
     * TestCase to test whether the minimum margin dollar amount is picked up on profile by pricing logic when
     * the shipper final linehaul calculated using the margin% is less than (carrier final linehaul + minimum
     * margin dollar amount).
     *
     * @throws Exception
     */
    @Test
    public void testMinMarginInPricingCalculation() throws Exception {
        /*
         * Blanket/CSP pricing profile is defined for carrier Con-Way freight for MACSTEEL with margin% set as
         * 10% and Min$ amount set as 50$. The margin% and Min$ defined in the carrier pricing profile(margin%
         * set as 10% and Min$ amount set as 50$) must be considered. Also as ShipperFinalLinehaul(~165)
         * calculated with margin 10% is less than the carrierFinalLinehaul+marginDollarAmount(150+50=200),the
         * ShipperFinalLinehaul is carrierFinalLinehaul+marginDollarAmount(200).
         */
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(10L);
        pricDtlEntity.setMarginAmount(new BigDecimal("10"));
        pricDtlEntity.setMinMarginAmount(new BigDecimal("50"));
        getSession().flush();
        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setShipperOrgId(2L);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1333.33"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1133.33"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("200.00"), pricingResult.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test Eff Date and Exp dates on pricing detail in pricing logic.
     *
     * @throws Exception
     */
    @Test
    public void testOverriddenEffDateAndExpDateInPricingCalculation() throws Exception {
        /*
         * The effective date for RDWY with pricing detail id 43 is 01/28/2013 and the expiration date is
         * 02/21/3020 with discount of 72. For shipping date in between the effective and expiration date, the
         * carrier pricing with id 43 must be displayed in the pricing proposals else carrier pricing with id
         * 60 and discount of 80 will be displayed.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertEquals(43L, expResToEvaluate.getCarrierPricingDetailId().longValue());
        Assert.assertEquals(new BigDecimal("72.00"), expResToEvaluate.getCostDiscount());

        // For ship date before the effective date the carrier must not appear in the quote proposals.
        LtlPricingDetailsEntity pricDtl = ltlPricDetServ.getPricingDetailById(43L);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        pricDtl.setEffDate(DateUtils.addDays(c.getTime(), 1));
        ltlPricDetServ.savePricingDetail(pricDtl);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertEquals(60L, expResToEvaluate.getCarrierPricingDetailId().longValue());
        Assert.assertEquals(new BigDecimal("80.00"), expResToEvaluate.getCostDiscount());

        // For ship date after the expiration date the carrier must not appear in the quote proposals.
        pricDtl = ltlPricDetServ.getPricingDetailById(43L);
        c.setTime(new Date());
        pricDtl.setEffDate(DateUtils.addDays(c.getTime(), -2));
        pricDtl.setExpDate(DateUtils.addDays(c.getTime(), -1));
        ltlPricDetServ.savePricingDetail(pricDtl);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertEquals(60L, expResToEvaluate.getCarrierPricingDetailId().longValue());
        Assert.assertEquals(new BigDecimal("80.00"), expResToEvaluate.getCostDiscount());
    }

    /**
     * TestCase to test if CSP is picked by system when both Blanket and CSP exist for a carrier.
     *
     * @throws Exception
     */
    @Test
    public void testCSPOverBlanketProfile() throws Exception {
        /*
         * Customer PLS Shipper has 2 Land Air Express of New England carrier pricing profiles that are
         * applicable to it : Blanket(id:31) and CSP(id:9). The blanket has minimum cost of $120 and discount
         * as 65% and the CSP has minimum cost of $130 and discount as 60%. The pricing engine should pick the
         * CSP profile irrespective of the costs.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("LAXV".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.10"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("600.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("666.66"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("400.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("444.44"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("88.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("108.50"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("488.00"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("552.94"), expResToEvaluate.getTotalShipperCost());

    }

    /**
     * TestCase to test if Blanket CSP is picked correctly for a carrier.
     *
     * @throws Exception
     */
    @Test
    public void testBlanketCSPOverBlanketProfile() throws Exception {
        /*
         * Customer PLS Shipper has 2 Con-Way Freight carrier pricing profiles that are applicable to it :
         * Blanket(id:24) and Blanket/CSP(id:2). The blanket has minimum cost of $80 and discount as 85%.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1333.33"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1133.33"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("200.00"), pricingResult.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("33.00"), pricingResult.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("53.50"), pricingResult.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("183.00"), pricingResult.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("253.50"), pricingResult.getTotalShipperCost());
    }

    /**
     * TestCase to test if Buy/Sell is picked by system when both Blanket and Buy/Sell exist for a carrier.
     *
     * @throws Exception
     */
    @Test
    public void testBuySellOverBlanketProfile() throws Exception {
        /*
         * Customer PLS Shipper has 2 Estes Express Lines carrier pricing profiles that are applicable to it :
         * Blanket(id:26) and Buy/Sell(id:4). The blanket has minimum cost of $100 and discount of 55% and
         * the Buy/Sell has minimum cost of $90 and discount of 80%. The pricing engine should pick the
         * Buy/Sell profile irrespective of the costs.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getEstesRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal(1000), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("800.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("700.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("200.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("300.00"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("44.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("86.50"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("244.00"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("386.50"), expResToEvaluate.getTotalShipperCost());
    }

    /**
     * TestCase to test scenario when either Buy or Sell value exists for a carrier pricing
     * profile for a lane, then that carrier should not be picked. For Buy/Sell pricing profile
     * both the buy value and sell value needs to be defined.
     *
     * @throws Exception
     */
    @Test
    public void testBuyAvailableSellNotAvailable() throws Exception {
        /*
         * Carrier Midland Transport Limited has Buy/Sell profile defined for customer PLS Shipper, where both
         * Buy value and Sell value is defined for a lane. Then the carrier should appear in the pricing
         * proposals list.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("MDLD".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNotNull(expResToEvaluate);

        /*
         * Carrier Midland Transport Limited has Buy/Sell profile defined for customer PLS Shipper, where Buy value
         * is defined for a lane but Sell value is not. Then the carrier should not appear in the pricing proposals
         * list.
         */
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(70L)), 12L, true);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("MDLD".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNull(expResToEvaluate);

        /*
         * Carrier Midland Transport Limited has Buy/Sell profile defined for customer PLS Shipper, where Buy value
         * is not defined for a lane but Sell value is defined. Then the carrier should not appear in the pricing proposals
         * list.
         */
        ltlPricDetServ.reactivatePricingDetails(new ArrayList<Long>(Arrays.asList(70L)), 12L);
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(69L)), 11L, true);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("MDLD".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * TestCases to test scenario's where the margin percentage and margin minimum dollar amount is defined at
     * 4 levels : 1) defined in carrier pricing profile lane specific(margin can be defined at carrier level
     * only for Blanket/CSP, CSP and Buy/Sell. For Blanket profile this level of margin would not exist) 2)
     * defined in customer pricing profile lane specific(Set margin) 3) defined in customer profile
     * default(Default Margin% and Default Margin Minimum $) and 4)default margin percentage and dollar amount
     * values defined for LTL network.
     *
     * The pricing engine should consider the margin percentage and margin minimum dollar amount defined at
     * carrier pricing profile lane specific. If it does not exist then consider customer pricing profile lane
     * specific. Finally if both of the above are not available then consider the default margin percentage
     * and minimum dollar amount defined in customer profile. If all the margin% and minimum$ is not defined
     * at first 3 levels and customer is not specified for the quote then consider the values defined at
     * network level(level 4)for LTL.
     *
     * If the margin% and min$ are defined in level 1 and/or level 2 then the shipper final linehaul should be
     * greater than (carrier final linehaul + margin$ amount) else shipper final linehaul = carrier final
     * linehaul + margin$ amount.
     *
     * It works Similarly for the accessorials at all 4 levels.
     *
     * If the margin% and minimum$ are defined at only level 3 and/or level 4 then the shipper total cost must
     * be greater than [carrier total cost + default minimum margin$ amount] else shipper total cost = carrier
     * total cost + default minimum margin$ amount and the accessorials shipper cost would be made equal to
     * its carrier cost.
     *
     * @throws Exception
     */
    @Test
    public void testMarginPercAndMinDollarAmtDefinedAtAll4Levels() throws Exception {
        /*
         * For customer MACSTEEL we have defined the margin% as 15% and Min$ as 55$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * Blanket/CSP pricing profile is defined for carrier Con-Way freight for MACSTEEL with margin%
         * set as 25% and Min$ amount set as 50$.
         * The margin% and Min$ defined in the carrier pricing profile(margin% set as 25% and Min$ amount
         * set as 50$) must be considered by the pricing logic.
         * Also as ShipperFinalLinehaul(~190) calculated with margin 25% is less than the
         * carrierFinalLinehaul+marginDollarAmount(150+50=200),the ShipperFinalLinehaul is 200.
         *
         * We have set the accessorial Residential(RES) for the carrier Con-Way freight for MACSTEEL
         * with margin% set as 25% and Min$ amount set as 5$.
         * For customer MACSTEEL we have defined the RES acc margin% as 22% and Min$ as 4$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The pricing logic would pick the margin% of 25% and as the ShipperAccessorialCost is 33.33$
         * which is greater than the CarrierAccessorialCost+Min$(25+5=30$), the ShipperAccessorialCost
         * is 33.34$.
         */
        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setShipperOrgId(2L);
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1333.33"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1133.33"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("200.00"), pricingResult.getShipperFinalLinehaul());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("33.33"), pricingResult.getAccessorials().get(0).getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("33.00"), pricingResult.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("53.50"), pricingResult.getShipperFuelSurcharge());

        /*
         * For customer : MACSTEEL we have defined the margin% as 15% and Min$ as 55$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * Blanket/CSP pricing profile is defined for carrier Con-Way freight for MACSTEEL with margin%
         * set as 10% and Min$ amount set as 50$.
         * The margin% and Min$ defined in the carrier pricing profile(margin% set as 10% and Min$ amount
         * set as 50$) must be considered by the pricing logic.
         * Also as ShipperFinalLinehaul(~165) calculated with margin 10% is less than the
         * carrierFinalLinehaul+marginDollarAmount(150+50=200),the ShipperFinalLinehaul is $200.
         *
         * We have set the accessorial Residential(RES) for the carrier Con-Way freight for MACSTEEL
         * with margin% set as 5% and Min$ amount set as 5$.
         * For customer MACSTEEL we have defined the RES acc margin% as 22% and Min$ as 4$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The pricing logic would pick the margin% of 5% and as the ShipperAccessorialCost is 26.32$
         * which is less than the CarrierAccessorialCost+Min$(25+5=30$), the ShipperAccessorialCost
         * is 30.00$.
         */
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(10L);
        pricDtlEntity.setMarginAmount(new BigDecimal("10"));
        LtlAccessorialsEntity accEntity = ltlAccessorialsDao.get(18L);
        accEntity.setUnitMargin(new BigDecimal("5"));
        accEntity.setApplyBeforeFuel(true);
        getSession().saveOrUpdate(accEntity);
        getSession().flush();
        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1333.33"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1133.33"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("200.00"), pricingResult.getShipperFinalLinehaul());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("30.00"), pricingResult.getAccessorials().get(0).getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("38.50"), pricingResult.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("59.00"), pricingResult.getShipperFuelSurcharge());
    }

    @Test
    public void testMarginPercAndMinDollarAmtDefinedAtOnly3Levels() throws Exception {
        /*
         * For customer : MACSTEEL we have defined the margin% as 15% and Min$ as 15$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The margin% and Min$ defined in the customer pricing details (margin% set as 15% and Min$ amount
         * set as 15$) must be considered by the pricing logic.
         * Also as ShipperFinalLinehaul(187.50) calculated with margin 15% is greater than the
         * carrierFinalLinehaul+marginDollarAmount(250+15=265),the ShipperFinalLinehaul is $187.50
         *
         * For customer MACSTEEL we have defined the RES acc margin% as 22% and Min$ as 4$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The pricing logic would pick the margin% of 22% and as the ShipperAccessorialCost is 31.25$
         * which is greater than the CarrierAccessorialCost+Min$(25+4=29$), the ShipperAccessorialCost
         * is 31.25$.
         */
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(10L);
        pricDtlEntity.setMarginAmount(null);
        pricDtlEntity.setMinMarginAmount(null);
        LtlAccessorialsEntity accEntity = ltlAccessorialsDao.get(18L);
        accEntity.setUnitMargin(null);
        accEntity.setMarginDollarAmt(null);
        LtlPricingDetailsEntity custPricDtlEntity = ltlPricDetServ.getPricingDetailById(29L);
        custPricDtlEntity.setMinMarginAmount(new BigDecimal("15"));
        getSession().flush();

        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setShipperOrgId(2L);
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1250.00"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1062.50"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("187.50"), pricingResult.getShipperFinalLinehaul());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("31.25"), pricingResult.getAccessorials().get(0).getShipperAccessorialCost());

        /*
         * For customer : MACSTEEL we have defined the margin% as 15% and Min$ as 55$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The margin% and Min$ defined in the customer pricing details (margin% set as 15% and Min$ amount
         *  set as 55$) must be considered by the pricing logic.
         * Also as ShipperFinalLinehaul(187.50) calculated with margin 15% is less than the
         * carrierFinalLinehaul+marginDollarAmount(150+55=205),the ShipperFinalLinehaul is $187.5.
         *
         * For customer MACSTEEL we have defined the RES acc margin% as 6% and Min$ as 4$ in pricing details.
         * Also default margin% is set to 20% and default Min$ amount is set to 60$ in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The pricing logic would pick the margin% of 30% and as the ShipperAccessorialCost is 35.71$
         * which is more than the CarrierAccessorialCost+Min$(25+4=29$), the ShipperAccessorialCost
         * is 35.71$.
         */
        custPricDtlEntity = ltlPricDetServ.getPricingDetailById(29L);
        custPricDtlEntity.setMinMarginAmount(new BigDecimal("100000")); // not taken into account as Margin
                                                                        // Amount is not specified.
        getSession().update(custPricDtlEntity);
        accEntity = ltlAccessorialsDao.get(18L);
        accEntity.setUnitMargin(new BigDecimal("30"));
        getSession().flush();
        getSession().clear();

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1250.00"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1062.50"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("187.50"), pricingResult.getShipperFinalLinehaul());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("35.71"), pricingResult.getAccessorials().get(0).getShipperAccessorialCost());
    }

    @Test
    public void testMarginPercAndMinDollarAmtDefinedAtOnly2Levels() throws Exception {
        /*
         * For customer MACSTEEL we have defined the default margin% as 20% and default Min$ amount as 60$
         * in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The customer pricing details and carrier margin pricing details are not defined for this customer.
         * Hence the default margin% and Min$ defined in the customer profile (margin% as 20% and Min$ amount
         * as 60$) must be considered by the pricing logic.
         * Also as TotalShipperCost(272.25) is greater than the TotalCarrierCost+minMarginDollarAmount(208+60=268),
         * the TotalShipperCost is $272.25.
         *
         * For RES accessorial the pricing logic should pick the default margin% of 20% and Minimum margin$ amount
         * of 0 would be considered. Hence the ShipperAccessorialCost would be $31.25.
         */
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(10L);
        pricDtlEntity.setMarginAmount(null);
        pricDtlEntity.setMinMarginAmount(null);
        LtlPricingDetailsEntity custPricDtlEntity = ltlPricDetServ.getPricingDetailById(71L);
        custPricDtlEntity.setStatus(Status.INACTIVE);
        LtlAccessorialsEntity carrierAccEntity = ltlAccessorialsDao.get(18L);
        carrierAccEntity.setUnitMargin(null);
        carrierAccEntity.setMarginDollarAmt(null);
        LtlAccessorialsEntity custAccEntity = ltlAccessorialsDao.get(329L);
        custAccEntity.setStatus(Status.INACTIVE);
        getSession().flush();

        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setShipperOrgId(2L);
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1250.00"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("1062.50"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("187.50"), pricingResult.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("33.00"), pricingResult.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("53.50"), pricingResult.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("208.00"), pricingResult.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("272.25"), pricingResult.getTotalShipperCost());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("31.25"), pricingResult.getAccessorials().get(0).getShipperAccessorialCost());

        /*
         * For customer : MACSTEEL we have defined the default margin% as 20% and default Min$ amount as 60$
         * in profile details.
         * The margin% and min$ is set as 8% and 15$ for LTL network.
         * The customer pricing details and carrier margin pricing details are not defined for this customer.
         * Hence the default margin% and Min$ defined in the customer profile (margin% as 20% and Min$ amount
         * as 60$) must be considered by the pricing logic.
         * Also as TotalShipperCost(272.25) is less than the TotalCarrierCost+minMarginDollarAmount(208+300=508),
         * the TotalShipperCost is $508. Also the accessorials shipper cost is made equal to its carrier cost.
         */
        OrganizationPricingEntity orgPricEntity = organizationPricingDao.get(2L);
        orgPricEntity.setDefaultMinMarginAmt(new BigDecimal("300"));
        getSession().flush();

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("1000.00"), pricingResult.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("3000.00"), pricingResult.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), pricingResult.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("2550.00"), pricingResult.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), pricingResult.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("450.00"), pricingResult.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("33.00"), pricingResult.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("33.00"), pricingResult.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("208.00"), pricingResult.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("508.00"), pricingResult.getTotalShipperCost());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("25.00"), pricingResult.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(pricingResult.getAccessorials().get(0).getShipperAccessorialCost(), pricingResult.getAccessorials().get(0)
                .getCarrierAccessorialCost());
    }

    @Test
    public void testMarginPercAndMinDollarAmtDefinedAtNetworkLevel() throws Exception {
        /*
         * The customer is not specified and the margin% and min$ are set as 8% and 15$ for LTL network.
         * Hence the margin% and Min$ defined for LTL network (margin% as 8% and Min$ amount as 15$)
         * must be considered by the pricing logic. Also only applicable blanket profiles would be returned.
         * Also as TotalShipperCost(573.91) is greater than the TotalCarrierCost+minMarginDollarAmount
         * (528+15=543), the TotalShipperCost is $573.91
         *
         * For RES accessorial the pricing logic should pick the default margin% of 8% and Minimum margin$ amount
         * of 0 would be considered. Hence the ShipperAccessorialCost would be $43.48
         */
        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.setShipperOrgId(null);
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        boolean containsNonBlanket = false;
        for (LtlPricingResult result : expectedResults) {
            if ("HMES".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
            if (PricingType.BLANKET != result.getPricingType()) {
                containsNonBlanket = true;
            }
        }
        Assert.assertFalse(containsNonBlanket);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1086.95"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("600.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("652.17"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("400.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("434.78"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("88.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("95.65"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("528.00"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("573.91"), expResToEvaluate.getTotalShipperCost());
        Assert.assertNotNull(expResToEvaluate.getAccessorials());
        Assert.assertFalse(expResToEvaluate.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("40.00"), expResToEvaluate.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("43.48"), expResToEvaluate.getAccessorials().get(0).getShipperAccessorialCost());


        /*
         * The customer is not specified and the margin% and min$ are set as 8% and 50$ for LTL network.
         * Hence the margin% and Min$ defined for PLS network (margin% as 8% and Min$ amount as 50$)
         * must be considered by the pricing logic.
         * Also as TotalShipperCost(573.91) is less than the TotalCarrierCost+minMarginDollarAmount
         * (528+50=578), the TotalShipperCost is $578. Also the accessorials shipper cost is made equal
         * to its carrier cost.
         */
        LtlPricNwDfltMarginEntity ltlPricNwDfltMarginEntity = ltlPricNwDfltMarginDAO.getDefaultLTLMargin();
        ltlPricNwDfltMarginEntity.setMinMarginAmt(new BigDecimal("50"));
        ltlPricNwDfltMarginDAO.saveOrUpdate(ltlPricNwDfltMarginEntity);
        getSession().flush();

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = null;
        containsNonBlanket = false;
        for (LtlPricingResult result : expectedResults) {
            if ("HMES".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
            if (PricingType.BLANKET != result.getPricingType()) {
                containsNonBlanket = true;
            }
        }
        Assert.assertFalse(containsNonBlanket);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1125.00"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("600.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("675.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("400.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("450.00"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("88.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("88.00"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("528.00"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("578.00"), expResToEvaluate.getTotalShipperCost());
        Assert.assertNotNull(expResToEvaluate.getAccessorials());
        Assert.assertFalse(expResToEvaluate.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("40.00"), expResToEvaluate.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(expResToEvaluate.getAccessorials().get(0).getShipperAccessorialCost(), expResToEvaluate.getAccessorials().get(0)
                .getCarrierAccessorialCost());
    }

    /**
     * Test case for scenario where Gainshare customer has a Benchmark pricing profile defined.
     * Expected result : Carriers must be displayed during get quotes.
     * @throws Exception
     */
//    @Test
    public void testGainshareCustomerWithBenchamarkSpecified() throws Exception {
        OrganizationPricingEntity entity = organizationPricingDao.get(1L);
        entity.setGainshare(StatusYesNo.YES);
        entity.setGsCustPct(new BigDecimal("40"));
        entity.setGsPlsPct(new BigDecimal("60"));
        organizationPricingDao.saveOrUpdate(entity);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
    }

    /**
     * Test case for scenario where Gainshare customer has no Benchmark defined.
     * Expected result : No carriers must be displayed during get quotes.
     * @throws Exception
     */
//    @Test
    public void testGainshareCustomerWithNoBenchamarkSpecified() throws Exception {
        OrganizationPricingEntity entity = organizationPricingDao.get(1L);
        entity.setGainshare(StatusYesNo.YES);
        entity.setGsCustPct(new BigDecimal("40"));
        entity.setGsPlsPct(new BigDecimal("60"));
        organizationPricingDao.saveOrUpdate(entity);
        LtlPricingProfileEntity pricProfEntity = ltlPricingProfileDao.get(6L);
        pricProfEntity.setStatus(Status.INACTIVE);
        ltlPricingProfileDao.saveOrUpdate(pricProfEntity);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNull(expectedResults);
    }

    /**
     * If a customer is specified as Gainshare customer then we need to set the PLS% and Customer%.
     * It can be set at 2 levels :
     * 1) Carrier specific level - applies only to that specific carrier, for rest percentages
     * mentioned at customer level would be applied.
     * 2) Customer level - applies to all the carriers.
     *
     * Test case for scenario where PLS% is specified for a carrier at Carrier specific level for
     * a Gainshare customer and also specified at customer level.
     *
     * Expected Result : For that particular carrier the PLS% would be pulled from the carrier
     * specific level and for rest of the carrier PLS% would be pulled from the customer level
     * during get quotes for pricing calculation.
     * @throws Exception
     */
    @Test
    public void testGainshareCustomerAtCarrierSpecificLevel() throws Exception {

    }

    /**
     * Test case for scenario where PLS% is specified only at customer level.
     *
     * Expected Result : For all of the carriers PLS% would be pulled from the customer level
     * during get quotes for pricing calculation.
     * @throws Exception
     */
    @Test
    public void testGainshareCustomerAtCustomerSpecificlevel() throws Exception {

    }

    /**
     * Test case to test scenario when a Benchmark has been specified for a Customer for specific
     * Carrier.
     * Expected Result : The Benchmark cost information should be pulled only for the specified
     * carrier.
     * @throws Exception
     */
    @Test
    public void testIfBenchmarkCostIsPulledForSpecificCarrier() throws Exception {
        //Here we have defined Benchmark for Customer PLS Shipper for the carrier
        //Fedex Freight Economy. Hence when we do get quotes for PLS Shipper the
        //Benchmark cost information has to be pulled only for Fedex Freight Economy.
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getFedexRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("300.00"), pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertEquals(new BigDecimal("700.00"), pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("66.00"), pricingResult.getBenchmarkFuelSurcharge());
        pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertNull(pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertNull(pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertNull(pricingResult.getBenchmarkFuelSurcharge());
    }

    /**
     * Test case to test scenario when a Benchmark has been specified for a Customer for all
     * carriers.
     * Expected Result : The Benchmark cost information should be pulled for all the carriers.
     * @throws Exception
     */
    @Test
    public void testIfBenchmarkCostIsPulledForAllCarriers() throws Exception {
        //Here we have defined Benchmark for Customer PLS Shipper for all the carriers.
        //Hence when we do get quotes for PLS Shipper the Benchmark cost information has
        //to be pulled for all the carriers.
        LtlPricingProfileEntity pricProfEntity = ltlPricingProfileDao.get(6L);
        pricProfEntity.setCarrierOrganization(null);
        ltlPricingProfileDao.saveOrUpdate(pricProfEntity);
        getSession().flush();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getFedexRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("300.00"), pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertEquals(new BigDecimal("700.00"), pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("66.00"), pricingResult.getBenchmarkFuelSurcharge());
        pricingResult = getConwayRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("300.00"), pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertEquals(new BigDecimal("700.00"), pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("66.00"), pricingResult.getBenchmarkFuelSurcharge());
    }

    /**
     * Test case to test scenario when a Benchmark has been specified for a Customer for specific
     * Carrier. The Include Benchmark Accessorial is not selected.
     * Expected Result : Benchmark accessorials cost information should not be pulled. The Benchmark
     * accessorials are assigned the Shipper accessorials cost.
     * @throws Exception
     */
    @Test
    public void testIfBmAccCostIsPulledWhenIncludeBmAccNotSelected() throws Exception {
        // Here we have defined Benchmark for Customer PLS Shipper for the carrier
        // Fedex Freight Economy.
        GetOrderRatesCO ratesCO = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getFedexRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("300.00"), pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertEquals(new BigDecimal("700.00"), pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("66.00"), pricingResult.getBenchmarkFuelSurcharge());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(pricingResult.getAccessorials().get(0).getBenchmarkAccessorialCost(), pricingResult.getAccessorials().get(0)
                .getShipperAccessorialCost());
    }

    private LtlPricingResult getFedexRate(List<LtlPricingResult> expectedResults) {
        return expectedResults.stream().filter(result -> FEDEX_FREIGHT_ECONOMY.equalsIgnoreCase(result.getScac())).findFirst().orElse(null);
    }

    private LtlPricingResult getConwayRate(List<LtlPricingResult> expectedResults) {
        return expectedResults.stream().filter(result -> "CNWY".equalsIgnoreCase(result.getScac())).findFirst().orElse(null);
    }

    private LtlPricingResult getYrcRate(List<LtlPricingResult> expectedResults) {
        return expectedResults.stream().filter(result -> YRC_FREIGHT_SCAC.equalsIgnoreCase(result.getScac())).findFirst().orElse(null);
    }

    private LtlPricingResult getEstesRate(List<LtlPricingResult> expectedResults) {
        return expectedResults.stream().filter(result -> ESTES_EXPRESS_LINES_SCAC.equalsIgnoreCase(result.getScac())).findFirst().orElse(null);
    }

    /**
     * Test case to test scenario when a Benchmark has been specified for a Customer for specific
     * Carrier. The Include Benchmark Accessorial is selected.
     * Expected Result : Benchmark accessorials cost information should be pulled.
     * @throws Exception
     */
    @Test
    public void testIfBmAccCostIsPulledWhenIncludeBmAccSelected() throws Exception {
        // Here we have defined Benchmark for Customer PLS Shipper for the carrier
        // Fedex Freight Economy.
        organizationPricingDao.get(1L).setIncludeBenchmarkAcc(StatusYesNo.YES);
        getSession().flush();
        GetOrderRatesCO ratesCO = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult pricingResult = getFedexRate(expectedResults);
        Assert.assertNotNull(pricingResult);
        Assert.assertEquals(new BigDecimal("300.00"), pricingResult.getBenchmarkFinalLinehaul());
        // Assert.assertEquals(new BigDecimal("700.00"), pricingResult.getBenchmarkLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("66.00"), pricingResult.getBenchmarkFuelSurcharge());
        Assert.assertNotNull(pricingResult.getAccessorials());
        Assert.assertFalse(pricingResult.getAccessorials().isEmpty());
        Assert.assertEquals(new BigDecimal("65.00"), pricingResult.getAccessorials().get(0).getBenchmarkAccessorialCost());
    }

    /**
     * Should block carrier lane for specified origin and destination. Carrier should not be shown.
     */
    @Test
    public void shouldBlockCarrierLane() throws Exception {
        LtlBlockCarrGeoServicesEntity blockEntity = new LtlBlockCarrGeoServicesEntity();
        blockEntity.setStatus(Status.ACTIVE);
        blockEntity.setProfileId(10L);
        blockEntity.getLtlBkCarrOriginGeoServiceDetails()
                .add(new LtlBlockCarrierGeoServDetailsEntity(blockEntity, "PA", GeoType.ORIGIN, 6, "PA"));
        blockEntity.getLtlBkCarrDestGeoServiceDetails()
                .add(new LtlBlockCarrierGeoServDetailsEntity(blockEntity, "IL", GeoType.DESTINATION, 6, "IL"));
        getSession().save(blockEntity);
        getSession().flush();

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());

        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 21) {
                Assert.fail("Carrier lane is expected to be blocked");
            }
        }
    }

    /**
     * TestCase to test Block carrier service type scenario when carrier_type is “0”(NONE) and service type is
     * “INDIRECT”. Expected: Carriers with both direct and indirect service type should show. No carrier
     * should be blocked.
     * 
     * @throws Exception
     */
    @Test
    public void testBlkNoCarrierTypeIndirectServiceTypeSelected() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        OrganizationPricingEntity orgPricing = organizationPricingDao.getActivePricing(criteria.getShipperOrgId());
        orgPricing.setBlkServCarrierType(null);
        orgPricing.setBlkServiceType(null);
        organizationPricingDao.saveOrUpdate(orgPricing);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        int count = 0;
        // Carrier Org with id 21 has its service type as INDIRECT and
        // Carrier Org with id 18 has its service type as DIRECT.
        // The result should contain all carrier org's irrespective of their service type
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 21) {
                count++;
            }
            if (ltlPricingResult.getCarrierOrgId() == 18) {
                count++;
            }
        }
        Assert.assertTrue(count == 2);
    }

    /**
     * TestCase to test Block carrier service type scenario when carrier_type is “0” and service type is
     * “DIRECT”.
     * Expected: Carriers with both direct and indirect service type should show. No carrier should
     * be blocked.
     * @throws Exception
     */
    @Test
    public void testBlkNoCarrierTypeDirectServiceTypeSelected() throws Exception {

        GetOrderRatesCO criteria = getCriteria();
        OrganizationPricingEntity orgPricing = organizationPricingDao.getActivePricing(criteria.getShipperOrgId());
        orgPricing.setBlkServCarrierType(LtlBlkServCarrierType.NONE);
        orgPricing.setBlkServiceType(null);
        organizationPricingDao.saveOrUpdate(orgPricing);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        int count = 0;
        // Carrier Org with id 21 has its service type as INDIRECT and
        // Carrier Org with id 18 has its service type as DIRECT.
        // The result should contain all carrier org's irrespective of their service type
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 21) {
                count++;
            }
            if (ltlPricingResult.getCarrierOrgId() == 18) {
                count++;
            }
        }
        Assert.assertTrue(count == 2);
    }

    /**
     * TestCase to test Block carrier service type scenario when carrier_type is “1” and service type is
     * “INDIRECT”.
     * Expected: Only direct carriers should show.
     * @throws Exception
     */
    @Test
    public void testAllCarriersIndirectServiceTypeSelected() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        OrganizationPricingEntity orgPricing = organizationPricingDao.getActivePricing(criteria.getShipperOrgId());
        orgPricing.setBlkServCarrierType(LtlBlkServCarrierType.ALL_CARRIER);
        orgPricing.setBlkServiceType(com.pls.core.domain.enums.LtlServiceType.INDIRECT);
        organizationPricingDao.saveOrUpdate(orgPricing);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        int count = 0;
        // Carrier Org with id 21 has its service type as INDIRECT and
        // Carrier Org with id 18 has its service type as DIRECT.
        // The result should contain all and only carrier org's with their service type as DIRECT.
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            Assert.assertNotEquals(LtlServiceType.INDIRECT, ltlPricingResult.getServiceType());
            Assert.assertFalse(ltlPricingResult.getCarrierOrgId() == 21);
            if (ltlPricingResult.getCarrierOrgId() == 18) {
                count++;
            }
        }
        Assert.assertTrue(count == 1);
    }

    /**
     * TestCase to test Block carrier service type scenario when carrier_type is “null” and service type is
     * “INDIRECT” Expected: Carriers with both direct and indirect service type should show. No carrier should
     * be blocked.
     *
     * @throws Exception
     */
    @Test
    public void testCarrierTypeNullServiceTypeIndirect() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        OrganizationPricingEntity orgPricing = organizationPricingDao.getActivePricing(criteria.getShipperOrgId());
        orgPricing.setBlkServCarrierType(null);
        orgPricing.setBlkServiceType(com.pls.core.domain.enums.LtlServiceType.INDIRECT);
        organizationPricingDao.saveOrUpdate(orgPricing);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        int count = 0;
        // Carrier Org with id 21 has its service type as INDIRECT and Carrier Org with id 18 has its service
        // type as DIRECT.
        // The result should contain carrier org's with id 21 and 18, as no service type is blocked
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 21) {
                count++;
            }
            if (ltlPricingResult.getCarrierOrgId() == 18) {
                count++;
            }
        }
        Assert.assertTrue(count == 2);
    }

    /**
     * TestCase to test Block carrier service type scenario when carrier_type is “null” and service type is
     * “DIRECT” Expected: Carriers with both direct and indirect service type should show. No carrier should
     * be blocked.
     *
     * @throws Exception
     */
    @Test
    public void testCarrierTypeNullServiceTypeDirect() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        OrganizationPricingEntity orgPricing = organizationPricingDao.getActivePricing(criteria.getShipperOrgId());
        orgPricing.setBlkServCarrierType(null);
        orgPricing.setBlkServiceType(com.pls.core.domain.enums.LtlServiceType.DIRECT);
        organizationPricingDao.saveOrUpdate(orgPricing);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        int count = 0;
        // Carrier Org with id 21 has its service type as INDIRECT and
        // Carrier Org with id 18 has its service type as DIRECT.
        // The result should contain carrier org's with id 21 and 18, as no service type is blocked
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 21) {
                count++;
            }
            if (ltlPricingResult.getCarrierOrgId() == 18) {
                count++;
            }
        }
        Assert.assertTrue(count == 2);
    }

    /**
     * TestCase to test scenario when origin/destination is Canadian postal code.
     * The pricing is set with RDWY as below :
     * -- Origin = USA, Destination = USA and with discount = 80%.
     * -- Origin = 16066, Destination = 61760 and with discount = 72%.
     * -- Origin = 16066, Destination = CAN and with discount = 70.
     * -- Origin = 16066, Destination = BC and with discount = 65.
     * -- Origin = 16066, Destination = ABBOTSFORD and with discount = 60.
     * -- Origin = 16066, Destination = V2J3H9 - V2S0B1 and with discount = 55.
     * -- Origin = 16066, Destination = V2S0A1 and with discount = 50.
     * The pricing with Origin = 16066 and Destination = V2S0A1 should show up as it is set with more detailed
     * origin/destination values.
     * Below are the settings in testdata. Pricing Type = Blanket Discount % = 50% Minimum Cost = 140
     * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
     * "PLS Shipper")
     * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
     * 1111.11 Carrier Discount = 500.00 Shipper Discount = 555.55 Carrier Final Linehaul = 500.00 Shipper
     * Final Linehaul = 555.56 Carrier.
     * @throws Exception
     */
    @Test
    public void testWithCanadianAddressFullPostalCode() throws Exception {
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteriaWithCanadianAddr());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.12"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("500.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("555.56"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("500.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("555.56"), expResToEvaluate.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test scenario when origin/destination is Canadian postal code.
     * The pricing is set with RDWY as below :
     * -- Origin = USA, Destination = USA and with discount = 80%.
     * -- Origin = 16066, Destination = 61760 and with discount = 72%.
     * -- Origin = 16066, Destination = CAN and with discount = 70.
     * -- Origin = 16066, Destination = BC and with discount = 65.
     * -- Origin = 16066, Destination = ABBOTSFORD and with discount = 60.
     * -- Origin = 16066, Destination = V2J3H9 - V2S0B1 and with discount = 55.
     * The pricing with Origin = 16066 and Destination = V2J3H9 - V2S0B1 should show up as it is set with more detailed
     * origin/destination values.
     * Below are the settings in testdata. Pricing Type = Blanket Discount % = 55% Minimum Cost = 140
     * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
     * "PLS Shipper")
     * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
     * 1111.11 Carrier Discount = 550.00 Shipper Discount = 611.11 Carrier Final Linehaul = 450.00 Shipper
     * Final Linehaul = 500.00 Carrier.
     * @throws Exception
     */
    @Test
    public void testWithCanadianAddressFullPostalCodeRange() throws Exception {
        SecurityTestUtils.login("Test", 1L);
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(65L)), 40L, true);
        getSession().flush();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteriaWithCanadianAddr());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.11"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("550.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("611.11"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("450.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("500.00"), expResToEvaluate.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test scenario when origin/destination is Canadian postal code.
     * The pricing is set with RDWY as below :
     * -- Origin = USA, Destination = USA and with discount = 80%.
     * -- Origin = 16066, Destination = 61760 and with discount = 72%.
     * -- Origin = 16066, Destination = CAN and with discount = 70.
     * -- Origin = 16066, Destination = BC and with discount = 65.
     * -- Origin = 16066, Destination = ABBOTSFORD and with discount = 60.
     * The pricing with Origin = 16066 and Destination = ABBOTSFORD should show up as it is set with more detailed
     * origin/destination values.
     * Below are the settings in testdata. Pricing Type = Blanket Discount % = 60% Minimum Cost = 140
     * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
     * "PLS Shipper")
     * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
     * 1111.11 Carrier Discount = 600.00 Shipper Discount = 666.67 Carrier Final Linehaul = 400.00 Shipper
     * Final Linehaul = 444.45 Carrier.
     * @throws Exception
     */
    @Test
    public void testWithCanadianAddressWithCityPricingProfile() throws Exception {
        SecurityTestUtils.login("Test", 1L);
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(64L, 65L)), 40L, true);
        getSession().flush();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteriaWithCanadianAddr());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.10"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("600.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("666.66"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("400.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("444.44"), expResToEvaluate.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test scenario when origin/destination is Canadian postal code.
     * The pricing is set with RDWY as below :
     * -- Origin = USA, Destination = USA and with discount = 80%.
     * -- Origin = 16066, Destination = 61760 and with discount = 72%.
     * -- Origin = 16066, Destination = CAN and with discount = 70.
     * -- Origin = 16066, Destination = BC and with discount = 65.
     * The pricing with Origin = 16066 and Destination = BC should show up as it is set with more detailed
     * origin/destination values.
     * Below are the settings in testdata. Pricing Type = Blanket Discount % = 65% Minimum Cost = 140
     * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
     * "PLS Shipper")
     * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
     * 1111.11 Carrier Discount = 650.00 Shipper Discount = 722.22 Carrier Final Linehaul = 350.00 Shipper
     * Final Linehaul = 388.89 Carrier.
     * @throws Exception
     */
    @Test
    public void testWithCanadianAddressWithStatePricingProfile() throws Exception {
        SecurityTestUtils.login("Test", 1L);
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(63L, 64L, 65L)), 40L, true);
        getSession().flush();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteriaWithCanadianAddr());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.11"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("650.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("722.22"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("350.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("388.89"), expResToEvaluate.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test scenario when origin/destination is Canadian postal code.
     * The pricing is set with RDWY as below :
     * -- Origin = USA, Destination = USA and with discount = 80%.
     * -- Origin = 16066, Destination = 61760 and with discount = 72%.
     * -- Origin = 16066, Destination = CAN and with discount = 70.
     * The pricing with Origin = 16066 and Destination = CAN should show up as it is set with more detailed
     * origin/destination values.
     * Below are the settings in testdata. Pricing Type = Blanket Discount % = 65% Minimum Cost = 140
     * Service Type = DIRECT Movement Type = Both Margin % = 10 % (Default Margin on Customer
     * "PLS Shipper")
     * Based on this information, final Carrier Initial Linehaul = 1000.00 Shipper Initial Linehaul =
     * 1111.11 Carrier Discount = 700.00 Shipper Discount = 777.78 Carrier Final Linehaul = 300.00 Shipper
     * Final Linehaul = 333.34 Carrier.
     * @throws Exception
     */
    @Test
    public void testWithCanadianAddressWithCountryPricingProfile() throws Exception {
        SecurityTestUtils.login("Test", 1L);
        ltlPricDetServ.inactivatePricingDetails(new ArrayList<Long>(Arrays.asList(62L, 63L, 64L, 65L)), 40L, true);
        getSession().flush();
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteriaWithCanadianAddr());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1111.10"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("700.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("777.77"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("300.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("333.33"), expResToEvaluate.getShipperFinalLinehaul());
    }

    /**
     * Testcase to test scenario where there is valid buy/sell pricing available for a lane and use blanket
     * flag is set on the sell profile. Also a valid blanket pricing is available for the same lane for same
     * carrier.
     * Expected Output : The pricing engine should pick the blanket pricing instead of the sell pricing.
     * @throws Exception
     */
    @Test
    public void testUseBlanketSetForSellPricingProfile() throws Exception {

        //Set the Sell pricing profile to use the corresponding blanket pricing profile of that carrier.
        LtlPricingProfileEntity pricingProfile = profileDetailsService.getProfileById(4L);
        Set<LtlPricingProfileDetailsEntity> profileDetails = pricingProfile.getProfileDetails();
        for (LtlPricingProfileDetailsEntity pricingProfileDetails : profileDetails) {
            if (pricingProfileDetails.getPricingDetailType().compareTo(PricingDetailType.SELL) == 0) {
                pricingProfileDetails.setUseBlanket("Y");
            }
            profileDetails.add(pricingProfileDetails);
        }
        pricingProfile.setProfileDetails(profileDetails);
        profileDetailsService.saveProfile(pricingProfile);
        getSession().flush();

        /*
         * Customer PLS Shipper has 2 Estes Express Lines carrier pricing profiles that are applicable to it :
         * Blanket(id:26) and Buy/Sell(id:4). The blanket has minimum cost of $100 and discount of 55% and the
         * Buy/Sell has minimum cost of $90 and discount of 80% for Buy and minimum cost of $90 and discount
         * of 70% for Sell. The pricing engine should pick the blanket profile instead of Sell profile,
         * irrespective of the costs.
         */
        GetOrderRatesCO ratesCO = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);
        ratesCO.setGuaranteedTime(1000);
        // ratesCO.setScac(scac);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getEstesRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal(1000), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("800.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("550.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("200.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("450.00"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("48.40"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("112.20"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("20.00"), expResToEvaluate.getGuaranteed().getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("60.00"), expResToEvaluate.getGuaranteed().getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("20.00"), expResToEvaluate.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("36.00"), expResToEvaluate.getAccessorials().get(0).getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("288.40"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("658.20"), expResToEvaluate.getTotalShipperCost());
    }

    /**
     * Testcase to test scenario where there is valid buy/sell pricing available for a lane and use blanket
     * flag is set on the buy profile. Also a valid blanket pricing is available for the same lane for same
     * carrier.
     * Expected Output : The pricing engine should pick the blanket pricing instead of the buy pricing.
     * @throws Exception
     */
    @Test
    public void testUseBlanketSetForBuyPricingProfile() throws Exception {

        //Set the Buy pricing profile to use the corresponding blanket pricing profile of that carrier.
        LtlPricingProfileEntity pricingProfile = profileDetailsService.getProfileById(4L);
        Set<LtlPricingProfileDetailsEntity> profileDetails = pricingProfile.getProfileDetails();
        for (LtlPricingProfileDetailsEntity pricingProfileDetails : profileDetails) {
            if (pricingProfileDetails.getPricingDetailType().compareTo(PricingDetailType.BUY) == 0) {
                pricingProfileDetails.setUseBlanket("Y");
            }
            profileDetails.add(pricingProfileDetails);
        }
        pricingProfile.setProfileDetails(profileDetails);
        profileDetailsService.saveProfile(pricingProfile);
        getSession().flush();

        /*
         * Customer PLS Shipper has 2 Estes Express Lines carrier pricing profiles that are applicable to it :
         * Blanket(id:26) and Buy/Sell(id:4). The blanket has minimum cost of $100 and discount of 55% and the
         * Buy/Sell has minimum cost of $90 and discount of 80% for Buy and minimum cost of $90 and discount
         * of 70% for Sell. The pricing engine should pick the blanket profile instead of Buy profile,
         * irrespective of the costs.
         */
        GetOrderRatesCO ratesCO = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add("RES");
        ratesCO.setAccessorialTypes(accList);
        ratesCO.setGuaranteedTime(1000);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getEstesRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal(1000), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("550.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("700.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("450.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("300.00"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("107.80"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("97.50"), expResToEvaluate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("40.00"), expResToEvaluate.getGuaranteed().getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("50.00"), expResToEvaluate.getGuaranteed().getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("36.00"), expResToEvaluate.getAccessorials().get(0).getCarrierAccessorialCost());
        Assert.assertEquals(new BigDecimal("45.00"), expResToEvaluate.getAccessorials().get(0).getShipperAccessorialCost());
        Assert.assertEquals(new BigDecimal("633.80"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("492.50"), expResToEvaluate.getTotalShipperCost());
    }

    /**
     * Testcase to test scenario where there is valid sell pricing available for a lane but the buy pricing
     * has not been setup and use blanket flag is set on the buy profile. Also 2 valid blanket pricing's are
     * available for the same lane for same carrier. Expected Output : The pricing engine should pick a
     * blanket pricing based on hierarchy instead of the buy pricing.
     *
     * @throws Exception
     */
    @Test
    public void testUseBlanketSetForBuyPricProfWithNoPricSetup() throws Exception {

        LtlPricingProfileEntity pricingProfile = profileDetailsService.getProfileById(51L);
        pricingProfile.setStatus(Status.ACTIVE);
        profileDetailsService.saveProfile(pricingProfile);
        getSession().flush();
        /*
         * Customer PLS Shipper has 2 Fedex Freight Economy carrier pricing profiles that are applicable to it
         * : Blanket(id:28) and Buy/Sell(id:51).
         * The blanket has 2 valid lane pricing's available :
         * 1)minimum cost of $140 and discount of 80% from USA to USA.
         * 2)minimum cost of $120 and discount of 85% from PA to IL.
         * The Buy/Sell has minimum cost of $150 and discount of 75% for Sell and the buy profile
         * has no pricing set up. The pricing engine should pick the blanket profile from PA to IL(due to
         * hierarchy) instead of Buy profile, irrespective of whether buy profile has any pricing's set up.
         */
        List<LtlPricingResult> results = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(results);
        LtlPricingResult fedExRate = getFedexRate(results);
        Assert.assertNotNull(fedExRate);
        Assert.assertEquals(new BigDecimal("1000.00"), fedExRate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal(1000), fedExRate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("850.00"), fedExRate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("750.00"), fedExRate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("150.00"), fedExRate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("250.00"), fedExRate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("33.00"), fedExRate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("55.00"), fedExRate.getShipperFuelSurcharge());
        Assert.assertEquals(new BigDecimal("183.00"), fedExRate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("305.00"), fedExRate.getTotalShipperCost());
    }

    /**
     * Test case to verify if the Benchmark for a customer created as a carrier CSP pricing profile
     * is being pulled as part of the list of pricing proposals.
     * @throws Exception
     */
    @Test
    public void testBenchmarkCreatedAsCarrierPricingProfile() throws Exception {
        //Here the benchmark pricing details for customer PLS shipper for specific carrier RLCA
        //is setup as carrier CSP pricing profile with SCAC RLC1. This benchmark pricing details
        //would be pulled as part of the pricing proposals to enable users to compare multiple
        //carrier specific benchmark pricing details.
        GetOrderRatesCO rates = getCriteria();

        rates.setMilemakerMiles(500);
        rates.setPcmilerMiles(500);
        rates.getOriginAddress().setFormattedPostalCode(rates.getOriginAddress().getPostalCode());
        rates.getDestinationAddress().setFormattedPostalCode(rates.getDestinationAddress().getPostalCode());

        List<LtlRatingProfileVO> carrierRates = ratingEngineDao.getCarrierRates(rates, Arrays.asList(-1L));
        Assert.assertNotNull(carrierRates);
        for (LtlRatingProfileVO carrRate : carrierRates) {
            if ("RLC1".equalsIgnoreCase(carrRate.getScac())) {
                Assert.assertEquals("RLCA", carrRate.getActualCarrierScac());
            }
        }

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("RLC1".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNotNull(expResToEvaluate);
    }

    /**
     * TestCase to test if blocked lane for specific blanket carrier profile is considered by rating engine when
     * user specified lane matches the blocked lane for particular shipper.
     *
     * @throws Exception
     */
    @Test
    public void testBlkLnForSpecificCarrier() throws Exception {

        // For customer PLS Shipper we have setup a block lane for specific carrier - USF Holland(HMES) from
        // PA to IL. As this block lane is currently inactive blanket HMES pricing profile must be part of the
        // pricing results along with other blanket pricing profiles like UPS Freight A(UPGF).
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        boolean hmes = false;
        boolean upgf = false;
        for (LtlPricingResult result : expectedResults) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
        }
        Assert.assertTrue(hmes);
        Assert.assertTrue(upgf);

        // When the blocked lane is inactive but the carrier HMES is blocked by customer then HMES
        // must not be part of the pricing results. This should not affect other valid profiles.
        LtlPricingBlockedCustomersEntity blockedEntity = new LtlPricingBlockedCustomersEntity();
        blockedEntity.setLtlPricingProfileId(46L);
        blockedEntity.setShipperOrgId(1L);
        blockedEntity.setStatus(Status.ACTIVE);
        LtlPricingBlockedCustomersEntity savedBlkCustEntity = blockedCustomerDao.saveOrUpdate(blockedEntity);
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        hmes = false;
        upgf = false;
        for (LtlPricingResult result : expectedResults) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
        }
        Assert.assertFalse(hmes);
        Assert.assertTrue(upgf);
        blockedEntity = blockedCustomerDao.get(savedBlkCustEntity.getId());
        blockedEntity.setStatus(Status.INACTIVE);
        blockedCustomerDao.saveOrUpdate(blockedEntity);
        getSession().flush();

        // When we activate this blocked lane, HMES pricing profile must not be part of the pricing results
        // but other blanket profiles must not be affected by it. Hence UPGF must be part of the pricing
        // results.
        List<Long> blockedLaneIds = new ArrayList<Long>();
        blockedLaneIds.add(1L);
        ltlBlockLaneDao.updateStatusOfBlockedLanes(blockedLaneIds, Status.ACTIVE,
                SecurityUtils.getCurrentPersonId());
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        hmes = false;
        upgf = false;
        for (LtlPricingResult result : expectedResults) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
        }
        Assert.assertFalse(hmes);
        Assert.assertTrue(upgf);

        // The block lane setup for customer PLS shipper for carrier HMES must only affect PLS Shipper and not
        // other customers. Hence pricing results for customer Macsteel must include HMES as part of its
        // pricing results.
        GetOrderRatesCO criteria = getCriteria();
        criteria.setShipperOrgId(2L);
        expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        hmes = false;
        for (LtlPricingResult result : expectedResults) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
        }
        Assert.assertTrue(hmes);

        // When we expire this blocked lane, HMES must be part of the pricing results along with other blanket
        // profiles.
        ltlBlockLaneDao.expireBlockedLanes(blockedLaneIds, SecurityUtils.getCurrentPersonId());
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        hmes = false;
        upgf = false;
        for (LtlPricingResult result : expectedResults) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
        }
        Assert.assertTrue(hmes);
        Assert.assertTrue(upgf);

        // The blocked lane must be applicable only on the blanket profile of that carrier. Block lane has
        // been set on the Estes Express lines(EXLA) carrier from 16066 to 61760. EXLA has both buy/sell
        // profile and blanket profile setup that are applicable to PLS Shipper. As we have customer specific
        // profile setup buy/sell profile with id 4 would be part of the pricing results and would not be
        // affected by the block lane setup.
        blockedLaneIds.clear();
        blockedLaneIds.add(3L);
        ltlBlockLaneDao.updateStatusOfBlockedLanes(blockedLaneIds, Status.ACTIVE,
                SecurityUtils.getCurrentPersonId());
        getSession().flush();
        expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        Assert.assertNotNull(getEstesRate(expectedResults));

    }

    /**
     * TestCase to test if blocked lane specified for all blanket carrier's profile is considered by rating engine when
     * user specified lane matches the blocked lane for particular shipper.
     *
     * @throws Exception
     */
    @Test
    public void testBlkLnForAllCarriers() throws Exception {

        // For customer PLS Shipper we have setup a block lane for all carriers from 160 to 617. As this block
        // lane is currently inactive all the valid applicable blanket profiles must be part of the pricing
        // results. This should not affect customer specific profiles.
        List<LtlPricingResult> results = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(results);
        Assert.assertEquals(results.size(), 23);
        boolean hmes = false;
        boolean upgf = false;
        boolean exla = false;
        for (LtlPricingResult result : results) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
                Assert.assertEquals(result.getProfileId().longValue(), 46L);
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
                Assert.assertEquals(result.getProfileId().longValue(), 36L);
            }
            if (ESTES_EXPRESS_LINES_SCAC.equalsIgnoreCase(result.getScac())) {
                exla = true;
                Assert.assertEquals(result.getProfileId().longValue(), 4L);
            }
        }
        Assert.assertTrue(hmes);
        Assert.assertTrue(upgf);
        Assert.assertTrue(exla);

        // When we activate this blocked lane, all the blanket pricing profiles must not be part of the
        // pricing results. Customer specific profiles must not be affected by it.
        List<Long> blockedLaneIds = new ArrayList<Long>();
        blockedLaneIds.add(2L);
        ltlBlockLaneDao.updateStatusOfBlockedLanes(blockedLaneIds, Status.ACTIVE,
                SecurityUtils.getCurrentPersonId());
        getSession().flush();
        results = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(results);
        Assert.assertEquals(results.size(), 5);
        hmes = false;
        upgf = false;
        exla = false;
        for (LtlPricingResult result : results) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
            if (ESTES_EXPRESS_LINES_SCAC.equalsIgnoreCase(result.getScac())) {
                exla = true;
                Assert.assertEquals(result.getProfileId().longValue(), 4L);
            }
        }
        Assert.assertFalse(hmes);
        Assert.assertFalse(upgf);
        Assert.assertTrue(exla);

        // The block lane setup for customer PLS shipper for all carriers must only affect PLS Shipper and not
        // other customers. Hence pricing results for customer Macsteel must include all blanket carrier
        // profiles as part of its pricing results.
        GetOrderRatesCO criteria = getCriteria();
        criteria.setShipperOrgId(2L);
        results = pricingEng.getRates(criteria);
        Assert.assertNotNull(results);
        Assert.assertEquals(results.size(), 21);
        hmes = false;
        for (LtlPricingResult result : results) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
            }
            if (ESTES_EXPRESS_LINES_SCAC.equalsIgnoreCase(result.getScac())) {
                exla = true;
                Assert.assertEquals(result.getProfileId().longValue(), 26L);
            }
        }
        Assert.assertTrue(hmes);
        Assert.assertTrue(upgf);
        Assert.assertTrue(exla);

        // When we expire this blocked lane, all applicable valid blanket carrier profiles must be part of the
        // pricing results.
        ltlBlockLaneDao.expireBlockedLanes(blockedLaneIds, SecurityUtils.getCurrentPersonId());
        getSession().flush();
        results = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(results);
        Assert.assertEquals(23, results.size());
        hmes = false;
        upgf = false;
        exla = false;
        for (LtlPricingResult result : results) {
            if (USF_HOLLAND_SCAC.equalsIgnoreCase(result.getScac())) {
                hmes = true;
                Assert.assertEquals(result.getProfileId().longValue(), 46L);
            }
            if (UPS_FREIGHT_A_SCAC.equalsIgnoreCase(result.getScac())) {
                upgf = true;
                Assert.assertEquals(result.getProfileId().longValue(), 36L);
            }
            if (ESTES_EXPRESS_LINES_SCAC.equalsIgnoreCase(result.getScac())) {
                exla = true;
                Assert.assertEquals(result.getProfileId().longValue(), 4L);
            }
        }
        Assert.assertTrue(hmes);
        Assert.assertTrue(upgf);
        Assert.assertTrue(exla);

    }

    /**
     * Test scenario where a qualifying pallet price is defined for a blanket carrier profile with
     * exclude fuel unchecked along with Fuel data defined and undefined.
     *
     * @throws Exception
     */
    @Test
    public void testExcludeFuelUncheckedForBlanketProfile() throws Exception {

        // We have defined pallet pricing for carrier ODFL blanket profile with id 44 and cost discount of
        // 80%. The exclude fuel is unchecked and fuel data has been defined. The carrier pricing must appear
        // in results with fuel surcharge.
        GetOrderRatesCO criteria = getCriteria();
        criteria.getMaterials().get(0).setPackageType("PLT");
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = expectedResults.stream()
                .filter(result -> OLD_DOMINION_FREIGHT_LINES.equalsIgnoreCase(result.getScac()))
                .findFirst().orElse(null);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(expResToEvaluate.getProfileId().longValue(), 44L);
        Assert.assertEquals(new BigDecimal("80.00"), expResToEvaluate.getCostDiscount());
        Assert.assertNotNull(expResToEvaluate.getCarrierFSId());
        Assert.assertNotNull(expResToEvaluate.getCarrierFuelDiscount());
        Assert.assertEquals(new BigDecimal("44.00"), expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertEquals(new BigDecimal("48.89"), expResToEvaluate.getShipperFuelSurcharge());

        // We have defined pallet pricing for carrier ODFL blanket profile with id 44 and cost discount of
        // 80%. The exclude fuel is unchecked and fuel data has not been defined. The carrier pricing must not
        // appear in results.
        List<Long> fuelTriggerIds = new ArrayList<Long>();
        fuelTriggerIds.add(33L);
        ltlFuelDao.updateFuelStatus(fuelTriggerIds, Status.INACTIVE);
        getSession().flush();
        expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = null;
        expResToEvaluate = expectedResults.stream()
                .filter(result -> OLD_DOMINION_FREIGHT_LINES.equalsIgnoreCase(result.getScac()))
                .findFirst().orElse(null);
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * Test scenario where a qualifying pallet price is defined for a blanket carrier profile with
     * exclude fuel checked along with Fuel data defined and undefined.
     *
     * @throws Exception
     */
    @Test
    public void testExcludeFuelCheckedForBlanketProfile() throws Exception {

        // We have defined pallet pricing for carrier ODFL blanket profile with id 44 and cost discount of
        // 80%. The exclude fuel is checked and fuel data has been defined. The carrier pricing must appear
        // in results without fuel surcharge.
        LtlPalletPricingDetailsEntity palletPricEntity = palletPricingDetailsDao.get(23L);
        palletPricEntity.setIsExcludeFuel(true);
        palletPricingDetailsDao.saveOrUpdate(palletPricEntity);
        getSession().flush();
        GetOrderRatesCO criteria = getCriteria();
        criteria.getMaterials().get(0).setPackageType("PLT");
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = expectedResults.stream()
                .filter(result -> OLD_DOMINION_FREIGHT_LINES.equalsIgnoreCase(result.getScac()))
                .findFirst().orElse(null);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(expResToEvaluate.getProfileId().longValue(), 44L);
        Assert.assertEquals(new BigDecimal("80.00"), expResToEvaluate.getCostDiscount());
        Assert.assertNull(expResToEvaluate.getCarrierFSId());
        Assert.assertNull(expResToEvaluate.getCarrierFuelDiscount());
        Assert.assertNull(expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertNull(expResToEvaluate.getShipperFuelSurcharge());

        // We have defined pallet pricing for carrier ODFL blanket profile with id 44 and cost discount of
        // 80%. The exclude fuel is checked and fuel data has not been defined. The carrier pricing must
        // appear
        // in results without fuel surcharge.
        List<Long> fuelTriggerIds = new ArrayList<Long>();
        fuelTriggerIds.add(33L);
        ltlFuelDao.updateFuelStatus(fuelTriggerIds, Status.INACTIVE);
        getSession().flush();
        expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = null;
        expResToEvaluate = expectedResults.stream()
                .filter(result -> OLD_DOMINION_FREIGHT_LINES.equalsIgnoreCase(result.getScac()))
                .findFirst().orElse(null);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(expResToEvaluate.getProfileId().longValue(), 44L);
        Assert.assertEquals(new BigDecimal("80.00"), expResToEvaluate.getCostDiscount());
        Assert.assertNull(expResToEvaluate.getCarrierFSId());
        Assert.assertNull(expResToEvaluate.getCarrierFuelDiscount());
        Assert.assertNull(expResToEvaluate.getCarrierFuelSurcharge());
        Assert.assertNull(expResToEvaluate.getShipperFuelSurcharge());
    }

    /**
     * If pricing is configurred the way that calculated cost is less than minimum amount provided by SMC3,
     * then we should charge that minimum amount.
     *
     * @throws Exception
     */
    @Test
    public void shouldSetMinimumCostFromSMC3ForBenchmarkProfile() throws Exception {
        Long profileDetailId = 72L;
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(profileDetailId);
        String smcTariff = "MINAMOUNT_20070326_11666-RWMUC";
        pricDtlEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(pricDtlEntity);
        getSession().flush();

        LTLRateShipmentDTO rateDTO = new LTLRateShipmentDTO();
        List<LTLDetailDTO> rateDetails = new ArrayList<LTLDetailDTO>();
        LTLDetailDTO rateDetailDTO = new LTLDetailDTO();
        rateDTO.setTotalCharge("1100.00");
        rateDTO.setMinimumCharge(new BigDecimal("1500"));
        rateDTO.setDeficitCharge(new BigDecimal("200"));
        rateDTO.setDeficitNmfcClass("");
        rateDTO.setDeficitRate(BigDecimal.ZERO);
        rateDetailDTO.setCharge("900.00");
        rateDetails.add(rateDetailDTO);
        rateDTO.setDetails(rateDetails);
        rateDTO.setTotalChargeFromDetails(new BigDecimal("900"));
        setUpSmc3Mock(smcTariff, rateDTO);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        LtlPricingResult result = getFedexRate(expectedResults);
        Assert.assertEquals(new BigDecimal("450.00"), result.getBenchmarkFinalLinehaul());
    }

    /**
     * If pricing is configurred the way that calculated cost is less than minimum amount provided by SMC3,
     * then we should charge that minimum amount.
     *
     * @throws Exception
     */
    @Test
    public void shouldSetMinimumCostFromSMC3ForBuySellProfile() throws Exception {
        Long profileDetailId = 67L;
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(profileDetailId);
        String smcTariff = "MINAMOUNT_20070326_11666-RWMUC";
        pricDtlEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(pricDtlEntity);
        pricDtlEntity = ltlPricDetServ.getPricingDetailById(68L);
        pricDtlEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(pricDtlEntity);
        getSession().flush();

        LTLRateShipmentDTO rateDTO = new LTLRateShipmentDTO();
        List<LTLDetailDTO> rateDetails = new ArrayList<LTLDetailDTO>();
        LTLDetailDTO rateDetailDTO = new LTLDetailDTO();
        rateDTO.setTotalCharge("1100.00");
        rateDTO.setMinimumCharge(new BigDecimal("1500"));
        rateDTO.setDeficitCharge(new BigDecimal("200"));
        rateDTO.setDeficitNmfcClass("");
        rateDTO.setDeficitRate(BigDecimal.ZERO);
        rateDetailDTO.setCharge("900.00");
        rateDetails.add(rateDetailDTO);
        rateDTO.setDetails(rateDetails);
        rateDTO.setTotalChargeFromDetails(new BigDecimal("900"));
        setUpSmc3Mock(smcTariff, rateDTO);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        LtlPricingResult result = expectedResults.stream().filter(r -> r.getCarrierPricingDetailId() == profileDetailId).findAny().get();
        Assert.assertEquals(new BigDecimal("300.00"), result.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("450.00"), result.getShipperFinalLinehaul());
    }

    /**
     * TestCase to test if a carrier should appear in the pricing proposals based on whether the distance
     * between origin and destination lies between the minimum and maximum distance range set on its pricing
     * profile.
     *
     * @throws Exception
     */
    @Test
    public void testMinAndMaxDistOnPricingCalculation() throws Exception {
        /*
         * The minimum and max distance(in miles) for RDWY is set as 100 and 2000 respectively. For
         * distance(here 541miles) between the distance range of min and max, it should return the RDWY in the
         * proposals.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);

        // For distance(here 2384miles) greater than the max distance, RDWY must not be returned in the
        // proposals.
        GetOrderRatesCO ratesCO = getCriteria();
        ratesCO.getDestinationAddress().setPostalCode("93526");
        ratesCO.getDestinationAddress().setCity("ABERDEEN");
        ratesCO.getDestinationAddress().setStateCode("CA");
        ratesCO.getDestinationAddress().setCountryCode("USA");

        Mockito.when(mileageService.getMileage(Mockito.any(), Mockito.argThat(new ArgumentMatcher<AddressVO>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof AddressVO && "93526".equals(((AddressVO) argument).getPostalCode());
            }
        }), Mockito.eq(MileageCalculatorType.PC_MILER))).thenReturn(2384);

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);

        // For distance(here 31miles) less than the minimum distance, RDWY must not be returned in the
        // proposals.
        ratesCO = getCriteria();
        ratesCO.getDestinationAddress().setPostalCode("15112");
        ratesCO.getDestinationAddress().setCity("EAST PITTSBURGH");
        ratesCO.getDestinationAddress().setStateCode("PA");
        ratesCO.getDestinationAddress().setCountryCode("USA");

        Mockito.when(mileageService.getMileage(Mockito.any(), Mockito.argThat(new ArgumentMatcher<AddressVO>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof AddressVO && "15112".equals(((AddressVO) argument).getPostalCode());
            }
        }), Mockito.eq(MileageCalculatorType.PC_MILER))).thenReturn(31);

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * TestCase to test the scenario where tariff of carrier is set incorrectly to contain only tariffName and
     * effectiveDate instead of (tariffName + effectiveDate + productNumber). NOTE : In such cases the minimum
     * price returned from the carrier pricing details should not be applied. Expected Output : The carrier
     * should not be returned as part of the proposals. Also the quote details must be saved to
     * INCORRECT_SMC_QUOTES and INCORRECT_SMC_QUOTE_DTLS tables.
     *
     * @throws Exception
     */
    @Test
    public void testCarrierPricProfWithIncorrectTariffDetails() throws Exception {
        LtlPricingDetailsEntity ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(41L);
        // Updating the tariff to contain only tariffName and effectiveDate.
        String smcTariff = "LITECZ02_20070903";
        ltlPricDetailsEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        getSession().flush();
        getSession().clear();

        setUpSmc3Mock(smcTariff, new LTLRateShipmentDTO());

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());

        Assert.assertNotNull(expectedResults);

        LtlPricingResult expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("UPGF".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * TestCase to test the scenario where SMC3 returns basePrice of 0$ due to incorrect quoting for a
     * carrier. NOTE : In such cases the minimum price returned from the SMC3 should not be applied. Expected
     * Output : The carrier should not be returned as part of the proposals. Also the quote details must be
     * saved to INCORRECT_SMC_QUOTES and INCORRECT_SMC_QUOTE_DTLS tables.
     *
     * @throws Exception
     */
    @Test
    public void testIncorrectSmc3BasepriceForCarrier() throws Exception {
        // For FXNL we have 2 valid pricing's, hence will be setting the tariff for both.
        LtlPricingDetailsEntity ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(33L);
        // For this particular tariff the SMC3 will be returning $0 basePrice.
        String smcTariff = "LITECZ02_20100201_20696-RWM";
        ltlPricDetailsEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        ltlPricDetailsEntity = ltlPricDetServ.getPricingDetailById(74L);
        ltlPricDetailsEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(ltlPricDetailsEntity);
        getSession().flush();
        getSession().clear();

        setUpSmc3Mock(smcTariff, new LTLRateShipmentDTO());

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getFedexRate(expectedResults);
        Assert.assertNull(expResToEvaluate);
    }

    /**
     * TestCase to test whether the minimum cost on carrier pricing profile would be picked or not by pricing
     * logic when the carrier cost is less than it.
     *
     * @throws Exception
     */
    @Test
    public void testMinCostInPricingCalculation() throws Exception {
        /*
         * The minimum cost for RDWY is set as $140. For the origin(16066) and destination(61760), the base
         * rate from SMC3 is $1000. Applying the discount of 72% according to the pricing setup the cost would
         * be $280, which is greater than $140. Hence the minimum cost would not be applied.
         */
        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("720.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("280.00"), expResToEvaluate.getCarrierFinalLinehaul());

        /*
         * The minimum cost for RDWY is set as $140. For the origin(16066) and destination(15112), the base
         * rate from SMC3 is $100. Applying the discount of 80% according to the pricing setup the cost would
         * be $20, which is less than $140. Hence the minimum cost would be applied.
         */
        GetOrderRatesCO ratesCO = getCriteria();
        String postalCode = "45810";
        ratesCO.getDestinationAddress().setPostalCode(postalCode);
        ratesCO.getDestinationAddress().setCity("ADA");
        ratesCO.getDestinationAddress().setStateCode("OH");
        ratesCO.getDestinationAddress().setCountryCode("USA");

        LTLRateShipmentDTO rateDTO = new LTLRateShipmentDTO();
        List<LTLDetailDTO> rateDetails = new ArrayList<LTLDetailDTO>();
        LTLDetailDTO rateDetailDTO = new LTLDetailDTO();
        rateDTO.setTotalCharge("100.00");
        rateDTO.setMinimumCharge(new BigDecimal("100"));
        rateDTO.setDeficitCharge(BigDecimal.ZERO);
        rateDTO.setDeficitNmfcClass("");
        rateDTO.setDeficitRate(BigDecimal.ZERO);
        rateDetailDTO.setCharge("100.00");
        rateDetails.add(rateDetailDTO);
        rateDTO.setDetails(rateDetails);
        rateDTO.setTotalChargeFromDetails(new BigDecimal("100"));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Stream.of((LtlRatingVO) invocation.getArguments()[1], (LtlRatingVO) invocation.getArguments()[2])
                        .filter(Objects::nonNull)
                        .flatMap(p -> p.getCarrierPricingDetails().values().stream()).flatMap(List::stream)
                        .flatMap(r -> Stream.of(r.getRate().getPricingDetails(),
                                r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails()))
                        .filter(Objects::nonNull).forEach(profile -> {
                            profile.setSmc3Response(rateDTO);
                        });
                return null;
            }
        }).when(smc3Service).populateSMC3Rates(Mockito.any(), Mockito.any(), Mockito.any());

        expectedResults = pricingEng.getRates(ratesCO);
        Assert.assertNotNull(expectedResults);
        expResToEvaluate = getYrcRate(expectedResults);
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("100.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("-40.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("140.00"), expResToEvaluate.getCarrierFinalLinehaul());

    }

    /**
     * TestCase to test Overridden Tariff type in pricing logic. If the SMC3 Tariff defined in pricing profile
     * of carrier is overridden in the pricing details, then the tariff defined in the pricing details must be
     * considered by the pricing logic.
     *
     * @throws Exception
     */
    @Test
    public void testOverriddenTariffInPricingCalculation() throws Exception {
        // We have set the SMC3 Tariff as "LITECZ02_20070903_20718-RWM" in pricing profile of carrier UPS
        // Freight A.
        // While the SMC3 tariff is overridden as "UPF50202_20070326_11666-RWMUC" in its pricing detail. For
        // tariff
        // "LITECZ02_20070903_20718-RWM" the rate from SMC3 is 1000$ and for tariff
        // "UPF50202_20070326_11666-RWMUC"
        // the rate returned from SMC3 is 900$. Hence our carrierInitialLinehaul must be 900$.
        LtlPricingDetailsEntity pricDtlEntity = ltlPricDetServ.getPricingDetailById(41L);
        String smcTariff = "UPF50202_20070326_11666-RWMUC";
        pricDtlEntity.setSmcTariff(smcTariff);
        ltlPricDetServ.savePricingDetail(pricDtlEntity);
        getSession().flush();

        LTLRateShipmentDTO rateDTO = new LTLRateShipmentDTO();
        List<LTLDetailDTO> rateDetails = new ArrayList<LTLDetailDTO>();
        LTLDetailDTO rateDetailDTO = new LTLDetailDTO();
        rateDTO.setTotalCharge("900.00");
        rateDTO.setMinimumCharge(new BigDecimal("900"));
        rateDTO.setDeficitCharge(BigDecimal.ZERO);
        rateDTO.setDeficitNmfcClass("");
        rateDTO.setDeficitRate(BigDecimal.ZERO);
        rateDetailDTO.setCharge("900.00");
        rateDetails.add(rateDetailDTO);
        rateDTO.setDetails(rateDetails);
        rateDTO.setTotalChargeFromDetails(new BigDecimal("900"));
        setUpSmc3Mock(smcTariff, rateDTO);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(getCriteria());
        Assert.assertNotNull(expectedResults);
        LtlPricingResult expResToEvaluate = null;
        for (LtlPricingResult result : expectedResults) {
            if ("UPGF".equalsIgnoreCase(result.getScac())) {
                expResToEvaluate = result;
            }
        }
        Assert.assertNotNull(expResToEvaluate);
        Assert.assertEquals(new BigDecimal("900.00"), expResToEvaluate.getCarrierInitialLinehaul());
        Assert.assertEquals(new BigDecimal("1000.00"), expResToEvaluate.getShipperInitialLinehaul());
        Assert.assertEquals(new BigDecimal("639.00"), expResToEvaluate.getCarrierLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("710.00"), expResToEvaluate.getShipperLinehaulDiscount());
        Assert.assertEquals(new BigDecimal("261.00"), expResToEvaluate.getCarrierFinalLinehaul());
        Assert.assertEquals(new BigDecimal("290.00"), expResToEvaluate.getShipperFinalLinehaul());
        Assert.assertEquals(new BigDecimal("318.42"), expResToEvaluate.getTotalCarrierCost());
        Assert.assertEquals(new BigDecimal("353.80"), expResToEvaluate.getTotalShipperCost());
    }

    @Test
    public void shouldFilterCarriersByServiceType() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        criteria.setServiceType(LtlServiceType.DIRECT);

        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertNotNull(expectedResults);
        assertFalse(expectedResults.isEmpty());
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            Assert.assertEquals(LtlServiceType.DIRECT, ltlPricingResult.getServiceType());
        }
    }

    private GetOrderRatesCO getCriteria() {
        GetOrderRatesCO ratesCO = new GetOrderRatesCO();

        ratesCO.setShipperOrgId(1L);
        ratesCO.setShipDate(new Date());

        AddressVO originAddress = new AddressVO();
        originAddress.setCity("CRANBERRY TWP");
        originAddress.setPostalCode("16066");
        originAddress.setStateCode("PA");
        originAddress.setCountryCode("USA");

        AddressVO destinationAddress = new AddressVO();
        destinationAddress.setCity("MINONK");
        destinationAddress.setPostalCode("61760");
        destinationAddress.setStateCode("IL");
        destinationAddress.setCountryCode("USA");

        List<RateMaterialCO> materials = new ArrayList<RateMaterialCO>();
        RateMaterialCO material = new RateMaterialCO();
        material.setCommodityClassEnum(CommodityClass.CLASS_50);
        material.setWeight(new BigDecimal(180));
        material.setQuantity(1);
        materials.add(material);

        ratesCO.setOriginAddress(originAddress);
        ratesCO.setDestinationAddress(destinationAddress);
        ratesCO.setMaterials(materials);
        ratesCO.setTotalWeight(new BigDecimal("180.0"));

        return ratesCO;
    }

    private GetOrderRatesCO getCriteriaWithCanadianAddr() {
        GetOrderRatesCO ratesCO = new GetOrderRatesCO();

        ratesCO.setShipperOrgId(1L);
        ratesCO.setShipDate(new Date());

        AddressVO originAddress = new AddressVO();
        originAddress.setCity("CRANBERRY TWP");
        originAddress.setPostalCode("16066");
        originAddress.setStateCode("PA");
        originAddress.setCountryCode("USA");

        AddressVO destinationAddress = new AddressVO();
        destinationAddress.setCity("ABBOTSFORD");
        destinationAddress.setPostalCode("V2S 0A1");
        destinationAddress.setStateCode("BC");
        destinationAddress.setCountryCode("CAN");

        List<RateMaterialCO> materials = new ArrayList<RateMaterialCO>();
        RateMaterialCO material = new RateMaterialCO();
        material.setCommodityClassEnum(CommodityClass.CLASS_50);
        material.setWeight(new BigDecimal(180));
        material.setQuantity(1);
        materials.add(material);

        ratesCO.setOriginAddress(originAddress);
        ratesCO.setDestinationAddress(destinationAddress);
        ratesCO.setMaterials(materials);
        ratesCO.setTotalWeight(new BigDecimal("180.0"));

        return ratesCO;
    }
}
