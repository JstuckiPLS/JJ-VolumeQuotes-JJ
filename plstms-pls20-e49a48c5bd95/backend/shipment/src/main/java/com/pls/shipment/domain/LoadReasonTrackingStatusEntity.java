package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Load Reason Tracking Status type entity.
 * 
 * @author Alexander Nalapko
 */
@Entity
@Table(name = "LOAD_TRK_STATUS_REASON_TYPES")
public class LoadReasonTrackingStatusEntity implements Identifiable<String> {

    public static final String GET_ALL = "com.pls.shipment.domain.LoadReasonTrackingStatusEntity.GET_ALL";
    public static final String FIND = "com.pls.shipment.domain.LoadReasonTrackingStatusEntity.FIND";

    private static final long serialVersionUID = 686482709228733960L;

    @Id
    @Column(name = "REASON_CODE")
    private String id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SOURCE")
    private Long source;

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
