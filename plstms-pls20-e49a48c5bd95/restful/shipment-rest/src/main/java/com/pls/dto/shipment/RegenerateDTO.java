package com.pls.dto.shipment;

/**
 * DTO for regenerating consignee invoice.
 * 
 * @author Sergii Belodon
 */
public class RegenerateDTO {

    private Long loadId;
    private Long markup;

    public Long getLoadId() {
        return loadId;
    }
    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }
    public Long getMarkup() {
        return markup;
    }
    public void setMarkup(Long markup) {
        this.markup = markup;
    }

}
