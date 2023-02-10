package com.pls.documentmanagement.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.DocRequestType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.shared.Status;

/**
 * Document required to be filled before customer can be billed.
 * 
 * @author Alexander Nalapko
 * 
 */
@Entity
@Table(name = "BILL_TO_REQ_DOC")
public class RequiredDocumentEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -511396985175719237L;

    public static final String Q_COUNT_MISSING_REQUIRED_DOCUMENTS =
            "com.pls.documentmanagement.domain.RequiredDocumentEntity.Q_COUNT_MISSING_REQUIRED_DOCUMENTS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_to_req_doc_sequence")
    @SequenceGenerator(name = "bill_to_req_doc_sequence", sequenceName = "BILL_TO_REQ_DOC_SEQ", allocationSize = 1)
    @Column(name = "BILL_TO_REQ_DOC_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOCUMENT_TYPE", referencedColumnName = "DOCUMENT_TYPE")
    private DocumentTypeEntity documentType;

    @Version
    private Long version = 1L;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "SHIPPER_REQ_TYPE")
    @Enumerated(EnumType.STRING)
    private DocRequestType customerRequestType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BillToEntity getBillTo() {
        return billTo;
    }

    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    /**
     * Get document type.
     * 
     * @return the document.
     */
    public LoadDocumentTypeEntity getDocumentType() {
        return (LoadDocumentTypeEntity) documentType;
    }

    /**
     * Set document type.
     * 
     * @param documentType
     *            type.
     */
    public void setDocumentType(DocumentTypeEntity documentType) {
        this.documentType = documentType;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DocRequestType getCustomerRequestType() {
        return customerRequestType;
    }

    public void setCustomerRequestType(DocRequestType customerRequestType) {
        this.customerRequestType = customerRequestType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
