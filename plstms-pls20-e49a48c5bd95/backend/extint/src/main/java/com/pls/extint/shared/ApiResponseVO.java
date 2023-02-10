package com.pls.extint.shared;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Base VO for all the response VO for holding the data after parsing the API response.
 * 
 * @author Pavani Challa
 * 
 */
public class ApiResponseVO implements Serializable {

    private static final long serialVersionUID = 4514285414115246523L;

    private static final String EMPTY_STRING = "";

    private Long loadId;

    private Long orgId;

    private Long apiTypeId;

    private String error;

    /**
     * Constructor to set the default values for all responses from API.
     * 
     * @param requestVO
     *            request object containing the default values.
     */
    public ApiResponseVO(ApiRequestVO requestVO) {
        this.loadId = requestVO.getLoadId();
        this.apiTypeId = requestVO.getApiType().getId();
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Long apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    /**
     * Return empty string if there is no value set for error field. Else returns the field after trimming the white spaces.
     * 
     * @return the error field after trimming the white spaces
     */
    public String getError() {
        if (StringUtils.isEmpty(error)) {
            return EMPTY_STRING;
        }

        return error.trim();
    }

    public void setError(String error) {
        this.error = error;
    }
}
