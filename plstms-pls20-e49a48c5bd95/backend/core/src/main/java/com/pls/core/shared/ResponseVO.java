package com.pls.core.shared;

import java.io.Serializable;

/**
 * This class is used for returning the response (like a number, boolean value).
 * 
 * @author Pavani Challa
 * 
 */
public class ResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Serializable data;

    /**
     * Default Constructor.
     * 
     * @param pData
     *            data passed in the response.
     */
    public ResponseVO(Serializable pData) {
        this.data = pData;
    }

    public Serializable getData() {
        return data;
    }

}
