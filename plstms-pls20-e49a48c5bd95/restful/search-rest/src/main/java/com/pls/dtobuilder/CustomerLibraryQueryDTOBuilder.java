package com.pls.dtobuilder;

import com.pls.core.shared.Status;
import com.pls.dto.search.CustomerLibraryQueryDTO;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.search.domain.co.GetCustomerCO;

/**
 * Builder to convert {@link CustomerLibraryQueryDTO} into the {@link GetCustomerCO}.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryQueryDTOBuilder extends
        AbstractDTOBuilder<GetCustomerCO, CustomerLibraryQueryDTO> {

    @Override
    public CustomerLibraryQueryDTO buildDTO(GetCustomerCO bo) {
        throw new UnsupportedOperationException("Not supported method");
    }

    @Override
    public GetCustomerCO buildEntity(CustomerLibraryQueryDTO dto) {
        GetCustomerCO co = new GetCustomerCO();

        if (!dto.getFromDate().isEmpty()) {
            co.setFromDate(DateUtils.parseDateWithoutTimeZone(dto.getFromDate()));
        }
        if (!dto.getToDate().isEmpty()) {
            co.setToDate(DateUtils.parseDateWithoutTimeZone(dto.getToDate()));
        }
        if (!dto.getFromLoadDate().isEmpty()) {
            co.setFromLoadDate(DateUtils.parseDateWithoutTimeZone(dto.getFromLoadDate()));
        }
        if (!dto.getToLoadDate().isEmpty()) {
            co.setToLoadDate(DateUtils.parseDateWithoutTimeZone(dto.getToLoadDate()));
        }
        if (!dto.getPersonId().isEmpty()) {
            co.setPersonId(Long.decode(dto.getPersonId()));
        }
        if (!dto.getStatus().isEmpty()) {
            co.setStatus(Status.getStatusByValue(dto.getStatus()));
        }
        if (!dto.getName().isEmpty()) {
            co.setName(dto.getName());
        }
        return co;
    }

}
