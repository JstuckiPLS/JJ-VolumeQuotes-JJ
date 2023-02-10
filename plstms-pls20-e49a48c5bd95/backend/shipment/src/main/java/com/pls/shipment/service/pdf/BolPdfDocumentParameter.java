package com.pls.shipment.service.pdf;

import java.io.InputStream;

import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.shipment.domain.LoadEntity;

/**
 * Implementation of {@link PdfDocumentParameter} for BOL document.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class BolPdfDocumentParameter implements PdfDocumentParameter {
    private LoadEntity load;
    private UserEntity currentUser;
    private boolean preview;
    private InputStream logo;
    private boolean hideShipmentCreatedTime;
    private boolean manualBolFlag;

    /**
     * Constructor.
     *
     * @param load load
     * @param currentUser currently logged in user
     * @param logo customer logo
     * @param preview if preview mode switched on
     * @param hideShipmentCreatedTime display time for shipment created by.
     */
    public BolPdfDocumentParameter(LoadEntity load, UserEntity currentUser,
            InputStream logo, boolean preview, boolean hideShipmentCreatedTime) {
        this.load = load;
        this.currentUser = currentUser;
        this.logo = logo;
        this.preview = preview;
        this.hideShipmentCreatedTime = hideShipmentCreatedTime;
    }

    /**
     * Constructor.
     *
     * @param load load
     * @param currentUser currently logged in user
     * @param logo customer logo
     * @param preview if preview mode switched on
     * @param hideShipmentCreatedTime display time for shipment created by.
     * @param manualBolFlag - flag which indicates that PDF BOL document is generated via ManualBol wizard.
     */
    public BolPdfDocumentParameter(LoadEntity load, UserEntity currentUser,
            InputStream logo, boolean preview, boolean hideShipmentCreatedTime, boolean manualBolFlag) {
        this.load = load;
        this.currentUser = currentUser;
        this.logo = logo;
        this.preview = preview;
        this.hideShipmentCreatedTime = hideShipmentCreatedTime;
        this.manualBolFlag = manualBolFlag;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public UserEntity getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserEntity currentUser) {
        this.currentUser = currentUser;
    }

    public InputStream getLogo() {
        return logo;
    }

    public void setLogo(InputStream logo) {
        this.logo = logo;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isHideShipmentCreatedTime() {
        return hideShipmentCreatedTime;
    }

    public void setHideShipmentCreatedTime(boolean hideShipmentCreatedTime) {
        this.hideShipmentCreatedTime = hideShipmentCreatedTime;
    }

    public boolean isManualBolFlag() {
        return manualBolFlag;
    }

    public void setManualBolFlag(boolean manualBolFlag) {
        this.manualBolFlag = manualBolFlag;
    }
}
