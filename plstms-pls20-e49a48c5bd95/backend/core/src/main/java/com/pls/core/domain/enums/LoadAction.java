package com.pls.core.domain.enums;

/**
 * Load action enum for load details entity. Can have pickup and delivery values.
 * Describes action for current load details.
 *
 * @author Denis Zhupinsky
 */
public enum LoadAction {
    PICKUP("P"), DELIVERY("D");

    private String action;

    LoadAction(String action) {
        this.action = action;
    }

    /**
     * Get loadAction of current enum by String action.
     *
     * @param action loadAction to find
     * @return instance of current enum
     */
    public static LoadAction getLoadActionBy(String action) {
        for (LoadAction loadAction : values()) {
            if (loadAction.action.equals(action)) {
                return loadAction;
            }
        }

        throw new IllegalArgumentException(String.format("Cannot get LoadAction object by value: '%s'", action));
    }

    /**
     * Related DB value.
     * 
     * @return the loadAction for the field action.
     */
    public String getLoadAction() {
        return action;
    }

    @Override
    public String toString() {
        return action;
    }
}
