package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;

/**
 * DAO for {@link DocumentTypeEntity} entity.
 * 
 * @author Alexander Nalapko
 */
public interface DocumentTypeDao extends AbstractDao<DocumentTypeEntity, Long> {
    /**
     * Find document type entity by string representation of that type.
     *
     * @param docType string doc type to find
     * @param clazz class of document type
     * @param <T> type of document type entity
     * @return {@link DocumentTypeEntity} entity
     */
    <T extends DocumentTypeEntity> T findByDocTypeString(String docType, Class<T> clazz);

    /**
     * Find document types of specified subclass.
     *
     * @param clazz class of document type
     * @param <T> type of document type
     * @return list of document types of specified type
     */
    <T extends DocumentTypeEntity> List<T> find(Class<T> clazz);

    /**
     * Find document types of {@link LoadDocumentTypeEntity}.
     * 
     * @return list of document types of {@link LoadDocumentTypeEntity}
     */
    List<LoadDocumentTypeEntity> getLoadDocumentType();

}
