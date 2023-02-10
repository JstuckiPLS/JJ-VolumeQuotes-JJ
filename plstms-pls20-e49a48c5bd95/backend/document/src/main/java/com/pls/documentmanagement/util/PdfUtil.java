package com.pls.documentmanagement.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfWriter;
import com.pls.core.common.MimeTypes;
import com.pls.documentmanagement.shared.DocumentSplitCO;

/**
 * An utility class for creating or manipulating pdf documents.
 *
 * @author Pavani Challa
 *
 */
public final class PdfUtil {
    private static final List<MimeTypes> IMAGE_FORMATS =
            Arrays.asList(MimeTypes.TIFF, MimeTypes.TIF, MimeTypes.JPEG, MimeTypes.PNG, MimeTypes.GIF, MimeTypes.BMP, MimeTypes.JPG);
    private static final MimeTypes PDF_FORMAT = MimeTypes.PDF;

    private PdfUtil() {
    }

    /**
     * Write pdf document with several pages considering mimeType of content.
     *
     * @param pages pages that need to be stored at document
     * @param mimeType mime type of document, can be of image mimeType that need to be stored as pdf
     * @return byte array of prepared pdf document
     * @throws IOException thrown if there was IO issue during pdf creation
     * @throws DocumentException thrown if there was issue with document writing with IText
     */
    public static byte[] writePdfDocument(List<byte[]> pages, MimeTypes mimeType) throws IOException, DocumentException {
        byte[] content;
        if (pages == null || pages.isEmpty()) {
            return new byte[0];
        } else {
            // Images should be written to pdf differently. So, first check if the document was returned by API in image mimeType.
            if (isImage(mimeType)) {
                content = writePdfAsImage(pages);
            } else {
                content = writePdf(pages);
            }
        }

        return content;
    }

    /**
     * Combines multiple documents into a single document with name "fileName" passed in the parameters and saves it to the location in the file
     * system given the parameter "path".
     *
     * @param filesToMerge
     *            full path to PDF documents that are to be combined
     * @param outputStream
     *            output stream where newly created document must be saved.
     * @throws IOException
     *             thrown if the pdf file cannot be created on file system
     * @throws DocumentException
     *             thrown if the files can not be copied to the new file
     */
    public static void mergeDocuments(List<String> filesToMerge, OutputStream outputStream) throws IOException, DocumentException {
        Document document = new Document();
        PdfCopy copy = new PdfSmartCopy(document, outputStream);
        copy.createXmpMetadata();
        PdfReader reader;

        try {
            document.open();
            for (String fileToMerge : filesToMerge) {
                reader = new PdfReader(fileToMerge);
                for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                    copy.addPage(copy.getImportedPage(reader, page));
                }
                copy.freeReader(reader);
                reader.close();
            }
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Loads the document from the file system and created multiple pdf documents based on the criteria.
     *
     * @param documentPathToSplit
     *            Full path of the document in the file system including the file name.
     * @param criteria
     *            Criteria for splitting the document
     * @return the Load document entities with the metadata of the newly created documents.
     * @throws IOException
     *             for any error when reading the document/creating the file on file system
     * @throws DocumentException
     *             for any error in reading/copying the document.
     */
    public static List<Pair<DocumentSplitCO, byte[]>> splitDocument(String documentPathToSplit,
            List<DocumentSplitCO> criteria) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(documentPathToSplit);
        return splitDocument(reader, criteria);
    }

    /**
     * Created multiple pdf documents based on the criteria from the document provided.
     *
     * @param documentToSplit
     *            Actual document bytes for creating new documents.
     * @param criteria
     *            Criteria for splitting the document
     * @return the Load document entities with the metadata of the newly created documents.
     * @throws IOException
     *             for any error when reading the document/creating the file on file system
     * @throws DocumentException
     *             for any error in reading/copying the document.
     */
    public static List<Pair<DocumentSplitCO, byte[]>> splitDocument(byte[] documentToSplit,
            List<DocumentSplitCO> criteria) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(documentToSplit);
        return splitDocument(reader, criteria);
    }

    /**
     * Converts load document in image format to pdf file and returns as byte array. This conversion uses iText library. using the iText
     * library, we first read the bytes as "Image" object and then set the pdf page size to be same as the "Image" size. This needs to be done else
     * the image is cropped to fit the pdf page size. The image is positioned on the pdf page without any margins.
     *
     * @param pages
     *            array of bytes that represents pages of document that need to be saved
     * @throws IOException
     *             thrown if the pdf file cannot be created on file system
     * @throws DocumentException
     *             thrown if the image cannot be converted to pdf file on file system
     */
    private static byte[] writePdfAsImage(List<byte[]> pages) throws IOException, DocumentException {
        Document loadDoc = new Document(PageSize.LETTER);
        loadDoc.setMargins(0, 0, 0, 0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(loadDoc, baos);
            pdfWriter.setFullCompression();
            pdfWriter.setStrictImageSequence(true);
            pdfWriter.setLinearPageMode();
            pdfWriter.setViewerPreferences(PdfWriter.HideMenubar | PdfWriter.HideToolbar | PdfWriter.HideWindowUI);

            loadDoc.open();

            for (byte[] page : pages) {
                Image image = Image.getInstance(decode(page));

                image.setCompressionLevel(9);
                image.setAbsolutePosition(0, 0);
                image.scaleToFit(loadDoc.getPageSize().getWidth(), loadDoc.getPageSize().getHeight());
                loadDoc.newPage();
                loadDoc.add(image);
            }
        } finally {
            if (loadDoc.isOpen()) {
                loadDoc.close();
            }
        }

        return baos.toByteArray();
    }

    /**
     * Convert image to pdf.
     *
     * @param fileName path to image
     * @param outputStream output stream to write result
     * @throws DocumentException if internal IText error happens
     * @throws IOException if internal IO error happens
     */
    public static void convertImageToPdf(String fileName, OutputStream outputStream) throws DocumentException, IOException {
        try {
            Document imagePdfDocument = new Document();
            PdfWriter.getInstance(imagePdfDocument, outputStream);
            imagePdfDocument.open();
            Image image = Image.getInstance(fileName);
            float scaler = ((imagePdfDocument.getPageSize().getWidth() - imagePdfDocument.leftMargin() - imagePdfDocument.rightMargin())
                    / image.getWidth()) * 100;

            image.scalePercent(scaler);
            imagePdfDocument.add(image);
            imagePdfDocument.close();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Writes the load document as "pdf" file as byte array. This method assumes that the document is already in "pdf" format and hence no
     * conversion is done.
     *
     * @param pages
     *            array of bytes that represents pages of document that need to be saved
     * @throws IOException
     *             thrown if the pdf file cannot be created on file system
     * @throws DocumentException
     *             thrown if the bytes cannot be converted to pdf file on file system
     */
    private static byte[] writePdf(List<byte[]> pages) throws IOException, DocumentException {
        Document loadDoc = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfCopy copy = new PdfSmartCopy(loadDoc, baos);
        copy.createXmpMetadata();

        try {
            loadDoc.open();
            for (byte[] doc : pages) {
                PdfReader reader = new PdfReader(decode(doc));
                for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                    copy.addPage(copy.getImportedPage(reader, page));
                }
                copy.freeReader(reader);
                reader.close();
            }
        } finally {
            if (loadDoc.isOpen()) {
                loadDoc.close();
            }
        }

        return baos.toByteArray();
    }

    private static byte[] decode(byte[] doc) {
        byte[] bytes = doc;
        while (Base64.isBase64(bytes) && bytes.length > 0) {
            bytes = Base64.decodeBase64(bytes);
        }

        return bytes;
    }

    /**
     * Checks if the mimeType is an image mimeType. This check is required as the images need to be written as pdf in a different way.
     *
     * @param mimeType
     *            mimeType to be checked if it is an image
     * @return true if the mimeType is an image else false;
     */
    public static boolean isImage(MimeTypes mimeType) {
        return mimeType != null && IMAGE_FORMATS.contains(mimeType);
    }

    /**
     * Checks if the mimeType is an pdf mimeType.
     *
     * @param mimeType
     *            mimeType to be checked if it is an image
     * @return true if the mimeType is an pdf else false;
     */
    public static boolean isPdf(MimeTypes mimeType) {
        return PDF_FORMAT.equals(mimeType);
    }

    private static List<Pair<DocumentSplitCO, byte[]>> splitDocument(PdfReader reader,
            List<DocumentSplitCO> criteria) throws IOException, DocumentException {
        List<Pair<DocumentSplitCO, byte[]>> documents = new ArrayList<Pair<DocumentSplitCO, byte[]>>(criteria.size());

        Document loadDocument;
        PdfCopy copy;

        for (DocumentSplitCO splitCo : criteria) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            setToPage(splitCo, reader.getNumberOfPages());
            if (splitCo.getFromPage() <= reader.getNumberOfPages()) {

                loadDocument = new Document(reader.getPageSizeWithRotation(1));
                copy = new PdfSmartCopy(loadDocument, baos);
                loadDocument.open();
                for (int page = splitCo.getFromPage(); page <= splitCo.getToPage(); page++) {
                    copy.addPage(copy.getImportedPage(reader, page));
                }

                loadDocument.close();

                documents.add(new ImmutablePair<DocumentSplitCO, byte[]>(splitCo, baos.toByteArray()));
            }
        }

        reader.close();
        return documents;
    }

    private static void setToPage(DocumentSplitCO criteria, int totalPages) {
        if (criteria.getToPage() > totalPages) {
            criteria.setToPage(totalPages);
        }

        if (criteria.getFromPage() > criteria.getToPage()) {
            criteria.setToPage(criteria.getFromPage());
        }
    }

}
