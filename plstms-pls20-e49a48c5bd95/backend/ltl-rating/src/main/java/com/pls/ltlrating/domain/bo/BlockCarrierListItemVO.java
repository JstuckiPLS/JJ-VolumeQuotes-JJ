package com.pls.ltlrating.domain.bo;

import java.io.Serializable;

import com.pls.core.shared.Status;

/**
 * Business object that is used to hold the list of the Block Carrier Zips for active/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class BlockCarrierListItemVO implements Serializable {

    private static final long serialVersionUID = -5094404750980834673L;

    private Long id;

    private Long profileId;

    private String origin;

    private String destination;

    private String notes;

    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.getStatusByValue(status);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
