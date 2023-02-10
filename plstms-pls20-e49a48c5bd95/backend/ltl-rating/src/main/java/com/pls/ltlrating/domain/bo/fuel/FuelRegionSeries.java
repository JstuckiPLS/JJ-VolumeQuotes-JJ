package com.pls.ltlrating.domain.bo.fuel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about fuel prices for specified region.
 * 
 * @author Aleksandr Leshchenko
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuelRegionSeries {
    /**
     * Fuel Region ID
     */
    @JsonProperty("series_id")
    private String seriesId;

    /**
     * List of requested fuel prices and their effective dates.<br>
     * In our case we request only one latest information.<br>
     * So regular response should look like:[["20170313",2.564]]
     */
    private List<List<String>> data;

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
