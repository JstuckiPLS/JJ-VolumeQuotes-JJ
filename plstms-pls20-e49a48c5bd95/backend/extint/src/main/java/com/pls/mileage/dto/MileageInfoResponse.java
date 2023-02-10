package com.pls.mileage.dto;

import java.math.BigDecimal;

public class MileageInfoResponse {

    private BigDecimal totalMiles;

    public BigDecimal getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(BigDecimal totalMiles) {
        this.totalMiles = totalMiles;
    }

}
