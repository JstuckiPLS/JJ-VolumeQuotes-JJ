package com.pls.dtobuilder.address;

import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.dto.address.RequiredDocumentDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for conversion {@link RequiredDocumentEntity} to {link RequiredDocumentDTO} and vice versa.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class RequiredDocumentDTOBuilder extends AbstractDTOBuilder<RequiredDocumentEntity, RequiredDocumentDTO> {
    private final DataProvider dataProvider;

    /**
     * Default constructor.
     */
    public RequiredDocumentDTOBuilder() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param dataProvider provider of existing data
     */
    public RequiredDocumentDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public RequiredDocumentDTO buildDTO(RequiredDocumentEntity entity) {
        RequiredDocumentDTO dto = new RequiredDocumentDTO();
        dto.setCustomerRequestType(entity.getCustomerRequestType());
        DocumentTypeEntity documentType = entity.getDocumentType();
        dto.setDocumentType(documentType.getDocTypeString());
        dto.setDocumentTypeDescription(documentType.getDescription());

        dto.setId(entity.getId());

        return dto;
    }

    @Override
    public RequiredDocumentEntity buildEntity(RequiredDocumentDTO dto) {
        RequiredDocumentEntity entity = dataProvider.getDocumentById(dto.getId());
        if (entity == null) {
            entity = new RequiredDocumentEntity();
            entity.setDocumentType(dataProvider.getDocumentTypeByDocTypeString(dto.getDocumentType()));
        }

        entity.setCustomerRequestType(dto.getCustomerRequestType());

        return entity;
    }

    /**
     * Provider of existing data.
     */
    public interface DataProvider {
        /**
         * Get required document by id.
         *
         * @param id id of document
         * @return {@link RequiredDocumentEntity}
         */
        RequiredDocumentEntity getDocumentById(Long id);

        /**
         * Get document type entity by document type string representation.
         * @param documentType string representation of document type
         *
         * @return {@link LoadDocumentTypeEntity}
         */
        LoadDocumentTypeEntity getDocumentTypeByDocTypeString(String documentType);
    }
}
