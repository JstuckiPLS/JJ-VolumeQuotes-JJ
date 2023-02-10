package com.pls.restful.shipment;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.OrganizationService;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.service.DocumentService;

/**
 * RESTful resource to obtain customer documents.
 * 
 * @author Sergii Belodon
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customerdocs")
public class CustomerDocumentResource {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentResponseHelper documentResponseHelper;

    @Autowired
    private OrganizationService organizationService;

    /**
     * Get content for document by id.
     * 
     * @param documentId
     *            Not <code>null</code> ID of document.
     * @param fileName
     *            optional name of file. If not specified - {@link LoadDocumentEntity#getDocFileName} will be used.
     * @param response
     *            servlet response
     * @param downloadToken
     *            token for download file without authorization
     * @throws IOException
     *             when can't write file content to output stream
     * @throws EntityNotFoundException
     *             when document not found
     * @throws DocumentReadException
     *             when document cannot be read
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
    // RequestMethod.HEAD is required for IE when displaying document inline
    public void getDocumentContentById(@RequestParam("id") Long documentId, @PathParam("fileName") String fileName,
            HttpServletResponse response, @RequestParam("token") String downloadToken)
                    throws IOException, EntityNotFoundException, DocumentReadException {

        LoadDocumentEntity doc = documentService.getDocumentWithStream(documentId, downloadToken);

        documentResponseHelper.fillResponseWithDocument(fileName, false, response, doc);
    }

    /**
     * Returns Customer Logo by specified id.
     * 
     * @param customerId
     *            Not <code>null</code> Customer id.
     * @param response
     *            Servlet response
     * @throws EntityNotFoundException
     *            when document not found
     * @throws DocumentReadException
     *            when document cannot be read
     * @throws IOException
     *            when can't write file content to output stream
     */
    @RequestMapping(value = "/{customerId}/logo", method = RequestMethod.GET)
    public void getCustomerLogo(@PathVariable("customerId") Long customerId, HttpServletResponse response)
            throws EntityNotFoundException, DocumentReadException, IOException {
        Long logoId = organizationService.getImageByOrganizationId(customerId);

        if (logoId != null) {
            LoadDocumentEntity doc = documentService.getDocumentWithStream(logoId);
            response.setContentType(ObjectUtils.defaultIfNull(doc.getFileType(), MimeTypes.PNG).getMimeString());
            response.addIntHeader("Content-Length", (int) doc.getStreamLength());
            response.setHeader("Content-Disposition", "filename=\"" + doc.getDocFileName() + "\"");
            IOUtils.copy(doc.getStreamContent(), response.getOutputStream());
        }
    }

    /**
     * Get content for document by id.
     * 
     * @param documentId
     *            Not <code>null</code> ID of document.
     * @param fileName
     *            optional name of file. If not specified - {@link LoadDocumentEntity#getDocFileName} will be used.
     * @param download
     *            if <code>true</code> then Content-Disposition will be set to "attachment" and browser will try to download file.
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
    public void getDocumentById(@PathVariable("documentId") Long documentId, @PathParam("fileName") String fileName,
            @PathParam("download") boolean download, HttpServletResponse response) throws IOException, EntityNotFoundException,
            DocumentReadException {
        LoadDocumentEntity doc = documentService.getDocumentWithStream(documentId);

        documentResponseHelper.fillResponseWithDocument(fileName, download, response, doc);
    }

}
