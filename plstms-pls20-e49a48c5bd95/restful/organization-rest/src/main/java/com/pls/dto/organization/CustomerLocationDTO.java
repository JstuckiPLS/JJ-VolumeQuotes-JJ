package com.pls.dto.organization;

import java.util.Date;

/**
 * Customer Location DTO.
 * 
 * @author Artem Arapov
 *
 */
public class CustomerLocationDTO {

    private Long id;

    private String name;

    private Long orgId;

    private Long accExecPersonId;

    private Date accExecStartDate;

    private Date accExecEndDate;

    private Long billToId;

    private Boolean defaultNode;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getAccExecPersonId() {
        return accExecPersonId;
    }

    public Date getAccExecStartDate() {
        return accExecStartDate;
    }

    public Date getAccExecEndDate() {
        return accExecEndDate;
    }

    public Long getBillToId() {
        return billToId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setAccExecPersonId(Long accExecId) {
        this.accExecPersonId = accExecId;
    }

    public void setAccExecStartDate(Date accExecStartDate) {
        this.accExecStartDate = accExecStartDate;
    }

    public void setAccExecEndDate(Date accExecEndDate) {
        this.accExecEndDate = accExecEndDate;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public Boolean isDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(Boolean defaultNode) {
        this.defaultNode = defaultNode;
    }
}
