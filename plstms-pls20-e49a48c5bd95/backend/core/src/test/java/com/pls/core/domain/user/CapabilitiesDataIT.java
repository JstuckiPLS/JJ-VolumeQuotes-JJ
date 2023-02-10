package com.pls.core.domain.user;

import org.junit.Assert;

import org.hibernate.Query;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;

/**
 * Test cases for DB content.
 * 
 * @author Maxim Medvedev
 */
public class CapabilitiesDataIT extends AbstractDaoTest {

    @Test
    public void testDbData() {
        Query query = getSession().createSQLQuery(
                "SELECT COUNT(*) FROM FLATBED.CAPABILITIES WHERE NAME=:name AND SYS_20 = 'Y'");

        for (Capabilities current : Capabilities.values()) {
            query.setParameter("name", current.name());
            int cont = ((Number) query.uniqueResult()).intValue();
            Assert.assertEquals("Record '" + current.name() + "' was not found", 1, cont);
        }
    }

}
