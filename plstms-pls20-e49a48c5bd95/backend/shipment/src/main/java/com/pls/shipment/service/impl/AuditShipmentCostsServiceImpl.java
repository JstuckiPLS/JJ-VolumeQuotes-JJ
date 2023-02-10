package com.pls.shipment.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.dao.AuditShipmentCostDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsOptionsBO;
import com.pls.shipment.service.AuditShipmentCostsService;
import com.pls.shipment.service.ShipmentEventService;
import com.pls.shipment.service.ShipmentNoteService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.audit.LoadEventBuilder;

/**
 * Implementation of {@link AuditShipmentCostsService}.
 *
 * @author Brichak Aleksandr
 */
@Service
@Transactional
public class AuditShipmentCostsServiceImpl implements AuditShipmentCostsService {

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private ShipmentSavingService shipmentSavingService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentEventService shipmentEventService;

    @Autowired
    private ShipmentNoteService shipmentNoteService;

    @Autowired
    private AuditShipmentCostDetailsDao auditShipmentCostDetailsDao;

    @Override
    public LoadEntity saveAuditShipmentCostsOptions(Long shipmentId, AuditShipmentCostsOptionsBO auditCostBO,
            AuditShipmentCostDetailsEntity invoiceAdditionalDetail) {
        LoadEntity load = shipmentService.getShipmentWithAllDependencies(shipmentId);
        // Set version of load for concurrency control
        if (auditCostBO.getLoadVersion() != null) {
            load.setVersion(auditCostBO.getLoadVersion());
        }
        createNewActiveCostDetail(load, auditCostBO.getProposal(), invoiceAdditionalDetail);
        billingAuditService.updateBillingAuditReasonForLoad(load, load.getFinalizationStatus());
        addEventAndNote(shipmentId, auditCostBO, load);
        return ltlShipmentDao.saveOrUpdate(load);
    }

    @Override
    public AuditShipmentCostDetailsEntity getInvoiceAdditionalDetails(Long loadId) {
        return auditShipmentCostDetailsDao.findAuditShipmentCostDetailsByLoadId(loadId);
    }

    private void addEventAndNote(Long shipmentId, AuditShipmentCostsOptionsBO dto, LoadEntity load) {
        if (dto.getAuditShipmentCosts() != null && dto.getAuditShipmentCosts().getUpdateRevenue() != null) {
            shipmentEventService.save(LoadEventBuilder.buildDisputeAndUpdateEvent(load.getId(),
                    dto.getAuditShipmentCosts(), dto.getCostDifference()));
        }
        if (StringUtils.isNotBlank(dto.getNote())) {
            shipmentNoteService.addNewNote(shipmentId, dto.getNote());
        }
    }

    private void createNewActiveCostDetail(LoadEntity entity, ShipmentProposalBO proposal,
            AuditShipmentCostDetailsEntity auditShipmentCostDetails) {
        LoadCostDetailsEntity newActiveCostDetail = shipmentSavingService.getNewActiveCostDetail(proposal, entity);
        newActiveCostDetail.setLoad(entity);
        entity.getCostDetails().add(newActiveCostDetail);
        auditShipmentCostDetails.setLoadCostDetail(newActiveCostDetail);
        newActiveCostDetail.setAuditShipmentCostDetails(auditShipmentCostDetails);

        LoadCostDetailsEntity activeCostDetail = entity.getActiveCostDetail();
        // Only one LoadCostDetail must be active
        activeCostDetail.setStatus(Status.INACTIVE);
        ltlShipmentDao.saveOrUpdate(entity);
    }

}
