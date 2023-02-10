package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.OutboundEdiQueueMappingDao;
import com.pls.core.domain.OutboundEdiQueueMapEntity;

/**
 * Unit test for testing the functionality of the operations done from QueueMappingDao.
 * 
 * @author Yasaman Palumbo
 *
 */
public class OutboundEdiQueueMappingDaoImplTest extends AbstractDaoTest {

    @Autowired
    private OutboundEdiQueueMappingDao sut;

    @Test
    public void testFindMappingsById() {
        Integer priority = 5;
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsById(1L);
        assertNotNull(result);
        assertNull(result.getScac());
        assertEquals(priority, result.getPriority());
        assertEquals("EDI20OutboundQueue1", result.getQueueName());
    }

    @Test
    public void testOrganizationNotQueueEnabled() {
        // Organization with id of 99999 doesn't exist in the queue mapping
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsById(99999L);
        assertNull(result);
    }

    @Test
    public void testCarrierNotQueueEnabled() {
        // No carrier with Scac TEST exists in the queue mapping
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsByScac("TEST");
        assertNull(result);
    }

    @Test
    public void testFindCarrierMapping() {
        Long orgId = 25L;
        Integer priority = 1;
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsByScac("RLCA");
        assertNotNull(result);
        assertEquals(orgId, result.getId());
        assertEquals(priority, result.getPriority());
        assertEquals("EDI20OutboundQueue1", result.getQueueName());
    }
}
