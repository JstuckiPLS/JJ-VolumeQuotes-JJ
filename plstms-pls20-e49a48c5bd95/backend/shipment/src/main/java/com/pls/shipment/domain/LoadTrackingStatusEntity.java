package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Load Tracking Status type entity.
 * 
 * @author Mikhail Boldinov, 11/03/14
 */
@Entity
@Table(name = "LOAD_TRACKING_STATUS_TYPES")
public class LoadTrackingStatusEntity implements Identifiable<LoadTrackingStatusEntityId> {

    public static final String GET_ALL = "com.pls.shipment.domain.LoadTrackingStatusEntity.GET_ALL";
    public static final String FIND = "com.pls.shipment.domain.LoadTrackingStatusEntity.FIND";

    private static final long serialVersionUID = -5057434864635746602L;

    @EmbeddedId
    private LoadTrackingStatusEntityId loadTrackingStatusEntityId;

    @Column(name = "DESCRIPTION")
    private String description;

    public LoadTrackingStatusEntityId getId() {
        return loadTrackingStatusEntityId;
    }

    public void setId(LoadTrackingStatusEntityId id) {
        this.loadTrackingStatusEntityId = id;
    }

    public LoadTrackingStatusEntityId getLoadTrackingStatusEntityId() {
        return loadTrackingStatusEntityId;
    }

    public void setLoadTrackingStatusEntityId(LoadTrackingStatusEntityId id) {
        this.loadTrackingStatusEntityId = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
