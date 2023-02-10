package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.DisputeCost;
import com.pls.core.shared.StatusYesNo;
import com.pls.core.shared.UpdateRevenueOption;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsBO;
import com.pls.shipment.domain.bo.AuditShipmentCostsOptionsBO;
import com.pls.shipment.service.AuditShipmentCostsService;

/**
 * Audit Shipment Costs Service tests.
 *
 * @author Brichak Aleksandr
 */
public class AuditShipmentCostsServiceImplIT extends BaseServiceITClass {

    @Autowired
    private AuditShipmentCostsService auditShipmentCostsService;

    @Test
    public void shouldSaveAuditShipmentCostsOptions() throws Exception {

        AuditShipmentCostsOptionsBO auditShipmentCostsOptions = getAuditShipmentCostsOptionsBO();
        ShipmentProposalBO shipmentProposal = prepareProposal();
        auditShipmentCostsOptions.setProposal(shipmentProposal);
        AuditShipmentCostDetailsEntity auditShipmentCostDetailsEntity = getAuditShipmentCostDetailsEntity();
        LoadEntity load = auditShipmentCostsService.saveAuditShipmentCostsOptions(1L, auditShipmentCostsOptions,
                auditShipmentCostDetailsEntity);
        Assert.assertNotNull(load);
        AuditShipmentCostDetailsEntity auditShipmentCostDetail = load.getActiveCostDetail()
                .getAuditShipmentCostDetails();
        Assert.assertNotNull(auditShipmentCostDetail);
        Assert.assertEquals(DisputeCost.ACCOUNT_EXEC, auditShipmentCostDetail.getDisputeCost());
        Assert.assertTrue(auditShipmentCostDetail.getRequestPaperwork());
        Assert.assertEquals(UpdateRevenueOption.INVOICE_WITHOUT_MARKUP, auditShipmentCostDetail.getUpdateRevenue());
        Assert.assertEquals(new BigDecimal("10.00"), auditShipmentCostDetail.getUpdateRevenueValue());
    }

    @Test
    public void shouldGetInvoiceAdditionalDetails() throws Exception {

        AuditShipmentCostsOptionsBO auditShipmentCostsOptions = getAuditShipmentCostsOptionsBO();
        ShipmentProposalBO shipmentProposal = prepareProposal();
        auditShipmentCostsOptions.setProposal(shipmentProposal);
        AuditShipmentCostDetailsEntity auditShipmentCostDetailsEntity = getAuditShipmentCostDetailsEntity();
        LoadEntity load = auditShipmentCostsService.saveAuditShipmentCostsOptions(1L, auditShipmentCostsOptions,
                auditShipmentCostDetailsEntity);
        Assert.assertNotNull(load);

        AuditShipmentCostDetailsEntity auditShipmentCostDetail = auditShipmentCostsService
                .getInvoiceAdditionalDetails(load.getId());
        Assert.assertNotNull(auditShipmentCostDetail);
        Assert.assertEquals(DisputeCost.ACCOUNT_EXEC, auditShipmentCostDetail.getDisputeCost());
        Assert.assertTrue(auditShipmentCostDetail.getRequestPaperwork());
        Assert.assertEquals(UpdateRevenueOption.INVOICE_WITHOUT_MARKUP, auditShipmentCostDetail.getUpdateRevenue());
        Assert.assertEquals(new BigDecimal("10.00"), auditShipmentCostDetail.getUpdateRevenueValue());
    }

    private AuditShipmentCostDetailsEntity getAuditShipmentCostDetailsEntity() {
        AuditShipmentCostDetailsEntity auditShipmentCostDetailsEntity = new AuditShipmentCostDetailsEntity();
        auditShipmentCostDetailsEntity.setDisputeCost(DisputeCost.ACCOUNT_EXEC);
        auditShipmentCostDetailsEntity.setRequestPaperwork(Boolean.TRUE);
        auditShipmentCostDetailsEntity.setUpdateRevenue(UpdateRevenueOption.INVOICE_WITHOUT_MARKUP);
        auditShipmentCostDetailsEntity.setUpdateRevenueValue(BigDecimal.TEN);
        return auditShipmentCostDetailsEntity;
    }

    private AuditShipmentCostsOptionsBO getAuditShipmentCostsOptionsBO() {
        AuditShipmentCostsOptionsBO auditShipmentCostsOptions = new AuditShipmentCostsOptionsBO();
        AuditShipmentCostsBO auditShipmentCosts = new AuditShipmentCostsBO();
        auditShipmentCosts.setRequestPaperwork(Boolean.TRUE);
        auditShipmentCosts.setUpdateRevenue(UpdateRevenueOption.INVOICE_WITHOUT_MARKUP);
        auditShipmentCosts.setDisputeCost(DisputeCost.ACCOUNT_EXEC);
        auditShipmentCosts.setUpdateRevenueValue(BigDecimal.TEN);
        auditShipmentCostsOptions.setAuditShipmentCosts(auditShipmentCosts);
        auditShipmentCostsOptions.setCostDifference(BigDecimal.TEN);
        return auditShipmentCostsOptions;
    }

    private ShipmentProposalBO prepareProposal() {
        ShipmentProposalBO proposal = new ShipmentProposalBO();
        CarrierDTO carrier = new CarrierDTO();
        carrier.setScac("scac");
        carrier.setSpecialMessage("specialMessage");
        proposal.setCarrier(carrier);
        proposal.setMileage(100);
        proposal.setEstimatedTransitTime(10L);
        proposal.setEstimatedTransitDate(new Date());
        proposal.setServiceType(LtlServiceType.BOTH);
        proposal.setNewLiability(BigDecimal.TEN);
        proposal.setUsedLiability(BigDecimal.TEN);
        proposal.setProhibited("prohibited");
        proposal.setPricingProfileId(101L);
        proposal.setGuaranteedNameForBOL("guaranteedNameForBOL");
        proposal.setCostOverride(StatusYesNo.YES);
        proposal.setRevenueOverride(StatusYesNo.YES);
        List<CostDetailItemBO> costDetails = getCostDetails();
        proposal.setCostDetailItems(costDetails);
        return proposal;
    }

    private List<CostDetailItemBO> getCostDetails() {
        List<CostDetailItemBO> costDetails = new ArrayList<CostDetailItemBO>();
        costDetails.add(getCostDetailItem(CostDetailOwner.C));
        costDetails.add(getCostDetailItem(CostDetailOwner.C));
        costDetails.add(getCostDetailItem(CostDetailOwner.S));
        costDetails.add(getCostDetailItem(CostDetailOwner.S));
        costDetails.add(getCostDetailItem(CostDetailOwner.B));
        costDetails.get(3).setGuaranteedBy(12L);
        costDetails.get(4).setRefType("SBR");
        return costDetails;
    }

    private CostDetailItemBO getCostDetailItem(CostDetailOwner costDetailOwner) {
        CostDetailItemBO costDetailItem = new CostDetailItemBO();
        costDetailItem.setCostDetailOwner(costDetailOwner);
        costDetailItem.setRefType("refType");
        costDetailItem.setSubTotal(BigDecimal.TEN);
        return costDetailItem;
    }
}
