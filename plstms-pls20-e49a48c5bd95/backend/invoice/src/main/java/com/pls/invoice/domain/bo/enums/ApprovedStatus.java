package com.pls.invoice.domain.bo.enums;

/**
 * Consolidated data approved status.
 *
 * @author Sergey Kirichenko
 */
public enum ApprovedStatus {
    /**
     * When all loads are approved.
     */
    ALL,

    /**
     * When no loads are approved.
     */
    NONE,

    /**
     * When some part of loads are approved (at least one but not all).
     */
    SOME
}
