package com.pls.core.domain.organization;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Organization voice phone.
 * 
 * @author Denis Zhupinsky
 */
@Entity
@DiscriminatorValue("VOICE")
public class OrganizationVoicePhoneEntity extends OrganizationPhoneEntity {
    private static final long serialVersionUID = 6922716505932327345L;
}
