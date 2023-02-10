package com.pls.shipment.service.impl.edi;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.sterling.AcknowledgementJaxbBO;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * This unit test is written to ensure the Acknowledgement Service works the way desired for both customers and carriers. test cases includes lookup
 * for load using load id or bol & scac or customer org id & shipment no. test case for application exception. test case for null load. test case for
 * reject statuses like "R", "M", "W" and "X".
 *
 * @author Jasmin Dhamelia
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AcknowledgementServiceImplTest {

    @Mock
    protected LtlShipmentDao ltlShipmentDao;

    @Mock
    protected LoadTrackingDao loadTrackingDao;

    @Mock
    protected EDIEmailSender ediEmailSender;

    @Mock
    private CustomerEntity organization;

    @Mock
    protected LoadEntity load;

    @Mock
    private OrganizationLocationEntity location;

    @Mock
    private AccountExecutiveEntity activeAccountExecutive;

    @Mock
    private LoadTrackingStatusEntity status;

    @Mock
    private PlainModificationObject modification;

    @Mock
    private UserEntity user;

    @Mock
    private UserEntity accountExecutive;

    @InjectMocks
    private AcknowledgementServiceImpl acknowledgementServiceImpl;

    private static final String SCAC = "RCLA";
    private static final String BOL = "128";
    private static final String STATUS_ACCEPTED = "A";
    private static final String STATUS_REJECTED = "R";
    private static final String TRANSACTION_SET_ID = "210";
    private static final String SHIPMENT_NO = "129";
    private static final String B2BI_FILE_NAME = "XML File";
    private static final Long LOAD_ID = 7001L;
    private static final Long PERSON_ID = 1L;
    private static final Long NETWORK_ID_NOT_LTL = 6L;
    private static final Long CUSTOMER_ORG_ID = 1L;

    @Before
    public void initialize() {
        when(load.getLocation()).thenReturn(location);
        when(location.getActiveAccountExecutive()).thenReturn(activeAccountExecutive);
        when(activeAccountExecutive.getUser()).thenReturn(accountExecutive);
        when(accountExecutive.getEmail()).thenReturn("test@testmail.com");
        when(load.getModification()).thenReturn(modification);
        when(status.getDescription()).thenReturn("Test Description");
        when(modification.getCreatedUser()).thenReturn(user);
        when(user.getEmail()).thenReturn("test@testmail.com");
    }

    private AcknowledgementJaxbBO createAcknowledgementBO(String status, Long loadId, Long customerOrgId, String scac) {
        AcknowledgementJaxbBO acknowledgementBO = new AcknowledgementJaxbBO();
        if (loadId != null) {
            acknowledgementBO.setLoadId(loadId);
        }
        if (customerOrgId != null) {
            acknowledgementBO.setCustomerOrgId(customerOrgId);
        }
        if (scac != null) {
            acknowledgementBO.setScac(scac);
        }
        acknowledgementBO.setPersonId(PERSON_ID);
        acknowledgementBO.setTransactionSetId(TRANSACTION_SET_ID);
        acknowledgementBO.setStatus(status);
        acknowledgementBO.setB2biFileName(B2BI_FILE_NAME);
        acknowledgementBO.setRecvDateTime(new Date());

        return acknowledgementBO;
    }

    private LoadTrackingEntity createLoadTrackingEntity(AcknowledgementJaxbBO acknowledgementBO) {
        LoadTrackingEntity loadTrackingEntity = new LoadTrackingEntity();
        loadTrackingEntity.setStatusCode(acknowledgementBO.getStatus());
        loadTrackingEntity.setFreeMessage(TRANSACTION_SET_ID);
        loadTrackingEntity.setTrackingDate(acknowledgementBO.getRecvDateTime());
        loadTrackingEntity.setCreatedBy(acknowledgementBO.getPersonId());
        loadTrackingEntity.setSource(IntegrationService.EDI_997);
        return loadTrackingEntity;
    }

    @Test
    public void testLoadFoundByLoadId() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_ACCEPTED, LOAD_ID, null, null);
        when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(1)).saveOrUpdate(createLoadTrackingEntity(acknowledgementBO));
        verify(ltlShipmentDao, Mockito.times(0)).findShipmentsByScacAndBolNumber(SCAC, BOL);
        verify(ltlShipmentDao, Mockito.times(0)).findShipmentByShipmentNumber(CUSTOMER_ORG_ID, SHIPMENT_NO);
    }

    @Test
    public void testLoadFoundByScacAndBol() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_ACCEPTED, null, null, SCAC);
        acknowledgementBO.setBol(BOL);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Collections.singletonList(load));
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(1)).saveOrUpdate(createLoadTrackingEntity(acknowledgementBO));
        verify(ltlShipmentDao, Mockito.times(0)).find(LOAD_ID);
        verify(ltlShipmentDao, Mockito.times(0)).findShipmentByShipmentNumber(CUSTOMER_ORG_ID, SHIPMENT_NO);
    }

    @Test
    public void testLoadFoundByScacAndBolWithMultipleResults() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_ACCEPTED, null, null, SCAC);
        acknowledgementBO.setBol(BOL);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Arrays.asList(load, load));
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(0)).saveOrUpdate(Mockito.any());
        verify(ltlShipmentDao, Mockito.times(0)).find(LOAD_ID);
        verify(ltlShipmentDao, Mockito.times(0)).findShipmentByShipmentNumber(CUSTOMER_ORG_ID, SHIPMENT_NO);
    }

    @Test
    public void testLoadFoundByCustOrgIdAndShipNo() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_ACCEPTED, null, CUSTOMER_ORG_ID, null);
        acknowledgementBO.setShipmentNo(SHIPMENT_NO);
        when(ltlShipmentDao.findShipmentByShipmentNumber(CUSTOMER_ORG_ID, SHIPMENT_NO)).thenReturn(load);
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(1)).saveOrUpdate(createLoadTrackingEntity(acknowledgementBO));
        verify(ltlShipmentDao, Mockito.times(0)).find(LOAD_ID);
        verify(ltlShipmentDao, Mockito.times(0)).findShipmentsByScacAndBolNumber(SCAC, BOL);
    }

    @Test
    public void testApplicationException() {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_ACCEPTED, LOAD_ID, null, null);
        when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);
        doThrow(ApplicationException.class).when(loadTrackingDao).saveOrUpdate((LoadTrackingEntity) anyObject());
        try {
            acknowledgementServiceImpl.processMessage(acknowledgementBO);
            fail("Application exception not thrown");
        } catch (ApplicationException e) {
            verify(ediEmailSender, Mockito.times(1)).loadTrackingFailed(acknowledgementBO.getB2biFileName(), e.getMessage(),
                    IntegrationService.EDI_997.toString(), Collections.singletonList(load.getId()));
        }
    }

    @Test
    public void testLoadFoundByLoadIdRejectedNotLTL() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_REJECTED, LOAD_ID, null, null);
        when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);
        when(load.getOrganization()).thenReturn(organization);
        when(organization.getNetworkId()).thenReturn(NETWORK_ID_NOT_LTL);
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(1)).saveOrUpdate(createLoadTrackingEntity(acknowledgementBO));
        verify(ediEmailSender, Mockito.times(2)).forCreatedUser(accountExecutive.getEmail(), load, TRANSACTION_SET_ID);
        verify(ediEmailSender, Mockito.times(0)).forLTLDistributionList(load, TRANSACTION_SET_ID);
    }

    @Test
    public void testLoadFoundByLoadIdRejectedLTL() throws ApplicationException {
        AcknowledgementJaxbBO acknowledgementBO = createAcknowledgementBO(STATUS_REJECTED, LOAD_ID, null, null);
        when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);
        when(load.getOrganization()).thenReturn(organization);
        when(organization.getNetworkId()).thenReturn(IntegrationService.LTL_NETWORK);
        acknowledgementServiceImpl.processMessage(acknowledgementBO);
        verify(loadTrackingDao, Mockito.times(1)).saveOrUpdate(createLoadTrackingEntity(acknowledgementBO));
        verify(ediEmailSender, Mockito.times(1)).forLTLDistributionList(load, TRANSACTION_SET_ID);
        verify(ediEmailSender, Mockito.times(0)).forCreatedUser(user.getEmail(), load, TRANSACTION_SET_ID);
    }

}
