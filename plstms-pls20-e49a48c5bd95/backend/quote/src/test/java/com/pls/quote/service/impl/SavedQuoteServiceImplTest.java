package com.pls.quote.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.quote.dao.SavedQuoteDao;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsItemEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Test for {@link SavedQuoteServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class SavedQuoteServiceImplTest {
    private static final long ORGANIZATION_ID = (long) (Math.random() * 100);

    @Mock
    private SavedQuoteDao savedQuoteDao;
    @Mock
    private CarrierDao carrierDao;

    @InjectMocks
    private SavedQuoteServiceImpl savedQuoteService;

    @Test
    public void shouldFindSavedQuotes() throws ApplicationException {
        List<SavedQuoteBO> savedQuotes = Arrays.asList(new SavedQuoteBO());

        when(savedQuoteDao.findSavedQuotes(ORGANIZATION_ID, null, null, null)).thenReturn(savedQuotes);

        List<SavedQuoteBO> result = savedQuoteService.findSavedQuotes(ORGANIZATION_ID, null, null);

        assertSame(savedQuotes, result);
    }

    @Test
    public void shouldGetSavedQuoteById() {
        final long quoteId = (long) (Math.random() * 100);
        SavedQuoteEntity savedQuote = new SavedQuoteEntity();
        when(savedQuoteDao.find(quoteId)).thenReturn(savedQuote);

        SavedQuoteEntity result = savedQuoteService.getSavedQuoteById(quoteId);

        assertSame(savedQuote, result);
    }

    @Test
    public void shouldDeleteSavedQuote() {
        final long quoteId = (long) (Math.random() * 100);

        savedQuoteService.deleteSavedQuote(quoteId);

        verify(savedQuoteDao).updateStatus(quoteId, Status.INACTIVE);
    }

    @Test
    public void shouldSaveQuote() {
        SavedQuoteEntity savedQuote = prepareSavedQuote();
        ShipmentProposalBO proposal = prepareProposal();
        CarrierEntity carrier = prepareCarrier();

        when(carrierDao.findByScac(proposal.getCarrier().getScac())).thenReturn(carrier);

        List<CostDetailItemBO> costDetailsList = new ArrayList<CostDetailItemBO>();
        costDetailsList.add(getCostDetailItem(CostDetailOwner.C));
        costDetailsList.add(getCostDetailItem(CostDetailOwner.C));
        costDetailsList.add(getCostDetailItem(CostDetailOwner.S));
        costDetailsList.add(getCostDetailItem(CostDetailOwner.S));
        costDetailsList.add(getCostDetailItem(CostDetailOwner.B));
        costDetailsList.get(3).setGuaranteedBy((long) (Math.random() * 100));
        costDetailsList.get(4).setRefType("SBR");
        proposal.setCostDetailItems(costDetailsList);

        SavedQuoteEntity result = savedQuoteService.saveQuote(savedQuote, ORGANIZATION_ID, proposal);
        verify(savedQuoteDao).saveOrUpdate(result);

        assertEquals(Status.ACTIVE, result.getStatus());
        assertSame(ORGANIZATION_ID, result.getCustomer().getId());

        assertSame(carrier, result.getCarrier());
        assertSame(proposal.getCarrier().getSpecialMessage(), result.getSpecialMessage());
        assertSame(proposal.getMileage(), result.getMileage());
        assertSame(StatusYesNo.YES, result.getCostOverride());
        assertSame(StatusYesNo.YES, result.getRevenueOverride());

        SavedQuoteCostDetailsEntity costDetails = result.getCostDetails();
        assertSame(proposal.getEstimatedTransitTime(), costDetails.getEstimatedTransitTime());
        assertSame(proposal.getEstimatedTransitDate(), costDetails.getEstTransitDate());
        assertEquals(Status.ACTIVE, costDetails.getStatus());
        assertSame(proposal.getServiceType(), costDetails.getServiceType());
        assertSame(proposal.getNewLiability(), costDetails.getNewLiability());
        assertSame(proposal.getUsedLiability(), costDetails.getUsedLiability());
        assertSame(proposal.getProhibited(), costDetails.getProhibitedCommodities());
        assertSame(proposal.getPricingProfileId(), costDetails.getPricingProfileDetailId());
        assertSame(proposal.getGuaranteedNameForBOL(), costDetails.getGuaranteedNameForBOL());

        assertSame(costDetailsList.get(3).getGuaranteedBy(), costDetails.getGuaranteedBy());

        assertSame(costDetailsList.size(), costDetails.getCostDetailsItems().size());
        for (CostDetailItemBO costDetail : costDetailsList) {
            SavedQuoteCostDetailsItemEntity item = findCostDetailsItem(costDetails.getCostDetailsItems(), costDetail);
            assertNotNull(item);
        }

        assertEquals(costDetailsList.get(0).getSubTotal().add(costDetailsList.get(1).getSubTotal()), costDetails.getTotalCost());
        assertEquals(costDetailsList.get(2).getSubTotal().add(costDetailsList.get(3).getSubTotal()), costDetails.getTotalRevenue());
    }

    private SavedQuoteCostDetailsItemEntity findCostDetailsItem(Set<SavedQuoteCostDetailsItemEntity> costDetailsItems, CostDetailItemBO bo) {
        for (SavedQuoteCostDetailsItemEntity item : costDetailsItems) {
            if (item.getRefType().equals(bo.getRefType()) && item.getOwner().equals(bo.getCostDetailOwner())
                    && item.getSubTotal().equals(bo.getSubTotal())) {
                return item;
            }
        }
        return null;
    }

    private CarrierEntity prepareCarrier() {
        CarrierEntity entity = new CarrierEntity();
        entity.setId((long) (Math.random() * 100));
        return entity;
    }

    private ShipmentProposalBO prepareProposal() {
        ShipmentProposalBO proposal = new ShipmentProposalBO();
        CarrierDTO carrier = new CarrierDTO();
        carrier.setScac("scac" + Math.random());
        carrier.setSpecialMessage("specialMessage" + Math.random());
        proposal.setCarrier(carrier);
        proposal.setMileage((int) (Math.random() * 100));
        proposal.setEstimatedTransitTime((long) (Math.random() * 100));
        proposal.setEstimatedTransitDate(new Date());
        proposal.setServiceType(LtlServiceType.BOTH);
        proposal.setNewLiability(BigDecimal.valueOf(Math.random()));
        proposal.setUsedLiability(BigDecimal.valueOf(Math.random()));
        proposal.setProhibited("prohibited" + Math.random());
        proposal.setPricingProfileId((long) (Math.random() * 100));
        proposal.setGuaranteedNameForBOL("guaranteedNameForBOL" + Math.random());
        proposal.setCostOverride(StatusYesNo.YES);
        proposal.setRevenueOverride(StatusYesNo.YES);
        return proposal;
    }

    private SavedQuoteEntity prepareSavedQuote() {
        return new SavedQuoteEntity();
    }

    private CostDetailItemBO getCostDetailItem(CostDetailOwner costDetailOwner) {
        CostDetailItemBO costDetailItem = new CostDetailItemBO();
        costDetailItem.setCostDetailOwner(costDetailOwner);
        costDetailItem.setRefType("refType" + Math.random());
        costDetailItem.setSubTotal(BigDecimal.valueOf(Math.random()));
        return costDetailItem;
    }
}
