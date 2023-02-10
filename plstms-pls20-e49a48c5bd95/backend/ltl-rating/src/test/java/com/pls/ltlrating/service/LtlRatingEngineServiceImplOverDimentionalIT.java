package com.pls.ltlrating.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.shared.GetOrderRatesCO;
import com.pls.ltlrating.shared.LtlPricingAccessorialResult;
import com.pls.ltlrating.shared.LtlPricingResult;
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
 * @author Aleksandr Leshchenko
 */
@ActiveProfiles("PricingTest")
public class LtlRatingEngineServiceImplOverDimentionalIT extends BaseServiceITClass {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private LtlSMC3Service smc3Service;
    @Autowired
    private CarrierTransitClient transitClient;
    @Autowired
    private MileageService mileageService;

    @Autowired
    private LtlRatingEngineService pricingEng;

    private static final int ODM_UNIT_COST = 10;

    @Before
    public void setUp() throws ApplicationException {
        saveLtlAccessorials();

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
     * TestCase to test scenario when user does 'get quotes' with single material, overdimension
     * accessorial selected and length value less than the minimum length specified for particular
     * carrier's ODM accessorial.
     * Expected Output : Display the carrier in the pricing results with overdimension price as $0.0
     *
     * @throws Exception
     */
    @Test
    public void shouldDisplayZeroODMIfLengthLessThanMinForSingleMaterial() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(500));
        criteria.getMaterials().set(0, material);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("0")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("0")) == 0) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with single material, overdimension
     * accessorial selected and length value more than the maximum length specified for particular
     * carrier's ODM accessorial.
     * Expected Output : The carrier should not be displayed in the pricing results.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFindCarrierIfODMLengthMoreThanMaxForSingleMaterial() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(2500));
        criteria.getMaterials().set(0, material);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertTrue(expectedResults.stream().noneMatch(result -> new Long(46).equals(result.getProfileId())));
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with single material, overdimension
     * accessorial selected and length value between the minimum and maximum length specified for particular
     * carrier's ODM accessorial.
     * Expected Output : Display the carrier in the pricing results with appropriate overdimension price.
     *
     * @throws Exception
     */
    @Test
    public void shouldDisplayODMInNormalScenarioForSingleMaterial() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        criteria.getMaterials().set(0, material);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("10")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("10")) == 1) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }

        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with single material, overdimension and
     * residential accessorials selected.
     * Expected Output : Display the carrier in the pricing results with appropriate price
     * 
     * @throws Exception
     */
    @Test
    public void shouldDisplayODMForSingleMaterialAndTwoAccessorials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        accList.add(LtlAccessorialType.RESIDENTIAL_DELIVERY.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        material.setPieces(5);
        material.setQuantity(10);
        criteria.getMaterials().set(0, material);
        int carrierCountAccResult = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getProfileId() == 28) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())) {
                        Assert.assertEquals(new BigDecimal(material.getQuantity() * ODM_UNIT_COST).setScale(2, RoundingMode.HALF_UP),
                                ltlPricingAccResult.getCarrierAccessorialCost());
                        Assert.assertEquals(new BigDecimal("111.11"), ltlPricingAccResult.getBenchmarkAccessorialCost());
                        Assert.assertEquals(new BigDecimal("111.11"), ltlPricingAccResult.getShipperAccessorialCost());
                        carrierCountAccResult++;
                    }
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.RESIDENTIAL_DELIVERY.getCode())) {
                        Assert.assertEquals(new BigDecimal(material.getPieces() * ODM_UNIT_COST).setScale(2, RoundingMode.HALF_UP),
                                ltlPricingAccResult.getCarrierAccessorialCost());
                        Assert.assertEquals(new BigDecimal("55.56"), ltlPricingAccResult.getBenchmarkAccessorialCost());
                        Assert.assertEquals(new BigDecimal("55.56"), ltlPricingAccResult.getShipperAccessorialCost());
                        carrierCountAccResult++;
                    }
                }
            }
        }

        Assert.assertTrue(carrierCountAccResult == 2);
    }

    @Test
    public void shouldNotDisplayODMIfLengthLessThanMinWhenNoAccessorialSelected() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(500));
        criteria.getMaterials().set(0, material);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertFalse(expectedResults.isEmpty());
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount == 0);
    }

    @Test
    public void shouldNotFindCarrierIfODMLengthMoreThanMaxWhenNoAccessorialSelected() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(2500));
        criteria.getMaterials().set(0, material);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertTrue(expectedResults.stream().noneMatch(result -> new Long(46).equals(result.getProfileId())));
    }

    @Test
    public void shouldDisplayODMCostInNormalScenarioWhenNoAccessorialSelected() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        criteria.getMaterials().set(0, material);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("10")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("10")) == 1) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with multiple materials and overdimension
     * accessorial selected. The materials list contains a material with length value more than minimum
     * length specified for particular carrier's ODM accessorial and another material with length value
     * less than minimum length specified for the same carrier's ODM accessorial.
     * Expected Output : Display the carrier in the pricing results with appropriate overdimension price.
     *
     * @throws Exception
     */
    @Test
    public void shouldDisplayODMIfLengthLessAndMoreThanMinForMultipleMaterials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        criteria.getMaterials().set(0, material);
        RateMaterialCO material2 = new RateMaterialCO();
        material2.setCommodityClassEnum(CommodityClass.CLASS_60);
        material2.setWeight(new BigDecimal(250));
        material2.setQuantity(1);
        material2.setLength(new BigDecimal(500));
        List<RateMaterialCO> materials = criteria.getMaterials();
        materials.add(material2);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("10")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("10")) == 1) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with multiple materials and overdimension
     * accessorial selected. The materials list contains a material with length value more than maximum
     * length specified for particular carrier's ODM accessorial and another material with length value
     * less than maximum length specified for the same carrier's ODM accessorial.
     * Expected Output : The carrier should not be displayed in the pricing results.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFindCarrierIfODMLengthLessAndMoreThanMaxForMultipleMaterials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        criteria.getMaterials().set(0, material);
        RateMaterialCO material2 = new RateMaterialCO();
        material2.setCommodityClassEnum(CommodityClass.CLASS_60);
        material2.setWeight(new BigDecimal(250));
        material2.setQuantity(1);
        material2.setLength(new BigDecimal(2500));
        List<RateMaterialCO> materials = criteria.getMaterials();
        materials.add(material2);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertTrue(expectedResults.stream().noneMatch(result -> new Long(46).equals(result.getProfileId())));
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with multiple materials and overdimension
     * accessorial selected. The materials list contains materials with length values between maximum
     * and minimum length specified for particular carrier's ODM accessorial.
     * Expected Output : Display the carrier in the pricing results with appropriate overdimension price.
     *
     * @throws Exception
     */
    @Test
    public void shouldDisplayODMInNormalScenarioForMultipleMaterials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(1500));
        criteria.getMaterials().set(0, material);
        RateMaterialCO material2 = new RateMaterialCO();
        material2.setCommodityClassEnum(CommodityClass.CLASS_60);
        material2.setWeight(new BigDecimal(250));
        material2.setQuantity(1);
        material2.setLength(new BigDecimal(1300));
        List<RateMaterialCO> materials = criteria.getMaterials();
        materials.add(material2);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("10")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("10")) == 1) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with multiple materials and overdimension
     * accessorial selected. The materials list contains materials with length values less than
     * minimum length specified for particular carrier's ODM accessorial.
     * Expected Output : Display the carrier in the pricing results with overdimension price as $0.0.
     *
     * @throws Exception
     */
    @Test
    public void shouldDisplayZeroODMIfLengthLessThanMinForAllMaterials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(500));
        criteria.getMaterials().set(0, material);
        RateMaterialCO material2 = new RateMaterialCO();
        material2.setCommodityClassEnum(CommodityClass.CLASS_60);
        material2.setWeight(new BigDecimal(250));
        material2.setQuantity(1);
        material2.setLength(new BigDecimal(300));
        List<RateMaterialCO> materials = criteria.getMaterials();
        materials.add(material2);
        int carrResWithOdmAccCount = 0;
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        for (LtlPricingResult ltlPricingResult : expectedResults) {
            if (ltlPricingResult.getCarrierOrgId() == 66) {
                for (LtlPricingAccessorialResult ltlPricingAccResult : ltlPricingResult.getAccessorials()) {
                    if (ltlPricingAccResult.getAccessorialType().equalsIgnoreCase(LtlAccessorialType.OVER_DIMENSION.getCode())
                            && ltlPricingAccResult.getCarrierAccessorialCost().compareTo(new BigDecimal("0")) == 0
                            && ltlPricingAccResult.getShipperAccessorialCost().compareTo(new BigDecimal("0")) == 0) {
                        carrResWithOdmAccCount++;
                    }
                }
            }
        }
        Assert.assertTrue(carrResWithOdmAccCount > 0);
    }

    /**
     * TestCase to test scenario when user does 'get quotes' with multiple materials and overdimension
     * accessorial selected. The materials list contains materials with length values more than
     * maximum length specified for particular carrier's ODM accessorial.
     * Expected Output : The carrier should not be displayed in the pricing results.
     *
     * @throws Exception
     */
    @Test
    public void shouldNotFindCarrierIfODMLengthMoreThanMaxForAllMaterials() throws Exception {
        GetOrderRatesCO criteria = getCriteria();
        List<String> accList = new ArrayList<String>();
        accList.add(LtlAccessorialType.OVER_DIMENSION.getCode());
        criteria.setAccessorialTypes(accList);
        RateMaterialCO material = criteria.getMaterials().get(0);
        material.setLength(new BigDecimal(2500));
        criteria.getMaterials().set(0, material);
        RateMaterialCO material2 = new RateMaterialCO();
        material2.setCommodityClassEnum(CommodityClass.CLASS_60);
        material2.setWeight(new BigDecimal(250));
        material2.setQuantity(1);
        material2.setLength(new BigDecimal(3000));
        List<RateMaterialCO> materials = criteria.getMaterials();
        materials.add(material2);
        List<LtlPricingResult> expectedResults = pricingEng.getRates(criteria);
        Assert.assertTrue(expectedResults.stream().noneMatch(result -> new Long(46).equals(result.getProfileId())));
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

    /**
    * Save the Over Dimension Accessory pricing detail for a specific carrier in the
    * LTL_ACCESSORIALS table for specific lanes.
    */
    public void saveLtlAccessorials() {
        Session session = sessionFactory.openSession();
        LtlAccessorialsEntity odmAcc = session.get(LtlAccessorialsEntity.class, 232L);
        odmAcc.setLtlPricProfDetailId(47L);
        odmAcc.setAccessorialType(LtlAccessorialType.OVER_DIMENSION.getCode());
        odmAcc.setBlocked("N");
        odmAcc.setCostType(LtlCostType.FL);
        odmAcc.setUnitCost(new BigDecimal("10"));
        odmAcc.setStatus(Status.ACTIVE);
        odmAcc.setCostApplMinLength(new BigDecimal("1001.00"));
        odmAcc.setCostApplMaxLength(new BigDecimal("2000.00"));
        odmAcc.setServiceType(LtlServiceType.DIRECT);

        session.saveOrUpdate(odmAcc);
        session.saveOrUpdate(getODMAccessorialPricing1(session));
        session.saveOrUpdate(getODMAccessorialPricing2(session));
        session.flush();
        session.close();
    }

    private LtlAccessorialsEntity getODMAccessorialPricing1(Session session) {
        LtlAccessorialsEntity odmAccessorial = session.get(LtlAccessorialsEntity.class, 221L);
        odmAccessorial.setLtlPricProfDetailId(30L);
        odmAccessorial.setAccessorialType(LtlAccessorialType.OVER_DIMENSION.getCode());
        odmAccessorial.setBlocked("N");
        odmAccessorial.setCostType(LtlCostType.PE);
        odmAccessorial.setUnitCost(new BigDecimal(ODM_UNIT_COST));
        odmAccessorial.setStatus(Status.ACTIVE);
        odmAccessorial.setCostApplMinLength(new BigDecimal("1000.00"));
        odmAccessorial.setCostApplMaxLength(new BigDecimal("2000.00"));
        odmAccessorial.setServiceType(LtlServiceType.DIRECT);
        return odmAccessorial;
    }

    private LtlAccessorialsEntity getODMAccessorialPricing2(Session session) {
        LtlAccessorialsEntity odmAccessorial = session.get(LtlAccessorialsEntity.class, 33L);
        odmAccessorial.setLtlPricProfDetailId(30L);
        odmAccessorial.setAccessorialType(LtlAccessorialType.RESIDENTIAL_DELIVERY.getCode());
        odmAccessorial.setBlocked("N");
        odmAccessorial.setCostType(LtlCostType.PE);
        odmAccessorial.setUnitCost(new BigDecimal(ODM_UNIT_COST));
        odmAccessorial.setStatus(Status.ACTIVE);
        odmAccessorial.setCostApplMinLength(new BigDecimal("1000.00"));
        odmAccessorial.setCostApplMaxLength(new BigDecimal("2000.00"));
        odmAccessorial.setServiceType(LtlServiceType.DIRECT);
        return odmAccessorial;
    }
}
