package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Umbrella organization. We need this entity to load organization data by HQL/Hibernate.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@DiscriminatorValue("UMBRELLA")
public class UmbrellaOrganizationEntity extends OrganizationEntity {
    private static final long serialVersionUID = 6981161244239963605L;
}
