package com.pls.security.restful.dto;

import com.pls.dto.enums.CustomerStatusReason;

/**
 * Information about organization that current user belong.
 * 
 * @author Maxim Medvedev
 */
public class CurrentOrganizationInfoDTO {

    private final String name;
    private final Long orgId;
    private final CustomerStatusReason statusReason;

    /**
     * Constructor.
     *
     * @param orgId
     *            organization Id
     * @param name
     *            organization name
     * @param statusReason
     *            status reason code
     */
    public CurrentOrganizationInfoDTO(Long orgId, String name, String statusReason) {
        this.name = name;
        this.orgId = orgId;
        this.statusReason = CustomerStatusReason.getByValue(statusReason);
    }

    public String getName() {
        return name;
    }

    public Long getOrgId() {
        return orgId;
    }

    public CustomerStatusReason getStatusReason() {
        return statusReason;
    }
}