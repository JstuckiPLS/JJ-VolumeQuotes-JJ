package com.pls.core.domain.bo;

import java.math.BigInteger;

import com.pls.core.domain.enums.OrganizationStatus;

/**
 * Object for SCAC Codes screen.
 * 
 * @author Stas Norochevskiy
 *
 */
public class SimpleCarrierInfoBO {

    private BigInteger carrierId;

    private String scac;

    private String name;

    private OrganizationStatus status;

    private BigInteger apiId;

    public BigInteger getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(BigInteger carrierId) {
        this.carrierId = carrierId;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public Character getStatusChar() {
        return status.getOrganizationStatus();
    }

    /**
     * Set status from it's string representation.
     * @param charStatus status received from database
     */
    public void setStatusChar(Character charStatus) {
        this.status = OrganizationStatus.getOrganizationStatusBy(charStatus);
    }

    public BigInteger getApiId() {
        return apiId;
    }

    public void setApiId(BigInteger apiId) {
        this.apiId = apiId;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

}
