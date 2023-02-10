package com.pls.shipment.service;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;

/**
 * Utility for processing shipments data.
 *
 * @author Denis Zhupinsky (Team International)
 */
public final class ShipmentUtils {

    private static final String AM_PM_FORMAT = "hh:mm a";

    private static final int MINUTES_IN_A_DAY = 60 * 24;

    private ShipmentUtils() {
    }

    /**
     * Get prepared string of city, state and zip.
     *
     * @param address address
     * @return city, state, zip string
     */
    public static String getCityStateZip(AddressEntity address) {
        String city = address.getCity();
        String zip = address.getZip();
        String state = getState(address);

        StringBuilder builder = new StringBuilder();
        if (city != null && StringUtils.isNotBlank(city)) {
            builder.append(city);
        }
        if (state != null && StringUtils.isNotBlank(state)) {
            builder.append(", ");
            builder.append(state);
        }
        if (zip != null && StringUtils.isNotBlank(zip)) {
            builder.append(", ");
            builder.append(zip);
        }
        return builder.toString();
    }

    private static String getState(AddressEntity address) {
        String state = null;
        if (address.getState() != null) {
            state = address.getState().getStateName() == null && address.getState().getStatePK() != null ? address
                    .getState().getStatePK().getStateCode() : address.getState().getStateName();
        }
        return state;
    }

    /**
     * Joins all parameters to one string for notes.
     * 
     * @param accessorials
     *            list of selected pickup and delivery accessorials. Joined using coma separator.
     * @param addressNotes
     *            pickup or delivery notes from address book. All new lines are replaced with space.
     * @param origin
     *            if <code>true</code> then only origin accessorials will be added to the result.<br>
     *            if <code>false</code> then only destination accessorials will be added to the result.
     * @param loadNotes
     *            load specific pickup or delivery notes. All new lines are replaced with space.
     * @return notes string as "accessorials. addressNotes. loadNotes."
     */
    public static String getAggregatedNotes(Set<LtlLoadAccessorialEntity> accessorials, boolean origin, String addressNotes, String loadNotes) {
        StringBuilder result = new StringBuilder();
        addAccessorials(result, accessorials, origin ? LtlAccessorialGroup.PICKUP : LtlAccessorialGroup.DELIVERY);
        addNotes(result, addressNotes);
        addNotes(result, loadNotes);
        return result.toString().trim();
    }

    private static void addNotes(StringBuilder result, String notees) {
        if (StringUtils.isNotBlank(notees)) {
            String adjustedNotes = StringUtils.replace(notees, "\n", " ").trim();
            if (StringUtils.isNotBlank(adjustedNotes)) {
                result.append(adjustedNotes).append(". ");
            }
        }
    }

    private static void addAccessorials(StringBuilder result, Set<LtlLoadAccessorialEntity> accessorials, LtlAccessorialGroup group) {
        if (accessorials != null && !accessorials.isEmpty()) {
            for (LtlLoadAccessorialEntity accessorial : accessorials) {
                if (group == accessorial.getAccessorial().getAccessorialGroup()) {
                    result.append(accessorial.getAccessorial().getDescription()).append(", ");
                }
            }
            if (result.length() > 0) {
                result.delete(result.length() - 2, result.length());
                result.append(". ");
            }
        }
    }

    /**
     * Get dimensions of load material.
     *
     * @param material load material
     * @return dimensions of load material
     */
    public static String getMaterialDimensions(LoadMaterialEntity material) {
        if (material.getWidth() != null || material.getHeight() != null || material.getLength() != null) {
            String length = ObjectUtils.toString(material.getLength());
            String width = ObjectUtils.toString(material.getWidth());
            String height = ObjectUtils.toString(material.getHeight());

            return String.format("%s x %s x %s", length, width, height);
        }

        return EMPTY;
    }

    /**
     *  Converts time represented as long variable into its string value.
     * 
     * @param time - time representation as long variable
     * @return time in string format
     */
    public static String getGuaranteedTime(Long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time value to be converted should be positive.");
        }
        if (time == 2400L) {
            return "EOD";
        }
        int hours = 12;
        boolean am = true;
        if (time >= 100) {
            hours = (int) (time / 100);
            if (hours > 12) {
                am = false;
                hours = hours - 12;
            } else if (hours == 12) {
                am = false;
            }
        }
        int minutes = (int) (time % 100);
        return String.format("%d", hours) + ':' + String.format("%02d ", minutes) + (am ? "AM" : "PM");
    }

    /**
     * Formats time in AM/PM format.
     * 
     * @param time - time to be formatted
     * @return - AM/PM time
     */
    public static String formatAmPm(Date time) {
        DateFormat formatter = new SimpleDateFormat(AM_PM_FORMAT, Locale.US);
        return formatter.format(time);
    }

    /**
     * Checks whether Load has Blind Bol accessorial or not.
     *
     * @param load - load to check
     * @return <code>true</code> if the Load has Blind Bol, otherwise <code>false</code>
     */
    public static boolean isBlindBol(LoadEntity load) {
        boolean blindBol = load.getLtlAccessorials() != null && load.getLtlAccessorials().stream()
                .anyMatch(accessorial -> LtlAccessorialType.BLIND_BOL.getCode().equals(accessorial.getAccessorial().getId()));

        if (!blindBol && load.getActiveCostDetail() != null) {
            blindBol = load.getActiveCostDetail().getCostDetailItems().stream()
                    .anyMatch(item -> (item.getOwner() == CostDetailOwner.S || item.getOwner() == CostDetailOwner.C)
                            && LtlAccessorialType.BLIND_BOL.getCode().equals(item.getAccessorialType()));
        }
        return blindBol;
    }

    /**
     * Checks whether Load has different address countries to show Customs Broker info.
     *
     * @param load - load to check
     * @return <code>true</code> if the Load has different address countries, otherwise <code>false</code>
     */
    public static boolean showCustomsBroker(LoadEntity load) {
        return ObjectUtils.notEqual(load.getOrigin().getAddress().getCountryCode(), load.getDestination().getAddress().getCountryCode());
    }

    /**
     * Converts travel time represented as long variable into its string value.
     *
     * @param travelTime
     *            - duration of the trip in minutes
     * @return Estimated Transit Days label string representation
     */
    public static String getEstimatedTransitDaysLabel(Long travelTime) {
        // 1 hour, 4 hour, 6 hour trip is still 1 transit day.
        // So we always have no less than 1 transit days
        // But for manual carrier rating types travel time might not be specified, but still we have go ahead
        // with the load build. In such cases the travelTime is 0 and must be displayed as 'N/A'.
        int travelDays = getTravelDays(travelTime);
        return String.format("Estimated Transit Day%s: %s", travelDays > 1 ? "s" : "",
                travelDays == 0 ? "N/A" : travelDays);
    }

    private static int getTravelDays(Long travelTime) {
        return travelTime != null && travelTime != 0 ? (int) Math.ceil(travelTime / MINUTES_IN_A_DAY) == 0 ? 1
                : (int) Math.ceil(travelTime / MINUTES_IN_A_DAY) : 0;
    }

    /**
     * Checks if can update Freight Bill Date. The Freight Bill Date should update only when Vendor Bill is
     * received are received and Delivery Date is present.
     * Note that we removed the requirement to have all customer required documents present from this check.
     * 
     * @param load
     *            - {@link LoadEntity}
     * @return true if can update Freight Bill Date
     */
    public static Boolean isCanUpdateFrtBillDate(LoadEntity load) {
        return load.getVendorBillDetails().getFrtBillRecvFlag()
                && !load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty() && load.getDestination() != null
                && load.getDestination().getDeparture() != null;
    }
}
