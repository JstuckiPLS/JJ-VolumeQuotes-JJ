package com.pls.user.domain.bo;

import java.util.Date;

import com.pls.user.domain.enums.AnnouncementStatus;

/**
 * BO for announcements.
 * 
 * @author Nalapko Alexander
 * 
 */
public class AnnouncementBO {

    private Long id;

    private String theme;

    private String text;

    private String announcer;

    private Date startDate;

    private Date endDate;

    private String createdBy;

    private String modifiedBy;

    private AnnouncementStatus status;

    private Boolean isAnnouncementRead;

    private Date publishingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public AnnouncementStatus getStatus() {
        return status;
    }

    public void setStatus(AnnouncementStatus status) {
        this.status = status;
    }

    public Boolean getIsAnnouncementRead() {
        return isAnnouncementRead;
    }

    public void setIsAnnouncementRead(Boolean isAnnouncementRead) {
        this.isAnnouncementRead = isAnnouncementRead;
    }

    public Date getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(Date publishingDate) {
        this.publishingDate = publishingDate;
    }

}
