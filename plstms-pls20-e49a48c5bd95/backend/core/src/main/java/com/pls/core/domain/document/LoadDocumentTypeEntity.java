package com.pls.core.domain.document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Document type for documents related to load.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@DiscriminatorValue("LOAD")
public class LoadDocumentTypeEntity extends DocumentTypeEntity {
    private static final long serialVersionUID = -5585368446520448378L;
    public static final String Q_GET_DOCUMENT_TYPES = "com.pls.documentmanagement.domain.LoadDocumentTypeEntity.Q_GET_DOCUMENT_TYPES";
}
