package com.pls.core.service.impl.security;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link PlsMD5Encoder} class.
 * 
 * @author Maxim Medvedev
 */
public class PlsMD5EncoderTest {

    @Test
    public void testEncodePasswordForHashCase() {
        PlsMD5Encoder sut = new PlsMD5Encoder();

        String result = sut.encodePassword("test", null);
        Assert.assertEquals(result, result.toUpperCase(Locale.ENGLISH));
    }

    @Test
    public void testEncodePasswordForPasswordCase() {
        PlsMD5Encoder sut = new PlsMD5Encoder();

        Assert.assertEquals(sut.encodePassword("test", null), sut.encodePassword("TEST", null));
    }

}
