/**
 * 
 */
package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for FINAN_COST_CENTER.
 * 
 * @author Alexander Nalapko
 * 
 */
@Entity
@Table(name = "FINAN_COST_CENTER")
public class FinanCostCenterEntity {
    @Id
    @Column(name = "COST_CENTER_CODE", nullable = false)
    private String costCenterCode;

    @Column(name = "COST_CENTER_NAME")
    private String costCenterName;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "COMPANY_CODE")
    private Long companyCode;

    @Column(name = "STATUS", columnDefinition = "A")
    private String status;

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(Long companyCode) {
        this.companyCode = companyCode;
    }

}
