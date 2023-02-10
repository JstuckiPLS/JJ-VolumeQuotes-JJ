package com.pls.ltlrating.domain.enums;

import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;

/**
 * Upcharge type for {@link LtlFuelSurchargeEntity}.
 * FL - Flat, PC - Percent.
 *
 *  These are the standard values in other tables to represent Flat/Percent types.
 *  User can enter and save both Upcharge Flat and Upcharge Percent rates
 *  but only one is effective at any point of time.
 *  Hence this new column is added to set what type of upcharge is effective.
 *
 * @author Stas Norochevskiy
 *
 */
public enum UpchargeType {
    FL, PC;
}
