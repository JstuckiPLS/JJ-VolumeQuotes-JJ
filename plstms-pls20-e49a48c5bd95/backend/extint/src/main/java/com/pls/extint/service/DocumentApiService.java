package com.pls.extint.service;

import com.pls.extint.shared.DocumentRequestVO;

/**
 * Service class for processing of the available documents and to call carrier API for downloading the documents.
 * 
 * @author Pavani Challa
 * 
 */
public interface DocumentApiService {

    /**
     * Downloads the documents for the load from the carrier website and saves them to the file system.
     * 
     * @param requestVO
     *            load data for the request
     */
    void getDocuments(DocumentRequestVO requestVO);
}
