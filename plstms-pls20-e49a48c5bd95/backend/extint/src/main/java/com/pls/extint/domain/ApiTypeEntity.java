package com.pls.extint.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpMethod;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import com.pls.extint.domain.enums.AuthPolicy;
import com.pls.extint.domain.enums.ResponseType;
import com.pls.extint.domain.enums.WebServiceType;

/**
 * Entity class for API_TYPES. API_ORG_TYPE is currently set to "CARRIER". The APIs implemented so far are the Carrier APIs only. Logic should be
 * added when we implement Shipper APIs.
 * 
 * @author Pavani Challa
 * 
 */
@Entity
@Table(name = "API_TYPES")
public class ApiTypeEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -6769904328831605744L;

    private static final int NUMBER_115 = 115;

    private static final int NUMBER_103 = 103;

    public static final String FIND_BY_CATEGORY = "com.pls.extint.domain.ApiTypeEntity.FIND_BY_CATEGORY";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "API_TYPES_SEQUENCE")
    @SequenceGenerator(name = "API_TYPES_SEQUENCE", sequenceName = "API_TYPES_SEQ", allocationSize = 1)
    @Column(name = "API_TYPE_ID")
    private Long id;

    @Column(name = "API_TYPE")
    private String apiType;

    @Column(name = "API_DESCRIPTION")
    private String apiDescription;

    @Column(name = "API_CATEGORY")
    private String apiCategory;

    @Column(name = "CARRIER_ORG_ID")
    private Long carrierOrgId;

    @Column(name = "SHIPPER_ORG_ID")
    private Long shipperOrgId;

    @Column(name = "API_ORG_TYPE")
    private String apiOrgType;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "URL")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "WS_TYPE")
    private WebServiceType wsType;

    @Column(name = "HTTP_METHOD")
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTH_POLICY")
    private AuthPolicy authPolicy = AuthPolicy.BASIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESPONSE_TYPE")
    private ResponseType responseType = ResponseType.XML;

    @Column(name = "SOAP_ACTION")
    private String soapAction;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "API_TYPE_ID", nullable = false)
    @OrderBy("metadataOrder ASC")
    private List<ApiMetadataEntity> metadata;

    @Transient
    private List<ApiMetadataEntity> reqMetadata;

    @Transient
    private List<ApiMetadataEntity> respMetadata;

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getApiCategory() {
        return apiCategory;
    }

    public void setApiCategory(String apiCategory) {
        this.apiCategory = apiCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebServiceType getWsType() {
        return wsType;
    }

    public void setWsType(WebServiceType wsType) {
        this.wsType = wsType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public AuthPolicy getAuthPolicy() {
        return authPolicy;
    }

    public void setAuthPolicy(AuthPolicy authPolicy) {
        this.authPolicy = authPolicy;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public String getApiOrgType() {
        return apiOrgType;
    }

    public void setApiOrgType(String apiOrgType) {
        this.apiOrgType = apiOrgType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ApiMetadataEntity> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<ApiMetadataEntity> metadata) {
        this.metadata = metadata;
    }

    /**
     * Loads the metadata for creating the request object.
     * 
     * @return metadata for the request
     */
    public List<ApiMetadataEntity> getReqMetadata() {
        if (reqMetadata == null && (metadata != null && !metadata.isEmpty())) {
            reqMetadata = new ArrayList<ApiMetadataEntity>();
            for (ApiMetadataEntity entity : metadata) {
                if ("REQUEST".equalsIgnoreCase(entity.getMetadataType())) {
                    reqMetadata.add(entity);
                }
            }
        } else if (reqMetadata == null) {
            reqMetadata = new ArrayList<ApiMetadataEntity>();
        }

        return reqMetadata;
    }

    /**
     * Sets the metadata for creating the request object.
     * 
     * @param reqMetadata
     *            metadata for the request
     */
    public void setReqMetadata(List<ApiMetadataEntity> reqMetadata) {
        this.reqMetadata = reqMetadata;
        Collections.sort(this.reqMetadata);
    }

    /**
     * Sets the metadata for parsing the response object.
     * 
     * @param respMetadata
     *            metadata for the response
     */
    public void setRespMetadata(List<ApiMetadataEntity> respMetadata) {
        this.respMetadata = respMetadata;
        Collections.sort(this.respMetadata);
    }

    /**
     * Loads the metadata for creating the response object.
     * 
     * @return metadata for the response
     */
    public List<ApiMetadataEntity> getRespMetadata() {
        if (respMetadata == null && (metadata != null && !metadata.isEmpty())) {
            respMetadata = new ArrayList<ApiMetadataEntity>();
            for (ApiMetadataEntity entity : metadata) {
                if ("RESPONSE".equalsIgnoreCase(entity.getMetadataType())) {
                    respMetadata.add(entity);
                }
            }
        } else if (respMetadata == null) {
            respMetadata = new ArrayList<ApiMetadataEntity>();
        }

        return respMetadata;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(NUMBER_103, NUMBER_115).append(getApiCategory()).append(getApiDescription()).append(getApiType())
                .append(getAuthPolicy()).append(getHttpMethod()).append(getCarrierOrgId()).append(getResponseType()).append(getSoapAction())
                .append(getStatus()).append(getUrl()).append(getWsType()).append(getShipperOrgId()).append(getApiOrgType()).append(getUsername())
                .append(getPassword()).toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("apiCategory", getApiCategory()).append("apiType", getApiType()).append("apiDescription", getApiDescription())
                .append("authPolicy", getAuthPolicy()).append("httpMethod", getHttpMethod()).append("url", getUrl())
                .append("soapAction", getSoapAction()).append("responseType", getResponseType()).append("wsType", getWsType())
                .append("carrierOrgid", getCarrierOrgId()).append("status", getStatus()).append("modification", getModification())
                .append("shipperOrgid", getShipperOrgId()).append("apiOrgType", getApiOrgType()).append("username", getUsername())
                .append("password", getPassword());

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ApiTypeEntity) {
            if (obj == this) {
                result = true;
            } else {
                ApiTypeEntity other = (ApiTypeEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getApiCategory(), other.getApiCategory()).append(getApiDescription(), other.getApiDescription())
                        .append(getApiType(), other.getApiType()).append(getAuthPolicy(), other.getAuthPolicy())
                        .append(getHttpMethod(), other.getHttpMethod()).append(getModification(), other.getModification())
                        .append(getCarrierOrgId(), other.getCarrierOrgId()).append(getResponseType(), other.getResponseType())
                        .append(getSoapAction(), other.getSoapAction()).append(getStatus(), other.getStatus()).append(getUrl(), other.getUrl())
                        .append(getWsType(), other.getWsType()).append(getShipperOrgId(), other.getShipperOrgId())
                        .append(getUsername(), other.getUsername()).append(getPassword(), other.getPassword())
                        .append(getApiOrgType(), other.getApiOrgType());

                result = builder.isEquals();
            }
        }
        return result;
    }
}
