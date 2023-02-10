package com.pls.documentmanagement.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.pls.core.common.MimeTypes;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentReadException;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.service.DocFileNamesResolver;
import com.pls.documentmanagement.service.DocumentService;
import com.pls.documentmanagement.shared.DocumentSplitCO;
import com.pls.documentmanagement.util.FileOperationsUtils;
import com.pls.documentmanagement.util.PdfUtil;

/**
 * Implementation of {@link DocumentService}.
 *
 * @author Pavani Challa
 * @author Denis Zhupinsky (Team International)
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private static final String FILE_SEPARATOR = File.separator;

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocFileNamesResolver docFileNamesResolver;

    @Autowired
    private LoadDocumentDao documentDao;

    @Override
    public LoadDocumentEntity loadDocument(Long id) throws DocumentReadException, EntityNotFoundException {
        LoadDocumentEntity document = documentDao.get(id);
        document.setContent(readDocument(document.getDocumentPath(), document.getDocFileName()));

        return document;
    }

    @Override
    public LoadDocumentEntity loadDocumentWithoutContent(Long id) throws EntityNotFoundException {
        return documentDao.get(id);
    }

    @Override
    public File getDocumentFile(LoadDocumentEntity document) throws IOException {
        return new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());
    }

    @Override
    public InputStream getDocumentInputStream(LoadDocumentEntity document) throws IOException {
        return new FileInputStream(getDocumentFile(document));
    }

    @Override
    public byte[] readDocument(LoadDocumentEntity document) throws DocumentReadException {
        return readDocument(document.getDocumentPath(), document.getDocFileName());
    }

    @Override
    public byte[] readDocument(String location, String fileName) throws DocumentReadException {
        File file = new File(docFileNamesResolver.buildDirectPath(location), fileName);
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            LOG.error("Cannot read document", e);
            throw new DocumentReadException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public LoadDocumentEntity saveTempDocument(byte[] content, String docType, MimeTypes mimeType) throws DocumentSaveException {
        String path = docFileNamesResolver.buildTempDocumentPath(docType);
        String fileName = docFileNamesResolver.buildTempFileName(docType, mimeType);

        LoadDocumentEntity loadDocument = new LoadDocumentEntity();
        loadDocument.setContent(content);

        loadDocument.setDocumentPath(path);
        loadDocument.setDocFileName(fileName);
        loadDocument.setDocumentType(DocumentTypes.TEMP.getDbValue());
        loadDocument.setFileType(mimeType);

        documentDao.saveOrUpdate(loadDocument);

        try {
            FileOperationsUtils.writeToFileSystem(docFileNamesResolver.buildDirectPath(path), fileName, loadDocument.getContent());
        } catch (Exception e) {
            deleteTempDocument(loadDocument);
            throw new DocumentSaveException("Temp document cannot be saved", e);
        }

        return loadDocument;
    }

    @Override
    public LoadDocumentEntity savePreparedDocument(LoadDocumentEntity loadDocument) {
        return documentDao.saveOrUpdate(loadDocument);
    }

    @Override
    public LoadDocumentEntity prepareTempDocument(String docType, MimeTypes mimeType) throws DocumentSaveException {
        String path = docFileNamesResolver.buildTempDocumentPath(docType);
        String fileName = docFileNamesResolver.buildTempFileName(docType, mimeType);

        LoadDocumentEntity loadDocument = new LoadDocumentEntity();
        loadDocument.setDocumentPath(path);
        loadDocument.setDocFileName(fileName);
        loadDocument.setDocumentType(DocumentTypes.TEMP.getDbValue());
        loadDocument.setFileType(mimeType);

        try {
            FileUtils.forceMkdir(new File(docFileNamesResolver.buildDirectPath(path)));

        } catch (Exception e) {
            deleteTempDocument(loadDocument);
            throw new DocumentSaveException("Temp document preparation failed", e);
        }

        return loadDocument;
    }

    @Override
    public void prepareDocument(LoadDocumentEntity document) throws DocumentSaveException {
        if (StringUtils.isBlank(document.getDocumentType())) {
            throw new IllegalArgumentException("Document type must be specified for saving document");
        }

        try {
            String documentPath = docFileNamesResolver.buildDocumentPath(document);
            String docFileName = docFileNamesResolver.buildDocumentFileName(document);

            document.setDocumentPath(documentPath);
            document.setDocFileName(docFileName);

            FileUtils.forceMkdir(new File(docFileNamesResolver.buildDirectPath(documentPath)));

        } catch (IOException ex) {
            LOG.error("Cannot prepare document", ex);
            throw new DocumentSaveException(ex.getMessage(), ex);
        }
    }

    @Override
    public void mergeDocuments(List<Long> loadDocuments, String path, String fileName) throws ApplicationException {
        try {
            List<LoadDocumentEntity> documents = documentDao.getAll(loadDocuments, true);

            List<String> filesToMerge = new ArrayList<String>();
            for (LoadDocumentEntity document : documents) {
                filesToMerge.add(docFileNamesResolver.buildDirectPath(document.getDocumentPath()) + FILE_SEPARATOR + document.getDocFileName());
            }

            File dir = new File(docFileNamesResolver.buildDirectPath(path));
            FileUtils.forceMkdir(dir);

            PdfUtil.mergeDocuments(filesToMerge, new FileOutputStream(new File(dir, fileName)));
        } catch (Exception ex) {
            LOG.error("Cannot merge document", ex);
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void splitDocument(String document, List<DocumentSplitCO> criteria) throws ApplicationException {
        try {
            List<LoadDocumentEntity> loadDocuments = new ArrayList<LoadDocumentEntity>();

            List<Pair<DocumentSplitCO, byte[]>> documentContentList = PdfUtil.splitDocument(docFileNamesResolver.buildDirectPath(document), criteria);
            processSplitDocuments(loadDocuments, documentContentList);
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void splitDocument(byte[] document, List<DocumentSplitCO> criteria) throws ApplicationException {
        try {
            List<LoadDocumentEntity> loadDocuments = new ArrayList<LoadDocumentEntity>();

            List<Pair<DocumentSplitCO, byte[]>> documentContentList = PdfUtil.splitDocument(document, criteria);
            processSplitDocuments(loadDocuments, documentContentList);
        } catch (Exception ex) {
            LOG.error("Cannot split document", ex);
            throw new ApplicationException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveDocument(LoadDocumentEntity document) throws DocumentSaveException {
        if (StringUtils.isBlank(document.getDocumentType())) {
            throw new IllegalArgumentException("Document type must be specified for saving document");
        }

        try {
            String documentPath = docFileNamesResolver.buildDocumentPath(document);
            String docFileName = docFileNamesResolver.buildDocumentFileName(document);

            document.setDocumentPath(documentPath);
            document.setDocFileName(docFileName);

            // Save the load document information to database.
            documentDao.saveOrUpdate(document);

            FileOperationsUtils.writeToFileSystem(docFileNamesResolver.buildDirectPath(documentPath), docFileName, document.getContent());
        } catch (IOException ex) {
            LOG.error("Cannot save document", ex);
            throw new DocumentSaveException(ex.getMessage(), ex);
        }
    }

    @Override
    public void saveDocument(LoadDocumentEntity document, InputStream inputStream, String prefix) throws DocumentSaveException {
        if (StringUtils.isBlank(document.getDocumentType()) && StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("Either document type or prefix must be specified for saving document");
        }

        try {
            String documentPath;
            String docFileName;
            if (StringUtils.isBlank(prefix)) {
                documentPath = docFileNamesResolver.buildDocumentPath(document);
                docFileName = docFileNamesResolver.buildDocumentFileName(document);
            } else {
                documentPath = prefix;
                docFileName = docFileNamesResolver.buildDocumentFileName(prefix, document.getFileType());
            }

            document.setDocumentPath(documentPath);
            document.setDocFileName(docFileName);

            // Save the load document information to database.
            documentDao.saveOrUpdate(document);

            FileOperationsUtils.writeToFileSystem(docFileNamesResolver.buildDirectPath(documentPath), docFileName, inputStream);
        } catch (IOException ex) {
            LOG.error("Cannot save document", ex);
            throw new DocumentSaveException(ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteTempDocument(long tempDocId) throws EntityNotFoundException {
        LoadDocumentEntity loadDocumentEntity = documentDao.get(tempDocId);
        deleteTempDocument(loadDocumentEntity);
    }

    @Override
    public void deleteTempDocument(LoadDocumentEntity loadDocumentEntity) {
        if (loadDocumentEntity == null) {
            return;
        }
        String dir = docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath());
        FileUtils.deleteQuietly(new File(dir, loadDocumentEntity.getDocFileName()));

        deleteEmptyFolder(dir);
        if (loadDocumentEntity.getId() != null) {
            documentDao.deleteTempDocument(loadDocumentEntity);
        }
    }

    @Override
    public void deleteDocuments(List<Long> docId) {
        documentDao.deleteDocuments(docId);
    }

    @Override
    public void deleteStaleTempDocuments(int passedDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -passedDays);
        List<LoadDocumentEntity> documents = documentDao.findTempDocumentsOlderThanSpecifiedDate(calendar.getTime());
        for (LoadDocumentEntity document : documents) {
            deleteTempDocument(document);
        }
    }

    @Override
    public void moveAndSaveTempDocPermanently(Long tempDocId, Long loadId, String docType) throws EntityNotFoundException, DocumentSaveException {
        LoadDocumentEntity tempDocument = documentDao.get(tempDocId);
        String tempDocPath = tempDocument.getDocumentPath();
        String tempDocFileName = tempDocument.getDocFileName();
        String tempDocFileExtension = FilenameUtils.getExtension(tempDocFileName);

        tempDocument.setLoadId(loadId);
        tempDocument.setDocumentType(docType);
        tempDocument.setFileType(MimeTypes.getByName(tempDocFileExtension));

        String docPath = docFileNamesResolver.buildDocumentPath(tempDocument);
        String fileName = docFileNamesResolver.buildDocumentFileName(tempDocument);

        String tempDocumentAbsoluteFolderPath = docFileNamesResolver.buildDirectPath(tempDocPath);
        File srcFile = new File(tempDocumentAbsoluteFolderPath, tempDocFileName);
        File destFile = new File(docFileNamesResolver.buildDirectPath(docPath), fileName);

        tempDocument.setDocumentPath(docPath);
        tempDocument.setDocFileName(fileName);
        documentDao.saveOrUpdate(tempDocument);

        try {
            FileOperationsUtils.moveFile(srcFile, destFile);
            deleteEmptyFolder(tempDocumentAbsoluteFolderPath);
        } catch (IOException e) {
            throw new DocumentSaveException("Cannot move document", e);
        }
    }

    private void deleteEmptyFolder(String folder) {
        File fileDirectory = new File(folder);
        if (fileDirectory.isDirectory() && fileDirectory.list().length == 0) {
            FileUtils.deleteQuietly(fileDirectory);
        }
    }

    @Override
    public LoadDocumentEntity getDocumentWithStream(Long documentId) throws EntityNotFoundException, DocumentReadException {
        LoadDocumentEntity document = documentDao.get(documentId);
        initDocumentStream(document);
        return document;
    }

    @Override
    public LoadDocumentEntity getDocumentWithStream(Long documentId, String downloadToken) throws EntityNotFoundException, DocumentReadException {
        LoadDocumentEntity document = documentDao.findDocumentByIdAndToken(documentId, downloadToken);
        initDocumentStream(document);
        return document;
    }

    @Override
    public void initDocumentStream(LoadDocumentEntity document) throws DocumentReadException {
        File file = new File(docFileNamesResolver.buildDirectPath(document.getDocumentPath()), document.getDocFileName());
        document.setStreamLength(file.length());
        try {
            document.setStreamContent(FileUtils.openInputStream(file));
        } catch (IOException e) {
            throw new DocumentReadException("Can't read document", e);
        }
    }

    private void processSplitDocuments(List<LoadDocumentEntity> loadDocuments,
            List<Pair<DocumentSplitCO, byte[]>> documentContentList) throws IOException {
        for (Pair<DocumentSplitCO, byte[]> pair : documentContentList) {
            DocumentSplitCO splitCo = pair.getKey();
            byte[] content = pair.getValue();
            LoadDocumentEntity loadDocEntity = new LoadDocumentEntity();
            loadDocEntity.setLoadId(splitCo.getLoadId());
            loadDocEntity.setDocumentType(splitCo.getDocumentType());

            loadDocEntity.setDocumentPath(docFileNamesResolver.buildDocumentPath(loadDocEntity));
            loadDocEntity.setDocFileName(docFileNamesResolver.buildDocumentFileName(loadDocEntity));
            loadDocEntity.setContent(content);

            loadDocuments.add(loadDocEntity);
        }

        documentDao.saveOrUpdateBatch(loadDocuments);

        for (LoadDocumentEntity loadDocEntity : loadDocuments) {
            String path = docFileNamesResolver.buildDirectPath(loadDocEntity.getDocumentPath());
            FileOperationsUtils.writeToFileSystem(path, loadDocEntity.getDocFileName(), loadDocEntity.getContent());
        }
    }

    @Override
    public LoadDocumentEntity concatenateAndSaveDocument(List<InputStream> documents, DocumentTypes documentType)
            throws ApplicationException {
        LoadDocumentEntity invoiceDocument = getNewPdfDocument(documentType);

        if (documents.isEmpty()) {
            invoiceDocument.setId(0L);
            return invoiceDocument;
        }

        File docFile = null;
        try {
            prepareDocument(invoiceDocument);
            docFile = new File(docFileNamesResolver.buildDirectPath(invoiceDocument.getDocumentPath()), invoiceDocument.getDocFileName());

            FileOutputStream docOutputStream = new FileOutputStream(docFile);
            concatenatePdfFilesInOne(documents, docOutputStream);
            savePreparedDocument(invoiceDocument);
        } catch (Exception e) {
            FileUtils.deleteQuietly(docFile);
            throw new ApplicationException("Exception during preparing document for transactional invoice. " + e.getMessage(), e);
        }
        return invoiceDocument;
    }

    private void concatenatePdfFilesInOne(List<InputStream> pdfFileInputStreams, OutputStream outputStream) throws ApplicationException {
        try {
            if (pdfFileInputStreams.isEmpty()) {
                return;
            }
            if (pdfFileInputStreams.size() == 1) {
                IOUtils.copy(pdfFileInputStreams.iterator().next(), outputStream);
                return;
            }

            Document document = new Document();

            PdfCopy copy = new PdfCopy(document, outputStream);
            copy.setFullCompression();
            copy.createXmpMetadata();

            document.open();
            for (InputStream pdfFileInputStream : pdfFileInputStreams) {
                PdfReader reader = new PdfReader(pdfFileInputStream);
                int numberOfPages = reader.getNumberOfPages();
                for (int page = 0; page < numberOfPages; page++) {
                    copy.addPage(copy.getImportedPage(reader, page + 1));
                }
                copy.freeReader(reader);
                reader.close();
            }
            copy.flush();
            copy.close();
        } catch (Exception e) {
            for (InputStream pdfFileInputStream : pdfFileInputStreams) {
                IOUtils.closeQuietly(pdfFileInputStream);
            }

            throw new ApplicationException("Exception on concatenating pdf files into one. " + e.getMessage(), e);
        }
    }

    private LoadDocumentEntity getNewPdfDocument(DocumentTypes documentType) {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setFileType(MimeTypes.PDF);
        document.setDocumentType(documentType.getDbValue());

        return document;
    }
    /**
     * Delete from file system.
     *
     * @param loadDocumentEntity the load document entity
     */
    public void deleteFromFileSystem(LoadDocumentEntity loadDocumentEntity) {
        String dir = docFileNamesResolver.buildDirectPath(loadDocumentEntity.getDocumentPath());
        FileUtils.deleteQuietly(new File(dir, loadDocumentEntity.getDocFileName()));

        deleteEmptyFolder(dir);
    }
}
