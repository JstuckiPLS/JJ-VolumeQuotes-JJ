package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
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
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.impl.LtlPricingDetailsDaoImpl;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;
import com.pls.ltlrating.service.impl.GeoOptimizingHelper;
import com.pls.ltlrating.service.impl.LtlPricingDetailsServiceImpl;

/**
 * Use cases of {@link LtlPricingDetailsService}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingDetailsServiceImplTest {

    private static final Long CURRENT_USER = 1L;

    private static final Long PRICING_DETAIL_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long[] PRICING_DETAILS_IDS = { 1L, 2L };

    private static final String BLANKET = "BLANKET";

    private static final Long BLANKET_CSP_PROFILE_ID = 8L;

    private static final BigDecimal DEFAULT_MARGIN = new BigDecimal(Math.random());

    private LtlPricingDetailsEntity pricingDetails;

    private PricingDetailListItemVO listItem;

    @Mock
    private LtlPricingDetailsDaoImpl dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private OrganizationPricingDao orgPricingDao;

    @Mock
    private Validator<LtlPricingNotesEntity> validator;

    @Mock
    private GeoOptimizingHelper geoHelper;

    @InjectMocks
    private LtlPricingDetailsServiceImpl sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", CURRENT_USER);

        pricingDetails = createMinimalEntity();
        listItem = createListItem();
    }

    @Test
    public void testSavePricingDetail() throws Exception {
        LtlPricingApplicableCustomersEntity applicableCustomer = createMinimalApplicableCustomerEntity();
        OrganizationPricingEntity orgPricing = createMinimalOrganizationPricingEntity();

        when(dao.saveOrUpdate(pricingDetails)).thenReturn(pricingDetails);
        when(profileDao.findPricingTypeByProfileDetailId(pricingDetails.getLtlPricProfDetailId())).thenReturn(BLANKET);
        when(profileDao.findActivePricingApplicableCustomer(BLANKET_CSP_PROFILE_ID)).thenReturn(applicableCustomer);
        when(profileDao.findChildCSPByProfileDetailId(pricingDetails.getLtlPricProfDetailId())).thenReturn(
                Arrays.asList(createMinimalProfileEntity()));
        when(orgPricingDao.getActivePricing(applicableCustomer.getCustomer().getId())).thenReturn(orgPricing);

        LtlPricingDetailsEntity result = sut.savePricingDetail(pricingDetails);

        verify(geoHelper).improveGeoDetails(pricingDetails);
        verify(dao).saveOrUpdate(pricingDetails);
        assertEquals(pricingDetails, result);
    }

    @Test
    public void testGetPricingDetailById() throws Exception {
        when(dao.find(PRICING_DETAIL_ID)).thenReturn(pricingDetails);

        LtlPricingDetailsEntity result = sut.getPricingDetailById(PRICING_DETAIL_ID);

        verify(dao).find(PRICING_DETAIL_ID);
        assertEquals(pricingDetails, result);
    }

    @Test
    public void testGetActivePricingDetailsByProfileDetailId() throws Exception {
        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(PROFILE_ID)).thenReturn(list);

        List<PricingDetailListItemVO> result = sut.getActivePricingDetailsByProfileDetailId(PROFILE_ID);

        verify(dao).findActiveAndEffectiveByProfileDetailId(PROFILE_ID);
        assertEquals(list, result);
    }

    @Test
    public void testGetInactivePricingDetailsByProfileDetailId() throws Exception {
        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findArchivedPrices(PROFILE_ID)).thenReturn(list);

        List<PricingDetailListItemVO> result = sut.getInactivePricingDetailsByProfileDetailId(PROFILE_ID);

        verify(dao).findArchivedPrices(PROFILE_ID);
        assertEquals(list, result);
    }

    @Test
    public void testGetExpiredPricingDetailsByProfileDetailId() throws Exception {
        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findExpiredByProfileDetailId(PROFILE_ID)).thenReturn(list);

        List<PricingDetailListItemVO> result = sut.getExpiredPricingDetailsByProfileDetailId(PROFILE_ID);

        verify(dao).findExpiredByProfileDetailId(PROFILE_ID);
        assertEquals(list, result);
    }

    @Test
    public void testInactivatePricingDetailsWithActiveParameter() throws Exception {
        when(profileDao.findPricingTypeByProfileDetailId(pricingDetails.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(PROFILE_ID)).thenReturn(list);

        List<Long> expectedIds = Arrays.asList(PRICING_DETAILS_IDS);

        List<PricingDetailListItemVO> result = sut.inactivatePricingDetails(expectedIds, PROFILE_ID, true);

        verify(dao).updateStatus(expectedIds, Status.INACTIVE, CURRENT_USER);
        verify(dao).findActiveAndEffectiveByProfileDetailId(PROFILE_ID);
        assertEquals(list, result);
    }

    @Test
    public void testInactivatePricingDetailsWithInactiveParameter() throws Exception {
        when(profileDao.findPricingTypeByProfileDetailId(pricingDetails.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findExpiredByProfileDetailId(PROFILE_ID)).thenReturn(list);

        List<Long> expectedIds = Arrays.asList(PRICING_DETAILS_IDS);

        List<PricingDetailListItemVO> result = sut.inactivatePricingDetails(expectedIds, PROFILE_ID, false);

        verify(dao).updateStatus(expectedIds, Status.INACTIVE, CURRENT_USER);
        verify(dao).findExpiredByProfileDetailId(PROFILE_ID);
        assertEquals(list, result);
    }

    @Test
    public void testReactivatePricingDetails() throws Exception {
        List<PricingDetailListItemVO> list = new ArrayList<PricingDetailListItemVO>();
        list.add(listItem);

        when(dao.findArchivedPrices(PROFILE_ID)).thenReturn(list);
        when(profileDao.findPricingTypeByProfileDetailId(pricingDetails.getLtlPricProfDetailId())).thenReturn(BLANKET);

        List<Long> expectedIds = Arrays.asList(PRICING_DETAILS_IDS);

        List<PricingDetailListItemVO> result = sut.reactivatePricingDetails(expectedIds, PROFILE_ID);
        verify(dao).updateStatus(expectedIds, Status.ACTIVE, CURRENT_USER);
        verify(dao).findArchivedPrices(PROFILE_ID);
        assertEquals(list, result);
    }

    private LtlPricingDetailsEntity createMinimalEntity() {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();

        entity.setLtlPricProfDetailId(1L);
        entity.setStatus(Status.ACTIVE);
        entity.getModification().setCreatedBy(CURRENT_USER);
        entity.getModification().setModifiedBy(CURRENT_USER);

        return entity;
    }

    private PricingDetailListItemVO createListItem() {
        PricingDetailListItemVO item = new PricingDetailListItemVO();
        item.setId(1L);

        return item;
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
