package com.pls.core.domain.bo.user;

/**
 * Data about parent organization for user.
 * 
 * @author Maxim Medvedev
 */
public class ParentOrganizationBO {
    private boolean customer;
    private Long organizationId;
    private String organizationName;

    public boolean isCustomer() {
        return customer;
    }

    public void setCustomer(boolean customer) {
        this.customer = customer;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
