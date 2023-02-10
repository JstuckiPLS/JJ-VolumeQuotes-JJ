package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.ltlrating.dao.LtlCarrierLiabilitiesDao;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.ProhibitedNLiabilitiesVO;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.service.impl.LtlCarrierLiabilitiesServiceImpl;

/**
 * Test cases for {@link LtlCarrierLiabilitiesServiceImpl}.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlCarrierLiabilitiesServiceImplTest {

    private static final Long CARRIER_LIABILITY_ID = 1L;

    private static final Long PROFILE_ID = 1L;

    private static final Long USER_ID = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlCarrierLiabilitiesEntity entity;

    private LtlPricingProfileEntity profileEntity = new LtlPricingProfileEntity();

    @InjectMocks
    private LtlCarrierLiabilitiesServiceImpl sut;

    @Mock
    private LtlCarrierLiabilitiesDao dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Mock
    private LtlProfileDetailsService profileService;

    @Mock
    private Validator<LtlPricingNotesEntity> validator;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", USER_ID);

        entity = createEntity();
    }

    @Test
    public void testSaveCarrierLiabilities() throws Exception {
        LtlPricingProfileEntity profile = createProfileEntity();
        List<LtlCarrierLiabilitiesEntity> list = Arrays.asList(entity);

        when(dao.saveOrUpdate(entity)).thenReturn(entity);
        when(profileDao.find(PROFILE_ID)).thenReturn(profile);
        when(profileDao.findPricingTypeByProfileId(PROFILE_ID)).thenReturn(BLANKET);

        List<LtlCarrierLiabilitiesEntity> afterSave = sut.saveCarrierLiabilities(list, PROFILE_ID);

        verify(dao, times(1)).deleteLiabilities(PROFILE_ID);
        verify(dao, times(1)).saveOrUpdate(entity);
        assertNotNull(afterSave);
    }

    @Test
    public void testGetCarrierLiabilitiesById() throws Exception {
        when(dao.find(CARRIER_LIABILITY_ID)).thenReturn(entity);

        LtlCarrierLiabilitiesEntity result = sut.getCarrierLiabilitiesById(CARRIER_LIABILITY_ID);

        verify(dao).find(CARRIER_LIABILITY_ID);
        assertNotNull(result);
    }

    @Test
    public void testGetCarrierLiabilitiesByProfileId() throws Exception {
        List<LtlCarrierLiabilitiesEntity> expectedResult = new ArrayList<LtlCarrierLiabilitiesEntity>();

        when(dao.findCarrierLiabilitiesByProfileId(PROFILE_ID)).thenReturn(expectedResult);

        List<LtlCarrierLiabilitiesEntity> actualResult = sut.getCarrierLiabilitiesByProfileId(PROFILE_ID);

        verify(dao).findCarrierLiabilitiesByProfileId(PROFILE_ID);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSaveProhibitedNLiabilities() throws Exception {
        profileEntity.setId(PROFILE_ID);
        profileEntity.setLtlPricingType(PricingType.CSP);
        List<LtlCarrierLiabilitiesEntity> list = Arrays.asList(entity);

        ProhibitedNLiabilitiesVO vo = new ProhibitedNLiabilitiesVO();
        vo.setLiabilities(list);
        vo.setProfile(profileEntity);

        when(dao.saveOrUpdate(entity)).thenReturn(entity);
        when(profileService.saveProfile(profileEntity)).thenReturn(profileEntity);
        when(profileDao.find(PROFILE_ID)).thenReturn(profileEntity);
        when(profileDao.findPricingTypeByProfileId(PROFILE_ID)).thenReturn(BLANKET);

        List<LtlCarrierLiabilitiesEntity> afterSave = sut.saveProhibitedNLiabilities(vo);

        verify(dao, times(1)).deleteLiabilities(PROFILE_ID);
        verify(dao, times(1)).saveOrUpdate(entity);
        verify(profileService, times(1)).saveProfile(profileEntity);
        assertNotNull(afterSave);
    }

    private LtlCarrierLiabilitiesEntity createEntity() {
        LtlCarrierLiabilitiesEntity entity = new LtlCarrierLiabilitiesEntity();

        entity.setId(CARRIER_LIABILITY_ID);
        entity.setPricingProfileId(PROFILE_ID);
        entity.setFreightClass(CommodityClass.CLASS_100);
        entity.setMaxNewProdLiabAmt(BigDecimal.ONE);
        entity.setMaxUsedProdLiabAmt(BigDecimal.ONE);
        entity.setNewProdLiabAmt(BigDecimal.ONE);
        entity.setUsedProdLiabAmt(BigDecimal.ONE);

        return entity;
    }

    private LtlPricingProfileEntity createProfileEntity() {
        LtlPricingProfileEntity entity = new LtlPricingProfileEntity();
        entity.setId(PROFILE_ID);
        entity.setLtlPricingType(PricingType.CSP);
        return entity;
    }
}
