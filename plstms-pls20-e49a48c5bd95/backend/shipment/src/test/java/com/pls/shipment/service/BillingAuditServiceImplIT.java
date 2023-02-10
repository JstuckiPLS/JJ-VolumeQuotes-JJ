package com.pls.shipment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.DefaultValuesAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.RequiredFieldPointType;
import com.pls.core.domain.enums.RequiredFieldShipmentDirection;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.BillToRequiredField;
import com.pls.core.shared.DisputeCost;
import com.pls.core.shared.Reasons;
import com.pls.core.shared.Status;
import com.pls.core.shared.UpdateRevenueOption;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LdBillingAuditReasonsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.impl.BillingAuditService;
import com.pls.shipment.service.impl.BillingAuditServiceImpl;

/**
 * Integration test for {@link BillingAuditServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
public class BillingAuditServiceImplIT extends BaseServiceITClass {
    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private BillingAuditReasonsDao auditReasonsDao;

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("test", 2L, 1L);
    }

    @Autowired
    private CarrierDao carrierDao;

    @Test
    public void shouldUpdateDisputeAndAuditReasonsForLoad() {
        LoadEntity load = shipmentService.findById(1L);

        AuditShipmentCostDetailsEntity additionalDetails = new AuditShipmentCostDetailsEntity();
        additionalDetails.setDisputeCost(DisputeCost.ACCOUNT_EXEC);
        additionalDetails.setRequestPaperwork(Boolean.TRUE);
        load.getActiveCostDetail().setAuditShipmentCostDetails(additionalDetails);
        additionalDetails.setLoadCostDetail(load.getActiveCostDetail());
        save(load.getActiveCostDetail());
        flushAndClearSession();
        load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        load.getActiveCostDetail().getAuditShipmentCostDetails().setUpdateRevenue(UpdateRevenueOption.MARGIN_PERCENT);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        AuditShipmentCostDetailsEntity carrierInvoiceAddDetails = load.getActiveCostDetail()
                .getAuditShipmentCostDetails();

        Assert.assertNotNull(carrierInvoiceAddDetails);
        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertFalse(auditReasons.isEmpty());
        Assert.assertEquals(auditReasons.size(), 4);
        Iterator<LdBillingAuditReasonsEntity> iterator = auditReasons.iterator();
        LdBillingAuditReasonsEntity auditReason = iterator.next();
        Assert.assertEquals("DA", auditReason.getReasonCd());
        auditReason = iterator.next();
        Assert.assertEquals("RP", auditReason.getReasonCd());
        auditReason = iterator.next();
        Assert.assertEquals("RC", auditReason.getReasonCd());
        auditReason = iterator.next();
        Assert.assertEquals("MD", auditReason.getReasonCd());
    }

    @Test
    public void testUpdateBillingAuditReasonForLoadWithoutReqDoc() {
        LoadEntity load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        Set<LdBillingAuditReasonsEntity> billingAuditReasons = load.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertTrue(billingAuditReasons.isEmpty());

        load.setCustReqDocPresent(false);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertFalse(auditReasons.isEmpty());
        Assert.assertEquals(auditReasons.size(), 1);
        Assert.assertEquals("MD", auditReasons.iterator().next().getReasonCd());
        Assert.assertEquals(Status.ACTIVE, auditReasons.iterator().next().getStatus());
        Assert.assertEquals(1, (long) auditReasons.iterator().next().getLoadId());
    }

    @Test
    public void testUpdateBillingAuditReasonForLoadWithAllReasons() {
        LoadEntity load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        Set<LdBillingAuditReasonsEntity> billingAuditReasons = load.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertTrue(billingAuditReasons.isEmpty());

        load.setCustReqDocPresent(false);
        load.getVendorBillDetails().setCarrierInvoiceDetails(buildCarrierInvoiceDetailsEntity());
        getSession().flush();
        load = shipmentService.findById(1L);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        getSession().flush();

        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertFalse(auditReasons.isEmpty());
        Assert.assertEquals(auditReasons.size(), 1);
        Iterator<LdBillingAuditReasonsEntity> iterator = auditReasons.iterator();
        LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity = iterator.next();
        Assert.assertEquals("MD", ldBillingAuditReasonsEntity.getReasonCd());
    }

    @Test
    public void testUpdateBillingAuditReasonForLoadWithReqDoc() {
        LoadEntity load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        Set<LdBillingAuditReasonsEntity> billingAuditReasons = load.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertTrue(billingAuditReasons.isEmpty());

        load.setCustReqDocPresent(false);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 1);
        Assert.assertEquals("MD", auditReasons.iterator().next().getReasonCd());

        getSession().refresh("LoadEntity", load);
        getSession().refresh("LdBillingAuditReasonsEntity", load.getBillingAuditReasons().iterator().next());
        load.setCustReqDocPresent(true);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 0);
    }

    @Test
    public void testUpdateBillingAuditReasonForLoadWithReqDoc1() {
        LoadEntity load = shipmentService.findById(1L);
        load.getNumbers().setBolNumber("BOL");
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        Set<LdBillingAuditReasonsEntity> billingAuditReasons = load.getBillingAuditReasons();
        Assert.assertNotNull(billingAuditReasons);
        Assert.assertTrue(billingAuditReasons.isEmpty());

        load.setCustReqDocPresent(false);
        getSession().flush();
        load = shipmentService.findById(1L);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 1);
        Assert.assertEquals("MD", auditReasons.iterator().next().getReasonCd());

        load.setCustReqDocPresent(true);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        flushAndClearSession();
        load = shipmentService.findById(1L);
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertTrue(load.getBillingAuditReasons().isEmpty()); // TODO correct wrong size of reasons list with null value
    }

    @Test
    public void testLowMargin() throws ApplicationException {
        Long testLoadId = 3573664L;
        LoadEntity load = shipmentService.findById(testLoadId);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        Set<LoadCostDetailsEntity> activeCostDetails = load.getActiveCostDetails();
        LoadCostDetailsEntity cost = activeCostDetails.iterator().next();
        cost.setTotalCost(new BigDecimal(300));
        getSession().saveOrUpdate(cost);
        getSession().saveOrUpdate(load);

        flushAndClearSession();

        load = shipmentService.findById(testLoadId);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        flushAndClearSession();

        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(testLoadId);
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 1);
        LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) auditReasons.iterator()
                .next();
        Assert.assertEquals(Reasons.LOW_MARGIN.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
    }

    @Test
    public void tesRequiredtInvoiceAudit() {
        Long testLoadId = 3573664L;
        LoadEntity load = shipmentService.findById(testLoadId);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        load.getBillTo().setAuditPrefReq(true);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        List<LdBillingAuditReasonsEntity> reasons = getAuditReasonsForLoad(testLoadId);
        LdBillingAuditReasonsEntity reasonsEntity = (LdBillingAuditReasonsEntity) reasons.iterator().next();
        Assert.assertNotNull(reasons);
        Assert.assertEquals(reasons.size(), 1);
        Assert.assertEquals(Reasons.INVOICE_AUDIT.getReasonCode(), reasonsEntity.getReasonCd());
        Assert.assertEquals(Status.ACTIVE, reasons.iterator().next().getStatus());
        Assert.assertEquals(testLoadId, Long.valueOf(reasons.iterator().next().getLoadId()));
    }

    @Test
    public void testCostDiff() throws ApplicationException {
        LoadEntity load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);

        Set<LoadCostDetailsEntity> activeCostDetails = load.getActiveCostDetails();
        LoadCostDetailsEntity cost = activeCostDetails.iterator().next();
        cost.setTotalCost(new BigDecimal(86));
        List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails = buildCarrierInvoiceDetailsEntity();
        CarrierInvoiceDetailsEntity vendorBill = carrierInvoiceDetails.iterator().next();
        load.getVendorBillDetails().setCarrierInvoiceDetails(carrierInvoiceDetails);
        load.setCustReqDocPresent(true);

        getSession().saveOrUpdate(vendorBill);
        getSession().saveOrUpdate(cost);
        getSession().saveOrUpdate(load);

        flushAndClearSession();

        load = shipmentService.findById(1L);
        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        flushAndClearSession();

        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 1);
        LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) auditReasons.iterator()
                .next();
        Assert.assertEquals(Reasons.COST_DIFF.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
    }

    private List<LdBillingAuditReasonsEntity> getAuditReasonsForLoad(Long loadId) {
        List<LdBillingAuditReasonsEntity> auditReasonsForLoad = new ArrayList<LdBillingAuditReasonsEntity>();
        List<LdBillingAuditReasonsEntity> auditReasons = auditReasonsDao.getAll();
        for (LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity : auditReasons) {
            if (loadId.equals(ldBillingAuditReasonsEntity.getLoadId())
                    && Status.ACTIVE.equals(ldBillingAuditReasonsEntity.getStatus())) {
                auditReasonsForLoad.add(ldBillingAuditReasonsEntity);
            }
        }
        return auditReasonsForLoad;
    }

    @Test
    public void testRequiredIdentifierReason() throws ApplicationException {
        CreateMatchedRules();
        flushAndClearSession();
        LoadEntity load = shipmentService.findById(1L);
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        load.setCustReqDocPresent(true);

        billingAuditService.updateBillingAuditReasonForLoad(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        flushAndClearSession();

        List<LdBillingAuditReasonsEntity> auditReasons = getAuditReasonsForLoad(1L);
        auditReasons.sort((r1, r2) -> r1.getId().compareTo(r2.getId()));
        Assert.assertNotNull(auditReasons);
        Assert.assertEquals(auditReasons.size(), 5);
        Iterator<LdBillingAuditReasonsEntity> iterator = auditReasons.iterator();
        LdBillingAuditReasonsEntity ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) iterator.next();
        Assert.assertEquals(Reasons.INCORRECT_BOL_NUMBER.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
        ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) iterator.next();
        Assert.assertEquals(Reasons.INCORRECT_GL_NUMBER.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
        ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) iterator.next();
        Assert.assertEquals(Reasons.INCORRECT_JOB_NUMBER.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
        ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) iterator.next();
        Assert.assertEquals(Reasons.INCORRECT_CARGO_VALUE.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
        ldBillingAuditReasonsEntity = (LdBillingAuditReasonsEntity) iterator.next();
        Assert.assertEquals(Reasons.INCORRECT_REQUESTED_BY.getReasonCode(), ldBillingAuditReasonsEntity.getReasonCd());
    }

    private void CreateMatchedRules() {
        BillToEntity billTo = new BillToEntity();
        billTo.setId(1L);

        Map<BillToRequiredField, BillToRequiredFieldEntity> result = new HashMap<BillToRequiredField, BillToRequiredFieldEntity>();
        BillToRequiredFieldEntity reqFields = new BillToRequiredFieldEntity();
        reqFields.setDefaultValue("BOL BOL");
        reqFields.setBillTo(billTo);
        reqFields.setStatus(Status.ACTIVE);
        reqFields.setRequired(true);
        reqFields.setActionForDefaultValues(DefaultValuesAction.AUDIT);
        reqFields.setAddressDirection(RequiredFieldPointType.BOTH);
        reqFields.setFieldName(BillToRequiredField.BOL);
        reqFields.setShipmentDirection(RequiredFieldShipmentDirection.BOTH);
        result.put(BillToRequiredField.BOL, reqFields);
        save(reqFields);

        reqFields = new BillToRequiredFieldEntity();
        reqFields.setBillTo(billTo);
        reqFields.setStatus(Status.ACTIVE);
        reqFields.setDefaultValue("CARGO CARGO");
        reqFields.setActionForDefaultValues(DefaultValuesAction.AUDIT);
        reqFields.setAddressDirection(RequiredFieldPointType.DESTINATION);
        reqFields.setRequired(true);
        reqFields.setFieldName(BillToRequiredField.CARGO);
        reqFields.setShipmentDirection(RequiredFieldShipmentDirection.BOTH);
        result.put(BillToRequiredField.CARGO, reqFields);
        save(reqFields);

        reqFields = new BillToRequiredFieldEntity();
        reqFields.setBillTo(billTo);
        reqFields.setStatus(Status.ACTIVE);
        reqFields.setDefaultValue("GL GL");
        reqFields.setActionForDefaultValues(DefaultValuesAction.AUDIT);
        reqFields.setAddressDirection(RequiredFieldPointType.ORIGIN);
        reqFields.setRequired(true);
        reqFields.setFieldName(BillToRequiredField.GL);
        reqFields.setShipmentDirection(RequiredFieldShipmentDirection.BOTH);
        result.put(BillToRequiredField.GL, reqFields);
        save(reqFields);

        reqFields = new BillToRequiredFieldEntity();
        reqFields.setBillTo(billTo);
        reqFields.setDefaultValue("REQUESTED_BY");
        reqFields.setRequired(true);
        reqFields.setStatus(Status.ACTIVE);
        reqFields.setActionForDefaultValues(DefaultValuesAction.AUDIT);
        reqFields.setAddressDirection(RequiredFieldPointType.BOTH);
        reqFields.setFieldName(BillToRequiredField.REQUESTED_BY);
        reqFields.setShipmentDirection(RequiredFieldShipmentDirection.BOTH);
        result.put(BillToRequiredField.REQUESTED_BY, reqFields);
        save(reqFields);

        reqFields = new BillToRequiredFieldEntity();
        reqFields.setBillTo(billTo);
        reqFields.setRequired(true);
        reqFields.setStatus(Status.ACTIVE);
        reqFields.setDefaultValue("JOB");
        reqFields.setActionForDefaultValues(DefaultValuesAction.AUDIT);
        reqFields.setAddressDirection(RequiredFieldPointType.BOTH);
        reqFields.setFieldName(BillToRequiredField.JOB);
        reqFields.setShipmentDirection(RequiredFieldShipmentDirection.BOTH);
        result.put(BillToRequiredField.JOB, reqFields);
        save(reqFields);
    }

    private List<CarrierInvoiceDetailsEntity> buildCarrierInvoiceDetailsEntity() {
        List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails = new ArrayList<CarrierInvoiceDetailsEntity>();
        CarrierInvoiceDetailsEntity entity = new CarrierInvoiceDetailsEntity();
        entity.setCarrier(getCarrier());
        entity.setInvoiceNumber("INV#1");
        entity.setInvoiceDate(new Date());
        entity.setReferenceNumber("REF#1");
        entity.setPaymentTerms(PaymentTerms.COLLECT);
        entity.setNetAmount(new BigDecimal(100.42));
        entity.setDeliveryDate(new Date());
        entity.setEstDeliveryDate(new Date());
        entity.setBolNumber("BOL1");
        entity.setPoNumber("PO1");
        entity.setShipperRefNumber("SHIP_NUM");
        entity.setProNumber("PRO_NUM");
        entity.setActualPickupDate(new Date());
        entity.setTotalWeight(new BigDecimal("123.45"));
        entity.setTotalCharges(new BigDecimal("80"));
        entity.setTotalQuantity(437);
        entity.setStatus(Status.ACTIVE);
        entity.setEdi(Boolean.TRUE);
        entity.setMatchedLoadId(1L);
        carrierInvoiceDetails.add(entity);
        return carrierInvoiceDetails;
    }

    private CarrierEntity getCarrier() {
        return carrierDao.find(58L);
    }
}