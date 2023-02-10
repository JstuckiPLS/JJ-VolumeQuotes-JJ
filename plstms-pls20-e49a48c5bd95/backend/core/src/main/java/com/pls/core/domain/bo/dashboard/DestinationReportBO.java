package com.pls.core.domain.bo.dashboard;

/**
 * DESTINATION_REPORT been.
 * 
 * @author Alexander Nalapko
 */
public class DestinationReportBO {
    private Long orgID;
    private String orgName;
    private String destState;
    private String origState;
    private Long lCount;
    private String precentOfTotal;

    public Long getOrgID() {
        return orgID;
    }

    public void setOrgID(Long orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDestState() {
        return destState;
    }

    public void setDestState(String destState) {
        this.destState = destState;
    }

    public String getOrigState() {
        return origState;
    }

    public void setOrigState(String origState) {
        this.origState = origState;
    }

    public String getPrecentOfTotal() {
        return precentOfTotal;
    }

    public void setPrecentOfTotal(String precentOfTotal) {
        this.precentOfTotal = precentOfTotal;
    }

    /**
     * l_count.
     * 
     * @return {@link Long}
     */
    public Long getlCount() {
        return lCount;
    }

    /**
     * l_count.
     * 
     * @param lCount
     *            {@link Long}
     */
    public void setlCount(Long lCount) {
        this.lCount = lCount;
    }
}
