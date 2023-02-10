package com.pls.ltlrating.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The VO that holds valid profiles before calculating final rates.
 *
 * @author Hima Bindu Challa
 */
public class LtlRatingVO {
    /*
     * ***Copied from service***
     * There can be more than one matching profile(Blanket, Customer Specific) for each org and we cannot
     * decide at this point which one to use. Also need to capture BUY and SELL separately as the rates need
     * to be calculated separately.
     * 
     * We need to capture multiple profiles for each org because there can Blanket profile and any customer
     * specific profile. Since we don't know which profile finally fits as we still need to find the
     * accessorials and guaranteed price.
     * 
     * [AL]: Ok, I got you, but right now we don't have logic to compare final total costs of all pricing
     * profiles for one carrier and we're just using first matching profile.
     * I basically think that we should use some hierarchy of pricing profiles.
     * For example CSP shouldn't be used when Buy/Sell exists.
     */
    // key is carrier org ID
    private Map<Long, List<CarrierRatingVO>> carrierPricingDetails = new HashMap<Long, List<CarrierRatingVO>>();

    public Map<Long, List<CarrierRatingVO>> getCarrierPricingDetails() {
        return carrierPricingDetails;
    }

    public void setCarrierPricingDetails(Map<Long, List<CarrierRatingVO>> carrierPricingDetails) {
        this.carrierPricingDetails = carrierPricingDetails;
    }

    public Set<Long> getValidCarrierProfileDetailIds() {
        return carrierPricingDetails.values().stream().flatMap(List::stream)
                .flatMap(r -> Stream.of(r.getRate().getPricingDetails().getProfileDetailId(),
                        r.getShipperRate() == null ? null : r.getShipperRate().getPricingDetails().getProfileDetailId()))
                .distinct().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
