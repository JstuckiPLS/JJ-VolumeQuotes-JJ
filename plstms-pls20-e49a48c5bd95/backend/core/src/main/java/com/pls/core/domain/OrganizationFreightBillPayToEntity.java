package com.pls.core.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;

/**
 * Customer Freight Bill Pay To Entity.
 * 
 * @author Artem Arapov
 *
 */
@Entity
@Table(name = "ORG_FRT_BILL_PAY_TO")
public class OrganizationFreightBillPayToEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -7587574290162153926L;

    public static final String Q_GET_ACTIVE_BY_ORG_ID =
                "com.pls.core.domain.OrganizationFreightBillPayToEntity.Q_GET_ACTIVE_BY_ORG_ID";

    public static final String Q_INACTIVATE_EXISTING_BY_ORG_ID =
            "com.pls.core.domain.OrganizationFreightBillPayToEntity.Q_INACTIVATE_EXISTING_BY_ORG_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_FRT_BILL_PAY_TO_SEQ")
    @SequenceGenerator(name = "ORG_FRT_BILL_PAY_TO_SEQ", sequenceName = "ORG_FRT_BILL_PAY_TO_SEQ", allocationSize = 1)
    @Column(name = "ORG_FRT_BILL_PAY_TO_ID")
    private Long id;

    @Column(name = "ORG_ID", nullable = false)
    private Long orgId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private CustomerEntity organization;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FRT_BILL_PAY_TO_ID")
    private FreightBillPayToEntity freightBillPayTo;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public CustomerEntity getOrganization() {
        return organization;
    }

    public FreightBillPayToEntity getFreightBillPayTo() {
        return freightBillPayTo;
    }

    public Status getStatus() {
        return status;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setOrganization(CustomerEntity organization) {
        this.organization = organization;
    }

    public void setFreightBillPayTo(FreightBillPayToEntity freightBillPayTo) {
        this.freightBillPayTo = freightBillPayTo;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
