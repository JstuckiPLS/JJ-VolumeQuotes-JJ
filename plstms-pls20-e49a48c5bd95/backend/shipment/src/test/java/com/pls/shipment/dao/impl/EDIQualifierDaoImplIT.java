package com.pls.shipment.dao.impl;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.edi.EDIQualifierDao;
import com.pls.shipment.domain.edi.EDIQualifierEntity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Tests for {@link com.pls.shipment.dao.impl.edi.EDIQualifierDaoImpl}.
 *
 * @author Mikhail Boldinov, 30/08/13
 */
public class EDIQualifierDaoImplIT extends AbstractDaoTest {

    private static final long CARRIER_ID = 58L;

    @Autowired
    private EDIQualifierDao sut;

    @Test
    public void testGetQualifiersForCarrier() {
        final String transactionSetId = "210";
        List<EDIQualifierEntity> qualifiers = sut.getQualifiersForCarrier(CARRIER_ID, transactionSetId);
        Assert.assertNotNull(qualifiers);
        Assert.assertFalse(qualifiers.isEmpty());
        for (EDIQualifierEntity qualifier : qualifiers) {
            Assert.assertNotNull(qualifier);
            Assert.assertEquals(CARRIER_ID, qualifier.getCarrier().getId().longValue());
            Assert.assertEquals(transactionSetId, qualifier.getTransactionSetId());
        }
    }
}
