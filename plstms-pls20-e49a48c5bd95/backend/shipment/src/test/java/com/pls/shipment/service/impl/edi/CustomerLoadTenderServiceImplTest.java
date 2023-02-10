package com.pls.shipment.service.impl.edi;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.IdentifiersUnavailableException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.TransitDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.OperationType;
import com.pls.shipment.service.LoadTenderService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.edi.LoadTenderAckService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * This class tests different scenarios in CustomerLoadTenderService.
 * @author Yasaman Honarvar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerLoadTenderServiceImplTest {

    @Mock
    private ShipmentSavingService shipmentSavingService;

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @Mock
    private LoadTrackingDao loadTrackingDao;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private LoadBuilderService loadBuilder;

    @Mock
    private ShipmentEventDao shipmentEventDao;

    @Mock
    private ShipmentNoteDao noteDao;

    @Mock
    private EDIEmailSender ediEmailSender;

    @Mock
    private AddressService addressService;

    @Mock
    private ContactInfoService contactInfoService;

    @Mock
    private LoadTenderService loadTenderService;

    @Mock
    private DBUtilityService dbUtilityService;

    @Mock
    @Qualifier("loadTenderAckService")
    private LoadTenderAckService loadTenderAckService;

    // Data Mock
    @Mock
    private LoadDetailsEntity originLoadDetails;

    @Mock
    private LoadDetailsEntity destLoadDetails;

    @Mock
    private OrganizationLocationEntity location;

    @Mock
    private AccountExecutiveEntity activeAccountExecutive;

    @Mock
    private BillToDao billToDao;

    @Mock
    private OrganizationLocationDao locationDao;

    @InjectMocks
    private CustomerLoadTenderServiceImpl sut;

    private static final Long ORG_ID = 1L;
    private static final String SHIPMENT_NO = "222";
    private static final Long LOAD_ID = 2L;
    private static final String ORIGIN_ADDRESS_CODE = "OriginAddressCode";
    private static final String DEST_ADDRESS_CODE = "DestAddressCode";
    private static final String EMAIL_ADDRESS = "test@test.edu";
    private static final Long  BILL_TO_ID = 5L;
    private static final Long LOCATION_ID = 6L;
    private static final Date RECV_DATE = new Date();
    private static final String BOL = "1111";

    private Calendar calendar;
    private UserAddressBookEntity userAddress;
    private LoadMessageJaxbBO loadTender;
    private LoadEntity loadEntity;
    private CarrierEntity carrier;
    private BillToEntity billTo;

    @Before
    public void initialize() throws ApplicationException {
        calendar = new GregorianCalendar();
        calendar.set(2015, 5, 23);
        userAddress = new UserAddressBookEntity();

        setupLoadTender();
        createLoadEntity();
        createBillTo();
        setMocks();
    }

    @Test
    public void testCreateLoadFromEDI() throws ApplicationException {
        when(ltlShipmentDao.findShipmentByShipmentNumber(ORG_ID, SHIPMENT_NO)).thenReturn(null);
        sut.processMessage(loadTender);
        verify(loadBuilder).createOrModifyLoad(loadTender, null, userAddress, userAddress);
        verify(loadTenderAckService).sendMessage(loadEntity, loadTender, true);
        verify(ltlShipmentDao, times(0)).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(loadBuilder, times(0)).isMaterialsUpdated(loadEntity, loadTender);
        verify(noteDao).saveOrUpdate((ShipmentNoteEntity) anyObject());
        verifyCommonScenarios();
        verifyAddressServiceCalls();
    }

    @Test
    public void testUpdateLoadFromEDI() throws ApplicationException {

        sut.processMessage(loadTender);
        verify(loadTenderAckService).sendMessage(loadEntity, loadTender, true);
        verify(loadBuilder).createOrModifyLoad(loadTender, loadEntity, userAddress, userAddress);
        verify(ltlShipmentDao, times(0)).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(loadBuilder, times(0)).isMaterialsUpdated(loadEntity, loadTender);
        verify(noteDao).saveOrUpdate((ShipmentNoteEntity) anyObject());
        verifyCommonScenarios();
        verifyAddressServiceCalls();
    }

    @Test
    public void testCancelShipment() throws ApplicationException {
        loadTender.setOperationType(OperationType.CANCEL);
        sut.processMessage(loadTender);
        verify(ltlShipmentDao, times(0)).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(loadTrackingDao).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(shipmentService).cancelShipment(LOAD_ID);
    }

    @Test(expected = ShipmentNotFoundException.class)
    public void testCancelNonExistingLoad() throws ApplicationException {
        loadTender.setOperationType(OperationType.CANCEL);
        when(ltlShipmentDao.findShipmentByShipmentNumber(ORG_ID, SHIPMENT_NO)).thenReturn(null);
        sut.processMessage(loadTender);
        verify(ltlShipmentDao, times(0)).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(shipmentService, times(0)).cancelShipment(anyLong());
    }

    @Test
    public void testCancelACancelledLoad() throws ApplicationException {
        loadTender.setOperationType(OperationType.CANCEL);
        loadEntity.setStatus(ShipmentStatus.CANCELLED);
        sut.processMessage(loadTender);
        verify(ltlShipmentDao, times(0)).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(loadTrackingDao).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(shipmentService, times(0)).cancelShipment(LOAD_ID);
    }

    @Test
    public void testUpdateLoadIOChanged() throws ApplicationException {
        loadEntity.setStatus(ShipmentStatus.BOOKED);
        loadTender.setInboundOutbound("O");
        sut.processMessage(loadTender);
        loadEntity.setCarrier(null);
        verifyCommonScenarios();
        verifyAddressServiceCalls();
        verifyEmailSender(false, false);
    }

    @Test
    public void testUpdateLoadMaterialChanged() throws ApplicationException {
        loadEntity.setStatus(ShipmentStatus.BOOKED);
        when(loadBuilder.isMaterialsUpdated(loadEntity, loadTender)).thenReturn(true);
        sut.processMessage(loadTender);
        loadEntity.setCarrier(null);
        verifyCommonScenarios();
        verifyAddressServiceCalls();
        verifyEmailSender(false, false);
    }

    @Test
    public void testUpdateLoadPickupDateChanged() throws ApplicationException {
        loadEntity.setStatus(ShipmentStatus.BOOKED);
        loadEntity.getOrigin().setEarlyScheduledArrival(new Date());
        sut.processMessage(loadTender);
        loadEntity.setCarrier(null);
        verifyCommonScenarios();
        verifyAddressServiceCalls();
        verifyEmailSender(false, false);
    }

    @Test
    public void testUpdateZipChanged() throws ApplicationException {
        loadTender.getAddress(AddressType.ORIGIN).setAddressCode("ModifiedAddressCode");
        loadTender.getAddress(AddressType.ORIGIN).setPostalCode("newPostalCode");
        sut.processMessage(loadTender);
        loadEntity.setCarrier(null);
        verify(loadTrackingDao).saveOrUpdate((LoadTrackingEntity) anyObject());
        verify(addressService).getCustomerAddressByCode(ORG_ID, "ModifiedAddressCode");
        verify(addressService).getCustomerAddressByCode(ORG_ID, DEST_ADDRESS_CODE);
        verify(shipmentSavingService).save(loadEntity, null, SecurityUtils.getCurrentPersonId(), SecurityUtils.getCurrentPersonId());
        verify(shipmentEventDao, times(0)).saveOrUpdate((LoadEventEntity) anyObject());
        verifyCommonScenarios();
        verifyEmailSender(false, false);
    }

    @Test
    public void testAddressNotExist() throws ApplicationException {
        when(addressService.getCustomerAddressByCode(ORG_ID, ORIGIN_ADDRESS_CODE)).thenReturn(null);
        sut.processMessage(loadTender);
        verify(addressService).saveOrUpdate((UserAddressBookEntity) anyObject(), anyLong(), anyLong(), eq(false));
        verifyCommonScenarios();
        verifyEmailSender(false, false);
    }

    @Test
    public void testLoadNotUpdatable() throws ApplicationException {
        loadEntity.setStatus(ShipmentStatus.DELIVERED);
        sut.processMessage(loadTender);
        verify(shipmentEventDao).saveOrUpdate((LoadEventEntity) anyObject());
        verify(loadBuilder, times(0)).createOrModifyLoad((LoadMessageJaxbBO) anyObject(), (LoadEntity) anyObject(),
                (UserAddressBookEntity) anyObject(), (UserAddressBookEntity) anyObject());
        verify(noteDao, times(0)).saveOrUpdate((ShipmentNoteEntity) anyObject());
        verify(shipmentSavingService, times(0)).save((LoadEntity) anyObject(), (ShipmentProposalBO) anyObject(), anyLong(), anyLong());
        verify(loadTenderAckService).sendMessage(loadEntity, loadTender, false);
        verifyEmailSender(true, false);
        verifyAddressServiceCalls();

    }

    @Test
    public void testNotesNotFound() throws ApplicationException {
        when(loadBuilder.buildNotes((LoadEntity) anyObject(), (LoadMessageJaxbBO) anyObject())).thenReturn(null);
        sut.processMessage(loadTender);
        verify(noteDao, times(0)).saveOrUpdate((ShipmentNoteEntity) anyObject());
        verifyCommonScenarios();
        verifyAddressServiceCalls();
        verifyEmailSender(false, false);
    }

    @Test
    public void testRepriceDispatchedLoad() throws ApplicationException {
        loadEntity.setStatus(ShipmentStatus.DISPATCHED);
        loadEntity.setCarrier(carrier);
        loadEntity.getOrigin().setEarlyScheduledArrival(new Date());
        sut.processMessage(loadTender);
        verify(loadTenderService).tenderLoad(loadEntity, carrier, ShipmentStatus.DISPATCHED, ShipmentStatus.OPEN);
        verifyEmailSender(false, false);
        verifyAddressServiceCalls();
    }

    @Test
    public void testFindLoadByBolUsingBillToId() throws ApplicationException {
        billTo.getEdiSettings().setIsUniqueRefAndBol(true);
        sut.processMessage(loadTender);
        verify(ltlShipmentDao).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(ltlShipmentDao, times(0)).findShipmentByShipmentNumber(ORG_ID, SHIPMENT_NO);
        verify(loadBuilder, times(0)).isMaterialsUpdated(loadEntity, loadTender);
        verify(loadBuilder).createOrModifyLoad(loadTender, loadEntity, userAddress, userAddress);
        verify(shipmentSavingService).save(loadEntity, null, SecurityUtils.getCurrentPersonId(), SecurityUtils.getCurrentPersonId());
        verifyCommonScenarios();
        verifyEmailSender(false, false);
        verifyAddressServiceCalls();
    }

    @Test
    public void testFindLoadByBolUsingLocationId() throws ApplicationException {
        loadTender.setCustomerBillToId(null);
        loadTender.setCustomerLocationId(LOCATION_ID);
        billTo.getEdiSettings().setIsUniqueRefAndBol(true);
        sut.processMessage(loadTender);
        verify(billToDao, times(0)).find(BILL_TO_ID);
        verify(ltlShipmentDao).findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL);
        verify(ltlShipmentDao, times(0)).findShipmentByShipmentNumber(ORG_ID, SHIPMENT_NO);
        verify(loadBuilder, times(0)).isMaterialsUpdated(loadEntity, loadTender);
        verify(loadBuilder).createOrModifyLoad(loadTender, loadEntity, userAddress, userAddress);
        verifyCommonScenarios();
        verifyEmailSender(false, false);
        verifyAddressServiceCalls();
    }

    @Test
    public void testBuilderThrowsException() throws ApplicationException {
        doThrow(ApplicationException.class).when(loadBuilder).createOrModifyLoad(loadTender, loadEntity, userAddress, userAddress);
        try {
            sut.processMessage(loadTender);
            fail("Application exception not thrown");
        } catch (ApplicationException e) {
            verify(loadTenderAckService).sendMessage(loadEntity, loadTender, false);
        }
    }

    @Test(expected = IdentifiersUnavailableException.class)
    public void testShipmentNumbeIsNull() throws ApplicationException {
        loadTender.setShipmentNo(null);
        sut.processMessage(loadTender);
        billTo.getEdiSettings().setIsUniqueRefAndBol(true);
    }

    @Test(expected = IdentifiersUnavailableException.class)
    public void testBolIsNull() throws ApplicationException {
        billTo.getEdiSettings().setIsUniqueRefAndBol(true);
        loadTender.setBol(null);
        sut.processMessage(loadTender);
    }

    @Test
    public void testOpenCanceledLoad() throws ApplicationException {
        loadTender.setOperationType(OperationType.UPDATE);
        loadEntity.setStatus(ShipmentStatus.CANCELLED);

        sut.processMessage(loadTender);

        verify(contactInfoService, times(1)).getContactInfo(loadEntity.getLocation().getActiveAccountExecutive().getUser());
        verify(ediEmailSender, times(1)).ediUpdateNotProcessed(loadEntity, loadTender, EMAIL_ADDRESS, true, false);
        verifyCommonScenarios();
        verifyEmailSender(true, true);
        verifyAddressServiceCalls();
    }

    private LoadTrackingEntity createLoadTrackingEntity(String statusCode) {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setSource(IntegrationService.EDI_204);
        loadTracking.setLoadId(LOAD_ID);
        loadTracking.setStatusCode(statusCode);
        loadTracking.setTimezoneCode("ET");
        loadTracking.setTrackingDate(RECV_DATE);
        return loadTracking;
    }

    private String getTrackingStatusCode(OperationType operationType) {
        switch (operationType) {
        case TENDER:
            return "CE";
        case UPDATE:
            return "EU";
        case CANCEL:
            return "CN";
        default:
            break;
        }
        return null;
    }

    private void createBillTo() {
        EdiSettingsEntity ediSetting = new EdiSettingsEntity();
        ediSetting.setIsUniqueRefAndBol(false);
        billTo = new BillToEntity();
        billTo.setEdiSettings(ediSetting);
    }

    private void createLoadEntity() {
        carrier = new CarrierEntity();

        Set<ShipmentNoteEntity> notes = new HashSet<ShipmentNoteEntity>();
        ShipmentNoteEntity note = new ShipmentNoteEntity();
        notes.add(note);

        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity costDetailEntity = new LoadCostDetailsEntity();
        costDetails.add(costDetailEntity);

        AddressEntity address = new AddressEntity();
        address.setZip("Z2I8P0");
        loadEntity = new LoadEntity();
        loadEntity.setStatus(ShipmentStatus.OPEN);
        loadEntity.setId(LOAD_ID);
        loadEntity.setShipmentDirection(ShipmentDirection.INBOUND);
        loadEntity.setFinalizationStatus(ShipmentFinancialStatus.NONE);
        loadEntity.setShipmentNotes(notes);
        loadEntity.setLocation(location);
        loadEntity.setActiveCostDetails(costDetails);
        createLoadDetails(loadEntity, PointType.ORIGIN, LoadAction.PICKUP);
        createLoadDetails(loadEntity, PointType.DESTINATION, LoadAction.DELIVERY);
        loadEntity.getOrigin().setAddressCode(ORIGIN_ADDRESS_CODE);
        loadEntity.getOrigin().setEarlyScheduledArrival(calendar.getTime());
        loadEntity.getDestination().setAddressCode(DEST_ADDRESS_CODE);
        loadEntity.getOrigin().setAddress(address);

    }

    private LoadDetailsEntity createLoadDetails(LoadEntity load, PointType pointType, LoadAction actionType) {
        LoadDetailsEntity details = new LoadDetailsEntity(actionType, pointType);
        details.setLoad(load);
        load.addLoadDetails(details);

        return details;
    }

    private void setupLoadTender() {
        loadTender = new LoadMessageJaxbBO();
        loadTender.setBol(BOL);
        loadTender.setCustomerOrgId(ORG_ID);
        loadTender.setShipmentNo(SHIPMENT_NO);
        loadTender.setCustomerBillToId(BILL_TO_ID);
        loadTender.setOperationType(OperationType.TENDER);
        loadTender.setRecvDateTime(RECV_DATE);
        AddressJaxbBO origin = createAddresses(ORIGIN_ADDRESS_CODE, AddressType.ORIGIN);
        origin.setTransitDate(new TransitDateJaxbBO());
        origin.getTransitDate().setFromDate(calendar.getTime());
        AddressJaxbBO dest = createAddresses(DEST_ADDRESS_CODE, AddressType.DESTINATION);
        loadTender.addAddress(origin);
        loadTender.addAddress(dest);
        loadTender.setInboundOutbound("I");
        loadTender.setNotes("testNote");
    }

    private AddressJaxbBO createAddresses(String addressCode, AddressType type) {
        AddressJaxbBO address = new AddressJaxbBO();
        address.setAddressType(type);
        address.setAddressCode(addressCode);
        return address;
    }

    private void verifyCommonScenarios() throws ApplicationException {
        verify(loadTrackingDao).saveOrUpdate(createLoadTrackingEntity(getTrackingStatusCode(loadTender.getOperationType())));
        verify(shipmentSavingService).save(loadEntity, null, SecurityUtils.getCurrentPersonId(), SecurityUtils.getCurrentPersonId());
        verify(shipmentEventDao, times(0)).saveOrUpdate((LoadEventEntity) anyObject());
        verify(loadTenderAckService).sendMessage(loadEntity, loadTender, true);
        verify(shipmentEventDao, times(0)).saveOrUpdate((LoadEventEntity) anyObject());
        verify(loadTenderService, times(0)).tenderLoad(loadEntity, null, null, ShipmentStatus.OPEN);
    }

    private void verifyEmailSender(Boolean shouldSend, Boolean reopen) {
        if (shouldSend) {
            verify(ediEmailSender).ediUpdateNotProcessed(loadEntity, loadTender, EMAIL_ADDRESS, reopen, false);
            verify(contactInfoService).getContactInfo(loadEntity.getLocation().getActiveAccountExecutive().getUser());
        } else {
            verify(ediEmailSender, times(0)).ediUpdateNotProcessed(loadEntity, loadTender, EMAIL_ADDRESS, false, false);
            verify(contactInfoService, times(0)).getContactInfo(loadEntity.getLocation().getActiveAccountExecutive().getUser());
        }
    }

    private void verifyAddressServiceCalls() throws ValidationException {
        verify(addressService).getCustomerAddressByCode(ORG_ID, ORIGIN_ADDRESS_CODE);
        verify(addressService).getCustomerAddressByCode(ORG_ID, DEST_ADDRESS_CODE);
        verify(addressService, times(0)).saveOrUpdate((UserAddressBookEntity) anyObject(), anyLong(), anyLong(), eq(false));
    }

    private void setMocks() throws ApplicationException {
        ShipmentNoteEntity note = new ShipmentNoteEntity();
        UserAdditionalContactInfoBO contactInfo = new UserAdditionalContactInfoBO();
        contactInfo.setEmail(EMAIL_ADDRESS);
        UserEntity user = new UserEntity();
        when(loadBuilder.buildNotes((LoadEntity) anyObject(), (LoadMessageJaxbBO) anyObject())).thenReturn(note);
        when(locationDao.find(LOCATION_ID)).thenReturn(location);
        when(location.getBillTo()).thenReturn(billTo);
        when(ltlShipmentDao.findShipmentByBolAndShipmentNumber(ORG_ID, SHIPMENT_NO, BOL)).thenReturn(loadEntity);
        when(billToDao.find(BILL_TO_ID)).thenReturn(billTo);
        when(location.getActiveAccountExecutive()).thenReturn(activeAccountExecutive);
        when(activeAccountExecutive.getUser()).thenReturn(user);
        when(addressService.getCustomerAddressByCode(ORG_ID, ORIGIN_ADDRESS_CODE)).thenReturn(userAddress);
        when(addressService.getCustomerAddressByCode(ORG_ID, DEST_ADDRESS_CODE)).thenReturn(userAddress);
        when(originLoadDetails.getAddressCode()).thenReturn(ORIGIN_ADDRESS_CODE);
        when(originLoadDetails.getLoadMaterials()).thenReturn(null);
        when(originLoadDetails.getEarlyScheduledArrival()).thenReturn(calendar.getTime());
        when(destLoadDetails.getAddressCode()).thenReturn(DEST_ADDRESS_CODE);
        when(loadBuilder.createOrModifyLoad((LoadMessageJaxbBO) anyObject(), (LoadEntity) anyObject(), (UserAddressBookEntity) anyObject(),
                (UserAddressBookEntity) anyObject())).thenReturn(loadEntity);
        when(ltlShipmentDao.findShipmentByShipmentNumber(ORG_ID, SHIPMENT_NO)).thenReturn(loadEntity);
        when(loadBuilder.isMaterialsUpdated(loadEntity, loadTender)).thenReturn(false);
        when(contactInfoService.getContactInfo(user)).thenReturn(contactInfo);
    }
}
