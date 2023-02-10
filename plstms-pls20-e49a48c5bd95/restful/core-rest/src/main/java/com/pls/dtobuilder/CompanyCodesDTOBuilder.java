package com.pls.dtobuilder;

import com.pls.core.domain.organization.CompanyCodeEntity;
import com.pls.dto.CompanyCodeDTO;

/**
 * DTO Builder for {@link CompanyCodeEntity}.
 * 
 * @author Dmitry Nikolaenko
 */
public class CompanyCodesDTOBuilder extends AbstractDTOBuilder<CompanyCodeEntity, CompanyCodeDTO> {

    @Override
    public CompanyCodeDTO buildDTO(CompanyCodeEntity bo) {
        return new CompanyCodeDTO(bo.getId(), bo.getDescription());
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            {@link CompanyCodeDTO}
     * @return {@link CompanyCodeEntity}
     */
    @Override
    public CompanyCodeEntity buildEntity(CompanyCodeDTO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }
}