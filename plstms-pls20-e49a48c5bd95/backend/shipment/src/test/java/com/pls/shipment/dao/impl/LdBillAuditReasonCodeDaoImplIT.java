package com.pls.shipment.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;

/**
 * Test cases for {@link BillingAuditReasonCodeDao}.
 * 
 * @author Brichak Aleksandr
 */

public class LdBillAuditReasonCodeDaoImplIT extends AbstractDaoTest {

    @Autowired
    private BillingAuditReasonCodeDao sut;

    @Test
    public void testGetAuditReasonCode() {
        List<LdBillAuditReasonCodeEntity> auditReasonCode = sut.getReasonCodeEntityForReasonType();
        assertNotNull(auditReasonCode);
        assertFalse(auditReasonCode.isEmpty());
        assertEquals(13, auditReasonCode.size());
    }
}
