package com.pls.core.domain.enums;

/**
 * Enumeration for all possible organization statuses. Valid values are:
 * <ul style="list-style-type: circle;">
 *   <li><b>A</b> (Active)</li>
 *   <li><b>I</b> (Inactive)</li>
 *   <li><b>H</b> (Hold)</li>
 *   <li><b>R</b> (Rejected)</li>
 *   <li><b>P</b> (Pending)</li>
 *   <li><b>E</b> (Expired)</li>
 * </ul>
 *
 * @author Denis Zhupinsky, dnefedchenko, vdudinov
 */
public enum OrganizationStatus {

    ACTIVE('A'), INACTIVE('I'), HOLD('H'), PENDING('P'), REJECTED('R'), EXPIRED('E');

    private char status;

    OrganizationStatus(char status) {
        this.status = status;
    }

    /**
     * Get organizationStatus of current enum by char status.
     *
     * @param status organizationStatus to find
     * @return instance of current enum
     */
    public static OrganizationStatus getOrganizationStatusBy(char status) {
        for (OrganizationStatus organizationStatus : values()) {
            if (organizationStatus.status == status) {
                return organizationStatus;
            }
        }

        throw new IllegalArgumentException(String.format("Cannot get OrganizationStatus object by status:%s", status));
    }

    public char getOrganizationStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }
}
