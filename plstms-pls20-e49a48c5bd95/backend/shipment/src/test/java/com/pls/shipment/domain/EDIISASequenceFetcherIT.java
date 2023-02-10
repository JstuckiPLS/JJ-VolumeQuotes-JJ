package com.pls.shipment.domain;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.impl.edi.EDISequencesFetcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test cases for {@link EDISequencesFetcher}.
 *
 * @author Mikhail Boldinov, 23/10/13
 */
public class EDIISASequenceFetcherIT extends AbstractDaoTest {

    @Autowired
    private EDISequencesFetcher sut;

    @Test
    public void testGetNextISA() {
        Long value1 = sut.getNextISA();
        Assert.assertNotNull(value1);
        Long value2 = sut.getNextISA();
        Assert.assertNotNull(value2);
        Assert.assertNotEquals(value1, value2);
        Assert.assertEquals(1L, value2 - value1);
    }

    @Test
    public void testGetNextVal() {
        Long value1 = sut.getNextGS();
        Assert.assertNotNull(value1);
        Long value2 = sut.getNextGS();
        Assert.assertNotNull(value2);
        Assert.assertNotEquals(value1, value2);
        Assert.assertEquals(1L, value2 - value1);
    }
}
