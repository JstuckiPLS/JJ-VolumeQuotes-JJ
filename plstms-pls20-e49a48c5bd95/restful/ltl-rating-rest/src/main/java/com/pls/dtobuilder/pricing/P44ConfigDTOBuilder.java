package com.pls.dtobuilder.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.dto.P44ConfigDTO;
import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountGroupMappingDTO;

@Component
public class P44ConfigDTOBuilder {
    
    @Autowired
    private CustomerDao customerDao;

    public P44ConfigDTO buildP44ConfigDTO(Long customerId, P44AccountGroupMappingDTO accountGroupMapping) {
        
        P44ConfigDTO configDto = new P44ConfigDTO();
        if (accountGroupMapping != null) {
            configDto.setAccountGroupCode(accountGroupMapping.getP44AccountGroupCode());
            configDto.setAccounts(accountGroupMapping.getP44Accounts());
        }
        
        CustomerEntity customer = customerDao.find(customerId);
        configDto.setCustomerName(customer.getName());
        
        return configDto;
    }
    
    public P44AccountGroupMappingDTO buildP44AccountGroupMappingDTO(P44ConfigDTO p44Config) {
        
        P44AccountGroupMappingDTO accountGroupMapping = new P44AccountGroupMappingDTO();
        accountGroupMapping.setP44AccountGroupCode(p44Config.getAccountGroupCode());
        accountGroupMapping.setP44Accounts(p44Config.getAccounts());
        
        return accountGroupMapping;
    }
}
