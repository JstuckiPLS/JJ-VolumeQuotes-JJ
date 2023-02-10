package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.shared.ReasonType;

/**
 * Billing Audit Reason Code Entity.
 * 
 * @author Brichak Aleksandr
 */
@Entity
@Table(name = "LD_BILL_AUDIT_REASON_CODES")
public class LdBillAuditReasonCodeEntity implements Identifiable<String> {

    public static final String GET_REASON_CODE_FOR_REASON_TYPE =
            "com.pls.shipment.domain.LdBillAuditReasonCodeEntity.GET_REASON_CODE_FOR_REASON_TYPE";

    public static final String GET_REASON_FOR_REASON_CODE =
            "com.pls.shipment.domain.LdBillAuditReasonCodeEntity.GET_REASON_FOR_REASON_CODE";

    private static final long serialVersionUID = 57345342966543147L;

    @Id
    @Column(name = "REASON_CD", nullable = false)
    private String id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_COMMENT_REQ", nullable = false)
    private String isCommentReq;

    @Column(name = "REASON_TYPE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.shared.ReasonType"),
            @Parameter(name = "identifierMethod", value = "geReasonTypeCode"),
            @Parameter(name = "valueOfMethod", value = "getTypeByValue") })
    private ReasonType reasonType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsCommentReq() {
        return isCommentReq;
    }

    public void setIsCommentReq(String isCommentReq) {
        this.isCommentReq = isCommentReq;
    }

    public ReasonType getReasonType() {
        return reasonType;
    }

    public void setReasonType(ReasonType reasonType) {
        this.reasonType = reasonType;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
