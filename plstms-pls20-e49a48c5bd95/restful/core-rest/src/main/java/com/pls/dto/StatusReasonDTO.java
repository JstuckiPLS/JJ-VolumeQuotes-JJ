package com.pls.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Status Reason DTO.
 * 
 * @author Aleksandr Leshchenko
 */
@XmlRootElement
public class StatusReasonDTO {
    private String status;
    private String reason;
    private String reasonText;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }
}
