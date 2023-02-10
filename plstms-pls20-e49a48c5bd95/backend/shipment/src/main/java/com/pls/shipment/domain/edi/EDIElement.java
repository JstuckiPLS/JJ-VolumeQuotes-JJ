package com.pls.shipment.domain.edi;

import com.pls.shipment.domain.enums.EDIElementErrorCode;

/**
 * EDI element value.
 *
 * @author Mikhail Boldinov, 23/07/14
 */
public class EDIElement {

    private String id;

    private String dictionaryId;

    private int index;

    private String value;

    private String errorMsg;

    private EDIElementErrorCode errorCode;

    /**
     * Constructor.
     *
     * @param id element id
     */
    public EDIElement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public EDIElementErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(EDIElementErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Method to identify whether the element was parsed successfully or not.
     *
     * @return <code>true</code> if no error occurs while parsing element, otherwise <code>false</code>
     */
    public boolean isValid() {
        return errorMsg == null && errorCode == null;
    }

    @Override
    public String toString() {
        return id;
    }
}
