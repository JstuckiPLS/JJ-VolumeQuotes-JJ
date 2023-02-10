package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.organization.OrganizationPricingEntity;

/**
 * VO to hold the Organization information and profiles information when saving customer pricing data.
 *
 * @author Hima Bindu Challa
 * @author Ashwini Neelgund
 *
 */
public class LtlCustomerPricingVO implements Serializable {

    private static final long serialVersionUID = 2237612123572121232L;

    private Long orgId;
    private String customerName;
    private OrganizationPricingEntity orgPricing;
    private Boolean goShipBusinessUnit;
    private List<LtlCustomerPricingProfileVO> pricingProfiles;

    public Long getOrgId() {
        return orgId;
    }
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public OrganizationPricingEntity getOrgPricing() {
        return orgPricing;
    }
    public void setOrgPricing(OrganizationPricingEntity orgPricing) {
        this.orgPricing = orgPricing;
    }
    public List<LtlCustomerPricingProfileVO> getPricingProfiles() {
        return pricingProfiles;
    }
    public void setPricingProfiles(List<LtlCustomerPricingProfileVO> pricingProfiles) {
        this.pricingProfiles = pricingProfiles;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("orgId", orgId)
                .append("customerName", customerName);

        return builder.toString();
    }

    public boolean isGoShipBusinessUnit() {
        return goShipBusinessUnit;
    }
    public void setIsGoShipBusinessUnit(boolean goShipBusinessUnit) {
        this.goShipBusinessUnit = goShipBusinessUnit;
    }
}
