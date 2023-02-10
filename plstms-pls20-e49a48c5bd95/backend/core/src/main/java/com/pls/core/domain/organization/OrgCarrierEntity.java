package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Entity for CARRIERS table.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "CARRIERS")
public class OrgCarrierEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 945281726357623L;

    @Id
    @Column(name = "CARRIER_ID")
    private Long id;

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "NETWORK_ID")
    private int networkId;

    @Column(name = "TERM_ID")
    private Long termId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }


}
