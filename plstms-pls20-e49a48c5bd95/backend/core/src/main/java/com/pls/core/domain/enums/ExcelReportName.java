package com.pls.core.domain.enums;

/**
 * Excel report name enum.
 * 
 * @author Alexander Nalapko
 *
 */
public enum ExcelReportName {
    SAVINGS("Savings"), UNBILLED("Unbilled"), ACTIVITY("Activity"), CARRIER_ACTIVITY("Carrier Activity"), SHIPMENT_CREATION("Shipment"), LOST_SAVINGS("Lost");

    private String name;

    ExcelReportName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Retrieves enum value by report name.
     * 
     * @param name
     *            - report name
     * @return - respective enum value
     */
    public static ExcelReportName getValue(String name) {
        for (ExcelReportName value : ExcelReportName.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
