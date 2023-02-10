package com.pls.shipment.service.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.domain.enums.ShipmentOperationType;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.integration.ltllifecycle.service.LTLLifecycleIntegrationService;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.impl.edi.EDIHelper;

/**
 * Test cases for {@link LoadTenderServiceImpl}.
 * 
 * @author Artem Arapov
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@RunWith(MockitoJUnitRunner.class)
public class LoadTenderServiceImplTest {

    private static final long ORGANIZATION_ID = (long) (Math.random() * 100 + 1L);
    private static final long USER_ID = (long) (Math.random() * 100 + 1L);

    @InjectMocks
    private LoadTenderServiceImpl sut;

    @Mock
    private OutboundEdiQueueMappingUtils outboundEdiQueueUtils;

    @Mock
    LoadTrackingDao loadTrackingDaoImpl;

    @Mock
    private EDIService ediService;

    @Mock
    private UserPermissionsService userPermissionsService;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Mock
    private EDIHelper ediHelper;
    
    @Mock
    private LTLLifecycleIntegrationService ltlLifecycleIntegrationService;
    
    @Mock
    private LtlProfileDetailsService ltlProfileDetailsService;

    @Test
    public void shouldSendEDIWithTenderStatus() throws Exception {
        LoadEntity load = returnRandomLoad();
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(true);
        sut.tenderLoad(load, (CarrierEntity) anyObject(), ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED);
    }
    
    @Test
    public void testDispatchWithLTLLC() throws Exception {
        LoadEntity load = returnRandomLoad();
        load.getActiveCostDetail().setPricingProfileDetailId(123L);
        
        LtlPricingProfileEntity testProfile = new LtlPricingProfileEntity();
        LtlPricingProfileDetailsEntity testProfileDetails = new LtlPricingProfileDetailsEntity();
        testProfileDetails.setCarrierType("LTLLC");
        testProfile.setProfileDetails(Collections.singleton(testProfileDetails ));
        when(ltlProfileDetailsService.getProfileById(123L)).thenReturn(testProfile );
        
        sut.tenderLoad(load, (CarrierEntity) anyObject(), ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED);
        
        Mockito.verify(ltlLifecycleIntegrationService).dispatchLoad(Mockito.eq(load));
    }

    @Test
    public void testSendEDIThroughFTPWithTenderStatus() throws Exception {
        LoadEntity load = returnRandomLoad();
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(false);
        sut.tenderLoad(load, null, ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY, ShipmentOperationType.TENDER);
        params.put("PRODUCTION_MODE", null);
        Mockito.verify(ediService).sendEDI(load.getCarrier().getId(), Arrays.asList(load.getId()), EDITransactionSet._204, params);
    }

    @Test
    public void testSendEDIThroughFTPWithUpdateStatus() throws Exception {
        LoadEntity load = returnRandomLoad();
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(false);
        sut.tenderLoad(load, null, ShipmentStatus.DISPATCHED, ShipmentStatus.DISPATCHED);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY, ShipmentOperationType.UPDATE);
        params.put("PRODUCTION_MODE", null);
        Mockito.verify(ediService).sendEDI(load.getCarrier().getId(), Arrays.asList(load.getId()), EDITransactionSet._204, params);
    }

    @Test
    public void shouldSendEDIWithUpdateStatus() throws Exception {
        SecurityTestUtils.login("username", USER_ID, ORGANIZATION_ID, Capabilities.DO_NOT_SEND_DISPATCH_TO_CARRIER.name());
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(true);
        when(ediHelper.isEdi204Changed((LoadMessageJaxbBO) anyObject())).thenReturn(true);
        LoadEntity load = returnRandomLoad();
        sut.tenderLoad(load, null, ShipmentStatus.DISPATCHED, ShipmentStatus.DISPATCHED);
        Mockito.verify(sterlingMessageProducer, Mockito.times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
        Mockito.verify(loadTrackingDaoImpl, Mockito.times(1)).saveOrUpdate((LoadTrackingEntity) anyObject());
        Mockito.verify(userPermissionsService).hasCapability(Matchers.eq(Capabilities.DO_NOT_SEND_DISPATCH_TO_CARRIER.name()));
    }

    @Test
    public void shouldSendCancelationEDIAndTenderEDI() throws Exception {
        CarrierEntity oldCarrier = new CarrierEntity();
        oldCarrier.setId((long) Math.random() * 100 + 101);
        LoadEntity load = returnRandomLoad();
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(true);
        sut.tenderLoad(load, oldCarrier, ShipmentStatus.DISPATCHED, ShipmentStatus.DISPATCHED);
    }

    @Test
    public void testSendCancelationEDIAndTenderEDIThroughFTP() throws Exception {
        CarrierEntity oldCarrier = new CarrierEntity();
        oldCarrier.setId((long) Math.random() * 100 + 101);
        LoadEntity load = returnRandomLoad();
        when(outboundEdiQueueUtils.isQueueEnabled((String) anyObject())).thenReturn(false);
        sut.tenderLoad(load, oldCarrier, ShipmentStatus.DISPATCHED, ShipmentStatus.DISPATCHED);

        ArgumentCaptor<Long> carrierIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<List> loadIdListCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<EDITransactionSet> transactionSetCaptor = ArgumentCaptor.forClass(EDITransactionSet.class);
        ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(ediService, Mockito.times(2)).sendEDI(carrierIdCaptor.capture(), loadIdListCaptor.capture(), transactionSetCaptor.capture(),
                paramsCaptor.capture());

        Object[] carriersArray = carrierIdCaptor.getAllValues().toArray();
        Object[] paramsArray = paramsCaptor.getAllValues().toArray();

        for (int i = 0; i < carriersArray.length; i++) {
            Long carrierId = (Long) carriersArray[i];
            Map params = (Map) paramsArray[i];
            if (carrierId.equals(oldCarrier.getId())) {
                assertOperationTypeKey(ShipmentOperationType.CANCELLATION, params);
            }

            if (carrierId.equals(load.getCarrier().getId())) {
                assertOperationTypeKey(ShipmentOperationType.TENDER, params);
            }
        }
    }

    private LoadEntity returnRandomLoad() {
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
        // setting up Freight Bill To address
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

        // setting up hazmat info
        loadMaterialEntity.setHazmat(Boolean.TRUE);
        loadMaterials.add(loadMaterialEntity);
        originLoadDetailsEntity.setLoadMaterials(loadMaterials);

        // setting up origin address information
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

    private void assertOperationTypeKey(ShipmentOperationType expectedOperationType, Map params) {
        Object rawOperationType = params.get(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY);
        Assert.assertEquals(expectedOperationType, (ShipmentOperationType) rawOperationType);
    }
}
