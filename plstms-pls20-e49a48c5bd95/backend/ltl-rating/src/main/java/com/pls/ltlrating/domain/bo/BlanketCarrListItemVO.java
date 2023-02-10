package com.pls.ltlrating.domain.bo;

import java.io.Serializable;

/**
 * Business object that is used to hold the list of the unblocked active blanket carrier information.
 *
 * @author Ashwini Neelgund
 *
 */
public class BlanketCarrListItemVO implements Serializable {

    private static final long serialVersionUID = -5356464507124219501L;

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
}
