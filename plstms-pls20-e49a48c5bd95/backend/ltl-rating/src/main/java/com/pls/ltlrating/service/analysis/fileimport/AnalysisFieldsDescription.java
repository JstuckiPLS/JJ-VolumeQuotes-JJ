package com.pls.ltlrating.service.analysis.fileimport;

import org.apache.commons.lang3.StringUtils;

import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;


/**
 * Represents fields of {@link FAOutputDetailsEntity} entity. Also holds column header information for Excel file.
 *
 * @author Svetlana Kulish
 */
public enum AnalysisFieldsDescription {
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    SEQ("seq", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    SCAC("SCAC", false, 4),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    SHIPDATE("ShipDate", false, 100),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ORIGIN_CITY("Origin_City", false, 25),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ORIGIN_STATE("Origin_State", false, 25),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ORIGIN_ZIP("Origin_Zip", true, 15),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ORIGIN_OVERRIDE_ZIP("Origin_Override_Zip", false, 15),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ORIGIN_COUNTRY("Origin_Country", true, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    DEST_CITY("Dest_City", false, 25),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    DEST_STATE("Dest_State", false, 25),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    DEST_ZIP("Dest_Zip", true, 15),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    DEST_OVERRIDE_ZIP("Dest_Override_Zip", false, 15),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    DEST_COUNTRY("Dest_Country", true, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    PALLET("Pallet", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    PIECES("Pieces", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS1("Class1", true, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT1("Weight1", true, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS2("Class2", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT2("Weight2", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS3("Class3", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT3("Weight3", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS4("Class4", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT4("Weight4", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS5("Class5", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT5("Weight5", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS6("Class6", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT6("Weight6", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS7("Class7", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT7("Weight7", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS8("Class8", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT8("Weight8", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS9("Class9", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT9("Weight9", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CLASS10("Class10", false, 8),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    WEIGHT10("Weight10", false, 13),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL1("Accessorial1", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL2("Accessorial2", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL3("Accessorial3", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL4("Accessorial4", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL5("Accessorial5", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL6("Accessorial6", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL7("Accessorial7", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL8("Accessorial8", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL9("Accessorial9", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    ACCESSORIAL10("Accessorial10", false, 3),
    /**
     * Data for {@link FAOutputDetailsEntity#} field.
     */
    CALCULATE_FSC("Calculate_FSC", true, 1);

    /**
     * Get item by header text.
     *
     * @param header
     *            text
     * @return {@link AnalysisFieldsDescription}
     */
    public static AnalysisFieldsDescription getByHeaderText(String header) {
        AnalysisFieldsDescription result = null;

        String normalizedHeader = StringUtils.trimToNull(header);
        for (AnalysisFieldsDescription field : AnalysisFieldsDescription.values()) {
            if (StringUtils.equalsIgnoreCase(field.getHeader(), normalizedHeader)) {
                result = field;
                break;
            }
        }

        return result;
    }

    /**
     * Get item by name.
     *
     * @param name
     *            text
     * @return {@link AnalysisFieldsDescription}
     */
    public static AnalysisFieldsDescription getByName(String name) {
        for (AnalysisFieldsDescription c : AnalysisFieldsDescription.values()) {
            if (StringUtils.equalsIgnoreCase(c.name(), name)) {
                return c;
            }
        }
        return null;
    }

    private String header;

    private boolean required;

    private Integer maxLength;

    AnalysisFieldsDescription(String header, boolean required, Integer maxLength) {
        this.header = header;
        this.required = required;
        this.maxLength = maxLength;
    }

    public String getHeader() {
        return header;
    }

    public boolean isRequired() {
        return required;
    }

    public Integer getMaxLength() {
        return maxLength;
    }
}
