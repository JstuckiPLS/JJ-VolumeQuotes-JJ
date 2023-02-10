package com.pls.extint.domain.enums;

/**
 * Enum for the different statuses for API call. When the call starts, API request is logged into API_LOG table with status IN_PROGRESS. Once the
 * response is received, it is updated with response and status OPEN. If the response is successfully processed, log is updated with status COMPLETED.
 * If the API call results in error, the log status is updated to ERRORED.
 * 
 * @author Pavani Challa
 * 
 */
public enum ApiRequestStatus {

    IN_PROGRESS, OPEN, COMPLETED, ERRORED;

}
