package com.pls.core.domain.enums;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link com.pls.core.domain.enums.CommodityClass} class.
 * 
 * @author Maxim Medvedev
 */
public class CommodityClassTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConvertFromDbCode_InvalidString() {
        Assert.assertSame(null, CommodityClass.convertFromDbCode("Blah"));
    }

    @Test
    public void testConvertFromDbCode_NormalCase() {
        Assert.assertSame(CommodityClass.CLASS_100,
                CommodityClass.convertFromDbCode(CommodityClass.CLASS_100.getDbCode()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertFromDbCode_NullString() {
        Assert.assertNull(CommodityClass.convertFromDbCode(null));
    }
}
