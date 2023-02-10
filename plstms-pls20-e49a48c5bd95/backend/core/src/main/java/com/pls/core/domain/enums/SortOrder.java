package com.pls.core.domain.enums;

/**
 * Represents the Order of sorting.
 * 
 * @author Gleb Zgonikov
 */
public enum SortOrder {
    ASC("ASC"),
    DESC("DESC");

    private String name;

    SortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Get {@link SortOrder} by {@link SortOrder#getName()} value.
     * 
     * @param name {@link SortOrder#getName()} value.
     * @return Not <code>null</code> {@link SortOrder}. ASC if not found.
     */
    public static SortOrder getByName(String name) {
        if (name != null) {
            for (SortOrder order : SortOrder.values()) {
                if (order.name.equalsIgnoreCase(name)) {
                    return order;
                }
            }
        }
        return ASC;
    }
}