package com.pls.ltlrating.domain.bo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.ltlrating.domain.DotRegionEntity;
import com.pls.ltlrating.domain.DotRegionFuelEntity;

/**
 * Business object that is used to select array of {@link DotRegionFuelEntity}.
 *
 * @author Stas Norochevskiy
 *
 */
public class GetRegionsFuelCO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetRegionsFuelCO.class);

    private static final String DELIMITER = ",";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static final int FROM_DATE_POS = 0;
    private static final int TO_DATE_POS = 1;
    private static final int FULL_PARAMS_NUM = 3;

    /**
     * Selected From date
     * If "Range" = "Today", From date = today's date
     * if "Range" = "This Week", From date = Sunday date of this week and so on.
     * These values should be set in View layer
     */
    private Date fromDate;

    /**
     * Selected To date
     * If "Range" = "Today", To date = today's date
     * if "Range" = "This Week", To date = Today's date
     * if "Range" = "Last Week", To date = Last Saturday Date and so on.
     * These values should be set in View layer
     */
    private Date toDate;

    /**
     * List of regions that should be compared. If this value is null, all regions should be considered.
     * {@link DotRegionEntity}.
     */
    List<Long> regionIds;

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

    public List<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(List<Long> regionIds) {
        this.regionIds = regionIds;
    }

    /**
     * Empty constructor.
     */
    public GetRegionsFuelCO() {

    }

    /**
     * Constructor used in RESRful.
     * @param param fromDate,toDate, regionId,regionId,...
     */
    public GetRegionsFuelCO(String param) {
        String[] params = param.split(DELIMITER);

        DateFormat formater = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault());

        if (params.length >= 2) {
            try {
                fromDate = formater.parse(params[FROM_DATE_POS]);
            } catch (ParseException e) {
                LOGGER.error("Cannot parse date [" + params[FROM_DATE_POS] + "]", e);
            }
            try {
                toDate = formater.parse(params[TO_DATE_POS]);
            } catch (ParseException e) {
                LOGGER.error("Cannot parse date [" + params[TO_DATE_POS] + "]", e);
            }
           // Extract IDs
            if (params.length >= FULL_PARAMS_NUM) {
                regionIds = new ArrayList<Long>();
                for (int i = 2; i < params.length; i++) {
                    regionIds.add(Long.parseLong(params[i]));
                }
            }
        }
    }
}
