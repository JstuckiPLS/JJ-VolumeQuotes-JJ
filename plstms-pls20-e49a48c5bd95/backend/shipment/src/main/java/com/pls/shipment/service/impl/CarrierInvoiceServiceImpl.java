package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.shipment.dao.CarrierInvoiceAddressDetailsDao;
import com.pls.shipment.dao.CarrierInvoiceCostItemDao;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.dao.CarrierInvoiceDetailsReasonLinksDao;
import com.pls.shipment.dao.CarrierInvoiceDetailsReasonsDao;
import com.pls.shipment.dao.CarrierInvoiceLineItemDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailReasonLinksEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailReasonsEntity;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ReasonBO;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;
import com.pls.shipment.domain.bo.CostDetailTransfeBO;
import com.pls.shipment.service.CarrierInvoiceService;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.ShipmentUtils;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * Implementation of {@link CarrierInvoiceService}.
 *
 * @author Mikhail Boldinov, 01/10/13
 */
@Service
@Transactional
public class CarrierInvoiceServiceImpl implements CarrierInvoiceService {

    @Autowired
    private CarrierInvoiceDetailsDao dao;

    @Autowired
    private LtlShipmentDao loadDao;

    @Autowired
    private CarrierInvoiceAddressDetailsDao carrierInvoiceAddressDetailsDao;

    @Autowired
    private CarrierInvoiceLineItemDao carrierInvoiceLineItemDao;

    @Autowired
    private CarrierInvoiceCostItemDao carrierInvoiceCostItemDao;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private LoadDocumentDao documentDao;

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private CarrierInvoiceDetailsReasonLinksDao reasonLinksDao;

    @Autowired
    private CarrierInvoiceDetailsReasonsDao reasonsDao;

    @Override
    public List<CarrierInvoiceDetailsListItemBO> getUnmatched() {
        return dao.getUnmatched();
    }

    @Override
    public List<CarrierInvoiceDetailsListItemBO> getArchived() {
        return dao.getArchived();
    }

    @Override
    public void archive(Long carrierInvoiceId) {
        dao.updateStatus(carrierInvoiceId, Status.INACTIVE, new Date(), SecurityUtils.getCurrentPersonId());
    }

    @Override
    public void archive(ReasonBO reasonBO) {
        CarrierInvoiceDetailReasonsEntity reason = new CarrierInvoiceDetailReasonsEntity();
        reason.setLoadId(reasonBO.getLoadId());
        reason.setNote(reasonBO.getNote());
        reason.setReasonCode(reasonBO.getReason());
        List<CarrierInvoiceDetailsEntity> carrierInvoices = dao.getAll(reasonBO.getVendorBills());

        List<CarrierInvoiceDetailReasonLinksEntity> links = new ArrayList<CarrierInvoiceDetailReasonLinksEntity>();
        for (CarrierInvoiceDetailsEntity vb : carrierInvoices) {
            vb.setStatus(Status.INACTIVE);
            if (vb.getReasonLink() != null) {
                vb.getReasonLink().setStatus(Status.INACTIVE);
            }
            CarrierInvoiceDetailReasonLinksEntity link = new CarrierInvoiceDetailReasonLinksEntity();
            link.setStatus(Status.ACTIVE);
            link.setReason(reason);
            link.setCarrierInvoiceDetails(vb);
            links.add(link);
        }
        reasonsDao.saveOrUpdate(reason);
        reasonLinksDao.saveOrUpdateBatch(links);
    }

    @Override
    public void unArchive(Long carrierInvoiceId) {
        dao.updateStatus(carrierInvoiceId, Status.ACTIVE, new Date(), SecurityUtils.getCurrentPersonId());
    }

    @Override
    public CarrierInvoiceDetailsEntity getById(Long id) {
        return dao.find(id);
    }

    @Override
    public CostDetailTransfeBO saveVendorBillWithMatchedLoad(CarrierInvoiceDetailsEntity newVendorBill, Integer loadVersion) {
        dao.saveOrUpdate(newVendorBill);

        LoadEntity load = loadDao.find(newVendorBill.getMatchedLoadId());

        // Set version of load for optimistic concurrency control
        if (loadVersion != null) {
            load.setVersion(loadVersion);
        }
        updateVendorBillsStatus(load.getVendorBillDetails().getCarrierInvoiceDetails(), newVendorBill.getId());

        load.getVendorBillDetails().setFrtBillNumber(newVendorBill.getInvoiceNumber());
        load.getVendorBillDetails().setFrtBillAmount(newVendorBill.getTotalCharges());
        load.getVendorBillDetails().setFrtBillRecvFlag(true);
        Long frtBillRecvBy = SecurityUtils.getCurrentPersonId() != null ? SecurityUtils.getCurrentPersonId() : newVendorBill.getModification()
                .getCreatedBy();
        load.getVendorBillDetails().setFrtBillRecvBy(frtBillRecvBy);
        load.getNumbers().setProNumber(newVendorBill.getProNumber());

        // we can't rely on flushing mechanism, sometimes Vendor Bills is not populated at this time
        if (load.getVendorBillDetails().getCarrierInvoiceDetails().stream().noneMatch(i -> ObjectUtils.equals(newVendorBill.getId(), i.getId()))) {
            load.getVendorBillDetails().getCarrierInvoiceDetails().add(newVendorBill);
        }

        updateDatesForLoad(load, newVendorBill);
        ShipmentStatus oldStatus = load.getStatus();
        updateStatusForLoad(load);

        if (load.getFinalizationStatus() == ShipmentFinancialStatus.NONE && isVendorBillAttachedOverThreeDays(newVendorBill)) {
            load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        }
        boolean isTotalCostUpdated = updateCostDependingOnThreshold(newVendorBill, load);
        billingAuditService.updateBillingAuditReasonForLoad(load, load.getFinalizationStatus());
        loadDao.saveOrUpdate(load);
        shipmentAlertService.processShipmentAlerts(load); // we need to remove all outdated alerts after shipment is changed
        shipmentEmailSender.sendGoShipTrackingUpdateEmail(load, oldStatus);
        if (isTotalCostUpdated) {
            return new CostDetailTransfeBO(load.getVersion(), load.getActiveCostDetail().getId(),
                    load.getActiveCostDetail().getCostDetailItems());
        } else {
            return new CostDetailTransfeBO(load.getVersion(), null, Collections.emptySet());
        }
    }

    private boolean updateCostDependingOnThreshold(CarrierInvoiceDetailsEntity vendorBill, LoadEntity load) {
        BigDecimal threshold = load.getBillTo() != null && load.getBillTo().getBillToThresholdSettings() != null
                ? load.getBillTo().getBillToThresholdSettings().getThreshold()
                : BillingAuditServiceImpl.DEFAULT_THRESHOLD;
        BigDecimal vendorBillCost = vendorBill.getTotalCharges() != null ? vendorBill.getTotalCharges()
                : BigDecimal.ZERO;
        BigDecimal loadCost = load.getActiveCostDetail().getTotalCost();
        BigDecimal discrepancy = loadCost.subtract(vendorBillCost);
        if (BigDecimal.ZERO.compareTo(discrepancy) != 0 && discrepancy.abs().compareTo(threshold) < 0) {
            LoadCostDetailsEntity newActiveCostDetails = getNewActiveCostDetail(load, discrepancy);
            load.getActiveCostDetail().setStatus(Status.INACTIVE);
            load.getCostDetails().add(newActiveCostDetails);
            loadDao.saveOrUpdate(load);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private LoadCostDetailsEntity getNewActiveCostDetail(LoadEntity load, BigDecimal discrepancy) {
        LoadCostDetailsEntity costDetail = load.getActiveCostDetail().copy();
        createOrUpdateBaseRate(costDetail, discrepancy, load);
        return costDetail;
    }

    private void createOrUpdateBaseRate(LoadCostDetailsEntity activeCost, BigDecimal discrepancy, LoadEntity load) {
        Set<CostDetailItemEntity> costItems = activeCost.getCostDetailItems();
        for (CostDetailItemEntity costDetailItemEntity : costItems) {
            if (CostDetailOwner.C.equals(costDetailItemEntity.getOwner())
                    && "CRA".equals(costDetailItemEntity.getAccessorialType())) {
                BigDecimal cost = costDetailItemEntity.getSubtotal().subtract(discrepancy);
                costDetailItemEntity.setSubtotal(cost);
                costDetailItemEntity.setUnitCost(cost);
                activeCost.setTotalCost(activeCost.getTotalCost().subtract(discrepancy));
                return;
            }
        }
        CostDetailItemEntity baseRate = new CostDetailItemEntity();
        baseRate.setAccessorialType("CRA");
        baseRate.setCarrierId(load.getCarriedId());
        baseRate.setOwner(CostDetailOwner.C);
        baseRate.setBillTo(load.getBillTo());
        baseRate.setCostDetails(activeCost);
        baseRate.setSubtotal(discrepancy.negate());
        baseRate.setUnitCost(discrepancy.negate());
        activeCost.setTotalCost(activeCost.getTotalCost().subtract(discrepancy));
        costItems.add(baseRate);
    }

    private void updateDatesForLoad(LoadEntity load, CarrierInvoiceDetailsEntity newVendorBill) {
        if (load.getDestination().getDeparture() == null && isValidActualDeliveryDate(newVendorBill.getDeliveryDate())) {
            load.getDestination().setDeparture(newVendorBill.getDeliveryDate());
        }
        if (load.getDestination().getScheduledArrival() == null) {
            load.getDestination().setScheduledArrival(
                    newVendorBill.getEstDeliveryDate() == null ? load.getDestination().getDeparture() : newVendorBill.getEstDeliveryDate());
        }
        if (load.getOrigin().getDeparture() == null) {
            load.getOrigin().setDeparture(newVendorBill.getActualPickupDate());
        }
        if (load.getOrigin().getEarlyScheduledArrival() == null) {
            load.getOrigin().setEarlyScheduledArrival(load.getOrigin().getDeparture());
        }
        updateFreightBillDate(load);
    }

    private boolean isValidActualDeliveryDate(Date deliveryDate) {
        return deliveryDate != null && deliveryDate.toInstant().truncatedTo(ChronoUnit.DAYS).isBefore(Instant.now());
    }

    @Override
    public void updateFreightBillDate(LoadEntity load) {
        if (load.getVendorBillDetails().getFrtBillRecvDate() == null && ShipmentUtils.isCanUpdateFrtBillDate(load)) {
            List<Date> dates = new ArrayList<Date>();
            dates.addAll(documentDao.findCreatedDatesForReqDocsByLoadId(load.getId()));
            dates.add(load.getVendorBillDetails().getCarrierInvoiceDetails().stream().filter(i -> i.getStatus() == Status.ACTIVE).findFirst()
                    .get().getInvoiceDate());
            dates.add(load.getDestination().getDeparture());
            load.getVendorBillDetails().setFrtBillRecvDate(Collections.max(dates));
        }
    }

    @Override
    public void match(Long carrierInvoiceDetailsId, Long shipmentId) {
        CarrierInvoiceDetailsEntity vendorBill = dao.find(carrierInvoiceDetailsId);
        vendorBill.setMatchedLoadId(shipmentId);
        saveVendorBillWithMatchedLoad(vendorBill, null);
    }

    @Override
    public void detach(Long loadId, Integer loadVersion) throws ApplicationException {
        // update load
        LoadEntity load = loadDao.find(loadId);
        updateVendorBillsStatus(load.getVendorBillDetails().getCarrierInvoiceDetails(), null);
        load.getVendorBillDetails().setFrtBillNumber(null);
        load.getVendorBillDetails().setFrtBillRecvDate(null);
        load.getVendorBillDetails().setFrtBillAmount(null);
        load.getVendorBillDetails().setFrtBillRecvFlag(false);
        load.getVendorBillDetails().setFrtBillRecvBy(null);
        load.setFinalizationStatus(ShipmentFinancialStatus.NONE);
        load.setVersion(loadVersion);

        load = loadDao.saveOrUpdate(load);
        shipmentAlertService.processShipmentAlerts(load);

        // update vendor bills created via EDI
        List<CarrierInvoiceDetailsEntity> vendorBills = dao.getEDIVendorBillsForLoad(loadId);
        if (!vendorBills.isEmpty()) {
            for (CarrierInvoiceDetailsEntity carrierInvoiceDetailsEntity : vendorBills) {
                dao.updateStatusAndMatchedLoad(carrierInvoiceDetailsEntity.getId(), Status.ACTIVE, null);
            }
        }
    }

    @Override
    public CarrierInvoiceAddressDetailsEntity getCarrierInvoiceAddressDetailsEntityById(Long id) {
        return carrierInvoiceAddressDetailsDao.find(id);
    }

    @Override
    public CarrierInvoiceDetailsEntity getForShipment(Long shipmentId) {
        LoadEntity load = loadDao.find(shipmentId);
        if (load != null && !load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty()) {
            return load.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next();
        }
        return null;
    }

    @Override
    public CarrierInvoiceLineItemEntity getCarrierInvoiceLineItemById(Long id) {
        return carrierInvoiceLineItemDao.find(id);
    }

    @Override
    public CarrierInvoiceCostItemEntity getCarrierInvoiceCostItemById(Long id) {
        return carrierInvoiceCostItemDao.find(id);
    }

    @Override
    public void archiveOldUnmatched() {
        dao.archiveOldUnmatched();
    }

    private boolean isVendorBillAttachedOverThreeDays(CarrierInvoiceDetailsEntity vendorBill) {
        if (vendorBill.getInvoiceDate() == null) {
            return false;
        }
        ZonedDateTime curentDay = ZonedDateTime.now();
        ZonedDateTime invoiceDate = ZonedDateTime.ofInstant(vendorBill.getInvoiceDate().toInstant(), ZoneOffset.UTC);
        long diffDays = Duration.between(invoiceDate, curentDay).toDays();
        return diffDays > 3;
    }

    private void updateVendorBillsStatus(List<CarrierInvoiceDetailsEntity> vendorBills, Long vendorBillIdToSkip) {
        if (!vendorBills.isEmpty()) {
            for (CarrierInvoiceDetailsEntity carrierInvoiceDetailsEntity : vendorBills) {
                if (ObjectUtils.notEqual(vendorBillIdToSkip, carrierInvoiceDetailsEntity.getId())) {
                    carrierInvoiceDetailsEntity.setStatus(Status.INACTIVE);
                    dao.saveOrUpdate(carrierInvoiceDetailsEntity);
                }
            }
        }
    }

    private void updateStatusForLoad(LoadEntity load) {
        if (load.getDestination().getDeparture() != null) {
            load.setStatus(ShipmentStatus.DELIVERED);
        } else if (load.getDestination().getScheduledArrival() != null) {
            load.setStatus(ShipmentStatus.IN_TRANSIT);
        }
    }
}
