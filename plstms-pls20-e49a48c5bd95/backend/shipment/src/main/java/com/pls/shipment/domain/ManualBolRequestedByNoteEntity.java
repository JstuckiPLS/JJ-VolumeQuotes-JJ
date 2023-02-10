package com.pls.shipment.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.pls.core.domain.organization.NoteEntity;

/**
 * Implementation of {@link NoteEntity} for knowing who requested the shipment,
 * on Manual BOL.
 * 
 * @author Dmitriy Davydenko
 */
@Entity
@DiscriminatorValue("MBOL_REQ")
public class ManualBolRequestedByNoteEntity extends NoteEntity {

    private static final long serialVersionUID = -5429632898013353869L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", nullable = false)
    private ManualBolEntity manualBol;

    public ManualBolEntity getLoad() {
        return manualBol;
    }

    public void setLoad(ManualBolEntity manualBol) {
        this.manualBol = manualBol;
    }

}
