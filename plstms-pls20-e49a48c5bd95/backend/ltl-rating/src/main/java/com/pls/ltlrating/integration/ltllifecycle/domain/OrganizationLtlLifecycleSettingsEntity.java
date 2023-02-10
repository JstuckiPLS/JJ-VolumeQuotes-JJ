package com.pls.ltlrating.integration.ltllifecycle.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

@Entity
@Table(name = "org_ltllc_settings")
public class OrganizationLtlLifecycleSettingsEntity implements Identifiable<Long> {
    
    private static final long serialVersionUID = 7970665563503377649L;

    @Id
    @Column(name = "org_id")
    private Long organizationId;
    
    @Column(name = "api_timeout")
    private Integer apiTimeout;

    public Integer getApiTimeout() {
        return apiTimeout;
    }

    public void setApiTimeout(Integer apiTimeout) {
        this.apiTimeout = apiTimeout;
    }

    @Override
    public Long getId() {
        return organizationId;
    }

    @Override
    public void setId(Long id) {
        this.organizationId = id;
    }
    
    
}
