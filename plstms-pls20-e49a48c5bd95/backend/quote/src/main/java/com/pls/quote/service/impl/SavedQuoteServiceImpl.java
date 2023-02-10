package com.pls.quote.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.quote.dao.SavedQuoteDao;
import com.pls.quote.dao.SavedQuotePricDtlsDao;
import com.pls.quote.service.SavedQuoteService;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsItemEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Saved Quote service implementation.
 *
 * @author Mikhail Boldinov, 27/03/13
 */
@Service
@Transactional
public class SavedQuoteServiceImpl implements SavedQuoteService {
    @Autowired
    private SavedQuoteDao savedQuoteDao;
    @Autowired
    private CarrierDao carrierDao;
    @Autowired
    private SavedQuotePricDtlsDao savedQuotePricDtlsDao;

    @Override
    public List<SavedQuoteBO> findSavedQuotes(Long organizationId, Date fromDate, Date toDate)
            throws ApplicationException {
        return savedQuoteDao.findSavedQuotes(organizationId, SecurityUtils.getCurrentPersonId(), fromDate,
                toDate);
    }

    @Override
    public SavedQuoteEntity getSavedQuoteById(Long quoteId) {
        return savedQuoteDao.find(quoteId);
    }

    @Override
    public SavedQuoteEntity saveQuote(SavedQuoteEntity savedQuote, Long organizationId, ShipmentProposalBO proposal) {
        savedQuote.setStatus(Status.ACTIVE);
        if (organizationId != null) {
            CustomerEntity customer = new CustomerEntity();
            customer.setId(organizationId);
            savedQuote.setCustomer(customer);
        } else {
            savedQuote.setCustomer(null);
        }

        if (savedQuote.getRoute() != null && savedQuote.getRoute().getCreatedBy() == null) {
            savedQuote.getRoute().setCreatedBy(SecurityUtils.getCurrentPersonId());
        }
        savedQuote.setMileage(proposal.getMileage());
        savedQuote.setCarrier(carrierDao.findByScac(proposal.getCarrier().getScac()));
        savedQuote.setSpecialMessage(proposal.getCarrier().getSpecialMessage());
        savedQuote.setRevenueOverride(proposal.getRevenueOverride());
        savedQuote.setCostOverride(proposal.getCostOverride());
        updateCostDetails(savedQuote, proposal);
        savedQuote.setBlockedFromBooking(proposal.getBlockedFrmBkng());
        savedQuote.setExternalUuid(proposal.getExternalUuid());
        savedQuote.setCarrierQuoteNumber(proposal.getCarrierQuoteNumber());
        savedQuote.setServiceLevelCode(proposal.getServiceLevelCode());
        savedQuote.setServiceLevelDescription(proposal.getServiceLevelDescription());

        savedQuoteDao.saveOrUpdate(savedQuote);
        return savedQuote;
    }

    @Override
    public void deleteSavedQuote(Long quoteId) {
        savedQuoteDao.updateStatus(quoteId, Status.INACTIVE);
    }

    private void updateCostDetails(SavedQuoteEntity entity, ShipmentProposalBO proposal) {
        SavedQuoteCostDetailsEntity costDetail = entity.getCostDetails();
        if (costDetail == null) {
            costDetail = new SavedQuoteCostDetailsEntity();
            costDetail.setQuote(entity);
            entity.setCostDetails(costDetail);
        }
        costDetail.setEstimatedTransitTime(proposal.getEstimatedTransitTime());
        costDetail.setEstTransitDate(proposal.getEstimatedTransitDate());
        costDetail.setStatus(Status.ACTIVE);

        costDetail.setServiceType(proposal.getServiceType());
        costDetail.setNewLiability(proposal.getNewLiability());
        costDetail.setUsedLiability(proposal.getUsedLiability());
        costDetail.setProhibitedCommodities(proposal.getProhibited());
        costDetail.setGuaranteedNameForBOL(proposal.getGuaranteedNameForBOL());
        costDetail.setPricingProfileDetailId(proposal.getPricingProfileId());
        if (costDetail.getCostDetailsItems() == null) {
            costDetail.setCostDetailsItems(new HashSet<SavedQuoteCostDetailsItemEntity>());
        } else {
            costDetail.getCostDetailsItems().clear();
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (CostDetailItemBO costDetailItem : proposal.getCostDetailItems()) {
            SavedQuoteCostDetailsItemEntity itemEntity = new SavedQuoteCostDetailsItemEntity();
            itemEntity.setCostDetails(costDetail);
            itemEntity.setRefType(costDetailItem.getRefType());
            if (costDetailItem.getGuaranteedBy() != null) {
                costDetail.setGuaranteedBy(costDetailItem.getGuaranteedBy());
            }
            itemEntity.setOwner(costDetailItem.getCostDetailOwner());
            itemEntity.setSubTotal(costDetailItem.getSubTotal());
            if (itemEntity.getOwner() == CostDetailOwner.C) {
                totalCost = totalCost.add(costDetailItem.getSubTotal());
            } else if (itemEntity.getOwner() == CostDetailOwner.S) {
                totalRevenue = totalRevenue.add(costDetailItem.getSubTotal());
            }
            costDetail.getCostDetailsItems().add(itemEntity);
        }
        costDetail.setTotalCost(totalCost);
        costDetail.setTotalRevenue(totalRevenue);
    }

    @Override
    public SavedQuotePricDtlsEntity getSavedQuotePricDtls(Long quoteId) {
        return savedQuotePricDtlsDao.getSavedQuotePricDtls(quoteId);
    }

    @Override
    public List<Long> getListOfLoadIds(Long quoteId) {
        return savedQuoteDao.getListOfLoadIds(quoteId);
    }
}
