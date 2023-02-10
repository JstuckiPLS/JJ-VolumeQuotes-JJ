package com.pls.ltlrating.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.ltlrating.dao.LtlPricingProfileDao;
import com.pls.ltlrating.dao.impl.LtlFuelSurchargeDaoImpl;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import com.pls.ltlrating.service.impl.LtlFuelSurchargeServiceImpl;

/**
 * Mocked tests for {@link LtlFuelSurchargeService}.
 * @author Stas Norochevskiy
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlFuelSurchargeServiceImplTest {

    private static final String BLANKET = "BLANKET";

    @Mock
    private LtlFuelSurchargeDaoImpl dao;

    @Mock
    private LtlPricingProfileDao profileDao;

    @InjectMocks
    private LtlFuelSurchargeServiceImpl ltlFuelSurchargeServiceImpl;

    private LtlFuelSurchargeEntity ltlFuelSurchargeEntity = createMinimalEntity();

    @Test
    public void testSave() {
        when(profileDao.findPricingTypeByProfileDetailId(ltlFuelSurchargeEntity.getLtlPricingProfileId())).thenReturn(BLANKET);
        when(dao.saveOrUpdate(ltlFuelSurchargeEntity)).thenReturn(ltlFuelSurchargeEntity);

        LtlFuelSurchargeEntity result = ltlFuelSurchargeServiceImpl.saveFuelSurcharge(ltlFuelSurchargeEntity);

        verify(dao).saveOrUpdate(ltlFuelSurchargeEntity);
        assertEquals(ltlFuelSurchargeEntity, result);
    }

    private LtlFuelSurchargeEntity createMinimalEntity() {
      LtlFuelSurchargeEntity ltlFuelSurchargeEntity = new LtlFuelSurchargeEntity();
      ltlFuelSurchargeEntity.setLtlPricingProfileId(1L);
      ltlFuelSurchargeEntity.setMinRate(BigDecimal.valueOf(1));
      ltlFuelSurchargeEntity.setMaxRate(BigDecimal.valueOf(5));
      ltlFuelSurchargeEntity.setSurcharge(BigDecimal.valueOf(2));
      return ltlFuelSurchargeEntity;
    }
}
