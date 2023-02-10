package com.pls.documentmanagement.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
import com.pls.core.exception.ApplicationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;

/**
 * Utility class for converting to tiff.
 * 
 * @author Alexander Nalapko
 *
 */
public final class TiffConverter {

    private static final String ERROR_MESSAGE = "Error saving tiff document. ";
    private static final String OUTPUT_DIRECTORY = "invoiceDocuments";
    private static final String IMAGE_FORMAT = "tiff";
    private static final String FILE_NAME_FORMAT = "%s%s.tiff";
    private static final String PDF_EXTENSION = "pdf";

    private static final Logger LOGGER = LoggerFactory.getLogger(TiffConverter.class);

    static {
        ImageIO.scanForPlugins();
    }

    /**
     * Constructor.
     */
    private TiffConverter() {
    }

    /**
     * Converts images of different formats into <code>tiff</code> format.
     * 
     * @param documentsPath - path to images. Not <code>null</code>.
     * @param invoiceId - invoice id. Not <code>null</code>.
     * @param document - {@link LoadDocumentEntity}. Not <code>null</code>.
     * @throws ApplicationException if there is an error.
     */
    public static void convert(String documentsPath, Long invoiceId, LoadDocumentEntity document) throws ApplicationException {
        String inFilePath = buildDocumentPath(documentsPath, document.getDocumentPath(), document.getDocFileName());
        String outFileName = String.format(FILE_NAME_FORMAT, document.getLoadId().toString(), document.getDocumentType());
        String outFilePath = buildDocumentPath(documentsPath, OUTPUT_DIRECTORY, invoiceId.toString(), outFileName);
        mkdirs(FilenameUtils.getFullPath(outFilePath));

        try {
            if (FilenameUtils.isExtension(document.getDocFileName(), PDF_EXTENSION)) {
                convertPdf(inFilePath, outFilePath);
            } else {
                convertImg(inFilePath, outFilePath);
            }
        } catch (Exception ex) {
            throw new ApplicationException(ERROR_MESSAGE + ex.getMessage(), ex);
        }
    }

    private static String buildDocumentPath(String documentsPath, String... path) {
        String filePath = documentsPath;
        for (String item : path) {
            filePath = FilenameUtils.concat(filePath, item);
        }

        return filePath;
    }

    /**
     * Convert pdf.
     *
     * @param inFilePath the in file path
     * @param outFilePath the out file path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void convertPdf(String inFilePath, String outFilePath) throws IOException {
        InputStream inStream = new FileInputStream(inFilePath);

        File outputfile = new File(outFilePath);
        FileOutputStream outStream = new FileOutputStream(outputfile);

        try {
            convertToStream(inStream, outStream);
        } catch (IOException ex) {
            FileUtils.deleteQuietly(outputfile);
            throw new IOException(ex);
        } finally {
            IOUtils.closeQuietly(inStream);
            IOUtils.closeQuietly(outStream);
        }
    }

    private static void convertToStream(InputStream inStream, OutputStream outStream) throws IOException {
        ImageWriter imageWriter = null;
        ImageOutputStream ios = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(inStream);
            List<PDPage> list = document.getDocumentCatalog().getAllPages();
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT);
            imageWriter = writers.next();

            TIFFImageWriteParam writeParam = new TIFFImageWriteParam(Locale.getDefault());
            writeParam.setCompressionMode(TIFFImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType("LZW");

            ios = ImageIO.createImageOutputStream(outStream);
            imageWriter.setOutput(ios);
            imageWriter.prepareWriteSequence(null);
            for (PDPage page : list) {
                BufferedImage image = page.convertToImage();
                imageWriter.writeToSequence(new IIOImage(image, null, null), writeParam);
            }
        } finally {
            closeResource(imageWriter, ios, document);
        }
    }

    private static void convertImg(String inFilePath, String outFilePath) throws IOException {
        FileInputStream inStream = new FileInputStream(inFilePath);
        File outFile = new File(outFilePath);
        try {
            final BufferedImage img = ImageIO.read(inStream);
            ImageIO.write(img, IMAGE_FORMAT, outFile);
        } catch (IOException ex) {
            FileUtils.deleteQuietly(outFile);
            throw new IOException(ex);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    private static void closeResource(ImageWriter imageWriter, ImageOutputStream ios, PDDocument document) {
        try {
            if (imageWriter != null) {
                imageWriter.endWriteSequence();
                imageWriter.dispose();
            }
        } catch (IOException ioe) {
            LOGGER.error(ioe.getMessage());
        }
        try {
            if (ios != null) {
                ios.flush();
                ios.close();
            }
        } catch (IOException ioe) {
            LOGGER.error(ioe.getMessage());
        }
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException ioe) {
            LOGGER.error(ioe.getMessage());
        }
    }

    private static void mkdirs(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}