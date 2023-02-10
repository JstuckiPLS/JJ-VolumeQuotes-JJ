package com.pls.smc3.dto;

/**
 * Density request detail DTO.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class DensityRequestDetailDTO {

    private String length;
    private String width;
    private String height;
    private String dimensionUnits;
    private String weight;
    private String weightUnits;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDimensionUnits() {
        return dimensionUnits;
    }

    public void setDimensionUnits(String dimensionUnits) {
        this.dimensionUnits = dimensionUnits;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightUnits() {
        return weightUnits;
    }

    public void setWeightUnits(String weightUnits) {
        this.weightUnits = weightUnits;
    }

}
