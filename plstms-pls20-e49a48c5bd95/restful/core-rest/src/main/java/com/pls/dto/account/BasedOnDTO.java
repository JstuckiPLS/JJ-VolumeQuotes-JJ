package com.pls.dto.account;

/**
 * DTO for account filter Based On property.
 * 
 * @author Aleksandr Leshchenko
 */
public enum BasedOnDTO {
    PICKUP_DATE("PICKUP DATE"),
    BOOKED_DATE("BOOKED DATE"),
    DELIVERY_DATE("DELIVERY DATE");

    private String label;

    BasedOnDTO(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Retrieves enum value by its label.
     * 
     * @param label - key
     * @return - respective enum value
     */
    public static BasedOnDTO getValue(String label) {
        for (BasedOnDTO value : BasedOnDTO.values()) {
            if (value.getLabel().equals(label)) {
                return value;
            }
        }
        return null;
    }
}
