package com.pls;

import java.util.List;

import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * DTO for EDI Setting.
 * 
 * @author Brichak Aleksandr
 */
public class EdiSettingsDTO {

    private List<ShipmentStatus> ediStatus;

    private List<EdiType> ediType;

    private boolean bolUnique;

    public List<ShipmentStatus> getEdiStatus() {
        return ediStatus;
    }

    public void setEdiStatus(List<ShipmentStatus> list) {
        this.ediStatus = list;
    }

    public List<EdiType> getEdiType() {
        return ediType;
    }

    public void setEdiType(List<EdiType> list) {
        this.ediType = list;
    }

    public boolean isBolUnique() {
        return bolUnique;
    }

    public void setBolUnique(boolean bolUnique) {
        this.bolUnique = bolUnique;
    }
}
