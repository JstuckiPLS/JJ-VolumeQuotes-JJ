package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlGuaranteedPriceDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.ChargeRuleTypeEnum;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;
import com.pls.ltlrating.service.impl.LtlGuaranteedPriceServiceImpl;

/**
 * Test cases ofr using {@link LtlGuaranteedPriceServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlGuaranteedPriceServiceImplTest {

    private static final Long GUARANTEED_PRICE_ID = 1L;

    private static final Long PRICE_PROFILE_ID = 1L;

    private static final Long CURRENT_USER = 1L;

    private static final BigDecimal DEFAULT_MARGIN = new BigDecimal(Math.random());

    private static final String BLANKET = "BLANKET";

    private static final Long BLANKET_CSP_PROFILE_ID = 8L;

    private LtlGuaranteedPriceEntity minimalEntity;

    private GuaranteedPriceListItemVO listItem;

    @Mock
    private LtlGuaranteedPriceDao dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private OrganizationPricingDao orgPricingDao;

    @Mock
    private Validator<LtlGuaranteedPriceEntity> validator;

    @InjectMocks
    private LtlGuaranteedPriceServiceImpl sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", CURRENT_USER);

        minimalEntity = createMinimalEntity();
    }

    @Test
    public void testSaveGuaranteedPrice() throws Exception {
        LtlPricingApplicableCustomersEntity applicableCustomer = createMinimalApplicableCustomerEntity();
        OrganizationPricingEntity orgPricing = createMinimalOrganizationPricingEntity();

        when(dao.saveOrUpdate(minimalEntity)).thenReturn(minimalEntity);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getLtlPricProfDetailId())).thenReturn(BLANKET);
        when(profileDao.findActivePricingApplicableCustomer(BLANKET_CSP_PROFILE_ID)).thenReturn(applicableCustomer);
        when(profileDao.findChildCSPByProfileDetailId(minimalEntity.getLtlPricProfDetailId())).thenReturn(
                Arrays.asList(createMinimalProfileEntity()));
        when(orgPricingDao.getActivePricing(applicableCustomer.getCustomer().getId())).thenReturn(orgPricing);

        LtlGuaranteedPriceEntity newEntity = sut.saveGuaranteedPrice(minimalEntity);

        verify(dao).saveOrUpdate(newEntity);
        assertNotNull(newEntity);
    }

    @Test
    public void testGetGuaranteedPriceById() throws Exception {
        when(dao.find(GUARANTEED_PRICE_ID)).thenReturn(minimalEntity);

        LtlGuaranteedPriceEntity newEntity = sut.getGuaranteedPriceById(GUARANTEED_PRICE_ID);

        verify(dao).find(GUARANTEED_PRICE_ID);
        assertNotNull(newEntity);
    }

    @Test
    public void testGetAllGuaranteedByProfileDetailId() throws Exception {
        List<LtlGuaranteedPriceEntity> expectedResults = new ArrayList<LtlGuaranteedPriceEntity>();
        expectedResults.add(minimalEntity);

        when(dao.findByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(expectedResults);

        List<LtlGuaranteedPriceEntity> actualResults = sut.getAllGuaranteedByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testGetActiveGuaranteedByProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(expectedResults);

        List<GuaranteedPriceListItemVO> actualResults = sut.getActiveGuaranteedByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findActiveAndEffectiveByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testGetInactiveGuaranteedByProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findByStatusAndProfileDetailId(Status.INACTIVE, PRICE_PROFILE_ID)).thenReturn(expectedResults);

        List<GuaranteedPriceListItemVO> actualResults = sut.getInactiveGuaranteedByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findByStatusAndProfileDetailId(Status.INACTIVE, PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testGetExpiredGuaranteedByProfileDetailId() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findExpiredByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(expectedResults);

        List<GuaranteedPriceListItemVO> actualResults = sut.getExpiredGuaranteedByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findExpiredByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testInactivateGuaranteedPricingsWithActiveParameter() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(expectedResults);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<Long> guaranteedIds = new ArrayList<Long>();
        guaranteedIds.add(GUARANTEED_PRICE_ID);

        List<GuaranteedPriceListItemVO> actualResults = sut.inactivateGuaranteedPricings(guaranteedIds,
                PRICE_PROFILE_ID, true);

        verify(dao).findActiveAndEffectiveByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testInactivateGuaranteedPricingsWithInactiveParameter() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findExpiredByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(expectedResults);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<Long> guaranteedIds = new ArrayList<Long>();
        guaranteedIds.add(GUARANTEED_PRICE_ID);

        List<GuaranteedPriceListItemVO> actualResults = sut.inactivateGuaranteedPricings(guaranteedIds,
                PRICE_PROFILE_ID, false);

        verify(dao).findExpiredByProfileDetailId(PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testreactivateGuaranteedPricings() throws Exception {
        List<GuaranteedPriceListItemVO> expectedResults = new ArrayList<GuaranteedPriceListItemVO>();
        expectedResults.add(listItem);

        when(dao.findByStatusAndProfileDetailId(Status.INACTIVE, PRICE_PROFILE_ID)).thenReturn(expectedResults);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<Long> guaranteedIds = new ArrayList<Long>();
        guaranteedIds.add(GUARANTEED_PRICE_ID);

        List<GuaranteedPriceListItemVO> actualResults = sut
                .reactivateGuaranteedPricings(guaranteedIds, PRICE_PROFILE_ID);

        verify(dao).findByStatusAndProfileDetailId(Status.INACTIVE, PRICE_PROFILE_ID);

        assertNotNull(actualResults);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    private LtlGuaranteedPriceEntity createMinimalEntity() {
        LtlGuaranteedPriceEntity entity = new LtlGuaranteedPriceEntity();

        entity.setLtlPricProfDetailId(PRICE_PROFILE_ID);
        entity.setApplyBeforeFuel(Boolean.TRUE);
        entity.setChargeRuleType(ChargeRuleTypeEnum.FL);
        entity.setStatus(Status.ACTIVE);

        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }

    private LtlPricingProfileEntity createMinimalProfileEntity() {
        LtlPricingProfileEntity entity = new LtlPricingProfileEntity();
        entity.setId(8L);

        LtlPricingProfileDetailsEntity detail = new LtlPricingProfileDetailsEntity();
        detail.setId(9L);
        detail.setLtlPricingProfileId(8L);

        entity.setProfileDetails(new HashSet<LtlPricingProfileDetailsEntity>());
        entity.getProfileDetails().add(detail);

        return entity;
    }

    private LtlPricingApplicableCustomersEntity createMinimalApplicableCustomerEntity() {
        LtlPricingApplicableCustomersEntity entity = new LtlPricingApplicableCustomersEntity();
        entity.setId(1L);
        SimpleOrganizationEntity customer = new SimpleOrganizationEntity();
        customer.setId(1L);
        entity.setCustomer(customer);

        return entity;
    }

    private OrganizationPricingEntity createMinimalOrganizationPricingEntity() {
        OrganizationPricingEntity entity = new OrganizationPricingEntity();
        entity.setDefaultMargin(DEFAULT_MARGIN);

        return entity;
    }
}
