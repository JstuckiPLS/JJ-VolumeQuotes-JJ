package com.pls.core.domain.enums;

/**
 * Action for Bill To identifiers enumeration.
 *
 * @author Dmitriy Davydenko, 10/03/16
 */
public enum DefaultValuesAction {
    RESTRICT("R", "Don't allow to use different format then Default or Start/End"),
    AUDIT("A", "Send to Invoice Audit if value differs from Default or Start/End");

    private String code;

    private String description;

    DefaultValuesAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method returns {@link DefaultValuesAction} by code.
     *
     * @param code {@link DefaultValuesAction} code
     * @return {@link DefaultValuesAction}
     */
    public static DefaultValuesAction getByCode(String code) {
        for (DefaultValuesAction defaultValuesAction : DefaultValuesAction.values()) {
            if (defaultValuesAction.code.equals(code)) {
                return defaultValuesAction;
            }
        }
        throw new IllegalArgumentException("Can not get Default Values Action by code: " + code);
    }
}