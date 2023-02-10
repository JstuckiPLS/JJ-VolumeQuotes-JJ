package com.pls.ltlrating.integration.ltllifecycle.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat
public enum FreightClass {
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

    private String val;

    private FreightClass(String val) {
        this.val = val;
    }
    
    @JsonValue
    public String getValue() {
        return val;
    }

    @JsonCreator
    public static FreightClass fromValue(String value) {
        for(FreightClass fc:FreightClass.values()) {
            if(fc.val.equals(value)) return fc;
        }
        
        throw new IllegalArgumentException("There is no such freight class "+value);
    }

}
