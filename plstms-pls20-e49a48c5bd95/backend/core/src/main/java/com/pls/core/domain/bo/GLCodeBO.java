package com.pls.core.domain.bo;


/** GL number component BO.
 * 
 * @author Sergey Vovchuk
 *
 */
public class GLCodeBO {

    private String group;

    private String description;

    private String value;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}