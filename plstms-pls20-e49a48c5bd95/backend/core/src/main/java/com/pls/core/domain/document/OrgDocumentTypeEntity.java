package com.pls.core.domain.document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Document type for documents related to organization.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@DiscriminatorValue("ORG")
public class OrgDocumentTypeEntity extends DocumentTypeEntity {
    private static final long serialVersionUID = 2591388241319749434L;
}
