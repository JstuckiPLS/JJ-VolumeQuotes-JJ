package com.pls.shipment.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;

/**
 * Test cases for {@link BillingAuditReasonsDao}.
 * 
 * @author Brichak Aleksandr
 */

public class LdBillingAuditReasonsDaoImplIT extends AbstractDaoTest {

    @Autowired
    private BillingAuditReasonsDao sut;

    private static final Long ADJUSTMENT_ID = 1L;

    private static final Long PERSON_ID = 1L;

    private static final String REASON_CD = "CD";

    @Test
    public void testSavedBillingAuditReasons() {
        long count = getSession()
                .createSQLQuery(
                        "insert into LD_BILLING_AUDIT_REASONS "
                                + "(COMMENTS, CREATED_BY, DATE_CREATED, FAA_DETAIL_ID, LOAD_ID, MODIFIED_BY,"
                                + " DATE_MODIFIED, REASON_CD, STATUS, LD_BILL_AUDIT_RSN_ID) "
                                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").setString(0, "dd").setLong(1, 1L)
                .setDate(2, new Date()).setLong(3, 1L).setLong(4, 1L).setLong(5, 1L).setDate(6, new Date())
                .setString(7, "AD").setCharacter(8, 'A').setLong(9, 1L).executeUpdate();
        assertNotNull(count);
        assertEquals(1, count);
    }

    @Test
    public void testUpdateBillingAuditReasonsStatusForLoad() {
        sut.createAndSave(REASON_CD, PERSON_ID, null, null);
        long count = getSession().getNamedQuery(LdBillingAuditReasonsEntity.Q_DEACTIVATE_LOAD_REASONS)
                .setParameter("loadId", 1L).setParameter("modifiedBy", PERSON_ID).executeUpdate();
        assertNotNull(count);
        assertEquals(1, count);
    }

    @Test
    public void testUpdateBillingAuditReasonsStatusForAdjustment() {
        sut.createAndSave(REASON_CD, PERSON_ID, null, ADJUSTMENT_ID);
        long count = getSession().getNamedQuery(LdBillingAuditReasonsEntity.Q_DEACTIVATE_ADJUSTMENT_REASONS)
                .setParameter("adjustmentId", ADJUSTMENT_ID).setParameter("modifiedBy", PERSON_ID).executeUpdate();
        assertNotNull(count);
        assertEquals(1, count);
    }

}
