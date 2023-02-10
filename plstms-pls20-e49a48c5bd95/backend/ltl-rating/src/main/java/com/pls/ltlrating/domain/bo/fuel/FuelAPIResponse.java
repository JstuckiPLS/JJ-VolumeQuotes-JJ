package com.pls.ltlrating.domain.bo.fuel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response for Fuel API call.
 * 
 * @author Aleksandr Leshchenko
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuelAPIResponse {
    private List<FuelRegionSeries> series;

    public List<FuelRegionSeries> getSeries() {
        return series;
    }

    public void setSeries(List<FuelRegionSeries> series) {
        this.series = series;
    }
}
