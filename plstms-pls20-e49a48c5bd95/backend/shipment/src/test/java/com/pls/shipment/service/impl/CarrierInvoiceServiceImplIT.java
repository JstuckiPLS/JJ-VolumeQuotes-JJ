package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Reasons;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.CostDetailTransfeBO;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.ShipmentAlertService;

/**
 * Vendor Bill integration tests.
 *
 * @author Stas Norochevskiy
 */
public class CarrierInvoiceServiceImplIT extends BaseServiceITClass {

    @Autowired
    private CarrierInvoiceService carrierInvoiceService;

    @Autowired
    private LtlShipmentDao loadDao;

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("TAQI");
    }

    @Test
    public void shouldHoldLoadOnVendorBillsTabForSevenDays() throws Exception {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);
        load.setStatus(ShipmentStatus.IN_TRANSIT);

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        load.getOrigin().setDeparture(null);
        shipmentAlertService.processShipmentAlerts(load);
        flushAndClearSession();

        load = loadDao.find(testLoadId);
        Assert.assertEquals(1, load.getAlerts().size());
        Assert.assertNull(load.getOrigin().getDeparture());
        load.getOrigin().setDeparture(new Date());

        Date invoiceDate = DateUtils.addDays(new Date(), -1);
        // add vendor bill with date less than 7 days
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(ShipmentFinancialStatus.NONE, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertTrue(load.getBillingAuditReasons().isEmpty());
        Assert.assertEquals(ShipmentStatus.DELIVERED, load.getStatus());
    }

    @Test
    public void shouldMoveLoadToInvoiceAuditTabIfMoreThanSevenDays() throws Exception {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);
        load.getNumbers().setBolNumber("BOL");
        load.getNumbers().setTrailerNumber("tr");
        load.getNumbers().setPuNumber("pu");
        load.getNumbers().setSoNumber("SO");

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Date invoiceDate = DateUtils.addDays(new Date(), -8);
        // add vendor bill with date more than 7 days
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertEquals(1, load.getBillingAuditReasons().size());
        Assert.assertEquals(Reasons.COST_DIFF.getReasonCode(), load.getBillingAuditReasons().iterator().next().getReasonCd());
    }

    @Test
    public void shouldMoveLoadToTransactionalTabIfMoreThanSevenDays() throws Exception {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);
        load.getNumbers().setBolNumber("BOL");
        load.getNumbers().setTrailerNumber("tr");
        load.getNumbers().setPuNumber("pu");
        load.getNumbers().setSoNumber("SO");

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Date invoiceDate = DateUtils.addDays(new Date(), -8);
        // add vendor bill with date more than 7 days and no Audit Issues
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);
        carrierInvoice.setTotalCharges(new BigDecimal("78.00"));

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);


        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertTrue(load.getBillingAuditReasons().isEmpty());
    }

    @Test
    public void shouldSaveVendorBillWithMatchedLoadWhenOnTransactionalInvoices() {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);

        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertTrue(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillRecvDate());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillAmount());

        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertTrue(!load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillRecvDate());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillRecvFlag());
        long matchedLoadId = load.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next().getMatchedLoadId();
        Assert.assertEquals(testLoadId, matchedLoadId);
        MathContext mc = new MathContext(0, RoundingMode.HALF_UP);
        Assert.assertEquals(new BigDecimal("78.04", mc), load.getActiveCostDetail().getTotalCost());
    }

    @Test
    public void shouldDetachVendorBill() throws ApplicationException {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);

        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        Assert.assertEquals(0, load.getAlerts().size());
        load.getOrigin().setDeparture(null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(0, load.getAlerts().size());
        Assert.assertNull(load.getOrigin().getDeparture());
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertTrue(!load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillRecvDate());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertNotNull(load.getVendorBillDetails().getFrtBillRecvFlag());
        long matchedLoadId = load.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next().getMatchedLoadId();
        Assert.assertEquals(testLoadId, matchedLoadId);
        MathContext mc = new MathContext(0, RoundingMode.HALF_UP);
        Assert.assertEquals(new BigDecimal("78.04", mc), load.getActiveCostDetail().getTotalCost());

        carrierInvoiceService.detach(carrierInvoice.getMatchedLoadId(), load.getVersion());

        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(1, load.getAlerts().size());
        Assert.assertNull(load.getOrigin().getDeparture());
        Assert.assertEquals(ShipmentFinancialStatus.NONE, load.getFinalizationStatus());
        Assert.assertTrue(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillRecvDate());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertNull(load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertEquals(load.getVendorBillDetails().getFrtBillRecvFlag(), false);
        mc = new MathContext(0, RoundingMode.HALF_UP);
        Assert.assertEquals(new BigDecimal("78.04", mc), load.getActiveCostDetail().getTotalCost());
    }

    @Test
    public void applyVendorBillToLoadWithLessThanCostDifference() throws Exception {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);
        load.getNumbers().setBolNumber("BOL");
        load.getNumbers().setTrailerNumber("tr");
        load.getNumbers().setPuNumber("pu");
        load.getNumbers().setSoNumber("SO");

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        load.getActiveCostDetail().setTotalCost(new BigDecimal(77));
        loadDao.saveOrUpdate(load);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(new BigDecimal("77.00"), load.getActiveCostDetail().getTotalCost());

        Date invoiceDate = DateUtils.addDays(new Date(), -8);
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);
        carrierInvoice.setTotalCharges(new BigDecimal("78.00"));

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(new BigDecimal("78.00"), load.getActiveCostDetail().getTotalCost());
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertTrue(load.getBillingAuditReasons().isEmpty());
    }

    @Test
    public void applyVendorBillToLoadWitCostDifference() throws Exception {
        long testLoadId = 3573664L;
        Integer activeCostDetailVersion = null;
        Long activeCostDetailId = null;
        LoadEntity load = loadDao.find(testLoadId);
        load.getNumbers().setBolNumber("BOL");
        load.getNumbers().setTrailerNumber("tr");
        load.getNumbers().setPuNumber("pu");
        load.getNumbers().setSoNumber("SO");

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        load.getActiveCostDetail().setTotalCost(new BigDecimal("77.1"));
        loadDao.saveOrUpdate(load);
        flushAndClearSession();

        load = loadDao.find(testLoadId);
        activeCostDetailVersion = load.getActiveCostDetail().getVersion();
        activeCostDetailId = load.getActiveCostDetail().getId();
        Assert.assertEquals(new BigDecimal("77.10"), load.getActiveCostDetail().getTotalCost());

        Date invoiceDate = DateUtils.addDays(new Date(), -8);
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);
        carrierInvoice.setTotalCharges(new BigDecimal("77.10"));

        CostDetailTransfeBO result = carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(activeCostDetailVersion, load.getActiveCostDetail().getVersion());
        Assert.assertEquals(activeCostDetailId, load.getActiveCostDetail().getId());
        Assert.assertNull(result.getActiveCostDetailId());
        Assert.assertEquals(new BigDecimal("77.10"), load.getActiveCostDetail().getTotalCost());
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertTrue(load.getBillingAuditReasons().isEmpty());
    }

    @Test
    public void applyVendorBillToLoadWithMoreThanCostDifference() throws Exception {
        long testLoadId = 3573664L;
        LoadEntity load = loadDao.find(testLoadId);
        load.getNumbers().setBolNumber("BOL");
        load.getNumbers().setTrailerNumber("tr");
        load.getNumbers().setPuNumber("pu");
        load.getNumbers().setSoNumber("SO");

        // clean up load
        carrierInvoiceService.detach(load.getId(), load.getVersion());
        load.getActiveCostDetail().setTotalCost(new BigDecimal(77));
        loadDao.saveOrUpdate(load);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(new BigDecimal("77.00"), load.getActiveCostDetail().getTotalCost());

        Date invoiceDate = DateUtils.addDays(new Date(), -8);
        CarrierInvoiceDetailsEntity carrierInvoice = getCarrierInvoiceDetails(load);
        carrierInvoice.setInvoiceDate(invoiceDate);
        carrierInvoice.setTotalCharges(new BigDecimal("79.00"));

        carrierInvoiceService.saveVendorBillWithMatchedLoad(carrierInvoice, null);
        flushAndClearSession();

        load = loadDao.find(testLoadId);

        Assert.assertEquals(new BigDecimal("77.00"), load.getActiveCostDetail().getTotalCost());
        Assert.assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD, load.getFinalizationStatus());
        Assert.assertFalse(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        Assert.assertEquals(carrierInvoice.getInvoiceDate(), invoiceDate);
        Assert.assertEquals(carrierInvoice.getInvoiceNumber(), load.getVendorBillDetails().getFrtBillNumber());
        Assert.assertEquals(carrierInvoice.getTotalCharges(), load.getVendorBillDetails().getFrtBillAmount());
        Assert.assertTrue(load.getVendorBillDetails().getFrtBillRecvFlag());
        Assert.assertFalse(load.getBillingAuditReasons().isEmpty());
        Assert.assertEquals(load.getBillingAuditReasons().iterator().next().getReasonCd(), "CD");

    }

    private CarrierInvoiceDetailsEntity getCarrierInvoiceDetails(LoadEntity load) {
        CarrierInvoiceDetailsEntity carrierInvoice = new CarrierInvoiceDetailsEntity();

        CarrierEntity carrier = carrierDao.find(6L);

        BigDecimal estimationCost = load.getActiveCostDetail().getTotalCost();

        carrierInvoice.setBolNumber(load.getNumbers().getBolNumber());
        carrierInvoice.setCarrier(carrier);
        carrierInvoice.setStatus(Status.ACTIVE);
        carrierInvoice.setInvoiceNumber("123");
        carrierInvoice.setInvoiceDate(new Date());

        // Making discrepancy higher than $5
        carrierInvoice.setNetAmount(estimationCost.add(new BigDecimal("18")));
        carrierInvoice.setTotalCharges(carrierInvoice.getNetAmount());
        carrierInvoice.setCarrierInvoiceCostItems(Collections.singleton(createCarrierInvoiceCostItemEntity("CRA",
                carrierInvoice.getNetAmount(), carrierInvoice)));
        carrierInvoice.setMatchedLoadId(3573664L);
        return carrierInvoice;
    }

    private CarrierInvoiceCostItemEntity createCarrierInvoiceCostItemEntity(String refType, BigDecimal subTotal,
                                                                            CarrierInvoiceDetailsEntity carrierInvoiceDetailsEntity) {
        CarrierInvoiceCostItemEntity costItemEntity = new CarrierInvoiceCostItemEntity();
        costItemEntity.setAccessorialType(refType);
        costItemEntity.setSubtotal(subTotal);
        costItemEntity.setCarrierInvoiceDetails(carrierInvoiceDetailsEntity);
        return costItemEntity;
    }
}
