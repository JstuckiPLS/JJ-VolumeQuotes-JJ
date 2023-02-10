package com.pls.shipment.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.pls.core.domain.organization.NoteEntity;

/**
 * Implementation of {@link NoteEntity} for load special message.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("SPRINSTMAN")
public class LoadNoteEntity extends NoteEntity {

    private static final long serialVersionUID = -8480470763369396395L;

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
