package com.pls.shipment.domain.bo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * BO to retrieve Accessorial Report Information from DB.
 * 
 * @author Sergey Vovchuk
 */
public class AccessorialReportBO {

    private Long loadId;
    private String accessorialTypeCode;
    private String description;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getAccessorialTypeCode() {
        return accessorialTypeCode;
    }

    public void setAccessorialTypeCode(String accessorialTypeCode) {
        this.accessorialTypeCode = accessorialTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(accessorialTypeCode).append(description).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccessorialReportBO) {
            final AccessorialReportBO other = (AccessorialReportBO) obj;
            return new EqualsBuilder().append(accessorialTypeCode, other.accessorialTypeCode)
                    .append(description, other.description).isEquals();
        } else {
            return false;
        }
    }
}
