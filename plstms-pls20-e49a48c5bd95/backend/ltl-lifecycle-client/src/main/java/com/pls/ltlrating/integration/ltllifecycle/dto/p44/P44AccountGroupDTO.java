package com.pls.ltlrating.integration.ltllifecycle.dto.p44;

import java.util.List;

import lombok.Data;

@Data
public class P44AccountGroupDTO {

    private String accountGroupName;
    private String accountGroupCode;
    private List<P44AccountDTO> accounts;

}
