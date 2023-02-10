package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuoteItemDTO extends ItemDTO {

    private boolean isHazmat;

}
