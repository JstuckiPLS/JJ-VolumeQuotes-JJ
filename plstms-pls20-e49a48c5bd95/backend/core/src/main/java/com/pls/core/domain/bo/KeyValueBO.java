package com.pls.core.domain.bo;

/** Key Value BO.
 * 
 * @author Artem Arapov
 *
 */
public class KeyValueBO {

    private Long key;

    private String value;

    public Long getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
