package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.OrganizationStatus;

/**
 * This class contains simple Organizations table fields without joining to any other tables
 * so that this table can be joined to other Entities.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "ORGANIZATIONS")
public class SimpleOrganizationEntity implements Identifiable<Long> {
//TODO mark this entity as @Immutable and with non-updatable/insertable fields
    private static final long serialVersionUID = 2718077523206419893L;

    @Id
    @Column(name = "ORG_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.OrganizationStatus"),
            @Parameter(name = "identifierMethod", value = "getOrganizationStatus"),
            @Parameter(name = "valueOfMethod", value = "getOrganizationStatusBy")})
    private OrganizationStatus status;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "ORG_TYPE")
    private String orgType;

    @Column(name = "SCAC")
    private String scac;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(name).
                append(status).
                append(networkId).
                append(companyCode).
                append(scac).
                append(orgType).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleOrganizationEntity) {
            final SimpleOrganizationEntity other = (SimpleOrganizationEntity) obj;
            return new EqualsBuilder().
                    append(name, other.getName()).
                    append(status, other.getStatus()).
                    append(networkId, other.getNetworkId()).
                    append(companyCode, other.getCompanyCode()).
                    append(scac, other.getScac()).
                    append(orgType, other.getOrgType()).isEquals();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("name", getName())
                .append("status", getStatus())
                .append("networkId", getNetworkId())
                .append("companyCode", getCompanyCode())
                .append("scac", getScac())
                .append("orgType", getOrgType());

        return builder.toString();
    }
}
