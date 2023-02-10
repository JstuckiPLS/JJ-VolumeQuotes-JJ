package com.pls.shipment.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import com.google.common.collect.Sets;
import com.pls.core.dao.OrgServiceDao;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CreditLimitEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.NetworkEntity;
import com.pls.core.domain.organization.UnbilledRevenueEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.CustomerService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.RequiredDocumentDao;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.dao.LoadAdditionalInfoEntityDao;
import com.pls.shipment.dao.LoadPricingDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;
import com.pls.shipment.service.impl.ShipmentServiceImpl;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

import static org.mockito.Matchers.*;


/**
 * Test cases for {@link com.pls.shipment.service.impl.ShipmentServiceImpl} class.
 *
 * @author Gleb Zgonikov
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceImplTest {

    private static final long ORGANIZATION_ID = (long) (Math.random() * 100 + 1L);

    private static final long USER_ID = (long) (Math.random() * 100 + 1L);

    private static final long SHIPMENT_ID = 1L;

    private static final Long BILL_TO_ID = Math.round(Math.random() * 100);

    @Mock
    private LtlShipmentDao ltlShipmentDao;

    @Mock
    private RequiredDocumentDao requiredDocumentDao;

    @Mock
    private ShipmentAlertService shipmentAlertService;

    @Mock
    private UserPermissionsService userPermissionsService;

    @Mock
    private CarrierInvoiceDetailsDao carrierInvoiceDetailsDao;

    @Mock
    private ShipmentEmailSender shipmentEmailSender;

    @Mock
    private LoadTenderService loadTenderService;

    @Mock
    private OrgServiceDao orgServicesDao;

    @Mock
    private LoadAdditionalInfoEntityDao loadAdditionalInfoEntityDao;

    @Mock
    private ShipmentDocumentService shipmentDocumentService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ShipmentServiceImpl service;

    @Mock
    private LoadPricingDetailsDao loadPricingDetailsDao;
    
    @Test
    public void testDispatchShipment() throws ApplicationException {
        LoadEntity shipment = new LoadEntity();
        shipment.setStatus(ShipmentStatus.BOOKED);
        when(ltlShipmentDao.find(1L)).thenReturn(shipment);
        
        service.dispatchShipment(1L);
        
        verify(loadTenderService).tenderLoad(eq(shipment), isNull(CarrierEntity.class), eq(ShipmentStatus.BOOKED), eq(ShipmentStatus.DISPATCHED));
        Assert.assertEquals(ShipmentStatus.DISPATCHED, shipment.getStatus());
        verify(ltlShipmentDao).saveOrUpdate(shipment);
        verify(shipmentDocumentService).generateShipmentDocuments(any(Set.class), eq(shipment), eq(false), any(Long.class));
        verify(shipmentEmailSender).sendLoadStatusChangedNotification(shipment, ShipmentStatus.DISPATCHED);
    }
    
    @Test(expected = AccessDeniedException.class)
    public void testDispatchShipment_fail() throws ApplicationException {
        LoadEntity shipment = new LoadEntity();
        shipment.setStatus(ShipmentStatus.PENDING_PAYMENT);
        when(ltlShipmentDao.find(1L)).thenReturn(shipment);
        
        service.dispatchShipment(1L);
     }

    @Test
    public void shouldFindShipment() {
        LoadEntity shipment = new LoadEntity();

        when(ltlShipmentDao.find(1L)).thenReturn(shipment);

        LoadEntity result = service.findById(1L);
        assertSame(shipment, result);
    }

    @Test
    public void shouldGetShipmentWithAllDependencies() {
        LoadEntity shipment = new LoadEntity();

        when(ltlShipmentDao.getShipmentWithAllDependencies(1L)).thenReturn(shipment);

        LoadEntity result = service.getShipmentWithAllDependencies(1L);
        assertSame(shipment, result);
    }

    @Test
    public void shouldCheckPaperworkRequiredForCustomerInvoiceAndUpdateLoad() {
        LoadEntity load = new LoadEntity();
        long id = (long) (Math.random() * 100);
        load.setId(id);
        when(requiredDocumentDao.isAllPaperworkRequiredForBillToInvoicePresent(id)).thenReturn(true);

        service.checkPaperworkRequiredForCustomerInvoice(load);
        verify(requiredDocumentDao).isAllPaperworkRequiredForBillToInvoicePresent(id);
        verify(ltlShipmentDao).update(load);
        assertTrue(load.isCustReqDocPresent());
    }

    @Test
    public void shouldCheckPaperworkRequiredForCustomerInvoiceAndNotUpdateLoad() {
        LoadEntity load = new LoadEntity();
        long id = (long) (Math.random() * 100);
        load.setId(id);
        when(requiredDocumentDao.isAllPaperworkRequiredForBillToInvoicePresent(id)).thenReturn(false);

        service.checkPaperworkRequiredForCustomerInvoice(load);
        verify(requiredDocumentDao).isAllPaperworkRequiredForBillToInvoicePresent(id);
        verify(ltlShipmentDao, never()).update(load);
        assertFalse(load.isCustReqDocPresent());
    }

    @Test
    public void shouldCheckPaperworkRequiredForCustomerInvoiceAndUpdateLoadWhenFlagChanged() {
        LoadEntity load = new LoadEntity();
        load.setCustReqDocPresent(true);
        long id = (long) (Math.random() * 100);
        load.setId(id);
        when(requiredDocumentDao.isAllPaperworkRequiredForBillToInvoicePresent(id)).thenReturn(false);

        service.checkPaperworkRequiredForCustomerInvoice(load);
        verify(requiredDocumentDao).isAllPaperworkRequiredForBillToInvoicePresent(id);
        verify(ltlShipmentDao).update(load);
        assertFalse(load.isCustReqDocPresent());
    }

    @Test
    public void shouldCheckPaperworkRequiredForCustomerInvoiceAndNotUpdateLoadWhenFlagNotChanged() {
        LoadEntity load = new LoadEntity();
        load.setCustReqDocPresent(true);
        long id = (long) (Math.random() * 100);
        load.setId(id);
        when(requiredDocumentDao.isAllPaperworkRequiredForBillToInvoicePresent(id)).thenReturn(true);

        service.checkPaperworkRequiredForCustomerInvoice(load);
        verify(requiredDocumentDao).isAllPaperworkRequiredForBillToInvoicePresent(id);
        verify(ltlShipmentDao, never()).update(load);
        assertTrue(load.isCustReqDocPresent());
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotCancelShipmentDueToInvoicedStatus() throws ApplicationException {
        SecurityTestUtils.login("username", USER_ID, ORGANIZATION_ID, Capabilities.CAN_CANCEL_ORDER.name());

        LoadEntity shipment = new LoadEntity();
        shipment.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        when(ltlShipmentDao.find(SHIPMENT_ID)).thenReturn(shipment);

        service.cancelShipment(SHIPMENT_ID);
        verify(ltlShipmentDao).find(Matchers.eq(SHIPMENT_ID));
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotCancelShipmentDueToMissingCapability() throws ApplicationException {
        SecurityTestUtils.login("username", USER_ID, ORGANIZATION_ID);

        String capabilityName = Capabilities.CAN_CANCEL_ORDER.name();

        LoadEntity shipment = new LoadEntity();
        shipment.setStatus(ShipmentStatus.DELIVERED);
        when(ltlShipmentDao.find(SHIPMENT_ID)).thenReturn(shipment);

        doThrow(new AccessDeniedException("Mocked check capabilities")).when(userPermissionsService).checkCapability(capabilityName);

        service.cancelShipment(SHIPMENT_ID);
        verify(ltlShipmentDao).find(Matchers.eq(SHIPMENT_ID));
        verify(userPermissionsService).checkCapability(Matchers.eq(capabilityName));
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfCreditHold() throws Exception {
        BillToEntity billTo = new BillToEntity();
        billTo.setCreditHold(true);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(50L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(billTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfAvailableAmountLessThanRevenue() throws Exception {
        CreditLimitEntity creditLimit = new CreditLimitEntity();
        creditLimit.setCreditLimit(BigDecimal.valueOf(100L));
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(BigDecimal.valueOf(80L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);
        customer.setOverrideCreditHold(Boolean.TRUE);
        customer.setAutoCreditHold(Boolean.TRUE);

        BillToEntity billTo = new BillToEntity();
        billTo.setCreditLimit(creditLimit);
        billTo.setCreditHold(false);
        billTo.setUnbilledRevenue(unbilledRevenue);
        billTo.setOrganization(customer);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(50L));

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(billTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test
    public void shouldCheckCreditLimitForNewLoadNormalCase() throws Exception {
        CreditLimitEntity creditLimit = new CreditLimitEntity();
        creditLimit.setCreditLimit(BigDecimal.valueOf(100L));
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(BigDecimal.valueOf(50L));

        BillToEntity billTo = new BillToEntity();
        billTo.setCreditLimit(creditLimit);
        billTo.setCreditHold(false);
        billTo.setUnbilledRevenue(unbilledRevenue);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(50L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(billTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfBillToHoldForExistingLoadModifiedStatus() throws Exception {
        Long oldBillToId = BILL_TO_ID;

        BillToEntity newBillTo = new BillToEntity();
        newBillTo.setId(BILL_TO_ID);
        newBillTo.setCreditHold(true);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(50L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);

        LoadEntity load = new LoadEntity();
        load.setId(SHIPMENT_ID);
        load.setStatus(ShipmentStatus.DISPATCHED);
        load.setBillTo(newBillTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(ltlShipmentDao.getShipmentBillTo(SHIPMENT_ID)).thenReturn(oldBillToId);
        when(ltlShipmentDao.getShipmentStatus(SHIPMENT_ID)).thenReturn(ShipmentStatus.BOOKED);
        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfBillToHoldForExistingLoadModifiedBillTo() throws Exception {
        Long oldBillToId = Math.round(Math.random() * 100);

        BillToEntity newBillTo = new BillToEntity();
        newBillTo.setId(Math.round(Math.random() * 100 + 101));
        newBillTo.setCreditHold(true);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(50L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);

        LoadEntity load = new LoadEntity();
        load.setId(SHIPMENT_ID);
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(newBillTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(ltlShipmentDao.getShipmentBillTo(SHIPMENT_ID)).thenReturn(oldBillToId);
        when(ltlShipmentDao.getShipmentStatus(SHIPMENT_ID)).thenReturn(ShipmentStatus.BOOKED);
        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test(expected = ApplicationException.class)
    public void shouldCheckCreditLimitRelyingOnCustomerLevel() throws Exception {
        CreditLimitEntity creditLimit = new CreditLimitEntity();
        creditLimit.setCreditLimit(BigDecimal.valueOf(100L));
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(BigDecimal.valueOf(50L));

        BillToEntity billTo = new BillToEntity();
        billTo.setCreditLimit(creditLimit);
        billTo.setCreditHold(false);
        billTo.setUnbilledRevenue(unbilledRevenue);
        billTo.setOverrideCreditHold(false);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(100L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);
        customer.setOverrideCreditHold(true);
        customer.setAutoCreditHold(true);

        billTo.setOrganization(customer);
        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(billTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test(expected = ApplicationException.class)
    public void shouldCheckCreditLimitRelyingOnNetworkLevel() throws Exception {
        CreditLimitEntity creditLimit = new CreditLimitEntity();
        creditLimit.setCreditLimit(BigDecimal.valueOf(100L));
        UnbilledRevenueEntity unbilledRevenue = new UnbilledRevenueEntity();
        unbilledRevenue.setUnbilledRevenue(BigDecimal.valueOf(50L));

        BillToEntity billTo = new BillToEntity();
        billTo.setCreditLimit(creditLimit);
        billTo.setCreditHold(false);
        billTo.setUnbilledRevenue(unbilledRevenue);
        billTo.setOverrideCreditHold(false);

        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalRevenue(BigDecimal.valueOf(100L));

        CustomerEntity customer = new CustomerEntity();
        customer.setId(ORGANIZATION_ID);
        customer.setOverrideCreditHold(false);
        customer.setAutoCreditHold(true);

        NetworkEntity network = new NetworkEntity();
        network.setAutoCreditHold(true);

        customer.setNetwork(network);

        billTo.setOrganization(customer);
        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.BOOKED);
        load.setBillTo(billTo);
        load.setActiveCostDetails(Sets.newHashSet(costDetails));
        load.setOrganization(customer);

        when(customerService.getCreditLimitRequited(ORGANIZATION_ID)).thenReturn(Boolean.TRUE);

        service.checkBillToCreditLimit(load);
    }

    @Test
    public void shouldRegenerateDoc() throws PDFGenerationException {
        LoadEntity loadEntity = new LoadEntity();
        loadEntity.setId(1L);
        LoadAdditionalInfoEntity additionalInfoEntity = new LoadAdditionalInfoEntity();
        additionalInfoEntity.setMarkup(10L);
        when(loadAdditionalInfoEntityDao.getAdditionalInfoByLoadId(Matchers.eq(1L))).thenReturn(additionalInfoEntity);
        when(service.findById(Matchers.eq(1L))).thenReturn(loadEntity);
        service.regenerateDocsForShipment(1L, 1L, false);
        verify(loadAdditionalInfoEntityDao, times(1)).saveOrUpdate(Matchers.eq(additionalInfoEntity));
        verify(shipmentDocumentService, times(1)).generateShipmentDocuments(
                    Mockito.eq(Collections.singleton(DocumentTypes.CONSIGNEE_INVOICE)),
                    Mockito.eq(loadEntity),
                    Mockito.eq(false),
                    Mockito.eq(SecurityUtils.getCurrentPersonId())
               );
    }

    @Test
    public void shouldFindMatchedLoadsByProAndOrgId() {
        String proNum = "__    __000aBc12`~!@@#$%^&*((()))____  -+ =|\\}]{[<,>.?/;:300|\\}]{[<,>.?/;:4*()_5";
        Long orgId = 1L;

        when(ltlShipmentDao.findMatchedLoadsByProAndOrgId(Matchers.anyString(), Matchers.eq(orgId))).thenReturn(Collections.emptyList());
        service.getMatchedLoadsByProAndOrgId(proNum, orgId);
        verify(ltlShipmentDao, times(1)).findMatchedLoadsByProAndOrgId(Matchers.eq("aBc1230045"), Matchers.eq(orgId));
    }

    @Test
    public void shouldGetShipmentPricingDetails() {
        LoadPricingDetailsEntity pricDtl = new LoadPricingDetailsEntity();

        when(loadPricingDetailsDao.getShipmentPricingDetails(1L)).thenReturn(pricDtl);

        LoadPricingDetailsEntity result = service.getShipmentPricingDetails(1L);
        assertSame(pricDtl, result);
    }
}
