package com.pls.documentmanagement.service.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.common.MimeTypes;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;

/**
 * Test cases for {@link RequiredDocumentServiceImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceImplTest {

    public static final long TEMP_DOC_ID = 1L;

    public static final long DOC_ID = 2L;

    public static final long DOC_ID_NOT_EXIST = -1L;

    public static final long LOAD_ID = 1L;

    @Mock
    private LoadDocumentDao documentDao;

    @Mock
    private DocFileNamesResolver docFileNamesResolver;

    @InjectMocks
    private DocumentServiceImpl sut;

    @Test
    public void testReadDocument() throws EntityNotFoundException, IOException, DocumentReadException {
        File tempFile = File.createTempFile("temp", "tmp");
        FileUtils.writeStringToFile(tempFile, "TEST");
        assertTrue(tempFile.exists());

        String location = tempFile.getParent();
        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);

        byte[] bytes = sut.readDocument(location, tempFile.getName());
        verify(docFileNamesResolver).buildDirectPath(location);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        tempFile.deleteOnExit();
    }

    @Test
    public void testReadDocumentEntity() throws EntityNotFoundException, IOException, DocumentReadException {
        File tempFile = File.createTempFile("temp", "tmp");
        FileUtils.writeStringToFile(tempFile, "TEST");
        assertTrue(tempFile.exists());

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setId(DOC_ID);
        document.setDocumentPath(location);
        document.setDocFileName(fileName);

        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);

        byte[] bytes = sut.readDocument(document);
        verify(docFileNamesResolver).buildDirectPath(location);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        tempFile.deleteOnExit();
    }

    @Test
    public void testLoadDocument() throws EntityNotFoundException, IOException, DocumentReadException {
        File tempFile = File.createTempFile("temp", "tmp");
        FileUtils.writeStringToFile(tempFile, "TEST");
        assertTrue(tempFile.exists());

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setId(DOC_ID);
        document.setDocumentPath(location);
        document.setDocFileName(fileName);

        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);
        when(documentDao.get(DOC_ID)).thenReturn(document);

        LoadDocumentEntity actualDocument = sut.loadDocument(DOC_ID);

        verify(docFileNamesResolver).buildDirectPath(location);
        verify(documentDao).get(DOC_ID);

        assertEquals(document, actualDocument);
        byte[] content = actualDocument.getContent();
        assertNotNull(content);
        assertTrue(content.length > 0);

        tempFile.deleteOnExit();
    }

    @Test(expected = EntityNotFoundException.class)
    public void testLoadDocumentNotFound() throws EntityNotFoundException, DocumentReadException {
        doThrow(EntityNotFoundException.class).when(documentDao).get(DOC_ID_NOT_EXIST);
        sut.loadDocument(DOC_ID_NOT_EXIST);
        verify(documentDao).get(DOC_ID_NOT_EXIST);
    }

    @Test
    public void testSaveTempDocument() throws EntityNotFoundException, IOException, DocumentSaveException {
        File tempFile = File.createTempFile("temp", "tmp");
        assertTrue(tempFile.exists());

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        String docType = DocumentTypes.BOL.getDbValue();
        MimeTypes mimeType = MimeTypes.PDF;

        when(docFileNamesResolver.buildTempDocumentPath(docType)).thenReturn(location);
        when(docFileNamesResolver.buildTempFileName(docType, mimeType)).thenReturn(fileName);
        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);

        byte[] content = "TEST DATA".getBytes();

        sut.saveTempDocument(content, docType, mimeType);

        verify(docFileNamesResolver).buildTempDocumentPath(docType);
        verify(docFileNamesResolver).buildTempFileName(docType, mimeType);
        verify(docFileNamesResolver).buildDirectPath(location);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).saveOrUpdate(argumentCaptor.capture());

        LoadDocumentEntity actualDocument = argumentCaptor.getValue();

        assertNotNull(actualDocument);
        assertEquals(location, actualDocument.getDocumentPath());
        assertEquals(fileName, actualDocument.getDocFileName());
        assertEquals(DocumentTypes.TEMP.getDbValue(), actualDocument.getDocumentType());
        assertEquals(mimeType, actualDocument.getFileType());
        byte[] actualContent = actualDocument.getContent();
        assertNotNull(actualContent);
        assertTrue(actualContent.length > 0);

        byte[] fileContent = FileUtils.readFileToByteArray(tempFile);
        assertArrayEquals(content, actualContent);
        assertArrayEquals(actualContent, fileContent);

        tempFile.deleteOnExit();
    }

    @Test
    public void testSaveDocument() throws IOException, DocumentSaveException {
        File tempFile = File.createTempFile("temp", "tmp");
        assertTrue(tempFile.exists());

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        byte[] expectedContent = "TEST DATA".getBytes();
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setContent(expectedContent);
        document.setDocumentType(DocumentTypes.BOL.getDbValue());

        when(docFileNamesResolver.buildDocumentPath(document)).thenReturn(location);
        when(docFileNamesResolver.buildDocumentFileName(document)).thenReturn(fileName);
        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);

        sut.saveDocument(document);

        verify(docFileNamesResolver).buildDocumentPath(document);
        verify(docFileNamesResolver).buildDocumentFileName(document);
        verify(docFileNamesResolver).buildDirectPath(location);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).saveOrUpdate(argumentCaptor.capture());

        LoadDocumentEntity actualDocument = argumentCaptor.getValue();

        assertNotNull(actualDocument);
        assertEquals(location, actualDocument.getDocumentPath());
        assertEquals(fileName, actualDocument.getDocFileName());
        byte[] actualContent = actualDocument.getContent();
        assertNotNull(actualContent);
        assertTrue(actualContent.length > 0);

        byte[] fileContent = FileUtils.readFileToByteArray(tempFile);
        assertArrayEquals(expectedContent, actualContent);
        assertArrayEquals(actualContent, fileContent);

        tempFile.deleteOnExit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveDocumentDocTypeNotSpecified() throws IOException, DocumentSaveException {
        byte[] expectedContent = "TEST DATA".getBytes();
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setContent(expectedContent);

        sut.saveDocument(document);
    }

    @Test
    public void testSaveDocumentInputStream() throws IOException, DocumentSaveException {
        File tempFile = File.createTempFile("temp", "tmp");
        assertTrue(tempFile.exists());

        File inputFile = File.createTempFile("tempInput", "tmp");
        assertTrue(inputFile.exists());

        byte[] expectedContent = "TEST DATA".getBytes();
        FileUtils.writeByteArrayToFile(inputFile, expectedContent);

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentType(DocumentTypes.BOL.getDbValue());

        when(docFileNamesResolver.buildDocumentPath(document)).thenReturn(location);
        when(docFileNamesResolver.buildDocumentFileName(document)).thenReturn(fileName);
        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);

        sut.saveDocument(document, new FileInputStream(inputFile), null);

        verify(docFileNamesResolver).buildDocumentPath(document);
        verify(docFileNamesResolver).buildDocumentFileName(document);
        verify(docFileNamesResolver).buildDirectPath(location);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).saveOrUpdate(argumentCaptor.capture());

        LoadDocumentEntity actualDocument = argumentCaptor.getValue();

        assertNotNull(actualDocument);
        assertEquals(location, actualDocument.getDocumentPath());
        assertEquals(fileName, actualDocument.getDocFileName());

        byte[] fileContent = FileUtils.readFileToByteArray(tempFile);
        assertArrayEquals(expectedContent, fileContent);

        tempFile.deleteOnExit();
        inputFile.deleteOnExit();
    }

    @Test
    public void testSaveDocumentInputStreamPrefix() throws IOException, DocumentSaveException {
        File tempFile = File.createTempFile("temp", "tmp");
        assertTrue(tempFile.exists());

        File inputFile = File.createTempFile("tempInput", "tmp");
        assertTrue(inputFile.exists());

        String prefix = "test prefix";
        byte[] expectedContent = "TEST DATA".getBytes();
        FileUtils.writeByteArrayToFile(inputFile, expectedContent);

        String location = tempFile.getParent();
        String fileName = tempFile.getName();

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentType(DocumentTypes.BOL.getDbValue());

        when(docFileNamesResolver.buildDocumentFileName(prefix, document.getFileType())).thenReturn(fileName);
        when(docFileNamesResolver.buildDirectPath(prefix)).thenReturn(location);

        sut.saveDocument(document, new FileInputStream(inputFile), prefix);

        verify(docFileNamesResolver, never()).buildDocumentPath(document);
        verify(docFileNamesResolver, never()).buildDocumentFileName(document);
        verify(docFileNamesResolver).buildDocumentFileName(prefix, document.getFileType());
        verify(docFileNamesResolver).buildDirectPath(prefix);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).saveOrUpdate(argumentCaptor.capture());

        LoadDocumentEntity actualDocument = argumentCaptor.getValue();

        assertNotNull(actualDocument);
        assertEquals(prefix, actualDocument.getDocumentPath());
        assertEquals(fileName, actualDocument.getDocFileName());

        byte[] fileContent = FileUtils.readFileToByteArray(tempFile);
        assertArrayEquals(expectedContent, fileContent);

        tempFile.deleteOnExit();
        inputFile.deleteOnExit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveDocumentInputStreamIllegalArgument() throws IOException, DocumentSaveException {
        File inputFile = File.createTempFile("tempInput", "tmp");
        assertTrue(inputFile.exists());

        String prefix = null;
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentType(null);

        inputFile.deleteOnExit();
        sut.saveDocument(document, new FileInputStream(inputFile), prefix);
    }

    @Test
    public void testDeleteTempDocument() throws EntityNotFoundException, IOException {
        File parentDir = new File(FileUtils.getTempDirectoryPath(), "temp" + System.nanoTime());
        parentDir.mkdirs();
        File tempFile = File.createTempFile("temp", "tmp", parentDir);
        assertTrue(tempFile.exists());
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setId(TEMP_DOC_ID);
        document.setDocumentPath(tempFile.getParent());
        document.setDocFileName(tempFile.getName());
        when(documentDao.get(TEMP_DOC_ID)).thenReturn(document);
        when(docFileNamesResolver.buildDirectPath(tempFile.getParent())).thenReturn(tempFile.getParent());

        sut.deleteTempDocument(TEMP_DOC_ID);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).deleteTempDocument(argumentCaptor.capture());
        LoadDocumentEntity actual = argumentCaptor.getValue();
        assertEquals(document, actual);

        assertFalse(tempFile.exists());
    }

    @Test
    public void testDeleteStaleTempDocuments() throws EntityNotFoundException, IOException {
        File parentDir = new File(FileUtils.getTempDirectoryPath(), "temp" + System.nanoTime());
        parentDir.mkdirs();
        File tempFile = File.createTempFile("temp", "tmp", parentDir);
        assertTrue(tempFile.exists());
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setId(TEMP_DOC_ID);
        document.setDocumentPath(tempFile.getParent());
        document.setDocFileName(tempFile.getName());


        List<LoadDocumentEntity> list = new ArrayList<LoadDocumentEntity>();
        list.add(document);
        when(documentDao.findTempDocumentsOlderThanSpecifiedDate(any(Date.class))).thenReturn(list);
        when(docFileNamesResolver.buildDirectPath(tempFile.getParent())).thenReturn(tempFile.getParent());

        sut.deleteStaleTempDocuments(2);

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).findTempDocumentsOlderThanSpecifiedDate(any(Date.class));
        verify(documentDao).deleteTempDocument(argumentCaptor.capture());
        LoadDocumentEntity actual = argumentCaptor.getValue();
        assertEquals(document, actual);

        assertFalse(tempFile.exists());
    }

    @Test
    public void testMoveAndSaveTempDocPermanently() throws IOException, DocumentSaveException, EntityNotFoundException {
        File dstFile = new File(FileUtils.getTempDirectoryPath(), "tempDst" + System.nanoTime() + "tmp");

        File srcFile = File.createTempFile("tempSrc", "tmp");
        assertTrue(srcFile.exists());

        String docType = DocumentTypes.BOL.getDbValue();

        byte[] expectedContent = "TEST DATA".getBytes();
        FileUtils.writeByteArrayToFile(srcFile, expectedContent);

        String location = srcFile.getParent();
        String fileName = srcFile.getName();

        String dstLocation = dstFile.getParent();
        String dstFileName = dstFile.getName();

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentPath(location);
        document.setDocFileName(fileName);

        when(documentDao.get(TEMP_DOC_ID)).thenReturn(document);

        when(docFileNamesResolver.buildDocumentPath(document)).thenReturn(dstLocation);
        when(docFileNamesResolver.buildDocumentFileName(document)).thenReturn(dstFileName);

        when(docFileNamesResolver.buildDirectPath(location)).thenReturn(location);
        when(docFileNamesResolver.buildDirectPath(dstLocation)).thenReturn(dstLocation);

        sut.moveAndSaveTempDocPermanently(TEMP_DOC_ID, LOAD_ID, docType);

        verify(documentDao).get(TEMP_DOC_ID);

        verify(docFileNamesResolver).buildDocumentPath(document);
        verify(docFileNamesResolver).buildDocumentFileName(document);
        verify(docFileNamesResolver, times(2)).buildDirectPath(anyString());

        ArgumentCaptor<LoadDocumentEntity> argumentCaptor = ArgumentCaptor.forClass(LoadDocumentEntity.class);
        verify(documentDao).saveOrUpdate(argumentCaptor.capture());

        LoadDocumentEntity actualDocument = argumentCaptor.getValue();

        assertNotNull(actualDocument);
        assertEquals(dstLocation, actualDocument.getDocumentPath());
        assertEquals(dstFileName, actualDocument.getDocFileName());
        assertEquals(docType, actualDocument.getDocumentType());

        byte[] fileContent = FileUtils.readFileToByteArray(dstFile);
        assertArrayEquals(expectedContent, fileContent);

        dstFile.deleteOnExit();
        srcFile.deleteOnExit();
    }

    @Test
    public void testDeleteShipmentDocuments() {
        List<Long> ids = Collections.singletonList(DOC_ID);
        sut.deleteDocuments(ids);

        verify(documentDao).deleteDocuments(ids);
    }
}
