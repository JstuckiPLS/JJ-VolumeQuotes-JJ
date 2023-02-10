package com.pls.core.domain.bo.dashboard;

/**
 * This class is used for KPI SCAC filter.
 * 
 * @author Alexander Nalapko
 *
 */
public class ScacBO {

    /**
     * Constructor.
     * 
     * @param scac - the unique Id.
     * @param label - the label to be displayed.
     */
    public ScacBO(String scac, String label) {
        this.scac = scac;
        this.label = label;
    }
    /**
     * Constructor.
     * 
     * @param scac - the unique Id.
     */
    public ScacBO(String scac) {
        this.scac = scac;
    }

    @Override
    public String toString() {
        return this.scac;
    }

    private String scac;

    private String label;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
