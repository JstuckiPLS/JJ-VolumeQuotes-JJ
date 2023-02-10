/**
 * 
 */
package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for FINAN_CUST_GROUP.
 * 
 * @author Alexander Nalapko
 * 
 */
@Entity
@Table(name = "FINAN_CUST_GROUP")
public class FinanCustGroupEntity {
    @Id
    @Column(name = "GROUP_CODE", nullable = false)
    private String groupCode;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "STATUS", columnDefinition = "A")
    private String status;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
