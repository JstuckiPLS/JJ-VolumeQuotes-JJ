package com.pls.dto.query;

/**
 * DTO object to transfer arguments from UI side to server side.
 *
 * @author Artem Arapov
 */
public class DateRangeQueryDTO {

    private static final String DELIMITER = ",";

    private static final int SHORT_STRING_LENGTH = 1;

    private static final int MIDDLE_STRING_LENGTH = 2;

    private static final int LONG_STRING_LENGTH = 3;

    private String dateRange = "";

    private String fromDate = "";

    private String toDate = "";

    /**
     * Default constructor.
     */
    public DateRangeQueryDTO() {
    }

    /**
     * Constructor with date range parameter.
     * 
     * @param param
     *            date range parameter
     */
    public DateRangeQueryDTO(String param) {
        int count = param.replaceAll("[^,]", "").length();
        String[] tokens = param.split(DELIMITER);

        if (count == 0 || tokens.length == SHORT_STRING_LENGTH) {
            dateRange = tokens[0];
        }

        if (count == 2 && tokens.length == MIDDLE_STRING_LENGTH) {
            dateRange = tokens[0];
            fromDate = tokens[1];
        }

        if (count == 2 && tokens.length == LONG_STRING_LENGTH) {
            dateRange = tokens[0];
            fromDate = tokens[1];
            toDate = tokens[2];
        }
    }

    public String getDateRange() {
        return dateRange;
    }

    /**
     * Set date range.
     * 
     * @param dateRange
     *            the dateRange to set. Should be values from {@link com.pls.dto.enums.DateRange}.
     */
    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getFromDate() {
        return fromDate;
    }

    /**
     * Set from date.
     * 
     * @param fromDate
     *            the fromDate to set. Should be in format 'yyyy-MM-dd Z'
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    /**
     * Set to date.
     * 
     * @param toDate
     *            the toDate to set. Should be in format 'yyyy-MM-dd Z'
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        if ((fromDate == null || fromDate.isEmpty()) && (toDate == null || toDate.isEmpty())) {
            return dateRange;
        }

        return dateRange + DELIMITER + fromDate + DELIMITER + toDate;
    }
}
