package com.pls.core.domain.enums;

/**
 * Enum that stands for status of users.
 * Status can be active, inactive, pending.
 *
 * @author Denis Zhupinsky
 */
public enum UserStatus {
    ACTIVE("A"), INACTIVE("I"), PENDING("P");

    private String status;

    UserStatus(String status) {
        this.status = status;
    }

    /**
     * Get userStatus of current enum by String userStatus.
     *
     * @param status userStatus to find
     * @return instance of current enum
     */
    public static UserStatus getUserStatusBy(String status) {
        for (UserStatus userStatus : values()) {
            if (userStatus.status.equals(status)) {
                return userStatus;
            }
        }

        throw new IllegalArgumentException("Cannot get UserStatus object by status: '" + status + "'");
    }

    public String getUserStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
