package com.pls.shipment.service.impl.edi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentUpdateMessage;
import com.pls.ltlrating.integration.ltllifecycle.dto.message.UpdateStatus;
import com.pls.ltlrating.integration.ltllifecycle.dto.request.AddressDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentUpdateMessage.ShipmentUpdate;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadAdditionalFieldsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadTrackingJaxbBO;
import com.pls.shipment.domain.sterling.TrackingStatusAddressJaxbBO;
import com.pls.shipment.domain.sterling.TrackingStatusJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.impl.email.EDIEmailSender;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * This test unit is written to ensure the LoadTrackingService behavior is correct.
 *
 * @author Jasmin Dhamelia
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class LoadTrackingServiceImplTest {

    private static final String SCAC = "RLCA";
    private static final String BOL = "128";
    private static final String BOL_LTLLC = "128-LTLLC";
    private static final String PRO_NUMBER = "123";
    private static final String STATUS_REASON = "Status Reason";
    private static final String CITY = "PITTSBURGH";
    private static final String STATE = "PA";
    private static final String COUNTRY = "US";
    private static final String POSTAL_CODE = "11111";
    private static final String TIME_ZONE = "ET";
    private static final String B2BI_FILE_NAME = "B2bi File";
    private static final String STATUS_IN_TRANSIT = "CP";
    private static final String STATUS_DELIVERED = "D1";
    private static final String STATUS_OUT_FOR_DELIVERY = "X6";
    private static final Date RECEIVED_DATE = new Date();
    private static final Date DEPARTURE_DATE = new Date();
    private static final String ERROR_MESSAGE = "Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# '128' for Carrier 'RLCA'";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @Mock
    private LoadTrackingDao loadTrackingDao;

    @Mock
    private ShipmentEmailSender shipmentEmailSender;

    @Mock
    private ShipmentAlertService shipmentAlertService;

    @Mock
    private EDIEmailSender ediEmailSender;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @InjectMocks
    private LoadTrackingServiceImpl loadTrackingServiceImpl;

    private LoadTrackingEntity savedEntity;

    private BillToEntity billTo;

    private LoadEntity load;

    @Before
    public void initialize() {
        createBillTo();
        createLoadEntity(ShipmentStatus.OPEN, PRO_NUMBER, DEPARTURE_DATE);
        createSavedTrackingEntity();
        when(loadTrackingDao.saveOrUpdate(any(LoadTrackingEntity.class))).thenReturn(savedEntity);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Collections.singletonList(load));
    }

    @Test
    public void testProcessLoadNotFound() throws ApplicationException {
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Collections.emptyList());
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        try {
            loadTrackingServiceImpl.processMessage(loadTrackingBO);
            fail("ShipmentNotFound exception not thrown");
        } catch (ShipmentNotFoundException e) {
            String errorMessage = ERROR_MESSAGE + ". Actually 0 shipments were found.";
            verify(ediEmailSender, times(1)).loadTrackingFailed(loadTrackingBO.getB2biFileName(), errorMessage,
                IntegrationService.EDI_214.toString(), null);
            verify(loadTrackingDao, times(0)).saveOrUpdate(any(LoadTrackingEntity.class));
            verify(ediEmailSender, times(1)).loadTrackingFailed(B2BI_FILE_NAME, errorMessage, IntegrationService.EDI_214.toString(), null);
        }
    }

    @Test
    public void testProcessLoadWithMultipleResults() throws ApplicationException {
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL)).thenReturn(Arrays.asList(load, load));
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        try {
            loadTrackingServiceImpl.processMessage(loadTrackingBO);
            fail("ShipmentNotFound exception not thrown");
        } catch (ShipmentNotFoundException e) {
            String errorMessage = ERROR_MESSAGE + ". Actually 2 shipments were found.";
            verify(ediEmailSender, times(1)).loadTrackingFailed(loadTrackingBO.getB2biFileName(), errorMessage,
                    IntegrationService.EDI_214.toString(), null);
            verify(loadTrackingDao, times(0)).saveOrUpdate(any(LoadTrackingEntity.class));
            verify(ediEmailSender, times(1)).loadTrackingFailed(B2BI_FILE_NAME, errorMessage, IntegrationService.EDI_214.toString(), null);
        }
    }

    @Test
    public void testLoadInDeliveredStatus() throws ApplicationException {
        createLoadEntity(ShipmentStatus.DELIVERED, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.DELIVERED.name(), STATUS_DELIVERED, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.DELIVERED, PRO_NUMBER, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(0)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testLoadWithProAndDeprDateNull() throws ApplicationException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, null, null);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.IN_TRANSIT, null, null);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testLoadWithProIsNullAndDeprDateIsNotNull() throws ApplicationException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, null, DEPARTURE_DATE);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.IN_TRANSIT, null, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testLoadWithProIsNotNullAndDeprDateIsNull() throws ApplicationException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, PRO_NUMBER, null);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.IN_TRANSIT, PRO_NUMBER, null);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testLoadWithProIsNotNullAndDeprDateIsNotNull() throws ApplicationException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(0)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testLoadInOutForDeliveryStatus() throws ApplicationException {
        createLoadEntity(ShipmentStatus.OUT_FOR_DELIVERY, null, null);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.OUT_FOR_DELIVERY.name(), STATUS_OUT_FOR_DELIVERY, null, null);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.OUT_FOR_DELIVERY, PRO_NUMBER, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(0)).saveOrUpdate(load);
        assertLoadStatusNotChanged(load);
    }

    @Test
    public void testDispatchedLoadtoInTransit() throws ApplicationException {
        createLoadEntity(ShipmentStatus.DISPATCHED, null, null);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.IN_TRANSIT.name(), STATUS_IN_TRANSIT, null, null);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.DISPATCHED, PRO_NUMBER, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusChanged(load);
    }

    @Test
    public void testInTransitLoadtoOutForDelivery() throws ApplicationException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.OUT_FOR_DELIVERY.name(), STATUS_OUT_FOR_DELIVERY, null, null);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusChanged(load);
    }

    @Test
    public void testOutForDeliveryLoadToDelivered() throws ApplicationException {
        createLoadEntity(ShipmentStatus.OUT_FOR_DELIVERY, null, null);
        LoadTrackingJaxbBO loadTrackingBO = createLoadTrackingBO(ShipmentStatus.DELIVERED.name(), STATUS_DELIVERED, PRO_NUMBER, DEPARTURE_DATE);
        LoadTrackingEntity loadTrackingEntity = createLoadTrackingEntity(loadTrackingBO, ShipmentStatus.OUT_FOR_DELIVERY, null, null);

        loadTrackingServiceImpl.processMessage(loadTrackingBO);

        assertLoadTrackingUpdated(loadTrackingEntity, load);
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertLoadStatusChanged(load);
    }

    @Test
    public void testSendEdiMessage() throws InternalJmsCommunicationException, JMSException {
        loadTrackingServiceImpl.sendMessage(load);
        verify(sterlingMessageProducer).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testEdiSettingIsNull() throws InternalJmsCommunicationException, JMSException {
        load.getBillTo().setEdiSettings(null);
        loadTrackingServiceImpl.sendMessage(load);
        verify(sterlingMessageProducer, times(0)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testEdiSettingNotIncludes214() throws InternalJmsCommunicationException, JMSException {
        load.getBillTo().getEdiSettings().getEdiType().clear();
        loadTrackingServiceImpl.sendMessage(load);
        verify(sterlingMessageProducer, times(0)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testEdiSettingNotIncludesStatus() throws InternalJmsCommunicationException, JMSException {
        load.getBillTo().getEdiSettings().getEdiStatus().clear();
        loadTrackingServiceImpl.sendMessage(load);
        verify(sterlingMessageProducer, times(0)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testSendEdiInTransitStatus() throws InternalJmsCommunicationException, JMSException {
        createLoadEntity(ShipmentStatus.IN_TRANSIT, PRO_NUMBER, DEPARTURE_DATE);
        loadTrackingServiceImpl.sendMessage(load);
        SterlingIntegrationMessageBO outboundMessage =
                new SterlingIntegrationMessageBO(createOutboundBO(ShipmentStatus.IN_TRANSIT), EDIMessageType.EDI214_STERLING_MESSAGE_TYPE);
        verify(sterlingMessageProducer).publishMessage(outboundMessage);
    }

    @Test
    public void testSendEdiInDeliveredStatus() throws InternalJmsCommunicationException, JMSException {
        createLoadEntity(ShipmentStatus.DELIVERED, PRO_NUMBER, DEPARTURE_DATE);
        loadTrackingServiceImpl.sendMessage(load);
        SterlingIntegrationMessageBO outboundMessage =
                new SterlingIntegrationMessageBO(createOutboundBO(ShipmentStatus.DELIVERED), EDIMessageType.EDI214_STERLING_MESSAGE_TYPE);
        verify(sterlingMessageProducer).publishMessage(outboundMessage);
    }

    private IntegrationMessageBO createOutboundBO(ShipmentStatus status) {
        LoadTrackingJaxbBO loadTrackingBO = new LoadTrackingJaxbBO();
        loadTrackingBO.setCustomerOrgId(1L);
        loadTrackingBO.setProNumber(PRO_NUMBER);
        loadTrackingBO.setMessageType(EDIMessageType.EDI214_STERLING_MESSAGE_TYPE.name());
        loadTrackingBO.setScac(SCAC);
        loadTrackingBO.setPersonId(SecurityUtils.getCurrentPersonId());
        loadTrackingBO.setTrackingStatuses(new ArrayList<TrackingStatusJaxbBO>(1));
        TrackingStatusJaxbBO trackingStatus = new TrackingStatusJaxbBO();
        trackingStatus.setLoadStatus(status.toString());
        trackingStatus.setTransactionDate(DEPARTURE_DATE);
        trackingStatus.setTransactionDateTz(TIME_ZONE);
        loadTrackingBO.getTrackingStatuses().add(trackingStatus);

        setAddresses(loadTrackingBO);
        return loadTrackingBO;
    }

    private void setAddresses(LoadTrackingJaxbBO loadTrackingBO) {
        loadTrackingBO.addAddress(setLoadPoint(AddressType.ORIGIN));
        loadTrackingBO.addAddress(setLoadPoint(AddressType.DESTINATION));
    }

    private AddressJaxbBO setLoadPoint(AddressType addressType) {
        AddressJaxbBO address = new AddressJaxbBO();
        address.setCity(CITY);
        address.setStateCode(STATE);
        address.setPostalCode(POSTAL_CODE);
        address.setCountryCode(COUNTRY);
        return address;
    }

    private void assertLoadStatusNotChanged(LoadEntity load) {
        verify(shipmentEmailSender, times(0)).sendLoadStatusChangedNotification(load, load.getStatus());
        verify(shipmentAlertService, times(0)).processShipmentAlerts(load);
    }

    private void assertLoadStatusChanged(LoadEntity load) {
        verify(shipmentEmailSender, times(1)).sendLoadStatusChangedNotification(load, load.getStatus());
        verify(shipmentAlertService, times(1)).processShipmentAlerts(load);
    }

    private void assertLoadTrackingUpdated(LoadTrackingEntity loadTrackingEntity, LoadEntity load) {
        verify(loadTrackingDao, times(1)).saveOrUpdate(loadTrackingEntity);
        verify(shipmentEmailSender, times(1)).sendLoadDetailsNotification(load, savedEntity.getStatus().getDescription());
    }

    public LoadTrackingJaxbBO createLoadTrackingBO(String loadStatus, String status, String proNumber, Date date) {
        LoadTrackingJaxbBO loadTrackingBO = new LoadTrackingJaxbBO();
        loadTrackingBO.setScac(SCAC);
        loadTrackingBO.setBol(BOL);
        loadTrackingBO.setProNumber(proNumber);
        loadTrackingBO.setB2biFileName(B2BI_FILE_NAME);
        List<TrackingStatusJaxbBO> trackingStatuses = new ArrayList<TrackingStatusJaxbBO>();
        trackingStatuses.add(createTrackingStatus(loadStatus, status, date));
        loadTrackingBO.setTrackingStatuses(trackingStatuses);

        return loadTrackingBO;

    }

    private TrackingStatusJaxbBO createTrackingStatus(String loadStatus, String status, Date date) {
        TrackingStatusJaxbBO trackingStatus = new TrackingStatusJaxbBO();
        trackingStatus.setStatus(status);
        trackingStatus.setLoadStatus(loadStatus);
        trackingStatus.setStatusReason(STATUS_REASON);
        trackingStatus.setTransactionDate(date);
        trackingStatus.setTransactionDateTz(TIME_ZONE);
        TrackingStatusAddressJaxbBO trackingStatusAddress = new TrackingStatusAddressJaxbBO();
        trackingStatusAddress.setCity(CITY);
        trackingStatusAddress.setState(STATE);
        trackingStatusAddress.setPostalCode(POSTAL_CODE);
        trackingStatusAddress.setCountry(COUNTRY);
        trackingStatus.setTrackingStatusAddress(trackingStatusAddress);

        return trackingStatus;

    }

    private LoadTrackingEntity createLoadTrackingEntity(LoadTrackingJaxbBO loadTrackingBO,
            ShipmentStatus shipmentStatus, String proNumber, Date date) {
        createLoadEntity(shipmentStatus, proNumber, date);
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setLoad(load);
        loadTracking.setSource(Long.valueOf(IntegrationService.EDI_214));
        loadTracking.setDepartureTime(loadTrackingBO.getTrackingStatuses().get(0).getTransactionDate());
        loadTracking.setPro(loadTrackingBO.getProNumber());
        loadTracking.setTimezoneCode(loadTrackingBO.getTrackingStatuses().get(0).getTransactionDateTz());
        loadTracking.setStatusCode(loadTrackingBO.getTrackingStatuses().get(0).getStatus());
        loadTracking.setStatusReasonCode(loadTrackingBO.getTrackingStatuses().get(0).getStatusReason());
        loadTracking.setCity(loadTrackingBO.getTrackingStatuses().get(0).getTrackingStatusAddress().getCity());
        loadTracking.setCountry(loadTrackingBO.getTrackingStatuses().get(0).getTrackingStatusAddress().getCountry());
        loadTracking.setPostalCode(loadTrackingBO.getTrackingStatuses().get(0).getTrackingStatusAddress().getPostalCode());
        loadTracking.setState(loadTrackingBO.getTrackingStatuses().get(0).getTrackingStatusAddress().getState());
        loadTracking.setScac(loadTrackingBO.getScac());
        loadTracking.setBol(loadTrackingBO.getBol());
        return loadTracking;
    }

    private void createLoadEntity(ShipmentStatus shipmentStatus, String proNumber, Date date) {
        if (load == null) {
            load = new LoadEntity();
        }
        load.getNumbers().setProNumber(proNumber);
        load.setStatus(shipmentStatus);
        load.setBillTo(billTo);
        setLoadDetails(date);
        setOrganizations();
    }

    private void setOrganizations() {
        CarrierEntity carrier = new CarrierEntity();
        CustomerEntity org = new CustomerEntity();
        carrier.setScac(SCAC);
        org.setId(1L);
        load.setOrganization(org);
        load.setCarrier(carrier);
    }

    private void setLoadDetails(Date date) {
        AddressEntity addressEntity = new AddressEntity();
        CountryEntity country = new CountryEntity();
        country.setId("USA");
        addressEntity.setCountry(country);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        origin.setDeparture(date);
        origin.setAddress(addressEntity);
        destination.setAddress(addressEntity);
        destination.setDeparture(date);

        load.addLoadDetails(origin);
        load.addLoadDetails(destination);
    }

    private void createSavedTrackingEntity() {
        savedEntity = new LoadTrackingEntity();
        savedEntity.setDepartureTime(DEPARTURE_DATE);
        savedEntity.setPro(PRO_NUMBER);
        savedEntity.setTrackingDate(RECEIVED_DATE);
        savedEntity.setPostalCode(POSTAL_CODE);
        LoadTrackingStatusEntity loadTrackingStatusEntity = new LoadTrackingStatusEntity();
        loadTrackingStatusEntity.setDescription(TEST_DESCRIPTION);
        savedEntity.setStatus(loadTrackingStatusEntity);

    }

    private void createBillTo() {
        billTo = new BillToEntity();
        EdiSettingsEntity ediSettings = new EdiSettingsEntity();
        List<ShipmentStatus> ediStatus = new ArrayList<ShipmentStatus>();
        ediStatus.add(ShipmentStatus.OPEN);
        ediStatus.add(ShipmentStatus.IN_TRANSIT);
        ediStatus.add(ShipmentStatus.DELIVERED);
        ediSettings.setEdiStatus(ediStatus);

        List<EdiType> ediTypes = new ArrayList<EdiType>();
        ediTypes.add(EdiType.EDI_214);
        ediSettings.setEdiType(ediTypes);
        billTo.setEdiSettings(ediSettings);
    }
    
    @Test
    public void testProcessLtllcTrackingMessage() throws ApplicationException {
        LoadEntity load = createLtllcLoad(SCAC, BOL_LTLLC, ShipmentStatus.DISPATCHED);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL_LTLLC)).thenReturn(Collections.singletonList(load));
        
        AddressDTO address = new AddressDTO();
        address.setAddress1("add1");
        address.setCity("city");
        address.setCountry("US");
        address.setState("State");
        address.setZip("zip");
        
        ShipmentUpdateMessage message = new ShipmentUpdateMessage();
        message.setBolNumber(BOL_LTLLC);
        message.setCarrierCode(SCAC);
        message.setLoadUUID("load-uuid");
        message.setProNumber("PRO-LTLLC");
        message.setPuNumber("PU-LTLLC");
        message.setLastReportedPickupDateTime(ZonedDateTime.parse("2020-03-18T11:02:00+00:00"));
        message.setLastReportedEstimatedDeliveryDate(ZonedDateTime.parse("2020-03-19T11:02:00+00:00"));
        List<ShipmentUpdate> shipmentUpdates = new ArrayList<>();
        ShipmentUpdate update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.IN_TRANSIT);
        update.setStatus(UpdateStatus.IN_TRANSIT);
        update.setAddress(address );
        update.setMessageReceivedTime(LocalDateTime.parse("2020-03-18T11:05:00"));
        update.setTransactionDate(ZonedDateTime.parse("2020-03-18T11:05:00+00:00"));
        update.setNotes("some notes");
        shipmentUpdates.add(update);
        message.setShipmentUpdates(shipmentUpdates);
        
        // call
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        // check correct parsing
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        assertEquals("PRO-LTLLC", load.getNumbers().getProNumber());
        assertEquals("PU-LTLLC", load.getNumbers().getPuNumber());
        assertEquals(Date.from(ZonedDateTime.parse("2020-03-18T11:02:00+00:00").toInstant()), load.getOrigin().getDeparture());
        assertEquals(ShipmentStatus.IN_TRANSIT, load.getStatus());
        ArgumentCaptor<LoadTrackingEntity> trackEntityCaptor = ArgumentCaptor.forClass(LoadTrackingEntity.class);
        verify(loadTrackingDao, times(1)).saveOrUpdate(trackEntityCaptor.capture());
        LoadTrackingEntity savedTrackingEntity = trackEntityCaptor.getValue();
        assertEquals("IN_TRANSIT", savedTrackingEntity.getStatusCode());
        assertEquals(SCAC, savedTrackingEntity.getScac());
        assertEquals(SCAC, savedTrackingEntity.getCarrier().getScac());
        assertEquals(BOL_LTLLC, savedTrackingEntity.getBol());
        assertEquals("PRO-LTLLC", savedTrackingEntity.getPro());
        assertEquals(Date.from(ZonedDateTime.parse("2020-03-18T11:05:00+00:00").toInstant()), savedTrackingEntity.getTrackingDate());
        assertEquals(Long.valueOf(44L), savedTrackingEntity.getSource());
        assertEquals("some notes", savedTrackingEntity.getFreeMessage());
        assertEquals("city", savedTrackingEntity.getCity());
        assertEquals("State", savedTrackingEntity.getState());
        assertEquals("US", savedTrackingEntity.getCountry());
        assertEquals("zip", savedTrackingEntity.getPostalCode());
        verify(shipmentEmailSender, times(1)).sendLoadDetailsNotification(load, savedEntity.getStatus().getDescription());
        verify(shipmentEmailSender, times(1)).sendLoadStatusChangedNotification(load, ShipmentStatus.IN_TRANSIT);
        verify(shipmentEmailSender, times(1)).sendGoShipTrackingUpdateEmail(load, ShipmentStatus.DISPATCHED);
        verify(shipmentAlertService, times(1)).processShipmentAlerts(load);
        
        //
        // check status transitions
        //
        
        // still IN_TRANSIT
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        assertEquals(ShipmentStatus.IN_TRANSIT, load.getStatus());
        verify(ltlShipmentDao, times(1)).saveOrUpdate(load);
        verify(loadTrackingDao, times(2)).saveOrUpdate(trackEntityCaptor.capture());
        savedTrackingEntity = trackEntityCaptor.getValue();
        assertEquals("IN_TRANSIT", savedTrackingEntity.getStatusCode());
        verify(shipmentEmailSender, times(2)).sendLoadDetailsNotification(load, savedEntity.getStatus().getDescription());
        verify(shipmentEmailSender, times(1)).sendLoadStatusChangedNotification(load, ShipmentStatus.IN_TRANSIT);
        verify(shipmentEmailSender, times(1)).sendGoShipTrackingUpdateEmail(any(LoadEntity.class), any(ShipmentStatus.class));
        verify(shipmentAlertService, times(1)).processShipmentAlerts(load);
        
        // OUT_FOR_DELIVERED
        update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.OUT_FOR_DELIVERY);
        update.setStatus(UpdateStatus.OUT_FOR_DELIVERY);
        shipmentUpdates.add(update); // add to the top of existing in_transit update
        
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        assertEquals(ShipmentStatus.OUT_FOR_DELIVERY, load.getStatus());
        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(loadTrackingDao, times(4)).saveOrUpdate(trackEntityCaptor.capture()); // note, we sent 2 messages this time, so this needs to be called +2 times
        savedTrackingEntity = trackEntityCaptor.getValue();
        assertEquals("OUT_FOR_DELIVERY", savedTrackingEntity.getStatusCode());
        verify(shipmentEmailSender, times(4)).sendLoadDetailsNotification(load, savedEntity.getStatus().getDescription());// note, we sent 2 messages this time, so this needs to be called +2 times
        verify(shipmentEmailSender, times(1)).sendLoadStatusChangedNotification(load, ShipmentStatus.OUT_FOR_DELIVERY);
        verify(shipmentEmailSender, times(1)).sendGoShipTrackingUpdateEmail(load, ShipmentStatus.IN_TRANSIT);
        verify(shipmentAlertService, times(2)).processShipmentAlerts(load);
        
        // DISPATCHED
        shipmentUpdates = new ArrayList<>(); // want to send only 1 update now
        update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.DELIVERED);
        update.setStatus(UpdateStatus.DELIVERED);
        shipmentUpdates.add(update); 
        message.setShipmentUpdates(shipmentUpdates);
        message.setLastReportedDeliveryDateTime(ZonedDateTime.parse("2020-03-20T11:02:00+00:00"));
        
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        assertEquals(ShipmentStatus.DELIVERED, load.getStatus());
        assertEquals(Date.from(ZonedDateTime.parse("2020-03-20T11:02:00+00:00").toInstant()), load.getDestination().getDeparture());
        verify(ltlShipmentDao, times(3)).saveOrUpdate(load);
        verify(loadTrackingDao, times(5)).saveOrUpdate(trackEntityCaptor.capture()); 
        savedTrackingEntity = trackEntityCaptor.getValue();
        assertEquals("DELIVERED", savedTrackingEntity.getStatusCode());
        verify(shipmentEmailSender, times(5)).sendLoadDetailsNotification(load, savedEntity.getStatus().getDescription());
        verify(shipmentEmailSender, times(1)).sendLoadStatusChangedNotification(load, ShipmentStatus.DELIVERED);
        verify(shipmentEmailSender, times(1)).sendGoShipTrackingUpdateEmail(load, ShipmentStatus.OUT_FOR_DELIVERY);
        verify(shipmentAlertService, times(3)).processShipmentAlerts(load);
    }
    
    @Test
    public void testProcessLtllcTrackingMessage_minimalInfo() throws ApplicationException {
        LoadEntity load = createLtllcLoad(SCAC, BOL_LTLLC, ShipmentStatus.DISPATCHED);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL_LTLLC)).thenReturn(Collections.singletonList(load));
        
        ShipmentUpdateMessage message = new ShipmentUpdateMessage();
        message.setBolNumber(BOL_LTLLC);
        message.setCarrierCode(SCAC);
        message.setLoadUUID("load-uuid");
        List<ShipmentUpdate> shipmentUpdates = new ArrayList<>();
        ShipmentUpdate update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.IN_TRANSIT);
        update.setStatus(UpdateStatus.IN_TRANSIT);
        shipmentUpdates.add(update);
        message.setShipmentUpdates(shipmentUpdates);
        
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        // should not generate exception
    }
    
    @Test
    public void testProcessLtllcTrackingMessage_dontOverrideValues() throws ApplicationException {
        LoadEntity load = createLtllcLoad(SCAC, BOL_LTLLC, ShipmentStatus.DISPATCHED);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL_LTLLC)).thenReturn(Collections.singletonList(load));
        load.getNumbers().setProNumber("existing-pro");
        load.getNumbers().setPuNumber("existing-pu");
        load.getOrigin().setDeparture(new Date(10001));
        load.getDestination().setDeparture(new Date(10002));

        
        ShipmentUpdateMessage message = new ShipmentUpdateMessage();
        message.setBolNumber(BOL_LTLLC);
        message.setCarrierCode(SCAC);
        message.setLoadUUID("load-uuid");
        message.setProNumber("PRO-LTLLC");
        message.setPuNumber("PU-LTLLC");
        message.setLastReportedPickupDateTime(ZonedDateTime.parse("2020-03-18T11:02:00+00:00"));
        message.setLastReportedEstimatedDeliveryDate(ZonedDateTime.parse("2020-03-19T11:02:00+00:00"));
        message.setLastReportedDeliveryDateTime(ZonedDateTime.parse("2020-03-20T11:02:00+00:00"));
        List<ShipmentUpdate> shipmentUpdates = new ArrayList<>();
        ShipmentUpdate update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.IN_TRANSIT);
        update.setStatus(UpdateStatus.IN_TRANSIT);
        update.setMessageReceivedTime(LocalDateTime.parse("2020-03-18T11:05:00"));
        update.setTransactionDate(ZonedDateTime.parse("2020-03-18T11:05:00+00:00"));
        update.setNotes("some notes");
        shipmentUpdates.add(update);
        message.setShipmentUpdates(shipmentUpdates);
        
        // call
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        // verify we are not updating existing values
        assertEquals(BOL_LTLLC, load.getNumbers().getBolNumber());
        assertEquals("existing-pro", load.getNumbers().getProNumber());
        assertEquals("existing-pu", load.getNumbers().getPuNumber());
        assertEquals(new Date(10001), load.getOrigin().getDeparture());
        assertEquals(new Date(10002), load.getDestination().getDeparture());
    }

    @Test
    public void testProcessLtllcTrackingMessage_notTracked() throws ApplicationException {
        LoadEntity load = createLtllcLoad(SCAC, BOL_LTLLC, ShipmentStatus.DISPATCHED);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL_LTLLC)).thenReturn(Collections.singletonList(load));
        load.getLoadAdditionalFields().setTrackedVia(null);
        
        ShipmentUpdateMessage message = new ShipmentUpdateMessage();
        message.setBolNumber(BOL_LTLLC);
        message.setCarrierCode(SCAC);
        message.setLoadUUID("load-uuid");
        List<ShipmentUpdate> shipmentUpdates = new ArrayList<>();
        ShipmentUpdate update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.IN_TRANSIT);
        update.setStatus(UpdateStatus.IN_TRANSIT);
        shipmentUpdates.add(update);
        message.setShipmentUpdates(shipmentUpdates);
        
        // call
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        verify(ltlShipmentDao, times(0)).saveOrUpdate(load);
        verify(loadTrackingDao, times(0)).saveOrUpdate(any(LoadTrackingEntity.class)); 
        verify(shipmentEmailSender, times(0)).sendLoadDetailsNotification(any(LoadEntity.class), any(String.class));
        verify(shipmentEmailSender, times(0)).sendLoadStatusChangedNotification(any(LoadEntity.class), any(ShipmentStatus.class));
        verify(shipmentEmailSender, times(0)).sendGoShipTrackingUpdateEmail(any(LoadEntity.class), any(ShipmentStatus.class));
        verify(shipmentAlertService, times(0)).processShipmentAlerts(any(LoadEntity.class));
    }
    
    @Test(expected = ShipmentNotFoundException.class)
    public void testProcessLtllcTrackingMessage_notFound() throws ApplicationException {
        LoadEntity load = createLtllcLoad(SCAC, BOL_LTLLC, ShipmentStatus.DISPATCHED);
        when(ltlShipmentDao.findShipmentsByScacAndBolNumber(SCAC, BOL_LTLLC)).thenReturn(Collections.singletonList(load));
        load.getLoadAdditionalFields().setTrackedVia(null);
        
        ShipmentUpdateMessage message = new ShipmentUpdateMessage();
        message.setBolNumber("unknown bol");
        message.setCarrierCode(SCAC);
        message.setLoadUUID("load-uuid");
        List<ShipmentUpdate> shipmentUpdates = new ArrayList<>();
        ShipmentUpdate update = new ShipmentUpdate();
        update.setLoadStatus(com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentStatus.IN_TRANSIT);
        update.setStatus(UpdateStatus.IN_TRANSIT);
        shipmentUpdates.add(update);
        message.setShipmentUpdates(shipmentUpdates);
        
        // call
        loadTrackingServiceImpl.processLtllcTrackingMessage(message);
        
        verify(ltlShipmentDao, times(0)).saveOrUpdate(load);
        verify(loadTrackingDao, times(0)).saveOrUpdate(any(LoadTrackingEntity.class)); 
        verify(shipmentEmailSender, times(0)).sendLoadDetailsNotification(any(LoadEntity.class), any(String.class));
        verify(shipmentEmailSender, times(0)).sendLoadStatusChangedNotification(any(LoadEntity.class), any(ShipmentStatus.class));
        verify(shipmentEmailSender, times(0)).sendGoShipTrackingUpdateEmail(any(LoadEntity.class), any(ShipmentStatus.class));
        verify(shipmentAlertService, times(0)).processShipmentAlerts(any(LoadEntity.class));
    }
    
    private LoadEntity createLtllcLoad(String scac, String bol, ShipmentStatus shipmentStatus) {
        LoadEntity load = new LoadEntity();
        load.setBOL(bol);
        load.getNumbers().setBolNumber(bol);
        CarrierEntity carrier = new CarrierEntity();
        carrier.setScac(scac);
        load.setCarrier(carrier);
        load.setLoadAdditionalFields(new LoadAdditionalFieldsEntity());
        load.getLoadAdditionalFields().setTrackedVia("LTLLC");
        load.setStatus(shipmentStatus);
        
        AddressEntity addressEntity = new AddressEntity();
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        origin.setAddress(addressEntity);
        destination.setAddress(addressEntity);

        load.addLoadDetails(origin);
        load.addLoadDetails(destination);
        
        return load;
    }
}
