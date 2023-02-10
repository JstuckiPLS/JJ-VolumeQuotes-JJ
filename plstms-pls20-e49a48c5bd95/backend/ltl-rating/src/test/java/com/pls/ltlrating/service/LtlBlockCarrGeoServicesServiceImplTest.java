package com.pls.ltlrating.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlBlockCarrGeoServicesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;
import com.pls.ltlrating.domain.bo.BlockCarrierListItemVO;
import com.pls.ltlrating.service.impl.LtlBlockCarrGeoServicesServiceImpl;

/**
 * Test cases for {@link LtlBlockCarrGeoServicesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlBlockCarrGeoServicesServiceImplTest {

    private static final Long BLOCK_CARRIER_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlBlockCarrGeoServicesEntity entity;

    private BlockCarrierListItemVO listItem;

    @InjectMocks
    private LtlBlockCarrGeoServicesServiceImpl sut;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private LtlBlockCarrGeoServicesDao dao;

    @Mock
    private Validator<LtlBlockCarrGeoServicesEntity> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        entity = createEntity();
        listItem = createListItem();
    }

    @Test
    public void testGetBlockedCarrierGeoServiceById() throws Exception {
        when(dao.find(BLOCK_CARRIER_ID)).thenReturn(entity);

        LtlBlockCarrGeoServicesEntity result = sut.getBlockedCarrierGeoServiceById(BLOCK_CARRIER_ID);
        verify(dao).find(BLOCK_CARRIER_ID);
        assertNotNull(result);
    }

    @Test
    public void testGetActiveBlockedCarrGeoServByProfileDetailId() throws Exception {
        List<BlockCarrierListItemVO> expectedResult = Arrays.asList(listItem);
        when(dao.findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID)).thenReturn(expectedResult);

        List<BlockCarrierListItemVO> actualResult = sut.getActiveBlockedCarrGeoServByProfileDetailId(PROFILE_ID);
        verify(dao).findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testGetInactiveBlockedCarrGeoServByProfileDetailId() throws Exception {
        List<BlockCarrierListItemVO> expectedResult = Arrays.asList(listItem);
        when(dao.findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID)).thenReturn(expectedResult);

        List<BlockCarrierListItemVO> actualResult = sut
                .getInactiveBlockedCarrGeoServByProfileDetailId(PROFILE_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    public void testInactivateBlockedCarrierGeoServices() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(profileDao.findPricingTypeByProfileDetailId(PROFILE_ID)).thenReturn(BLANKET);

        sut.inactivateBlockedCarrierGeoServices(ids, PROFILE_ID);
        verify(dao).updateStatus(ids, Status.INACTIVE, USER_ID);
        verify(dao).findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);
    }

    @Test
    public void testReactivateBlockedCarrierGeoServices() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(profileDao.findPricingTypeByProfileDetailId(PROFILE_ID)).thenReturn(BLANKET);

        sut.reactivateBlockedCarrierGeoServices(ids, PROFILE_ID);
        verify(dao).updateStatus(ids, Status.ACTIVE, USER_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);
    }

    private LtlBlockCarrGeoServicesEntity createEntity() {
        LtlBlockCarrGeoServicesEntity entity = new LtlBlockCarrGeoServicesEntity();

        entity.setId(BLOCK_CARRIER_ID);
        entity.setProfileId(PROFILE_ID);

        return entity;
    }

    private BlockCarrierListItemVO createListItem() {
        BlockCarrierListItemVO entity = new BlockCarrierListItemVO();

        entity.setId(BLOCK_CARRIER_ID);
        entity.setProfileId(PROFILE_ID);

        return entity;
    }
}
