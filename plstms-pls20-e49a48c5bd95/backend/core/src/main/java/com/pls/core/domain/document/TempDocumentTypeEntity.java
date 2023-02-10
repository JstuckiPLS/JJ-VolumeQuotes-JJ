package com.pls.core.domain.document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

/**
 * Document type for temporary documents.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Polymorphism(type = PolymorphismType.EXPLICIT)
@DiscriminatorValue("TEMP")
public class TempDocumentTypeEntity extends DocumentTypeEntity {
    private static final long serialVersionUID = -3468236748474199880L;
}
