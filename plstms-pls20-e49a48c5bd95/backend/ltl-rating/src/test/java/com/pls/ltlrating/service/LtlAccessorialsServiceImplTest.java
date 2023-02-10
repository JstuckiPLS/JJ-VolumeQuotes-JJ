package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.organization.OrganizationPricingEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.impl.LtlAccessorialsDaoImpl;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.AccessorialListItemVO;
import com.pls.ltlrating.service.impl.LtlAccessorialsServiceImpl;
import com.pls.ltlrating.shared.TestDataHelper;

/**
 * Test cases for {@link LtlAccessorialsServiceImpl} class.
 *
 * Looking at here is the type of data we store and retrieve:
 *
 * 1. Selecting accessorial is mandatory.
 * 2. An accessorial can be blocked or unblocked and if it is blocked, it cannot be used temporarily.
 * 3. Cost : User can set up cost based on cost type and weight or distance or both.
 * 4. Margin : User can set up margin based on margin type weight or distance or both. User can also set
 * minimum margin percent or fixed margin amount.
 * 5. Effective Date is required and Expiration date is optional. Effective date should be defaulted to
 * current date if not available.
 * 6. Note is optional.
 * 7. Origin and Destination also are optional and they hold either State Code, or Complete Zip code ranges,
 * or 3 digit Zip code ranges, or 3 digit single zip codes or 5 digit single zip code.
 * These are the origins and destinations to which this accessorial and rate is applicable
 *
 * Each scenario that can be possible with this type of data is defined at each DAO testcase level and tested the same
 * and hence no need to cover all these scenarios here.
 *
 * List of Service methods that need to be covered:
 * -------------------------------------------------
 * saveAccessorial(LtlAccessorialsEntity accessorial);
 * inactivateAccessorials(List<Long> accessorialIds, Long profileDetailId, Boolean isActiveList);
 * reactivateAccessorials(List<Long> accessorialIds, Long profileDetailId);
 * getAccessorialById(Long id);
 * getAllAccessorialsByProfileDetailId(Long profileDetailId);
 * getActiveAccessorialsByProfileDetailId(Long profileDetailId);
 * getInactiveAccessorialsByProfileDetailId(Long profileDetailId);
 * getExpiredAccessorialsByProfileDetailId(Long profileDetailId);
 *
 * Dont need to cover these methods in depth as they are covered in DAO test class
 * and they are straight forward call to Dao class.
 * Also I believe we need IntegrationTestCases for all these methods instead of these test cases
 * as these are not adding much value and Integration test cases are not covered in this project.
 *
 * @author Hima Bindu Challa
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlAccessorialsServiceImplTest {

    private final LtlAccessorialsEntity newAccessorial = TestDataHelper.createLTLAccessorial();

    private final LtlAccessorialsEntity existingAccessorial = createExistingEntity();

    private final AccessorialListItemVO listItem = createListItem();

    private static final String BLANKET = "BLANKET";

    private static final Long BLANKET_CSP_PROFILE_ID = 8L;

    private static final BigDecimal DEFAULT_MARGIN = new BigDecimal(Math.random());

    @Mock
    private LtlAccessorialsDaoImpl dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private OrganizationPricingDao orgPricingDao;

    @InjectMocks
    private LtlAccessorialsServiceImpl sut;

    /**
     * Service Method: saveAccessorial(LtlAccessorialsEntity accessorial);
     *
     * Testcase to test creating and updating accessorial.
     */
    @Test
    public void testSaveAccessorial() throws Exception {
        LtlPricingApplicableCustomersEntity applicableCustomer = createMinimalApplicableCustomerEntity();
        OrganizationPricingEntity orgPricing = createMinimalOrganizationPricingEntity();

        when(dao.saveOrUpdate(newAccessorial)).thenReturn(newAccessorial);
        when(profileDao.findPricingTypeByProfileDetailId(newAccessorial.getLtlPricProfDetailId())).thenReturn(BLANKET);
        when(profileDao.findActivePricingApplicableCustomer(BLANKET_CSP_PROFILE_ID)).thenReturn(applicableCustomer);
        when(profileDao.findChildCSPByProfileDetailId(newAccessorial.getLtlPricProfDetailId())).thenReturn(
                Arrays.asList(createMinimalProfileEntity()));
        when(orgPricingDao.getActivePricing(applicableCustomer.getCustomer().getId())).thenReturn(orgPricing);

        //Create new accessorial
        LtlAccessorialsEntity result = sut.saveAccessorial(newAccessorial);

        //Test created new accessorial
        verify(dao).saveOrUpdate(newAccessorial);
        assertEquals(newAccessorial, result);

        Long id = result.getId();

        newAccessorial.setAccessorialType(TestDataHelper.ACCESSORIAL_TYPE_INSIDE_PICKUP);

        //Update accessorial
        LtlAccessorialsEntity updatedResult = sut.saveAccessorial(newAccessorial);

        //Test updated accessorial
        assertEquals(newAccessorial, updatedResult);
        assertEquals(id, updatedResult.getId());
        assertEquals(TestDataHelper.ACCESSORIAL_TYPE_INSIDE_PICKUP, updatedResult.getAccessorialType());
    }

    /**
     * Service Method: inactivateAccessorials(List<Long> accessorialIds, Long profileDetailId, Boolean isActiveList);
     *
     * Testcase to test creating and updating accessorial.
     */
    @Test
    public void testInactivateAccessorials() throws Exception {
        List<AccessorialListItemVO> list = new ArrayList<AccessorialListItemVO>();
        list.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(list);
        when(profileDao.findPricingTypeByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(BLANKET);

        List<Long> accessorialIds = new ArrayList<Long>();
        accessorialIds.add(TestDataHelper.LTL_ACCESSORIAL_ID_1);

        List<AccessorialListItemVO> newResult = sut.inactivateAccessorials(accessorialIds,
                TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2, true);

//        verify(dao).updateStatusById(Status.INACTIVE, TestDataHelper.LTL_ACCESSORIAL_ID_1);
        verify(dao).findActiveAndEffectiveByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);
        assertEquals(list, newResult);

    }

    /**
     * Service Method: reactivateAccessorials(List<Long> accessorialIds, Long profileDetailId);
     *
     * Testcase to test creating and updating accessorial.
     */
    @Test
    public void testReactivateAccessorials() throws Exception {
        List<AccessorialListItemVO> list = new ArrayList<AccessorialListItemVO>();
        list.add(listItem);

        when(dao.findAllByStatusAndProfileDetailId(
                TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2, Status.INACTIVE)).thenReturn(list);
        when(profileDao.findPricingTypeByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(BLANKET);

        List<Long> accessorialIds = new ArrayList<Long>();
        accessorialIds.add(TestDataHelper.INACTIVE_LTL_ACCESSORIAL_ID_11);

        List<AccessorialListItemVO> newResult = sut.reactivateAccessorials(accessorialIds,
                TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);

//        verify(dao).updateStatusById(Status.ACTIVE, TestDataHelper.INACTIVE_LTL_ACCESSORIAL_ID_11);
        verify(dao).findAllByStatusAndProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2, Status.INACTIVE);
        assertEquals(list, newResult);
    }

    /**
     * Service Method: getAccessorialById(Long Id);
     *
     * Testcase to test getting Accessorial by primary Key. No transaction is required
     */
    @Test
    public void testGetAccessorialById() throws Exception {
        when(dao.find(TestDataHelper.LTL_ACCESSORIAL_ID_1)).thenReturn(existingAccessorial);

        LtlAccessorialsEntity result = sut.getAccessorialById(TestDataHelper.LTL_ACCESSORIAL_ID_1);

        verify(dao).find(TestDataHelper.LTL_ACCESSORIAL_ID_1);
        assertEquals(existingAccessorial, result);
    }

    /**
     * Service Method: getAllAccessorialsByProfileDetailId(Long profileDetailId);
     *
     * Testcase to test retrieving All Accessorials irrespective of status
     * and expiration date for the given profile detail (Buy/Sell/None).
     */
    @Test
    public void testGetAllAccessorialsByProfileDetailId() throws Exception {
        List<LtlAccessorialsEntity> list = new ArrayList<LtlAccessorialsEntity>();
        list.add(existingAccessorial);

        when(dao.findAllByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(list);

        List<LtlAccessorialsEntity> result =
                sut.getAllAccessorialsByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);

        verify(dao).findAllByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);
        assertEquals(list, result);
    }

    /**
     * Service Method: getActiveAccessorialsByProfileDetailId(Long profileDetailId);
     *
     * Testcase to test retrieving All Active and Effective Accessorials (LOCALTIMESTAMP <= expdate)
     * for selected profile detail (Buy/Sell/None).
     */
    @Test
    public void testGetActiveAccessorialsByProfileDetailId() throws Exception {
        List<AccessorialListItemVO> list = new ArrayList<AccessorialListItemVO>();
        list.add(listItem);

        when(dao.findActiveAndEffectiveByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(list);

        List<AccessorialListItemVO> result =
                sut.getActiveAccessorialsByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);

        verify(dao).findActiveAndEffectiveByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);
        assertEquals(list, result);
    }

    /**
     * Service Method: getInactiveAccessorialsByProfileDetailId(Long profileDetailId);
     *
     * Testcase to test retrieving All inactive Accessorials for selected profile detail (Buy/Sell/None).
     */
    @Test
    public void testGetInactiveAccessorialsByProfileDetailId() throws Exception {
        List<AccessorialListItemVO> list = new ArrayList<AccessorialListItemVO>();
        list.add(listItem);

        when(dao.findAllByStatusAndProfileDetailId(
                TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2, Status.INACTIVE)).thenReturn(list);

        List<AccessorialListItemVO> result =
                sut.getInactiveAccessorialsByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);

        verify(dao).findAllByStatusAndProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2, Status.INACTIVE);
        assertEquals(list, result);
    }

    /**
     * Service Method: getExpiredAccessorialsByProfileDetailId(Long profileDetailId);
     *
     * Testcase to test retrieving active and expired (LOCALTIMESTAMP > expdate) Accessorials for selected profile detail
     * (Buy/Sell/None).
     */
    @Test
    public void testGetExpiredAccessorialsByProfileDetailId() throws Exception {
        List<AccessorialListItemVO> list = new ArrayList<AccessorialListItemVO>();
        list.add(listItem);

        when(dao.findExpiredByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2)).thenReturn(list);

        List<AccessorialListItemVO> result =
                sut.getExpiredAccessorialsByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);

        verify(dao).findExpiredByProfileDetailId(TestDataHelper.LTL_PRIC_PROF_DETAIL_ID_2);
        assertEquals(list, result);
    }


    private LtlAccessorialsEntity createExistingEntity() {
        LtlAccessorialsEntity entity = TestDataHelper.createLTLAccessorial();

        entity.setId(TestDataHelper.LTL_ACCESSORIAL_ID_1);
        entity.setVersion(1);
        entity.setStatus(Status.ACTIVE);
        entity.getModification().setCreatedBy(TestDataHelper.CURRENT_USER_PERSON_ID);
        entity.getModification().setCreatedDate(new Date());
        entity.getModification().setModifiedBy(TestDataHelper.CURRENT_USER_PERSON_ID);
        entity.getModification().setModifiedDate(new Date());
        return entity;
    }

    private AccessorialListItemVO createListItem() {
        AccessorialListItemVO listItem = new AccessorialListItemVO();

        LtlAccessorialsEntity entity = TestDataHelper.createLTLAccessorial();
        listItem.setStatus(entity.getStatus().getCode());
        listItem.setId(entity.getId());
        listItem.setMinCost(entity.getMinCost());
        listItem.setType(entity.getAccessorialType());

        return listItem;
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
