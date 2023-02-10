package com.pls.ltlrating.domain.bo;

import java.io.Serializable;

/**
 * Business object that is used to save the copy data from one profile detail to another.
 *
 * @author Pavani Challa
 *
 */
public class CopyFromCO implements Serializable {

    private static final long serialVersionUID = 1856154263524512458L;

    private Long copyFromId;

    private Long copyToId;

    public Long getCopyFromId() {
        return copyFromId;
    }

    public void setCopyFromId(Long copyFromId) {
        this.copyFromId = copyFromId;
    }

    public Long getCopyToId() {
        return copyToId;
    }

    public void setCopyToId(Long copyToId) {
        this.copyToId = copyToId;
    }
}