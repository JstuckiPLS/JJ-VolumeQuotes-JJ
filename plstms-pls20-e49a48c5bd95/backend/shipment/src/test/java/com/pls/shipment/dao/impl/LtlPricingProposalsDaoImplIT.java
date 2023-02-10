package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.shipment.dao.LtlPricingProposalsDao;
import com.pls.shipment.domain.LtlPricPropCostDetailsEntity;
import com.pls.shipment.domain.LtlPricingProposalsEntity;

/**
 * Tests for {@link com.pls.shipment.dao.impl.LtlPricingProposalsDaoImpl}.
 *
 * @author Ashwini Neelgund
 */
public class LtlPricingProposalsDaoImplIT extends AbstractDaoTest {

    @Autowired
    private LtlPricingProposalsDao sut;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void testSavePricingProposalEntities() {
        List<LtlPricingProposalsEntity> proposalEntities = buildProposalEntities();
        sut.persistBatch(proposalEntities);
        flushAndClearSession();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        proposalEntities = sut.getAll();
        boolean loadPersisted = false;
        for (LtlPricingProposalsEntity ltlPricingProposalsEntity : proposalEntities) {
            if (ltlPricingProposalsEntity.getLoadId() == 1) {
                loadPersisted = true;
            }
        }
        Assert.assertNotNull(proposalEntities);
        Assert.assertFalse(proposalEntities.isEmpty());
        Assert.assertTrue(loadPersisted);
    }

    private List<LtlPricingProposalsEntity> buildProposalEntities() {
        List<LtlPricingProposalsEntity> proposalEntities = new ArrayList<LtlPricingProposalsEntity>();
        LtlPricingProposalsEntity proposalEntity = new LtlPricingProposalsEntity();
        proposalEntity.setLoadId(1L);
        proposalEntity.setLtlPricProfId(1L);
        proposalEntity.setServiceType(LtlServiceType.BOTH);
        proposalEntity.setPalletPackageType(false);
        proposalEntity.setHazmat(false);
        proposalEntity.setProposalSelected(false);
        proposalEntity.setTotalWeight(new BigDecimal(123));
        proposalEntity.setTotalQuantity(2);
        proposalEntity.setTotalPieces(4);
        proposalEntity.setTotalRevenue(new BigDecimal(200));
        proposalEntity.setTotalCost(new BigDecimal(150));
        proposalEntity.setTransitTime(2L);
        Set<LtlPricPropCostDetailsEntity> pricPropDtlEntities = new HashSet<LtlPricPropCostDetailsEntity>();
        LtlPricPropCostDetailsEntity pricPropDtlEntity = new LtlPricPropCostDetailsEntity();
        pricPropDtlEntity.setBillable(true);
        pricPropDtlEntity.setLtlPricingId(1L);
        pricPropDtlEntity.setLtlPricProposalEntity(proposalEntity);
        pricPropDtlEntity.setRefType("SRA");
        pricPropDtlEntity.setShipCarr(CostDetailOwner.S);
        pricPropDtlEntity.setSubTotal(new BigDecimal(20));
        pricPropDtlEntity.setCreatedBy(2L);
        pricPropDtlEntities.add(pricPropDtlEntity);
        proposalEntity.setPricPropDtlEntity(pricPropDtlEntities);
        proposalEntities.add(proposalEntity);
        return proposalEntities;
    }

}
