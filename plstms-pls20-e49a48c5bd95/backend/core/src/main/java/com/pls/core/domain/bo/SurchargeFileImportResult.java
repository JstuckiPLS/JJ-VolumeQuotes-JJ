package com.pls.core.domain.bo;

/**
 * Results of surcharge import.
 * 
 * @author Sergii Belodon
 *
 */
public class SurchargeFileImportResult {
    private boolean isSucess;
    private boolean incorrectHeader;
    private boolean incorrectData;
    public boolean isOk() {
        return isSucess;
    }
    public void setOk(boolean isOk) {
        this.isSucess = isOk;
    }
    public boolean isIncorrectHeader() {
        return incorrectHeader;
    }
    public void setIncorrectHeader(boolean incorrectHeader) {
        this.incorrectHeader = incorrectHeader;
    }
    public boolean isIncorrectData() {
        return incorrectData;
    }
    public void setIncorrectData(boolean incorrectData) {
        this.incorrectData = incorrectData;
    }



}
