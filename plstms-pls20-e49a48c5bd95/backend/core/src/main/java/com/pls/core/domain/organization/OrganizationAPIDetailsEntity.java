package com.pls.core.domain.organization;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Carrier's API detail information.
 *
 * @author Sergey Kirichenko
 */
@Entity
@Table(name = "ORGANIZATION_API_DETAILS")
public class OrganizationAPIDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 3878941985847582972L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_api_sequence")
    @SequenceGenerator(name = "org_api_sequence", sequenceName = "ORGANIZATION_API_DETAILS_SEQ", allocationSize = 1)
    @Column(name = "ORGANIZATION_API_DETAIL_ID")
    private Long id;

    @Column(name = "API_NAME", nullable = false)
    private String apiName;

    @Column(name = "URL")
    private String url;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORG_ID", nullable = false)
    private SimpleOrganizationEntity organizationEntity;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SimpleOrganizationEntity getOrganizationEntity() {
        return organizationEntity;
    }

    public void setOrganizationEntity(SimpleOrganizationEntity organizationEntity) {
        this.organizationEntity = organizationEntity;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
