package com.pls.shipment.shared;

import java.io.Serializable;

import com.pls.shipment.domain.LoadEntity;

/**
 * VO for sending data back to controller after load is saved.
 * 
 * @author Pavani Challa
 * 
 */
public class SaveLoadVO implements Serializable {

    private static final long serialVersionUID = 3542341612845462817L;

    private LoadEntity load;

    private boolean ediDispatch;

    /**
     * Default Constructor.
     */
    public SaveLoadVO() {
        // Do Nothing.
    }

    /**
     * Constructor with params.
     * 
     * @param load
     *            load created/updated by save operation
     * @param ediDispatch
     *            flag to identify if the pickup edi is dispatched
     */
    public SaveLoadVO(LoadEntity load, boolean ediDispatch) {
        this.load = load;
        this.ediDispatch = ediDispatch;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public boolean isEdiDispatch() {
        return ediDispatch;
    }

    public void setEdiDispatch(boolean ediDispatch) {
        this.ediDispatch = ediDispatch;
    }

}
