package com.pls.shipment.service.pdf;

import java.io.InputStream;

import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.shipment.domain.LoadEntity;

/**
 * Implementation of {@link PdfDocumentParameter} for shipping labels.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class ShippingLabelsDocumentParameter implements PdfDocumentParameter {
    private LoadEntity load;
    private Long loadId;
    private InputStream logo;
    private Printable printType;

    /**
     * Constructor.
     *
     * @param load load
     * @param loadId load id
     * @param logo customer logo
     * @param printType format for printing document
     */
    public ShippingLabelsDocumentParameter(LoadEntity load, Long loadId, InputStream logo, Printable printType) {
        this.load = load;
        this.loadId = loadId;
        this.logo = logo;
        this.printType = printType;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public InputStream getLogo() {
        return logo;
    }

    public void setLogo(InputStream logo) {
        this.logo = logo;
    }

    public Printable getPrintType() {
        return printType;
    }

    public void setPrintType(Printable printType) {
        this.printType = printType;
    }
}
