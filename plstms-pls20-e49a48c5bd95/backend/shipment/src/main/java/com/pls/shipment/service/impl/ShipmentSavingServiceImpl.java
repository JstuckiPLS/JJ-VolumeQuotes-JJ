package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.FreightBillPayToDao;
import com.pls.core.dao.address.AddressDao;
import com.pls.core.dao.impl.FreightBillPayToDaoImpl;
import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.bo.proposal.Smc3CostDetailsDTO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.validation.support.Validator;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.dao.LoadPricingDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNoteEntity;
import com.pls.shipment.domain.LoadPricMaterialDtlsEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.service.LoadTenderService;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;
import com.pls.shipment.service.impl.validation.ShipmentValidator;

/**
 * {@link ShipmentSavingService} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class ShipmentSavingServiceImpl implements ShipmentSavingService {

    private static final Integer WEIGHT_PRECISION = 3;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;
    @Autowired
    private CarrierDao carrierDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private FreightBillPayToDao freightBillPayToDao;

    @Resource(type = ShipmentValidator.class)
    private Validator<LoadEntity> shipmentValidator;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private LoadTenderService loadTenderService;
    @Autowired
    private ShipmentAlertService shipmentAlertService;
    @Autowired
    private AccessorialTypeDao accessorialTypeDao;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private LoadPricingDetailsDao loadPricingDetailsDao;
    
    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Override
    public LoadEntity bookShipment(LoadEntity loadEntity, boolean autoDispatch, Long customerUserId,
            ShipmentProposalBO proposal, Long currentUserId)
                 throws ApplicationException, InternalJmsCommunicationException, EdiProcessingException {

        ShipmentStatus oldStatus = getPrevStatus(loadEntity.getId());
        CarrierEntity oldCarrier = getPrevCarrier(loadEntity.getId());

        if ((oldStatus == null || (loadEntity.getCarrier() != null && !ShipmentStatus.OPEN.equals(loadEntity.getStatus())))
                && !ShipmentStatus.PENDING_PAYMENT.equals(loadEntity.getStatus())) {
            updateShipmentStatus(loadEntity, autoDispatch, oldStatus);
        }

        loadEntity.setPersonId(customerUserId);
        setUpRoute(loadEntity, currentUserId);
        updateFreightBillPayTo(loadEntity);
        shipmentService.updateTimeZoneInfo(loadEntity);

        if (proposal != null) {
            updateLoadWithQuoteNumber(loadEntity, proposal);
            updateLoadWithCostDetails(loadEntity, proposal, currentUserId);
            updateLoadWithPricingDetails(loadEntity, proposal);
            shipmentService.checkBillToCreditLimit(loadEntity);
            if (proposal.getEstimatedTransitDate() != null) {
                loadEntity.getDestination().setEarlyScheduledArrival(
                        getDateWithTime(proposal.getEstimatedTransitDate(), loadEntity.getOrigin().getEarlyScheduledArrival()));
                loadEntity.getDestination().setScheduledArrival(
                        getDateWithTime(proposal.getEstimatedTransitDate(), loadEntity.getOrigin().getScheduledArrival()));
            }
            shipmentService.addImplicitOverdimentionalAccessorial(loadEntity);
        }

        shipmentValidator.validate(loadEntity);

        LoadEntity returnedLoad = updateLoadWithBolNumber(loadEntity);

        shipmentAlertService.processShipmentAlerts(returnedLoad);

        tenderLoad(proposal, oldStatus, oldCarrier, returnedLoad);

        // Send notification e-mails
        shipmentEmailSender.sendLoadStatusChangedNotification(returnedLoad, loadEntity.getStatus());

        return returnedLoad;
    }

    @Override
    public LoadEntity save(LoadEntity loadEntity, ShipmentProposalBO proposal, Long customerUserId, Long currentUserId)
            throws ApplicationException, EdiProcessingException, InternalJmsCommunicationException {

        ShipmentStatus oldStatus = getPrevStatus(loadEntity.getId());
        CarrierEntity oldCarrier = getPrevCarrier(loadEntity.getId());

        loadEntity.setPersonId(customerUserId);
        setUpRoute(loadEntity, currentUserId);
        updateFreightBillPayTo(loadEntity);
        shipmentService.updateTimeZoneInfo(loadEntity);

        if (proposal != null) {
            updateLoadWithQuoteNumber(loadEntity, proposal);
            fillAccessorialsFromCostDetailItems(loadEntity, proposal);
            updateLoadWithCostDetails(loadEntity, proposal, currentUserId);
            shipmentService.checkBillToCreditLimit(loadEntity);
        }

        shipmentValidator.validate(loadEntity);

        LoadEntity updatedLoadEntity = updateLoadWithBolNumber(loadEntity);
        tenderLoad(proposal, oldStatus, oldCarrier, updatedLoadEntity);

        if (isShipmentInTransitCorrectly(loadEntity, oldStatus) || loadEntity.getStatus() != oldStatus) {
            shipmentEmailSender.sendLoadStatusChangedNotification(loadEntity, loadEntity.getStatus());
        }
        shipmentEmailSender.sendGoShipTrackingUpdateEmail(loadEntity, oldStatus);

        shipmentAlertService.processShipmentAlerts(updatedLoadEntity);

        return updatedLoadEntity;
    }

    @Override
    public LoadEntity save(LoadEntity loadEntity) throws ApplicationException {
        loadEntity = ltlShipmentDao.saveOrUpdate(loadEntity);

        // saving might change details which are present on BOL and other documents. Update them.
        shipmentDocumentService.generateShipmentDocuments(Collections.<DocumentTypes>emptySet(), loadEntity, false, SecurityUtils.getCurrentPersonId());

        return loadEntity;
    }

    @Override
    public void updateLoadWithQuoteNumber(LoadEntity loadEntity, ShipmentProposalBO proposal) {
        if (proposal != null && loadEntity.getNumbers() != null) {
            loadEntity.getNumbers().setCarrierQuoteNumber(proposal.getCarrierQuoteNumber());
            loadEntity.getNumbers().setServiceLevelCode(proposal.getServiceLevelCode());
            loadEntity.getNumbers().setServiceLevelDescription(proposal.getServiceLevelDescription());
        }
    }

    @Override
    public void updateLoadWithCostDetails(LoadEntity entity, ShipmentProposalBO proposal, Long currentUserId) {
        entity.setTravelTime(proposal.getEstimatedTransitTime());
        entity.setMileage(proposal.getMileage());

        setCarrier(entity, proposal, currentUserId);
        updateActiveCostDetail(entity, proposal);
    }

    @Override
    public void fillAccessorialsFromCostDetailItems(LoadEntity entity, ShipmentProposalBO dto) {
        if (entity.getLtlAccessorials() != null) {
            entity.getLtlAccessorials().clear();
        }
        Set<String> refTypes = getRefTypesForAccessorials(dto);
        if (!refTypes.isEmpty()) {
            List<AccessorialTypeEntity> pickupAndDeliveryAccessorialTypes = accessorialTypeDao
                    .getPickupAndDeliveryAccessorials(refTypes);
            if (entity.getLtlAccessorials() == null && !pickupAndDeliveryAccessorialTypes.isEmpty()) {
                entity.setLtlAccessorials(new HashSet<LtlLoadAccessorialEntity>());
            }
            for (AccessorialTypeEntity accessorialTypeCode : pickupAndDeliveryAccessorialTypes) {
                addAccessorialToLoad(entity, accessorialTypeCode);
            }
        }
    }

    @Override
    public LoadCostDetailsEntity getNewActiveCostDetail(ShipmentProposalBO proposal, LoadEntity load) {
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);

        costDetail.setServiceType(proposal.getServiceType());
        costDetail.setNewLiability(proposal.getNewLiability());
        costDetail.setUsedLiability(proposal.getUsedLiability());
        costDetail.setProhibitedCommodities(proposal.getProhibited());
        costDetail.setGuaranteedNameForBOL(proposal.getGuaranteedNameForBOL());
        costDetail.setPricingProfileDetailId(proposal.getPricingProfileId());
        costDetail.setCostDetailItems(new HashSet<CostDetailItemEntity>());
        if (load.getActiveCostDetail() != null) {
            costDetail.setInvoiceNumber(load.getActiveCostDetail().getInvoiceNumber());
            costDetail.setShipDate(load.getActiveCostDetail().getShipDate());
        }
        costDetail.setCostOverride(proposal.getCostOverride());
        costDetail.setRevenueOverride(proposal.getRevenueOverride());
        addCostItemsToCostDetail(proposal, load, costDetail);
        return costDetail;
    }

    private void updateLoadWithPricingDetails(LoadEntity loadEntity, ShipmentProposalBO proposal) {
        LoadPricingDetailsEntity loadPricDetail = getNewActiveLoadPricDetail(proposal, loadEntity);
        if (loadEntity.getLoadPricingDetails() == null) {
            loadEntity.setLoadPricingDetails(new HashSet<LoadPricingDetailsEntity>());
        } else if (loadEntity.getLoadPricingDetails() != null && loadEntity.getLoadPricingDetails().size() > 0) {
            LoadPricingDetailsEntity activeLoadPricDetail = loadEntity.getLoadPricingDetails().iterator().next();
            if (isLoadPricDetailNotChanged(loadPricDetail, activeLoadPricDetail)) {
                // active cost detail is not changed
                return;
            } else {
                loadEntity.getLoadPricingDetails().remove(activeLoadPricDetail);
                loadPricingDetailsDao.delete(activeLoadPricDetail);
            }
        }
        loadPricDetail.setLoad(loadEntity);
        loadEntity.getLoadPricingDetails().add(loadPricDetail);
    }

    private boolean isLoadPricDetailNotChanged(LoadPricingDetailsEntity loadPricDtl1,
            LoadPricingDetailsEntity loadPricDtl2) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        if (loadPricDtl1.getId() != null && loadPricDtl2.getId() != null) {
            equalsBuilder.append(loadPricDtl1.getId(), loadPricDtl2.getId());
            return equalsBuilder.isEquals();
        } else {
            equalsBuilder.append(loadPricDtl1.getSmc3MinimumCharge(), loadPricDtl2.getSmc3MinimumCharge())
                    .append(loadPricDtl1.getTotalChargeFromSmc3(), loadPricDtl2.getTotalChargeFromSmc3())
                    .append(loadPricDtl1.getDeficitChargeFromSmc3(), loadPricDtl2.getDeficitChargeFromSmc3())
                    .append(loadPricDtl1.getCostAfterDiscount(), loadPricDtl2.getCostAfterDiscount())
                    .append(loadPricDtl1.getMinimumCost(), loadPricDtl2.getMinimumCost())
                    .append(loadPricDtl1.getCostDiscount(), loadPricDtl2.getCostDiscount())
                    .append(loadPricDtl1.getCarrierFSId(), loadPricDtl2.getCarrierFSId())
                    .append(loadPricDtl1.getCarrierFuelDiscount(), loadPricDtl2.getCarrierFuelDiscount())
                    .append(loadPricDtl1.getPricingType(), loadPricDtl2.getPricingType())
                    .append(loadPricDtl1.getMovementType(), loadPricDtl2.getMovementType());
            return equalsBuilder.isEquals()
                    && isEqualDate(loadPricDtl1.getEffectiveDate(), loadPricDtl2.getEffectiveDate())
                    && loadPricDtl1.getLoadPricMaterialDtls() != null && loadPricDtl2.getLoadPricMaterialDtls() != null
                    && isLoadPricMaterialsNotChanged(loadPricDtl1.getLoadPricMaterialDtls(),
                            loadPricDtl2.getLoadPricMaterialDtls());
        }
    }

    private boolean isLoadPricMaterialsNotChanged(Set<LoadPricMaterialDtlsEntity> loadPricMatDtls1,
            Set<LoadPricMaterialDtlsEntity> loadPricMatDtls2) {
        if (loadPricMatDtls1 != null && loadPricMatDtls2 != null
                && loadPricMatDtls1.size() == loadPricMatDtls2.size()) {
            return isLoadPricMatSubCollection(loadPricMatDtls1, loadPricMatDtls2)
                    && isLoadPricMatSubCollection(loadPricMatDtls2, loadPricMatDtls1);
        } else if (loadPricMatDtls1 == null && loadPricMatDtls2 == null) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if loadPricMatDtls1 is a sub-collection of loadPricMatDtls2.
     */
    private boolean isLoadPricMatSubCollection(Set<LoadPricMaterialDtlsEntity> loadPricMatDtls1,
            Set<LoadPricMaterialDtlsEntity> loadPricMatDtls2) {
        for (Iterator<LoadPricMaterialDtlsEntity> it1 = loadPricMatDtls1.iterator(); it1.hasNext();) {
            LoadPricMaterialDtlsEntity item = it1.next();
            if (!isLoadPricMatCollectionContainsElement(loadPricMatDtls2, item)) {
                return false;
            }
        }
        return true;
    }

    private boolean isLoadPricMatCollectionContainsElement(Set<LoadPricMaterialDtlsEntity> items,
            LoadPricMaterialDtlsEntity item) {
        for (Iterator<LoadPricMaterialDtlsEntity> it2 = items.iterator(); it2.hasNext();) {
            if (isEqualLoadPricMatItems(item, it2.next())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEqualLoadPricMatItems(LoadPricMaterialDtlsEntity item1, LoadPricMaterialDtlsEntity item2) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(item1.getCharge(), item2.getCharge()).append(item1.getNmfcClass(), item2.getNmfcClass())
                .append(item1.getEnteredNmfcClass(), item2.getEnteredNmfcClass())
                .append(item1.getRate(), item2.getRate()).append(item1.getWeight(), item2.getWeight());
        return equalsBuilder.isEquals();
    }

    private LoadPricingDetailsEntity getNewActiveLoadPricDetail(ShipmentProposalBO proposal, LoadEntity load) {
        PricingDetailsBO pricingDetails = proposal.getPricingDetails();
        LoadPricingDetailsEntity loadPricingDetails = new LoadPricingDetailsEntity();
        if (pricingDetails != null) {
            loadPricingDetails.setSmc3MinimumCharge(pricingDetails.getSmc3MinimumCharge());
            loadPricingDetails.setTotalChargeFromSmc3(pricingDetails.getTotalChargeFromSmc3());
            loadPricingDetails.setDeficitChargeFromSmc3(pricingDetails.getDeficitChargeFromSmc3());
            loadPricingDetails.setCostAfterDiscount(pricingDetails.getCostAfterDiscount());
            loadPricingDetails.setMinimumCost(pricingDetails.getMinimumCost());
            loadPricingDetails.setCostDiscount(pricingDetails.getCostDiscount());
            loadPricingDetails.setCarrierFSId(pricingDetails.getCarrierFSId());
            loadPricingDetails.setCarrierFuelDiscount(pricingDetails.getCarrierFuelDiscount());
            loadPricingDetails.setPricingType(pricingDetails.getPricingType());
            loadPricingDetails.setMovementType(pricingDetails.getMovementType());
            loadPricingDetails.setEffectiveDate(pricingDetails.getEffectiveDate());
            Set<LoadPricMaterialDtlsEntity> loadPricMaterialDtls = new HashSet<LoadPricMaterialDtlsEntity>();
            if (pricingDetails.getSmc3CostDetails() != null) {
                final Set<LoadMaterialEntity> materials = load.getOrigin().getLoadMaterials();
                for (Smc3CostDetailsDTO smc3CostDetails : pricingDetails.getSmc3CostDetails()) {
                    LoadPricMaterialDtlsEntity loadPricMaterialDtl = new LoadPricMaterialDtlsEntity();
                    loadPricMaterialDtl.setCharge(smc3CostDetails.getCharge());
                    loadPricMaterialDtl.setNmfcClass(smc3CostDetails.getNmfcClass());
                    loadPricMaterialDtl.setEnteredNmfcClass(smc3CostDetails.getEnteredNmfcClass());
                    loadPricMaterialDtl.setRate(smc3CostDetails.getRate());
                    loadPricMaterialDtl.setWeight(smc3CostDetails.getWeight());
                    Optional<LoadMaterialEntity> materialOptional = materials.parallelStream().filter(material -> matchMaterial(material,
                                    smc3CostDetails.getEnteredNmfcClass(), new BigDecimal(smc3CostDetails.getWeight()))).findFirst();
                    if (materialOptional.isPresent()) {
                        LoadMaterialEntity material = materialOptional.get();
                        loadPricMaterialDtl.setDescription(material.getProductDescription());
                        loadPricMaterialDtl.setQuantity(material.getQuantity());
                        loadPricMaterialDtl.setNmfc(material.getNmfc());
                    }
                    loadPricMaterialDtl.setLoadPricingDetails(loadPricingDetails);
                    loadPricMaterialDtls.add(loadPricMaterialDtl);
                }
            }
            loadPricingDetails.setLoadPricMaterialDtls(loadPricMaterialDtls);
        }
        return loadPricingDetails;
    }

    private boolean matchMaterial(LoadMaterialEntity material, String nmfc, BigDecimal bigDecimalWeight) {
        return material.getCommodityClass().getDbCode().equalsIgnoreCase(nmfc)
                && material.getWeight().setScale(WEIGHT_PRECISION, RoundingMode.HALF_EVEN)
                .compareTo(bigDecimalWeight.setScale(WEIGHT_PRECISION, RoundingMode.HALF_EVEN)) == 0;
    }

    private void updateShipmentStatus(LoadEntity loadEntity, boolean autoDispatch, ShipmentStatus oldStatus) {
        if (autoDispatch && (oldStatus == null || oldStatus == ShipmentStatus.OPEN || oldStatus == ShipmentStatus.BOOKED)) {
            loadEntity.setStatus(ShipmentStatus.DISPATCHED);
        } else if (oldStatus == null || oldStatus == ShipmentStatus.OPEN) {
            loadEntity.setStatus(ShipmentStatus.BOOKED);
        }
    }

    private void tenderLoad(ShipmentProposalBO proposal, ShipmentStatus oldStatus, CarrierEntity prevCarrier, LoadEntity load)
            throws EdiProcessingException, InternalJmsCommunicationException {
        if (shouldEdiBeDispatched(oldStatus, load.getStatus()) && proposal != null && proposal.getCarrier() != null) {
            loadTenderService.tenderLoad(load, prevCarrier, oldStatus, load.getStatus());
        }
    }

    private ShipmentStatus getPrevStatus(Long shipmentId) {
        if (shipmentId != null) {
            return ltlShipmentDao.getShipmentStatus(shipmentId);
        }
        return null;
    }

    private CarrierEntity getPrevCarrier(Long shipmentId) {
        if (shipmentId != null) {
            CarrierEntity carrier = ltlShipmentDao.getShipmentCarrier(shipmentId);
            return carrier == null ? null : carrier;
        }
        return null;
    }

    private void updateActiveCostDetail(LoadEntity entity, ShipmentProposalBO proposal) {
        LoadCostDetailsEntity costDetail = getNewActiveCostDetail(proposal, entity);
        if (entity.getOrigin() != null) {
            costDetail.setShipDate(entity.getOrigin().getDeparture() == null
                    ? entity.getOrigin().getEarlyScheduledArrival() : entity.getOrigin().getDeparture());
        }
        if (entity.getCostDetails() == null) {
            entity.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        } else if (entity.getActiveCostDetail() != null) {
            LoadCostDetailsEntity activeCostDetail = entity.getActiveCostDetail();
            if (isCostDetailNotChanged(costDetail, activeCostDetail)) {
                // active cost detail is not changed
                return;
            } else {
                activeCostDetail.setStatus(Status.INACTIVE);
                AuditShipmentCostDetailsEntity carrInvAddDetail = activeCostDetail.getAuditShipmentCostDetails();
                if (carrInvAddDetail != null) {
                    AuditShipmentCostDetailsEntity currentAuditShipmentCostDetails = carrInvAddDetail.copy();
                    currentAuditShipmentCostDetails.setLoadCostDetail(costDetail);
                    costDetail.setAuditShipmentCostDetails(currentAuditShipmentCostDetails);
                }
            }
        }
        costDetail.setLoad(entity);
        entity.getCostDetails().add(costDetail);
    }

    /**
     * Get new Date instance with date equal to first parameter and time equal to second parameter.
     *
     * @param date
     *            date
     * @param time
     *            time
     * @return date + time
     */
    private Date getDateWithTime(Date date, Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, (int) DateUtils.getFragmentInHours(time, Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.MINUTE, (int) DateUtils.getFragmentInMinutes(time, Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.SECOND, (int) DateUtils.getFragmentInSeconds(time, Calendar.MINUTE));
        calendar.set(Calendar.MILLISECOND, (int) DateUtils.getFragmentInMilliseconds(time, Calendar.SECOND));
        return calendar.getTime();
    }

    private boolean isCostDetailNotChanged(LoadCostDetailsEntity cD1, LoadCostDetailsEntity cD2) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        if (cD1.getId() != null && cD2.getId() != null) {
            equalsBuilder.append(cD1.getId(), cD2.getId());
            return equalsBuilder.isEquals();
        } else {
            equalsBuilder.append(cD1.getStatus(), cD2.getStatus()).append(cD1.getTotalRevenue(), cD2.getTotalRevenue())
                    .append(cD1.getTotalCost(), cD2.getTotalCost()).append(cD1.getSentToFinance(), cD2.getSentToFinance())
                    .append(cD1.getServiceType(), cD2.getServiceType()).append(cD1.getNewLiability(), cD2.getNewLiability())
                    .append(cD1.getUsedLiability(), cD2.getUsedLiability()).append(cD1.getProhibitedCommodities(), cD2.getProhibitedCommodities())
                    .append(cD1.getInvoiceNumber(), cD2.getInvoiceNumber()).append(cD1.getGuaranteedBy(), cD2.getGuaranteedBy());
            return equalsBuilder.isEquals() && isEqualDate(cD1.getShipDate(), cD2.getShipDate())
                    && isEqualDate(cD1.getGeneralLedgerDate(), cD2.getGeneralLedgerDate()) && cD1.getCostDetailItems() != null
                    && cD2.getCostDetailItems() != null && isCostItemsNotChanged(cD1.getCostDetailItems(), cD2.getCostDetailItems());
        }
    }

    private boolean isCostItemsNotChanged(Set<CostDetailItemEntity> costDetailItems1, Set<CostDetailItemEntity> costDetailItems2) {
        if (costDetailItems1 != null && costDetailItems2 != null && costDetailItems1.size() == costDetailItems2.size()) {
            return isCostItemsSubCollection(costDetailItems1, costDetailItems2) && isCostItemsSubCollection(costDetailItems2, costDetailItems1);
        } else if (costDetailItems1 == null && costDetailItems2 == null) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if a is a sub-collection of b.
     */
    private boolean isCostItemsSubCollection(Set<CostDetailItemEntity> a, Set<CostDetailItemEntity> b) {
        for (Iterator<CostDetailItemEntity> it1 = a.iterator(); it1.hasNext();) {
            CostDetailItemEntity item = it1.next();
            if (!isCostItemsCollectionContainsElement(b, item)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCostItemsCollectionContainsElement(Set<CostDetailItemEntity> items, CostDetailItemEntity item) {
        for (Iterator<CostDetailItemEntity> it2 = items.iterator(); it2.hasNext();) {
            if (isEqualCostItems(item, it2.next())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEqualCostItems(CostDetailItemEntity item1, CostDetailItemEntity item2) {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        Long billToId1 = item1.getBillTo() == null ? null : item1.getBillTo().getId();
        Long billToId2 = item2.getBillTo() == null ? null : item2.getBillTo().getId();
        equalsBuilder.append(item1.getSubtotal(), item2.getSubtotal()).append(item1.getOwner(), item2.getOwner())
                .append(item1.getNote(), item2.getNote()).append(item1.getAccessorialType(), item2.getAccessorialType())
                .append(item1.getCarrierId(), item2.getCarrierId()).append(billToId1, billToId2);
        return equalsBuilder.isEquals();
    }

    private boolean isEqualDate(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.getTime() == date2.getTime();
        } else if (date1 != null || date2 != null) {
            return false;
        }
        return true;
    }

    /**
     * Get unique list of ref types from cost detail items of carrier and shipper owner.
     *
     * @param dto
     *            proposal with cost items
     * @return unique list of ref types from cost detail items of carrier and shipper owner
     */
    private Set<String> getRefTypesForAccessorials(ShipmentProposalBO dto) {
        return dto.getCostDetailItems().stream().filter(item -> item.getCostDetailOwner() != CostDetailOwner.B).map(CostDetailItemBO::getRefType)
                .collect(Collectors.toSet());
    }

    private void addAccessorialToLoad(LoadEntity entity, AccessorialTypeEntity accessorial) {
        LtlLoadAccessorialEntity accessorialEntity = new LtlLoadAccessorialEntity();
        accessorialEntity.setLoad(entity);
        accessorialEntity.setAccessorial(accessorial);
        entity.getLtlAccessorials().add(accessorialEntity);
    }

    private void updateFreightBillPayTo(LoadEntity loadEntity) {
        if (loadEntity.getFreightBillPayTo() != null
                && !FreightBillPayToDaoImpl.DEFAULT_FREIGHT_BILL_PAY_TO_ID.equals(loadEntity.getFreightBillPayTo().getId())) {
            freightBillPayToDao.saveOrUpdate(loadEntity.getFreightBillPayTo());
        }
    }

    private void setCarrier(LoadEntity entity, ShipmentProposalBO proposal, Long currentUserId) {
        CarrierDTO carrier = proposal.getCarrier();
        if (carrier != null) {
            if (entity.getCarrier() == null || !StringUtils.equalsIgnoreCase(carrier.getScac(), entity.getCarrier().getScac())) {
                entity.setCarrier(carrierDao.findByScac(carrier.getScac()));
                entity.setAwardedBy(currentUserId);
                setSpecialMessage(entity, proposal);
            }
        } else {
            entity.setCarrier(null);
            entity.setAwardedBy(null);
            entity.setSpecialMessage(null);
        }
    }

    private void setSpecialMessage(LoadEntity entity, ShipmentProposalBO proposal) {
        String specialMessage = proposal.getCarrier().getSpecialMessage();
        if (StringUtils.isBlank(specialMessage)) {
            entity.setSpecialMessage(null);
        } else if (entity.getSpecialMessage() == null) {
            LoadNoteEntity message = new LoadNoteEntity();
            message.setLoad(entity);
            message.setNote(specialMessage);
            entity.setSpecialMessage(message);
        } else {
            entity.getSpecialMessage().setNote(specialMessage);
        }
    }

    private LoadEntity updateLoadWithBolNumber(LoadEntity loadEntity) {
        LoadEntity savedEntity = ltlShipmentDao.saveOrUpdate(loadEntity);
        if (StringUtils.isBlank(savedEntity.getNumbers().getBolNumber())) {
            savedEntity.getNumbers().setBolNumber(String.valueOf(savedEntity.getId()));
            ltlShipmentDao.saveOrUpdate(savedEntity);
            return savedEntity;
        }
        return savedEntity;
    }

    private void setUpRoute(LoadEntity loadEntity, Long currentUserId) {
        RouteEntity route = addressDao.findRouteByAddresses(loadEntity.getOrigin().getAddress().getId(), loadEntity.getDestination().getAddress()
                .getId());
        if (route != null) {
            loadEntity.setRoute(route);
        } else {
            loadEntity.setRoute(buildRoute(loadEntity.getOrigin().getAddress(), loadEntity.getDestination().getAddress(), currentUserId));
        }
    }

    private RouteEntity buildRoute(AddressEntity origin, AddressEntity destination, Long currentUserId) {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setCreatedBy(currentUserId);

        routeEntity.setDestCity(destination.getCity());
        routeEntity.setDestCountry(destination.getCountry().getId());
        routeEntity.setDestState(destination.getStateCode());
        routeEntity.setDestZip(destination.getZip());

        routeEntity.setOriginCity(origin.getCity());
        routeEntity.setOriginCountry(origin.getCountry().getId());
        routeEntity.setOriginState(origin.getStateCode());
        routeEntity.setOriginZip(origin.getZip());
        return routeEntity;
    }

    private void addCostItemsToCostDetail(ShipmentProposalBO proposal, LoadEntity load, LoadCostDetailsEntity costDetail) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        final Long carrierId = load.getCarrier() == null ? null : load.getCarrier().getId();
        for (CostDetailItemBO costDetailItem : proposal.getCostDetailItems()) {
            if (isItemShouldBeIgnored(proposal.getCostDetailItems(), costDetailItem)) {
                continue;
            }
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
            itemEntity.setBillTo(load.getBillTo());
            itemEntity.setNote(costDetailItem.getNote());
            if (itemEntity.getOwner() == CostDetailOwner.C) {
                totalCost = totalCost.add(costDetailItem.getSubTotal());
            } else if (itemEntity.getOwner() == CostDetailOwner.S) {
                totalRevenue = totalRevenue.add(costDetailItem.getSubTotal());
            }
            costDetail.getCostDetailItems().add(itemEntity);
        }
        costDetail.setTotalCost(totalCost);
        costDetail.setTotalRevenue(totalRevenue);
    }

    /**
     * Cost detail item should be ignored only if it is a benchmark accessorial and there are no shipper or
     * carrier accessorials of the same type. SBR is a benchmark accessorial which is the same as SRA (Base
     * Rate for Shipper)/CRA (Base Rate for Carrier)/LH (Linehaul)
     *
     * @param costDetailItems
     *            list of all cost items
     * @param costDetailItem
     *            item to check
     * @return <code>true</code> if item should be ignored
     */
    private boolean isItemShouldBeIgnored(List<CostDetailItemBO> costDetailItems, CostDetailItemBO costDetailItem) {
        return costDetailItem.getCostDetailOwner() == CostDetailOwner.B && !"SBR".equals(costDetailItem.getRefType())
                && isNonBenchmarkAccessorialAbsent(costDetailItem.getRefType(), costDetailItems);
    }

    private boolean isNonBenchmarkAccessorialAbsent(final String refType, List<CostDetailItemBO> costDetailItems) {
        return costDetailItems.stream().noneMatch(bo -> bo.getRefType().equals(refType) && bo.getCostDetailOwner() != CostDetailOwner.B);
    }

    private boolean isShipmentInTransitCorrectly(LoadEntity loadEntity, ShipmentStatus oldStatus) {
        return loadEntity.getStatus() == ShipmentStatus.IN_TRANSIT && (oldStatus == ShipmentStatus.BOOKED || oldStatus == ShipmentStatus.DISPATCHED);
    }

    private boolean shouldEdiBeDispatched(ShipmentStatus oldStatus, ShipmentStatus currentStatus) {
        return (oldStatus == ShipmentStatus.DISPATCHED && (currentStatus == ShipmentStatus.DISPATCHED || currentStatus == ShipmentStatus.CANCELLED
                || currentStatus == ShipmentStatus.BOOKED || currentStatus == ShipmentStatus.OPEN))
                || currentStatus == ShipmentStatus.DISPATCHED;
    }
}
