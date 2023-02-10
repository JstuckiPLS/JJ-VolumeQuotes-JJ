package com.pls.shipment.service.impl.edi;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BillToDao;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadNumbersEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.sterling.LoadAcknowledgementJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * This test unit is written to ensure the LoadTenderAckService works the way desired. The test is developed fore cases including empty load, empty
 * message, load accepted, load rejected, load rejected in LTL network and load rejected in non LTL network.
 *
 * @author Yasaman Honarvar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadTenderAckServiceImplTest {

    @Mock
    private CarrierDao carrierDao;

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @Mock
    private LoadTrackingDao loadTrackingDao;

    @Mock
    private EDIEmailSender ediEmailSender;

    // Data Mocked
    @Mock
    private LoadEntity load;

    @Mock
    private CustomerEntity organization;

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
    private CarrierEntity carrier;

    @Mock
    private UserEntity createdUser;

    @Mock
    private BillToDao billToDao;

    @Mock
    private OrganizationLocationDao locationDao;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @InjectMocks
    private LoadTenderAckServiceImpl testee;

    private static final Long BILL_TO_ID = 2L;
    private static final Long LOCATION_ID = 3L;
    private static final String SCAC = "RCLA";
    private static final String BOL = "123";
    private static final String PRO = "345";
    private static final Long CREATOR_ID = 4L;
    private static final Long CARRIER_ID = 1L;
    private static final String STATUS_NOTES = "Test Note";
    private static final Date RECV_DATE = new Date();
    private BillToEntity billTo = null;
    private LoadNumbersEntity loadNumbers = null;
    private OrganizationLocationEntity locationEntity = null;
    private LoadMessageJaxbBO loadTender = null;
    private LoadAcknowledgementJaxbBO loadAcknowledgementBO = null;
    private SterlingIntegrationMessageBO sterlingMessage = null;

    @Before
    public void initialize() {
        createLoadTender();
        createLoadNumbersEntity();
        createBillToEntity();
        createLocationEntity();
        createLoadAcknowledgementBO();
        createOutboundMessageVO(loadAcknowledgementBO);

        when(billToDao.find(BILL_TO_ID)).thenReturn(billTo);
        when(locationDao.find(LOCATION_ID)).thenReturn(locationEntity);
        when(load.getNumbers()).thenReturn(loadNumbers);
        when(load.getBillTo()).thenReturn(billTo);
        when(load.getId()).thenReturn(0L);

        when(carrierDao.findByScac(SCAC)).thenReturn(carrier);
        when(carrier.getId()).thenReturn(CARRIER_ID);
        when(load.getLocation()).thenReturn(location);
        when(location.getActiveAccountExecutive()).thenReturn(activeAccountExecutive);
        when(load.getModification()).thenReturn(modification);
        when(status.getDescription()).thenReturn("Test Description");
        when(activeAccountExecutive.getUser()).thenReturn(user);
        when(modification.getCreatedUser()).thenReturn(createdUser);
        when(createdUser.getEmail()).thenReturn("test@testemails.com");
        when(carrierDao.findByScac(SCAC)).thenReturn(carrier);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Collections.singletonList(load));
    }

    private void createOutboundMessageVO(LoadAcknowledgementJaxbBO ldAcknowledgement) {
        ldAcknowledgement.setStatus("A");
        ldAcknowledgement.setStatusNotes("EDI 990 created : Accepted");
        sterlingMessage = new SterlingIntegrationMessageBO(ldAcknowledgement, EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);

    }

    private void createLoadAcknowledgementBO() {
        loadAcknowledgementBO = new LoadAcknowledgementJaxbBO();
        loadAcknowledgementBO.setBol(BOL);
        loadAcknowledgementBO.setProNumber(PRO);
        loadAcknowledgementBO.setLoadId(0L);
        loadAcknowledgementBO.setPersonId(SecurityUtils.getCurrentPersonId());
        loadAcknowledgementBO.setRecvDateTime(new Date());
    }

    private void createLocationEntity() {
        locationEntity = new OrganizationLocationEntity();
        locationEntity.setBillTo(billTo);
    }

    private void createLoadNumbersEntity() {
        loadNumbers = new LoadNumbersEntity();
        loadNumbers.setProNumber(PRO);
        loadNumbers.setBolNumber(BOL);
    }

    private void createBillToEntity() {
        billTo = new BillToEntity();
        EdiSettingsEntity ediSettings = new EdiSettingsEntity();
        List<EdiType> ediTypes = new ArrayList<EdiType>();
        ediTypes.add(EdiType.EDI_990);
        ediSettings.setEdiType(ediTypes);
        billTo.setEdiSettings(ediSettings);
    }

    private LoadMessageJaxbBO createLoadTender() {
        loadTender = new LoadMessageJaxbBO();
        loadTender.setCustomerBillToId(BILL_TO_ID);
        loadTender.setCustomerLocationId(LOCATION_ID);
        return loadTender;
    }

    private LoadAcknowledgementJaxbBO createLoadAcknowledgment() {
        LoadAcknowledgementJaxbBO loadEmpty = new LoadAcknowledgementJaxbBO();
        loadEmpty.setB2biFileName("2342353455.edi");

        return loadEmpty;
    }

    private LoadAcknowledgementJaxbBO createAcceptedLoadAcknowledgment() {
        LoadAcknowledgementJaxbBO accepted = new LoadAcknowledgementJaxbBO();
        accepted.setB2biFileName("2342353455.edi");
        accepted.setPersonId(CREATOR_ID);
        accepted.setScac(SCAC);
        accepted.setBol(BOL);
        accepted.setStatus("A");
        accepted.setRecvDateTime(RECV_DATE);
        accepted.setStatusNotes(STATUS_NOTES);

        return accepted;
    }

    private LoadTrackingEntity createLoadTrackingEntity(LoadAcknowledgementJaxbBO acknowledgement) {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setTrackingDate(acknowledgement.getRecvDateTime());
        loadTracking.setTimezoneCode("ET");
        loadTracking.setSource(IntegrationService.EDI_990);
        loadTracking.setScac(acknowledgement.getScac());
        loadTracking.setBol(acknowledgement.getBol());
        loadTracking.setStatusCode(acknowledgement.getStatus());
        loadTracking.setStatusReasonCode(acknowledgement.getDeclineReasonCd());
        loadTracking.setFreeMessage(acknowledgement.getStatus() + ":" + STATUS_NOTES + " " + "Out of Service");

        return loadTracking;
    }

    private LoadAcknowledgementJaxbBO createRejectedLoadAcknowledgment() {
        LoadAcknowledgementJaxbBO rejected = new LoadAcknowledgementJaxbBO();
        rejected.setPersonId(CREATOR_ID);
        rejected.setScac(SCAC);
        rejected.setBol(BOL);
        rejected.setStatus("D");
        rejected.setDeclineReasonCd("CC");
        rejected.setDeclineReasonDesc("Out of Service");
        rejected.setRecvDateTime(RECV_DATE);
        rejected.setStatusNotes(STATUS_NOTES);
        return rejected;
    }

    private SterlingIntegrationMessageBO createSterlingMessageWithNullLoad() {
        loadAcknowledgementBO = new LoadAcknowledgementJaxbBO();
        loadAcknowledgementBO.setPersonId(SecurityUtils.getCurrentPersonId());
        loadAcknowledgementBO.setRecvDateTime(new Date());
        loadAcknowledgementBO.setStatus("E");
        loadAcknowledgementBO.setStatusNotes("EDI 990 created : Rejected");
        return new SterlingIntegrationMessageBO(loadAcknowledgementBO, EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);
    }

    @Test
    public void testAcceptedTender() throws ApplicationException {
        LoadAcknowledgementJaxbBO loadAccepted = createAcceptedLoadAcknowledgment();
        testee.processMessage(loadAccepted);
        LoadTrackingEntity entity = createLoadTrackingEntity(loadAccepted);
        entity.setFreeMessage(loadAccepted.getStatus() + ":" + STATUS_NOTES);
        verify(loadTrackingDao).saveOrUpdate(entity);
    }

    @Test
    public void testRejectedTenderLoadLTL() throws ApplicationException {
        LoadAcknowledgementJaxbBO loadRejected = createRejectedLoadAcknowledgment();
        when(load.getOrganization()).thenReturn(organization);
        when(organization.getNetworkId()).thenReturn(7L);
        testee.processMessage(createRejectedLoadAcknowledgment());
        verify(ediEmailSender).forLTLDistributionList(load, loadRejected.getStatus() + ":" + STATUS_NOTES + " " + "Out of Service");
        verify(loadTrackingDao).saveOrUpdate(createLoadTrackingEntity(loadRejected));
    }

    @Test
    public void testRejectedTenderLoadnotLTL() throws ApplicationException {
        LoadAcknowledgementJaxbBO loadRejected = createRejectedLoadAcknowledgment();
        when(load.getOrganization()).thenReturn(organization);
        when(organization.getNetworkId()).thenReturn(6L);
        testee.processMessage(loadRejected);
        verify(ediEmailSender).forCreatedUser(load.getModification().getCreatedUser().getEmail(), load,
                loadRejected.getStatus() + ":" + STATUS_NOTES + " " + "Out of Service");
        verify(loadTrackingDao).saveOrUpdate(createLoadTrackingEntity(loadRejected));
    }

    @Test
    public void testLoadNotFound() throws ApplicationException {
        LoadAcknowledgementJaxbBO noLoadFound = createAcceptedLoadAcknowledgment();
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Collections.emptyList());
        try {
            testee.processMessage(noLoadFound);
            fail("ShipmentNotFound exception not thrown");
        } catch (ShipmentNotFoundException e) {
            String errorMsg = "Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# '123' for Carrier 'RCLA'."
                    + " Actually 0 shipments were found.";
            verify(ediEmailSender).loadTrackingFailed(noLoadFound.getB2biFileName(), errorMsg, IntegrationService.EDI_990.toString(), null);
        }
    }

    @Test
    public void testHandlerEmptyInput() throws ApplicationException {
        LoadAcknowledgementJaxbBO loadEmpty = createLoadAcknowledgment();
        try {
            testee.processMessage(loadEmpty);
            fail("ShipmentNotFound exception not thrown");
        } catch (ShipmentNotFoundException e) {
            String errorMsg = "Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# 'null' for Carrier 'null'."
                    + " Actually 0 shipments were found.";
            verify(ediEmailSender).loadTrackingFailed(loadEmpty.getB2biFileName(), errorMsg, IntegrationService.EDI_990.toString(), null);
            verify(ltlShipmentDao).findShipmentsByScacAndBolNumber(eq(loadEmpty.getScac()), eq(loadEmpty.getBol()));
        }
    }

    // Tests the general functionality of the service when an ACCEPTED 990 is sent to the customer
    @Test
    public void testSendEdiAccept() throws InternalJmsCommunicationException, JMSException {
        testee.sendMessage(load, loadTender, true);

        verify(billToDao, times(0)).find(BILL_TO_ID);
        verify(locationDao, times(0)).find(LOCATION_ID);
        verify(loadTrackingDao, times(1)).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    // Tests the case of sending a 990 reject to the customer
    @Test
    public void testSendEdiReject() throws JMSException, InternalJmsCommunicationException {
        loadAcknowledgementBO.setStatus("E");
        loadAcknowledgementBO.setStatusNotes("EDI 990 created : Rejected");
        sterlingMessage = new SterlingIntegrationMessageBO(loadAcknowledgementBO, EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);

        testee.sendMessage(load, loadTender, false);

        verify(billToDao, times(0)).find(BILL_TO_ID);
        verify(locationDao, times(0)).find(LOCATION_ID);
        verify(loadTrackingDao, times(1)).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    // Tests the case where we don't receive a load entity and out BillToId is null
    @Test
    public void testSendIfLoadAndBillToIdAreNull() throws InternalJmsCommunicationException, JMSException {
        loadTender.setCustomerBillToId(null);
        testee.sendMessage(null, loadTender, true);
        verify(billToDao, times(0)).find(BILL_TO_ID);
        verify(locationDao, times(1)).find(LOCATION_ID);
        verify(loadTrackingDao, times(0)).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    // Tests the case where no load entity is received but the bill to Id exists
    @Test
    public void testSendIfLoadIsNullAndBillToIdExist() throws InternalJmsCommunicationException, JMSException {
        loadTender.setCustomerBillToId(BILL_TO_ID);
        testee.sendMessage(null, loadTender, false);
        verify(billToDao, times(1)).find(BILL_TO_ID);
        verify(locationDao, times(0)).find(LOCATION_ID);
        verify(loadTrackingDao, times(0)).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(sterlingMessageProducer, times(1)).publishMessage(createSterlingMessageWithNullLoad());
    }

    // Tests the case where the user doesn't have any EDI setting for 990
    @Test
    public void testShouldNotSendEdi() throws InternalJmsCommunicationException, JMSException {
        billTo.getEdiSettings().getEdiType().clear();
        load.setBillTo(billTo);
        testee.sendMessage(load, loadTender, true);
        verify(billToDao, times(0)).find(BILL_TO_ID);
        verify(locationDao, times(0)).find(LOCATION_ID);
        verify(sterlingMessageProducer, times(0)).publishMessage(sterlingMessage);
        verify(loadTrackingDao, times(0)).saveOrUpdate((LoadTrackingEntity) anyObject());
    }

}
