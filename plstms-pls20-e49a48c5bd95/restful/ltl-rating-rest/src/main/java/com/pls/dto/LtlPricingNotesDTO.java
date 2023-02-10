package com.pls.dto;


import java.time.ZonedDateTime;

import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * DTO object for {@link LtlPricingNotesEntity}.
 * 
 * @author Artem Arapov
 *
 */
public class LtlPricingNotesDTO {

    private Long profileId;

    private String note;

    private String createdBy;

    private ZonedDateTime createdDate;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
