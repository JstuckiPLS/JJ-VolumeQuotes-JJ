package com.pls.core.service.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;

/**
 * This integration test file, tests the functionality of outbound queue utility class.
 * 
 * @author Yasaman Honarvar
 *
 */

public class OutboundEdiQueueMappingUtilsIT extends BaseServiceITClass {

    @Autowired
    private OutboundEdiQueueMappingUtils capabilityUtil;

    @Test
    public void testContainsEntry() {
        Assert.assertTrue(capabilityUtil.isQueueEnabled("RLCA"));
    }

    @Test
    public void testNotContainsEntry() {
        Assert.assertFalse(capabilityUtil.isQueueEnabled("ABCD"));
    }

    @Test
    public void testObjectFoundByIdNotNull() {
        Assert.assertNotNull(capabilityUtil.getOutboundEdiQueueMapEntityById(25L));
    }

    @Test
    public void testObjectFoundByScacNotNull() {
        Assert.assertNotNull(capabilityUtil.getOutboundEdiQueueMapEntityByScac("RLCA"));
    }
}
