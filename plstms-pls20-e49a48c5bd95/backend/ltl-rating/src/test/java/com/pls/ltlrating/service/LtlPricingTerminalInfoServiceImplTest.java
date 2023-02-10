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
import com.pls.ltlrating.dao.LtlPricingTerminalInfoDao;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;
import com.pls.ltlrating.service.impl.LtlPricingTerminalInfoServiceImpl;

/**
 * Test cases for {@link LtlPricingTerminalInfoService} class.
 *
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingTerminalInfoServiceImplTest {

    private static final Long LTL_TERMINAL_INFO_ID = 1L;

    private static final Long PRICE_PROFILE_ID = 1L;

    private static final Long CURRENT_USER = 1L;

    private static final String BLANKET = "BLANKET";

    private LtlPricingTerminalInfoEntity minimalEntity;

    @Mock
    private LtlPricingTerminalInfoDao dao;

    @Mock
    private AddressService addressService;

    @Mock
    private Validator<LtlPricingTerminalInfoEntity> validator;

    @InjectMocks
    private LtlPricingTerminalInfoServiceImpl sut;

    @Mock
    private LtlPricingProfileDao profileDao;

    @Before
    public void setUp() {
        SecurityTestUtils.login("Test", CURRENT_USER);

        minimalEntity = createMinimalEntity();
    }

    @Test
    public void testSaveCarrierTerminalInfo() throws Exception {
        when(dao.saveOrUpdate(minimalEntity)).thenReturn(minimalEntity);
        when(dao.find(LTL_TERMINAL_INFO_ID)).thenReturn(minimalEntity);
        when(profileDao.findPricingTypeByProfileDetailId(minimalEntity.getPriceProfileId())).thenReturn(BLANKET);

        LtlPricingTerminalInfoEntity result = sut.saveCarrierTerminalInfo(minimalEntity);

        verify(dao).saveOrUpdate(minimalEntity);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetCarrierTerminalInfoById() throws Exception {
        when(dao.find(LTL_TERMINAL_INFO_ID)).thenReturn(minimalEntity);

        LtlPricingTerminalInfoEntity result = sut.getCarrierTerminalInfoById(LTL_TERMINAL_INFO_ID);

        verify(dao).find(LTL_TERMINAL_INFO_ID);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetActiveCarrierTerminalInfoByProfileDetailId() throws Exception {
        when(dao.findActiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(minimalEntity);

        LtlPricingTerminalInfoEntity result = sut.getActiveCarrierTerminalInfoByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findActiveByProfileDetailId(PRICE_PROFILE_ID);

        assertEquals(minimalEntity, result);
    }

    @Test
    public void testGetActiveCarrierTerminalInfoByProfileDetailIdWithNull() throws Exception {
        when(dao.findActiveByProfileDetailId(PRICE_PROFILE_ID)).thenReturn(null);

        LtlPricingTerminalInfoEntity result = sut.getActiveCarrierTerminalInfoByProfileDetailId(PRICE_PROFILE_ID);

        verify(dao).findActiveByProfileDetailId(PRICE_PROFILE_ID);

        assertEquals(null, result);
    }

    private LtlPricingTerminalInfoEntity createMinimalEntity() {
        LtlPricingTerminalInfoEntity entity = new LtlPricingTerminalInfoEntity();

        entity.setId(LTL_TERMINAL_INFO_ID);
        entity.setPriceProfileId(PRICE_PROFILE_ID);
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
