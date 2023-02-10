package com.pls.core.service;

/**
 * Service to control db behaviour like flush mode.
 *
 * @author Sergey Kirichenko
 */
public interface DBUtilityService {

    /**
     * Sets flush mode of hibernate session into manual mode.
     */
    void startCommitMode();

    /**
     * Flush all changes in the hibernate session to the DB.
     */
    void flushSession();
}
