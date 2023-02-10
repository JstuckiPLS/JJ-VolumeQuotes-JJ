package com.pls.dto.shipment;

import java.math.BigDecimal;

/**
 * Contains quote guaranteed option (delivery time and the price of the option).
 *
 * @author Sergey Kirichenko
 */
public class QuoteGuaranteedDTO {

    String timeOption;

    BigDecimal priceOption;

    /**
     * Default constructor.
     */
    public QuoteGuaranteedDTO() {
    }

    /**
     * Constructs object with predefined fields.
     *
     * @param timeOption is the predefined value for the time option
     * @param priceOption is the predefined value for the price option
     */
    public QuoteGuaranteedDTO(String timeOption, BigDecimal priceOption) {
        this.timeOption = timeOption;
        this.priceOption = priceOption;
    }

    public String getTimeOption() {
        return timeOption;
    }

    /**
     * Sets time option.
     *
     * @param timeOption the time option.
     */
    public void setTimeOption(String timeOption) {
        this.timeOption = timeOption;
    }

    public BigDecimal getPriceOption() {
        return priceOption;
    }

    /**
     * Sets price option.
     *
     * @param priceOption the price option.
     */
    public void setPriceOption(BigDecimal priceOption) {
        this.priceOption = priceOption;
    }
}
