package com.pls.shipment.domain.bo;

import com.pls.core.domain.bo.proposal.CarrierDTO;

/**
 * DTO for adjustments.
 * 
 * @author Aleksandr Leshchenko
 */
public class AdjustmentLoadInfoBO {
    private Long billToId;
    private CarrierDTO carrier;
    private String bolNumber;
    private String poNumber;
    private String refNumber;
    private String soNumber;

    public Long getBillToId() {
        return billToId;
    }

    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }

    public CarrierDTO getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierDTO carrier) {
        this.carrier = carrier;
    }

    public String getBolNumber() {
        return bolNumber;
    }

    public void setBolNumber(String bolNumber) {
        this.bolNumber = bolNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }
}
