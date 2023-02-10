package com.pls.shipment.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.pls.core.domain.organization.NoteEntity;

/**
 * Implementation of {@link NoteEntity} for knowing who requested the shipment,
 * when creating shipment anywhere, except Manual BOL.
 * 
 * @author Dmitriy Davydenko
 */
@Entity
@DiscriminatorValue("REQUESTOR")
public class ShipmentRequestedByNoteEntity extends NoteEntity {

    private static final long serialVersionUID = 5034541432498733582L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", nullable = false)
    private LoadEntity load;

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

}
