package com.pls.invoice.service.impl.processing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.CbiInvoiceType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.shared.Reasons;
import com.pls.invoice.dao.BillingStatusReasonDao;
import com.pls.invoice.dao.FinanIntResponsesDao;
import com.pls.invoice.dao.FinancialInvoiceDao;
import com.pls.invoice.dao.InvoiceToAXDao;
import com.pls.invoice.dao.LoadBillingHistoryDao;
import com.pls.invoice.domain.BillingStatusReasonEntity;
import com.pls.invoice.domain.FinancialInvoiceHistoryEntity;
import com.pls.invoice.domain.LoadBillingHistoryEntity;
import com.pls.invoice.domain.bo.InvoiceProcessingBO;
import com.pls.invoice.domain.bo.InvoiceProcessingItemBO;
import com.pls.invoice.domain.bo.SendToFinanceBO;
import com.pls.invoice.domain.bo.enums.InvoiceErrorCode;
import com.pls.invoice.domain.bo.enums.InvoiceReleaseStatus;
import com.pls.invoice.domain.xml.finance.FinanceInfoTable;
import com.pls.invoice.service.processing.FinancialInvoiceProcessingService;
import com.pls.shipment.dao.BillingAuditReasonsDao;
import com.pls.shipment.dao.FinanRequestsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.FinanRequestsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventDataEntity;
import com.pls.shipment.domain.LoadEventDataPK;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.impl.BillingAuditService;

/**
 * Implementation of {@link FinancialInvoiceProcessingService}.
 *
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class FinancialInvoiceProcessingServiceImpl implements FinancialInvoiceProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FinancialInvoiceProcessingServiceImpl.class);

    static final String CBI_CODE_PREFIX = "C-";
    static final String GENERATED_CBI_CODE_FORMAT = "%s%07d";

    @Autowired
    private FinancialInvoiceDao invoiceDao;

    @Autowired
    private InvoiceToAXDao invoiceAXDao;

    @Autowired
    private BillingAuditReasonsDao billingAuditReasonsDao;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Autowired
    private ShipmentNoteDao shipmentNoteDao;

    @Autowired
    private ShipmentEventDao eventDao;

    @Autowired
    private LtlShipmentDao shipmentDao;

    @Autowired
    private LoadBillingHistoryDao billingDao;

    @Autowired
    private BillingStatusReasonDao statusReasonDao;

    @Autowired
    private FinanRequestsDao finanRequestsDao;

    @Autowired
    private FinanIntResponsesDao finanIntResponsesDao;

    @Override
    public Long processInvoices(SendToFinanceBO bo, boolean rollbackOnError, Long userId) {
        List<Long> loads = new ArrayList<>();
        List<Long> adjustments = new ArrayList<>();
        populateIDs(bo, loads, adjustments);
        List<InvoiceProcessingItemBO> items = getItemsForProcessing(bo, loads, adjustments);
        validateItems(items);
        validatePairAdjustments(items);
        Long invoiceId = invoiceDao.getNextInvoiceId();
        processItems(items, rollbackOnError, invoiceId, bo.getInvoiceDate(), userId);
        return invoiceId;
    }

    private void validatePairAdjustments(List<InvoiceProcessingItemBO> items) {
        Map<Long, List<InvoiceProcessingItemBO>> failedAdjustmentsByLoadId = items.stream()
                .filter(it -> BooleanUtils.isTrue(it.getRebill()) && it.getErrorCode() != null)
                .collect(Collectors.groupingBy(InvoiceProcessingItemBO::getLoadId));
        if (!failedAdjustmentsByLoadId.isEmpty()) {
            failedAdjustmentsByLoadId.forEach((loadId, adjustments) -> {
                if (adjustments.size() == 1) {
                    findAndFixPairAdjustment(items, loadId, adjustments);
                }
            });
        }
    }

    private void findAndFixPairAdjustment(List<InvoiceProcessingItemBO> items, Long loadId, List<InvoiceProcessingItemBO> adjustments) {
        InvoiceProcessingItemBO failedAdjustment = adjustments.get(0);
        Optional<InvoiceProcessingItemBO> secondAdjustment = items.stream().filter(it -> ObjectUtils.equals(it.getLoadId(), loadId)
                && ObjectUtils.notEqual(failedAdjustment.getAdjustmentId(), it.getAdjustmentId())).findAny();
        if (secondAdjustment.isPresent()) {
            secondAdjustment.get().setErrorCode(failedAdjustment.getErrorCode());
        }
    }

    private void processItems(List<InvoiceProcessingItemBO> items, boolean rollbackOnError, Long invoiceId, Date invoiceDate, Long userId) {
        boolean errorExists = items.stream().anyMatch(it -> it.getErrorCode() != null);
        if (rollbackOnError && errorExists) {
            rollbackSuccessfulItems(items, invoiceId, userId);
        } else {
            processSuccessfulItems(items, invoiceId, invoiceDate, userId);
        }
        processFailedItems(items, invoiceId, userId);
    }

    private void processSuccessfulItems(List<InvoiceProcessingItemBO> items, Long invoiceId, Date invoiceDate, Long userId) {
        List<InvoiceProcessingItemBO> loads = items.stream().filter(it -> it.getErrorCode() == null && it.getAdjustmentId() == null)
                .collect(Collectors.toList());
        if (!loads.isEmpty()) {
            Long requestId = createFinanRequests(items, invoiceId, invoiceDate, userId, ShipmentFinancialStatus.ACCOUNTING_BILLING, true);
            List<Long> successfulLoads = loads.stream().map(InvoiceProcessingItemBO::getLoadId).collect(Collectors.toList());
            finanIntResponsesDao.insertLoads(requestId, successfulLoads, userId);
            invoiceDao.insertLoads(invoiceId, InvoiceReleaseStatus.SUCCESS, successfulLoads, userId);
            invoiceDao.updateLoadsFinancialStatuses(successfulLoads, invoiceDate, userId);
            invoiceDao.insertFinalizationHistoryLoads(successfulLoads, requestId);
            createLoadEvents(userId, loads);
        }
        List<InvoiceProcessingItemBO> adjustments = items.stream().filter(it -> it.getErrorCode() == null && it.getAdjustmentId() != null)
                .collect(Collectors.toList());
        if (!adjustments.isEmpty()) {
            Long requestId = createFinanRequests(items, invoiceId, invoiceDate, userId,
                    ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL, false);
            List<Long> successfulAdjustments = adjustments.stream().map(InvoiceProcessingItemBO::getAdjustmentId).collect(Collectors.toList());
            finanIntResponsesDao.insertAdjustments(requestId, successfulAdjustments, userId);
            invoiceDao.insertAdjustments(invoiceId, InvoiceReleaseStatus.SUCCESS, successfulAdjustments, userId);
            invoiceDao.insertFinalizationHistoryAdjustments(successfulAdjustments, requestId);
            createAdjustmentEvents(userId, successfulAdjustments);
        }

        updateInvoices(items, invoiceDate, userId);
    }

    private void createLoadEvents(Long userId, List<InvoiceProcessingItemBO> loads) {
        List<LoadEventEntity> events = new ArrayList<>(loads.size());
        for (InvoiceProcessingItemBO load : loads) {
            LoadEventEntity event = createEvent(userId, load.getLoadId(), "LD.BSU");
            addLoadEventData(event, 0, "Accounting Billing");
            addLoadEventData(event, 1, "Accounting Billing Release");
            events.add(event);
        }
        eventDao.saveOrUpdateBatch(events);
    }

    private void createAdjustmentEvents(Long userId, List<Long> adjustments) {
        List<KeyValueBO> adjustmentReasons = invoiceDao.getAdjustmentReasons(adjustments);
        List<LoadEventEntity> events = new ArrayList<>(adjustmentReasons.size());
        for (KeyValueBO bo : adjustmentReasons) {
            LoadEventEntity event = createEvent(userId, bo.getKey(), "RLSD.ABR");
            addLoadEventData(event, 0, bo.getValue());
            events.add(event);
        }
        eventDao.saveOrUpdateBatch(events);
    }

    private LoadEventEntity createEvent(Long userId, Long loadId, String eventType) {
        LoadEventEntity event = new LoadEventEntity();
        event.setEventTypeCode(eventType);
        event.setLoadId(loadId);
        event.setFailure(false);
        event.setCreatedBy(userId);
        event.setData(new ArrayList<>());
        return event;
    }

    private void addLoadEventData(LoadEventEntity event, int ordinal, String data) {
        LoadEventDataEntity dataEntity = new LoadEventDataEntity();
        dataEntity.setEventDataPK(new LoadEventDataPK(event, (byte) ordinal));
        dataEntity.setDataType('S');
        dataEntity.setData(data);
        event.getData().add(dataEntity);
    }

    private void updateInvoices(List<InvoiceProcessingItemBO> items, Date invoiceDate, Long userId) {
        updateItemsInvoicedInFinancial(items, invoiceDate, userId);
        updateConsolidatedItems(items, invoiceDate, userId);
        updateTransactionalItems(items, invoiceDate, userId);
    }

    private void updateTransactionalItems(List<InvoiceProcessingItemBO> items, Date invoiceDate, Long userId) {
        List<Long> loads = items.stream().filter(it -> it.getCbiInvoiceType() != CbiInvoiceType.FIN
                && it.getInvoiceType() == InvoiceType.TRANSACTIONAL && it.getAdjustmentId() == null).map(InvoiceProcessingItemBO::getLoadId)
                .collect(Collectors.toList());
        if (!loads.isEmpty()) {
            invoiceDao.updateLoadsWithInvoiceNumbers(loads, invoiceDate, userId);
        }
        List<Long> adjustments = items.stream().filter(it -> it.getCbiInvoiceType() != CbiInvoiceType.FIN
                && it.getInvoiceType() == InvoiceType.TRANSACTIONAL && it.getAdjustmentId() != null)
                .map(InvoiceProcessingItemBO::getAdjustmentId).collect(Collectors.toList());
        if (!adjustments.isEmpty()) {
            invoiceDao.updateAdjustmentsWithInvoiceNumbers(adjustments, invoiceDate, userId);
        }
    }

    private void updateConsolidatedItems(List<InvoiceProcessingItemBO> items, Date invoiceDate, Long userId) {
        Map<Long, List<InvoiceProcessingItemBO>> itemsByBillTo = items.stream()
                .filter(it -> it.getCbiInvoiceType() != CbiInvoiceType.FIN && it.getInvoiceType() == InvoiceType.CBI)
                .collect(Collectors.groupingBy(InvoiceProcessingItemBO::getBillToId, Collectors.toList()));
        if (!itemsByBillTo.isEmpty()) {
            for (List<InvoiceProcessingItemBO> groupedItems : itemsByBillTo.values()) {
                if (groupedItems.size() > 1) {
                    Collections.sort(groupedItems, new InvoicesComparator());
                }
                String groupInvoiceNumber = groupedItems.get(0).getConsolidatedInvoiceNumber();
                if (StringUtils.isBlank(groupInvoiceNumber)) {
                    groupInvoiceNumber = getCBIInvoiceNumber();
                }
                for (int i = 0; i < groupedItems.size(); i++) {
                    InvoiceProcessingItemBO item = groupedItems.get(i);
                    String invoiceNumber = groupInvoiceNumber + "-" + i;
                    if (item.getAdjustmentId() == null) {
                        invoiceDao.updateLoadWithInvoiceNumber(item.getLoadId(), groupInvoiceNumber, invoiceNumber, invoiceDate, userId);
                    } else {
                        invoiceDao.updateAdjustmentWithInvoiceNumber(item.getAdjustmentId(), groupInvoiceNumber, invoiceNumber, invoiceDate, userId);
                    }
                }
            }
        }
    }

    @Override
    public String getCBIInvoiceNumber() {
        Long sequenceNumber = invoiceDao.getNextCBIInvoiceSequenceNumber();
        return String.format(GENERATED_CBI_CODE_FORMAT, CBI_CODE_PREFIX, sequenceNumber.longValue());
    }

    private void updateItemsInvoicedInFinancial(List<InvoiceProcessingItemBO> items, Date invoiceDate, Long userId) {
        List<InvoiceProcessingItemBO> invoiceInFinancialItems = items.stream().filter(it -> it.getCbiInvoiceType() == CbiInvoiceType.FIN)
                .collect(Collectors.toList());
        if (!invoiceInFinancialItems.isEmpty()) {
            List<Long> loadsIds = invoiceInFinancialItems.stream().filter(it -> it.getAdjustmentId() == null)
                    .map(InvoiceProcessingItemBO::getLoadId).collect(Collectors.toList());
            if (!loadsIds.isEmpty()) {
                invoiceDao.updateLoadsInvoicedInFinancials(loadsIds, invoiceDate, userId);
            }
            List<Long> adjustmentsIds = invoiceInFinancialItems.stream().filter(it -> it.getAdjustmentId() != null)
                    .map(InvoiceProcessingItemBO::getAdjustmentId).collect(Collectors.toList());
            if (!adjustmentsIds.isEmpty()) {
                invoiceDao.updateAdjustmentsInvoicedInFinancials(adjustmentsIds, invoiceDate, userId);
            }
        }
    }

    private Long createFinanRequests(List<InvoiceProcessingItemBO> items, Long invoiceId, Date invoiceDate, Long userId,
            ShipmentFinancialStatus finStatus, boolean isLoads) {
        FinanRequestsEntity req = new FinanRequestsEntity();
        req.setPersonId(userId);
        req.setStartDate(new Date());
        req.setEndDate(req.getStartDate());
        req.setParameterList(String.valueOf(invoiceId));
        req.setLoadList(req.getParameterList());
        req.setFinStatus(finStatus);
        req.setToFinStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        req.setGlDate(invoiceDate);

        List<InvoiceProcessingItemBO> filteredItems = items.stream().filter(it -> (it.getAdjustmentId() == null) == isLoads)
                .collect(Collectors.toList());
        req.setLoadsProcessed(filteredItems.size());
        req.setLoadStatusChanges(filteredItems.size());
        req.setArFinalized((int) filteredItems.stream().filter(it -> it.getTotalRevenue().compareTo(BigDecimal.ZERO) != 0).count());
        req.setApFinalized((int) filteredItems.stream().filter(it -> it.getTotalCost().compareTo(BigDecimal.ZERO) != 0).count());
        req.setLoadErrors((int) filteredItems.stream().filter(it -> it.getErrorCode() != null).count());
        req.setAbCount(0);
        req.setAbhCount(req.getLoadErrors());
        req.setLoadsFinalized(filteredItems.size() - req.getLoadErrors());
        req.setAbrCount(req.getLoadsFinalized());
        req.setPercentComplete(new BigDecimal(req.getAbrCount()).divide(new BigDecimal(filteredItems.size())).multiply(new BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP).intValue());
        req.setTotalCostsFinalized(filteredItems.stream().filter(it -> it.getErrorCode() == null).map(InvoiceProcessingItemBO::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        req.setTotalRevenueFinalized(filteredItems.stream().filter(it -> it.getErrorCode() == null).map(InvoiceProcessingItemBO::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return finanRequestsDao.saveOrUpdate(req).getId();
    }

    private void processFailedItems(List<InvoiceProcessingItemBO> items, Long invoiceId, Long userId) {
        List<InvoiceProcessingItemBO> failedLoads = items.stream().filter(it -> it.getErrorCode() != null && it.getAdjustmentId() == null)
                .collect(Collectors.toList());
        if (!failedLoads.isEmpty()) {
            processFailedLoads(failedLoads, invoiceId, userId);
        }
        List<InvoiceProcessingItemBO> failedAdjustments = items.stream().filter(it -> it.getErrorCode() != null && it.getAdjustmentId() != null)
                .collect(Collectors.toList());
        if (!failedAdjustments.isEmpty()) {
            processFailedAdjustments(failedAdjustments, invoiceId, userId);
        }
    }

    private void processFailedLoads(List<InvoiceProcessingItemBO> failedLoads, Long invoiceId, Long userId) {
        for (InvoiceProcessingItemBO load : failedLoads) {
            String errorMessage = getErrorMessageByCode(load.getErrorCode());
            billingAuditReasonsDao.createAndSave(Reasons.INVOICE_FAILED, load.getLoadId(), errorMessage, null);
            LoadEntity loadEntity = shipmentDao.find(load.getLoadId());
            loadEntity.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
            shipmentDao.saveOrUpdate(loadEntity);
            billingAuditService.updateBillingAuditReasonForLoad(loadEntity, ShipmentFinancialStatus.NONE);
            createNoteFromReason(load.getLoadId(), Reasons.INVOICE_FAILED_DESCRIPTION, errorMessage);
            eventDao.saveOrUpdate(LoadEventBuilder.buildInvoiceFailedEvent(load.getLoadId(), errorMessage));
            createFailedBillingHistory(load, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD, userId);
            createFailedInvoiceHistory(load, invoiceId, userId);
        }
    }

    private void processFailedAdjustments(List<InvoiceProcessingItemBO> failedAdjustments, Long invoiceId, Long userId) {
        for (InvoiceProcessingItemBO adjustment : failedAdjustments) {
            String errorMessage = getErrorMessageByCode(adjustment.getErrorCode());
            billingAuditReasonsDao.createAndSave(Reasons.INVOICE_FAILED, adjustment.getLoadId(), errorMessage, adjustment.getAdjustmentId());
            eventDao.saveOrUpdate(LoadEventBuilder.buildInvoiceFailedEvent(adjustment.getLoadId(), errorMessage));
            createFailedBillingHistory(adjustment, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL, userId);
            createFailedInvoiceHistory(adjustment, invoiceId, userId);
        }
        List<Long> adjustmentIds = failedAdjustments.stream().map(InvoiceProcessingItemBO::getAdjustmentId).collect(Collectors.toList());
        invoiceDao.updateAdjustmentsFinancialStatus(adjustmentIds, ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL, userId);
    }

    private void createFailedInvoiceHistory(InvoiceProcessingItemBO item, Long invoiceId, Long userId) {
        FinancialInvoiceHistoryEntity history = new FinancialInvoiceHistoryEntity();
        history.setInvoiceId(invoiceId);
        history.setInvoiceType(item.getInvoiceType());
        history.setLoadId(item.getLoadId());
        history.setAdjustmentId(item.getAdjustmentId());
        history.setReleaseStatus(InvoiceReleaseStatus.FAILURE);
        BillingStatusReasonEntity errorMessage = new BillingStatusReasonEntity();
        errorMessage.setId(item.getErrorCode().name());
        history.setErrorMessage(errorMessage);
        history.getModification().setCreatedBy(userId);
        history.getModification().setModifiedBy(userId);
        invoiceDao.saveOrUpdate(history);
    }

    private void createFailedBillingHistory(InvoiceProcessingItemBO item, ShipmentFinancialStatus newStatus, Long userId) {
        LoadBillingHistoryEntity billingHistory = new LoadBillingHistoryEntity();
        billingHistory.setLoadId(item.getLoadId());
        billingHistory.setCostDetailId(item.getCostDetailId());
        billingHistory.setAdjustmentId(item.getAdjustmentId());
        if (item.getAdjustmentId() == null) {
            billingHistory.setOldFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING);
        } else {
            billingHistory.setOldFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
        }
        billingHistory.setNewFinalizationStatus(newStatus);
        billingHistory.setStatusReason(item.getErrorCode());
        billingHistory.setCreatedBy(userId);
        billingDao.saveOrUpdate(billingHistory);
    }

    private String getErrorMessageByCode(InvoiceErrorCode errorCode) {
        return statusReasonDao.getDescriptionByCode(errorCode.name());
    }

    private void rollbackSuccessfulItems(List<InvoiceProcessingItemBO> items, Long invoiceId, Long userId) {
        List<Long> successfulLoads = items.stream().filter(it -> it.getErrorCode() == null && it.getAdjustmentId() == null)
                .map(InvoiceProcessingItemBO::getLoadId).collect(Collectors.toList());
        if (!successfulLoads.isEmpty()) {
            invoiceDao.insertLoads(invoiceId, InvoiceReleaseStatus.CANCELLED, successfulLoads, userId);
        }
        List<Long> successfulAdjustments = items.stream().filter(it -> it.getErrorCode() == null && it.getAdjustmentId() != null)
                .map(InvoiceProcessingItemBO::getAdjustmentId).collect(Collectors.toList());
        if (!successfulAdjustments.isEmpty()) {
            invoiceDao.insertAdjustments(invoiceId, InvoiceReleaseStatus.CANCELLED, successfulAdjustments, userId);
        }
    }

    private void validateItems(List<InvoiceProcessingItemBO> items) {
        for (InvoiceProcessingItemBO item : items) {
            validateCurrency(item);
            if (item.getTotalRevenue() == null || item.getTotalItemsRevenue() == null || item.getTotalCost() == null || item.getTotalItemsCost() == null) {
                item.setErrorCode(InvoiceErrorCode.CRN);
            } 
            if (item.getErrorCode() == null && (item.getTotalRevenue().compareTo(item.getTotalItemsRevenue()) != 0
                    || item.getTotalCost().compareTo(item.getTotalItemsCost()) != 0)) {
                item.setErrorCode(InvoiceErrorCode.TI);
            }
            if (item.getAdjustmentId() != null) {
                validateAdjustmentSpecificErrors(item);
            } else {
                validateLoadSpecificErrors(item);
            }
        }
    }

    private void validateCurrency(InvoiceProcessingItemBO item) {
        if (item.getBillToCurrency() != item.getCarrierCurrency()) {
            if (item.getBillToCurrency() == Currency.USD) {
                item.setErrorCode(InvoiceErrorCode.USCN);
            } else {
                item.setErrorCode(InvoiceErrorCode.CNUS);
            }
        }
    }

    private void validateLoadSpecificErrors(InvoiceProcessingItemBO item) {
        if (item.getErrorCode() == null && item.getBolNumber() == null) {
            item.setErrorCode(InvoiceErrorCode.BOL);
        }
        if (item.getErrorCode() == null && item.getPickupDate() == null) {
            item.setErrorCode(InvoiceErrorCode.PD);
        }
        validateZeroRevenueAndCosts(item);
        validateFreightBillDate(item);
    }

    private void validateFreightBillDate(InvoiceProcessingItemBO item) {
        if (item.getErrorCode() == null && item.getFreightBillDate() != null && item.getFreightBillDate().after(new Date())) {
            item.setErrorCode(InvoiceErrorCode.BD);
        }
    }

    private void validateZeroRevenueAndCosts(InvoiceProcessingItemBO item) {
        if (item.getErrorCode() == null && item.getTotalRevenue().compareTo(BigDecimal.ZERO) == 0
                && item.getTotalCost().compareTo(BigDecimal.ZERO) == 0) {
            item.setErrorCode(InvoiceErrorCode.ZR);
        }
    }

    private void validateAdjustmentSpecificErrors(InvoiceProcessingItemBO item) {
        if (item.getErrorCode() == null && item.getInvalidCostItemsCount() > 0) {
            item.setErrorCode(InvoiceErrorCode.AL);
        }
    }

    private List<InvoiceProcessingItemBO> getItemsForProcessing(SendToFinanceBO bo, List<Long> loads, List<Long> adjustments) {
        List<InvoiceProcessingItemBO> items = new ArrayList<>();
        if (!loads.isEmpty()) {
            items.addAll(invoiceDao.getLoadsForProcessing(loads));
        }
        if (!adjustments.isEmpty()) {
            items.addAll(invoiceDao.getAdjustmentsForProcessing(adjustments));
        }
        if (bo.getInvoiceProcessingDetails().stream().anyMatch(it -> StringUtils.isNotBlank(it.getConsolidatedInvoiceNumber()))) {
            items.stream().filter(it -> it.getInvoiceType() == InvoiceType.CBI).forEach(it -> {
                it.setConsolidatedInvoiceNumber(findInvoiceNumber(bo.getInvoiceProcessingDetails(), it.getBillToId()));
            });
        }
        return items;
    }

    private String findInvoiceNumber(List<InvoiceProcessingBO> invoiceProcessingDetails, Long billToId) {
        return invoiceProcessingDetails.stream().filter(it -> ObjectUtils.equals(it.getBillToId(), billToId))
                .map(InvoiceProcessingBO::getConsolidatedInvoiceNumber).findAny().orElse(null);
    }

    private void populateIDs(SendToFinanceBO bo, List<Long> loads, List<Long> adjustments) {
        for (InvoiceProcessingBO detail : bo.getInvoiceProcessingDetails()) {
            if (CollectionUtils.isNotEmpty(detail.getLoadIds())) {
                loads.addAll(detail.getLoadIds());
            }
            if (CollectionUtils.isNotEmpty(detail.getAdjustmentIds())) {
                adjustments.addAll(detail.getAdjustmentIds());
            }
            if (CollectionUtils.isEmpty(detail.getLoadIds()) && CollectionUtils.isEmpty(detail.getAdjustmentIds())) {
                addItems(loads, invoiceDao.getLoadsByBillTo(detail.getBillToId(), bo.getFilterLoadsDate()));
                addItems(adjustments, invoiceDao.getAdjustmentsByBillTo(detail.getBillToId()));
            }
        }
    }

    private void addItems(List<Long> to, List<Long> from) {
        if (CollectionUtils.isNotEmpty(from)) {
            to.addAll(from);
        }
    }

    @Override
    public void sendInvoicesToAX(Long invoiceId, Long personId) throws ApplicationException {
        LOGGER.info("Sending to AX invoice ID: {}", invoiceId);
        try {
            Collection<FinanceInfoTable> financeData = invoiceAXDao.getDataForFinanceSystemByInvoiceID(invoiceId);
            for (FinanceInfoTable financeInfoTable : financeData) {
                SterlingIntegrationMessageBO sterlingMessage = new SterlingIntegrationMessageBO((IntegrationMessageBO) financeInfoTable,
                        (financeInfoTable.isArType() ? EDIMessageType.AR : EDIMessageType.AP));
                sterlingMessageProducer.publishMessage(sterlingMessage);
            }
            LOGGER.info("Finished Sending to AX invoice ID: {}", invoiceId);

        } catch (JMSException e) {
            String errorMsg = String.format("Error sending financial information with Invoice ID %d to finance system", invoiceId);
            LOGGER.error(errorMsg);
            throw new ApplicationException(errorMsg, e);
        }
    }

    private void createNoteFromReason(Long loadId, String reasonDesc, String comment) {
        ShipmentNoteEntity note = new ShipmentNoteEntity();
        note.setLoadId(loadId);
        note.setNote(String.format("Invoice Audit : Reason %s Comment: %s", reasonDesc, comment == null ? " -" : comment));
        shipmentNoteDao.saveOrUpdate(note);
    }

    /**
     * Invoices Comparator.
     * 
     * @author Aleksandr Leshchenko
     */
    private final class InvoicesComparator implements Comparator<InvoiceProcessingItemBO> {
        @Override
        public int compare(InvoiceProcessingItemBO o1, InvoiceProcessingItemBO o2) {
            InvoiceSortType sortType = o1.getSortType();
            CompareToBuilder builder = new CompareToBuilder();
            if (sortType != null) {
                if (sortType == InvoiceSortType.BOL) {
                    builder.append(o1.getBolNumber(), o2.getBolNumber());
                } else if (sortType == InvoiceSortType.GL_NUM) {
                    builder.append(o1.getGlNumber(), o2.getGlNumber());
                } else if (sortType == InvoiceSortType.DELIV_DATE) {
                    builder.append(o1.getDeliveryDate(), o2.getDeliveryDate());
                }
                builder.append(o1.getLoadId(), o2.getLoadId()).append(o1.getAdjustmentId(), o2.getAdjustmentId());
            }
            return builder.build();
        }
    }
}
