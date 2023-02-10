package com.pls.core.service.impl.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OutboundEdiQueueMappingDao;
import com.pls.core.domain.OutboundEdiQueueMapEntity;

/**
 * Test case for the service which calls the DAO to receive outbound queue mappings.
 * 
 * @author Yasaman Honarvar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OutboundEdiQueueMappingServiceImplTest {

    @Mock
    private OutboundEdiQueueMappingDao queueDao;

    @InjectMocks
    private OutboundEdiQueueMappingServiceImpl sut;

    private static final Long CUSTOMER_ORG_ID = 1L;

    private static final String UNMAPPED_CARRIER_SCAC = "TEST";

    private static final String CARRIER_SCAC = "TEST2";

    @Before
    public void initialize() {
        OutboundEdiQueueMapEntity expected = new OutboundEdiQueueMapEntity();
        when(queueDao.getQueueMappingsById(CUSTOMER_ORG_ID)).thenReturn(expected);
        when(queueDao.getQueueMappingsByScac(CARRIER_SCAC)).thenReturn(expected);
        when(queueDao.getQueueMappingsByScac(UNMAPPED_CARRIER_SCAC)).thenReturn(null);
    }

    /**
     * Test for retrieving Queue mapping by customer Org ID must return result.
     */
    @Test
    public void testGetCustomerMapping() {
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsById(CUSTOMER_ORG_ID);
        verify(queueDao).getQueueMappingsById(CUSTOMER_ORG_ID);
        assertNotNull(result);
    }

    /**
     * Test for retrieving Queue mapping by carrier scac must return result.
     */
    @Test
    public void testGetExistingCarrierMapping() {
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsByScac(CARRIER_SCAC);
        verify(queueDao).getQueueMappingsByScac(CARRIER_SCAC);
        assertNotNull(result);
    }

    /**
     * Test for retrieving Queue mapping by unmapped carrier scac must return null.
     */
    @Test
    public void testGetNonExistingCarrierMapping() {
        OutboundEdiQueueMapEntity result = sut.getQueueMappingsByScac(UNMAPPED_CARRIER_SCAC);
        verify(queueDao).getQueueMappingsByScac(UNMAPPED_CARRIER_SCAC);
        assertNull(result);
    }

    /**
     * Test for verifiying whether a carrier is queue enabled or not should return false if the carrier is not mapped.
     */
    @Test
    public void testCarrierNotQueueEnabled() {
        boolean result = sut.isQueueEnabled(UNMAPPED_CARRIER_SCAC);
        verify(queueDao).getQueueMappingsByScac(UNMAPPED_CARRIER_SCAC);
        assertFalse(result);
    }

    /**
     * Test for verifiying whether a carrier is queue enabled or not should return true if the carrier is mapped.
     */
    @Test
    public void testCarrierQueueEnabled() {
        boolean result = sut.isQueueEnabled(CARRIER_SCAC);
        verify(queueDao).getQueueMappingsByScac(CARRIER_SCAC);
        assertTrue(result);
    }
}
