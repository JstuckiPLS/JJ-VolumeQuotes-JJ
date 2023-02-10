package com.pls.shipment.service.pdf;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.PhoneUtils;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.service.ITextUtils;
import com.pls.shipment.service.ShipmentUtils;

/**
 * Implementation of {@link PdfDocumentWriter} that generated shipping labels.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class ShippingLabelsPdfDocumentWriter implements PdfDocumentWriter<ShippingLabelsDocumentParameter> {

    public static final int HUNDRED_PERCENT = 100;

    private static final String BOL_LABEL = "BOL#: ";
    private static final String GUARANTEED_BY_LABEL = "Guaranteed By: ";
    private static final String PO_LABEL = "PO#: ";
    private static final String PU_LABEL = "PU#: ";
    private static final String PRO_LABEL = "PRO#: ";
    private static final String SHIPPER_REF = "Ship Ref#: ";
    private static final String NOT_SPECIFIED_LABEL = "NOT SPECIFIED";
    private static final String LOAD_ID_LABEL = "Load ID#: ";
    private static final String SO_LABEL = "SO#: ";
    private static final String TRAILER_LABEL = "Trailer#: ";
    private static final String GL_LABEL = "GL#: ";

    private Font textFont;
    private Font smallTextFont;
    private Font titleFont;
    private Long loadId;

    private Printable printable;

    @Override
    public void write(ShippingLabelsDocumentParameter parameter, OutputStream outputStream)
            throws PDFGenerationException {
        LoadEntity load = parameter.getLoad();
        printable = Printable.getPrintable(parameter.getPrintType() == null ? 0 : parameter.getPrintType().ordinal());
        Document document = parameter.getPrintType() == null
                || printable.equals(Printable.DEFAULT_TEMPLATE)
                || printable.equals(Printable.TEMPLATE4)
                ? new Document(PageSize.A4.rotate()) : new Document();

        initProperties(parameter);

        try {
            ITextUtils.startWriting(document, new BufferedOutputStream(outputStream));
            Image logo = getLogo(parameter.getLogo());
            if (printable != null && printable != Printable.DEFAULT_TEMPLATE) {
                printSelectedDocument(document, load, logo);
            } else {
                document.add(getShippingLabelContent(load, logo));
            }
        } catch (DocumentException | IOException e) {
            throw new PDFGenerationException("PDF generation of Shipping Labels failed", e);
        } finally {
            ITextUtils.closeQuietly(document);
        }
    }

    private void initProperties(ShippingLabelsDocumentParameter parameter) {
        textFont = getFont(printable.getTextSize(), false);
        smallTextFont = getFont(printable.getTextSize() - 1, false);
        titleFont = getFont(printable.getTitleSize(), true);
        loadId = parameter.getLoad().getId() == null ? parameter.getLoadId() : parameter.getLoad().getId();
    }

    private Element getShippingLabelContent(LoadEntity load, Image logo) {
        Paragraph content = new Paragraph();
        PdfPTable firstLine = firstLine(load.getOrigin(), logo,
                new float[] { .3f, .6f }, load.getOrigin().getContact(),
                ShipmentUtils.isBlindBol(load));
        firstLine.setWidthPercentage(HUNDRED_PERCENT);

        content.add(firstLine);

        PdfPTable secondLine = secondLine(load.getDestination(),
                new float[] { .6f, .8f }, load.getDestination().getContact());
        secondLine.setWidthPercentage(HUNDRED_PERCENT);
        if (printable.compareTo(Printable.TEMPLATE4) < 0) {
            firstLine.setSpacingAfter(30);
        }

        content.add(secondLine);

        addPhraseToParagraph(content, "Service Type: ", titleFont);
        content.add(generateCellForService(load.getOrigin()));

        content.add(Chunk.NEWLINE);
        if (printable.compareTo(Printable.TEMPLATE4) < 0) {
            content.add(Chunk.NEWLINE);
        }

        addPhraseToParagraph(content, "Notes: ", titleFont);
        if (StringUtils.isNotEmpty(load.getBolInstructions())) {
            String bolInstruction = printable.equals(Printable.DEFAULT_TEMPLATE)
                    ? load.getBolInstructions() : load.getBolInstructions().substring(0, Math.min(load.getBolInstructions().length(), 130));
            addPhraseToParagraph(content, StringUtils.defaultString(bolInstruction), textFont);
        }

        return content;
    }

    private void printSelectedDocument(Document document, LoadEntity load, Image logo) throws DocumentException {
        PdfPTable table = new PdfPTable(getNumberOfTableColumns());
        table.setWidthPercentage(100);
        PdfPCell cell;

        for (int i = 0; i < printable.getCells(); i++) {
            cell = new PdfPCell();
            cell.setBorder(0);
            cell.setBorderColor(BaseColor.LIGHT_GRAY);
            cell.setPaddingRight(10);
            cell.setPaddingLeft(10);
            cell.setPaddingTop(10);
            cell.setMinimumHeight(printable.getCellHeight());
            cell.addElement(getShippingLabelContent(load, logo));
            table.addCell(cell);
        }

        document.add(table);
    }

    private Font getFont(float size, boolean isBold) {
        return new Font(Font.FontFamily.HELVETICA, size,
                isBold ? Font.BOLD : Font.NORMAL);
    }

    private int getNumberOfTableColumns() {
        return printable.compareTo(Printable.TEMPLATE4) < 0 ? 1 : 2;
    }

    private Image getLogo(InputStream image) throws BadElementException, MalformedURLException, IOException {
        Image logo = null;
        if (image != null) {
            logo = Image.getInstance(IOUtils.toByteArray(image));
        }
        return logo;
    }

    private PdfPCell createCell(String content, Font font) {
        return ITextUtils.createCell(content, font, null, Rectangle.NO_BORDER);
    }

    private void addChunkToCell(PdfPCell cell, String text) {
        cell.addElement(new Chunk(text, textFont));
    }

    private void addChunkToCellSmall(PdfPCell cell, String text) {
        cell.addElement(new Chunk(text, smallTextFont));
    }

    private void addChunkToCellBold(PdfPCell cell, String text) {
        cell.addElement(new Chunk(text, titleFont));
    }

    private void addPhraseToParagraph(Paragraph content, String text, Font font) {
        content.add(new Chunk(text, font));
    }

    private PdfPCell generateCellForAddress(LoadDetailsEntity loadDetails, String addressName, String title) {
        PdfPCell result = createCell("", textFont);
        addChunkToCellBold(result, title);
        addChunkToCell(result, addressName);
        if (!StringUtils.isEmpty(loadDetails.getAddress().getAddress1())) {
            addChunkToCell(result, loadDetails.getAddress().getAddress1());
        }
        if (!StringUtils.isEmpty(loadDetails.getAddress().getAddress2())) {
            addChunkToCell(result, loadDetails.getAddress().getAddress2());
        }
        addChunkToCell(result, ShipmentUtils.getCityStateZip(loadDetails.getAddress()));
        addChunkToCell(result, buildContactNamePhoneInfoString(loadDetails));
        return result;
    }

    private PdfPCell generateCellForIdentifiers(LoadDetailsEntity loadDetails) {
        PdfPCell result = createCell("", smallTextFont);
        addChunkToCellBold(result, "IDENTIFIERS");
        addChunkToCellSmall(result, LOAD_ID_LABEL + getDefaultValueIfEmpty(loadId == null ? "" : String.valueOf(loadId)));
        addChunkToCellSmall(result, BOL_LABEL + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getBolNumber()));
        addChunkToCellSmall(result, PRO_LABEL
                + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getProNumber()));
        addChunkToCellSmall(result, PO_LABEL + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getPoNumber()));
        addChunkToCellSmall(result, SO_LABEL + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getSoNumber()));
        addChunkToCellSmall(result, PU_LABEL + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getPuNumber()));
        addChunkToCellSmall(result, SHIPPER_REF + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getRefNumber()));
        addChunkToCellSmall(result, TRAILER_LABEL
                + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getTrailerNumber()));
        addChunkToCellSmall(result, GL_LABEL + getDefaultValueIfEmpty(loadDetails.getLoad().getNumbers().getGlNumber()));
        return result;
    }

    private Chunk generateCellForService(LoadDetailsEntity loadDetails) {
        if (loadDetails.getLoad().getActiveCostDetail() != null
                && loadDetails.getLoad().getActiveCostDetail().getGuaranteedBy() != null) {
            return new Chunk(GUARANTEED_BY_LABEL
                    + ShipmentUtils.getGuaranteedTime(loadDetails.getLoad().getActiveCostDetail().getGuaranteedBy()),
                    textFont);
        } else {
            return new Chunk("Standard Service", textFont);
        }
    }

    private PdfPTable firstLine(LoadDetailsEntity loadDetails, Image image, float[] columnsWidths, String addressName,
            boolean isBlindBol) {
        PdfPTable table = new PdfPTable(columnsWidths);
        if (image != null) {
            PdfPCell cell = new PdfPCell(image);
            cell.setBorder(0);
            if (printable.getLogo().getWidth() < image.getWidth() || printable.getLogo().getHeight() < image.getHeight()) {
                image.scalePercent((float) (100 / Math.max(
                        image.getWidth() / printable.getLogo().getWidth(),
                        image.getHeight() / printable.getLogo().getHeight())));
            } else {
                image.scaleAbsolute(printable.getLogo().getWidth(), printable.getLogo().getHeight());
            }

            table.addCell(cell);
        } else {
            table.addCell(createCell("", textFont));
        }

        if (!isBlindBol) {
            table.addCell(generateCellForAddress(loadDetails, addressName, "SHIPPER"));
        } else {
            table.addCell(createCell("", textFont));
        }
        return table;
    }

    private PdfPTable secondLine(LoadDetailsEntity loadDetails, float[] columnsWidths, String addressName) {
        PdfPTable table = new PdfPTable(columnsWidths);
        PdfPCell cell = generateCellForAddress(loadDetails, addressName, "CONSIGNEE");
        if (loadDetails.getContactEmail() != null) {
            addChunkToCell(cell, loadDetails.getContactEmail());
        }
        table.addCell(cell);

        table.addCell(generateCellForIdentifiers(loadDetails));
        return table;
    }

    private String buildContactNamePhoneInfoString(LoadDetailsEntity loadDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.defaultString(loadDetails.getContactName()));
        if (StringUtils.isNotEmpty(loadDetails.getContactPhone())) {
            stringBuilder.append(", ");
            stringBuilder
                    .append(StringUtils.defaultString(PhoneUtils.formatPhoneNumber(loadDetails.getContactPhone())));
        }
        return stringBuilder.toString();
    }

    private String getDefaultValueIfEmpty(String value) {
        return StringUtils.isEmpty(value) ? NOT_SPECIFIED_LABEL : value;
    }
}
