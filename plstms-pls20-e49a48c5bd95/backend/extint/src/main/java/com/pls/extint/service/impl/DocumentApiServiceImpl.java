package com.pls.extint.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.itextpdf.text.DocumentException;
import com.pls.core.common.MimeTypes;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.documentmanagement.util.PdfUtil;
import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.domain.ApiMetadataEntity;
import com.pls.extint.domain.ApiTypeEntity;
import com.pls.extint.domain.enums.DataType;
import com.pls.extint.domain.enums.ResponseType;
import com.pls.extint.service.DocumentApiService;
import com.pls.extint.shared.DocumentRequestVO;
import com.pls.extint.shared.DocumentResponseVO;

/**
 * Implementation of DocumentApiService interface.
 *
 * @author Pavani Challa
 *
 */
@Service
@Transactional(readOnly = true)
public class DocumentApiServiceImpl extends ApiServiceImpl<DocumentResponseVO, DocumentRequestVO> implements DocumentApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentApiServiceImpl.class);

    @Autowired
    private LoadDocumentDao documentDao;

    @Autowired
    private DocumentService documentService;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void getDocuments(DocumentRequestVO requestVO) {
        List<ApiTypeEntity> apiTypes = apiTypeDao.findDocumentApiTypesForLoad(requestVO.getLoadId(), requestVO.getCarrierOrgId(),
                requestVO.getShipperOrgId(), CARRIER_ORG_TYPE);

        if (apiTypes != null && !apiTypes.isEmpty()) {
            for (ApiTypeEntity apiType : apiTypes) {
                // Get the API Type for the request. Api type provides the details of the request like the url to invoke, SOAP or REST etc along with
                // metadata for constructing the request object and parsing the response.
                requestVO.setApiType(apiType);

                // Process the request. This sends the request to the API and parses the response. When the response is parsed, load documents are
                // created and saved to file system.
                try {
                    processRequest(requestVO);
                } catch (ApplicationException ae) {
                    // Ignore the exception and continue with the next api call.
                    LOGGER.error("An error occurred while retrieving documents from API", ae);
                }
            }

            documentDao.updatePaperworkReceived(requestVO.getLoadId());
        }
    }

    @Override
    protected void setData(ApiMetadataEntity metadata, Object entity, String value, Long orgId) throws ApplicationException {
        String data = value;
        if (metadata.getDataType() == DataType.URL && !StringUtils.isEmpty(value)) {
            int urlStartIndex = value.indexOf("http");

            if (urlStartIndex > -1) {
                String[] strings = StringUtils.tokenizeToStringArray(value.substring(urlStartIndex), " \"\'");

                data = strings[0];
            }
        }
        super.setData(metadata, entity, data, orgId);
    }

    /**
     * Parse the response received from the web service. When reading the response using the metadata, make sure that the first pls field set is the
     * Document Type. This ensures that if there is already a load document object for the same document type, it will add the bytes as another page
     * for the same load document object. If no load document object exists for this document type, then one is created. After parsing the response
     * from web services, load documents are saved to file system.
     *
     * @param requestVO
     *            Object containing request data.
     * @param wsResponse
     *            response from the web service
     * @return the corresponding response object created from the web service response.
     * @throws ApplicationException
     *             thrown for any errors while parsing the response.
     */
    @Override
    protected DocumentResponseVO parseWsResponse(DocumentRequestVO requestVO, String wsResponse) throws ApplicationException {
        List<String> missingPaperwork = null;
        try {
            DocumentResponseVO responseVO = parseResponse(requestVO, wsResponse);

            // If there is an error message in the web service response, mark the request as "Errored" and do not proceed.
            if (!StringUtils.isEmpty(responseVO.getError())) {
                updateLogStatusErrored(requestVO.getRequestLog(), responseVO.getError(), wsResponse);
                return null;
            }

            //if (requestVO.getApiType().getApiType().equalsIgnoreCase("ALL")) {
                // Get missing paperwork for load.
                missingPaperwork = documentDao.getMissingPaperworkForLoad(requestVO.getLoadId());
            //}

            if (responseVO.getLoadDocuments() != null) {
                saveLoadDocuments(missingPaperwork, responseVO);
            }
            return responseVO;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }

    }

    private void saveLoadDocuments(List<String> missingPaperwork, DocumentResponseVO responseVO) throws DocumentSaveException {
        // Set the Path for all the documents and save them
        for (LoadDocumentEntity document : responseVO.getLoadDocuments()) {
            if (ArrayUtils.isNotEmpty(document.getContent()) && missingPaperwork != null && !missingPaperwork.isEmpty()
                    && missingPaperwork.contains(document.getDocumentType())) {
                documentService.saveDocument(document);
            }
        }
    }

    /**
     * Parses the response string received from the API and extracts the document from the response.
     *
     * @param requestVO
     *            request data sent to the API. It contains the response metadata needed to parse the response.
     * @param wsResponse
     *            response received from the API
     * @return the documents for the load
     * @throws Exception
     *             thrown when any exception occurs while parsing.
     */
    protected DocumentResponseVO parseResponse(DocumentRequestVO requestVO, String wsResponse) throws Exception {
        if (requestVO.getApiType().getResponseType() == ResponseType.XML) {
            return parseXmlResponse(requestVO, wsResponse);
        } else if (requestVO.getApiType().getResponseType() == ResponseType.BYTES) {
            return parseByteResponse(requestVO, wsResponse);
        }

        return null;
    }

    /**
     * Response received from web service is not xml but document bytes. Parse the metadata and set the other properties.
     *
     * @param requestVO
     *            request data sent to the API. It contains the response meatadata needed to parse the response.
     * @param wsResponse
     *            response received from the API
     * @return the documents for the load
     * @throws Exception
     *             thrown when any exception occurs while parsing.
     */
    private DocumentResponseVO parseByteResponse(DocumentRequestVO requestVO, String wsResponse) throws ApplicationException {
        DocumentResponseVO responseVO = new DocumentResponseVO(requestVO);

        if (requestVO.getApiType().getRespMetadata() != null) {
            List<byte[]> docsContentList = new ArrayList<byte[]>();
            for (ApiMetadataEntity metadata : requestVO.getApiType().getRespMetadata()) {
                // The pdf bytes always corresponds to single document. Hence we won't be checking for the multiple flag.
                // And the ApiFieldName must be empty for pdf response. The metadata for bytes response will not have any children. Hence set the
                // default value from metadata to pls fields and iterating through children is not needed.

                if (!StringUtils.isEmpty(metadata.getPlsFieldName())) {
                    if (metadata.getDataType() == DataType.URL && !StringUtils.isEmpty(metadata.getDefaultValue())) {
                        byte[] bytes = restHelper.getDocument(metadata.getDefaultValue());
                        if (bytes != null) {
                            docsContentList.add(bytes);
                        }
                    } else {
                        setData(metadata, responseVO, metadata.getDefaultValue(), responseVO.getOrgId());
                    }
                }
            }

            docsContentList.add(wsResponse.getBytes());

            try {
                byte[] docContent = PdfUtil.writePdfDocument(docsContentList, responseVO.getFileType());
                responseVO.setDocumentFormat(MimeTypes.PDF.name());
                responseVO.setContent(docContent);

            } catch (IOException ex) {
                throw new ApplicationException(ex.getMessage(), ex.getCause());
            } catch (DocumentException ex) {
                throw new ApplicationException(ex.getMessage(), ex.getCause());
            }

        }
        return responseVO;
    }

    /**
     * Response received from web service in xml format. Parse the nodes in xml and get the required data from the response.
     *
     * @param requestVO
     *            request data sent to the API. It contains the response metadata needed to parse the response.
     * @param wsResponse
     *            response received from the API
     * @return the documents for the load
     * @throws Exception
     *             thrown when any exception occurs while parsing.
     */
    private DocumentResponseVO parseXmlResponse(DocumentRequestVO requestVO, String wsResponse) throws Exception {
        DocumentResponseVO responseVO = new DocumentResponseVO(requestVO);
        super.parseXmlResponse(requestVO, responseVO, wsResponse);

        try {
            for (LoadDocumentEntity document : responseVO.getLoadDocuments()) {
                document.setContent(PdfUtil.writePdfDocument(document.getPages(), document.getFileType()));
                document.setFileType(MimeTypes.PDF);
            }
        } catch (IOException ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        } catch (DocumentException ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }

        return responseVO;
    }

    @Override
    protected void updateLogStatusProcessing(ApiLogEntity log, String responseMsg) {
        // The response for document requests may contain bytes for constructing the document. Hence the response is not saved for document requests.
        super.updateLogStatusProcessing(log, null);
    }
}
