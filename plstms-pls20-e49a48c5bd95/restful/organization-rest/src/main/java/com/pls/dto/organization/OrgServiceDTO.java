package com.pls.dto.organization;

import com.pls.core.domain.organization.OrgServiceEntity;


/**
 * DTO for {@link OrgServiceEntity}.
 * 
 * @author Artem Arapov
 *
 */
public class OrgServiceDTO {

    private Long id;

    private Long orgId;

    private String tracking;

    private String pickup;

    private String rating;

    private String invoice;

    private String imaging;

    private String manualTypeEmail;

    private Integer version;

    public Long getId() {
        return id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getTracking() {
        return tracking;
    }

    public String getPickup() {
        return pickup;
    }

    public String getRating() {
        return rating;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getImaging() {
        return imaging;
    }

    public String getManualTypeEmail() {
        return manualTypeEmail;
    }

    public Integer getVersion() {
        return version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void setImaging(String imaging) {
        this.imaging = imaging;
    }

    public void setManualTypeEmail(String manualTypeEmail) {
        this.manualTypeEmail = manualTypeEmail;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
