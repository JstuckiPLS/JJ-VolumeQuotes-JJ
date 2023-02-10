package com.pls.restful.shipment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.adjustment.AdjustmentSaveDTO;
import com.pls.dto.shipment.UploadedDocumentDTO;
import com.pls.dtobuilder.product.ShipmentMaterialDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentNoteDTOBuilder;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.service.AdjustmentService;
import com.pls.shipment.service.ShipmentNoteService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;

/**
 * Shipment Adjustment REST service.
 * 
 * @author Brichak Aleksandr
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/shipment/{shipmentId}/adjustments")
public class AdjustmentResource {


    @Autowired
    private DBUtilityService dbUtilityService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentNoteService shipmentNoteService;

    @Autowired
    private AdjustmentService adjustmentService;

    private static final ShipmentNoteDTOBuilder SHIPMENT_NOTE_BUILDER = new ShipmentNoteDTOBuilder();

    private static final ShipmentMaterialDTOBuilder MATERIAL_BUILDER = new ShipmentMaterialDTOBuilder();

    /**
     * Method saves adjustments for invoiced sales order.
     * 
     * @param customerId
     *            id of customer
     * @param shipmentId
     *            shipment identifier
     * @param adjustments
     *            object holding list of adjustments to save and to remove. Both can be empty!
     * @param freightBillDate
     *            - freight bill date for update
     * @throws ApplicationException
     *             see {@link ShipmentSavingService#saveAdjustments(Long, List, List)}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void saveAdjustments(@PathVariable("customerId") Long customerId,
            @PathVariable("shipmentId") Long shipmentId,
            @RequestParam(value = "freightBillDate", required = false) String freightBillDate,
            @RequestBody AdjustmentSaveDTO adjustments) throws ApplicationException {
        if (!SecurityUtils.isPlsUser()) {
            throw new AccessDeniedException("You aren't PLS user and don't have permissions to fulfill this request.");
        }

        userPermissionsService.checkOrganization(customerId);
        // dbUtilityService.startCommitMode();
        List<LoadMaterialEntity> materials = adjustments.getAdjustmentsToSave().getProducts() == null ? null
                : MATERIAL_BUILDER.buildEntityList(adjustments.getAdjustmentsToSave().getProducts());
        adjustmentService.saveAdjustments(shipmentId, adjustments.getAdjustmentsToSave().getCostItems(), adjustments.getAdjustmentsToRemove(),
                adjustments.getAdjustmentsToSave().getLoadInfo(), materials);

        if (userPermissionsService.hasCapability(Capabilities.REMOVE_DOCUMENTS_AFTER_INVOICING.name())) {
            documentService.deleteDocuments(adjustments.getRemovedDocumentsIds());
        }

        if (userPermissionsService.hasCapability(Capabilities.ADD_DOCUMENTS_AFTER_INVOICING.name())) {
            for (UploadedDocumentDTO uploadedDocument : adjustments.getUploadedDocuments()) {
                documentService.moveAndSaveTempDocPermanently(uploadedDocument.getId(),  shipmentId, uploadedDocument.getDocType());
            }
        }

        if (adjustments.getNotes() != null) {
            List<ShipmentNoteEntity> shipmentNotes = SHIPMENT_NOTE_BUILDER.buildEntityList(adjustments.getNotes());
            shipmentNoteService.updateNotes(shipmentId, shipmentNotes);
        }
        dbUtilityService.flushSession();
        shipmentService.updateFrtBillDate(DateUtils.getDateFrom(freightBillDate), shipmentId);

    }



}
