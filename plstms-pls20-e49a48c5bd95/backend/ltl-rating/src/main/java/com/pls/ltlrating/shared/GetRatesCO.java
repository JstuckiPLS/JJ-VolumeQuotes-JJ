package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.enums.GetRatesDateType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Criteria object to represent Get Rates criteria object.
 *
 * @author Mikhail Boldinov, 22/02/13
 *
 * @author Hima Bindu Challa
 */
public class GetRatesCO implements Serializable {

    private static final long serialVersionUID = 8236281753812735L;

    private Date fromDate;

    private Date toDate;

    private GetRatesDateType dateType;

    private List<PricingType> pricingTypes;

    private Status status;

    private Long customer;

    private PricingDetailType copyFromPricingDetailType;

    private Long profileId;

    private String pricingGroup;

    private Boolean prohibitedNLiabilities;


    /**
     * GetRatesCO default constructor.
     */
    public GetRatesCO() {

    }
    /**
     * GetRatesCO constructor using query parameters.
     *
     * @param param - query parameters
     */
    public GetRatesCO(String param) {
        throw new IllegalArgumentException("GetRatesCO(param) not implemented. " + param);
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public GetRatesDateType getDateType() {
        return dateType;
    }

    public void setDateType(GetRatesDateType dateType) {
        this.dateType = dateType;
    }

    public List<PricingType> getPricingTypes() {
        return pricingTypes;
    }

    public void setPricingTypes(List<PricingType> pricingTypes) {
        this.pricingTypes = pricingTypes;
    }

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PricingDetailType getCopyFromPricingDetailType() {
        return copyFromPricingDetailType;
    }
    public void setCopyFromPricingDetailType(PricingDetailType copyFromPricingDetailType) {
        this.copyFromPricingDetailType = copyFromPricingDetailType;
    }
    public Long getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    public String getPricingGroup() {
        return pricingGroup;
    }
    public void setPricingGroup(String pricingGroup) {
        this.pricingGroup = pricingGroup;
    }

    public Boolean getProhibitedNLiabilities() {
        return prohibitedNLiabilities;
    }
    public void setProhibitedNLiabilities(Boolean prohibitedNLiabilities) {
        this.prohibitedNLiabilities = prohibitedNLiabilities;
    }
    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("fromDate", getFromDate())
                .append("toDate", getToDate())
                .append("dateType", getDateType())
                .append("pricingTypes", getPricingTypes())
                .append("status", getStatus())
                .append("customer", getCustomer())
                .append("copyFromPricingDetailType", getCopyFromPricingDetailType());
        return builder.toString();
    }

}
