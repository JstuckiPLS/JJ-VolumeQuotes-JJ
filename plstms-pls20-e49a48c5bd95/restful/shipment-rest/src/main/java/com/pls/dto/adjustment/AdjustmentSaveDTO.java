package com.pls.dto.adjustment;

import java.util.ArrayList;
import java.util.List;

import com.pls.dto.shipment.ShipmentNoteDTO;
import com.pls.dto.shipment.UploadedDocumentDTO;
import com.pls.shipment.domain.bo.AdjustmentBO;

/**
 * DTO for saving adjustments.
 * 
 * @author Aleksandr Leshchenko
 */
public class AdjustmentSaveDTO {
    private AdjustmentDTO adjustmentsToSave;
    private List<AdjustmentBO> adjustmentsToRemove;

    private final List<Long> removedDocumentsIds = new ArrayList<Long>();
    private final List<UploadedDocumentDTO> uploadedDocuments = new ArrayList<UploadedDocumentDTO>();

    private List<ShipmentNoteDTO> notes;

    public AdjustmentDTO getAdjustmentsToSave() {
        return adjustmentsToSave;
    }

    public void setAdjustmentsToSave(AdjustmentDTO adjustmentsToSave) {
        this.adjustmentsToSave = adjustmentsToSave;
    }

    public List<AdjustmentBO> getAdjustmentsToRemove() {
        return adjustmentsToRemove;
    }

    public void setAdjustmentsToRemove(List<AdjustmentBO> adjustmentsToRemove) {
        this.adjustmentsToRemove = adjustmentsToRemove;
    }

    public List<Long> getRemovedDocumentsIds() {
        return removedDocumentsIds;
    }

    public List<UploadedDocumentDTO> getUploadedDocuments() {
        return uploadedDocuments;
    }

    public List<ShipmentNoteDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<ShipmentNoteDTO> notes) {
        this.notes = notes;
    }
}
