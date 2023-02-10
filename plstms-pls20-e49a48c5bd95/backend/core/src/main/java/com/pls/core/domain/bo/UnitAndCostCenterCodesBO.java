package com.pls.core.domain.bo;

/**
 * Business Object for Unit Code and Cost Center Code values.
 * 
 * @author Sergey Vovchuk
 * 
 */
public class UnitAndCostCenterCodesBO {

    private String unitCode;

    private String costCenterCode;

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

}
