package com.pls.restful.shipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.dto.shipment.AdditionalCostDetailsItemsDTO;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AuditShipmentCostsBO;
import com.pls.shipment.domain.bo.AuditShipmentCostsOptionsBO;
import com.pls.shipment.domain.bo.CostDetailTransfeBO;
import com.pls.shipment.service.AuditShipmentCostsService;

/**
 * REST for sending audit shipment costs.
 * 
 * @author Brichak Aleksandr
 */

@Controller
@Transactional(readOnly = true)
@RequestMapping("/shipment/{shipmentId}/audit")
public class AuditShipmentCostsResource {

    @Autowired
    private AuditShipmentCostsService auditShipmentCostsService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilderUtils;

    /**
     * Method saves Invoice Additional Details.
     * 
     * @param shipmentId
     *            shipment identifier
     * @param auditCostBO
     *            {@link AuditShipmentCostsOptionsBO}
     * @return {@link CostDetailTransfeBO}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AdditionalCostDetailsItemsDTO saveInvoiceAdditionalDetails(@PathVariable("shipmentId") Long shipmentId,
            @RequestBody AuditShipmentCostsOptionsBO auditCostBO) {
        userPermissionsService.checkCapability(Capabilities.CAN_EDIT_AUDIT_TAB_FOR_DISPUTE_AND_REVENUE_UPDATE.name());
        LoadEntity updatedLoad = auditShipmentCostsService.saveAuditShipmentCostsOptions(shipmentId, auditCostBO,
                shipmentBuilderUtils.getAuditShipmentCostDTOBuilder().buildEntity(auditCostBO.getAuditShipmentCosts()));
        return new AdditionalCostDetailsItemsDTO(updatedLoad.getVersion(), updatedLoad.getActiveCostDetail().getId());
    }


    /**
     * Method get Invoice Additional Details.
     * 
     * @param shipmentId
     *            shipment identifier
     * @return {@link AuditShipmentCostsBO}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public AuditShipmentCostsBO getInvoiceAdditionalDetails(@PathVariable("shipmentId") Long shipmentId) {
        return shipmentBuilderUtils.getAuditShipmentCostDTOBuilder()
                .buildDTO(auditShipmentCostsService.getInvoiceAdditionalDetails(shipmentId));
    }
}
