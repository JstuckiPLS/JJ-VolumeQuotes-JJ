package com.pls.shipment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.OrganizationPricingDao;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Reasons;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.BillingAuditReasonCodeDao;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.audit.LoadFinancialStatusTrackingService;
import com.pls.shipment.service.billing.audit.SidHarveyAuditReasonService;
import com.pls.shipment.service.impl.BillingAuditServiceImpl;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;



/**
 * Tests for {@link BillingAuditServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class BillingAuditServiceImplTest {
    private static final String INVOICE_FAILED = "IF";

    @Mock
    private BillingAuditReasonsDao billingAuditReasonsDao;
    @Mock
    private BillingAuditReasonCodeDao billingAuditReasonCodeDao;
    @Mock
    private LtlShipmentDao ltlShipmentDao;
    @Mock
    private ShipmentEmailSender shipmentEmailSender;
    @Mock
    private OrganizationPricingDao organizationPricingDao;
    @Mock
    private SidHarveyAuditReasonService sidHarveyAuditReasonService;
    @Mock
    private BillingAuditReasonCodeDao reasonCodeDao;
    @Mock
    private LoadFinancialStatusTrackingService loadTrackingService;

    @InjectMocks
    private BillingAuditServiceImpl billingAuditService;

    @Test
    public void shouldUpdateBillingAuditReasonForLoad() {
        LoadEntity load = getLoad();
        List<Reasons> sidHarveyReasons = new ArrayList<Reasons>();
        sidHarveyReasons.add(Reasons.HIGH_COST_REVIEW);

        Mockito.when(sidHarveyAuditReasonService.getBillingAuditReasonForSidHarvey(load)).thenReturn(sidHarveyReasons);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.COST_DIFF.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.LOW_MARGIN.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.MISSING_DOCUMENT.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.INVOICE_AUDIT.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.HIGH_COST_REVIEW.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).createAndSave(Reasons.MISSING_PAYMENTS_TERM.getReasonCode(), load.getId(), null, null);
        Mockito.verify(billingAuditReasonsDao).update(Mockito.argThat(new ArgumentMatcher<LdBillingAuditReasonsEntity>() {
            @Override
            public boolean matches(Object argument) {
                LdBillingAuditReasonsEntity reasonsEntity = (LdBillingAuditReasonsEntity) argument;
                return reasonsEntity.getStatus() == Status.INACTIVE
                        && Reasons.HIGH_REVENUE_REVIEW.getReasonCode().equals(reasonsEntity.getReasonCd());
            }
        }));

        Mockito.verifyNoMoreInteractions(billingAuditReasonsDao);
    }

    @Test
    public void shouldSendIfNesessaryAuditReasonEmailToAccountExecutive() {
        LoadEntity load = getLoad();
        int times = 0;
        billingAuditService.updateBillingAuditReasonForLoad(load, load.getFinalizationStatus());
        Mockito.verify(shipmentEmailSender, Mockito.times(times)).sendLoadReasonsEmail(Mockito.<LoadEntity>any(),
                Mockito.<List<LdBillAuditReasonCodeEntity>>any());

        BillToEntity newBillTo = load.getBillTo();
        newBillTo.setEmailAccountExecutive(false);
        load.setBillTo(newBillTo);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.NONE);
        Mockito.verify(shipmentEmailSender, Mockito.times(times)).sendLoadReasonsEmail(Mockito.<LoadEntity>any(),
                Mockito.<List<LdBillAuditReasonCodeEntity>>any());

        newBillTo.setEmailAccountExecutive(true);
        load.setBillTo(newBillTo);
        Set<LdBillingAuditReasonsEntity> reasons = new HashSet<LdBillingAuditReasonsEntity>();
        reasons.add(getReason(Reasons.MISSING_DOCUMENT));
        load.setBillingAuditReasons(reasons);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.NONE);
        Mockito.verify(shipmentEmailSender, Mockito.times(++times)).sendLoadReasonsEmail(Mockito.<LoadEntity>any(),
                Mockito.<List<LdBillAuditReasonCodeEntity>>any());

        newBillTo.setEmailAccountExecutive(true);
        load.setBillTo(newBillTo);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.NONE);
        Mockito.verify(shipmentEmailSender, Mockito.times(++times)).sendLoadReasonsEmail(Mockito.<LoadEntity>any(),
                Mockito.<List<LdBillAuditReasonCodeEntity>>any());

        reasons.add(getReason(Reasons.COST_DIFF));
        load.setBillingAuditReasons(reasons);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.NONE);
        Mockito.verify(shipmentEmailSender, Mockito.times(++times)).sendLoadReasonsEmail(Mockito.<LoadEntity>any(),
                Mockito.<List<LdBillAuditReasonCodeEntity>>any());

    }


    private LoadEntity getLoad() {
        LoadEntity load = new LoadEntity();
        load.setId((long) (Math.random() * 100));
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        CustomerEntity organization = new CustomerEntity();
        organization.setId((long) (Math.random() * 100));
        load.setOrganization(organization);

        load.setBillingAuditReasons(new HashSet<LdBillingAuditReasonsEntity>());
        load.getBillingAuditReasons().add(getReason(INVOICE_FAILED));
        load.getBillingAuditReasons().add(getReason(Reasons.HIGH_REVENUE_REVIEW));

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        LoadCostDetailsEntity costDetails = Mockito.spy(new LoadCostDetailsEntity());
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setTotalCost(new BigDecimal(40));
        costDetails.setTotalRevenue(new BigDecimal(50));
        Mockito.when(costDetails.getMargin()).thenReturn(new BigDecimal(3));
        load.getCostDetails().add(costDetails);

        load.getVendorBillDetails().setCarrierInvoiceDetails(new ArrayList<CarrierInvoiceDetailsEntity>());
        CarrierInvoiceDetailsEntity invoice = new CarrierInvoiceDetailsEntity();
        invoice.setTotalCharges(new BigDecimal(30));
        load.getVendorBillDetails().getCarrierInvoiceDetails().add(invoice);

        BillToEntity billTo = new BillToEntity();
        BillToThresholdSettingsEntity thresholdSettings = new BillToThresholdSettingsEntity();
        billTo.setAuditPrefReq(true);
        thresholdSettings.setThreshold(BigDecimal.valueOf(1.99));
        thresholdSettings.setTotalRevenue(BigDecimal.valueOf(1500));
        thresholdSettings.setMargin(BigDecimal.valueOf(70));
        billTo.setBillToThresholdSettings(thresholdSettings);
        load.setBillTo(billTo);
        return load;
    }

    private LdBillingAuditReasonsEntity getReason(Reasons reason) {
        return getReason(reason.getReasonCode());
    }

    private LdBillingAuditReasonsEntity getReason(String reasonCode) {
        LdBillingAuditReasonsEntity reasonEntity = new LdBillingAuditReasonsEntity();
        reasonEntity.setStatus(Status.ACTIVE);
        reasonEntity.setReasonCd(reasonCode);
        LdBillAuditReasonCodeEntity billAuditReasonCode = new LdBillAuditReasonCodeEntity();
        billAuditReasonCode.setId(reasonCode);
        reasonEntity.setBillAuditReasonCodeEntity(billAuditReasonCode);
        return reasonEntity;
    }
}