package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Paperwork email Entity.
 * 
 * Carrier may have paperwork email which can be used for automating email requests.
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Entity
@Table(name = "PAPERWORK_EMAIL")
public class PaperworkEmailEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -6531603819232025439L;

    public static final String Q_UNSUBSCRIBE = "com.pls.core.domain.organization.PaperworkEmailEntity.Q_UNSUBSCRIBE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paperwork_email_sequence")
    @SequenceGenerator(name = "paperwork_email_sequence", sequenceName = "paperwork_email_seq", allocationSize = 1)
    @Column(name = "PAPERWORK_EMAIL_ID")
    private Long id;

    @Column(name = "ORG_ID")
    private Long orgId;

    @OneToOne
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private CarrierEntity carrier;

    @Column(name = "DONT_REQ_PPW", nullable = false)
    @Type(type = "yes_no")
    private boolean dontRequestPaperwork;

    @Column(name = "EMAIL")
    private String email;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public CarrierEntity getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierEntity carrier) {
        this.carrier = carrier;
    }

    public boolean isDontRequestPaperwork() {
        return dontRequestPaperwork;
    }

    public void setDontRequestPaperwork(boolean dontRequestPaperwork) {
        this.dontRequestPaperwork = dontRequestPaperwork;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
