package com.pls.extint.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.extint.domain.enums.DataType;
import com.pls.extint.domain.enums.PLSFieldType;

/**
 * Entity class for API_METADATA. This class keeps the information about each carrier configuration for creating the request and parsing the response.
 * 
 * @author PAVANI CHALLA
 * 
 */
@Entity
@Table(name = "API_METADATA")
public class ApiMetadataEntity implements Identifiable<Long>, HasModificationInfo, Comparable<ApiMetadataEntity> {

    private static final long serialVersionUID = 7387798734193868575L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "API_METADATA_SEQUENCE")
    @SequenceGenerator(name = "API_METADATA_SEQUENCE", sequenceName = "API_METADATA_SEQ", allocationSize = 1)
    @Column(name = "API_METADATA_ID")
    private Long id;

    @Column(name = "API_TYPE_ID", nullable = false, insertable = false, updatable = false)
    private Long apiTypeId;

    @Column(name = "PLS_FIELD_NAME")
    private String plsFieldName;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLS_FIELD_TYPE")
    private PLSFieldType plsFieldType;

    @Column(name = "PLS_FIELD_PARENT")
    private String plsFieldParent;

    @Column(name = "PLS_FIELD_DESCRIPTION")
    private String plsFieldDescription;

    @Column(name = "API_FIELD_FORMAT")
    private String apiFieldFormat;

    @Column(name = "API_FIELD_NAME")
    private String apiFieldName;

    @Column(name = "PARENT")
    private Long parent;

    @Column(name = "API_FIELD_DESCRIPTION")
    private String apiFieldDescription;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE")
    private DataType dataType;

    @Column(name = "MULTIPLE")
    private String multiple;

    @Column(name = "LOOKUP")
    private String lookup;

    @Column(name = "METADATA_TYPE")
    private String metadataType;

    @Column(name = "FIELD_TYPE")
    private String fieldType;

    @Column(name = "METADATA_ORDER")
    private Integer metadataOrder;

    @Column(name = "NAMESPACE")
    private String namespace;

    @Column(name = "NS_ELEMENT")
    private String nsElement;

    @Column(name = "START_INDEX")
    private Integer startIndex;

    @Column(name = "MAXLENGTH")
    private Integer maxLength;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    private Integer version;

    @Transient
    private List<ApiMetadataEntity> children;

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

    public String getPlsFieldName() {
        return plsFieldName;
    }

    public void setPlsFieldName(String plsFieldName) {
        this.plsFieldName = plsFieldName;
    }

    public PLSFieldType getPlsFieldType() {
        return plsFieldType;
    }

    public void setPlsFieldType(PLSFieldType plsFieldType) {
        this.plsFieldType = plsFieldType;
    }

    public String getPlsFieldParent() {
        return plsFieldParent;
    }

    public void setPlsFieldParent(String plsFieldParent) {
        this.plsFieldParent = plsFieldParent;
    }

    public String getPlsFieldDescription() {
        return plsFieldDescription;
    }

    public void setPlsFieldDescription(String plsFieldDescription) {
        this.plsFieldDescription = plsFieldDescription;
    }

    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Long getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Long apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    public String getApiFieldFormat() {
        return apiFieldFormat;
    }

    public void setApiFieldFormat(String apiFieldFormat) {
        this.apiFieldFormat = apiFieldFormat;
    }

    public String getApiFieldName() {
        return apiFieldName;
    }

    public void setApiFieldName(String apiFieldName) {
        this.apiFieldName = apiFieldName;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long apiFieldParent) {
        this.parent = apiFieldParent;
    }

    public String getApiFieldDescription() {
        return apiFieldDescription;
    }

    public void setApiFieldDescription(String apiFieldDescription) {
        this.apiFieldDescription = apiFieldDescription;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getMetadataOrder() {
        return metadataOrder;
    }

    public void setMetadataOrder(Integer metadataOrder) {
        this.metadataOrder = metadataOrder;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNsElement() {
        return nsElement;
    }

    public void setNsElement(String nsElement) {
        this.nsElement = nsElement;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sorts the child metadata before returning it. The metadata is ordered by the metadataOrder field.
     * 
     * @return the sorted child metadata
     */
    public List<ApiMetadataEntity> getChildren() {
        if (children != null) {
            Collections.sort(children);
        }

        return children;
    }

    /**
     * Sets the metadata as child to this entity.
     * 
     * @param metadata
     *            to be set as child
     */
    public void addChildren(ApiMetadataEntity metadata) {
        if (this.children == null) {
            this.children = new ArrayList<ApiMetadataEntity>();
        }

        this.children.add(metadata);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getApiTypeId()).append(getDefaultValue()).append(getApiFieldFormat()).append(getApiFieldName())
                .append(getParent()).append(getFieldType()).append(getLookup()).append(getMetadataOrder()).append(getMetadataType())
                .append(getDataType()).append(getMultiple()).append(getNamespace()).append(getPlsFieldDescription()).append(getPlsFieldName())
                .append(getPlsFieldParent()).append(getPlsFieldType()).append(getMaxLength()).append(getStartIndex()).append(getNsElement())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof ApiMetadataEntity) {
            if (obj == this) {
                result = true;
            } else {
                ApiMetadataEntity other = (ApiMetadataEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getApiTypeId(), other.getApiTypeId()).append(getDefaultValue(), other.getDefaultValue())
                        .append(getApiFieldFormat(), other.getApiFieldFormat()).append(getApiFieldName(), other.getApiFieldName())
                        .append(getApiFieldDescription(), other.getApiFieldDescription()).append(getParent(), other.getParent())
                        .append(getFieldType(), other.getFieldType()).append(getLookup(), other.getLookup())
                        .append(getMetadataOrder(), other.getMetadataOrder()).append(getMetadataType(), other.getMetadataType())
                        .append(getDataType(), other.getDataType()).append(getMultiple(), other.getMultiple())
                        .append(getNamespace(), other.getNamespace()).append(getPlsFieldDescription(), other.getPlsFieldDescription())
                        .append(getPlsFieldName(), other.getPlsFieldName()).append(getPlsFieldParent(), other.getPlsFieldParent())
                        .append(getPlsFieldType(), other.getPlsFieldType()).append(getModification(), other.getModification())
                        .append(getNsElement(), other.getNsElement()).append(getStartIndex(), other.getStartIndex())
                        .append(getMaxLength(), other.getMaxLength());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("apiTypeId", getApiTypeId()).append("defaultValue", getDefaultValue()).append("apiFieldFormat", getApiFieldFormat())
                .append("apiFieldName", getApiFieldName()).append("parent", getParent()).append("fieldType", getFieldType())
                .append("apiFieldDescription", getApiFieldDescription()).append("lookup", getLookup()).append("metadataOrder", getMetadataOrder())
                .append("metadataType", getMetadataType()).append("dataType", getDataType()).append("multiple", getMultiple())
                .append("namespace", getNamespace()).append("plsFieldDescription", getPlsFieldDescription())
                .append("plsFieldName", getPlsFieldName()).append("plsFieldParent", getPlsFieldParent()).append("plsFieldType", getPlsFieldType())
                .append("nsElement", getNsElement()).append("startIndex", getStartIndex()).append("maxLength", getMaxLength())
                .append("modification", getModification());

        return builder.toString();
    }

    @Override
    public int compareTo(ApiMetadataEntity other) {
        return this.metadataOrder - other.metadataOrder;
    }
}
