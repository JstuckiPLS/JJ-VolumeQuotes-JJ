package com.pls.dtobuilder.organization;

import com.pls.core.domain.organization.CustomerEntity;
import com.pls.dto.organization.CustomerInfoDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for customer info.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class CustomerInfoDTOBuilder extends AbstractDTOBuilder<CustomerEntity, CustomerInfoDTO> {

    @Override
    public CustomerInfoDTO buildDTO(CustomerEntity entity) {
        CustomerInfoDTO customerInfo = new CustomerInfoDTO();
        customerInfo.setId(entity.getId());
        customerInfo.setName(entity.getName());
        customerInfo.setCustomerNetworkId(entity.getNetworkId());
        return customerInfo;
    }

    @Override
    public CustomerEntity buildEntity(CustomerInfoDTO dto) {
        return null;
    }
}
