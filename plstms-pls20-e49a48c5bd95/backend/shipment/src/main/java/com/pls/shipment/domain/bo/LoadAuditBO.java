package com.pls.shipment.domain.bo;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * BO to retrieve Load Audit Information from DB.
 * 
 * @author Sergey Vovchuk
 */
public class LoadAuditBO {

    private String group;
    private String fieldName;
    private String plsQuoted;
    private String plsCurrent;
    private String vendorBill;
    private ZonedDateTime lastModified;
    private String modifiedBy;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPlsQuoted() {
        return plsQuoted;
    }

    public void setPlsQuoted(String plsQuoted) {
        this.plsQuoted = plsQuoted;
    }

    public String getPlsCurrent() {
        return plsCurrent;
    }

    public void setPlsCurrent(String plsCurrent) {
        this.plsCurrent = plsCurrent;
    }

    public String getVendorBill() {
        return vendorBill;
    }

    public void setVendorBill(String vendorBill) {
        this.vendorBill = vendorBill;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified != null ? ZonedDateTime.ofInstant(lastModified.toInstant(), ZoneOffset.UTC) : null;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

}
