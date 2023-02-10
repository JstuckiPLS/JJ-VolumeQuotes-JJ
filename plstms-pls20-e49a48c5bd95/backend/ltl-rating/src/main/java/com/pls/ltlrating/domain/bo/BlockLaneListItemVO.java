package com.pls.ltlrating.domain.bo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import com.pls.core.shared.Status;

/**
 * Business object that is used to hold the list of the Blocked lanes for active/expired/archived tabs.
 *
 * @author Ashwini Neelgund
 *
 */
public class BlockLaneListItemVO implements Serializable {

    private static final long serialVersionUID = 7955510440268331586L;

    private Long id;
    private Long carrierId;
    private Long shipperId;
    private String name;
    private String origin;
    private String destination;
    private Status status;
    private Date effDate;
    private Date expDate;
    private String notes;
    private String lastModUser;
    private LocalDateTime lastModDate;
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getShipperId() {
        return shipperId;
    }

    public void setShipperId(Long shipperId) {
        this.shipperId = shipperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLastModUser() {
        return lastModUser;
    }

    public void setLastModUser(String lastModUser) {
        this.lastModUser = lastModUser;
    }

    public LocalDateTime getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Timestamp lastModDate) {
        this.lastModDate = lastModDate.toLocalDateTime();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
