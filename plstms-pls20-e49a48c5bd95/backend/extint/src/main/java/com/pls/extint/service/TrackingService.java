package com.pls.extint.service;

import com.pls.extint.shared.TrackingRequestVO;
import com.pls.extint.shared.TrackingResponseVO;

/**
 * 
 * Service class to get the tracking information from the carrier API.
 * 
 * @author Pavani Challa
 * 
 */
public interface TrackingService {

    /**
     * Get the tracking information from the carrier API and saves the current status to database.
     * 
     * @param requestVO
     *            Holds the data for calling the Tracking API.
     * @return the response from API call
     */
    TrackingResponseVO getTrackingInformation(TrackingRequestVO requestVO);

}
