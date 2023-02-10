package com.pls.restful.shipment;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.dto.shipment.ShipmentDocumentDTO;
import com.pls.shipment.service.ShipmentDocumentService;

/**
 * Test class for {@link} ShipmentDocumentResource.
 * 
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentDocumentResourceTest extends BaseServiceITClass {
    @Mock
    private ShipmentDocumentService shipmentDocumentService;
    @Mock
    private DocumentService documentService;

    @InjectMocks
    private ShipmentDocumentResource sut;

    @Mock
    private DocumentResponseHelper documentResponseHelper;

    @Mock
    private ShipmentBuilderHelper shipmentBuilder;

    @Test
    public void testDeleteTmpDocument() throws EntityNotFoundException {
        Long documentId = new Long(1);

        sut.deleteTempDocument(documentId);

        verify(documentService).deleteTempDocument(documentId);
    }

    @Test
    public void testFindDocumentTypes() {
        sut.findDocumentTypes();
        verify(shipmentDocumentService).findDocumentTypes();
    }

    @Test
    public void testGetDocumentList() throws InvalidArgumentException, EntityNotFoundException {
        Long shipmentId = new Long(-1);
        when(shipmentBuilder.getShipmentDocumentDTOBuilder()).thenCallRealMethod();

        List<ShipmentDocumentDTO> documents = sut.getDocumentList(shipmentId);

        verify(shipmentDocumentService).getDocumentList(shipmentId);
        assertTrue(documents.isEmpty());
    }

    @Test
    public void shouldGetShipmentDocumentContentWithoutFileName() throws Exception {
        final long documentId = (long) (Math.random() * 100);
        final long streamLength = (long) (Math.random() * 100);
        final String docFileName = "docFileName" + Math.random();
        final MimeTypes fileType = MimeTypes.values()[(int) (Math.random() * (MimeTypes.values().length - 1))];

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletStream);

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocFileName(docFileName);
        document.setStreamLength(streamLength);
        document.setFileType(fileType);
        InputStream content = mock(InputStream.class);
        when(content.read((byte[]) Mockito.anyObject())).thenReturn(50, -1);
        document.setStreamContent(content);
        when(documentService.getDocumentWithStream(documentId)).thenReturn(document);
        when(documentResponseHelper.fillResponseWithDocument(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(HttpServletResponse.class),
                Mockito.any(LoadDocumentEntity.class))).thenCallRealMethod();

        sut.getDocumentContentById(documentId, "  ", false, response);

        verify(servletStream).write((byte[]) Mockito.anyObject(), Mockito.eq(0), Mockito.eq(50));
        Mockito.verify(response, Mockito.times(2)).getOutputStream();

        verify(response).setContentType(fileType.getMimeString());
        verify(response).addIntHeader("Content-Length", (int) streamLength);
        verify(response).setHeader("Content-Disposition", "filename=\"" + docFileName + "\"");
    }

    @Test
    public void shouldGetShipmentDocumentContentWithoutMimeTypeForDownloading() throws Exception {
        final long documentId = (long) (Math.random() * 100);
        final long streamLength = (long) (Math.random() * 100);
        final String docFileName = "docFileName" + Math.random();

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletStream);

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setStreamLength(streamLength);
        InputStream content = mock(InputStream.class);
        when(content.read((byte[]) Mockito.anyObject())).thenReturn(50, -1);
        document.setStreamContent(content);
        when(documentService.getDocumentWithStream(documentId)).thenReturn(document);
        when(documentResponseHelper.fillResponseWithDocument(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(HttpServletResponse.class),
                Mockito.any(LoadDocumentEntity.class))).thenCallRealMethod();

        sut.getDocumentContentById(documentId, docFileName, true, response);

        verify(servletStream).write((byte[]) Mockito.anyObject(), Mockito.eq(0), Mockito.eq(50));
        Mockito.verify(response, Mockito.times(2)).getOutputStream();

        verify(response).setContentType(MimeTypes.PDF.getMimeString());
        verify(response).addIntHeader("Content-Length", (int) streamLength);
        verify(response).setHeader("Content-Disposition", "attachment; filename=\"" + docFileName + "\"");
    }
}
