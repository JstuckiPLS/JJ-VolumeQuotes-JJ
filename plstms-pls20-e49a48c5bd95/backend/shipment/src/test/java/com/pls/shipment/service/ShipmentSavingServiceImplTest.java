package com.pls.shipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.address.AddressDao;
import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNoteEntity;
import com.pls.shipment.service.impl.ShipmentSavingServiceImpl;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * Test cases for {@link ShipmentSavingServiceImpl} class.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentSavingServiceImplTest {
    private static final long USER_ID = (long) (Math.random() * 100 + 1L);

    @Mock
    private LtlShipmentDao ltlShipmentDao;
    @Mock
    private CarrierDao carrierDao;
    @Mock
    private AddressDao addressDao;
    @Mock
    private Validator<LoadEntity> shipmentValidator;
    @Mock
    private ShipmentEmailSender shipmentEmailSender;
    @Mock
    private LoadTenderService loadTenderService;
    @Mock
    private ShipmentAlertService shipmentAlertService;
    @Mock
    private AccessorialTypeDao accessorialTypeDao;
    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private ShipmentSavingServiceImpl service;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(accessorialTypeDao.getPickupAndDeliveryAccessorials(Mockito.anySet())).thenReturn(new ArrayList<AccessorialTypeEntity>());
    }

    @Test
    public void shouldBookShipment() throws ApplicationException {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        billTo.setInvoiceSettings(invoiceSettings);
        load.setBillTo(billTo);
        load.getBillTo().setId((long) (Math.random() * 100));
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);
        proposal.setPricingDetails(getPricingDetails());

        CarrierEntity carrierEntity = createCarrier();
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.bookShipment(load, false, USER_ID, proposal, currentUserId);

        verify(shipmentValidator).validate(load);
        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.BOOKED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());
        assertSame(route, bookedLoad.getRoute());
        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationUserAddressBook.getAddress(), destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getEarlyScheduledArrival(),
                destinationLoadDetails.getEarlyScheduledArrival());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getScheduledArrival(),
                destinationLoadDetails.getScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());
        assertSame(StatusYesNo.YES, activeCostDetails.getCostOverride());
        assertSame(StatusYesNo.YES, activeCostDetails.getRevenueOverride());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    private PricingDetailsBO getPricingDetails() {
        PricingDetailsBO pricingDetails = new PricingDetailsBO();
        pricingDetails.setSmc3MinimumCharge(new BigDecimal(Math.random()));
        pricingDetails.setTotalChargeFromSmc3(new BigDecimal(Math.random()));
        pricingDetails.setDeficitChargeFromSmc3(new BigDecimal(Math.random()));
        pricingDetails.setCostAfterDiscount(new BigDecimal(Math.random()));
        pricingDetails.setMinimumCost(new BigDecimal(Math.random()));
        pricingDetails.setCostDiscount(new BigDecimal(Math.random()));
        pricingDetails.setCarrierFSId((long) Math.random());
        pricingDetails.setCarrierFuelDiscount(new BigDecimal(Math.random()));
        pricingDetails.setPricingType(PricingType.BLANKET);
        pricingDetails.setMovementType(MoveType.INTER);
        pricingDetails.setEffectiveDate(new Date());
        Set<Smc3CostDetailsDTO> smc3CostDetails = new HashSet<Smc3CostDetailsDTO>();
        Smc3CostDetailsDTO smc3CostDetail = new Smc3CostDetailsDTO();
        smc3CostDetail.setCharge("" + Math.random());
        smc3CostDetail.setEnteredNmfcClass("" + Math.random());
        smc3CostDetail.setNmfcClass("" + Math.random());
        smc3CostDetail.setRate("" + Math.random());
        smc3CostDetail.setWeight("" + Math.random());
        smc3CostDetails.add(smc3CostDetail);
        pricingDetails.setSmc3CostDetails(smc3CostDetails);
        return pricingDetails;
    }

    @Test
    public void shouldBookShipmentForAutoDispatch() throws ApplicationException {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        billTo.setInvoiceSettings(invoiceSettings);
        load.setBillTo(billTo);
        load.getBillTo().setId((long) (Math.random() * 100));
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        load.setCarrier(createCarrier());

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);
        proposal.getCarrier().setApiCapable(false);
        proposal.setPricingDetails(getPricingDetails());

        CarrierEntity carrierEntity = createCarrier();

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.bookShipment(load, true, USER_ID, proposal, currentUserId);

        verify(shipmentValidator).validate(load);
        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());
        assertSame(route, bookedLoad.getRoute());
        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationUserAddressBook.getAddress(), destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getEarlyScheduledArrival(),
                destinationLoadDetails.getEarlyScheduledArrival());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getScheduledArrival(),
                destinationLoadDetails.getScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());
        assertSame(StatusYesNo.YES, activeCostDetails.getCostOverride());
        assertSame(StatusYesNo.YES, activeCostDetails.getRevenueOverride());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldBookExistingShipmentForAutoDispatch() throws ApplicationException {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setId(loadId);
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.CBI);
        billTo.setInvoiceSettings(invoiceSettings);
        load.setBillTo(billTo);
        load.getBillTo().setId((long) (Math.random() * 100));
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        CarrierEntity oldCarrier = createCarrier();
        load.setCarrier(oldCarrier);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);
        proposal.getCarrier().setApiCapable(false);
        proposal.setPricingDetails(getPricingDetails());

        CarrierEntity carrierEntity = createCarrier();

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(ltlShipmentDao.getShipmentCarrier(loadId)).thenReturn(oldCarrier);
        when(ltlShipmentDao.getShipmentStatus(loadId)).thenReturn(ShipmentStatus.BOOKED);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).thenReturn(load);

        LoadEntity bookedLoad = service.bookShipment(load, true, USER_ID, proposal, currentUserId);

        verify(shipmentValidator).validate(load);
        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));
        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), Matchers.eq(oldCarrier),
                Matchers.eq(ShipmentStatus.BOOKED), Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());
        assertSame(route, bookedLoad.getRoute());
        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationUserAddressBook.getAddress(), destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getEarlyScheduledArrival(),
                destinationLoadDetails.getEarlyScheduledArrival());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getScheduledArrival(),
                destinationLoadDetails.getScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldBookShipmentForCarrierNotEdiCapable() throws ApplicationException {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        billTo.setInvoiceSettings(invoiceSettings);
        load.setBillTo(billTo);
        load.getBillTo().setId((long) (Math.random() * 100));
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        load.setCarrier(createCarrier());

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);
        proposal.setPricingDetails(getPricingDetails());

        CarrierEntity carrierEntity = createCarrier();
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.bookShipment(load, true, USER_ID, proposal, currentUserId);

        verify(shipmentValidator).validate(load);
        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));
        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());
        assertSame(route, bookedLoad.getRoute());
        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationUserAddressBook.getAddress(), destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getEarlyScheduledArrival(),
                destinationLoadDetails.getEarlyScheduledArrival());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getScheduledArrival(),
                destinationLoadDetails.getScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldBookShipmentWithNewRouteAndBolNumber() throws ApplicationException {
        final Long currentUserId = USER_ID + 1;
        SecurityTestUtils.login("username", currentUserId);
        LoadEntity load = new LoadEntity();
        BillToEntity billTo = new BillToEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        billTo.setInvoiceSettings(invoiceSettings);
        load.setBillTo(billTo);
        load.getBillTo().setId((long) (Math.random() * 100));
        final String bol = "BOL" + Math.random();
        load.getNumbers().setBolNumber(bol);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);


        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);
        proposal.setPricingDetails(getPricingDetails());

        CarrierEntity carrierEntity = createCarrier();
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).thenReturn(load);

        LoadEntity bookedLoad = service.bookShipment(load, false, USER_ID, proposal, currentUserId);

        verify(shipmentValidator).validate(load);
        verify(ltlShipmentDao).saveOrUpdate(load);
        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.BOOKED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(bol, bookedLoad.getNumbers().getBolNumber());

        assertNotNull(bookedLoad.getRoute());
        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationUserAddressBook.getAddress(), destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getEarlyScheduledArrival(),
                destinationLoadDetails.getEarlyScheduledArrival());
        compareDateAndTime(proposal.getEstimatedTransitDate(), originLoadDetails.getScheduledArrival(),
                destinationLoadDetails.getScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    private void setupAddressInfoToLoadDetails(LoadDetailsEntity loadDetails, UserAddressBookEntity userAddressBook) {
        loadDetails.setAddress(userAddressBook.getAddress());
    }

    @Test
    public void shouldSaveSalesOrderWithChangedCarrier() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.IN_TRANSIT);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setDeparture(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        CarrierEntity oldCarrier = createCarrier();
        load.setCarrier(oldCarrier);
        when(ltlShipmentDao.getShipmentCarrier(loadId)).thenReturn(oldCarrier);
        when(ltlShipmentDao.getShipmentStatus(loadId)).thenReturn(ShipmentStatus.BOOKED);
        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadNoteEntity specialMessage = new LoadNoteEntity();
        specialMessage.setNote("note" + Math.random());
        load.setSpecialMessage(specialMessage);
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId());
        costDetail.setShipDate(originLoadDetails.getDeparture());
        load.getCostDetails().add(costDetail);

        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.eq(ShipmentStatus.BOOKED),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.IN_TRANSIT, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(specialMessage, bookedLoad.getSpecialMessage());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getDeparture(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo().getId(), itemEntity.getBillTo().getId());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldSaveSalesOrderWithRemovedCarrier() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.OPEN);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setDeparture(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCarrier(null);
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCarrier(createCarrier());
        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadNoteEntity specialMessage = new LoadNoteEntity();
        specialMessage.setNote("note" + Math.random());
        load.setSpecialMessage(specialMessage);
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId());
        costDetail.setShipDate(originLoadDetails.getDeparture());
        load.getCostDetails().add(costDetail);

        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.OPEN, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertNull(bookedLoad.getCarrier());
        assertNull(bookedLoad.getSpecialMessage());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertNotSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getDeparture(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertNull(itemEntity.getCarrierId());
            assertSame(load.getBillTo().getId(), itemEntity.getBillTo().getId());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldSaveSalesOrderInDispatchedWithAddedCarrier() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.DISPATCHED);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId());
        proposal.getCarrier().setApiCapable(false);
        costDetail.setShipDate(originLoadDetails.getEarlyScheduledArrival());
        load.getCostDetails().add(costDetail);

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo().getId(), itemEntity.getBillTo().getId());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldRemoveBenchmarkAccessorialsIfNoAppropriateCarrierOrCustomerAccessorial() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.DISPATCHED);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        CostDetailItemBO carrierCostDetailItem = getCostDetailItem(CostDetailOwner.C);
        costDetails.add(carrierCostDetailItem);
        CostDetailItemBO benchmarkCostDetailItem = getCostDetailItem(CostDetailOwner.B);
        benchmarkCostDetailItem.setRefType(carrierCostDetailItem.getRefType());
        costDetails.add(benchmarkCostDetailItem);
        CostDetailItemBO shipperCostDetailItem = getCostDetailItem(CostDetailOwner.S);
        costDetails.add(shipperCostDetailItem);
        benchmarkCostDetailItem = getCostDetailItem(CostDetailOwner.B);
        benchmarkCostDetailItem.setRefType(shipperCostDetailItem.getRefType());
        costDetails.add(benchmarkCostDetailItem);
        benchmarkCostDetailItem = getCostDetailItem(CostDetailOwner.B);
        costDetails.add(benchmarkCostDetailItem);
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId());
        proposal.getCarrier().setApiCapable(false);
        costDetail.setShipDate(originLoadDetails.getEarlyScheduledArrival());
        load.getCostDetails().add(costDetail);

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(),  Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertNotSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size() - 1, activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            if (itemDTO == benchmarkCostDetailItem) {
                assertNull(itemEntity);
            } else {
                assertNotNull(itemEntity);
                if (itemDTO.getGuaranteedBy() != null) {
                    assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
                }
                assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
                assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
                assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
                assertSame(load.getBillTo().getId(), itemEntity.getBillTo().getId());
            }
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()).add(costDetails.get(5).getSubTotal()),
                activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()).add(costDetails.get(7).getSubTotal()),
                activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldUpdateCostsIfBillToChanged() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.DISPATCHED);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        load.setCarrier(createCarrier());

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId() + 1);
        proposal.getCarrier().setApiCapable(false);
        costDetail.setShipDate(originLoadDetails.getEarlyScheduledArrival());
        load.getCostDetails().add(costDetail);

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertNotSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldUpdateCostsIfCarrierChanged() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.DISPATCHED);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId() + 1, load.getBillTo().getId());
        proposal.getCarrier().setApiCapable(false);
        costDetail.setShipDate(originLoadDetails.getEarlyScheduledArrival());
        load.getCostDetails().add(costDetail);

        OrgServiceEntity orgServicesEntity = new OrgServiceEntity();
        orgServicesEntity.setPickup(CarrierIntegrationType.EDI);
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(1)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.DISPATCHED, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertNotSame(costDetail, activeCostDetails);
        assertSame(originLoadDetails.getEarlyScheduledArrival(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());
        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldSaveSalesOrderWithNewCostDetail() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        load.setBillTo(new BillToEntity());
        load.getBillTo().setId((long) (Math.random() * 100));
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        load.setStatus(ShipmentStatus.IN_TRANSIT);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        originLoadDetails.setDeparture(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        load.setCarrier(createCarrier());

        UserAddressBookEntity originUserAddressBook = getUserAddressBook(-1L);
        originUserAddressBook.getAddress().setId((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook(-1L);
        destinationUserAddressBook.getAddress().setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);
        AddressEntity destinationAddress = (AddressEntity) BeanUtils.cloneBean(destinationUserAddressBook.getAddress());
        destinationAddress.setId((long) (Math.random() * 100));
        destinationLoadDetails.setAddress(destinationAddress);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();

        load.setCostDetails(new LinkedHashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetail = getNewActiveCostDetailFromProposal(proposal, carrierEntity.getId(), load.getBillTo().getId());
        // Changed one of cost detail items - service should generate new active load cost details
        costDetail.getCostDetailItems().iterator().next().setSubtotal(BigDecimal.TEN);
        costDetail.setInvoiceNumber("invoiceNumber" + Math.random());

        costDetail.setShipDate(originLoadDetails.getEarlyScheduledArrival());
        load.getCostDetails().add(costDetail);
        load.getCostDetails().add(new LoadCostDetailsEntity());

        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        LoadEntity bookedLoad = service.save(load, proposal, USER_ID, currentUserId);

        verify(ltlShipmentDao, times(2)).saveOrUpdate(load);
        verify(addressDao, never()).saveOrUpdate(destinationUserAddressBook.getAddress());

        verify(shipmentAlertService).processShipmentAlerts(Matchers.eq(load));

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(bookedLoad), (CarrierEntity) anyObject(), Matchers.isNull(ShipmentStatus.class),
                Matchers.eq(bookedLoad.getStatus()));

        assertNotNull(bookedLoad);
        assertSame(ShipmentStatus.IN_TRANSIT, bookedLoad.getStatus());
        assertSame(USER_ID, bookedLoad.getPersonId());
        assertEquals(loadId.toString(), bookedLoad.getNumbers().getBolNumber());

        assertSame(originUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getOriginZip());
        assertSame(originUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getOriginState());
        assertSame(originUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getOriginCity());
        assertSame(originUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getOriginCountry());
        assertSame(destinationUserAddressBook.getAddress().getZip(), bookedLoad.getRoute().getDestZip());
        assertSame(destinationUserAddressBook.getAddress().getStateCode(), bookedLoad.getRoute().getDestState());
        assertSame(destinationUserAddressBook.getAddress().getCity(), bookedLoad.getRoute().getDestCity());
        assertSame(destinationUserAddressBook.getAddress().getCountry().getId(), bookedLoad.getRoute().getDestCountry());
        assertSame(currentUserId, bookedLoad.getRoute().getCreatedBy());

        assertSame(currentUserId, bookedLoad.getModification().getCreatedBy());
        assertSame(originUserAddressBook.getAddress(), originLoadDetails.getAddress());
        assertSame(destinationAddress, destinationLoadDetails.getAddress());

        assertSame(proposal.getEstimatedTransitTime(), bookedLoad.getTravelTime());
        assertSame(proposal.getMileage(), bookedLoad.getMileage());
        assertSame(carrierEntity, bookedLoad.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), bookedLoad.getSpecialMessage().getNote());
        assertSame(bookedLoad, bookedLoad.getSpecialMessage().getLoad());
        assertNull("Sales order doesn't have information about Travel Time. So we can't calculate Estimated Delivery Date",
                destinationLoadDetails.getEarlyScheduledArrival());

        LoadCostDetailsEntity activeCostDetails = bookedLoad.getActiveCostDetail();
        assertNotSame(costDetail, activeCostDetails);
        assertSame(costDetail, bookedLoad.getCostDetails().iterator().next());
        assertSame(Status.INACTIVE, costDetail.getStatus());
        assertSame(costDetail.getInvoiceNumber(), activeCostDetails.getInvoiceNumber());

        assertSame(originLoadDetails.getDeparture(), activeCostDetails.getShipDate());
        assertSame(Status.ACTIVE, activeCostDetails.getStatus());

        assertSame(proposal.getServiceType(), activeCostDetails.getServiceType());
        assertSame(proposal.getNewLiability(), activeCostDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), activeCostDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), activeCostDetails.getProhibitedCommodities());
        assertSame(proposal.getGuaranteedNameForBOL(), activeCostDetails.getGuaranteedNameForBOL());
        assertSame(proposal.getPricingProfileId(), activeCostDetails.getPricingProfileDetailId());
        assertSame(proposal.getRevenueOverride(), activeCostDetails.getRevenueOverride());
        assertSame(proposal.getCostOverride(), activeCostDetails.getCostOverride());

        assertSame(costDetails.size(), activeCostDetails.getCostDetailItems().size());
        for (CostDetailItemBO itemDTO : costDetails) {
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(activeCostDetails.getCostDetailItems(), itemDTO);
            assertNotNull(itemEntity);
            if (itemDTO.getGuaranteedBy() != null) {
                assertSame(itemDTO.getGuaranteedBy(), activeCostDetails.getGuaranteedBy());
            }
            assertSame(itemDTO.getSubTotal(), itemEntity.getSubtotal());
            assertSame(itemDTO.getSubTotal(), itemEntity.getUnitCost());
            assertSame(carrierEntity.getId(), itemEntity.getCarrierId());
            assertSame(load.getBillTo(), itemEntity.getBillTo());
        }

        assertEquals(costDetails.get(0).getSubTotal().add(costDetails.get(1).getSubTotal()), activeCostDetails.getTotalCost());
        assertEquals(costDetails.get(2).getSubTotal().add(costDetails.get(3).getSubTotal()), activeCostDetails.getTotalRevenue());
    }

    @Test
    public void shouldSendNotifications() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.setBillTo(new BillToEntity());
        load.getBillTo().setInvoiceSettings(invoiceSettings);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        load.setId(loadId);
        load.setStatus(ShipmentStatus.IN_TRANSIT);

        when(ltlShipmentDao.getShipmentStatus(load.getId())).thenReturn(ShipmentStatus.BOOKED);
        service.save(load, proposal, USER_ID, currentUserId);
        // status was updated, so notification should be sent
        verify(shipmentEmailSender).sendLoadStatusChangedNotification(load, load.getStatus());

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(load), (CarrierEntity) anyObject(), Matchers.eq(ShipmentStatus.BOOKED),

        Matchers.eq(load.getStatus()));
    }

    @Test
    public void shouldNotSendNotifications() throws Exception {
        final Long currentUserId = USER_ID + 1;
        final Long loadId = (long) (Math.random() * 100);
        SecurityTestUtils.login("username", currentUserId);
        final LoadEntity load = new LoadEntity();
        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);
        load.setBillTo(new BillToEntity());
        load.getBillTo().setInvoiceSettings(invoiceSettings);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setScheduledArrival(new Date());
        load.addLoadDetails(originLoadDetails);
        LoadDetailsEntity destinationLoadDetails = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        load.addLoadDetails(destinationLoadDetails);

        UserAddressBookEntity originUserAddressBook = getUserAddressBook((long) (Math.random() * 100));
        UserAddressBookEntity destinationUserAddressBook = getUserAddressBook((long) (Math.random() * 100 + 101));
        destinationUserAddressBook.setId(null);
        setupAddressInfoToLoadDetails(originLoadDetails, originUserAddressBook);
        setupAddressInfoToLoadDetails(destinationLoadDetails, destinationUserAddressBook);

        List<CostDetailItemBO> costDetails = getCostDetails();
        ShipmentProposalBO proposal = getProposal();
        proposal.setCostDetailItems(costDetails);

        CarrierEntity carrierEntity = createCarrier();
        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrierEntity);
        RouteEntity route = getRoute();
        when(addressDao.findRouteByAddresses(originUserAddressBook.getAddress().getId(), destinationUserAddressBook.getAddress().getId()))
                .thenReturn(route);
        when(ltlShipmentDao.saveOrUpdate(load)).then(new Answer<LoadEntity>() {
            @Override
            public LoadEntity answer(InvocationOnMock invocation) throws Throwable {
                load.setId(loadId);
                return load;
            }
        });

        load.setId(loadId);
        load.setStatus(ShipmentStatus.IN_TRANSIT);

        when(ltlShipmentDao.getShipmentStatus(load.getId())).thenReturn(ShipmentStatus.IN_TRANSIT);
        service.save(load, proposal, USER_ID, currentUserId);
        // if previous status is same as new status sending notification is not carried out
        verify(shipmentEmailSender, never()).sendLoadStatusChangedNotification(load, load.getStatus());

        verify(loadTenderService, times(0)).tenderLoad(Matchers.eq(load), (CarrierEntity) anyObject(), Matchers.eq(ShipmentStatus.IN_TRANSIT),
                Matchers.eq(load.getStatus()));
    }

    private CarrierEntity createCarrier() {
        CarrierEntity carrierEntity = new CarrierEntity();
        carrierEntity.setId((long) (Math.random() * 100));
        carrierEntity.setScac("scac" + Math.random());
        carrierEntity.setName("carrier name" + Math.random());
        return carrierEntity;
    }

    private void compareDateAndTime(Date expectedDate, Date expectedTime, Date actualDateTime) {
        Calendar cExpectedDate = Calendar.getInstance();
        cExpectedDate.setTime(expectedDate);
        Calendar cExpectedTime = Calendar.getInstance();
        cExpectedTime.setTime(expectedTime);
        Calendar cActualDateTime = Calendar.getInstance();
        cActualDateTime.setTime(actualDateTime);

        assertEquals(cExpectedDate.get(Calendar.DAY_OF_YEAR), cActualDateTime.get(Calendar.DAY_OF_YEAR));
        assertEquals(cExpectedDate.get(Calendar.YEAR), cActualDateTime.get(Calendar.YEAR));

        assertEquals(cExpectedTime.get(Calendar.HOUR_OF_DAY), cActualDateTime.get(Calendar.HOUR_OF_DAY));
        assertEquals(cExpectedTime.get(Calendar.MINUTE), cActualDateTime.get(Calendar.MINUTE));
        assertEquals(cExpectedTime.get(Calendar.SECOND), cActualDateTime.get(Calendar.SECOND));
        assertEquals(cExpectedTime.get(Calendar.MILLISECOND), cActualDateTime.get(Calendar.MILLISECOND));
    }

    private CostDetailItemEntity getCostDetailByTypeAndOwner(Set<CostDetailItemEntity> items, CostDetailItemBO origItem) {
        for (CostDetailItemEntity item : items) {
            if (StringUtils.equals(origItem.getRefType(), item.getAccessorialType()) && origItem.getCostDetailOwner() == item.getOwner()) {
                return item;
            }
        }
        return null;
    }

    private LoadCostDetailsEntity getNewActiveCostDetailFromProposal(ShipmentProposalBO proposal, Long carrierId, Long billToId) {
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);

        costDetail.setServiceType(proposal.getServiceType());
        costDetail.setNewLiability(proposal.getNewLiability());
        costDetail.setUsedLiability(proposal.getUsedLiability());
        costDetail.setProhibitedCommodities(proposal.getProhibited());
        costDetail.setGuaranteedNameForBOL(proposal.getGuaranteedNameForBOL());
        costDetail.setPricingProfileDetailId(proposal.getPricingProfileId());
        costDetail.setRevenueOverride(proposal.getRevenueOverride());
        costDetail.setCostOverride(proposal.getCostOverride());

        costDetail.setCostDetailItems(new HashSet<CostDetailItemEntity>());

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (CostDetailItemBO costDetailItem : proposal.getCostDetailItems()) {
            CostDetailItemEntity itemEntity = new CostDetailItemEntity();
            itemEntity.setCostDetails(costDetail);
            itemEntity.setAccessorialType(costDetailItem.getRefType());
            if (costDetailItem.getGuaranteedBy() != null) {
                costDetail.setGuaranteedBy(costDetailItem.getGuaranteedBy());
            }
            itemEntity.setOwner(costDetailItem.getCostDetailOwner());
            itemEntity.setSubtotal(costDetailItem.getSubTotal());
            itemEntity.setUnitCost(costDetailItem.getSubTotal());
            itemEntity.setCarrierId(carrierId);
            itemEntity.setBillTo(new BillToEntity());
            itemEntity.getBillTo().setId(billToId);
            if (itemEntity.getOwner() == CostDetailOwner.C) {
                totalCost = totalCost.add(costDetailItem.getSubTotal());
            } else if (itemEntity.getOwner() == CostDetailOwner.S) {
                totalRevenue = totalRevenue.add(costDetailItem.getSubTotal());
            }
            costDetail.getCostDetailItems().add(itemEntity);
        }
        costDetail.setTotalCost(totalCost);
        costDetail.setTotalRevenue(totalRevenue);
        return costDetail;
    }

    private List<CostDetailItemBO> getCostDetails() {
        List<CostDetailItemBO> costDetails = new ArrayList<CostDetailItemBO>();
        costDetails.add(getCostDetailItem(CostDetailOwner.C));
        costDetails.add(getCostDetailItem(CostDetailOwner.C));
        costDetails.add(getCostDetailItem(CostDetailOwner.S));
        costDetails.add(getCostDetailItem(CostDetailOwner.S));
        costDetails.add(getCostDetailItem(CostDetailOwner.B));
        costDetails.get(3).setGuaranteedBy((long) (Math.random() * 100));
        costDetails.get(4).setRefType("SBR");
        return costDetails;
    }

    private RouteEntity getRoute() {
        RouteEntity route = new RouteEntity();
        route.setId((long) (Math.random() * 100));
        return route;
    }

    private UserAddressBookEntity getUserAddressBook(long id) {
        UserAddressBookEntity userAddressBook = new UserAddressBookEntity();
        userAddressBook.setId(id);
        userAddressBook.setAddressName("address" + id);
        AddressEntity address = getAddress(id);
        userAddressBook.setAddress(address);
        userAddressBook.setPhone(new PhoneEntity());
        return userAddressBook;
    }

    private AddressEntity getAddress(long id) {
        AddressEntity address = new AddressEntity();
        address.setId(id);
        address.setCity("city" + Math.random());
        address.setStateCode("stateCode");
        CountryEntity country = new CountryEntity();
        country.setId("countryCode" + Math.random());
        address.setCountry(country);
        address.setZip("zip" + Math.random());
        address.setAddress1("address1" + Math.random());
        address.setAddress2("address2" + Math.random());
        address.setLatitude(BigDecimal.valueOf(Math.random()));
        address.setLongitude(BigDecimal.valueOf(Math.random()));
        address.setStatus(Status.ACTIVE);
        return address;
    }

    private ShipmentProposalBO getProposal() {
        ShipmentProposalBO proposal = new ShipmentProposalBO();
        CarrierDTO carrier = new CarrierDTO();
        carrier.setScac("scac" + Math.random());
        carrier.setSpecialMessage("specialMessage" + Math.random());
        proposal.setCarrier(carrier);
        proposal.setMileage((int) (Math.random() * 100));
        proposal.setEstimatedTransitTime((long) (Math.random() * 100));
        proposal.setEstimatedTransitDate(DateUtils.addDays(new Date(), ((int) Math.random() * 10) + 1));
        proposal.setServiceType(LtlServiceType.BOTH);
        proposal.setNewLiability(BigDecimal.valueOf(Math.random()));
        proposal.setUsedLiability(BigDecimal.valueOf(Math.random()));
        proposal.setProhibited("prohibited" + Math.random());
        proposal.setPricingProfileId((long) (Math.random() * 100));
        proposal.setGuaranteedNameForBOL("guaranteedNameForBOL" + Math.random());
        proposal.setCostOverride(StatusYesNo.YES);
        proposal.setRevenueOverride(StatusYesNo.YES);
        return proposal;
    }

    private CostDetailItemBO getCostDetailItem(CostDetailOwner costDetailOwner) {
        CostDetailItemBO costDetailItem = new CostDetailItemBO();
        costDetailItem.setCostDetailOwner(costDetailOwner);
        costDetailItem.setRefType("refType" + Math.random());
        costDetailItem.setSubTotal(BigDecimal.valueOf(Math.random()));
        return costDetailItem;
    }
}
