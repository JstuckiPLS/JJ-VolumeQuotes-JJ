package com.pls.emailhistory.service;

import java.util.List;

import com.pls.emailhistory.domain.bo.EmailHistoryBO;

/**
 * Service for email history.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public interface EmailHistoryService {

    /**
     * Find all email history for the load. Data depend of user permissions.
     * 
     * @param loadId {@link LoadEntity#getId()}.
     * 
     * @return list of {@link EmailHistoryBO}
     */
    List<EmailHistoryBO> getEmailHistory(long loadId);
}
