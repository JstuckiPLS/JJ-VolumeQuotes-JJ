package com.pls.ltlrating.dao.profile;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.utils.DateUtility;
import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.enums.GetRatesDateType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.shared.GetRatesCO;
import com.pls.ltlrating.shared.LtlCustomerPricingProfileVO;
import com.pls.ltlrating.shared.LtlPricingProfileVO;

/**
 * Test for {@link com.pls.ltlrating.dao.impl.LtlPricingProfileDaoImpl} class.
 *
 * @author Hima Bindu Challa
 */
public class LtlPricingProfileDaoImplIT extends AbstractDaoTest {

    private static final Long ACTIVE_PROFILE_ID = 1L;

    private static final Long EXPECTED_APPLICABLE_CUSTOMER_ID = 1L;

    private static final Long EXPECTED_CSP_ID = 8L;

    private static final BigInteger EXPECTED_CSP_DETAIL_ID = BigInteger.valueOf(9L);

    private static final List<PricingType> ALL_TYPES = Arrays.asList(PricingType.values());

    @Autowired
    private LtlPricingProfileDao sut;

    @Test
    public void testGetProfilesByCriteriaWithApplicableCustomers() throws Exception {
        GetRatesCO criteria = new GetRatesCO();
        criteria.setCustomer(EXPECTED_APPLICABLE_CUSTOMER_ID);
        criteria.setPricingTypes(ALL_TYPES);
        criteria.setStatus(Status.ACTIVE);

        List<LtlPricingProfileVO> actualResults = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(actualResults);
        Assert.assertFalse(actualResults.isEmpty());
    }

    @Test
    public void testGetProfilesByCriteriaWithEffectiveDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, Calendar.NOVEMBER, 20, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date expectedFromDate = calendar.getTime();

        GetRatesCO criteria = new GetRatesCO();
        criteria.setDateType(GetRatesDateType.EFFECTIVE);
        criteria.setFromDate(expectedFromDate);
        criteria.setStatus(Status.ACTIVE);
        criteria.setPricingTypes(ALL_TYPES);

        List<LtlPricingProfileVO> actualList = sut.getTariffsBySelectedCriteria(criteria);

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlPricingProfileVO item : actualList) {
            Assert.assertTrue(item.getEffDate().compareTo(expectedFromDate) >= 0);
        }
    }

    @Test
    public void testGetProfilesByCriteriaWithActiveStatus() throws Exception {
        GetRatesCO criteria = new GetRatesCO();
        criteria.setStatus(Status.ACTIVE);
        criteria.setPricingTypes(ALL_TYPES);

        List<LtlPricingProfileVO> actualList = sut.getTariffsBySelectedCriteria(criteria);

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testGetProfilesByCriteriaWithInactiveStatus() throws Exception {
        GetRatesCO criteria = new GetRatesCO();
        criteria.setStatus(Status.INACTIVE);
        criteria.setPricingTypes(ALL_TYPES);

        List<LtlPricingProfileVO> actualList = sut.getTariffsBySelectedCriteria(criteria);

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
    }

    @Test
    public void testGetProfilesByCriteriaWithBlanketType() throws Exception {
        PricingType expectedType = PricingType.BLANKET;
        GetRatesCO criteria = new GetRatesCO();
        List<PricingType> expectedTypes = Arrays.asList(expectedType);
        criteria.setPricingTypes(expectedTypes);
        criteria.setStatus(Status.ACTIVE);

        List<LtlPricingProfileVO> actualList = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlPricingProfileVO item : actualList) {
            Assert.assertEquals("Blanket", item.getPricingType());
        }
    }

    @Test
    public void testGetProfilesBySearchCriteria() throws EntityNotFoundException, ParseException {
        //For Pallet Type
        GetRatesCO criteria = new GetRatesCO();
        criteria.setDateType(GetRatesDateType.EFFECTIVE);
        criteria.setFromDate(DateUtility.stringToDate("11/20/2012", DateUtility.SLASHED_DATE));
        criteria.setPricingTypes(ALL_TYPES);

        //Active records with effective date >= 11/20/2012.
        criteria.setStatus(Status.ACTIVE);
        List<LtlPricingProfileVO> profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(36, profiles.size());

        //InActive records with effective date >= 11/20/2012.
        criteria.setStatus(Status.INACTIVE);
        profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(4, profiles.size());

        criteria.setFromDate(DateUtility.stringToDate("11/01/2012", DateUtility.SLASHED_DATE));
        criteria.setToDate(DateUtility.stringToDate("11/19/2012", DateUtility.SLASHED_DATE));

        //Active records with effective date between 11/01/2012 and 11/20/2012
        criteria.setStatus(Status.ACTIVE);
        profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(10, profiles.size());

        //InActive records with effective date between 11/01/2012 and 11/20/2012
        criteria.setStatus(Status.INACTIVE);
        profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(0, profiles.size());

        criteria.setFromDate(DateUtility.stringToDate("11/01/2012", DateUtility.SLASHED_DATE));
        criteria.setToDate(DateUtility.stringToDate("12/31/2016", DateUtility.SLASHED_DATE));

        List<PricingType> pricingTypes = new ArrayList<PricingType>();
        pricingTypes.add(PricingType.BLANKET);
        pricingTypes.add(PricingType.BLANKET_CSP);
        criteria.setPricingTypes(pricingTypes);

        //Active records with effective date between 11/01/2012 and 01/01/2016
        //and Tariffs - Blanket and Gainshare
        criteria.setStatus(Status.ACTIVE);
        profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(36, profiles.size());

        //InActive records with effective date between 11/01/2012 and 01/01/2016
        //and Tariffs - Blanket and Gainshare
        criteria.setStatus(Status.INACTIVE);
        profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(3, profiles.size());
    }

    @Test
    public void testGetTariffsByBlanketType() throws EntityNotFoundException, ParseException {
        //For Pallet Type
        GetRatesCO criteria = new GetRatesCO();
        criteria.setPricingGroup("CARRIER");
        criteria.setStatus(Status.ACTIVE);
        criteria.setDateType(GetRatesDateType.EFFECTIVE);
        List<PricingType> pricingTypes = new ArrayList<PricingType>();
        pricingTypes.add(PricingType.BLANKET);
        criteria.setPricingTypes(pricingTypes);
        criteria.setFromDate(DateUtility.stringToDate("11/20/2012", DateUtility.SLASHED_DATE));

        //Active records with effective date >= 11/20/2012.
        List<LtlPricingProfileVO> profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(32, profiles.size());
    }

    @Test
    public void testGetTariffsByNullType() throws EntityNotFoundException, ParseException {
        //Active records with null type.
        GetRatesCO criteria = new GetRatesCO();
        criteria.setPricingGroup("CARRIER");
        criteria.setStatus(Status.ACTIVE);
        criteria.setDateType(GetRatesDateType.EFFECTIVE);
        criteria.setPricingTypes(null);
        List<LtlPricingProfileVO> profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(0, profiles.size());
    }

    @Test
    public void testGetTariffsByTimeRange() throws EntityNotFoundException, ParseException {
        //Active records with effective date between 11/01/2012 and 01/01/2016
        //and Tariffs - Blanket and Gainshare
        GetRatesCO criteria = new GetRatesCO();
        criteria.setPricingGroup("CARRIER");
        criteria.setStatus(Status.ACTIVE);
        criteria.setDateType(GetRatesDateType.EFFECTIVE);
        criteria.setFromDate(DateUtility.stringToDate("11/01/2012", DateUtility.SLASHED_DATE));
        criteria.setToDate(DateUtility.stringToDate("12/31/2016", DateUtility.SLASHED_DATE));
        List<PricingType> pricingTypes = new ArrayList<PricingType>();
        pricingTypes.add(PricingType.BLANKET);
        pricingTypes.add(PricingType.BLANKET_CSP);
        criteria.setPricingTypes(pricingTypes);
        List<LtlPricingProfileVO> profiles = sut.getTariffsBySelectedCriteria(criteria);
        Assert.assertNotNull(profiles);
        Assert.assertEquals(36, profiles.size());
    }

    @Test
    public void testUpdateStatus() throws EntityNotFoundException {
        LtlPricingProfileEntity activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(activeProfile);
        Assert.assertEquals(Status.ACTIVE, activeProfile.getStatus());

        activeProfile.setStatus(Status.INACTIVE);
        sut.saveOrUpdate(activeProfile);
        flushAndClearSession();

        activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertEquals(Status.INACTIVE, activeProfile.getStatus());
        activeProfile.setStatus(Status.ACTIVE);
        sut.saveOrUpdate(activeProfile);
        flushAndClearSession();

        activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertEquals(Status.ACTIVE, activeProfile.getStatus());
    }

    @Test
    public void testExpireProfile() throws EntityNotFoundException, ParseException {
        LtlPricingProfileEntity activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(activeProfile);
        Assert.assertEquals(Status.ACTIVE, activeProfile.getStatus());

        activeProfile.setExpDate(DateUtility.stringToDate("11/01/2000", DateUtility.SLASHED_DATE));

        LtlPricingProfileEntity updatedProfile = sut.update(activeProfile);
        flushAndClearSession();

        Assert.assertEquals(DateUtility.stringToDate("11/01/2000", DateUtility.SLASHED_DATE), updatedProfile.getExpDate());

        LtlPricingProfileEntity profile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(profile);
        Assert.assertEquals(DateUtility.stringToDate("11/01/2000", DateUtility.SLASHED_DATE), profile.getExpDate());
    }

    @Test
    public void testFutureEffDateProfile() throws EntityNotFoundException, ParseException {
        LtlPricingProfileEntity activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(activeProfile);
        Assert.assertEquals(Status.ACTIVE, activeProfile.getStatus());

        activeProfile.setEffDate(DateUtility.stringToDate("11/01/2500", DateUtility.SLASHED_DATE));

        LtlPricingProfileEntity updatedProfile = sut.update(activeProfile);
        flushAndClearSession();

        Assert.assertEquals(DateUtility.stringToDate("11/01/2500", DateUtility.SLASHED_DATE), updatedProfile.getEffDate());

        LtlPricingProfileEntity profile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(profile);
        Assert.assertEquals(DateUtility.stringToDate("11/01/2500", DateUtility.SLASHED_DATE), profile.getEffDate());
    }

    @Test
    public void testBlockUnBlockProfile() throws EntityNotFoundException {
        LtlPricingProfileEntity activeProfile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(activeProfile);
        Assert.assertEquals(Status.ACTIVE, activeProfile.getStatus());

        activeProfile.setBlocked("Y");
        LtlPricingProfileEntity updatedProfile = sut.update(activeProfile);
        flushAndClearSession();

        Assert.assertEquals("Y", updatedProfile.getBlocked());

        LtlPricingProfileEntity profile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(profile);
        Assert.assertEquals("Y", profile.getBlocked());

        activeProfile.setBlocked("N");
        updatedProfile = sut.update(activeProfile);
        flushAndClearSession();

        Assert.assertEquals("N", updatedProfile.getBlocked());

        profile = sut.get(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(profile);
        Assert.assertEquals("N", profile.getBlocked());
    }

    @Test
    public void testFindPricingTypeByProfileDetailId() {
        String expectedPricingType = "BLANKET";

        String actualPricingType = sut.findPricingTypeByProfileDetailId(1L);
        Assert.assertEquals(expectedPricingType, actualPricingType);
    }

    @Test
    public void testFindPricingTypeByProfileId() {
        String expectedPricingType = "BLANKET";

        String actualPricingType = sut.findPricingTypeByProfileId(1L);
        Assert.assertEquals(expectedPricingType, actualPricingType);
    }

    @Test
    public void testFindChildCuctomerSpecificProfiles() {
        List<LtlPricingProfileEntity> actualChildsList = sut.findChildCSPByProfileDetailId(1L);
        Assert.assertNotNull(actualChildsList);
        Assert.assertFalse(actualChildsList.isEmpty());
        Assert.assertEquals(1, actualChildsList.size());
    }

    @Test
    public void testFindActivePricingApplicableCustomer() {
        LtlPricingApplicableCustomersEntity actualEntity = sut.findActivePricingApplicableCustomer(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(actualEntity);
        Assert.assertEquals(ACTIVE_PROFILE_ID, actualEntity.getLtlPricingProfileId());
        Assert.assertEquals(Status.ACTIVE, actualEntity.getStatus());
    }

    @Test
    public void testFindChildCSPByProfileId() {
        List<Long> actualChildsList = sut.findChildCSPByProfileId(ACTIVE_PROFILE_ID);
        Assert.assertNotNull(actualChildsList);
        Assert.assertFalse(actualChildsList.isEmpty());
        Assert.assertEquals(1, actualChildsList.size());
        Long actualCSPId = actualChildsList.get(0);
        Assert.assertEquals(EXPECTED_CSP_ID, actualCSPId);
    }

    @Test
    public void testGetPricingProfilesForCustomer() throws Exception {
        Long shipperOrgId = 1L;

        List<LtlCustomerPricingProfileVO> actualResult = sut.getPricingProfilesForCustomer(shipperOrgId);
        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
        for (LtlCustomerPricingProfileVO item : actualResult) {
            Assert.assertEquals(shipperOrgId, item.getShipperOrgId());
        }
    }

    @Test
    public void testGetProfilesToCopy() throws Exception {
        GetRatesCO criteria = new GetRatesCO();
        criteria.setProfileId(ACTIVE_PROFILE_ID);
        criteria.setProhibitedNLiabilities(true);

        List<LtlPricingProfileEntity> actualResult = sut.getProfilesToCopy(criteria);
        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
        for (LtlPricingProfileEntity item : actualResult) {
            Assert.assertNotEquals(ACTIVE_PROFILE_ID, item.getId());
            Assert.assertEquals(Status.ACTIVE, item.getStatus());
        }
    }

    @Test
    public void testSaveProfilesForCustomer() throws Exception {
        List<LtlCustomerPricingProfileVO> existedProfiles = getListOfRandomCustomerPricingProfileVO();
        Assert.assertNotNull(existedProfiles);
        Assert.assertFalse(existedProfiles.isEmpty());

        SecurityTestUtils.logout();
        SecurityTestUtils.login("sysadmin");
        sut.saveProfilesForCustomer(existedProfiles);
        flushAndClearSession();
    }

    @Test
    public void testFindChildCSPByParentProfile() {
        LtlPricingProfileEntity parentEntity = sut.find(ACTIVE_PROFILE_ID);

        List<LtlPricingProfileEntity> actualList = sut.findChildCSPByParentProfile(parentEntity);
        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        for (LtlPricingProfileEntity child : actualList) {
            Assert.assertEquals(parentEntity.getId(), child.getCopiedFrom());
        }
    }

    @Test
    public void testFindChildCSPDetailByParentDetailId() {
        List<BigInteger> actualList = sut.findChildCSPDetailByParentDetailId(ACTIVE_PROFILE_ID);

        Assert.assertNotNull(actualList);
        Assert.assertFalse(actualList.isEmpty());
        Assert.assertEquals(EXPECTED_CSP_DETAIL_ID, actualList.get(0));
    }

    private List<LtlCustomerPricingProfileVO> getListOfRandomCustomerPricingProfileVO() {
        return Arrays.asList(getRandomCustomerPricingProfileVO());
    }

    private LtlCustomerPricingProfileVO getRandomCustomerPricingProfileVO() {
        LtlCustomerPricingProfileVO vo = new LtlCustomerPricingProfileVO();
        vo.setLtlPricingProfileId(44L);
        vo.setShipperOrgId(2L);

        return vo;
    }
}
