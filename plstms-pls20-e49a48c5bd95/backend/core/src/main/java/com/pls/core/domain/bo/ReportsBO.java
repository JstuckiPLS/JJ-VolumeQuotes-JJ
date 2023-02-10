package com.pls.core.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Reports BO.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class ReportsBO {

    private String shipperCode;
    private String ownerName;
    private Long loadId;
    private Date shipDate;
    private Date glDate;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal margin;

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }
}
