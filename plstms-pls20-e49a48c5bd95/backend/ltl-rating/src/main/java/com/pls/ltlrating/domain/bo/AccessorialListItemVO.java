package com.pls.ltlrating.domain.bo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Business object that is used to hold the list of the accessorials for active/expired/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class AccessorialListItemVO implements Serializable {

    private static final long serialVersionUID = -8914962561798262827L;

    private Long id;

    private String type;

    private String origin;

    private String destination;

    private BigDecimal minCost;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
