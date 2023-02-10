package com.pls.core.domain.document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Document type for internal loads.
 * 
 * @author Artem Arapov
 *
 */
@Entity
@DiscriminatorValue("LOAD_INTERNAL")
public class LoadInternalDocumentTypeEntity extends DocumentTypeEntity {
    private static final long serialVersionUID = 3963055770662637581L;
}
