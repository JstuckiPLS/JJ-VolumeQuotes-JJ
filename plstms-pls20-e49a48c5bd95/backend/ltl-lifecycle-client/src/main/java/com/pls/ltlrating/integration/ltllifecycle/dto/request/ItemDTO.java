package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import java.math.BigDecimal;

import com.pls.ltlrating.integration.ltllifecycle.dto.FreightClass;

import lombok.Data;

@Data
public abstract class ItemDTO {

    private FreightClass productClass;

    private String packageType;

    /** width in inches */
    private BigDecimal width;

    /** height in inches */
    private BigDecimal height;

    /** length in inches */
    private BigDecimal length;

    /** weight in LB */
    private BigDecimal weight;

    private Integer quantity;

    private Integer pieces;

    private String description;

    private Boolean stackable;
    
    private String nmfcItemCode;
    
    private String nmfcSubCode;

    private BigDecimal value;

}
