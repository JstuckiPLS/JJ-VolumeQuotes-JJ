package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Master/root organization. We need this entity to load organization data by HQL/Hibernate.
 * 
 * @author Maxim Medvedev
 */
@Entity
@DiscriminatorValue("EFLATBED")
public class EflatbedOrganizationEntity extends OrganizationEntity {

    private static final long serialVersionUID = 836652358269037425L;

    public static final String Q_GET_ACTIVE_BY_NAME = "com.pls.core.domain.organization.EflatbedOrganizationType.Q_GET_ACTIVE_BY_NAME";
}
