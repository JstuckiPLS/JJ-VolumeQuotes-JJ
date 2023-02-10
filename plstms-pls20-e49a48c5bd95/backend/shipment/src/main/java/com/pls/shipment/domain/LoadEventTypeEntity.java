package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Load event type.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LOAD_GENERIC_EVENT_TYPES")
public class LoadEventTypeEntity implements Identifiable<String> {

    private static final long serialVersionUID = -981089447342923624L;

    @Id
    @Column(name = "EVENT_TYPE")
    private String id;

    @Column(name = "DESCRIPTION")
    private String description;

    public String getId() {
        return id;
    }

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
