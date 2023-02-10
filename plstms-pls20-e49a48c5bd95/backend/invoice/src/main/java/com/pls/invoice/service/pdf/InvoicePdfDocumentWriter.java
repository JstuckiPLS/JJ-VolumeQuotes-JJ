package com.pls.invoice.service.pdf;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.PhoneUtils;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PrepaidDetailEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;
import com.pls.shipment.service.ITextUtils;
import com.pls.shipment.service.ShipmentUtils;

/**
 * PDF Writer that handles Invoice generation.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class InvoicePdfDocumentWriter implements PdfDocumentWriter<InvoicePdfDocumentParameter> {

    public static final int PADDING_BETWEEN_TABLES = 20;

    private static final float HUNDRED_PERCENT = 100f;

    private static final String DATE_FORMAT = "EEE MM/dd/yyyy";

    private static final float DETAILS_SECTION_HEIGHT = 60f;

    private static final int WEIGHT_CLN_ORDER_NMB = 2;

    private static final float INVOICE_LABEL_HEIGHT = 30f;

    private static final float INFO_SECTION_HEIGHT = 12f;

    private static final String PREPAID_ONLY = "PREPAID_ONLY";

    private final Font helvetica6 = new Font(Font.FontFamily.HELVETICA, 6);

    private final Font helvetica8 = new Font(Font.FontFamily.HELVETICA, 8);

    private final Font helvetica8Bold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

    private final Font helvetica10 = new Font(Font.FontFamily.HELVETICA, 10);

    private final Font helvetica10Bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    private final Font helvetica12Bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    private final Font helvetica25BoldLightGray = new Font(
            Font.FontFamily.HELVETICA, 25f, Font.BOLD, BaseColor.LIGHT_GRAY);

    private static final String INVOICE_LABEL = "INVOICE";

    @Value("/images/logo.jpg")
    private ClassPathResource organizationLogo;

    private final Paragraph emailSignature = new Paragraph();
    private final Paragraph paymentSignature = new Paragraph();

    /**
     * Default constructor.
     */
    public InvoicePdfDocumentWriter() {
        emailSignature.setAlignment(Element.ALIGN_CENTER);
        emailSignature.add(new Chunk("If you have any questions about this invoice, please email: ", helvetica6));
        Anchor emailLinkAnchor = new Anchor("accountsreceivable@plslogistics.com", FontFactory.getFont(FontFactory.HELVETICA, 8, Font.UNDERLINE,
                new BaseColor(0, 0, 255)));
        emailLinkAnchor.setReference("mailto:accountsreceivable@plslogistics.com");
        emailSignature.add(emailLinkAnchor);

        paymentSignature.setAlignment(Element.ALIGN_RIGHT);
        paymentSignature.add(new Phrase("To pay by credit card, go to ", helvetica10Bold));
        Anchor paymentLink = new Anchor("https://plspay.plslogistics.com", FontFactory.getFont(FontFactory.HELVETICA, 10,
                Font.UNDERLINE | Font.BOLD, new BaseColor(0, 0, 255)));
        paymentLink.setReference("https://plspay.plslogistics.com");
        paymentSignature.add(paymentLink);
    }

    @Override
    public void write(InvoicePdfDocumentParameter documentParameter, OutputStream outputStream) throws PDFGenerationException {
        Document document = new Document();
        File tempFile = null;

        try {
            tempFile = File.createTempFile("tempInvoiceDocument", "tmp");
            PdfWriter pdfWriter = ITextUtils.startWriting(document, new BufferedOutputStream(new FileOutputStream(tempFile)));
            pdfWriter.setFullCompression();

            writeInvoices(document, pdfWriter, documentParameter);

            ITextUtils.closeQuietly(document);
        } catch (IOException e) {
            throw new PDFGenerationException("IO exception occurs during Invoice generation", e);
        } catch (DocumentException e) {
            throw new PDFGenerationException("PDF generation of Invoice failed", e);
        } finally {
            ITextUtils.addPagination(tempFile, document, outputStream);
        }
    }

    private void writeInvoices(Document document, PdfWriter pdfWriter, InvoicePdfDocumentParameter documentParameter)
            throws IOException, DocumentException {
        Image img = Image.getInstance(IOUtils.toByteArray(organizationLogo.getInputStream()));
        img.setWidthPercentage(45);

        boolean firstPage = true;
        for (LoadAdjustmentBO invoice : documentParameter.getInvoices()) {
            if (firstPage) {
                firstPage = false;
            } else {
                document.newPage();
            }
            LoadEntity load = invoice.getLoad();
            FinancialAccessorialsEntity adjustment = invoice.getAdjustment();
            String invoiceNumber = getInvoiceNumber(invoice);

            if (adjustment != null) {
                drawAdjustmentInvoiceWatermark(document, pdfWriter.getDirectContentUnder());
            }

            PdfContentByte pdfContentByte = pdfWriter.getDirectContent();

            NumberFormat currencyNumberFormat = getCurrencyFormat();

            writeHeaderSection(load, document, invoiceNumber);
            writeTopSection(document, load, adjustment, invoiceNumber, currencyNumberFormat, img);
            writeDetailsSection(load, document);
            writeMaterialsGrid(load, document);
            writeAmountSection(load, adjustment, document, currencyNumberFormat);
            writeLoadInfoSection(load, document);
            drawBottomAlignedSignature(document, pdfContentByte);
        }
    }

    private String getInvoiceNumber(LoadAdjustmentBO invoice) {
        return invoice.getAdjustment() == null ? invoice.getLoad().getActiveCostDetail().getInvoiceNumber() : invoice.getAdjustment()
                .getInvoiceNumber();
    }

    private void writeHeaderSection(LoadEntity load, Document document, String invoiceNumber) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(new float[] {.2f, .2f});
        String po = load.getNumbers().getPoNumber();

        fillHeaderCell(table, String.format("Invoice Number: %s", StringUtils.defaultString(invoiceNumber)));
        fillHeaderCell(table, String.format("PO Number: %s", StringUtils.defaultString(po)));

        document.add(table);
    }

    private void fillHeaderCell(PdfPTable headerTable, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setMinimumHeight(20);
        headerTable.addCell(cell);
    }

    private void writeTopSection(Document document, LoadEntity load, FinancialAccessorialsEntity adjustment, String invoiceNumber,
            NumberFormat currencyNumberFormat, Image img)
            throws DocumentException, IOException {

        PdfPTable table = ITextUtils.createTable(new float[] { .2f, .2f });
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(img);

        Chunk invoiceToChunk = new Chunk("Invoice To:", helvetica12Bold);
        invoiceToChunk.setUnderline(.8f, -2f);
        leftCell.addElement(invoiceToChunk);

        String contactName = null;
        String firstAddress = null;
        String cityStateZip = null;

        BillingInvoiceNodeEntity billingInvoiceNode = load.getBillTo().getBillingInvoiceNode();
        if (billingInvoiceNode != null) {
            AddressEntity billToAddress = billingInvoiceNode.getAddress();
            contactName = defaultString(billingInvoiceNode.getBillTo().getName());
            firstAddress = defaultString(billToAddress.getAddress1());
            cityStateZip = ShipmentUtils.getCityStateZip(billToAddress);
        }

        leftCell.addElement(new Phrase(contactName, helvetica10));
        leftCell.addElement(new Phrase(firstAddress, helvetica10));
        leftCell.addElement(new Phrase(cityStateZip, helvetica10));
        table.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        PdfPTable rightTable = ITextUtils.createTable(2);

        InvoicePdfDetails invoiceDetails = InvoiceUtils.getInvoiceGeneralInfo(load, adjustment);
        String invoiceDate = DateFormatUtils.format(invoiceDetails.getInvoiceDate(), DATE_FORMAT, Locale.US);
        String dueDate = DateFormatUtils.format(invoiceDetails.getDueDate(), DATE_FORMAT, Locale.US);
        String paymentsTerm = load.getBillTo().getPlsCustomerTerms() == null ? StringUtils.EMPTY
                : StringUtils.defaultString(load.getBillTo().getPlsCustomerTerms().getTermName());
        String totalInvoiceAmount = currencyNumberFormat.format(invoiceDetails.getAmountDue());

        Currency currencyCode = Currency.USD;
        if (load.getBillTo().getCurrency() != null) {
            currencyCode = load.getBillTo().getCurrency();
        }

        PdfPCell invoiceCell = ITextUtils.createCell(INVOICE_LABEL, helvetica25BoldLightGray, 0f, null);
        invoiceCell.setMinimumHeight(INVOICE_LABEL_HEIGHT);
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setColspan(2);
        rightTable.addCell(invoiceCell);

        addInvoiceGeneralInfoItem(rightTable, "Invoice Number: ", invoiceNumber);
        addInvoiceGeneralInfoItem(rightTable, "Invoice Date: ", invoiceDate);
        addInvoiceGeneralInfoItem(rightTable, "Payment Terms: ", paymentsTerm);
        addInvoiceGeneralInfoItem(rightTable, "Due Date: ", dueDate);

        if (adjustment == null && PREPAID_ONLY.equals(load.getBillTo().getPaymentMethod())
                && InvoiceType.TRANSACTIONAL == load.getBillTo().getInvoiceSettings().getInvoiceType()) {

            String prepaidAmt = currencyNumberFormat.format(getPrepaidAmount(load));
            String amountDueForPrepaid = currencyNumberFormat.format(invoiceDetails.getAmountDue().subtract(getPrepaidAmount(load)));

            addInvoiceGeneralInfoItem(rightTable, " ", " ");
            addInvoiceGeneralInfoItem(rightTable, "Invoice Amount: ", totalInvoiceAmount);
            addInvoiceGeneralInfoItem(rightTable, "Prepaid Amount: ", prepaidAmt);
            addInvoiceGeneralInfoItem(rightTable, "Amount Due: ", amountDueForPrepaid);
            addInvoiceGeneralInfoItem(rightTable, " ", " ");
        } else {
            addInvoiceGeneralInfoItem(rightTable, "Amount Due: ", totalInvoiceAmount);
        }

        addInvoiceGeneralInfoItem(rightTable, "Please pay in currency: ", currencyCode.toString());

        rightCell.addElement(rightTable);
        table.addCell(rightCell);

        document.add(table);
    }

    private BigDecimal getPrepaidAmount(LoadEntity load) {
        return load.getPrepaidDetails().stream().map(PrepaidDetailEntity::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private void addInvoiceGeneralInfoItem(PdfPTable table, String field, String content) {
        table.addCell(createInfoCell(field));
        table.addCell(createInfoCell(content));
    }

    private PdfPCell createInfoCell(String content) {
        PdfPCell cell = ITextUtils.createCell(content, helvetica10Bold, 0f, null);
        cell.setFixedHeight(INFO_SECTION_HEIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


    private void writeDetailsSection(LoadEntity load, Document document) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(HUNDRED_PERCENT);

        PdfPCell leftCell = ITextUtils.createCell("");
        PdfPCell rightCell = ITextUtils.createCell("");

        PdfPTable originTable = ITextUtils.createTable(new Phrase[] {new Phrase("Ship From:", helvetica8Bold)});

        PdfPCell originCell = ITextUtils.createCell("");

        Phrase[] originAddressDetails = getAddressDetails(load.getOrigin());
        for (Phrase originAddressDetail : originAddressDetails) {
            originCell.addElement(originAddressDetail);
        }

        originCell.setMinimumHeight(DETAILS_SECTION_HEIGHT);
        originCell.setVerticalAlignment(Element.ALIGN_CENTER);
        originCell.setPaddingBottom(5f);

        originTable.addCell(originCell);
        leftCell.addElement(originTable);
        mainTable.addCell(leftCell);

        PdfPTable destinationTable = ITextUtils.createTable(new Phrase[] {new Phrase("Ship To:", helvetica8Bold)});

        PdfPCell destinationCell = ITextUtils.createCell("");
        Phrase[] destinationAddressDetails = getAddressDetails(load.getDestination());
        for (Phrase details : destinationAddressDetails) {
            destinationCell.addElement(details);
        }

        destinationCell.setMinimumHeight(DETAILS_SECTION_HEIGHT);
        destinationCell.setVerticalAlignment(Element.ALIGN_CENTER);
        destinationCell.setPaddingBottom(5f);

        destinationTable.addCell(destinationCell);
        rightCell.addElement(destinationTable);
        mainTable.addCell(rightCell);

        document.add(mainTable);
    }

    private Phrase[] getAddressDetails(LoadDetailsEntity loadDetails) {
        Phrase[] addressDetails = new Phrase[5];

        addressDetails[0] = new Phrase(defaultString(loadDetails.getContact()), helvetica8Bold);
        addressDetails[1] = new Phrase(defaultString(loadDetails.getAddress().getAddress1()), helvetica8);
        addressDetails[2] = new Phrase(ShipmentUtils.getCityStateZip(loadDetails.getAddress()), helvetica8);
        addressDetails[3] = new Phrase(defaultString(loadDetails.getContactName()), helvetica8);
        addressDetails[4] = new Phrase(PhoneUtils.formatPhoneNumber(loadDetails.getContactPhone()), helvetica8);

        return addressDetails;
    }

    private void writeMaterialsGrid(LoadEntity load, Document document) throws DocumentException {

        LinkedHashMap<String, Float> columnsDefinitions = new LinkedHashMap<String, Float>();
        columnsDefinitions.put("Qty/Pkg Type", .1f);
        columnsDefinitions.put("Description", .62f);
        columnsDefinitions.put("Weight (Lbs)", .08f);
        columnsDefinitions.put("Dims (Inches)", .1f);
        columnsDefinitions.put("Class", .06f);

        PdfPTable table = ITextUtils.createTable(columnsDefinitions, helvetica8Bold);

        Set<LoadMaterialEntity> loadMaterials = load.getOrigin().getLoadMaterials();
        BigDecimal weightSum = BigDecimal.ZERO;
        for (LoadMaterialEntity loadMaterial : loadMaterials) {
            StringBuilder qtyWithPackageType = new StringBuilder("");

            if (StringUtils.isNotBlank(loadMaterial.getQuantity())) {
                qtyWithPackageType.append(loadMaterial.getQuantity());
            }

            if (loadMaterial.getPackageType() != null) {
                if (qtyWithPackageType.length() > 0) {
                    qtyWithPackageType.append('/');
                }

                qtyWithPackageType.append(loadMaterial.getPackageType().getDescription());
            }

            table.addCell(new Phrase(qtyWithPackageType.toString(), helvetica8));
            table.addCell(new Phrase(loadMaterial.getProductDescription(), helvetica8));

            BigDecimal weight = loadMaterial.getWeight();
            if (weight != null) {
                weightSum = weightSum.add(weight);
            }
            table.addCell(new Phrase(ObjectUtils.toString(weight), helvetica8));

            table.addCell(new Phrase(ShipmentUtils.getMaterialDimensions(loadMaterial), helvetica8));
            String commodityClass = "";
            if (loadMaterial.getCommodityClass() != null) {
                commodityClass = loadMaterial.getCommodityClass().getDbCode();
            }

            table.addCell(new Phrase(commodityClass, helvetica8));
        }

        fillMaterialTotalRow(columnsDefinitions, table, weightSum);

        document.add(table);
    }

    private void fillMaterialTotalRow(Map<String, Float> columnsDefinitions, PdfPTable table, BigDecimal weightSum) {
        int columnCount = columnsDefinitions.size();
        table.addCell(new Phrase("Total:", helvetica8Bold));
        for (int i = 1; i < columnCount; i++) {
            String content = "";
            if (i % columnCount == WEIGHT_CLN_ORDER_NMB) {
                content = weightSum.toString();
            }

            table.addCell(new Phrase(content, helvetica8Bold));
        }
    }

    private void writeAmountSection(LoadEntity load, FinancialAccessorialsEntity adjustment, Document document,
            NumberFormat currencyNumberFormat) throws DocumentException {

        LinkedHashMap<String, Float> columnsDefinitions = new LinkedHashMap<String, Float>();
        columnsDefinitions.put("Comments", .5f);
        columnsDefinitions.put("Unit", .3f);
        columnsDefinitions.put("Amount", .15f);

        PdfPTable amountTable = ITextUtils.createTable(columnsDefinitions, helvetica8Bold);
        amountTable.setWidthPercentage(HUNDRED_PERCENT);


        Set<CostDetailItemEntity> costDetailItems;
        BigDecimal totalRevenue;
        if (adjustment != null) {
            costDetailItems = new HashSet<CostDetailItemEntity>(adjustment.getCostDetailItems());
            totalRevenue = adjustment.getTotalRevenue();
        } else {
            costDetailItems = load.getActiveCostDetail().getCostDetailItems();
            totalRevenue = load.getActiveCostDetail().getTotalRevenue();
        }


        List<CostDetailItemEntity> sortedCDI = costDetailItems.stream()
                .filter(costDetailItem -> costDetailItem.getOwner() == CostDetailOwner.S && costDetailItem.getAccessorialType() != null)
                .sorted((o1, o2) -> {
                    if (o1.getAccessorialType().equals("SRA")) {
                        return -1;
                    }
                    if (o2.getAccessorialType().equals("SRA")) {
                        return 1;
                    }
                    return o1.getAccessorialType().compareTo(o2.getAccessorialType());
                }).collect(Collectors.toList());
        for (CostDetailItemEntity costDetailItem : sortedCDI) {
            amountTable.addCell(new Phrase(costDetailItem.getNote(), helvetica8Bold));
            amountTable.addCell(new Phrase(InvoiceUtils.getCostDetailsItemDescription(costDetailItem), helvetica8Bold));
            String subtotal = "";
            if (costDetailItem.getSubtotal() != null) {
                subtotal = currencyNumberFormat.format(costDetailItem.getSubtotal());
            }

            amountTable.addCell(new Phrase(subtotal, helvetica8));
        }

        String totalRevenueStr = "";
        if (totalRevenue != null) {
            totalRevenueStr = currencyNumberFormat.format(totalRevenue);
        }

        PdfPCell separatorCell = new PdfPCell(new Phrase(" ", helvetica8));
        separatorCell.setColspan(3);
        amountTable.addCell(separatorCell);

        PdfPCell emptyCell = new PdfPCell(new Phrase("", helvetica8Bold));
        amountTable.addCell(emptyCell);

        PdfPCell stringCell = new PdfPCell(new Phrase("Total Invoice:", helvetica8Bold));
        stringCell.setBorder(Rectangle.BOTTOM);
        amountTable.addCell(stringCell);

        PdfPCell totalCell = new PdfPCell(new Phrase(totalRevenueStr, helvetica8Bold));
        totalCell.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
        amountTable.addCell(totalCell);

        document.add(amountTable);
    }

    private NumberFormat getCurrencyFormat() {
        NumberFormat currencyNumberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        if (currencyNumberFormat instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) currencyNumberFormat;
            decimalFormat.setNegativePrefix("-$");
            decimalFormat.setNegativeSuffix("");
        }
        return currencyNumberFormat;
    }

    private void writeLoadInfoSection(LoadEntity load, Document document) throws DocumentException {
        LineSeparator separator = new LineSeparator();
        separator.setLineWidth(.5f);
        document.add(separator);

        PdfPTable table = ITextUtils.createTable(2);
        table.setSpacingBefore(10);

        PdfPCell leftCell = ITextUtils.createCell("");
        leftCell.setBorder(Rectangle.NO_BORDER);
        PdfPCell rightCell = ITextUtils.createCell("");
        rightCell.setBorder(Rectangle.NO_BORDER);

        PdfPTable loadInfoTable = ITextUtils.createTable(2);

        fillLoadInfoSection(loadInfoTable, String.format("Carrier Name: %s", StringUtils.defaultString(load.getCarrier().getName())), 2);
        fillLoadInfoSection(loadInfoTable, String.format("SCAC Code: %s", StringUtils.defaultString(load.getCarrier().getScac())), 2);
        fillLoadInfoSection(loadInfoTable, " ", 2);

        DateFormat slashedFormat = new SimpleDateFormat(DateUtility.SLASHED_DATE, Locale.US);
        String shipDateString = null;
        if (load.getActiveCostDetail().getShipDate() != null) {
            shipDateString = slashedFormat.format(load.getActiveCostDetail().getShipDate());
        }
        fillLoadInfoSection(loadInfoTable, String.format("Ship Date: %s", StringUtils.defaultString(shipDateString)), 2);
        fillLoadInfoSection(loadInfoTable, " ", 2);
        fillLoadInfoSection(loadInfoTable, String.format("PO Number: %s", StringUtils.defaultString(load.getNumbers().getPoNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("REF Number: %s", StringUtils.defaultString(load.getNumbers().getRefNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("PU Number: %s", StringUtils.defaultString(load.getNumbers().getPuNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("Order/SO#: %s", StringUtils.defaultString(load.getNumbers().getSoNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("Pro Number: %s", StringUtils.defaultString(load.getNumbers().getProNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("GL Code: %s", StringUtils.defaultString(load.getNumbers().getGlNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("BOL Number: %s", StringUtils.defaultString(load.getNumbers().getBolNumber())));
        fillLoadInfoSection(loadInfoTable, String.format("Trailer #: %s", StringUtils.defaultString(load.getNumbers().getTrailerNumber())));

        leftCell.addElement(loadInfoTable);
        table.addCell(leftCell);

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);

        paragraph.add(Chunk.NEWLINE);
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("Make Checks Payable To:", helvetica10Bold));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("Pittsburgh Logistics Systems, Inc.", helvetica10Bold));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("PO Box 778858", helvetica10));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("Chicago, IL 60677-8858", helvetica10));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("For proper credit, please reference our invoice number on your payment.", helvetica6));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Phrase("---or---", helvetica10Bold));

        rightCell.addElement(paragraph);
        table.addCell(rightCell);

        document.add(table);

        document.add(paymentSignature);
    }

    private void fillLoadInfoSection(PdfPTable loadInfoTable, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content, helvetica8Bold));
        cell.setBorder(Rectangle.NO_BORDER);
        loadInfoTable.addCell(cell);
    }

    private void fillLoadInfoSection(PdfPTable loadInfoTable, String content, int colSpan) {
        PdfPCell cell = new PdfPCell(new Phrase(content, helvetica8Bold));
        cell.setBorder(Rectangle.NO_BORDER);
        if (colSpan > 0 && colSpan <= loadInfoTable.getNumberOfColumns()) {
            cell.setColspan(colSpan);
        }
        loadInfoTable.addCell(cell);
    }

    private void drawBottomAlignedSignature(Document document, PdfContentByte pdfContentByte) {
        pdfContentByte.saveState();

        Rectangle rectangle = document.getPageSize();
        ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER, emailSignature, rectangle.getRight() / 2, rectangle.getBottom() + 75, 0);
        ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER, paymentSignature, rectangle.getRight() / 2, rectangle.getBottom() + 55, 0);
        pdfContentByte.restoreState();
    }

    private void drawAdjustmentInvoiceWatermark(Document document, PdfContentByte pdfContentByte) throws IOException, DocumentException {
        ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_RIGHT, new Phrase("ADJUSTMENT INVOICE",
                new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.LIGHT_GRAY)), document.right(), document.top(), 0f);
    }
}
