package com.pls.dto.shipment;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO that represents results of shipment document upload.
 *
 * @author Denis Zhupinsky (Team International)
 */
@XmlRootElement
public class ShipmentDocUploadResultDTO {
    private boolean success;
    private boolean limitSizeExceeded;
    private Long tempDocId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTempDocId() {
        return tempDocId;
    }

    public void setTempDocId(Long tempDocId) {
        this.tempDocId = tempDocId;
    }

    public boolean isLimitSizeExceeded() {
        return limitSizeExceeded;
    }

    public void setLimitSizeExceeded(boolean limitSizeExceeded) {
        this.limitSizeExceeded = limitSizeExceeded;
    }
}
