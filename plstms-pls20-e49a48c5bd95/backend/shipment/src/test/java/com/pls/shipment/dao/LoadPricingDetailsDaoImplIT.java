package com.pls.shipment.dao;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.shipment.domain.LoadPricingDetailsEntity;

/**
 * Test cases for {@link com.pls.shipment.dao.impl.LoadPricingDetailsDaoImpl} class.
 *
 * @author Ashwini Neelgund
 */
public class LoadPricingDetailsDaoImplIT extends AbstractDaoTest {

    @Autowired
    private LoadPricingDetailsDao pricDtlsDao;

    @Test
    public void testGetShipmentPricingDetails() {
        LoadPricingDetailsEntity pricDtls = pricDtlsDao.getShipmentPricingDetails(1L);
        Assert.assertNotNull(pricDtls);
        Assert.assertNotNull(pricDtls.getLoadPricMaterialDtls());
        Assert.assertFalse(pricDtls.getLoadPricMaterialDtls().isEmpty());
        Assert.assertEquals(new BigDecimal("88.00"), pricDtls.getSmc3MinimumCharge().setScale(2, BigDecimal.ROUND_UP));
        Assert.assertEquals(new BigDecimal("68.95"), pricDtls.getTotalChargeFromSmc3().setScale(2, BigDecimal.ROUND_UP));
        Assert.assertEquals(new BigDecimal("17.60"), pricDtls.getCostAfterDiscount().setScale(2, BigDecimal.ROUND_UP));
        Assert.assertEquals(PricingType.BUY_SELL, pricDtls.getPricingType());
        Assert.assertEquals("68.95", pricDtls.getLoadPricMaterialDtls().iterator().next().getCharge());
    }

    @Test
    public void testDelete() {
        LoadPricingDetailsEntity pricDtls = pricDtlsDao.getShipmentPricingDetails(1L);
        Assert.assertNotNull(pricDtls);
        pricDtlsDao.delete(pricDtls);
        pricDtls = pricDtlsDao.getShipmentPricingDetails(1L);
        Assert.assertNull(pricDtls);
    }
}
