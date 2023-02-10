package com.pls.shipment.service.pdf;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.service.ITextUtils;

/**
 * PDF Writer that handles Invoice generation for Safway customer users.
 *
 * @author Sergii Belodon
 */
@Component
public class ConsigneeInvoicePdfDocumentWriter implements PdfDocumentWriter<ConsigneeInvoiceDocumentParameter> {

    private static final String SHORT_DATE_FORMAT = "MM/dd/yy";
    private static final String SHIP_DATE_FORMAT = "MMM dd, yyyy";
    private final Font helvetica14 = new Font(Font.FontFamily.HELVETICA, 12);
    private final Font helvetica14Italic = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
    private final Font helvetica14Bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    @Value("/images/logo.jpg")
    private ClassPathResource defaultLogo;

    @Override
    public void write(ConsigneeInvoiceDocumentParameter parameter, OutputStream outputStream) throws PDFGenerationException {
        Document document = new Document();

        try {
            PdfWriter pdfWriter = ITextUtils.startWriting(document, new BufferedOutputStream(outputStream));
            pdfWriter.setFullCompression();

            writeInvoices(document, parameter);

            ITextUtils.closeQuietly(document);
        } catch (IOException e) {
            throw new PDFGenerationException("IO exception occurs during Invoice generation", e);
        } catch (DocumentException e) {
            throw new PDFGenerationException("PDF generation of Invoice failed", e);
        }
    }

    private void writeInvoices(Document document, ConsigneeInvoiceDocumentParameter documentParameter) throws IOException, DocumentException {
        Long markup = 0L;
        if (documentParameter.getLoad().getLoadAdditionalInfo() != null) {
            markup = documentParameter.getLoad().getLoadAdditionalInfo().getMarkup();
        }
        String amount = String.format("$%.2f", getFreightAmount(documentParameter.getLoad(), markup));
        document.add(writeFirstHeaderTable(documentParameter, amount));
        document.add(writeSecondHeaderTable(documentParameter.getLoad().getBillTo()));
        document.add(writeInfoTable(documentParameter, amount));
        document.add(writeFooterTable());
    }

    private PdfPTable writeFirstHeaderTable(ConsigneeInvoiceDocumentParameter documentParameter, String amount) throws IOException,
            DocumentException {
        PdfPTable headerTable = ITextUtils.createTable(new float[] {.25f, .35f, .15f});
        PdfPCell leftCell = new PdfPCell();
        InputStream logoInputStream = defaultLogo.getInputStream();
        Image img = Image.getInstance(IOUtils.toByteArray(logoInputStream));
        PdfPTable imgTable = new PdfPTable(1);
        imgTable.setWidthPercentage(100);
        imgTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        imgTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        imgTable.addCell(img);
        leftCell.addElement(imgTable);

        leftCell.setBorder(Rectangle.LEFT | Rectangle.TOP);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.RIGHT | Rectangle.TOP);
        Date currentDate = Calendar.getInstance().getTime();
        Date dueDate = DateUtility.addDays(currentDate, 15);
        String proNumber = documentParameter.getLoad().getId() == null ? "  " : ObjectUtils.toString(documentParameter.getLoad().getId());
        initRightAligmentText(rightCell, helvetica14,
                proNumber,
                DateUtility.dateToString(currentDate, SHORT_DATE_FORMAT),
                "15 days",
                DateUtility.dateToString(dueDate, SHORT_DATE_FORMAT),
                amount
                );
        headerTable.addCell(leftCell);
        PdfPCell middleCell = new PdfPCell();
        middleCell.setBorder(Rectangle.TOP);
        initRightAligmentText(middleCell, helvetica14Bold, "PLS Pro Number:", "Invoice Date:", "Payment Terms:", "Due Date:", "Freight Amount:");
        headerTable.addCell(middleCell);
        headerTable.addCell(rightCell);
        return headerTable;
    }

    private void initRightAligmentText(PdfPCell cell, Font font, String... contents) {
        for (String text : contents) {
            Paragraph element = new Paragraph(new Phrase(text, font));
            element.setAlignment(Element.ALIGN_RIGHT);
            cell.addElement(element);
            cell.setPaddingRight(10f);
        }
    }

    private PdfPTable writeSecondHeaderTable(BillToEntity billTo) {
        PdfPTable headerTable = ITextUtils.createTable(new float[] {.66f, .34f});
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT);
        leftCell.addElement(new Phrase("Invoice To:", helvetica14Bold));
        leftCell.addElement(new Phrase(StringUtils.defaultString(billTo.getName()), helvetica14));
        leftCell.addElement(new Phrase(StringUtils.defaultString(billTo.getBillingInvoiceNode().getAddress().getAddress1()), helvetica14));
        leftCell.addElement(new Phrase(buildCityStateZip(billTo.getBillingInvoiceNode().getAddress()), helvetica14));
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
        rightCell.addElement(new Phrase("Remit To:", helvetica14Bold));
        rightCell.addElement(new Phrase("Pittsburgh Logistics Systems, Inc.", helvetica14));
        rightCell.addElement(new Phrase("PO Box 778858", helvetica14));
        rightCell.addElement(new Phrase("Chicago, IL 60677-8858", helvetica14));
        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        return headerTable;
    }

    private String buildCityStateZip(AddressEntity address) {
        return address.getCity() + ", " + address.getState().getStatePK().getStateCode() + ", " + address.getZip();
    }

    private PdfPTable writeInfoTable(ConsigneeInvoiceDocumentParameter documentParameter, String amount) {
        PdfPTable infoTable = ITextUtils.createTable(new float[] {.20f, .35f, .45f});
        infoTable.setSpacingBefore(10f);
        LoadEntity load = documentParameter.getLoad();
        addRow(infoTable, "Shipment Number:", true, load.getNumbers().getRefNumber());
        float height = 10f;
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "General Ledger Number:", false, load.getNumbers().getGlNumber());
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Job #:", false, getJobNumbers(load.getNumbers().getJobNumbers()));
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "PO Number:", false, load.getNumbers().getPoNumber());
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Ship Date:", false, getShipDate(load));
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Origin:", false, load.getOrigin().getContact(), load.getOrigin().getAddress().getAddress1(),
                getCityStateZipString(load.getOrigin().getAddress()));
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Destination:", false, load.getDestination().getContact(), load.getDestination().getAddress().getAddress1(),
                getCityStateZipString(load.getDestination().getAddress()));
        addEmptyRowForInfoTable(infoTable, height);

        Set<LoadMaterialEntity> loadMaterials = load.getOrigin().getLoadMaterials();
        addProducts(infoTable, loadMaterials);
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Carrier Name:", false, load.getCarrier() != null ? load.getCarrier().getName() : "");
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Miles:", false, ObjectUtils.toString(load.getMileage()));
        addEmptyRowForInfoTable(infoTable, height);
        addRow(infoTable, "Freight Amount:", false, amount);
        int numberOfEmptyRows = 7;
        if (loadMaterials.size() < 6) {
            numberOfEmptyRows -= loadMaterials.size();
        }
        addEmptyRowForInfoTable(infoTable, height * numberOfEmptyRows);
        return infoTable;
    }

    private String getJobNumbers(Set<LoadJobNumbersEntity> jobNumbers) {
        List<String> jobNumbersList = new ArrayList<String>();
        if (jobNumbers != null) {
            for (LoadJobNumbersEntity loadJobNumbersEntity : jobNumbers) {
                jobNumbersList.add(loadJobNumbersEntity.getJobNumber());
            }
        }
        return String.join(", ", jobNumbersList);
    }

    private void addProducts(PdfPTable infoTable, Set<LoadMaterialEntity> loadMaterials) {
        SortedMap<String, BigDecimal> products = new TreeMap<String, BigDecimal>();
        for (LoadMaterialEntity product : loadMaterials) {
            if (product.getProductDescription() != null && !products.containsKey(product.getProductDescription())) {
                products.put(product.getProductDescription(), product.getWeight());
            }
        }
        boolean firstProduct = true;
        for (Map.Entry<String, BigDecimal> product : products.entrySet()) {
            String weight = "";
            if (product.getValue() != null) {
                weight = product.getValue().toString();
            }
            String productString = StringUtils.join(product.getKey(), " , ", weight, "LB");
            if (firstProduct) {
                addRow(infoTable, "BOL Commodity:", false, productString);
            } else {
                addRow(infoTable, "", false, productString);
            }
            firstProduct = false;
        }
    }

    private BigDecimal getFreightAmount(LoadEntity load, Long markup) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getTotalRevenue() != null) {
            BigDecimal totalRevenue = load.getActiveCostDetail().getTotalRevenue();
            BigDecimal markupPercentage = new BigDecimal(markup == null ? 0L : markup);
            return totalRevenue.add(totalRevenue.multiply(markupPercentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
        return BigDecimal.ZERO;
    }

    private String getCityStateZipString(AddressEntity address) {
        return StringUtils.join(address.getCity(), ", ",  address.getStateCode(), " ", address.getZip());
    }

    private String getShipDate(LoadEntity load) {
        if (load.getOrigin().getDeparture() != null) {
            return DateUtility.dateToString(load.getOrigin().getDeparture(), SHIP_DATE_FORMAT);
        } else {
            return DateUtility.dateToString(load.getOrigin().getEarlyScheduledArrival(), SHIP_DATE_FORMAT);
        }
    }

    private void addEmptyRowForInfoTable(PdfPTable infoTable, float height) {
        PdfPCell cell1 = new PdfPCell();
        cell1.setFixedHeight(height);
        cell1.setBorder(Rectangle.LEFT);
        infoTable.addCell(cell1);
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(cell2);
        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.RIGHT);
        infoTable.addCell(cell3);
    }

    private void addRow(PdfPTable infoTable, String labelText, boolean isFirstRow, String... contents) {
        int topBorderIfNeeded = 0;
        if (isFirstRow) {
            topBorderIfNeeded = Rectangle.TOP;
        }
        PdfPCell firstCell = new PdfPCell();
        firstCell.setBorder(Rectangle.LEFT | topBorderIfNeeded);
        infoTable.addCell(firstCell);

        PdfPCell labelCell = new PdfPCell();
        labelCell.setBorder(Rectangle.NO_BORDER | topBorderIfNeeded);
        labelCell.addElement(new Phrase(labelText, helvetica14Bold));
        infoTable.addCell(labelCell);

        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.RIGHT | topBorderIfNeeded);
        for (String text : contents) {
            textCell.addElement(new Phrase(text, helvetica14));
        }
        infoTable.addCell(textCell);
    }

    private PdfPTable writeFooterTable() {
        String text1 = "For proper credit, please reference our invoice number in your payment.\n"
                + "If you have questions related to this invoice, please send an email to ";
        String text2 = "2000 Westinghouse Drive, Suite 201, Cranberry Township, PA, 16066,";
        String email = "accountsreceivable@plslogistics.com";
        String site = "www.plslogistics.com";

        PdfPTable headerTable = ITextUtils.createTable(new float[] {1.0f});
        addFooterRow(text1, headerTable, Rectangle.LEFT | Rectangle.RIGHT);
        addFooterRow(getEmailPhrase(email), headerTable, Rectangle.LEFT | Rectangle.RIGHT);
        addFooterRow(text2, headerTable, Rectangle.LEFT | Rectangle.RIGHT);
        addFooterRow(getSitePhrase(site), headerTable, Rectangle.LEFT | Rectangle.RIGHT);
        addEmptyFooterRow(headerTable, Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT);
        return headerTable;
    }

    private void addEmptyFooterRow(PdfPTable headerTable, int border) {
        PdfPCell cell = new PdfPCell(new Phrase("", helvetica14));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(15f);
        cell.setBorder(border);
        headerTable.addCell(cell);
    }

    private void addFooterRow(String text1, PdfPTable headerTable, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(text1, helvetica14Italic));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        headerTable.addCell(cell);
    }

    private void addFooterRow(Phrase phrase, PdfPTable headerTable, int border) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(border);
        headerTable.addCell(cell);
    }
    private Phrase getEmailPhrase(String email) {
        Anchor anchor = new Anchor(email, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.UNDERLINE | Font.ITALIC, new BaseColor(0, 0, 255)));
        anchor.setName("LINK");
        anchor.setReference("mailto:" + email);

        Phrase emailPhrase = new Phrase();
        emailPhrase.add(anchor);
        return emailPhrase;
    }

    private Phrase getSitePhrase(String site) {
        Anchor anchor = new Anchor(site, FontFactory.getFont(FontFactory.HELVETICA, 14, Font.UNDERLINE | Font.ITALIC, new BaseColor(0, 0, 255)));
        anchor.setName("LINK");
        anchor.setReference(site);

        Phrase emailPhrase = new Phrase();
        emailPhrase.add(anchor);
        return emailPhrase;
    }
}
