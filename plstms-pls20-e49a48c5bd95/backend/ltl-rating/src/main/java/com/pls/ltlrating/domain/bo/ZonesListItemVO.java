package com.pls.ltlrating.domain.bo;

import java.io.Serializable;

import com.pls.core.shared.Status;

/**
 * Business object that is used to hold the list of the Zones for active/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class ZonesListItemVO implements Serializable {

    private static final long serialVersionUID = 977236547636787777L;

    private Long id;

    private Long profileId;

    private String name;

    private String geography;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.getStatusByValue(status);
    }


}
