package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.LtlPricingThirdPartyInfoDao;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;
import com.pls.ltlrating.service.impl.LtlPricingThirdPartyInfoServiceImpl;

/**
 * Test cases for {@link LtlPricingTerminalInfoService} class.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingThirdPartyInfoServiceImplTest {

    private static final Long LTL_THIRD_PARTY_INFO_ID = 1L;

    private static final Long PRICE_PROFILE_ID = 1L;

    private static final Long CURRENT_USER = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlPricingThirdPartyInfoEntity minimalEntity = createMinimalEntity();

    @Mock
    private LtlPricingThirdPartyInfoDao dao;

    @Mock
    private AddressService addressService;

    @Mock
    private Validator<LtlPricingThirdPartyInfoEntity> validator;

    @Mock
    private LtlPricingProfileDao profileDao;

    @InjectMocks
    private LtlPricingThirdPartyInfoServiceImpl sut;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", CURRENT_USER);

        minimalEntity = createMinimalEntity();
    }

    @Test
    public void testSaveThirdPartyInfo() throws Exception {
        when(dao.saveOrUpdate(minimalEntity)).thenReturn(minimalEntity);
        when(dao.find(LTL_THIRD_PARTY_INFO_ID)).thenReturn(minimalEntity);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getPricProfDetailId())).thenReturn(BLANKET);

        LtlPricingThirdPartyInfoEntity result = sut.saveThirdPartyInfo(minimalEntity);

        verify(dao).saveOrUpdate(minimalEntity);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetThirdPartylInfoById() throws Exception {
        when(dao.find(LTL_THIRD_PARTY_INFO_ID)).thenReturn(minimalEntity);

        LtlPricingThirdPartyInfoEntity result = sut.getThirdPartyInfoById(LTL_THIRD_PARTY_INFO_ID);

        verify(dao).find(LTL_THIRD_PARTY_INFO_ID);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetThirdPartylInfoByIdWithNull() throws Exception {
        when(dao.find(LTL_THIRD_PARTY_INFO_ID)).thenReturn(null);

        LtlPricingThirdPartyInfoEntity result = sut.getThirdPartyInfoById(LTL_THIRD_PARTY_INFO_ID);

        verify(dao).find(LTL_THIRD_PARTY_INFO_ID);

        assertEquals(null, result);
    }

    @Test
    public void testGetActiveThirdPartyInfoByProfileDetailId() throws Exception {
        when(dao.findActiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(minimalEntity);

        LtlPricingThirdPartyInfoEntity result = sut.getActiveThirdPartyInfoByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findActiveByProfileDetailId(PRICE_PROFILE_ID);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetActiveThirdPartyInfoByProfileDetailIdWithNull() throws Exception {
        when(dao.findActiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(null);

        LtlPricingThirdPartyInfoEntity result = sut.getActiveThirdPartyInfoByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findActiveByProfileDetailId(PRICE_PROFILE_ID);

        assertEquals(null, result);
    }

    private LtlPricingThirdPartyInfoEntity createMinimalEntity() {
        LtlPricingThirdPartyInfoEntity entity = new LtlPricingThirdPartyInfoEntity();

        entity.setId(LTL_THIRD_PARTY_INFO_ID);
        entity.setPricProfDetailId(PRICE_PROFILE_ID);
        entity.setStatus(Status.ACTIVE);
        entity.setAddress(createAddressEntity());

        return entity;
    }

    private AddressEntity createAddressEntity() {
        AddressEntity address = new AddressEntity();

        address.setAddress1("address1");
        address.setAddress2("address2");

        return address;
    }
}
