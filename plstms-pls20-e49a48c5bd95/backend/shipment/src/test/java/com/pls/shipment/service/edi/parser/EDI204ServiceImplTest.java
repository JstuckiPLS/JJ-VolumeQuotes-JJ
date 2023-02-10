package com.pls.shipment.service.edi.parser;

import static com.pls.core.domain.enums.ShipmentStatus.BOOKED;
import static com.pls.core.domain.enums.ShipmentStatus.CANCELLED;
import static com.pls.core.domain.enums.ShipmentStatus.DELIVERED;
import static com.pls.core.domain.enums.ShipmentStatus.DISPATCHED;
import static com.pls.core.domain.enums.ShipmentStatus.IN_TRANSIT;
import static com.pls.core.domain.enums.ShipmentStatus.OPEN;
import static com.pls.core.domain.enums.ShipmentStatus.OUT_FOR_DELIVERY;
import static com.pls.shipment.domain.enums.ShipmentOperationType.CANCELLATION;
import static com.pls.shipment.domain.enums.ShipmentOperationType.TENDER;
import static com.pls.shipment.domain.enums.ShipmentOperationType.UPDATE;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.enums.ShipmentOperationType;
import com.pls.shipment.service.impl.LoadTenderServiceImpl;
import com.pls.shipment.service.impl.edi.EDIHelper;

/**
 * Test cases for {@link LoadTenderServiceImpl} class.
 *
 * @author Mikhail Boldinov, 23/01/14
 */

@RunWith(MockitoJUnitRunner.class)
public class EDI204ServiceImplTest {

    private static final long ORGANIZATION_ID = (long) (Math.random() * 100 + 1L);
    private static final long USER_ID = (long) (Math.random() * 100 + 1L);

    private static final ShipmentStatusChange[] STATUSES_CHANGES_TENDERED_LOAD = {
            new ShipmentStatusChange(OPEN, DISPATCHED),
            new ShipmentStatusChange(BOOKED, DISPATCHED),
            new ShipmentStatusChange(IN_TRANSIT, DISPATCHED),
            new ShipmentStatusChange(DELIVERED, DISPATCHED),
            new ShipmentStatusChange(CANCELLED, DISPATCHED),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, DISPATCHED),
            new ShipmentStatusChange(null, DISPATCHED)
    };

    private static final ShipmentStatusChange[] STATUSES_CHANGES_UPDATED_LOAD = {
            new ShipmentStatusChange(DISPATCHED, DISPATCHED)
    };

    private static final ShipmentStatusChange[] STATUSES_CHANGES_CANCELLED_LOAD = {
            new ShipmentStatusChange(DISPATCHED, CANCELLED)
    };

    private static final ShipmentStatusChange[] STATUSES_CHANGES_IGNORED = {
            new ShipmentStatusChange(OPEN, OPEN),
            new ShipmentStatusChange(OPEN, BOOKED),
            new ShipmentStatusChange(OPEN, IN_TRANSIT),
            new ShipmentStatusChange(OPEN, DELIVERED),
            new ShipmentStatusChange(OPEN, CANCELLED),
            new ShipmentStatusChange(OPEN, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(BOOKED, OPEN),
            new ShipmentStatusChange(BOOKED, BOOKED),
            new ShipmentStatusChange(BOOKED, IN_TRANSIT),
            new ShipmentStatusChange(BOOKED, DELIVERED),
            new ShipmentStatusChange(BOOKED, CANCELLED),
            new ShipmentStatusChange(BOOKED, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(DISPATCHED, OPEN),
            new ShipmentStatusChange(DISPATCHED, BOOKED),
            new ShipmentStatusChange(DISPATCHED, IN_TRANSIT),
            new ShipmentStatusChange(DISPATCHED, DELIVERED),
            new ShipmentStatusChange(DISPATCHED, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(IN_TRANSIT, OPEN),
            new ShipmentStatusChange(IN_TRANSIT, BOOKED),
            new ShipmentStatusChange(IN_TRANSIT, IN_TRANSIT),
            new ShipmentStatusChange(IN_TRANSIT, DELIVERED),
            new ShipmentStatusChange(IN_TRANSIT, CANCELLED),
            new ShipmentStatusChange(IN_TRANSIT, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(DELIVERED, OPEN),
            new ShipmentStatusChange(DELIVERED, BOOKED),
            new ShipmentStatusChange(DELIVERED, IN_TRANSIT),
            new ShipmentStatusChange(DELIVERED, DELIVERED),
            new ShipmentStatusChange(DELIVERED, CANCELLED),
            new ShipmentStatusChange(DELIVERED, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(CANCELLED, OPEN),
            new ShipmentStatusChange(CANCELLED, BOOKED),
            new ShipmentStatusChange(CANCELLED, IN_TRANSIT),
            new ShipmentStatusChange(CANCELLED, DELIVERED),
            new ShipmentStatusChange(CANCELLED, CANCELLED),
            new ShipmentStatusChange(CANCELLED, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, OPEN),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, BOOKED),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, IN_TRANSIT),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, DELIVERED),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, CANCELLED),
            new ShipmentStatusChange(OUT_FOR_DELIVERY, OUT_FOR_DELIVERY),
            new ShipmentStatusChange(null, OPEN),
            new ShipmentStatusChange(null, BOOKED),
            new ShipmentStatusChange(null, IN_TRANSIT),
            new ShipmentStatusChange(null, DELIVERED),
            new ShipmentStatusChange(null, CANCELLED),
            new ShipmentStatusChange(null, OUT_FOR_DELIVERY)
    };

    @InjectMocks
    private LoadTenderServiceImpl sut;

    @Mock
    private UserPermissionsService userPermissionsService;

    @Mock
    private OutboundEdiQueueMappingUtils outboundEdiQueueUtils;

    @Mock
    LoadTrackingDao loadTrackingDaoImpl;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Mock
    private EDIHelper ediHelper;

    @Before
    public void initialize() {
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(true);
    }

    @Test
    public void shouldCreateEDI204LoadTendered() throws ApplicationException, JMSException {
        assertEdiCreatedWithValidOperationType(STATUSES_CHANGES_TENDERED_LOAD, TENDER);
    }

    @Test
    public void shouldCreateEDI204LoadUpdated() throws ApplicationException, JMSException {
        assertEdiCreatedWithValidOperationType(STATUSES_CHANGES_UPDATED_LOAD, UPDATE);
    }

    @Test
    public void shouldCreateEDI204LoadCancelled() throws ApplicationException, JMSException {
        assertEdiCreatedWithValidOperationType(STATUSES_CHANGES_CANCELLED_LOAD, CANCELLATION);
    }

    @Test
    public void shouldNotCreateEDI204() throws ApplicationException {
        SecurityTestUtils.login("username", USER_ID, ORGANIZATION_ID, Capabilities.DO_NOT_SEND_DISPATCH_TO_CARRIER.name());
        assertEDINotCreated(STATUSES_CHANGES_IGNORED);
    }

    private void assertEdiCreatedWithValidOperationType(ShipmentStatusChange[] shipmentStatusChanges, ShipmentOperationType expectedOperationType)
            throws ApplicationException, JMSException {
        for (ShipmentStatusChange shipmentStatusChange : shipmentStatusChanges) {
            LoadEntity load = getLoad();
            sut.tenderLoad(load, load.getCarrier(), shipmentStatusChange.getFrom(), shipmentStatusChange.getTo());
        }
    }

    private void assertEDINotCreated(ShipmentStatusChange[] shipmentStatusChanges) throws ApplicationException {
        for (ShipmentStatusChange shipmentStatusChange : shipmentStatusChanges) {
            LoadEntity load = getLoad();
            sut.tenderLoad(load, load.getCarrier(), shipmentStatusChange.getFrom(), shipmentStatusChange.getTo());
        }
    }

    private LoadEntity getLoad() {
        LoadEntity load = new LoadEntity();

        load.setId((long) (Math.random() * 100));
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId((long) (Math.random() * 100));
        load.setOrganization(customerEntity);
        load.setShipmentDirection(ShipmentDirection.INBOUND);
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        load.setCarrier(carrier);
        load.setPayTerms(PaymentTerms.PREPAID);
        load.setMileage((int) (Math.random() * 10));
        load.setWeight((int) (Math.random() * 10));
        load.setPieces((int) (Math.random() * 10));

        Set<LoadCostDetailsEntity> loadCostDetailsEntitySet = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setTotalCost(BigDecimal.valueOf((Math.random() * 100)));
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntitySet.add(loadCostDetailsEntity);
        load.setActiveCostDetails(loadCostDetailsEntitySet);
        load.setPersonId((long) (Math.random() * 100));

        OrganizationLocationEntity organizationLocationEntity = new OrganizationLocationEntity();
        organizationLocationEntity.setId((long) (Math.random() * 100));
        load.setLocation(organizationLocationEntity);
        BillToEntity billToEntity = new BillToEntity();
        billToEntity.setId((long) (Math.random() * 100));
        load.setBillTo(billToEntity);

        load.addLoadDetails(getOriginLoadDetails(LoadAction.PICKUP, PointType.ORIGIN));
        load.addLoadDetails(getDestinationLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION));

        //setting up Freight Bill To address
        FreightBillPayToEntity freightBillPayToEntity = new FreightBillPayToEntity();
        AddressEntity billToAddressEntity = new AddressEntity();
        CountryEntity freightBillToCountryEntity = new CountryEntity();
        billToAddressEntity.setCountry(freightBillToCountryEntity);
        freightBillPayToEntity.setAddress(billToAddressEntity);
        load.setFreightBillPayTo(freightBillPayToEntity);

        return load;
    }

    private LoadDetailsEntity getOriginLoadDetails(LoadAction loadAction, PointType pointType) {

        LoadDetailsEntity originLoadDetailsEntity = new LoadDetailsEntity(loadAction, pointType);
        originLoadDetailsEntity.setArrivalWindowStart(new Date());
        originLoadDetailsEntity.setArrivalWindowEnd(new Date());
        Set<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setWeight(BigDecimal.valueOf((Math.random() * 100)));
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_50);
        loadMaterialEntity.setQuantity(String.valueOf((int) (Math.random() * 10)));
        loadMaterialEntity.setStackable(Boolean.TRUE);
        loadMaterialEntity.setPieces((int) (Math.random() * 10));

        //setting up hazmat info
        loadMaterialEntity.setHazmat(Boolean.TRUE);
        loadMaterials.add(loadMaterialEntity);
        originLoadDetailsEntity.setLoadMaterials(loadMaterials);

        //setting up origin address information
        AddressEntity originAddressEntity = new AddressEntity();
        CountryEntity originCountryEntity = new CountryEntity();
        originAddressEntity.setCountry(originCountryEntity);
        originLoadDetailsEntity.setAddress(originAddressEntity);

        return originLoadDetailsEntity;

    }

    private LoadDetailsEntity getDestinationLoadDetails(LoadAction loadAction, PointType pointType) {

        LoadDetailsEntity destinationLoadDetailsEntity = new LoadDetailsEntity(loadAction, pointType);
         AddressEntity destinationAddressEntity = new AddressEntity();
         CountryEntity destCountryEntity = new CountryEntity();
         destinationLoadDetailsEntity.setScheduledArrival(new Date());
         destinationAddressEntity.setCountry(destCountryEntity);
         destinationLoadDetailsEntity.setAddress(destinationAddressEntity);

         return destinationLoadDetailsEntity;

    }

    /**
     * Inner class which represents a change of Shipment's status.
     * Holds previous and current values of Shipment status.
     */
    static class ShipmentStatusChange {
        private ShipmentStatus from;
        private ShipmentStatus to;

        /**
         * Constructor.
         *
         * @param from {@link ShipmentStatus} before change
         * @param to current {@link ShipmentStatus}
         */
        ShipmentStatusChange(ShipmentStatus from, ShipmentStatus to) {
            this.from = from;
            this.to = to;
        }

        ShipmentStatus getFrom() {
            return from;
        }

        ShipmentStatus getTo() {
            return to;
        }
    }
}
