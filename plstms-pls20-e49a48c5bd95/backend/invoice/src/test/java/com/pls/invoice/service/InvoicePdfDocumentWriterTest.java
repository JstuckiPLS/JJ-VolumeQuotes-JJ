package com.pls.invoice.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.NetworkEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.core.util.TestUtils;
import com.pls.invoice.service.pdf.InvoicePdfDocumentParameter;
import com.pls.invoice.service.pdf.InvoicePdfDocumentWriter;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * The Class InvoicePdfDocumentWriterTest.
 * 
 * @author Sergii Belodon
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoicePdfDocumentWriterTest {
    private String[] content = {
            "Page 1 of 1",
            "To pay by credit card, go to ",
            "https://plspay.plslogistics.com",
            "Invoice Number:",
            "PO Number: 2012-po-12",
            "Invoice To:",
            "INVOICE",
            "Invoice Number:",
            "Invoice Date:",
            "%DATE%",
            "Payment Terms:",
            "Due Date:",
            "%DATE%",
            "Amount Due:",
            "$10.00",
            "Please pay in currency:",
            "USD",
            "Ship From:",
            "contactO",
            "ADDRESS1",
            "CITY, 1111",
            "contactName",
            "111",
            "Ship To:",
            "contactD",
            "ADDRESS1",
            "CITYD, 1111D",
            "contactNamed",
            "123",
            "Qty/Pkg",
            "Type",
            "Description",
            "Weight",
            "(Lbs)",
            "Dims",
            "(Inches)",
            "Class",
            "11/description",
            "productDescription",
            "1",
            "1 x 0 x 0",
            "100",
            "Total:",
            "1",
            "Comments",
            "Unit",
            "Amount",
            "",
            "Total Invoice:",
            "$10.00",
            "Carrier Name: carrier",
            "SCAC Code:",
            "",
            "Ship Date:",
            "",
            "PO Number: 2012-po-12",
            "REF Number:",
            "PU Number:",
            "Order/SO#:",
            "Pro Number: 2012-pro-11",
            "GL Code:",
            "BOL Number: 2012-bol-10",
            "Trailer #:",
            "",
            "",
            "Make Checks Payable To:",
            "Pittsburgh Logistics Systems, Inc.",
            "PO Box 778858",
            "Chicago, IL 60677-8858",
            "For proper credit, please reference our invoice number on your payment.",
            "---or---",
            "If you have any questions about this invoice, please email: ",
            "accountsreceivable@plslogistics.com",
            "To pay by credit card, go to ",
            "https://plspay.plslogistics.com"
    };

    @InjectMocks
    private InvoicePdfDocumentWriter invoicePdfDocumentWriter;

    @Before
    public void init() {
        TestUtils.instantiateClassPathResource("organizationLogo", invoicePdfDocumentWriter);
    }

    // use commented code to create pdf file and check it
    @Test
    public void shouldGeneratePdf() throws Exception {
//        File file = new File("C:\\temp.pdf");
//        if (file.exists()) {
//            file.delete();
//            file = new File("C:\\temp.pdf");
//        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<LoadAdjustmentBO> invoices = new ArrayList<LoadAdjustmentBO>();
        LoadEntity load = getLoad();
        LoadAdjustmentBO loadAdjustmentBO = new LoadAdjustmentBO(load);
        invoices.add(loadAdjustmentBO);
        InvoicePdfDocumentParameter documentParameter = new InvoicePdfDocumentParameter(invoices);
        invoicePdfDocumentWriter.write(documentParameter, outputStream);
//        invoicePdfDocumentWriter.write(documentParameter, new FileOutputStream(file));
//        if (true)
//            return;
        List<String> pdfContent = getPdfContent(outputStream);
//        System.out.println(pdfContent.stream().collect(Collectors.joining("\n")));
        TestUtils.checkPdfContent(content, pdfContent, load.getModification().getCreatedDate(), "EEE MM/dd/yyyy", null);
    }

    private List<String> getPdfContent(ByteArrayOutputStream outputStream) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        PdfReader reader = new PdfReader(inputStream);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        StringArrayRenderListener stringArrayRenderListener = new StringArrayRenderListener();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            stringArrayRenderListener = parser.processContent(i, stringArrayRenderListener);
        }
        return stringArrayRenderListener.getTextBlocks();
    }

    private LoadEntity getLoad() throws ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(1L);
        load.getNumbers().setBolNumber("2012-bol-10");
        load.getNumbers().setProNumber("2012-pro-11");
        load.getNumbers().setPoNumber("2012-po-12");
        load.setPieces(10);
        load.setWeight(10000);
        load.setStatus(ShipmentStatus.BOOKED);

        load.setCarrier(getCarrier());

        CustomerEntity customer = new CustomerEntity();
        NetworkEntity network = new NetworkEntity();
        customer.setNetwork(network);
        customer.setName("customer");
        load.setOrganization(customer);
        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setOrganization(customer);
        UserEntity accUser = new UserEntity();
        accUser.setFirstName("first");
        accUser.setLastName("last");
        AccountExecutiveEntity accountExecutive = new AccountExecutiveEntity(location, accUser);
        location.getAccountExecutives().add(accountExecutive);
        load.setLocation(location);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setDeparture(toDate("25/02/13"));
        // origin.setArrivalWindowStart(toTime("25/02/13 12:00:00"));
        // origin.setArrivalWindowEnd(toTime("25/02/13 14:00:00"));
        origin.setAddress(new AddressEntity());
        origin.getAddress().setAddress1("address1");
        origin.getAddress().setCity("city");
        origin.getAddress().setStateCode("stateCode");
        origin.getAddress().setZip("1111");
        origin.setContactName("contactName");
        origin.setContactPhone("111");
        origin.setEarlyScheduledArrival(new Date());

        origin.setContact("contactO");
        load.addLoadDetails(origin);
        HashSet<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        load.setCostDetails(costDetails);
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setGuaranteedNameForBOL("nameForBOL");
        loadCostDetailsEntity.setGuaranteedBy(1L);
        loadCostDetailsEntity.setTotalRevenue(BigDecimal.TEN);
        costDetails.add(loadCostDetailsEntity);

        TimeZoneEntity timezone = new TimeZoneEntity();
        timezone.setCode("PDT");
        origin.setScheduledArrivalTimeZone(timezone);

        LoadDetailsEntity dest = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        dest.setDeparture(toDate("27/02/13"));
        dest.setArrivalWindowStart(toTime("27/02/13 12:00:00"));
        dest.setArrivalWindowEnd(toTime("27/02/13 14:00:00"));
        dest.setAddress(new AddressEntity());
        dest.getAddress().setAddress1("address1");
        dest.getAddress().setCity("cityd");
        dest.getAddress().setStateCode("stateCoded");
        dest.getAddress().setZip("1111d");
        dest.setContactName("contactNamed");
        dest.setContactPhone("123");
        dest.setContact("contactD");
        load.addLoadDetails(dest);
        // load.setBillingAuditReasons(addBillingAuditReasonsEntity());
        load.setCostDetails(costDetails);
        CarrierInvoiceDetailsEntity vendorBill = new CarrierInvoiceDetailsEntity();
        vendorBill.setTotalCharges(BigDecimal.TEN);
        ArrayList<CarrierInvoiceDetailsEntity> listVendorBill = new ArrayList<CarrierInvoiceDetailsEntity>();
        listVendorBill.add(vendorBill);
        load.getVendorBillDetails().setCarrierInvoiceDetails(listVendorBill);
        load.setVolumeQuoteId("qid");
        FreightBillPayToEntity freightBillPayTo = new FreightBillPayToEntity();
        AddressEntity address = new AddressEntity();
        address.setAddress1("address1");
        address.setAddress2("address2");
        address.setCity("city");
        address.setZip("65000");
        address.setStateCode("sC");
        freightBillPayTo.setCompany("company");
        freightBillPayTo.setContactName("contactName");
        freightBillPayTo.setAddress(address);
        load.setFreightBillPayTo(freightBillPayTo);
        load.getOrigin().setLoadMaterials(new HashSet<LoadMaterialEntity>());
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setWeight(BigDecimal.ONE);
        loadMaterialEntity.setWidth(BigDecimal.ZERO);
        loadMaterialEntity.setHeight(BigDecimal.ZERO);
        loadMaterialEntity.setLength(BigDecimal.ONE);
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_100);
        loadMaterialEntity.setQuantity("11");
        loadMaterialEntity.setPieces(17);
        loadMaterialEntity.setNmfc("nmfc");
        loadMaterialEntity.setProductDescription("productDescription");
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setDescription("description");
        loadMaterialEntity.setPackageType(packageType);
        load.getOrigin().getLoadMaterials().add(loadMaterialEntity);
        HashSet<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        loadMaterials.add(loadMaterialEntity);
        load.getOrigin().setLoadMaterials(loadMaterials);

        load.setCustomsBroker("broker");
        load.setCustomsBrokerPhone("+7150325948");

        BillToEntity billTo = new BillToEntity();
        billTo.setBillingInvoiceNode(new BillingInvoiceNodeEntity());
        billTo.getBillingInvoiceNode().setBillTo(billTo);
        billTo.getBillingInvoiceNode().setAddress(new AddressEntity());
        load.setBillTo(billTo);
        return load;
    }
 
    private CarrierEntity getCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setName("carrier");
        return carrier;
    }

    private Date toDate(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yy").parse(date);
    }

    private Date toTime(String time) throws ParseException {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss").parse(time);
    }

    private class StringArrayRenderListener implements RenderListener {
        private List<String> textBlocks = new ArrayList<String>(100);
        /**
         * Event handler for beginning of text block.
         * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
         */
        public void beginTextBlock() {
            // do nothing
        }

        /**
         * Event handler for ending of text block.
         * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
         */
        public void endTextBlock() {
            // do nothing
        }

        /**
         * Event handler for image.
         * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(
         *     com.itextpdf.text.pdf.parser.ImageRenderInfo)
         *     @param renderInfo - render info.
         */
        public void renderImage(ImageRenderInfo renderInfo) {
            // do nothing
        }

        /**
         * Event handler for text.
         * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(
         *     com.itextpdf.text.pdf.parser.TextRenderInfo)
         *     @param renderInfo - text render info.
         */
        public void renderText(TextRenderInfo renderInfo) {
            textBlocks.add(renderInfo.getText());
        }

        /**
         * Returns content.
         * @return generated array of strings
         */
        public List<String> getTextBlocks() {
            return textBlocks;
        }
    }

}
