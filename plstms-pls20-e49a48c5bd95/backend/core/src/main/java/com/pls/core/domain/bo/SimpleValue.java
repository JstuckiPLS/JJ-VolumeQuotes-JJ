package com.pls.core.domain.bo;

/**
 * This class is used for "LookupValue" fields.
 * 
 * @author Hima Bindu Challa
 *
 */
public class SimpleValue {

    /**
     * Constructor.
     */
    public SimpleValue() {
    }

    /**
     * Constructor.
     * 
     * @param id - the unique Id.
     * @param name - the label to be displayed.
     */
    public SimpleValue(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleValue)) {
            return false;
        }

        SimpleValue that = (SimpleValue) o;

        return id.equals(that.id) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
