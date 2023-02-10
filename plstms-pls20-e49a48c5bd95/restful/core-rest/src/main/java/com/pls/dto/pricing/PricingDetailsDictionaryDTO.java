package com.pls.dto.pricing;

import java.util.List;

import com.pls.dto.ValueLabelDTO;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.extint.shared.DataModuleVO;

/**
 * DTO for keeping dictionary data for pricing details.
 * 
 * @author Artem Arapov
 * 
 */
public class PricingDetailsDictionaryDTO {

    private List<ValueLabelDTO> ltlCostTypes;

    private List<ValueLabelDTO> ltlMarginTypes;

    private List<ValueLabelDTO> ltlServiceTypes;

    private List<CommodityClassDTO> classes = CommodityClassDTO.getList();

    private List<ValueLabelDTO> weightUOM;

    private List<ValueLabelDTO> distanceUOM;

    private List<ValueLabelDTO> zones;

    private List<DataModuleVO> smc3Tariffs;

    private List<ValueLabelDTO> fuelWeekDays;

    public List<ValueLabelDTO> getLtlCostTypes() {
        return ltlCostTypes;
    }

    public void setLtlCostTypes(List<ValueLabelDTO> ltlCostTypes) {
        this.ltlCostTypes = ltlCostTypes;
    }

    public List<ValueLabelDTO> getLtlMarginTypes() {
        return ltlMarginTypes;
    }

    public void setLtlMarginTypes(List<ValueLabelDTO> ltlMarginTypes) {
        this.ltlMarginTypes = ltlMarginTypes;
    }

    public List<ValueLabelDTO> getLtlServiceTypes() {
        return ltlServiceTypes;
    }

    public void setLtlServiceTypes(List<ValueLabelDTO> ltlServiceTypes) {
        this.ltlServiceTypes = ltlServiceTypes;
    }

    public List<CommodityClassDTO> getClasses() {
        return classes;
    }

    public void setClasses(List<CommodityClassDTO> classes) {
        this.classes = classes;
    }

    public List<ValueLabelDTO> getWeightUOM() {
        return weightUOM;
    }

    public void setWeightUOM(List<ValueLabelDTO> weightUOM) {
        this.weightUOM = weightUOM;
    }

    public List<ValueLabelDTO> getDistanceUOM() {
        return distanceUOM;
    }

    public void setDistanceUOM(List<ValueLabelDTO> distanceUOM) {
        this.distanceUOM = distanceUOM;
    }

    public List<ValueLabelDTO> getZones() {
        return zones;
    }

    public void setZones(List<ValueLabelDTO> zones) {
        this.zones = zones;
    }

    public List<DataModuleVO> getSmc3Tariffs() {
        return smc3Tariffs;
    }

    public void setSmc3Tariffs(List<DataModuleVO> smc3Tariffs) {
        this.smc3Tariffs = smc3Tariffs;
    }

    public List<ValueLabelDTO> getFuelWeekDays() {
        return fuelWeekDays;
    }

    public void setFuelWeekDays(List<ValueLabelDTO> fuelWeekDays) {
        this.fuelWeekDays = fuelWeekDays;
    }
}
