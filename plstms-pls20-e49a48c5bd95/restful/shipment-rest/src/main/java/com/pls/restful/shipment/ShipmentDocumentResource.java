package com.pls.restful.shipment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.itextpdf.text.DocumentException;
import com.pls.core.common.MimeTypes;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.exception.FileSizeLimitException;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.ValueLabelDTO;
import com.pls.dto.shipment.RegenerateDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentDocUploadResultDTO;
import com.pls.dto.shipment.ShipmentDocumentDTO;
import com.pls.extint.service.DocumentApiService;
import com.pls.extint.shared.DocumentRequestVO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.restful.util.ShipmentProposalUtils;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.service.AccessorialTypeService;
import com.pls.shipment.service.ManualBolDocumentService;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.pdf.Printable;

/**
 * RESTful resource to obtain shipment order documents.
 * 
 * @author Maxim Medvedev
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/shipmentdocs")
public class ShipmentDocumentResource {

    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Autowired
    private ManualBolDocumentService manualBolDocumentService;

    @Autowired
    private DocumentApiService apiDocumentService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("TermsOfAgreement.pdf")
    private ClassPathResource termsOfAgreementResource;

    @Autowired
    private DocumentResponseHelper documentResponseHelper;

    @Autowired
    private AccessorialTypeService accessorialTypeService;

    @Autowired
    private ShipmentSavingService shipmentSavingService;

    @Autowired
    private ShipmentBuilderHelper shipmentBuilder;

    /**
     * Delete shipment document by document id.
     * 
     * @param docId
     *            id of document
     * @throws EntityNotFoundException
     *             if temp document doesn't found
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/deleteTemp/{documentId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTempDocument(@PathVariable("documentId") long docId) throws EntityNotFoundException {
        documentService.deleteTempDocument(docId);
    }

    /**
     * Delete permanent shipment document by document id.
     * 
     * @param docId
     *            id of document
     * @throws EntityNotFoundException
     *             if temp document doesn't found
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/delete/{documentId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteDocumentPermanently(@PathVariable("documentId") long docId) throws EntityNotFoundException {
        documentService.deleteDocuments(Collections.singletonList(docId));
    }

    /**
     * Find shipment related document types.
     * 
     * @return list of document types
     */
    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    @ResponseBody
    public List<ValueLabelDTO> findDocumentTypes() {
        List<LoadDocumentTypeEntity> documentTypes = shipmentDocumentService.findDocumentTypes();
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>(documentTypes.size());
        for (LoadDocumentTypeEntity documentType : documentTypes) {
            result.add(new ValueLabelDTO(documentType.getDocTypeString(), documentType.getDescription()));
        }

        return result;
    }

    /**
     * Get brief information about shipment order documents.
     * 
     * @param shipmentId
     *            Not <code>null</code> ID of shipment order.
     * @return Not <code>null</code> {@link List}.
     * @throws InvalidArgumentException
     *             when null argument is passed.
     * @throws EntityNotFoundException
     *             when document not found.
     * 
     */
    @RequestMapping(value = "/{shipmentId}/list", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentDocumentDTO> getDocumentList(@PathVariable("shipmentId") Long shipmentId)
            throws InvalidArgumentException, EntityNotFoundException {
        return shipmentBuilder.getShipmentDocumentDTOBuilder().buildList(shipmentDocumentService.getDocumentList(shipmentId));
    }

    /**
     * Upload document for shipment.
     *
     * @param upload
     *            - File.
     * @param loadId
     *            - load Id.
     * @param docType
     *            - type of the document.
     * @return json string representation of upload results dto.
     * @throws javax.xml.bind.JAXBException
     *             if marshalling of object fails
     * @throws com.pls.core.exception.fileimport.InvalidFormatException
     *             if was received file that is of unsupported type
     * @throws org.apache.commons.fileupload.FileUploadException
     *             if upload fails
     * @throws EntityNotFoundException
     *             if entity is not found
     * @throws IOException
     *             if processing of received stream fails
     * @throws DocumentSaveException
     *             if document cannot be saved
     * @throws DocumentException
     *             if PDF generation failed
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{loadId}/saveDoc", method = RequestMethod.POST,
                    consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String uploadShipmentDocs(@RequestParam("upload") MultipartFile upload, @PathVariable("loadId") Long loadId,
            @RequestParam("docType") String docType) throws JAXBException, InvalidFormatException, FileUploadException, IOException,
            DocumentSaveException, DocumentException, EntityNotFoundException {
        ShipmentDocUploadResultDTO result = new ShipmentDocUploadResultDTO();

        if (upload != null && !upload.isEmpty()) {
            LoadDocumentEntity doc;
            try {
                doc = shipmentDocumentService.saveTemporaryDoc(upload);
                documentService.moveAndSaveTempDocPermanently(doc.getId(), loadId, docType);
                result.setTempDocId(doc.getId());
                result.setSuccess(true);
            } catch (FileSizeLimitException e) {
                result.setLimitSizeExceeded(true);
            } catch (Exception e) {
                result.setSuccess(false);
            }
        }

        return objectMapper.writeValueAsString(result);
    }

    /**
     * Get brief information about Manual Bol order documents.
     * 
     * @param manualBolId
     *            Not <code>null</code> ID of Manual Bol.
     * @return Not <code>null</code> {@link List}.
     * @throws InvalidArgumentException
     *             when null argument is passed.
     * @throws EntityNotFoundException
     *             when document not found.
     * 
     */
    @RequestMapping(value = "/manualBol/{manualBolId}/list", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentDocumentDTO> getManualBolDocumentList(@PathVariable("manualBolId") Long manualBolId)
            throws InvalidArgumentException, EntityNotFoundException {
        return shipmentBuilder.getShipmentDocumentDTOBuilder().buildList(manualBolDocumentService.getDocumentsList(manualBolId));
    }

    /**
     * Returns true if there are any pending documents to download from carrier website (API must be
     * configured).
     * 
     * @param shipmentId
     *            Not <code>null</code> ID of shipment order.
     * @return list of document types
     */
    @RequestMapping(value = "/{shipmentId}/canDownload", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO canDownload(@PathVariable("shipmentId") Long shipmentId) {
        List<ShipmentMissingPaperworkBO> shipments = shipmentService.getShipmentsWithMissingReqPaperwork(shipmentId);
        return new ResponseVO(shipments != null && !shipments.isEmpty());
    }

    /**
     * Downloads the documents from carrier website using api and returns the brief information about shipment
     * order documents.
     * 
     * @param shipmentId
     *            Not <code>null</code> ID of shipment order.
     * @return Not <code>null</code> {@link List}.
     * @throws InvalidArgumentException
     *             when null argument is passed.
     * @throws EntityNotFoundException
     *             when document not found.
     * 
     */
    @RequestMapping(value = "/{shipmentId}/download", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentDocumentDTO> downloadDocumentsFromApi(@PathVariable("shipmentId") Long shipmentId)
            throws InvalidArgumentException, EntityNotFoundException {
        List<ShipmentMissingPaperworkBO> shipments = shipmentService.getShipmentsWithMissingReqPaperwork(shipmentId);
        if (shipments != null && !shipments.isEmpty()) {
            // There should be only one item in the list.
            apiDocumentService.getDocuments(buildDocumentRequest(shipments.get(0)));
        }
        return shipmentBuilder.getShipmentDocumentDTOBuilder().buildList(shipmentDocumentService.getDocumentList(shipmentId));
    }

    private DocumentRequestVO buildDocumentRequest(ShipmentMissingPaperworkBO shipment) {
        DocumentRequestVO request = new DocumentRequestVO();
        request.setLoadId(shipment.getLoadId());
        request.setCarrierOrgId(shipment.getCarrierOrgId());
        request.setShipperOrgId(shipment.getShipperOrgId());
        request.setCarrierScac(shipment.getCarrierScac());
        request.setBol(shipment.getBol());
        request.setCarrierRefNum(shipment.getCarrierRefNum());
        request.setShipperRefNum(shipment.getShipperRefNum());
        request.setOriginZip(shipment.getOriginZip());
        request.setDestZip(shipment.getDestZip());
        request.setPickupDate(shipment.getPickupDate());

        return request;
    }

    /**
     *
     * Temporary upload document for shipment.
     *
     * @param upload
     *            File.
     * @return json string representation of upload results dto.
     * @throws javax.xml.bind.JAXBException
     *             if marshalling of object fails
     * @throws com.pls.core.exception.fileimport.InvalidFormatException
     *             if was received file that is of unsupported type
     * @throws org.apache.commons.fileupload.FileUploadException
     *             if upload fails
     * @throws IOException
     *             if processing of received stream fails
     * @throws DocumentSaveException
     *             if document cannot be saved
     * @throws DocumentException
     *             if PDF generation failed
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/temp", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                    produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String uploadTempDocs(@RequestParam("upload") MultipartFile upload)
            throws JAXBException, InvalidFormatException, FileUploadException, IOException, DocumentSaveException, DocumentException {
        ShipmentDocUploadResultDTO result = new ShipmentDocUploadResultDTO();

        if (upload != null && !upload.isEmpty()) {
            LoadDocumentEntity tempDoc;
            try {
                tempDoc = shipmentDocumentService.saveTemporaryDoc(upload);
                result.setTempDocId(tempDoc.getId());
                result.setSuccess(true);
            } catch (FileSizeLimitException e) {
                result.setLimitSizeExceeded(true);
            }
        }

        return objectMapper.writeValueAsString(result);
    }

    /**
     * Get content for document by id.
     * 
     * @param documentId
     *            Not <code>null</code> ID of document.
     * @param fileName
     *            optional name of file. If not specified - {@link LoadDocumentEntity#getDocFileName} will be
     *            used.
     * @param download
     *            if <code>true</code> then Content-Disposition will be set to "attachment" and browser will
     *            try to download file.
     * @param response
     *            servlet response
     * @throws IOException
     *             when can't write file content to output stream
     * @throws EntityNotFoundException
     *             when document not found
     * @throws DocumentReadException
     *             when document cannot be read
     */
    @RequestMapping(value = "/{documentId}", method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void getDocumentContentById(@PathVariable("documentId") Long documentId, @PathParam("fileName") String fileName,
            @PathParam("download") boolean download, HttpServletResponse response)
            throws IOException, EntityNotFoundException, DocumentReadException {

        LoadDocumentEntity doc = documentService.getDocumentWithStream(documentId);

        documentResponseHelper.fillResponseWithDocument(fileName, download, response, doc);
    }

    /**
     * Get Terms Of Agreement PDF file for preview.
     * 
     * @param response
     *            servlet response
     * @throws IOException
     *             when can't write PDF to output stream
     */
    @RequestMapping(value = "/termOfAgreement", method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void getTermsOfAgreementPDF(HttpServletResponse response) throws IOException {
        response.setContentType(MimeTypes.PDF.getMimeString());
        IOUtils.copy(termsOfAgreementResource.getInputStream(), response.getOutputStream());
    }

    /**
     * Generates and stores in the database documents that are needed based on shipment data .
     *
     * @param dto
     *            shipment to be stored in the session
     * @param types
     *            document types for which documents need to be generated
     * @param rateQuote
     *            should be <code>true</code> for rate quote wizard. <code>false</code> otherwise.
     * @param hideCreatedTime
     *            display shipment created time.
     * @param isManualBol
     *            shows if docs are regenerated from Manual Bol wizard.
     * @param printType
     *            print type for shipping label document
     * @return {@link ValueLabelDTO} object with value filled with id of entity where BOL data is stored.
     * @throws PDFGenerationException
     *             when BOL generation fails.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/tempDoc/{type}", method = RequestMethod.POST)
    @ResponseBody
    public List<ValueLabelDTO> prepareTemporaryShipmentDocs(@RequestBody ShipmentDTO dto, @PathVariable("type") DocumentTypes[] types,
            @QueryParam("rateQuote") Boolean rateQuote,
            @RequestParam(value = "hideCreatedTime", required = false, defaultValue = "false") Boolean hideCreatedTime,
            @RequestParam(value = "isManualBol", required = false, defaultValue = "false") Boolean isManualBol,
            @RequestParam(value = "printType", required = false) Integer printType) throws PDFGenerationException {
        Long loadId = dto.getId();
        dto.setId(null); // do not remove this setId method because otherwise load will be updated in database
                         // when transaction committed.
        dto.setPrepaidDetails(null); // do not remove this method, otherwise you won't be able to Regenerate
                                     // BOL.(caused by previous line).
        LoadEntity load = shipmentBuilder.getShipmentDTOBuilder().buildEntity(dto);

        ShipmentProposalBO proposal = dto.getSelectedProposition();
        proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, dto.getGuaranteedBy());

        shipmentSavingService.updateLoadWithQuoteNumber(load, proposal);
        shipmentSavingService.updateLoadWithCostDetails(load, proposal, SecurityUtils.getCurrentPersonId());
        if (rateQuote == null || !rateQuote) {
            shipmentSavingService.fillAccessorialsFromCostDetailItems(load, proposal);
        } else {
            @SuppressWarnings("unchecked")
            Collection<AccessorialTypeEntity> accessorials = (Collection<AccessorialTypeEntity>) CollectionUtils.collect(load.getLtlAccessorials(),
                    new Transformer() {
                        @Override
                        public Object transform(Object input) {
                            return ((LtlLoadAccessorialEntity) input).getAccessorial();
                        }
                    });
            accessorialTypeService.refreshAccessorials(accessorials);
        }

        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();

        List<DocumentTypes> typesList = Arrays.asList(types);
        if (typesList.contains(DocumentTypes.BOL)) {
            Long bolDocId = shipmentDocumentService.generateAndStoreTempBol(load, dto.getOrganizationId(), hideCreatedTime, isManualBol);
            result.add(new ValueLabelDTO(bolDocId, DocumentTypes.BOL.getDbValue()));
        }

        if (typesList.contains(DocumentTypes.SHIPPING_LABELS)) {
            Long shLabelsDocId =
                    shipmentDocumentService.generateAndStoreTempShippingLabels(load, loadId, dto.getOrganizationId(),
                            Printable.getPrintable(Objects.firstNonNull(printType, 0)));
            result.add(new ValueLabelDTO(shLabelsDocId, DocumentTypes.SHIPPING_LABELS.getDbValue()));
        }

        regenerateConsigneeInvoice(load, result, typesList);
        return result;
    }
	
	/*@Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/bolTest", method = RequestMethod.POST)
    public void bolTesting(@RequestBody ShipmentDTO dto, @PathVariable("type") DocumentTypes[] types,
            @QueryParam("rateQuote") Boolean rateQuote,
            @RequestParam(value = "hideCreatedTime", required = false, defaultValue = "false") Boolean hideCreatedTime,
            @RequestParam(value = "isManualBol", required = false, defaultValue = "false") Boolean isManualBol,
            @RequestParam(value = "printType", required = false) Integer printType) throws PDFGenerationException {
        Long loadId = dto.getId();
        dto.setId(null); // do not remove this setId method because otherwise load will be updated in database
                         // when transaction committed.
        dto.setPrepaidDetails(null); // do not remove this method, otherwise you won't be able to Regenerate
                                     // BOL.(caused by previous line).
        LoadEntity load = shipmentBuilder.getShipmentDTOBuilder().buildEntity(dto);

        ShipmentProposalBO proposal = dto.getSelectedProposition();
        proposal = ShipmentProposalUtils.filterGuaranteedOptions(proposal, dto.getGuaranteedBy());

        shipmentSavingService.updateLoadWithCostDetails(load, proposal, SecurityUtils.getCurrentPersonId());
        if (rateQuote == null || !rateQuote) {
            shipmentSavingService.fillAccessorialsFromCostDetailItems(load, proposal);
        } else {
            @SuppressWarnings("unchecked")
            Collection<AccessorialTypeEntity> accessorials = (Collection<AccessorialTypeEntity>) CollectionUtils.collect(load.getLtlAccessorials(),
                    new Transformer() {
                        @Override
                        public Object transform(Object input) {
                           
                        }
                    });
            accessorialTypeService.refreshAccessorials(accessorials);
        }

        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();

        List<DocumentTypes> typesList = Arrays.asList(types);
        if (typesList.contains(DocumentTypes.BOL)) {
            Long bolDocId = shipmentDocumentService.generateAndStoreTempBol(load, dto.getOrganizationId(), hideCreatedTime, isManualBol);
        }
    }*/

    private void regenerateConsigneeInvoice(LoadEntity load, List<ValueLabelDTO> result, List<DocumentTypes> typesList)
            throws PDFGenerationException {
        if (typesList.contains(DocumentTypes.CONSIGNEE_INVOICE)) {
            Long consigneeInvId = shipmentDocumentService.generateAndStoreTempConsigneeInvoice(load);
            result.add(new ValueLabelDTO(consigneeInvId, DocumentTypes.CONSIGNEE_INVOICE.getDbValue()));
        }
    }

    /**
     * Regenerate shipment docs.
     * 
     * @param regenerateDTO
     *            -DTO for regenerating
     * @param types
     *            document type to regenerate
     * @param hideCreatedTime
     *            the hide created time
     * @return the list
     * @throws PDFGenerationException
     *             the PDF generation exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/regenerate/{type}", method = RequestMethod.POST)
    @ResponseBody
    public List<ValueLabelDTO> regenerateShipmentDocs(@RequestBody RegenerateDTO regenerateDTO, @PathVariable("type") DocumentTypes[] types,
            @RequestParam(value = "hideCreatedTime", required = false, defaultValue = "false") Boolean hideCreatedTime)
            throws PDFGenerationException {
        List<ValueLabelDTO> result = new ArrayList<ValueLabelDTO>();
        List<DocumentTypes> typesList = Arrays.asList(types);
        if (typesList.contains(DocumentTypes.CONSIGNEE_INVOICE)) {
            Map<DocumentTypes, Long> resultMap = shipmentService.regenerateDocsForShipment(regenerateDTO.getLoadId(), regenerateDTO.getMarkup(),
                    hideCreatedTime);
            for (Map.Entry<DocumentTypes, Long> entry : resultMap.entrySet()) {
                if (typesList.contains(entry.getKey())) {
                    result.add(new ValueLabelDTO(entry.getValue(), entry.getKey().getDbValue()));
                }
            }
        }
        return result;
    }

}
