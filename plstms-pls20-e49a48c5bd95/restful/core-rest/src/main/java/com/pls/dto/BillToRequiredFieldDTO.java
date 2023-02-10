package com.pls.dto;

import com.pls.core.domain.bo.AddressBO;

/**
 * The Class BillToRequiredFieldDTO.
 * 
 * @author Sergii Belodon
 */
public class BillToRequiredFieldDTO {
    private Long id;
    private String name;
    private Boolean required;
    private String defaultValue;
    private char inboundOutbound;
    private AddressBO address;
    private char originDestination;
    private String startWith;
    private String endWith;
    private String actionForDefaultValues;
    private String ruleExp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleExp() {
        return ruleExp;
    }

    public void setRuleExp(String ruleExp) {
        this.ruleExp = ruleExp;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public char getInboundOutbound() {
        return inboundOutbound;
    }

    public void setInboundOutbound(char inboundOutbound) {
        this.inboundOutbound = inboundOutbound;
    }

    public AddressBO getAddress() {
        return address;
    }

    public void setAddress(AddressBO address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public char getOriginDestination() {
        return originDestination;
    }

    public void setOriginDestination(char originDestination) {
        this.originDestination = originDestination;
    }

    public String getStartWith() {
        return startWith;
    }

    public void setStartWith(String startWith) {
        this.startWith = startWith;
    }

    public String getEndWith() {
        return endWith;
    }

    public void setEndWith(String endWith) {
        this.endWith = endWith;
    }

    public String getActionForDefaultValues() {
        return actionForDefaultValues;
    }

    public void setActionForDefaultValues(String actionForDefaultValues) {
        this.actionForDefaultValues = actionForDefaultValues;
    }

}
