package com.pls.shipment.service.billing.audit.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.AccessorialType;
import com.pls.core.shared.Reasons;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Test for {@link SidHarveyAuditReasonServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class SidHarveyAuditReasonServiceImplTest {
    private SidHarveyAuditReasonServiceImpl sut = new SidHarveyAuditReasonServiceImpl();

    private BigDecimal margin;
    private CostDetailItemEntity mscRevenueItem;

    private CostDetailItemEntity mscCostItem;

    @Before
    public void init() {
        margin = SidHarveyAuditReasonServiceImpl.MAX_MARGIN.add(new BigDecimal(1));
    }

    @Test
    public void shouldGetNoReasons() {
        LoadEntity load = createTestLoad();
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertTrue(reasons.toString(), reasons.isEmpty());
    }

    @Test
    public void shouldGetMarginReviewReason() {
        margin = SidHarveyAuditReasonServiceImpl.MAX_MARGIN.subtract(new BigDecimal(1));
        LoadEntity load = createTestLoad();
        load.getActiveCostDetail().setTotalRevenue(new BigDecimal(51));
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.MARGIN_REVIEW), reasons);
    }

    @Test
    public void shouldGetHighRevenueReviewReason() {
        LoadEntity load = createTestLoad();
        load.getActiveCostDetail().setTotalRevenue(SidHarveyAuditReasonServiceImpl.MAX_REVENUE.add(new BigDecimal(1)));
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.HIGH_REVENUE_REVIEW), reasons);
    }

    @Test
    public void shouldGetHighCostReviewReason() {
        LoadEntity load = createTestLoad();
        load.getActiveCostDetail().setTotalCost(SidHarveyAuditReasonServiceImpl.MAX_COST.add(new BigDecimal(1)));
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.HIGH_COST_REVIEW), reasons);
    }

    @Test
    public void shouldGetAccessorialReviewReasonForMSCWithoutComment() {
        LoadEntity load = createTestLoad();
        mscRevenueItem.setNote(null);
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.ACCESSORIAL_REVIEW), reasons);
    }

    @Test
    public void shouldGetAccessorialReviewReasonForDifferentCostAndRevenue() {
        LoadEntity load = createTestLoad();
        mscRevenueItem.setSubtotal(new BigDecimal(2));
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.ACCESSORIAL_REVIEW), reasons);
    }

    @Test
    public void shouldGetAccessorialReviewReasonForHighAccessorialsCost() {
        LoadEntity load = createTestLoad();
        mscRevenueItem.setSubtotal(new BigDecimal(9));
        mscCostItem.setSubtotal(new BigDecimal(9));
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(Arrays.asList(Reasons.ACCESSORIAL_REVIEW), reasons);
    }

    @Test
    public void shouldGetAllReasons() {
        margin = SidHarveyAuditReasonServiceImpl.MAX_MARGIN.subtract(new BigDecimal(1));
        LoadEntity load = createTestLoad();
        load.getActiveCostDetail().setTotalRevenue(SidHarveyAuditReasonServiceImpl.MAX_REVENUE.add(new BigDecimal(1)));
        load.getActiveCostDetail().setTotalCost(SidHarveyAuditReasonServiceImpl.MAX_COST.add(new BigDecimal(1)));
        mscRevenueItem.setNote(null);
        List<Reasons> reasons = sut.getBillingAuditReasonForSidHarvey(load);
        Assert.assertEquals(
                Arrays.asList(Reasons.MARGIN_REVIEW, Reasons.ACCESSORIAL_REVIEW, Reasons.HIGH_REVENUE_REVIEW, Reasons.HIGH_COST_REVIEW), reasons);
    }

    private LoadEntity createTestLoad() {
        LoadEntity load = new LoadEntity();
        load.setOrganization(new CustomerEntity());
        load.getOrganization().setId(SidHarveyAuditReasonServiceImpl.SID_HARVEY_ID);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity costDetail = Mockito.spy(new LoadCostDetailsEntity());
        costDetail.setStatus(Status.ACTIVE);
        Mockito.when(costDetail.getMargin()).thenReturn(margin);
        costDetail.setTotalRevenue(new BigDecimal(50));
        costDetail.setTotalCost(new BigDecimal(50).subtract(SidHarveyAuditReasonServiceImpl.MAX_MARGIN));
        costDetail.setCostDetailItems(new HashSet<CostDetailItemEntity>());
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.S, AccessorialType.SHIPPER_BASE_RATE.getType(), new BigDecimal(37)));
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.C, AccessorialType.CARRIER_BASE_RATE.getType(),
                new BigDecimal(32).subtract(SidHarveyAuditReasonServiceImpl.MAX_MARGIN)));
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.S, AccessorialType.FUEL_SURCHARGE.getType(), new BigDecimal(10)));
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.C, AccessorialType.FUEL_SURCHARGE.getType(), new BigDecimal(5)));
        mscRevenueItem = getCostItem(CostDetailOwner.S, AccessorialType.MISC.getType(), new BigDecimal(1));
        mscRevenueItem.setNote("MSC accessorial");
        costDetail.getCostDetailItems().add(mscRevenueItem);
        mscCostItem = getCostItem(CostDetailOwner.C, AccessorialType.MISC.getType(), new BigDecimal(1));
        costDetail.getCostDetailItems().add(mscCostItem);
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.S, "BLB", new BigDecimal(2)));
        costDetail.getCostDetailItems().add(getCostItem(CostDetailOwner.C, "BLB", new BigDecimal(2)));
        activeCostDetails.add(costDetail);
        load.setActiveCostDetails(activeCostDetails);
        return load;
    }

    private CostDetailItemEntity getCostItem(CostDetailOwner owner, String accessorialType, BigDecimal subtotal) {
        CostDetailItemEntity costItem = new CostDetailItemEntity();
        costItem.setOwner(owner);
        costItem.setAccessorialType(accessorialType);
        costItem.setSubtotal(subtotal);
        return costItem;
    }

}