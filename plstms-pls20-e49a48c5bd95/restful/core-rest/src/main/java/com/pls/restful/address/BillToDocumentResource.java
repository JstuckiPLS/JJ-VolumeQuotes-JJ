package com.pls.restful.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;
import com.pls.documentmanagement.service.RequiredDocumentService;
import com.pls.dto.address.RequiredDocumentDTO;
import com.pls.dtobuilder.address.RequiredDocumentDTOBuilder;

/**
 * BillTo document resource.
 * 
 * @author Dmitriy Nefedchenko
 */
@Controller
@RequestMapping("/customer/{customerId}/billTo/{billToId}")
public class BillToDocumentResource {
    private final RequiredDocumentDTOBuilder requiredDocumentDTOBuilder =
            new RequiredDocumentDTOBuilder(new RequiredDocumentDTOBuilder.DataProvider() {
                @Override
                public RequiredDocumentEntity getDocumentById(Long id) {
                    return documentService.getRequiredDocument(id);
                }

                @Override
                public LoadDocumentTypeEntity getDocumentTypeByDocTypeString(String documentType) {
                    return null;
                }
            });

    @Autowired
    private RequiredDocumentService documentService;

    /**
     * Retrieves list of required documents for billTo.
     * 
     * @param billToId - billTo identifier
     * @return list of required documents
     */
    @Transactional(readOnly = true)
    @ResponseBody
    @RequestMapping("/requiredDocuments")
    public List<RequiredDocumentDTO> getRequiredDocuments(@PathVariable("billToId") Long billToId) {
        List<RequiredDocumentEntity> documents = documentService.getRequiredDocumentsOfShipmentTypes(billToId);
        return requiredDocumentDTOBuilder.buildList(documents);
    }

}
