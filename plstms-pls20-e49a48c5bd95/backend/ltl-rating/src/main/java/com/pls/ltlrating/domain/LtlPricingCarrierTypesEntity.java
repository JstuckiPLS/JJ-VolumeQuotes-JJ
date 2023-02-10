package com.pls.ltlrating.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Ltl rating carrier types entity.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "LTL_RATING_CARRIER_TYPES")
public class LtlPricingCarrierTypesEntity implements Serializable {

    private static final long serialVersionUID = 1465469526579824395L;

    @Id
    @Column(name = "LTL_RATING_CARRIER_TYPE")
    private String ltlRatingCarrierType;

    @Column(name = "DESCRIPTION")
    private String description;

    public String getLtlRatingCarrierType() {
        return ltlRatingCarrierType;
    }

    public void setLtlRatingCarrierType(String ltlRatingCarrierType) {
        this.ltlRatingCarrierType = ltlRatingCarrierType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
