package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Entity for COMPANY_CODES.
 * 
 * @author Sergey Vovchuk
 * 
 */
@Entity
@Table(name = "COMPANY_CODES")
public class CompanyCodeEntity implements Identifiable<String> {

    private static final long serialVersionUID = -34334693330135009L;

    @Id
    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "NETWORK_ID", nullable = false)
    private Long networkId;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Override
    public String getId() {
        return companyCode;
    }

    @Override
    public void setId(String companyCode) {
        this.companyCode = companyCode;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
