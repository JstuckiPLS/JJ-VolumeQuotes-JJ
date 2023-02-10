package com.pls.restful.shipment;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Helper to fill response by document.
 * 
 * @author Sergii Belodon
 */
@Controller
public class DocumentResponseHelper {
    /**
     * Fill response by document.
     * 
     * @param doc
     *            Not <code>null</code> ID of document.
     * @param fileName
     *            optional name of file. If not specified - {@link LoadDocumentEntity#getDocFileName} will be used.
     * @param download
     *            if <code>true</code> then Content-Disposition will be set to "attachment" and browser will try to download file.
     * @param response
     *            servlet response
     * @throws IOException
     *             when can't write file content to output stream
     * @throws IOException
     *             when document not found
     * @return true - workaround for Mockito
     */
    public boolean fillResponseWithDocument(String fileName, boolean download, HttpServletResponse response, LoadDocumentEntity doc)
            throws IOException {
        response.setContentType(ObjectUtils.defaultIfNull(doc.getFileType(), MimeTypes.PDF).getMimeString());
        response.addIntHeader("Content-Length", (int) doc.getStreamLength());
        String fileNameForUser = StringUtils.isBlank(fileName) ? doc.getDocFileName() : fileName;
        if (download) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameForUser + "\"");
        } else {
            response.setHeader("Content-Disposition", "filename=\"" + fileNameForUser + "\"");
        }

        if (doc.getStreamContent() != null) {
            IOUtils.copy(doc.getStreamContent(), response.getOutputStream());
            IOUtils.closeQuietly(response.getOutputStream());
            IOUtils.closeQuietly(doc.getStreamContent());
        }
        return true;
    }
}
