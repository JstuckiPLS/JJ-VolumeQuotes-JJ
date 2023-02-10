/**
 * 
 */
package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for FINAN_BUSN_UNIT.
 * 
 * @author Alexander Nalapko
 * 
 */
@Entity
@Table(name = "FINAN_BUSN_UNIT")
public class FinanBusnUnitEntity {
    @Id
    @Column(name = "UNIT_CODE", nullable = false)
    private String unitCode;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "STATUS", columnDefinition = "A")
    private String status;

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
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
