package com.pls.core.domain.bo;

/**
 * BO for billing audit reasons information.
 * 
 * @author Brichak Aleksandr
 */

public class AuditReasonBO {

    private String code;

    private String note;

    private Long adjustmentId;

    private Long loadId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    /**
     * Instantiates a new audit reason bo.
     *
     * @param loadId the load id
     * @param adjustmentId the adjustment id
     */
    public AuditReasonBO(Long loadId, Long adjustmentId) {
        this.loadId = loadId;
        this.adjustmentId = adjustmentId;
    }

    /**
     * Instantiates a new audit reason bo.
     *
     * @param loadId the load id
     */
    public AuditReasonBO(Long loadId) {
        this.loadId = loadId;
    }

    /**
     * Instantiates a new audit reason bo.
     *
     */
    public AuditReasonBO() {
    }

    /**
     * Instantiates a new audit reason bo.
     *
     * @param auditRecordsBO the audit records bo
     */
    public AuditReasonBO(AuditRecordsBO auditRecordsBO) {
        this.loadId = auditRecordsBO.getLoadId();
        this.adjustmentId = auditRecordsBO.getAdjustmentId();
    }

}
