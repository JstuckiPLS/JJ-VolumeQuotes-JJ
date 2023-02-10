package com.pls.invoice.domain.bo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pls.core.domain.bo.AuditRecordsBO;
/**
 * BO for billing audit reasons information, changing/editing one or multiple records at a time.
 * 
 * @author Sahil Thakkar
 */

public class MultipleAuditRecordsBO {

    @JsonProperty("auditRecords")
    private List<AuditRecordsBO> auditRecords;

    public List<AuditRecordsBO> getAuditRecords() {
        return auditRecords;
    }

    public void setAuditRecords(List<AuditRecordsBO> auditRecords) {
        this.auditRecords = auditRecords;
    }

}