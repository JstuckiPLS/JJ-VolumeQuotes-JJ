package com.pls.shipment.domain.bo;

import com.pls.core.domain.Identifiable;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Class to hold load with adjustment entities. If Adjustment is present, then it should be used for financial
 * calculations.
 * 
 * @author Aleksandr Leshchenko
 */
public class LoadAdjustmentBO implements Identifiable<Long> {
    private static final long serialVersionUID = -7861432359411542722L;

    private LoadEntity load;
    private FinancialAccessorialsEntity adjustment;

    /**
     * Constructor. When load is used for financial calculations.
     * 
     * @param load
     *            to be used for financial calculations.
     */
    public LoadAdjustmentBO(LoadEntity load) {
        this.load = load;
    }

    /**
     * Constructor. When adjustment is used for financial calculations.
     * 
     * @param adjustment
     *            to be used for financial calculations.
     */
    public LoadAdjustmentBO(FinancialAccessorialsEntity adjustment) {
        this.adjustment = adjustment;
        this.load = adjustment.getLoad();
    }

    public LoadEntity getLoad() {
        return load;
    }

    public FinancialAccessorialsEntity getAdjustment() {
        return adjustment;
    }

    @Override
    public Long getId() {
        return null;
    }

    /**
     * This method should not be used.
     * 
     * @param id
     *            never used
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException();
    }
}
