package com.pls.shipment.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableList;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.pls.core.common.MimeTypes;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.exception.fileimport.InvalidFormatException;
import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.exception.FileSizeLimitException;
import com.pls.core.service.util.io.LimitedInputStreamDecorator;
import com.pls.documentmanagement.dao.LoadDocumentDao;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.documentmanagement.exception.DocumentSaveException;
import com.pls.documentmanagement.util.PdfUtil;
import com.pls.shipment.dao.DocumentTypeDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.pdf.BolPdfDocumentParameter;
import com.pls.shipment.service.pdf.BolPdfDocumentWriter;
import com.pls.shipment.service.pdf.ConsigneeInvoiceDocumentParameter;
import com.pls.shipment.service.pdf.ConsigneeInvoicePdfDocumentWriter;
import com.pls.shipment.service.pdf.Printable;
import com.pls.shipment.service.pdf.ShippingLabelsDocumentParameter;
import com.pls.shipment.service.pdf.ShippingLabelsPdfDocumentWriter;

/**
 * Implementation of {@link com.pls.shipment.service.ShipmentDocumentService} service.
 *
 * @author Maxim Medvedev
 */
@Service
@Transactional
public class ShipmentDocumentServiceImpl extends AbstractDocumentService implements ShipmentDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentDocumentServiceImpl.class);

    public static final ImmutableList<String> TIFF_EXTENSIONS = ImmutableList.of("tif", "tiff");

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentDocumentServiceImpl.class);

    private static final double MAX_FILE_SIZE_MEGABYTES = 50;

    private static final long MAX_FILE_SIZE_BYTES = (long) (MAX_FILE_SIZE_MEGABYTES * 1024 * 1024);

    private static final List<ShipmentStatus> REGENERATE_BOL = Arrays.asList(ShipmentStatus.OPEN, ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED);

    @Autowired
    private LoadDocumentDao documentDao;

    @Autowired
    private DocumentTypeDao documentTypeDao;

    @Autowired
    private BolPdfDocumentWriter bolPdfDocumentWriter;

    @Autowired
    private ShippingLabelsPdfDocumentWriter shippingLabelsPdfDocumentWriter;

    @Autowired
    private ConsigneeInvoicePdfDocumentWriter consigneeInvoicePdfDocumentWriter;

    @Autowired
    private LtlShipmentDao loadDao;

    @Override
    public List<ShipmentDocumentInfoBO> getDocumentList(Long shipmentId) throws InvalidArgumentException, EntityNotFoundException {
        if (shipmentId == null) {
            throw new InvalidArgumentException();
        }
        return documentDao.getDocumentsInfoForShipment(shipmentId);
    }

    @Override
    public boolean generateShipmentDocumentsSafe(Set<DocumentTypes> regenerateDocTypes, LoadEntity load,
            boolean hideCreatedTime, Long userId) {
        try {
            generateShipmentDocuments(regenerateDocTypes, load, hideCreatedTime, userId);
            return true;
        } catch (PDFGenerationException e) {
            LOGGER.error("Can't create documents for load", e);
            return false;
        }
    }

    @Override
    public Map<DocumentTypes, Long> generateShipmentDocuments(Set<DocumentTypes> regenerateDocTypes, LoadEntity load,
            boolean hideCreatedTime, Long userId) throws PDFGenerationException {

        Map<DocumentTypes, Long> result = new HashMap<DocumentTypes, Long>();

        //regenerate BOL
        if (REGENERATE_BOL.contains(load.getStatus()) || regenerateDocTypes.contains(DocumentTypes.BOL)) {
            result.put(DocumentTypes.BOL, prepareBolDocument(load, hideCreatedTime, userId));
        }
        //regenerate SHIPPING_LABELS
        if (REGENERATE_BOL.contains(load.getStatus()) || regenerateDocTypes.contains(DocumentTypes.SHIPPING_LABELS)) {
            result.put(DocumentTypes.SHIPPING_LABELS, prepareShippingLabelsDocument(load));
        }
        //regenerate CONSIGNEE_INVOICE
        if (regenerateDocTypes.contains(DocumentTypes.CONSIGNEE_INVOICE)
                || (load.getStatus() != ShipmentStatus.CANCELLED
                && (isGenerateConsigneeInvoice(load.getOrganization())))) {
            result.put(DocumentTypes.CONSIGNEE_INVOICE, prepareConsigneeInvoiceDocument(load));
        }

        return result;
    }

    @Override
    public Long prepareShippingLabelsDocument(LoadEntity load) throws PDFGenerationException {
        return createPdfDocument(load.getId(), DocumentTypes.SHIPPING_LABELS, shippingLabelsPdfDocumentWriter,
                new ShippingLabelsDocumentParameter(load, load.getId(),
                        getCustomerLogoForShipLabel(load.getOrganization().getId()), Printable.DEFAULT_TEMPLATE));
    }

    @Override
    public Long prepareConsigneeInvoiceDocument(LoadEntity load) throws PDFGenerationException {
        return createPdfDocument(load.getId(), DocumentTypes.CONSIGNEE_INVOICE, consigneeInvoicePdfDocumentWriter,
                new ConsigneeInvoiceDocumentParameter(load));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long prepareBolDocument(LoadEntity load, boolean hideCreatedTime, Long userId) throws PDFGenerationException {
        BolPdfDocumentParameter pdf = new BolPdfDocumentParameter(load, getUserById(userId), getCustomerLogoForBol(load
                .getOrganization().getId()), false, hideCreatedTime);
        return createPdfDocument(load.getId(), DocumentTypes.BOL, bolPdfDocumentWriter, pdf);
    }

    @Override
    public LoadDocumentEntity saveTemporaryDoc(MultipartFile docItemStream) throws IOException, InvalidFormatException, FileSizeLimitException,
            DocumentSaveException, DocumentException {
        String fileName = docItemStream.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        InputStream docInputStream = docItemStream.getInputStream();

        InputStream fixedImageInputStream = null;
        try {
            validatePdf(docItemStream, extension);
            fixedImageInputStream = validateAndFixImage(docItemStream, extension);
        } catch (Exception e) {
            LOG.error("This document file format is invalid. File name: {}. Extension {}.", fileName, extension);
            throw new InvalidFormatException("This document file format is invalid.");
        }

        if (fixedImageInputStream != null) {
            docInputStream = fixedImageInputStream;
        }

        InputStream inputStream = null;
        LoadDocumentEntity tempDocument = null;
        try {
            if (TIFF_EXTENSIONS.contains(StringUtils.lowerCase(extension))) {
                inputStream = convertTiffToPdf(docInputStream);
                extension = StringUtils.lowerCase(MimeTypes.PDF.toString());
            } else {
                inputStream = docInputStream;
            }

            tempDocument = documentService.prepareTempDocument(null, MimeTypes.getByName(extension));
            File docFile = new File(docFileNamesResolver.buildDirectPath(tempDocument.getDocumentPath()), tempDocument.getDocFileName());
            FileUtils.copyInputStreamToFile(new LimitedInputStreamDecorator(inputStream, MAX_FILE_SIZE_BYTES), docFile);

            return documentService.savePreparedDocument(tempDocument);
        } catch (LimitedInputStreamDecorator.LimitExceededException e) {
            LOG.error("Error saving shipment document - limit size exceeded");
            documentService.deleteTempDocument(tempDocument);
            throw new FileSizeLimitException(MAX_FILE_SIZE_MEGABYTES, e);
        } catch (DocumentException e) {
            documentService.deleteTempDocument(tempDocument);
            LOG.error("Error converting TIFF document to PDF", e);
            throw e;
        } catch (Exception e) {
            documentService.deleteTempDocument(tempDocument);
            LOG.error("Error saving shipment document", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public Long generateAndStoreTempBol(LoadEntity load, Long customerId, boolean hideCreatedTime, boolean isManualBol)
            throws PDFGenerationException {
        return generateAndStoreTempDocuments(load, bolPdfDocumentWriter, new BolPdfDocumentParameter(load,
                getCurrentUser(),
                getCustomerLogoForBol(customerId), true, hideCreatedTime, isManualBol), DocumentTypes.BOL);
    }

    @Override
    public Long generateAndStoreTempShippingLabels(LoadEntity load, Long loadId, Long customerId, Printable printType)
            throws PDFGenerationException {
        return generateAndStoreTempDocuments(load, shippingLabelsPdfDocumentWriter,
                new ShippingLabelsDocumentParameter(load, loadId, getCustomerLogoForShipLabel(customerId), printType), DocumentTypes.SHIPPING_LABELS);
    }

    @Override
    public Long generateAndStoreTempConsigneeInvoice(LoadEntity load) throws PDFGenerationException {
        return generateAndStoreTempDocuments(load, consigneeInvoicePdfDocumentWriter,
                new ConsigneeInvoiceDocumentParameter(load),
                DocumentTypes.CONSIGNEE_INVOICE);
    }

    @Override
    public List<LoadDocumentTypeEntity> findDocumentTypes() {
        return documentTypeDao.getLoadDocumentType();
    }

    @Override
    public LoadDocumentTypeEntity getDocumentTypeByStringName(String documentType) {
        return documentTypeDao.findByDocTypeString(documentType, LoadDocumentTypeEntity.class);
    }

    @Override
    public List<LoadDocumentEntity> findReqDocumentsForLoad(Long loadId) {
        return documentDao.findReqDocumentsForLoad(loadId);
    }

    @Override
    protected LoadDocumentEntity prepareLoadDocument(Long documentId, DocumentTypes documentType) {
        String docType = documentType.getDbValue();
        List<LoadDocumentEntity> documentsList = documentDao.findDocumentsForLoad(documentId, docType);

        LoadDocumentEntity document;
        if (!documentsList.isEmpty()) {
            // in fact there should be only one row, however it might be more records from DB perspective
            document = documentsList.get(0);
        } else {
            document = new LoadDocumentEntity();
            document.setDocumentType(docType);
            document.setLoadId(documentId);
        }

        String documentPath = docFileNamesResolver.buildDocumentPath(document);
        String docFileName = docFileNamesResolver.buildDocumentFileName(document);

        document.setDocumentPath(documentPath);
        document.setDocFileName(docFileName);

        return document;
    }

    private InputStream convertTiffToPdf(InputStream inputStream) throws IOException, DocumentException {
        RandomAccessSourceFactory sourceFactory = new RandomAccessSourceFactory();
        RandomAccessSource byteSource = sourceFactory.createSource(inputStream);
        RandomAccessFileOrArray tiff = new RandomAccessFileOrArray(byteSource);

        int numberOfPages = TiffImage.getNumberOfPages(tiff);
        Document pdfDocument = new Document();
        pdfDocument.setPageSize(PageSize.A4);
        pdfDocument.setMargins(0f, 0f, 0f, 0f);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(pdfDocument, outStream);
        pdfDocument.open();

        try {
            for (int i = 1; i <= numberOfPages; i++) {
                Image image = TiffImage.getTiffImage(tiff, i);
                image.scaleToFit(PageSize.A4);

                pdfDocument.add(image);
            }
        } finally {
            pdfDocument.close();
        }

        return new ByteArrayInputStream(outStream.toByteArray());
    }

    private InputStream validateAndFixImage(MultipartFile docItemStream, String extension)
            throws BadElementException, IOException {
        InputStream docInputStream = null;
        if (PdfUtil.isImage(MimeTypes.getByName(extension.toLowerCase()))) {
            try {
                Image.getInstance(docItemStream.getBytes());
            } catch (IOException e) {
                ByteArrayOutputStream resultedOutputStream = fixImage(docItemStream.getInputStream(), extension);
                docInputStream = new ByteArrayInputStream(resultedOutputStream.toByteArray());
                Image.getInstance(resultedOutputStream.toByteArray());
            }
        }
        return docInputStream;
    }

    private void validatePdf(MultipartFile docItemStream, String extension) throws IOException {
        if (PdfUtil.isPdf(MimeTypes.getByName(extension.toLowerCase()))) {
            new PdfReader(new ByteArrayInputStream(docItemStream.getBytes()));
        }
    }

    /**
     * Workaround for itextpdf bug - library can't process some JPEG files.
     * Method converts image to the same fixed image with same extension using Java ImageIO.
     *
     * @param docInputStream - image input stream
     * @param extension - image extension
     * @return fixed output stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private ByteArrayOutputStream fixImage(InputStream docInputStream, String extension) throws IOException {
        ByteArrayOutputStream docOutputStream = new ByteArrayOutputStream();
        BufferedImage originalImage = ImageIO.read(docInputStream);
        ImageOutputStream  ios =  ImageIO.createImageOutputStream(docOutputStream);
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(extension);
        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(1f);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(originalImage, null, null), iwp);
        writer.dispose();
        return docOutputStream;
    }

    private <T extends PdfDocumentParameter> Long generateAndStoreTempDocuments(LoadEntity load, PdfDocumentWriter<T> writer, T parameter,
            DocumentTypes documentType) throws PDFGenerationException {

        loadDao.evict(load);

        LoadDocumentEntity tempDocument = null;

        try {
            tempDocument = documentService.prepareTempDocument(documentType.getDbValue(), MimeTypes.PDF);
            File docFile = new File(docFileNamesResolver.buildDirectPath(tempDocument.getDocumentPath()), tempDocument.getDocFileName());

            FileOutputStream docOutputStream = new FileOutputStream(docFile);
            writer.write(parameter, docOutputStream);

            LoadDocumentEntity document = documentService.savePreparedDocument(tempDocument);
            return document.getId();
        } catch (DocumentSaveException ex) {
            documentService.deleteTempDocument(tempDocument);
            throw new PDFGenerationException("Document cannot be saved", ex);
        } catch (Exception ex) {
            documentService.deleteTempDocument(tempDocument);
            throw new PDFGenerationException("Document file generation failed", ex);
        }
    }

    private boolean isGenerateConsigneeInvoice(CustomerEntity customer) {
        return customer != null
                && customer.isGenerateConsigneeInvoice() != null
                && customer.isGenerateConsigneeInvoice();
    }

}
