package com.pls.shipment.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;

/**
 * Utility class for processing pdf's with IText framework.
 *
 * @author Denis Zhupinsky (Team International)
 */
public final class ITextUtils {
    public static final Font HELVETICA10BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    public static final float HUNDRED_PERCENT = 100f;

    public static final float ZERO = 0f;

    private ITextUtils() {
    }

    /**
     * Create table with specified numbers of columns and spacing before and after that table. Table will fill all available width.
     *
     * @param colCount columns count
     * @param spacing spacing before and after table
     * @return created {@link PdfPTable}
     */
    public static PdfPTable createTable(int colCount, Float spacing) {
        PdfPTable table = new PdfPTable(colCount);
        if (spacing != null) {
            table.setSpacingBefore(spacing);
            table.setSpacingAfter(spacing);
        }

        table.setWidthPercentage(HUNDRED_PERCENT);

        return table;
    }

    /**
     * Create table with specified numbers of columns and without spacing before and after that table. Table will fill all available width.
     *
     * @param colCount columns count
     * @return created {@link PdfPTable}
     */
    public static PdfPTable createTable(int colCount) {
        return createTable(colCount, ZERO);
    }

    /**
     * Create table with columns of specified width and without spacing before and after that table. Table will fill all available width.
     *
     * @param relativeWidth array of columns width
     * @return created {@link PdfPTable}
     */
    public static PdfPTable createTable(float[] relativeWidth) {
        PdfPTable table = new PdfPTable(relativeWidth);
        table.setSpacingBefore(ZERO);
        table.setSpacingAfter(ZERO);
        table.setWidthPercentage(HUNDRED_PERCENT);

        return table;
    }

    /**
     * Create table with header row.
     *
     * @param headerNames column names
     * @return {@link PdfPTable}
     */
    public static PdfPTable createTable(Phrase[] headerNames) {
        PdfPTable table = createTable(headerNames.length);

        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        for (Phrase headerName : headerNames) {
            table.addCell(headerName);
        }

        table.getDefaultCell().setBackgroundColor(null);
        return table;
    }

    /**
     * Create table with single header row, but several columns.
     *
     * @param headerName header name
     * @param columnSizes sizes of table columns
     * @return {@link PdfPTable}
     */
    public static PdfPTable createSingleHeaderTable(Phrase headerName, float[] columnSizes) {
        PdfPTable table = createTable(columnSizes);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setColspan(columnSizes.length);
        cell.addElement(headerName);
        table.addCell(cell);

        return table;
    }

    /**
     * Create table with header row.
     *
     * @param columns linked map with columns names and sizes
     * @param headerFont font that will be used for header's columns
     * @return {@link PdfPTable}
     */
    public static PdfPTable createTable(Map<String, Float> columns, Font headerFont) {
        Collection<Float> widths = columns.values();
        PdfPTable table = createTable(ArrayUtils.toPrimitive(widths.toArray(new Float[widths.size()])));

        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        for (String headerName : columns.keySet()) {
            table.addCell(new Phrase(headerName, headerFont));
        }

        table.getDefaultCell().setBackgroundColor(null);
        return table;
    }

    /**
     * Create cell with specified content, font and padding.
     *
     * @param content cell content
     * @param font content font
     * @param padding padding of that column
     * @param border cell border
     * @return {@link} PdfPCell
     */
    public static PdfPCell createCell(String content, Font font, Float padding, Integer border) {
        PdfPCell cell;
        if (font != null) {
            cell = new PdfPCell(new Phrase(content, font));
        } else {
            cell = new PdfPCell(new Phrase(content));
        }

        if (padding != null) {
            cell.setPadding(padding);
        }

        if (border != null) {
            cell.setBorder(border);
        }

        return cell;
    }

    /**
     * Create cell with specified content, and default font, padding and border.
     *
     * @param content cell content
     * @return {@link} PdfPCell
     */
    public static PdfPCell createCell(String content) {
        return createCell(content, null, ZERO, null);
    }

    /**
     * Create cell without border, but with specified content, font and default padding.
     *
     * @param content cell content
     * @param font content font
     * @return {@link PdfPCell}
     */
    public static PdfPCell createCellWithoutBorder(String content, Font font) {
        return createCell(content, font, null, Rectangle.NO_BORDER);
    }

    /**
     * Create empty line as a {@link Paragraph}.
     *
     * @param number number of lines
     * @return {@link Paragraph}
     */
    public static Paragraph createEmptyLine(int number) {
        Paragraph emptySpace = new Paragraph();
        for (int i = 0; i < number; i++) {
            emptySpace.add(new Paragraph(" "));
        }

        return emptySpace;
    }

    /**
     * Create image of square with specified parameters.
     *
     * @param cb {@link} PdfContentByte
     * @param regionRadius radius of region where square will be placed
     * @param squareRadius radius of square
     * @return created {@link} Image
     * @throws BadElementException {@link Image#getInstance(PdfTemplate)}
     */
    public static Image getSquareImage(PdfContentByte cb, float regionRadius, float squareRadius) throws BadElementException {
        return getRectagleImage(cb, regionRadius, regionRadius, squareRadius, squareRadius, .2f);
    }

    /**
     * Create a barcode image using Barcode39 with code type CODE128.
     * 
     * @param pdfContentByte direct content for the document
     * @param bolNumber is BOL number
     * @return created {@link} Image
     */
    public static Image createBarcode(PdfContentByte pdfContentByte, String bolNumber) {
        Barcode39 barcode = new Barcode39();
        barcode.setCode(bolNumber);
        barcode.setCodeType(Barcode39.CODE128);
        barcode.setStartStopText(false);
        barcode.setExtended(true);
        return barcode.createImageWithBarcode(pdfContentByte, null, null);
    }

    /**
     * Create image of rectangle with specified parameters.
     *
     * @param cb {@link} PdfContentByte
     * @param regionWidth width of region where rectangle will be placed
     * @param regionHeight height of region where rectangle will be placed
     * @param rectangleWidth width of rectangle
     * @param rectangleHeight height of rectangle
     * @param lineWidth line width of rectangle
     * @return created {@link} Image
     * @throws BadElementException {@link Image#getInstance(PdfTemplate)}
     */
    public static Image getRectagleImage(PdfContentByte cb, float regionWidth, float regionHeight, float rectangleWidth, float rectangleHeight,
            float lineWidth) throws BadElementException {
        PdfTemplate template = cb.createTemplate(regionWidth, regionHeight);
        template.setLineWidth(lineWidth);
        template.rectangle(0, 0, rectangleWidth, rectangleHeight);
        template.stroke();

        return Image.getInstance(template);
    }

    /**
     * Starts writing of PDF document. Will return {@link PdfWriter}.
     *
     * @param document {@link Document} to write
     * @param outputStream output stream where content will be stored
     * @return {@link PdfWriter}
     * @throws DocumentException {@link PdfWriter#getInstance(Document, OutputStream)}
     */
    public static PdfWriter startWriting(Document document, OutputStream outputStream) throws DocumentException {
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        return pdfWriter;
    }

    /**
     * Insert pagination to footer - page number (page x of y) for PDF document.
     *
     * @param inputfile temporary filled PDF file with all data
     * @param document {@link Document} with meta information
     * @param outputStream output PDF file where content will be stored
     * @throws PDFGenerationException {@link PDFGenerationException}
     */
    public static void addPagination(File inputfile, Document document, OutputStream outputStream) throws PDFGenerationException {
        try {
            PdfReader pdfReader = new PdfReader(inputfile.getAbsolutePath());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            int totalPages = pdfReader.getNumberOfPages();
            for (int page = 1; page <= totalPages; page++) {
                PdfContentByte content = pdfStamper.getUnderContent(page);
                String pageNumber = String.format("Page %d of %d", page, totalPages);
                Phrase footer = new Phrase(pageNumber, HELVETICA10BOLD);
                ColumnText.showTextAligned(content, Element.ALIGN_CENTER, footer,
                        (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
            }

            pdfStamper.close();
            pdfReader.close();
        } catch (IOException e) {
            throw new PDFGenerationException("IO exception occurs during Invoice generation", e);
        } catch (DocumentException e) {
            throw new PDFGenerationException("PDF generation of Invoice failed", e);
        } finally {
            FileUtils.deleteQuietly(inputfile);
        }
    }

    /**
     * Close document with related resource quietly.
     *
     * @param document document to close
     */
    public static void closeQuietly(Document document) {
        if (document != null) {
            document.close();
        }
    }
}
