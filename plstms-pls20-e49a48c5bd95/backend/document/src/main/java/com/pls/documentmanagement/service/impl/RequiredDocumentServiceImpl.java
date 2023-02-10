package com.pls.documentmanagement.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.DocRequestType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.RequiredDocumentDao;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.documentmanagement.service.RequiredDocumentService;

/**
 * Implementation of {@link RequiredDocumentService}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
public class RequiredDocumentServiceImpl implements RequiredDocumentService {

    private static final ImmutableList<String> DEFAULT_REQUIRED_DOCUMENTS = ImmutableList.of("POD");

    private static final long DUMMY_BOL_NUMBER = -1;

    @Autowired
    private RequiredDocumentDao requiredDocumentDao;

    @Override
    public RequiredDocumentEntity getRequiredDocument(Long id) {
        return requiredDocumentDao.find(id);
    }

    @Override
    public void saveRequiredDocuments(Collection<RequiredDocumentEntity> requiredDocuments, Long billToId) {
        Iterator<RequiredDocumentEntity> iterator = requiredDocuments.iterator();
        while (iterator.hasNext()) {
            RequiredDocumentEntity requiredDocument = iterator.next();
            if (requiredDocument.getCustomerRequestType() == null) {
                iterator.remove();
                continue;
            }
            BillToEntity billTO = new BillToEntity();
            billTO.setId(billToId);
            requiredDocument.setBillTo(billTO);
            requiredDocument.setStatus(Status.ACTIVE);
        }
        requiredDocumentDao.saveOrUpdateBatch(requiredDocuments);
    }

    @Override
    public List<RequiredDocumentEntity> getRequiredDocumentsOfShipmentTypes(long billToId) {
        List<RequiredDocumentEntity> requiredDocuments = new ArrayList<RequiredDocumentEntity>();
        Map<LoadDocumentTypeEntity, RequiredDocumentEntity> map = requiredDocumentDao.getDocumentsForShipmentTypes(billToId);
        for (Map.Entry<LoadDocumentTypeEntity, RequiredDocumentEntity> entry : map.entrySet()) {

            RequiredDocumentEntity requiredDocument = entry.getValue();
            if (requiredDocument == null) {
                requiredDocument = new RequiredDocumentEntity();
            }

            requiredDocument.setDocumentType(entry.getKey());
            if (billToId == DUMMY_BOL_NUMBER) {
                requiredDocument = applyDefaultRequiredDocuments(requiredDocument);
            }

            requiredDocuments.add(requiredDocument);
        }
        Collections.sort(requiredDocuments, new Comparator<RequiredDocumentEntity>() {
            @Override
            public int compare(RequiredDocumentEntity o1, RequiredDocumentEntity o2) {
                String docType1 = o1.getDocumentType() != null ? StringUtils.defaultString(o1.getDocumentType().getDescription()) : "";
                String docType2 = o2.getDocumentType() != null ? StringUtils.defaultString(o2.getDocumentType().getDescription()) : "";
                return docType1.compareToIgnoreCase(docType2);
            }
        });
        return requiredDocuments;
    }

    private RequiredDocumentEntity applyDefaultRequiredDocuments(RequiredDocumentEntity document) {
        if (DEFAULT_REQUIRED_DOCUMENTS.contains(document.getDocumentType().getDocTypeString())) {
            document.setCustomerRequestType(DocRequestType.REQUIRED);
        }

        return document;
    }
}
