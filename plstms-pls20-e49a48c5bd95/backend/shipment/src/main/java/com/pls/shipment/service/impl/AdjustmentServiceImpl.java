package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.FinancialAccessorialsDao;
import com.pls.shipment.dao.LoadMaterialDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsAdditionalInfoEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.FinancialAccessorialsProductInfoEntity;
import com.pls.shipment.domain.FinancialReasonsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.bo.AdjustmentLoadInfoBO;
import com.pls.shipment.domain.enums.AdjustmentReason;
import com.pls.shipment.domain.enums.LoadEventType;
import com.pls.shipment.service.AdjustmentService;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.dictionary.FinancialReasonsDictionaryService;

/**
 * Implementation of {@link AdjustmentService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class AdjustmentServiceImpl implements AdjustmentService {
    private static final Logger LOG = LoggerFactory.getLogger(AdjustmentServiceImpl.class);

    @Autowired
    private LtlShipmentDao ltlShipmentDao;
    @Autowired
    private FinancialAccessorialsDao financialAccessorialsDao;
    @Autowired
    private BillToDao billToDao;
    @Autowired
    private FinancialReasonsDictionaryService reasonsService;
    @Autowired
    private ShipmentEventDao eventDao;
    @Autowired
    private LoadMaterialDao materialDao;

    @Override
    public void saveAdjustments(Long loadId, List<AdjustmentBO> adjustmentsToSave, List<AdjustmentBO> adjustmentsToRemove,
            AdjustmentLoadInfoBO loadInfo, List<LoadMaterialEntity> materials) throws ApplicationException {
        LoadEntity shipment = ltlShipmentDao.find(loadId);
        validateInvoicedShipment(shipment);
        Set<FinancialAccessorialsEntity> allFinancialAccessorials = shipment.getAllFinancialAccessorials();
        setReasonForAdjustmentsToRemove(adjustmentsToRemove, allFinancialAccessorials);
        logEvents(adjustmentsToRemove, LoadEventType.DELETED, loadId);
        int maxAdjustmentRevision = getMaxAdjustmentRevision(allFinancialAccessorials);
        removeAdjustments(allFinancialAccessorials, adjustmentsToRemove, shipment);
        validateNotChangedAndMissingAdjustments(allFinancialAccessorials, adjustmentsToSave, loadInfo);
        logEvents(adjustmentsToSave, LoadEventType.SAVED, loadId);
        deactivateChangedAdjustments(allFinancialAccessorials);
        maxAdjustmentRevision = createNegativePairAdjustments(shipment, adjustmentsToSave, maxAdjustmentRevision);
        updateShipment(shipment, loadInfo, materials);
        createNewAdjustments(shipment, adjustmentsToSave, maxAdjustmentRevision);
    }

    private void logEvents(List<AdjustmentBO> adjustments, LoadEventType event, Long loadId) {
        Map<Long, String> adjustmentsReasons = reasonsService.getFinancialReasonsForAdjustments().stream()
                .collect(Collectors.toMap(FinancialReasonsEntity::getId, FinancialReasonsEntity::getDescription));

        adjustments.stream().map(s -> s.getReason()).distinct().filter(Objects::nonNull)
                .map(item -> LoadEventBuilder.buildEventEntity(loadId, event, new String[] { adjustmentsReasons.get(item) }))
                .forEach(eventDao::persist);
    }

    private void setReasonForAdjustmentsToRemove(List<AdjustmentBO> adjustmentsToRemove,
            Set<FinancialAccessorialsEntity> allFinancialAccessorials) {
        if (CollectionUtils.isNotEmpty(adjustmentsToRemove) && CollectionUtils.isNotEmpty(allFinancialAccessorials)) {
            adjustmentsToRemove.forEach(adj -> {
                adj.setReason(allFinancialAccessorials.stream().filter(a -> ObjectUtils.equals(a.getId(), adj.getFinancialAccessorialsId()))
                        .map(a -> a.getCostDetailItems().iterator().next().getReason().getId()).findAny().orElse(null));
            });
        }
    }

    private void validateInvoicedShipment(LoadEntity shipment) throws ApplicationException {
        if (shipment.getStatus() != ShipmentStatus.DELIVERED
                || shipment.getFinalizationStatus() != ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE
                || shipment.getActiveCostDetail().getGeneralLedgerDate() == null) {
            throw new ApplicationException("Load must be invoiced");
        }
    }

    private int getMaxAdjustmentRevision(Set<FinancialAccessorialsEntity> adjustments) {
        return adjustments.stream().map(FinancialAccessorialsEntity::getRevision).filter(Objects::nonNull).mapToInt(Integer::intValue).max()
                .orElse(0);
    }

    private void deactivateChangedAdjustments(Set<FinancialAccessorialsEntity> allFinancialAccessorials) throws ApplicationException {
        for (FinancialAccessorialsEntity financialAccessorial : allFinancialAccessorials) {
            if (CollectionUtils.isNotEmpty(financialAccessorial.getRollbackInfo())) {
                throw new ApplicationException("Negative adjustment can't be changed");
            }
            LOG.info("Adjustment with id={} and version={} is changed and will be deactivated", financialAccessorial.getId(),
                    financialAccessorial.getVersion());
            financialAccessorial.setStatus(Status.INACTIVE);
            financialAccessorialsDao.update(financialAccessorial);
        }
    }

    private void validateNotChangedAndMissingAdjustments(Set<FinancialAccessorialsEntity> allFinancialAccessorials,
            List<AdjustmentBO> adjustmentsToSave, AdjustmentLoadInfoBO loadInfo) throws ApplicationException {
        Iterator<FinancialAccessorialsEntity> accessorialIt = allFinancialAccessorials.iterator();

        while (accessorialIt.hasNext()) {
            FinancialAccessorialsEntity financialAccessorial = accessorialIt.next();
            List<AdjustmentBO> adjustments = findAdjustmentsByFinancialAccessorial(adjustmentsToSave, financialAccessorial.getId(),
                    financialAccessorial.getVersion());
            if (adjustments.isEmpty()) {
                if (isInvoicedAdjustment(financialAccessorial)) {
                    // ignore invoiced adjustments
                    accessorialIt.remove();
                    continue;
                }
                throw new ApplicationException("Missing adjustment");
            }
            if (isAdjustmentUnchanged(financialAccessorial, adjustments)) {
                if (loadInfo != null && isPositiveRebillShipperAdjustment(financialAccessorial)) {
                    // some load information was updated. Need to create new adjustment in this case.
                    continue;
                }
                LOG.info("Adjustment with id={} and version={} is unchanged", financialAccessorial.getId(), financialAccessorial.getVersion());
                accessorialIt.remove();
                adjustmentsToSave.removeAll(adjustments);
            }
        }
    }

    private boolean isInvoicedAdjustment(FinancialAccessorialsEntity financialAccessorial) {
        return financialAccessorial.getFinancialStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE
                && financialAccessorial.getGeneralLedgerDate() != null;
    }

    private boolean isPositiveRebillShipperAdjustment(FinancialAccessorialsEntity financialAccessorial) {
        return isRebillShipperAdjustment(financialAccessorial) && CollectionUtils.isEmpty(financialAccessorial.getRollbackInfo());
    }

    private List<AdjustmentBO> findAdjustmentsByFinancialAccessorial(List<AdjustmentBO> adjustmentsToSave, final Long id, final Integer version) {
        return adjustmentsToSave.stream().filter(adjustment -> filterAdjustment(adjustment, id, version)).collect(Collectors.toList());
    }

    private boolean filterAdjustment(AdjustmentBO adjustment, Long id, Integer version) {
        if (id.equals(adjustment.getFinancialAccessorialsId())) {
            if (!adjustment.getVersion().equals(version)) {
                throw new StaleObjectStateException(adjustment.getClass().getName(), adjustment.getFinancialAccessorialsId());
            }
            return true;
        }
        return false;
    }

    private boolean isRebillShipperAdjustment(FinancialAccessorialsEntity adjustment) {
        return adjustment.getCostDetailItems().stream()
                .anyMatch(i -> i.getReason().getId().intValue() == AdjustmentReason.REBILL_SHIPPER.getReason());
    }

    private boolean isAdjustmentUnchanged(FinancialAccessorialsEntity financialAccessorial, List<AdjustmentBO> adjustments) {
        List<CostDetailItemEntity> costItems = new ArrayList<CostDetailItemEntity>(financialAccessorial.getCostDetailItems());
        for (AdjustmentBO adjustment : adjustments) {
            if (adjustment.isNotInvoice() != BooleanUtils.isTrue(financialAccessorial.getShortPay())) {
                return false;
            }
            List<CostDetailItemEntity> matchingCostItems = findCostItemsByRefType(costItems, adjustment.getRefType());
            if (matchingCostItems.size() != 2) {
                return false;
            }
            if (isCostDetailItemMatchesAdjustment(adjustment, matchingCostItems.get(0))
                    && isCostDetailItemMatchesAdjustment(adjustment, matchingCostItems.get(1))) {
                costItems.removeAll(matchingCostItems);
            } else {
                return false;
            }
        }
        return costItems.isEmpty();
    }

    private List<CostDetailItemEntity> findCostItemsByRefType(List<CostDetailItemEntity> costItems, String refType) {
        final String adjustedRefType = "CRA".equals(refType) ? "SRA" : refType;
        return costItems.stream()
                .filter(costItem -> adjustedRefType.equals("CRA".equals(costItem.getAccessorialType()) ? "SRA" : costItem.getAccessorialType()))
                .collect(Collectors.toList());
    }

    private boolean isCostDetailItemMatchesAdjustment(AdjustmentBO adjustment, CostDetailItemEntity costDetailItem) {
        return isEqualPrice(costDetailItem, adjustment) && isEqualNote(costDetailItem, adjustment)
                && adjustment.getReason().equals(costDetailItem.getReason().getId());
    }

    private boolean isEqualPrice(CostDetailItemEntity costDetailItem, AdjustmentBO adjustment) {
        BigDecimal adjustmentPrice;
        if (costDetailItem.getOwner() == CostDetailOwner.S) {
            adjustmentPrice = adjustment.getRevenue();
        } else {
            adjustmentPrice = adjustment.getCost();
        }
        return costDetailItem.getSubtotal().equals(adjustmentPrice);
    }

    private boolean isEqualNote(CostDetailItemEntity costDetailItem, AdjustmentBO adjustment) {
        String adjustmentNote;
        if (costDetailItem.getOwner() == CostDetailOwner.S) {
            adjustmentNote = adjustment.getRevenueNote();
        } else {
            adjustmentNote = adjustment.getCostNote();
        }
        return StringUtils.equals(costDetailItem.getNote(), adjustmentNote);
    }

    private void removeAdjustments(Set<FinancialAccessorialsEntity> allFinancialAccessorials, List<AdjustmentBO> adjustmentsToRemove,
            LoadEntity shipment) throws ApplicationException {
        if (!adjustmentsToRemove.isEmpty()) {
            boolean shipmentUpdated = false;
            for (AdjustmentBO adjustmentToRemove : adjustmentsToRemove) {
                shipmentUpdated |= validateAndRemoveAdjustment(allFinancialAccessorials, adjustmentToRemove, shipment, shipmentUpdated);
            }
            validatePairAdjustments(allFinancialAccessorials);
        }
    }

    private void validatePairAdjustments(Set<FinancialAccessorialsEntity> allFinancialAccessorials) throws ApplicationException {
        long count = allFinancialAccessorials.stream()
                .filter(acc -> acc.getGeneralLedgerDate() == null && acc.getCostDetailItems().stream().anyMatch(this::isPairAdjustment)).count();
        if (count % 2 == 1) {
            throw new ApplicationException("Both adjustments parts should be removed.");
        }
    }

    private boolean isPairAdjustment(CostDetailItemEntity i) {
        return i.getReason().getId().intValue() == AdjustmentReason.WRONG_CARRIER.getReason()
                || i.getReason().getId().intValue() == AdjustmentReason.REBILL_SHIPPER.getReason();
    }

    private boolean validateAndRemoveAdjustment(Set<FinancialAccessorialsEntity> allFinancialAccessorials, AdjustmentBO adjustmentToRemove,
            LoadEntity shipment, boolean shipmentUpdated) throws ApplicationException {
        Optional<FinancialAccessorialsEntity> foundAdjustment = allFinancialAccessorials.stream()
                .filter(adjustment -> adjustment.getId().equals(adjustmentToRemove.getFinancialAccessorialsId())).findAny();
        if (foundAdjustment.isPresent()) {
            FinancialAccessorialsEntity adjustment = foundAdjustment.get();
            if (adjustment.getFinancialStatus() != ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE
                    && adjustment.getGeneralLedgerDate() == null) {
                adjustment.setStatus(Status.INACTIVE);
                adjustment.setVersion(adjustmentToRemove.getVersion());
                financialAccessorialsDao.update(adjustment);
                allFinancialAccessorials.remove(adjustment);
                boolean shipmentUpdatedNow = false;
                if (!shipmentUpdated) {
                    // need to check if load was already updated. Otherwise load is updated twice for wrong carrier adjustment.
                    shipmentUpdatedNow = rollbackLoadChanges(adjustment, shipment);
                }
                LOG.info("Adjustment with id={} and version={} has been removed", adjustment.getId(), adjustment.getVersion());
                return shipmentUpdatedNow;
            } else if (!adjustment.getVersion().equals(adjustmentToRemove.getVersion())) {
                // adjustment has been invoiced
                throw new StaleObjectStateException(adjustment.getClass().getName(), adjustment.getId());
            } else {
                // user tries to update already invoiced adjustment
                throw new ApplicationException("Invoiced adjustment can't be removed");
            }
        }
        // adjustment might be inactive (updated by another user)
        throw new StaleObjectStateException(FinancialAccessorialsEntity.class.getName(), adjustmentToRemove.getFinancialAccessorialsId());
    }

    private boolean rollbackLoadChanges(FinancialAccessorialsEntity adjustment, LoadEntity shipment) {
        Optional<CostDetailItemEntity> costItem = adjustment.getCostDetailItems().stream()
                .filter(i -> i.getReason().getId().intValue() == AdjustmentReason.WRONG_CARRIER.getReason()
                        && !ObjectUtils.equals(i.getCarrierId(), shipment.getCarriedId()))
                .findAny();
        if (costItem.isPresent()) {
            shipment.setCarrier(costItem.get().getCarrier());
            ltlShipmentDao.update(shipment);
            return true;
        }
        if (CollectionUtils.isNotEmpty(adjustment.getRollbackInfo())) {
            costItem = adjustment.getCostDetailItems().stream()
                    .filter(i -> i.getReason().getId().intValue() == AdjustmentReason.REBILL_SHIPPER.getReason()).findAny();
            if (costItem.isPresent()) {
                rollbackShipmentFromRebillAdjustment(shipment, adjustment);
                return true;
            }
        }
        return false;
    }

    private void updateShipment(LoadEntity shipment, AdjustmentLoadInfoBO loadInfo, List<LoadMaterialEntity> materials) {
        if (loadInfo != null && materials != null) {
            if (ObjectUtils.notEqual(loadInfo.getBillToId(), shipment.getBillToId())) {
                shipment.setBillTo(billToDao.find(loadInfo.getBillToId()));
            }
            shipment.getNumbers().setBolNumber(loadInfo.getBolNumber());
            shipment.getNumbers().setRefNumber(loadInfo.getRefNumber());
            shipment.getNumbers().setPoNumber(loadInfo.getPoNumber());
            shipment.getNumbers().setSoNumber(loadInfo.getSoNumber());
            shipment.getOrigin().getLoadMaterials().clear();
            materials.forEach(m -> {
                m.setPickupDate(shipment.getOrigin().getArrivalWindowStart());
                m.setLoadDetail(shipment.getOrigin());
            });
            shipment.getOrigin().getLoadMaterials().addAll(materials);
            recalculateProductsTotals(shipment);
            ltlShipmentDao.saveOrUpdate(shipment);
        } else if (loadInfo != null && loadInfo.getCarrier() != null) {
            shipment.setCarrierId(loadInfo.getCarrier().getId());
            ltlShipmentDao.update(shipment);
        }
    }

    private void recalculateProductsTotals(LoadEntity shipment) {
        shipment.setHazmat(shipment.getOrigin().getLoadMaterials().stream().anyMatch(LoadMaterialEntity::isHazmat));
        shipment.setWeight(shipment.getOrigin().getLoadMaterials().stream().map(LoadMaterialEntity::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(0, RoundingMode.CEILING).intValue());
        shipment.setPieces(shipment.getOrigin().getLoadMaterials().stream().map(LoadMaterialEntity::getQuantity).filter(StringUtils::isNotBlank)
                .mapToInt(Integer::parseInt).sum());
    }

    private void rollbackShipmentFromRebillAdjustment(LoadEntity shipment, FinancialAccessorialsEntity adjustment) {
        shipment.setBillTo(adjustment.getCostDetailItems().iterator().next().getBillTo());
        shipment.getNumbers().setBolNumber(adjustment.getBol());
        FinancialAccessorialsAdditionalInfoEntity additionalInfo = adjustment.getRollbackInfo().iterator().next();
        shipment.getNumbers().setRefNumber(additionalInfo.getRefNumber());
        shipment.getNumbers().setPoNumber(additionalInfo.getPoNumber());
        shipment.getNumbers().setSoNumber(additionalInfo.getSoNumber());

        List<LoadMaterialEntity> materials = adjustment.getAdjProductInfo().stream().map(FinancialAccessorialsProductInfoEntity::getLoadMaterial)
                .collect(Collectors.toList());
        materials.forEach(m -> {
            m.setId(null);
            materialDao.evict(m);
            m.setLoadDetail(shipment.getOrigin());
        });
        shipment.getOrigin().getLoadMaterials().clear();
        shipment.getOrigin().getLoadMaterials().addAll(materials);
        recalculateProductsTotals(shipment);
        ltlShipmentDao.merge(shipment);
    }

    private int createNegativePairAdjustments(LoadEntity shipment, List<AdjustmentBO> adjustmentsToSave, int maxAdjustmentRevision) {
        List<AdjustmentBO> negativeAdjustments = adjustmentsToSave.stream().filter(this::isNegativePairAdjustment).collect(Collectors.toList());
        if (!negativeAdjustments.isEmpty()) {
            FinancialAccessorialsEntity financialAccessorial = createFinancialAccessorial(shipment, negativeAdjustments.get(0));
            LOG.info("Create new negative adjustment {}", ReflectionToStringBuilder.toString(negativeAdjustments.get(0)));
            adjustmentsToSave.removeAll(negativeAdjustments);
            negativeAdjustments.subList(1, negativeAdjustments.size()).forEach(adjustment -> {
                createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.C);
                createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.S);
            });
            financialAccessorial.setRevision(maxAdjustmentRevision + 1);
            addRollbackInfo(financialAccessorial, negativeAdjustments.get(0).getReason());
            financialAccessorialsDao.saveOrUpdate(financialAccessorial);
            return maxAdjustmentRevision + 1;
        }
        return maxAdjustmentRevision;
    }

    private void addRollbackInfo(FinancialAccessorialsEntity financialAccessorial, Long reason) {
        if (reason == AdjustmentReason.REBILL_SHIPPER.getReason()) {
            FinancialAccessorialsAdditionalInfoEntity rollbackInfo = new FinancialAccessorialsAdditionalInfoEntity();
            rollbackInfo.setPoNumber(financialAccessorial.getLoad().getNumbers().getPoNumber());
            rollbackInfo.setRefNumber(financialAccessorial.getLoad().getNumbers().getRefNumber());
            rollbackInfo.setSoNumber(financialAccessorial.getLoad().getNumbers().getSoNumber());
            rollbackInfo.setFinancialAccessorials(financialAccessorial);

            Set<LoadMaterialEntity> materials = financialAccessorial.getLoad().getOrigin().getLoadMaterials();
            for (LoadMaterialEntity material : materials) {
                material.setLoadDetail(null);
                FinancialAccessorialsProductInfoEntity adjProductInfoEntity = new FinancialAccessorialsProductInfoEntity();
                adjProductInfoEntity.setLoadMaterial(material);
                adjProductInfoEntity.setFinancialAccessorials(financialAccessorial);
                financialAccessorial.getAdjProductInfo().add(adjProductInfoEntity);
            }
            financialAccessorial.getRollbackInfo().add(rollbackInfo);
        }
    }

    private boolean isNegativePairAdjustment(AdjustmentBO adj) {
        return adj.getFinancialAccessorialsId() != null && adj.getFinancialAccessorialsId() == -1
                && (adj.getReason().intValue() == AdjustmentReason.REBILL_SHIPPER.getReason()
                        || adj.getReason().intValue() == AdjustmentReason.WRONG_CARRIER.getReason());
    }

    private void createNewAdjustments(LoadEntity shipment, List<AdjustmentBO> adjustmentsToSave, int maxAdjustmentRevision) {
        int revision = maxAdjustmentRevision + 1;
        while (!adjustmentsToSave.isEmpty()) {
            Iterator<AdjustmentBO> adjustmentsIt = adjustmentsToSave.iterator();
            AdjustmentBO firstAdjustment = adjustmentsIt.next();
            FinancialAccessorialsEntity financialAccessorial = createFinancialAccessorial(shipment, firstAdjustment);
            adjustmentsIt.remove();
            LOG.info("Create new adjustment {}", ReflectionToStringBuilder.toString(firstAdjustment));
            while (adjustmentsIt.hasNext()) {
                AdjustmentBO adjustment = adjustmentsIt.next();
                if (isSameAdjustmentGroup(firstAdjustment, adjustment)) {
                    LOG.info("Create cost items for adjustment {}", ReflectionToStringBuilder.toString(adjustment));
                    createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.C);
                    createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.S);
                    adjustmentsIt.remove();
                }
            }
            financialAccessorial.setRevision(revision++);
            financialAccessorialsDao.saveOrUpdate(financialAccessorial);
        }
    }

    private boolean isSameAdjustmentGroup(AdjustmentBO adjustment1, AdjustmentBO adjustment2) {
        return adjustment1.isNotInvoice() == adjustment2.isNotInvoice();
    }

    private FinancialAccessorialsEntity createFinancialAccessorial(LoadEntity shipment, AdjustmentBO adjustment) {
        FinancialAccessorialsEntity financialAccessorial = new FinancialAccessorialsEntity();
        financialAccessorial.setLoad(shipment);
        financialAccessorial.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL);
        financialAccessorial.setShortPay(adjustment.isNotInvoice());
        financialAccessorial.setBol(shipment.getNumbers().getBolNumber());
        createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.C);
        createCostDetailItemsForAdjustment(financialAccessorial, adjustment, shipment.getActiveCostDetail(), CostDetailOwner.S);
        return financialAccessorial;
    }

    private void createCostDetailItemsForAdjustment(FinancialAccessorialsEntity financialAccessorial, AdjustmentBO adjustment,
            LoadCostDetailsEntity costDetails, CostDetailOwner owner) {
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        if (owner == CostDetailOwner.C) {
            costDetailItem.setSubtotal(adjustment.getCost());
            costDetailItem.setUnitCost(adjustment.getCost());
            costDetailItem.setNote(adjustment.getCostNote());
            costDetailItem.setAccessorialType("SRA".equals(adjustment.getRefType()) ? "CRA" : adjustment.getRefType());
            if (financialAccessorial.getTotalCost() == null) {
                financialAccessorial.setTotalCost(adjustment.getCost());
            } else {
                financialAccessorial.setTotalCost(financialAccessorial.getTotalCost().add(adjustment.getCost()));
            }
        } else {
            costDetailItem.setSubtotal(adjustment.getRevenue());
            costDetailItem.setUnitCost(adjustment.getRevenue());
            costDetailItem.setNote(adjustment.getRevenueNote());
            costDetailItem.setAccessorialType("CRA".equals(adjustment.getRefType()) ? "SRA" : adjustment.getRefType());
            if (financialAccessorial.getTotalRevenue() == null) {
                financialAccessorial.setTotalRevenue(adjustment.getRevenue());
            } else {
                financialAccessorial.setTotalRevenue(financialAccessorial.getTotalRevenue().add(adjustment.getRevenue()));
            }
        }
        costDetailItem.setOwner(owner);
        costDetailItem.setCostDetails(costDetails);
        costDetailItem.setFinancialAccessorials(financialAccessorial);
        costDetailItem.setBillTo(financialAccessorial.getLoad().getBillTo());
        costDetailItem.setReason(new FinancialReasonsEntity());
        costDetailItem.getReason().setId(adjustment.getReason());
        costDetailItem.setCarrierId(financialAccessorial.getLoad().getCarriedId());
        financialAccessorial.getCostDetailItems().add(costDetailItem);
    }
}
