package com.pls.dto;

import java.io.Serializable;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * DTO Object for LTL carrier liabilities.
 * 
 * @author Pavani Challa
 * 
 */
public class LtlCarrierLiabilitiesDTO implements Serializable {

    private static final long serialVersionUID = -7267503218980278121L;

    private String freightClassCode;

    private LtlCarrierLiabilitiesEntity liability;

    /**
     * Constructor for LtlCarrierLiabilitiesDTO.
     * 
     * @param freightClass
     *            the commodity class code
     * @param liability
     *            liability for that commodity class
     */
    public LtlCarrierLiabilitiesDTO(String freightClass, LtlCarrierLiabilitiesEntity liability) {
        this.freightClassCode = freightClass;
        this.liability = liability;

        if (this.freightClassCode != null && this.liability.getFreightClass() == null) {
            this.liability.setFreightClass(CommodityClass.convertFromDbCode(this.freightClassCode));
        }
    }

    public String getFreightClassCode() {
        return freightClassCode;
    }

    public void setFreightClassCode(String freightClassCode) {
        this.freightClassCode = freightClassCode;
    }

    public LtlCarrierLiabilitiesEntity getLiability() {
        return liability;
    }

    public void setLiability(LtlCarrierLiabilitiesEntity liability) {
        this.liability = liability;
    }
}
