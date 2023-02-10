package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Proxy organization. We need this entity to load organization data by HQL/Hibernate.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("PROXY")
public class ProxyOrganizationEntity extends OrganizationEntity {
    private static final long serialVersionUID = -4044667859647460284L;
}
