package com.pls.ltlrating.integration.ltllifecycle.dto.message;

public enum ShipmentStatus {

    UNKNOWN(null),
    IN_TRANSIT("IN_TRANSIT"),
    OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"),
    DELIVERED("DELIVERED"),
    CANCELED("CANCELED"),
    TIMEOUT(null);
    
    private String pls20Code;
    
    private ShipmentStatus(String pls20Code) {
        this.pls20Code = pls20Code;
    }
    
    public String getPls20Code() {
        return pls20Code;
    }
}
