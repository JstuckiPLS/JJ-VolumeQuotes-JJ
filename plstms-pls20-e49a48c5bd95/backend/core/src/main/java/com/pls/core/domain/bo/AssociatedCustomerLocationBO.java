package com.pls.core.domain.bo;

import java.util.Date;

/**
 * BO for Associated Customer Location.
 * 
 * @author Brichak Aleksandr
 */
public class AssociatedCustomerLocationBO {

    private Long locationId;
    private String locationName;
    private String accountExecutive;
    private Date modifiedDate;
    private String modifiedBy;
    private Boolean defaultNode;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean isDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(Boolean defaultNode) {
        this.defaultNode = defaultNode;
    }
}


