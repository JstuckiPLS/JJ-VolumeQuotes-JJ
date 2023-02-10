package com.pls.shipment.service.pdf;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.pdf.PdfDocumentWriter;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.core.service.util.PhoneUtils;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.service.ITextUtils;
import com.pls.shipment.service.ShipmentUtils;

/**
 * {@link PdfDocumentWriter} implementation for BOL generation.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class BolPdfDocumentWriter implements PdfDocumentWriter<BolPdfDocumentParameter> {
    private static final float HUNDRED_PERCENT = 100f;

    public static final float EIGHTY_EIGHT_PERCENT = 88;

    public static final float SIXTY_FIVE = 65;

    private static final float ZERO = 0f;

    private static final String PREVIEW_LABEL = "PREVIEW";

    private static final float ADDRESS_SECTION_LEFT_PADDING = 10f;

    private static final String DATE_FORMAT = "EEE MM/dd/yy";

    private static final String DATE_TIME_FORMAT = "MM/dd/yy h:mm a";

    private static final String SHORT_DATE_TIME_FORMAT = "MM/dd/yy";

    private static final float DETAILS_SECTION_HEIGHT = 80f;

    private static final float INFO_SECTION_HEIGHT = 12f;

    private static final float CARRIER_NAME_HEIGHT = 60f;

    private static final int QTY_CLN_ORDER_NMB = 1;

    private static final int PIECES_CLN_ORDER_NMB = 2;

    private static final int WEIGHT_CLN_ORDER_NMB = 6;

    private static final float HAZMAT_SECTION_HEIGHT = 70f;

    private static final String SEPARATOR = ", ";

    private static final String EMERGENCY_CONTACT_LABEL = "EMERGENCY CONTACT: ";

    private final Font helvetica12Bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    private final Font helvetica12 = new Font(Font.FontFamily.HELVETICA, 12);

    private final Font helvetica6 = new Font(Font.FontFamily.HELVETICA, 6);

    private final Font helvetica8 = new Font(Font.FontFamily.HELVETICA, 8);

    private final Font helvetica10Bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    private final Font helvetica8Bold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

    private final Font arial10 = FontFactory.getFont("Arial", 10);

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContactInfoService contactInfoService;

    @Value("/images/logo.jpg")
    private ClassPathResource defaultLogo;

    @Value("/images/logo-goship.png")
    private ClassPathResource defaultGoShipLogo;
    
    @Autowired
    private LtlProfileDetailsService ltlProfileDetailsService;

    @Override
    public void write(BolPdfDocumentParameter parameter, OutputStream outputStream) throws PDFGenerationException {
        LoadEntity load = parameter.getLoad();
        UserEntity currentUser = parameter.getCurrentUser();
        InputStream logo = parameter.getLogo();
        boolean preview = parameter.isPreview();
        boolean isManualBol = parameter.isManualBolFlag();
        String paymentTerms = isManualBol ? load.getPayTerms().getDescription() : "Third Party";
        String custName = null;
        if(load.getBillTo()!= null) {
        	custName = load.getBillTo().getName();
        }

        File tempFile = null;

        String carrierName = "";
        if (load.getCarrier() != null) {
            carrierName = load.getCarrier().getName();
        }

        Document document = new Document();

        try {
            tempFile = File.createTempFile("tempBolDocument", "tmp");
            PdfWriter pdfWriter = ITextUtils.startWriting(document, new BufferedOutputStream(new FileOutputStream(
                    tempFile)));

            PdfContentByte pdfContentByte = pdfWriter.getDirectContent();

            if (preview) {
                PageEventHelper event = new PageEventHelper();
                event.setPdfContentByte(pdfContentByte);

                pdfWriter.setPageEvent(event);
            }

            StartPageEventHelper event = new StartPageEventHelper();
            event.setLoad(load);
            pdfWriter.setPageEvent(event);

            writeTopSection(pdfContentByte, load, document, carrierName, logo);

            writeDetailsSection(load, document, custName);

            writeMaterialsGrid(load, document);
            
            writeQuoteNumberSection(load, document);

            writeCommentsSection(load, document);

            writeMiscSection(paymentTerms, document, pdfContentByte);

            writeSignaturesSection(carrierName, document, pdfContentByte, load, custName);

            writeSertificationsSection(document);

            writeReceiptOfShipmentSection(document);

            writeBottomSection(document);

            writeContactInfoSection(document, parameter.isHideShipmentCreatedTime(), currentUser,
                    load.getModification());
            
            if(load.getOrganization().getId() == 299666) {
            	writeHazmatSection(document, pdfContentByte);
            }
        } catch (IOException e) {
            throw new PDFGenerationException("IO exception occurs during BOL generation", e);
        } catch (DocumentException e) {
            throw new PDFGenerationException("PDF generation of BOL failed", e);
        } finally {
            ITextUtils.closeQuietly(document);
            ITextUtils.addPagination(tempFile, document, outputStream);
        }
    }

    private void writeHeaderSection(LoadEntity load, Document document) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(new float[] { .3f, .3f, .3f, .3f });
        String bol = load.getNumbers().getBolNumber();
        String po = load.getNumbers().getPoNumber();
        String so = load.getNumbers().getSoNumber();
        String ref = load.getNumbers().getRefNumber();

        fillHeaderCell(table, String.format("BOL #: %s", StringUtils.defaultString(bol)));
        fillHeaderCell(table, String.format("PO #: %s", StringUtils.defaultString(po)));
        fillHeaderCell(table, String.format("SO #: %s", StringUtils.defaultString(so)));
        fillHeaderCell(table, String.format("Shipper Ref %s", StringUtils.defaultString(ref)));

        document.add(table);
    }

    private void fillHeaderCell(PdfPTable headerTable, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setMinimumHeight(20);
        headerTable.addCell(cell);
    }

    private void writeTopSection(PdfContentByte pdfContentByte, LoadEntity load, Document document,
            String carrierName, InputStream logo) throws DocumentException, IOException {
        String bolNumber = StringUtils.defaultString(load.getNumbers().getBolNumber());

        PdfPTable table = ITextUtils.createTable(new float[] { .4f, .4f, .4f });

        PdfPCell cell1 = new PdfPCell();
        cell1.setPadding(0);
        if (isPrintBarcode(load, bolNumber)) {
            Image barcode = ITextUtils.createBarcode(pdfContentByte, bolNumber);
            if (barcode.getWidth() > (PageSize.A4.getWidth() / 3)) {
                if (barcode.getWidth() > (PageSize.A4.getWidth() / 100 * SIXTY_FIVE)) {
                    barcode.scaleAbsoluteWidth(PageSize.A4.getWidth() / 100 * EIGHTY_EIGHT_PERCENT);
                }
                document.add(barcode);
            } else {
                cell1.addElement(ITextUtils.createEmptyLine(1));
                cell1.addElement(new Phrase(new Chunk(barcode, 0, 0)));
            }
        }
        cell1.addElement(new Phrase("BILL OF LADING", helvetica12Bold));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.addElement(new Phrase(new Phrase("Non-Negotiable", helvetica12)));
        cell1.addElement(ITextUtils.createEmptyLine(1));

        PdfPTable cell1BolNumberTable = new PdfPTable(1);
        cell1BolNumberTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell bolNumberCell = new PdfPCell(new Phrase(String.format("BOL No: %s", bolNumber), helvetica10Bold));
        bolNumberCell.setMinimumHeight(20);
        cell1BolNumberTable.addCell(bolNumberCell);

        cell1.addElement(cell1BolNumberTable);

        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setFixedHeight(70);

        InputStream logoInputStream = logo != null ? logo
                : load.getOriginatingSystem().equals("GS") ? defaultGoShipLogo.getInputStream() : defaultLogo.getInputStream();
        Image img = Image.getInstance(IOUtils.toByteArray(logoInputStream));
        PdfPTable imgTable = new PdfPTable(1);
        imgTable.setWidthPercentage(70);
        imgTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        imgTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        imgTable.addCell(img);
        cell2.addElement(imgTable);

        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.NO_BORDER);
        PdfPTable cell3BottomTable = new PdfPTable(1);
        cell3BottomTable.setSpacingAfter(ZERO);
        cell3BottomTable.setSpacingBefore(ZERO);
        cell3BottomTable.setWidthPercentage(HUNDRED_PERCENT);
        cell3BottomTable.addCell("ADD PRO STICKER HERE");

        PdfPCell carrierNameCell = new PdfPCell();
        Paragraph carrierParagraph = new Paragraph(carrierName, helvetica12Bold);
        carrierParagraph.setAlignment(Element.ALIGN_CENTER);
        carrierNameCell.addElement(carrierParagraph);
        carrierNameCell.setMinimumHeight(CARRIER_NAME_HEIGHT);
        carrierNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3BottomTable.addCell(carrierNameCell);

        cell3.addElement(cell3BottomTable);
        cell3.setPadding(0);
        table.addCell(cell3);

        document.add(table);
    }

    private boolean isPrintBarcode(LoadEntity load, String bolNumber) {
        return load.getOrganization() != null
                && BooleanUtils.isTrue(load.getOrganization().isPrintBarcode())
                && StringUtils.isNotEmpty(bolNumber);
    }

    private void writeDetailsSection(LoadEntity load, Document document) throws DocumentException {
    	writeDetailsSection(load, document, null);
    }
    
    private void writeDetailsSection(LoadEntity load, Document document, String custName) throws DocumentException {
        String emergencyCompany = null;
        PhoneEmbeddableObject emergencyPhone = null;
        String emergencyContract = null;
        boolean isHazmat = false;
        
        Phrase[] headerNames = { new Phrase("Shipper Information (Origin):", helvetica8Bold),
                new Phrase("Reference Information:", helvetica8Bold) };
        PdfPTable table = ITextUtils.createTable(headerNames);
        table.setWidths(new float[] { .3f, .5f });
        PdfPCell leftCell = new PdfPCell();
        leftCell.setPaddingLeft(ADDRESS_SECTION_LEFT_PADDING);

        if (!ShipmentUtils.isBlindBol(load)) {
            Phrase[] originAddressDetails = getAddressDetails(load.getOrigin(), false);
            for (Phrase originAddressDetail : originAddressDetails) {
                leftCell.addElement(originAddressDetail);
            }
        }

        leftCell.setMinimumHeight(DETAILS_SECTION_HEIGHT);
        table.addCell(leftCell);
        PdfPCell rightCell = new PdfPCell();
        rightCell.setPadding(0);
        writeRefInfSection(load, rightCell);
        table.addCell(rightCell);
        document.add(table);
        
    	List<LoadMaterialEntity> loadMaterials = new ArrayList<LoadMaterialEntity>(load.getOrigin().getLoadMaterials());
        if(load.getOrganization().getId() == 299666) 
        {
            for (LoadMaterialEntity loadMaterial : loadMaterials) 
            {
            	if(loadMaterial.getEmergencyCompany() != null) {
                	emergencyCompany = loadMaterial.getEmergencyCompany();	
            	}
            	if(loadMaterial.getEmergencyPhone() != null) {
            		emergencyPhone = loadMaterial.getEmergencyPhone();
            	}
            	if(loadMaterial.getEmergencyContract() != null) {
            		emergencyContract = loadMaterial.getEmergencyContract();
            	}
            	isHazmat = (loadMaterial.isHazmat() || isHazmat);
            	
            }
        }        
        
        List<Phrase> bottomHeaderNames = new ArrayList<Phrase>();
    	bottomHeaderNames.add(new Phrase("Consignee Information (Destination):", helvetica8Bold));
    	bottomHeaderNames.add(new Phrase("Bill To Information:", helvetica8Bold));
    	if(isHazmat) {
    		bottomHeaderNames.add(new Phrase("Hazmat Information:", helvetica8Bold));
    	}
        PdfPTable bottomTable = ITextUtils.createTable(bottomHeaderNames.toArray(new Phrase[bottomHeaderNames.size()]));
        if(isHazmat) {
        	bottomTable.setWidths(new float[] { .4f, .3f , .3f });
        }
        else {
        	bottomTable.setWidths(new float[] { .3f, .5f });
        }
        PdfPCell leftBottomCell = new PdfPCell();
        leftBottomCell.setPaddingLeft(ADDRESS_SECTION_LEFT_PADDING);

        Phrase[] destinationAddressDetails = getAddressDetails(load.getDestination(), true);
        for (Phrase details : destinationAddressDetails) {
            leftBottomCell.addElement(details);
        }
        bottomTable.addCell(leftBottomCell);

        PdfPCell centerBottomCell = new PdfPCell();
        centerBottomCell.setPaddingLeft(ADDRESS_SECTION_LEFT_PADDING);

        Phrase[] billToInformationPhrases = getBillToInformation(load);
        for (Phrase phrase : billToInformationPhrases) {
            centerBottomCell.addElement(phrase);
        }
        bottomTable.addCell(centerBottomCell);
        
        if(isHazmat) {
        	PdfPCell hazmatCell = new PdfPCell();
            Phrase[] hazmatInfo = {
            		new Phrase("FOR CHEMICAL EMERGENCY", helvetica8Bold),
            		new Phrase("CALL: " + emergencyCompany + ": (" + emergencyPhone.getAreaCode() + 
            				") " + emergencyPhone.getNumber(), helvetica8Bold),
            		new Phrase("DAY OR NIGHT", helvetica8Bold),
            		new Phrase("CONTRACT # " + emergencyContract, helvetica8Bold)};
            hazmatCell.setPaddingLeft(ADDRESS_SECTION_LEFT_PADDING);   
            for (Phrase phrase : hazmatInfo) {
                hazmatCell.addElement(phrase);
            }
            bottomTable.addCell(hazmatCell);
        }
        document.add(bottomTable);
    }

    /** Populates informational block for Bill of Lading. */ 
    private void writeRefInfSection(LoadEntity load, PdfPCell rightCell) {
        PdfPTable refInfTable = ITextUtils.createTable(2);
        String po = load.getNumbers().getPoNumber();
        String pu = load.getNumbers().getPuNumber();
        String so = load.getNumbers().getSoNumber();
        String trailerNumber = load.getNumbers().getTrailerNumber();
        String ref = load.getNumbers().getRefNumber();
        String cargo = StringUtils.EMPTY;
        if (load.getLoadAdditionalFields() != null && load.getLoadAdditionalFields().getCargoValue() != null && load.getCarrier() != null) {
            StringBuilder cargoBuilder = new StringBuilder(load.getLoadAdditionalFields().getCargoValue().toString());
            cargoBuilder.append(' ').append(load.getCarrier().getCurrencyCode());
            cargo = String.format("Cargo Value: %s", StringUtils.defaultString(cargoBuilder.toString()));
        }
        String shipDate = null;
        if (load.getOrigin() != null) {
            Date pickupDate = load.getOrigin().getEarlyScheduledArrival();
            if (pickupDate != null) {
                shipDate = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(pickupDate);
            }
        }

        String serviceLevelLabel;
        LoadCostDetailsEntity costDetail = load.getActiveCostDetail();
        if (costDetail != null && costDetail.getGuaranteedBy() != null) {
            serviceLevelLabel = getGuaranteedByTimeLabel(costDetail);
        } else if (load.getNumbers().getServiceLevelCode() != null) {
            serviceLevelLabel = load.getNumbers().getServiceLevelDescription() != null ? load.getNumbers().getServiceLevelDescription() : load.getNumbers().getServiceLevelCode();
        } else {
            serviceLevelLabel = "Standard Service";
        }
        
        fillRefInfSection(refInfTable, String.format("PO Number: %s", StringUtils.defaultString(po)));
        fillRefInfSection(refInfTable, String.format("Ship Date: %s", StringUtils.defaultString(shipDate)));
        fillRefInfSection(refInfTable, String.format("Pick-Up Number: %s", StringUtils.defaultString(pu)));
        fillRefInfSection(refInfTable, arial10, serviceLevelLabel);
        fillRefInfSection(refInfTable, String.format("SO Number: %s", StringUtils.defaultString(so)));      
        fillRefInfSection(refInfTable, String.format("Trailer Number: %s", StringUtils.defaultString(trailerNumber)));
        
        fillRefInfSection(refInfTable, String.format("Shipper Ref Number: %s", StringUtils.defaultString(ref)));
        fillRefInfSection(refInfTable, ShipmentUtils.getEstimatedTransitDaysLabel(load.getTravelTime()));
        fillPickupAndDeliveryWindowInfoCell(refInfTable, load.getOrigin(), load.getDestination());
        fillRefInfSection(refInfTable, cargo);

        rightCell.addElement(refInfTable);
    }

    private String getGuaranteedByTimeLabel(LoadCostDetailsEntity costDetail) {
        String guaranteedByTime = ShipmentUtils.getGuaranteedTime(costDetail.getGuaranteedBy());
        String guaranteedName = costDetail.getGuaranteedNameForBOL() == null ? "Guaranteed Delivery" : costDetail
                .getGuaranteedNameForBOL();
        return guaranteedName + String.format(" (by %s)", guaranteedByTime);
    }
    
    private void fillRefInfSection(PdfPTable refInfTable, String content) {
    	fillRefInfSection(refInfTable, content, 0);
    }

    private void fillRefInfSection(PdfPTable refInfTable, String content, int rowSpan) {
        PdfPCell cell = ITextUtils.createCell(content, helvetica8Bold, 0f, null);
        cell.setPaddingLeft(5f);
        cell.setPaddingBottom(4f);
        cell.setMinimumHeight(INFO_SECTION_HEIGHT);
        if (rowSpan > 0) {
        	cell.setRowspan(rowSpan);
        }
        	
        refInfTable.addCell(cell);
    }

    private void fillRefInfSection(PdfPTable refInfTable, Font font, String content) {
        PdfPCell cell = ITextUtils.createCell(content, font, 0f, null);
        cell.setPaddingLeft(5f);
        cell.setMinimumHeight(INFO_SECTION_HEIGHT);
        cell.setRowspan(2);
        refInfTable.addCell(cell);
    }

    private Phrase[] getAddressDetails(LoadDetailsEntity loadDetails, boolean showCustomsBroker) {
        Phrase[] addressDetails = new Phrase[7];
        String address2 = loadDetails.getAddress().getAddress2() != null ? ", "
                + loadDetails.getAddress().getAddress2() : "";
        String address3 = loadDetails.getContactName() + " " + loadDetails.getContactPhone();

        addressDetails[0] = new Phrase(defaultString(loadDetails.getContact()), helvetica8Bold);
        addressDetails[1] = new Phrase(defaultString(loadDetails.getAddress().getAddress1() + address2), helvetica8);
        addressDetails[2] = new Phrase(ShipmentUtils.getCityStateZip(loadDetails.getAddress()), helvetica8);

        addressDetails[3] = new Phrase(defaultString(address3), helvetica8);
        addressDetails[4] = new Phrase(defaultString(loadDetails.getContactEmail()), helvetica8);

        if (loadDetails.getLoad() != null && ShipmentUtils.showCustomsBroker(loadDetails.getLoad())
                && showCustomsBroker) {
            addressDetails[5] = new Phrase(new Chunk("Customs Broker: ", helvetica8Bold));
            addressDetails[5].add(new Chunk(StringUtils.defaultString(loadDetails.getLoad().getCustomsBroker()),
                    helvetica8));
            addressDetails[6] = new Phrase(new Chunk("Phone: ", helvetica8));
            addressDetails[6].add(new Chunk(StringUtils.defaultString(loadDetails.getLoad().getCustomsBrokerPhone()),
                    helvetica8));
        }

        return addressDetails;
    }

    private void fillPickupAndDeliveryWindowInfoCell(PdfPTable refInfTable, LoadDetailsEntity origin,
            LoadDetailsEntity destination) {
        StringBuilder pickupWindowInfo = new StringBuilder("Pickup Window: ");
        fillPickupAndDeliveryWindow(pickupWindowInfo, origin.getArrivalWindowStart());
        pickupWindowInfo.append(" - ");
        fillPickupAndDeliveryWindow(pickupWindowInfo, origin.getArrivalWindowEnd());
        StringBuilder deliveryWindowInfo = new StringBuilder("Delivery Hours: ");
        fillPickupAndDeliveryWindow(deliveryWindowInfo, destination.getArrivalWindowStart());
        deliveryWindowInfo.append(" - ");
        fillPickupAndDeliveryWindow(deliveryWindowInfo, destination.getArrivalWindowEnd());

        PdfPTable table = ITextUtils.createTable(1);

        PdfPCell cellPickup = ITextUtils.createCell(pickupWindowInfo.toString(), helvetica8, 0f, null);
        cellPickup.setPaddingLeft(4f);
        cellPickup.setFixedHeight(INFO_SECTION_HEIGHT - 1);
        cellPickup.setBorder(0);
        table.addCell(cellPickup);

        PdfPCell cellDelivery = ITextUtils.createCell(deliveryWindowInfo.toString(), helvetica8, 0f, null);
        cellDelivery.setPaddingLeft(4f);
        cellDelivery.setFixedHeight(INFO_SECTION_HEIGHT - 1);
        cellDelivery.setBorder(0);
        table.addCell(cellDelivery);

        refInfTable.addCell(table);
    }

    private void fillPickupAndDeliveryWindow(StringBuilder builder, Date pickupWindow) {
        if (pickupWindow != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pickupWindow);
            builder.append(ShipmentUtils.formatAmPm(calendar.getTime()));
        }
    }

    private Phrase[] getBillToInformation(LoadEntity load) {
        Phrase[] billToPhrases = new Phrase[6];

        billToPhrases[0] = new Phrase(defaultString(load.getFreightBillPayTo().getCompany()), helvetica8Bold);
        billToPhrases[1] = new Phrase(defaultString(load.getFreightBillPayTo().getAddress().getAddress1()), helvetica8);
        billToPhrases[2] = new Phrase(ShipmentUtils.getCityStateZip(load.getFreightBillPayTo().getAddress()),
                helvetica8);
        billToPhrases[3] = new Phrase(defaultString(load.getFreightBillPayTo().getContactName()), helvetica8);
        if (load.getVolumeQuoteId() != null) {
            billToPhrases[4] = new Phrase("QUOTE ID: " + defaultString(load.getVolumeQuoteId()), helvetica8);
        }

        return billToPhrases;
    }

    private void writeMaterialsGrid(LoadEntity load, Document document) throws DocumentException {

        Map<String, Float> columnsDefinitions = getMaterialsColumnsDefinition();
        PdfPTable table = ITextUtils.createTable(columnsDefinitions, helvetica8Bold);

        List<LoadMaterialEntity> loadMaterials = new ArrayList<LoadMaterialEntity>(load.getOrigin().getLoadMaterials());
        Collections.sort(loadMaterials, new Comparator<LoadMaterialEntity>() {
            @Override
            public int compare(LoadMaterialEntity l1, LoadMaterialEntity l2) {
                if (l1.isHazmat() ^ l2.isHazmat()) {
                    return l1.isHazmat() ? -1 : 1;
                } else {
                    return ObjectUtils.compare(l1.getProductDescription(), l2.getProductDescription());
                }
            }
        });

        for (LoadMaterialEntity loadMaterial : loadMaterials) {
            addMaterialRow(table, loadMaterial, load);
        }
        fillMaterialTotalRow(columnsDefinitions, table, getTotalQty(loadMaterials), getTotalWeight(loadMaterials), getTotalPieces(loadMaterials));
        document.add(table);
    }

    private Map<String, Float> getMaterialsColumnsDefinition() {
        LinkedHashMap<String, Float> columnsDefinitions = new LinkedHashMap<String, Float>();
        columnsDefinitions.put("Handling Units", .07f);
        columnsDefinitions.put("Qty", .05f);
        columnsDefinitions.put("Pcs", .05f);
        columnsDefinitions.put("Stack", .05f);
        columnsDefinitions.put("HM", .03f);
        columnsDefinitions.put("Description", .37f);
        columnsDefinitions.put("Weight (Lbs)", .06f);
        columnsDefinitions.put("Dimensions (Inches)", .10f);
        columnsDefinitions.put("Class", .05f);
        columnsDefinitions.put("NMFC#", .10f);
        return columnsDefinitions;
    }

    private void addMaterialRow (PdfPTable table, LoadMaterialEntity loadMaterial) {
    	addMaterialRow(table, loadMaterial, null);
    }
    
    private void addMaterialRow(PdfPTable table, LoadMaterialEntity loadMaterial, LoadEntity load) {
        String packageType = loadMaterial.getPackageType() != null ? loadMaterial.getPackageType().getDescription()
                : "";
        table.addCell(new Phrase(packageType, helvetica8));
        table.addCell(new Phrase(loadMaterial.getQuantity(), helvetica8));
        table.addCell(new Phrase(ObjectUtils.toString(loadMaterial.getPieces()), helvetica8));
        table.addCell(new Phrase(writeBooleanFlag(loadMaterial.isStackable()), helvetica8));
        table.addCell(new Phrase(writeBooleanFlag(loadMaterial.isHazmat()), helvetica8));
        table.addCell(new Phrase(getProductDescription(loadMaterial, load), helvetica10Bold));
        table.addCell(new Phrase(ObjectUtils.toString(loadMaterial.getWeight()), helvetica8));
        table.addCell(new Phrase(ShipmentUtils.getMaterialDimensions(loadMaterial), helvetica8));
        String commodityClass = "";
        if (loadMaterial.getCommodityClass() != null) {
            commodityClass = loadMaterial.getCommodityClass().getDbCode();
        }
        table.addCell(new Phrase(commodityClass, helvetica8));
        table.addCell(new Phrase(loadMaterial.getNmfc(), helvetica8));
    }

    private String writeBooleanFlag(boolean flag) {
        return flag ? "X" : "";
    }

    private BigDecimal getTotalWeight(List<LoadMaterialEntity> loadMaterials) {
        return loadMaterials.stream().map(LoadMaterialEntity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getTotalQty(List<LoadMaterialEntity> loadMaterials) {
        return loadMaterials.stream().mapToInt(material -> NumberUtils.toInt(material.getQuantity())).sum();
    }
    private int getTotalPieces(List<LoadMaterialEntity> loadMaterials) {
        return loadMaterials.stream()
                .filter(material -> material.getPieces() != null)
                .mapToInt(material -> material.getPieces())
                .sum();
    }
    
    private String getProductDescription(LoadMaterialEntity loadMaterial, LoadEntity load) {
        StringBuilder description = new StringBuilder();
        Long orgId = new Long(0);
        if(load != null) {
        	orgId = load.getOrganization().getId();
        }

        if(loadMaterial != null) {
        	if(loadMaterial.getUnNumber() != null) {
            	description.append(loadMaterial.getUnNumber()).append(SEPARATOR);
            }

            description.append(loadMaterial.getProductDescription());

            if(loadMaterial.getHazmatClass()!= null) {
            	description.append(SEPARATOR).append(loadMaterial.getHazmatClass()).append(SEPARATOR);
            }
            
            if (loadMaterial.getPackingGroup() != null) {
                description.append(loadMaterial.getPackingGroup());
            }
            if (loadMaterial.isHazmat() && orgId != 299666) {
            	description.append(SEPARATOR).append(EMERGENCY_CONTACT_LABEL).append(PhoneUtils.format(loadMaterial.getEmergencyPhone()))
                    .append(SEPARATOR).append(loadMaterial.getEmergencyCompany()).append(" - Contract # ")
                    .append(loadMaterial.getEmergencyContract());
            }
        }
        
        return description.toString();
    }

    private void fillMaterialTotalRow(Map<String, Float> columnsDefinitions, PdfPTable table, int qtySum,
            BigDecimal weightSum, int qtyPieces) {
        table.addCell(new Phrase("Total:", helvetica8Bold));
        for (int columnIndex = 1; columnIndex < columnsDefinitions.size(); columnIndex++) {
            String content;
            switch (columnIndex) {
            case QTY_CLN_ORDER_NMB:
                content = String.valueOf(qtySum);
                break;
            case WEIGHT_CLN_ORDER_NMB:
                content = weightSum.toString();
                break;
            case PIECES_CLN_ORDER_NMB:
                content = qtyPieces == 0 ? "" : Integer.toString(qtyPieces);
                break;
            default:
                content = "";
                    break;
            }
            table.addCell(new Phrase(content, helvetica8Bold));
        }
    }

    /** Write the carrier quote number to bol if available. */
    private void writeQuoteNumberSection(LoadEntity load, Document document) throws DocumentException {
        if (load.getNumbers().getCarrierQuoteNumber() != null && isQuoteNumberOnBolActivated(load)) {

            PdfPTable table = ITextUtils.createTable(1, null); // to synchronize sizes with previous table
            PdfPCell leftCell = new PdfPCell();
            leftCell.setMinimumHeight(INFO_SECTION_HEIGHT);
            leftCell.addElement(new Chunk("Quote number: " + (load.getVolumeQuoteId() != null ? load.getVolumeQuoteId() : load.getNumbers().getCarrierQuoteNumber()), helvetica8));
            table.addCell(leftCell);

            document.add(table);
        }
    }
    
    private boolean isQuoteNumberOnBolActivated(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            LtlPricingProfileEntity profile = ltlProfileDetailsService.getProfileById(load.getActiveCostDetail().getPricingProfileDetailId());
            return Boolean.TRUE.equals(profile.getDisplayQuoteNumberOnBol());
        }
        return false;
    }

    private void writeCommentsSection(LoadEntity load, Document document) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(1, null); // to synchronize sizes with previous table
        PdfPCell leftCell = new PdfPCell();
        leftCell.setMinimumHeight(HAZMAT_SECTION_HEIGHT / 2);
        leftCell.addElement(new Chunk("Comments / Special Instructions for Pick-up:", helvetica10Bold));
        String pickupNotes = ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), true, load.getOrigin()
                .getNotes(), load.getSpecialInstructions());
        leftCell.addElement(new Chunk(pickupNotes, helvetica8));
        table.addCell(leftCell);

        PdfPCell secondRowCell = new PdfPCell();
        secondRowCell.setColspan(2);
        secondRowCell.setMinimumHeight(HAZMAT_SECTION_HEIGHT / 2);
        secondRowCell.addElement(new Chunk("Comments / Special Instructions for Delivery:", helvetica10Bold));
        String deliveryNotes = ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), false, load.getDestination()
                .getNotes(), load.getDeliveryNotes());
        secondRowCell.addElement(new Chunk(deliveryNotes, helvetica8));
        table.addCell(secondRowCell);

        document.add(table);
    }

    private void writeMiscSection(String paymentTerms, Document document, PdfContentByte pdfContentByte) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(new float[] { .2f, .2f, .4f });
        table.setWidthPercentage(HUNDRED_PERCENT);
        Phrase leftPhrase = new Phrase();
        leftPhrase.add(new Chunk("Freight Charges: ", helvetica8));
        leftPhrase.add(new Chunk(paymentTerms, helvetica10Bold));

        PdfPCell leftCell = new PdfPCell(leftPhrase);
        table.addCell(leftCell);

        PdfPCell middleCell = new PdfPCell(new Phrase("C.O.D. Amount: $__________", helvetica8Bold));
        table.addCell(middleCell);

        PdfPCell rightCell = new PdfPCell();

        PdfPTable rightCellPdfPTable = ITextUtils.createTable(3);
        PdfPCell firstCell = new PdfPCell(new Phrase("CARRIERS C.O.D. FEE PAID BY: ", helvetica8));
        firstCell.setColspan(3);
        firstCell.setPadding(0);
        firstCell.setBorder(Rectangle.NO_BORDER);
        rightCellPdfPTable.addCell(firstCell);

        PdfPCell secondCell = new PdfPCell();
        secondCell.setBorder(Rectangle.NO_BORDER);

        Phrase shipperPhrase = new Phrase();
        Chunk checkboxChunk = new Chunk(ITextUtils.getSquareImage(pdfContentByte, 7f, 6f), 5, 0);
        shipperPhrase.add(new Chunk("Shipper", helvetica6));
        shipperPhrase.add(checkboxChunk);
        secondCell.addElement(shipperPhrase);
        secondCell.setRowspan(2);

        PdfPCell thirdCell = new PdfPCell();
        thirdCell.setBorder(Rectangle.NO_BORDER);

        Phrase consigneePhrase = new Phrase();
        consigneePhrase.add(new Chunk("Consignee", helvetica6));
        consigneePhrase.add(checkboxChunk);
        thirdCell.addElement(consigneePhrase);

        PdfPCell fourthCell = new PdfPCell();
        fourthCell.setBorder(Rectangle.NO_BORDER);

        Phrase thirdPartyPhrase = new Phrase();
        thirdPartyPhrase.add(new Chunk("Third Party", helvetica6));
        thirdPartyPhrase.add(checkboxChunk);
        fourthCell.addElement(thirdPartyPhrase);

        rightCellPdfPTable.addCell(secondCell);
        rightCellPdfPTable.addCell(thirdCell);
        rightCellPdfPTable.addCell(fourthCell);

        rightCell.addElement(rightCellPdfPTable);

        table.addCell(rightCell);

        document.add(table);
    }
    
    private void writeSignaturesSection(String carrierName, Document document, PdfContentByte pdfContentByte)
    		throws DocumentException {
    	LoadEntity load = null;
    	writeSignaturesSection(carrierName, document, pdfContentByte, load, null);
    }

    private void writeSignaturesSection(String carrierName, Document document, PdfContentByte pdfContentByte, LoadEntity load, String custName)
            throws DocumentException {
        PdfPTable table = ITextUtils.createTable(2, null);
        PdfPCell topLeftCell = new PdfPCell();
        topLeftCell.addElement(new Phrase(
                "Liability Limitation for loss or damage to this shipment may be applicable. See 49"
                        + " U.S.C. 14706(c)(1)(A) and (B). The agreed or declared value of the property is hereby"
                        + " specifically stated by the shipper to be not exceeding $_______per pound and Carrier's"
                        + " tariff charge for such declaration of value shall be applicable to this shipment.",
                helvetica8));
        topLeftCell.addElement(new Phrase("____________________________________Shipper", helvetica8));
        topLeftCell.addElement(new Phrase("Per ________________________________", helvetica8));
        table.addCell(topLeftCell);

        PdfPCell topRightCell = new PdfPCell();
        topRightCell.addElement(new Phrase("Accepted in good order and condition, unless otherwise stated herein.",
                helvetica8));

        Phrase piecesPhrase = new Phrase();
        Chunk rectangleChunk = new Chunk(ITextUtils.getRectagleImage(pdfContentByte, 104f, 10f, 100f, 9f, .8f), 5, -2);
        piecesPhrase.add(new Chunk("PIECES", helvetica8));
        piecesPhrase.add(rectangleChunk);
        topRightCell.addElement(piecesPhrase);

        topRightCell.addElement(new Phrase("Exceptions:", helvetica8));

        table.addCell(topRightCell);

        PdfPCell bottomLeftCell = new PdfPCell();
        bottomLeftCell.addElement(new Phrase("Per ________________________________", helvetica8));
        bottomLeftCell.addElement(new Phrase("(Shipper or Shipper's Agent Signature)", helvetica6));
        bottomLeftCell.addElement(new Phrase("Date and Time tendered _____________________ AM/PM", helvetica8));
        bottomLeftCell.addElement(new Phrase("PERMANENT ADDRESS:", helvetica8));
        table.addCell(bottomLeftCell);

        PdfPCell bottomRightCell = new PdfPCell();
        bottomRightCell.addElement(new Phrase(carrierName, helvetica8Bold));
        bottomRightCell.addElement(new Phrase("Per ________________________________", helvetica8));
        bottomRightCell.addElement(new Phrase("(Driver's Signature)", helvetica6));
        bottomRightCell.addElement(new Phrase("Date and Time tendered ________________________ AM/PM", helvetica8));
        table.addCell(bottomRightCell);

        document.add(table);
    }

    private void writeSertificationsSection(Document document) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(new Phrase[] { new Phrase("Shipper Certification:", helvetica8Bold),
                new Phrase("Carrier Certification:", helvetica8Bold) });

        PdfPCell leftCell = new PdfPCell();
        PdfPCell rightCell = new PdfPCell();
        leftCell.addElement(new Phrase(
                "This is to certify that the above named materials are properly classified, described, "
                        + "packaged, marked and labeled, and are in proper condition for transportation according to "
                        + "the applicable regulations of the U.S. Department of Transportation.", helvetica6));

        leftCell.addElement(new Phrase("Per: ________________________________ Date: _________________", helvetica6));

        rightCell.addElement(new Phrase(
                "Carrier acknowledges receipt of packages and required placards. Carrier certifies "
                        + "emergency response information was made available and/or carrier has the Department "
                        + "of Transportation emergency response guidebook or equivalent document in the vehicle.",
                helvetica6));
        rightCell.addElement(new Phrase("Per: ________________________________ Date: _________________", helvetica6));

        table.addCell(leftCell);
        table.addCell(rightCell);
        document.add(table);
    }

    private void writeReceiptOfShipmentSection(Document document) throws DocumentException {
        PdfPTable table = ITextUtils.createTable(new Phrase[] { new Phrase("Receipt of Shipment:", helvetica8Bold) });

        PdfPCell cell = new PdfPCell();

        Phrase phrase = new Phrase();
        phrase.add(new Chunk("Shipment has been received by consignee in apparent good order unless otherwise noted.",
                helvetica6));
        phrase.add(new Chunk("              Per: ________________________________ Date: _________________", helvetica6));
        cell.addElement(phrase);
        table.addCell(cell);

        document.add(table);
    }

    private void writeBottomSection(Document document) throws DocumentException {
        LineSeparator separator = new LineSeparator();
        separator.setLineWidth(.5f);
        document.add(separator);
        Paragraph paragraph = new Paragraph(new Phrase(
            "PLS Logistics Services is a Property Broker and does not provide any motor carrier services and PLS does not take "
                    + "possession or custody of freight. Carrier is solely liable for loss or damage to cargo and the extent of carrier's "
                    + "liability may "
                    + "be limited by carrier's tariff or other governing publication. PLS is not liable for any loss or damage to cargo.",
            helvetica6));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private void writeContactInfoSection(Document document, boolean hideCreatedTime, UserEntity currentUser,
            PlainModificationObject modification) throws DocumentException {
        UserEntity user = modification.getCreatedUser() != null ? modification.getCreatedUser() : currentUser;
        UserAdditionalContactInfoBO contactInfo = contactInfoService.getContactInfo(user);

        Date createdDate = modification.getCreatedDate();

        PdfPTable table = ITextUtils.createTable(new float[] { .3f, .3f, .4f });
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Phrase datePhrase = new Phrase();
        datePhrase.add(new Chunk("Shipment created by", helvetica8Bold));
        if (createdDate != null) {
            datePhrase.add(new Chunk(" (", helvetica8Bold));
            datePhrase.add(new Chunk(new SimpleDateFormat(hideCreatedTime ? SHORT_DATE_TIME_FORMAT : DATE_TIME_FORMAT,
                    Locale.US).format(createdDate), helvetica8Bold));
            datePhrase.add(new Chunk(")", helvetica8Bold));
        }
        datePhrase.add(new Chunk(":", helvetica8Bold));
        PdfPCell dateCell = new PdfPCell();
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setColspan(3);
        dateCell.setMinimumHeight(20);
        dateCell.addElement(datePhrase);
        table.addCell(dateCell);

        Phrase namePhrase = new Phrase();
        namePhrase.add(new Chunk(StringUtils.defaultString(contactInfo.getContactName()), helvetica8));
        table.addCell(namePhrase);

        Phrase phonePhrase = new Phrase();
        phonePhrase.add(new Chunk("Phone: ", helvetica8));
        phonePhrase.add(new Chunk(StringUtils.defaultString(PhoneUtils.format(contactInfo.getPhone())), helvetica8));
        table.addCell(phonePhrase);

        Anchor anchor = new Anchor(contactInfo.getEmail(), FontFactory.getFont(FontFactory.HELVETICA, 8,
                Font.UNDERLINE, new BaseColor(0, 0, 255)));
        anchor.setName("LINK");
        anchor.setReference("mailto:" + contactInfo.getEmail());

        Phrase emailPhrase = new Phrase();
        emailPhrase.add(new Chunk("Email: ", helvetica8));
        emailPhrase.add(anchor);
        table.addCell(emailPhrase);

        document.add(table);
    }

    private void drawPreviewMark(Document document, PdfContentByte overContentByte) throws DocumentException,
            IOException {
        overContentByte.saveState();
        float sinus = (float) Math.sin(Math.PI / 4);
        float cosinus = (float) Math.cos(Math.PI / 4);
        BaseFont bf = BaseFont.createFont();

        overContentByte.beginText();

        PdfGState gState = new PdfGState();
        gState.setFillOpacity(.5f);
        overContentByte.setGState(gState);

        overContentByte.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
        overContentByte.setLineWidth(.5f);
        overContentByte.setRGBColorFill(0xD3, 0xD3, 0xD3);
        overContentByte.setColorStroke(BaseColor.LIGHT_GRAY);
        overContentByte.setFontAndSize(bf, 136);
        overContentByte.setTextMatrix(cosinus, sinus, -sinus, cosinus, document.left() + 70, document.bottom() + 200);
        overContentByte.showText(PREVIEW_LABEL);
        overContentByte.setTextMatrix(0, 0);
        overContentByte.endText();
        overContentByte.restoreState();
    }

    private class PageEventHelper extends PdfPageEventHelper {
        PdfContentByte pdfContentByte;

        public void setPdfContentByte(PdfContentByte pdfContentByte) {
            this.pdfContentByte = pdfContentByte;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                drawPreviewMark(document, pdfContentByte);
            } catch (IOException e) {
                log.error("IO exception occurs during BOL generation", e);
            } catch (DocumentException e) {
                log.error("PDF generation of BOL failed", e);
            }
        }
    }

    private class StartPageEventHelper extends PdfPageEventHelper {
        LoadEntity load;

        public void setLoad(LoadEntity load) {
            this.load = load;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                if (writer.getCurrentPageNumber() > 1) {
                    writeHeaderSection(load, document);
                }
            } catch (DocumentException e) {
                log.error("PDF generation of BOL failed", e);
            }
        }
    }
    
    private void writeHazmatSection(Document document, PdfContentByte pdfContentByte) throws DocumentException {
    	
    	PdfPTable hazmatTable = ITextUtils.createTable(2);
    	
    	PdfPCell firstCell = new PdfPCell();
        firstCell.setBorder(Rectangle.NO_BORDER);
    	Phrase msdsPhrase = new Phrase();
    	Chunk checkboxChunk1 = new Chunk(ITextUtils.getSquareImage(pdfContentByte, 7f, 6f), 5, 0);
    	msdsPhrase.add(checkboxChunk1);
        msdsPhrase.add(new Chunk("    M.S.D.S ATTACHED OR ERG# _________________", helvetica8Bold)); 
        firstCell.addElement(msdsPhrase);
        hazmatTable.addCell(firstCell);

        PdfPCell secondCell = new PdfPCell();
        secondCell.setBorder(Rectangle.NO_BORDER);
    	Phrase placardPhrase = new Phrase();
    	Chunk checkboxChunk2 = new Chunk(ITextUtils.getSquareImage(pdfContentByte, 7f, 6f), 5, 0);
    	placardPhrase.add(checkboxChunk2);
        placardPhrase.add(new Chunk("    DRIVER HAS BEEN OFFERED PLACARDS", helvetica8Bold)); 
        secondCell.addElement(placardPhrase);
        hazmatTable.addCell(secondCell);
        
        PdfPCell thirdCell = new PdfPCell();
        thirdCell.setBorder(Rectangle.NO_BORDER);
    	Phrase securedPhrase = new Phrase();
    	Chunk checkboxChunk3 = new Chunk(ITextUtils.getSquareImage(pdfContentByte, 7f, 6f), 5, 0);
        securedPhrase.add(checkboxChunk3); 
        securedPhrase.add(new Chunk("    LOAD SECURED PRIOR TO TRANSIT LOADER", helvetica8Bold));
        thirdCell.addElement(securedPhrase);
        hazmatTable.addCell(thirdCell);
        
        PdfPCell fourthCell = new PdfPCell();
        fourthCell.setBorder(Rectangle.NO_BORDER);
    	Phrase loaderPhrase = new Phrase();
        loaderPhrase.add(new Chunk("   LOADER ____________________", helvetica8Bold));
        fourthCell.addElement(loaderPhrase);
        hazmatTable.addCell(fourthCell);
        
        document.add(hazmatTable);
    }
}
