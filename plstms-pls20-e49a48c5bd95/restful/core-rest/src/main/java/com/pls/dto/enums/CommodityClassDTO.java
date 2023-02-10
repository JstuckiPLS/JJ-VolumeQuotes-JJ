package com.pls.dto.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Commodity classes.
 * 
 * @author Gleb Zgonikov
 */
public enum CommodityClassDTO {
    CLASS_50("50"),
    CLASS_55("55"),
    CLASS_60("60"),
    CLASS_65("65"),
    CLASS_70("70"),
    CLASS_77_5("77.5"),
    CLASS_85("85"),
    CLASS_92_5("92.5"),
    CLASS_100("100"),
    CLASS_110("110"),
    CLASS_125("125"),
    CLASS_150("150"),
    CLASS_175("175"),
    CLASS_200("200"),
    CLASS_250("250"),
    CLASS_300("300"),
    CLASS_400("400"),
    CLASS_500("500");

    /**
     * Returns list of all commodity classes.
     *
     * @return list of the {@link CommodityClassDTO}
     */
    public static List<CommodityClassDTO> getList() {
        return Arrays.asList(CommodityClassDTO.values());
    }

    /**
     * Get {@link CommodityClassDTO} value by it's label.
     * 
     * @param code
     *            to get
     * @return {@link CommodityClassDTO} or exception if not found
     */
    public static CommodityClassDTO getByCode(String code) {
        for (CommodityClassDTO type : CommodityClassDTO.values()) {
            if (type.label.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No Commodity cass registered with code: " + code);
    }

    private String label;

    CommodityClassDTO(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
