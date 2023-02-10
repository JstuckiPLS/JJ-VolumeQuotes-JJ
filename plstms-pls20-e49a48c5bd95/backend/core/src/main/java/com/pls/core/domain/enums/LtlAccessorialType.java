package com.pls.core.domain.enums;

import static com.pls.core.domain.enums.LtlAccessorialGroup.DELIVERY;
import static com.pls.core.domain.enums.LtlAccessorialGroup.PICKUP;

/**
 * Enum that stands for types of LTL accessories.
 *
 * @author Denis Zhupinsky
 */
public enum LtlAccessorialType {
    RESIDENTIAL_PICKUP("REP", "Residential Pickup", PICKUP),
    LIFT_GATE_PICKUP("LFC", "Liftgate Pickup", PICKUP),
    INSIDE_PICKUP("IPU", "Inside Pickup", PICKUP),
    OVER_DIMENSION("ODM", "Over Dimension", PICKUP),
    BLIND_BOL("BLB", "Blind BOL", PICKUP),
    LIMITED_ACCESS_PICKUP("LAP", "Limited Access", PICKUP),

    RESIDENTIAL_DELIVERY("RES", "Residential Delivery", DELIVERY),
    LIFT_GATE_DELIVERY("LFT", "Liftgate Delivery", DELIVERY),
    INSIDE_DELIVERY("IDL", "Inside Delivery", DELIVERY),
    SORT_SEGREGATE_DELIVERY("SSD", "Sort & Segregate Delivery", DELIVERY),
    NOTIFY_DELIVERY("NDR", "Notify Delivery", DELIVERY),
    LIMITED_ACCESS_DELIVERY("LAD", "Limited Access", DELIVERY);

    private String code;

    private String description;

    private LtlAccessorialGroup group;

    LtlAccessorialType(String code, String description, LtlAccessorialGroup group) {
        this.code = code;
        this.description = description;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public LtlAccessorialGroup getGroup() {
        return group;
    }

    /**
     * Get value of current enum by String.
     *
     * @param code ltl accessorial code to find
     * @return instance of current enum
     */
    public static LtlAccessorialType getByCode(String code) {
        for (LtlAccessorialType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Cannot get LtlAccessorialType object by code: '" + code + "'");

    }
}
