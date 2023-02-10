package com.pls.invoice.domain.xml.finance;

/**
 * Additional order info.
 *
 * @author Jasmin Dhamelia.
 */
public class AddlOrderInfo {

    private Double carrierLinehaul;

    private Double fuelBenchmark;

    private Double benchmarkRate;

    public Double getCarrierLinehaul() {
        return carrierLinehaul;
    }

    public void setCarrierLinehaul(Double carrierLinehaul) {
        this.carrierLinehaul = carrierLinehaul;
    }

    public Double getFuelBenchmark() {
        return fuelBenchmark;
    }

    public void setFuelBenchmark(Double fuelBenchmark) {
        this.fuelBenchmark = fuelBenchmark;
    }

    public Double getBenchmarkRate() {
        return benchmarkRate;
    }

    public void setBenchmarkRate(Double benchmarkRate) {
        this.benchmarkRate = benchmarkRate;
    }

}
