package com.pls.dto.enums;

/**
 * Indicate state of saved quote:
 * <ul>
 * <li>normal (younger than 7 days old)</li>
 * <li>expired (older than 7 days old)</li>
 * <li>unavailable(younger than 7 days old, but carrier is not available any more or carrier cost is changed)</li>
 * </ul>
 * 
 * Normal resolution means that ShipmentDTO should be used on CreateQuote page of QuoteRater wizard.
 * 
 * @author Alexey Tarasyuk
 * @author Aleksandr Leshchenko
 */
public enum SavedQuoteExpirationResolution {
    NORMAL,
    EXPIRED,
    UNAVAILABLE
}
