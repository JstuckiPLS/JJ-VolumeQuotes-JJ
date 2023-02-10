package com.pls.shipment.domain.bo;

import java.util.Date;

import com.pls.shipment.domain.enums.LoadEventType;

/**
 * BO for shipment events.
 * 
 * @author Aleksandr Leshchenko
 */
public class ShipmentEventBO {
    private Long eventId;
    private Long loadId;
    private String description;
    private String firstName;
    private String lastName;
    private Date createdDate;
    private Byte ordinal;
    private String data;
    private LoadEventType eventType;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Byte getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Byte ordinal) {
        this.ordinal = ordinal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LoadEventType getEventType() {
        return eventType;
    }

    public void setEventTypeEnum(LoadEventType eventType) {
        this.eventType = eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = LoadEventType.ofDBCode(eventType);
    }
}
