package com.pls.shipment.domain.bo;

import java.math.BigDecimal;

/**
 * Quote Guaranteed info.
 *
 * @author Mikhail Boldinov
 */
public class QuoteGuaranteedBO {

    private String timeOption;

    private BigDecimal priceOption;

    /**
     * Default constructor.
     */
    public QuoteGuaranteedBO() {
    }

    /**
     * Constructor.
     * 
     * @param timeOption time.
     * @param priceOption price.
     */
    public QuoteGuaranteedBO(String timeOption, BigDecimal priceOption) {
        this.timeOption = timeOption;
        this.priceOption = priceOption;
    }

    public String getTimeOption() {
        return timeOption;
    }

    public void setTimeOption(String timeOption) {
        this.timeOption = timeOption;
    }

    public BigDecimal getPriceOption() {
        return priceOption;
    }

    public void setPriceOption(BigDecimal priceOption) {
        this.priceOption = priceOption;
    }
}
