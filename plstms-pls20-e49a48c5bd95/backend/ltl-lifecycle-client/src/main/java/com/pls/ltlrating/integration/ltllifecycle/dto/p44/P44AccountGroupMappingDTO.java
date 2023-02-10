package com.pls.ltlrating.integration.ltllifecycle.dto.p44;

import java.util.List;

import lombok.Data;

/** Mapping of account group and used accounts within group (by a customer) */
@Data
public class P44AccountGroupMappingDTO {

    private String p44AccountGroupCode;
    private List<P44AccountDTO> p44Accounts;

}
