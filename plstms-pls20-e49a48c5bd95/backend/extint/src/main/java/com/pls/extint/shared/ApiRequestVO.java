package com.pls.extint.shared;

import java.io.Serializable;

import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.domain.ApiTypeEntity;

/**
 * Base VO for the request data for calling the API services.
 * 
 * @author Pavani Challa
 * 
 */
public class ApiRequestVO implements Serializable {

    private static final long serialVersionUID = 3212346532341239876L;

    private ApiTypeEntity apiType;

    private Long loadId;

    private String carrierRefNum;

    private String shipperRefNum;

    private String bol;

    private Long carrierOrgId;

    private Long shipperOrgId;

    private String carrierScac;

    private ApiLogEntity requestLog;

    public ApiTypeEntity getApiType() {
        return apiType;
    }

    /**
     * Sets the API Type for this request. API Type contains the request metadata, response metadata and api details (with credentials).
     * 
     * @param apiType
     *            api type for this request
     */
    public void setApiType(ApiTypeEntity apiType) {
        this.apiType = apiType;

        this.requestLog = null;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long orgId) {
        this.carrierOrgId = orgId;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getCarrierRefNum() {
        return carrierRefNum;
    }

    public void setCarrierRefNum(String carrierRefNum) {
        this.carrierRefNum = carrierRefNum;
    }

    public String getShipperRefNum() {
        return shipperRefNum;
    }

    public void setShipperRefNum(String shipperRefNum) {
        this.shipperRefNum = shipperRefNum;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getCarrierScac() {
        return carrierScac;
    }

    public void setCarrierScac(String carrierScac) {
        this.carrierScac = carrierScac;
    }

    /**
     * Creates a new API_LOG entity if one doesn't exist to track the progress of API call.
     * 
     * @return the API_LOG entity
     */
    public ApiLogEntity getRequestLog() {
        if (requestLog == null) {
            requestLog = new ApiLogEntity();
            requestLog.setApiTypeId(apiType.getId());
            requestLog.setLoadId(loadId);
            requestLog.setCarrierReferenceNumber(carrierRefNum);
            requestLog.setShipperReferenceNumber(shipperRefNum);
            requestLog.setBol(bol);
        }

        return requestLog;
    }

    public void setRequestLog(ApiLogEntity requestLog) {
        this.requestLog = requestLog;
    }
}
