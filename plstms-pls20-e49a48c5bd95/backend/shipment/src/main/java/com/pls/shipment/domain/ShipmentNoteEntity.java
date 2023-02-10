package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pls.core.domain.organization.NoteEntity;

/**
 * Implementation of {@link com.pls.core.domain.organization.NoteEntity} for order notes.
 *
 * @author Sergey Kirichenko
 */
@Entity
@DiscriminatorValue("LOAD")
public class ShipmentNoteEntity extends NoteEntity {

    public static final String GET_SHIPMENT_NOTES_BY_LOAD_ID = "com.pls.shipment.domain.ShipmentNoteEntity.GET_SHIPMENT_NOTES_BY_LOAD_ID";
    private static final long serialVersionUID = 8276349197236795365L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", nullable = false, insertable = false, updatable = false)
    private LoadEntity load;

    @Column(name = "REF_ID", nullable = false, insertable = true, updatable = true)
    private Long loadId;

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Long getLoadId() {
        return loadId;
        }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }
}
