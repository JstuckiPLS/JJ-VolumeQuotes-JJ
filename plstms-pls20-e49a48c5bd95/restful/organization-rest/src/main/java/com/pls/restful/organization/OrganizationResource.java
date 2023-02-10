
package com.pls.restful.organization;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
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

import com.pls.core.common.MimeTypes;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.OrganizationService;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.ResponseVO;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.organization.OrgServiceDTO;
import com.pls.dtobuilder.organization.OrgServiceDTOBuilder;
import com.pls.extint.domain.ApiTypeEntity;
import com.pls.extint.service.ApiTypeService;

/**
 * Organization REST resource.
 * 
 * @author Aleksandr Nalapko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/organization")
public class OrganizationResource {

    private static final OrgServiceDTOBuilder ORG_SERVICE_DTO_BUILDER = new OrgServiceDTOBuilder();

    @Autowired
    private OrganizationService service;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ApiTypeService apiTypeService;

    @Value("/img/truck-hi.png")
    private ClassPathResource defaultCarrierLogo;

    @Value("/img/pls-logo.jpg")
    private ClassPathResource defaultCustomerLogo;

    /**
     * Find Customers by orgType and name for Customer Search Filters.
     * 
     * @param orgType
     *            organization type
     * @param name
     *            customer name
     * @param limit
     *            page size
     * @param offset
     *            pages
     * @return list of organizations
     */
    @RequestMapping(value = "/idNameTuples/{orgType}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleOrganizationEntity> getOrganizationsByNameAndType(@PathVariable("orgType") String orgType, @PathVariable("name") String name,
            @RequestParam(value = "limit", defaultValue = "100", required = false) Integer limit,
            @RequestParam(value = "offset", defaultValue = "0", required = false) Integer offset) {
        return service.getOrganizationsByNameAndType(orgType, name, limit, offset);
    }

    /**
     * Get carrier API details by selected Carrier.
     * 
     * @param organizationId
     *            - Org Id of the selected carrier
     * @return carrier API details for the selected carrier
     */
    @RequestMapping(value = "/getCarrierAPIDetails/{organizationId}", method = RequestMethod.GET)
    @ResponseBody
    public OrganizationAPIDetailsEntity getCarrierAPIDetailsByOrgId(@PathVariable("organizationId") Long organizationId) {
        return service.getCarrierAPIDetailsByOrgId(organizationId);
    }

    /**
     * Get logo for specified organization.
     *
     * @param orgId    organization id
     * @param customer <code>true</code> if customer logo, otherwise <code>false</code>.
     *                 If logo is not available for specified organization id, the default customer or carrier logo will be used
     * @param response {@link HttpServletResponse}
     * @throws EntityNotFoundException Image was not found.
     * @throws DocumentReadException   Logo cannot be read
     * @throws IOException             when can't write file content to output stream
     */
    @RequestMapping(value = "/{organizationId}/logo", method = RequestMethod.GET)
    @ResponseBody
    public void getImageByOrganizationId(@PathVariable("organizationId") Long orgId,
                                         @RequestParam(value = "customer", defaultValue = "false", required = false) Boolean customer,
                                         HttpServletResponse response)
            throws EntityNotFoundException, DocumentReadException, IOException {
        Long logoId = service.getImageByOrganizationId(orgId);
        if (logoId == null) {
            InputStream defaultLogo = customer ? defaultCustomerLogo.getInputStream() : defaultCarrierLogo.getInputStream();
            response.setContentType(MimeTypes.PNG.getMimeString());
            response.setHeader("Content-Disposition", "filename=\"default.png\"");
            IOUtils.copy(defaultLogo, response.getOutputStream());
        } else {
            LoadDocumentEntity doc = documentService.getDocumentWithStream(logoId);
            response.setContentType(ObjectUtils.defaultIfNull(doc.getFileType(), MimeTypes.PNG).getMimeString());
            response.addIntHeader("Content-Length", (int) doc.getStreamLength());
            response.setHeader("Content-Disposition", "filename=\"" + doc.getDocFileName() + "\"");
            IOUtils.copy(doc.getStreamContent(), response.getOutputStream());
        }
    }

    /**
     * Updates Organization API Details.
     * 
     * @param apiDetails
     *            api details object ro update
     * @throws ValidationException
     *             exception
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/updateCarrierAPIDetails", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateCarrierAPIDetails(@RequestBody OrganizationAPIDetailsEntity apiDetails) throws ValidationException {
        service.saveCarrierAPIDetails(apiDetails);
    }

    /**
     * Checks whether the organization logo is available or not.
     *
     * @param orgId organization id
     * @return <code>true</code> if organization logo exists, otherwise <code>false</code>
     */
    @RequestMapping(value = "/{organizationId}/isLogoAvailable", method = RequestMethod.GET)
    @ResponseBody
    public Boolean isOrganizationLogoAvailable(@PathVariable("organizationId") Long orgId) {
        Long logoId = service.getImageByOrganizationId(orgId);
        return logoId != null;
    }

    /**
     * Upload new logo for organization and returns uploaded logo id.
     *
     * @param logo path for organization logo
     * @return id of uploaded logo document
     * @throws IOException           @see MultipartStream#getInputStream() for more information.
     * @throws DocumentSaveException if logo cannot be uploaded
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/logo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO uploadForOrganizationLogo(@RequestParam("logo") MultipartFile logo) throws IOException, DocumentSaveException {
        String extension = FilenameUtils.getExtension(logo.getOriginalFilename());
        MimeTypes mimeType = ObjectUtils.defaultIfNull(MimeTypes.getByName(extension), MimeTypes.PNG);
        LoadDocumentEntity document = documentService.saveTempDocument(logo.getBytes(), DocumentTypes.TEMP.getDbValue(), mimeType);
        return new ResponseVO(document.getId());
    }

    /**
     * Get organization logo by id.
     *
     * @param documentId Not <code>null</code> ID of logo document.
     * @param response   servlet response
     * @throws IOException             when can't write file content to output stream
     * @throws EntityNotFoundException when document not found
     * @throws DocumentReadException   when document cannot be read
     */
    @RequestMapping(value = "/logo/{documentId}", method = {RequestMethod.GET, RequestMethod.HEAD})
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void getLogoById(@PathVariable("documentId") Long documentId, HttpServletResponse response)
            throws DocumentReadException, EntityNotFoundException, IOException {
        LoadDocumentEntity doc = documentService.getDocumentWithStream(documentId);
        response.setContentType(ObjectUtils.defaultIfNull(doc.getFileType(), MimeTypes.PDF).getMimeString());
        response.addIntHeader("Content-Length", (int) doc.getStreamLength());
        response.setHeader("Content-Disposition", "filename=\"" + doc.getDocFileName() + "\"");
        IOUtils.copy(doc.getStreamContent(), response.getOutputStream());
    }

    /**
     * Update logo for organization by id.
     * 
     * @param orgId
     *            organization id
     * @param logo
     *            path for organization logo
     * @exception EntityNotFoundException
     *                if organization with <id> not found
     * @exception IOException @see MultipartStream#getInputStream() for more information.
     *
     * @throws DocumentSaveException if logo cannot be updated
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/{organizationId}/logo", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateLogoForOrganization(@PathVariable("organizationId") Long orgId,
            @RequestParam("logo") MultipartFile logo) throws EntityNotFoundException, IOException, DocumentSaveException {
        InputStream inputStream = null;
        try {
            inputStream = logo.getInputStream();
            LoadDocumentEntity document = new LoadDocumentEntity();
            String extension = FilenameUtils.getExtension(logo.getOriginalFilename());
            MimeTypes mimeType = ObjectUtils.defaultIfNull(MimeTypes.getByName(extension), MimeTypes.PNG);
            document.setFileType(mimeType);
            document.setDocumentType(DocumentTypes.UNKNOWN.getDbValue());
            documentService.saveDocument(document, inputStream, "orgLogo-" + orgId);
            service.updateLogoForOrganization(orgId, document.getId());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Gets the org services for the selected organization.
     * 
     * @param orgId
     *            id of the selected organization
     * @return the org services for selected organization.
     */
    @RequestMapping(value = "/orgServices/get/{orgId}", method = RequestMethod.GET)
    @ResponseBody
    public OrgServiceDTO getOrgServices(@PathVariable("orgId") Long orgId) {
        return ORG_SERVICE_DTO_BUILDER.buildDTO(service.getServicesByOrgId(orgId));
    }

    /**
     * Create/Updates Org services for the selected organization.
     * 
     * @param dto
     *            Organization service details to save to db.
     * @return the saved org services details.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/orgServices/save", method = RequestMethod.POST)
    @ResponseBody
    public OrgServiceDTO saveOrgServices(@RequestBody OrgServiceDTO dto) {
        OrgServiceEntity entity = ORG_SERVICE_DTO_BUILDER.buildEntity(dto);
        return ORG_SERVICE_DTO_BUILDER.buildDTO(service.saveOrgServices(entity));
    }

    /**
     * Get the api type for the selected category and organization.
     * 
     * @param orgId
     *            id of the selected organization
     * @param category
     *            category for which the api type has to be returned
     * @return the api types for the selected category and organization
     */
    @RequestMapping(value = "/apiType/get/{orgId}/{category}", method = RequestMethod.GET)
    @ResponseBody
    public List<ApiTypeEntity> getApiTypeByOrgId(@PathVariable("orgId") Long orgId, @PathVariable("category") String category) {
        return apiTypeService.getByCategory(orgId, null, category, "CARRIER");
    }

    /**
     * Saves the api type to the database.
     * 
     * @param apiType
     *            api type to be saved
     * @return the saved api type
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(value = "/apiType/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiTypeEntity saveApiType(@RequestBody ApiTypeEntity apiType) {
        return apiTypeService.save(apiType);
    }
}
