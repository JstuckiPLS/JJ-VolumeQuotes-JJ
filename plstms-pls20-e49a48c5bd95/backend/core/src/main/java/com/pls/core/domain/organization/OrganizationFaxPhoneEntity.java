package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * {@link OrganizationPhoneEntity} implementation for fax phones.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("FAX")
public class OrganizationFaxPhoneEntity extends OrganizationPhoneEntity {
    private static final long serialVersionUID = -1920368139107074646L;
}
