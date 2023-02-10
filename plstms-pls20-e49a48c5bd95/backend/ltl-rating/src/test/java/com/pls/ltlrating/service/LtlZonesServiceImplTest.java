package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
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
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.domain.bo.ZonesListItemVO;
import com.pls.ltlrating.service.impl.LtlZonesServiceImpl;

/**
 * Test cases for {@link LtlZonesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlZonesServiceImplTest {

    private static final Long ZONE_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlZonesEntity entity;

    private ZonesListItemVO listItem;

    @InjectMocks
    private LtlZonesServiceImpl sut;

    @Mock
    private LtlZonesDao dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private Validator<LtlZonesEntity> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        entity = createEntity();
        listItem = createListItem();
    }

    @Test
    public void testSaveLTLZone() throws Exception {
        when(dao.merge(entity)).thenReturn(entity);
        when(profileDao.findPricingTypeByProfileDetailId(entity.getLtlPricProfDetailId())).thenReturn(BLANKET);

        LtlZonesEntity afterSave = sut.saveLTLZone(entity);
        verify(dao).merge(entity);
        assertNotNull(afterSave);
    }

    @Test
    public void testGetLTLZoneById() throws Exception {
        when(dao.find(ZONE_ID)).thenReturn(entity);

        LtlZonesEntity result = sut.getLTLZoneById(ZONE_ID);
        verify(dao).find(ZONE_ID);
        assertNotNull(result);
    }

    @Test
    public void testGetAllLTLZonesByProfileDetailId() throws Exception {
        List<LtlZonesEntity> expectedResult = Arrays.asList(entity);

        when(dao.findByProfileDetailId(PROFILE_ID)).thenReturn(expectedResult);

        List<LtlZonesEntity> actualResult = sut.getAllLTLZonesByProfileDetailId(PROFILE_ID);
        verify(dao).findByProfileDetailId(PROFILE_ID);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetInactiveLTLZonesByProfileDetailId() throws Exception {
        List<ZonesListItemVO> expectedResult = Arrays.asList(listItem);

        when(dao.findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID)).thenReturn(expectedResult);

        List<ZonesListItemVO> actualResult = sut.getInactiveLTLZonesByProfileDetailId(PROFILE_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetActiveLTLZonesByProfileDetailId() throws Exception {
        List<ZonesListItemVO> expectedResult = Arrays.asList(listItem);

        when(dao.findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID)).thenReturn(expectedResult);

        List<ZonesListItemVO> actualResult = sut.getActiveLTLZonesByProfileDetailId(PROFILE_ID);
        verify(dao).findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testInactivateLtlZones() throws Exception {
        when(profileDao.findPricingTypeByProfileDetailId(PROFILE_ID)).thenReturn(BLANKET);
        List<Long> ids = Arrays.asList(1L, 2L);

        sut.inactivateLTLZones(ids, PROFILE_ID);
        verify(dao).updateStatus(ids, Status.INACTIVE, USER_ID);
        verify(dao).findByStatusAndProfileId(Status.ACTIVE, PROFILE_ID);
    }

    @Test
    public void testReactivateLtlZones() throws Exception {
        when(profileDao.findPricingTypeByProfileDetailId(PROFILE_ID)).thenReturn(BLANKET);
        List<Long> ids = Arrays.asList(1L, 2L);

        sut.reactivateLTLZones(ids, PROFILE_ID);
        verify(dao).updateStatus(ids, Status.ACTIVE, USER_ID);
        verify(dao).findByStatusAndProfileId(Status.INACTIVE, PROFILE_ID);
    }

    private LtlZonesEntity createEntity() {
        LtlZonesEntity entity = new LtlZonesEntity();

        entity.setId(ZONE_ID);
        entity.setLtlPricProfDetailId(PROFILE_ID);
        entity.setName("Name: " + Math.random());
        entity.setStatus(Status.ACTIVE);

        return entity;
    }

    private ZonesListItemVO createListItem() {
        ZonesListItemVO entity = new ZonesListItemVO();

        entity.setId(ZONE_ID);
        entity.setProfileId(PROFILE_ID);
        entity.setName("Name: " + Math.random());
        entity.setStatus(Status.ACTIVE.getCode());

        return entity;
    }
}
