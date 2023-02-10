package com.pls.dto.address;

/**
 * Object to store day time value. Time is stored in 12 hour format, minutes should be equal to 0 or 30 minutes,
 * in order to set AM or PM value use {@link #setAm(Boolean)} method.
 *
 * @author Alexey Tarasyuk
 */
public final class PickupAndDeliveryWindowDTO {
    public static final int HR12 = 12;
    public static final int MIN59 = 59;

    private Integer hours;
    private Integer minutes;
    private Boolean am;

    /**
     * Default empty constructor.
     *
     * @deprecated use {@link PickupAndDeliveryWindowDTO#PickupWindowDTO(Integer, Integer, Boolean)} instead
     */
    @Deprecated
    public PickupAndDeliveryWindowDTO() {
    }

    /**
     * Constructor with all data.
     *
     * @param hours   - hours to be set
     * @param minutes - minutes to be set
     * @param am      - is time AM or PM
     */
    public PickupAndDeliveryWindowDTO(Integer hours, Integer minutes, Boolean am) {
        setHours(hours);
        setMinutes(minutes);
        setAm(am);
    }

    /**
     * Get daily time in an hours.
     *
     * @return daily time in an hours.
     */
    public Integer getHours() {
        return hours;
    }

    /**
     * Sets hours of a day value.
     *
     * @param hours should be in the interval from 1 to 12.
     */
    public void setHours(Integer hours) {
        if (hours == null || hours <= 0 || hours > HR12) {
            this.hours = HR12;
        } else {
            this.hours = hours;
        }
    }

    /**
     * Get minutes in an hour value.
     *
     * @return minutes in an hour value.
     */
    public Integer getMinutes() {
        return minutes;
    }

    /**
     * Sets minutes in an hour value.
     *
     * @param minutes should be only 0 or 30.
     */
    public void setMinutes(Integer minutes) {
        if (minutes == null || minutes < 0 || minutes > MIN59) {
            this.minutes = 0;
        } else {
            this.minutes = minutes;
        }
    }

    /**
     * Get AM or PM.
     *
     * @return <code>true</code> if current time is AM or false if time is PM.
     */
    public Boolean getAm() {
        return am;
    }

    /**
     * Set AM or PM.
     *
     * @param am if true that means AM time, false means PM time.
     */
    public void setAm(Boolean am) {
        this.am = am != null && am;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(hours).append(':');
        if (String.valueOf(minutes).length() == 1) {
            builder.append('0');
        }
        return builder.append(minutes).append(' ').append(am ? "AM" : "PM").toString();
    }
}
