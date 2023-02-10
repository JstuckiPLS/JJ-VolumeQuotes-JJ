package com.pls.ltlrating.service.impl;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
class LTLLCQuoteKey {
    private String scac;
    private String currency;
    private boolean isCsp;
    
    public LTLLCQuoteKey(String scac, String currency, boolean isCsp) {
        this.scac = scac;
        this.currency = currency;
        this.isCsp = isCsp;
    }
}