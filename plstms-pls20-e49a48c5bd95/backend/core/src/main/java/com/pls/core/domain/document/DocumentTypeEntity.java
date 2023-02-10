package com.pls.core.domain.document;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity that represents document types.
 * 
 * @author Viacheslav Vasianovych
 */
@Entity
@Table(name = "IMAGE_DOCUMENT_TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DOCUMENT_LEVEL", discriminatorType = DiscriminatorType.STRING)
public abstract class DocumentTypeEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -2344337989771618842L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_doctype_sequence")
    @SequenceGenerator(name = "image_doctype_sequence", sequenceName = "IMAGE_DOCUMENT_TYPE_SEQ", allocationSize = 1)
    @Column(name = "IMAGE_DOC_TYPE_ID")
    private Long id;

    @Column(name = "DOCUMENT_TYPE", unique = true, nullable = false)
    private String docTypeString;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SYSTEM_PROTECTED")
    @Type(type = "yes_no")
    private Boolean systemProtected;

    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    @Column(name = "DOCUMENT_ORG_TYPE")
    private String docOrgType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocTypeString() {
        return docTypeString;
    }

    public void setDocTypeString(String docTypeString) {
        this.docTypeString = docTypeString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSystemProtected() {
        return systemProtected;
    }

    public void setSystemProtected(Boolean systemProtected) {
        this.systemProtected = systemProtected;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDocOrgType() {
        return docOrgType;
    }

    public void setDocOrgType(String docOrgType) {
        this.docOrgType = docOrgType;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
