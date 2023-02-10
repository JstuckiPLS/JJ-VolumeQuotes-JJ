package com.pls.core.common;

/**
 * Enum with possible mime types. Name of each type will be correspond to file extension of document that will be created with that type.
 *
 * @author Denis Zhupinsky (Team International)
 */
public enum MimeTypes {
    PDF("application/pdf"), TIFF("image/tiff"), TIF("image/tiff"), PNG("image/png"), GIF("image/gif"), BMP("image/bmp"), JPEG("image/jpeg"),
    JPG("image/jpeg"), XLSX("application/vnd.ms-excel"), XLS("application/vnd.ms-excel"), CSV("text/csv"), XML("application/xml"), TXT("text/txt");

    private String mimeString;

    MimeTypes(String mimeString) {
        this.mimeString = mimeString;
    }

    /**
     * Get MimeType by string value.
     *
     * @param value string representation of mime type
     * @return {@link MimeTypes}
     */
    public static MimeTypes getByValue(String value) {
        for (MimeTypes mimeType : MimeTypes.values()) {
            if (mimeType.mimeString.equalsIgnoreCase(value)) {
                return mimeType;
            }
        }

        return null;
    }

    /**
     * Get MimeType by its name.
     *
     * @param mimeName mime type name
     * @return {@link MimeTypes}
     */
    public static MimeTypes getByName(String mimeName) {
        for (MimeTypes mimeType : MimeTypes.values()) {
            if (mimeType.name().equalsIgnoreCase(mimeName)) {
                return mimeType;
            }
        }

        return null;
    }

    public String getMimeString() {
        return mimeString;
    }
}
