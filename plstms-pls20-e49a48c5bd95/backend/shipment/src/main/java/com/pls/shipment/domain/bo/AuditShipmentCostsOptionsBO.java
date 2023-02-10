package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;

/**
 * DTO for audit shipment costs Options.
 *
 * @author Brichak Aleksandr
 */
public class AuditShipmentCostsOptionsBO {

    private Integer loadVersion;
    private ShipmentProposalBO proposal;
    private String note;
    private AuditShipmentCostsBO auditShipmentCosts;
    private BigDecimal costDifference;

    public Integer getLoadVersion() {
        return loadVersion;
    }

    public void setLoadVersion(Integer loadVersion) {
        this.loadVersion = loadVersion;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ShipmentProposalBO getProposal() {
        return proposal;
    }

    public void setProposal(ShipmentProposalBO proposal) {
        this.proposal = proposal;
    }

    public BigDecimal getCostDifference() {
        return costDifference;
    }

    public void setCostDifference(BigDecimal costDifference) {
        this.costDifference = costDifference;
    }

    public AuditShipmentCostsBO getAuditShipmentCosts() {
        return auditShipmentCosts;
    }

    public void setAuditShipmentCosts(AuditShipmentCostsBO auditShipmentCosts) {
        this.auditShipmentCosts = auditShipmentCosts;
    }

}
