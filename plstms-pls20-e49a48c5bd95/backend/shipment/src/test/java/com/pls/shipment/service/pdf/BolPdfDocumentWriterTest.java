package com.pls.shipment.service.pdf;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.shared.Status;
import com.pls.core.util.TestUtils;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNumbersEntity;

/**
 * Test for BOL generation.
 * 
 * @author Sergii Belodon
 */
@RunWith(MockitoJUnitRunner.class)
public class BolPdfDocumentWriterTest {
    private String[] content = {
            "Page 1 of 1",
            "PLS Logistics Services is a Property Broker and does not provide "
            + "any motor carrier services and PLS does not take possession or "
            + "custody of freight. "
            + "Carrier is solely liable for loss or damage to",
            "cargo and the extent of carrier's liability may be limited by "
            + "carrier's tariff or other governing publication. "
            + "PLS is not liable for any loss or damage to cargo.",
            "BILL OF LADING",
            "Non-Negotiable",
            "",
            "BOL No: 123456",
            "ADD PRO STICKER HERE",
            "Shipper Information (Origin):",
            "Reference Information:",
            "null",
            "null null",
            "PO Number: 135246",
            "Ship Date:",
            "Pick-Up Number:",
            "Standard Service",
            "SO Number: 654321",
            "Trailer Number:",
            "Shipper Ref Number: 010101",
            "Estimated Transit Day: N/A",
            "Pickup Window:  -",
            "Delivery Hours:  -",
            "Consignee Information (Destination):",
            "Bill To Information:",
            "null",
            "null null",
            "Handling",
            "Units",
            "Qty",
            "Pcs",
            "Stack",
            "HM",
            "Description",
            "Weight",
            "(Lbs)",
            "Dimensions",
            "(Inches)",
            "Class",
            "NMFC#",
            "Total:",
            "0",
            "0",
            "Comments / Special Instructions for Pick-up:",
            "Comments / Special Instructions for Delivery:",
            "Freight Charges: ",
            "",
            "C.O.D. Amount: $__________",
            "CARRIERS C.O.D. FEE PAID BY:",
            "Shipper",
            "Consignee",
            "Third Party",
            "Liability Limitation for loss or damage to this shipment may be",
            "applicable. See 49 U.S.C. 14706(c)(1)(A) and (B). The agreed or",
            "declared value of the property is hereby specifically stated by the",
            "shipper to be not exceeding $_______per pound and Carrier's tariff",
            "charge for such declaration of value shall be applicable to this shipment.",
            "____________________________________Shipper",
            "Per ________________________________",
            "Accepted in good order and condition, unless otherwise stated herein.",
            "PIECES",
            "Exceptions:",
            "Per ________________________________",
            "(Shipper or Shipper's Agent Signature)",
            "Date and Time tendered _____________________ AM/PM",
            "PERMANENT ADDRESS:",
            "Per ________________________________",
            "(Driver's Signature)",
            "Date and Time tendered ________________________ AM/PM",
            "Shipper Certification:",
            "Carrier Certification:",
            "This is to certify that the above named materials are properly "
            + "classified, described, packaged,",
            "marked and labeled, and are in proper condition for transportation"
            + " according to the applicable",
            "regulations of the U.S. Department of Transportation.",
            "Per: ________________________________ Date: _________________",
            "Carrier acknowledges receipt of packages and required placards. "
            + "Carrier certifies emergency",
            "response information was made available and/or carrier has the "
            + "Department of Transportation",
            "emergency response guidebook or equivalent document in the vehicle.",
            "Per: ________________________________ Date: _________________",
            "Receipt of Shipment:",
            "Shipment has been received by consignee in apparent good order "
            + "unless otherwise noted.              Per: ________________________________ "
            + "Date: _________________",
            "Shipment created by (%DATE%):", "Test Contact", "Phone: +1 (123) 456-7890", "Email: ", "email@example.com" };

    @Mock
    private ContactInfoService contactInfoService;

    @Mock
    private ClassPathResource defaultLogo;

    @Mock
    private ClassPathResource defaultGoShipLogo;
    
    @Mock
    private LtlProfileDetailsService ltlProfileDetailsService;

    @InjectMocks
    public BolPdfDocumentWriter bolPdfDocumentWriter;
    
    @Before
    public void setUp() {
        LtlPricingProfileEntity profile = new LtlPricingProfileEntity();
        profile.setDisplayQuoteNumberOnBol(false);
        when(ltlProfileDetailsService.getProfileById(999111L)).thenReturn(profile );
    }

    @Test
    public void shouldGeneratePdf() throws Exception {
        LoadEntity load = fillLoadInfo();
        UserEntity userEntity = new UserEntity();
        UserAdditionalContactInfoBO contactInfo = new UserAdditionalContactInfoBO();
        contactInfo.setEmail("email@example.com");
        contactInfo.setPhone(PhoneUtils.parse("1234567890"));
        contactInfo.setContactName("Test Contact");
        Mockito.when(contactInfoService.getContactInfo(userEntity)).thenReturn(contactInfo);
        InputStream logo = null;
        BolPdfDocumentParameter bolPdfDocumentParameter = new BolPdfDocumentParameter(load, userEntity, logo,
                false, false);
        setPaymentMethod("Third Party");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(defaultLogo.getInputStream()).thenReturn(
                new ClassPathResource("/images/logo.jpg").getInputStream());
        Mockito.when(defaultGoShipLogo.getInputStream())
                .thenReturn(new ClassPathResource("/images/logo-goship.png")
                        .getInputStream());
        //bolPdfDocumentWriter.setDefaultEmail("email@example.com <|> 1234567890");
        bolPdfDocumentWriter.write(bolPdfDocumentParameter, outputStream);
        List<String> pdfContent = getPdfContent(outputStream);
        TestUtils.checkPdfContent(content, pdfContent, load.getModification().getCreatedDate(), "MM/dd/yy h:mm a", null);
    }

    @Test
    public void shouldGeneratePdfForManualBol() throws Exception {
        LoadEntity load = fillLoadInfo();
        UserEntity userEntity = new UserEntity();
        UserAdditionalContactInfoBO contactInfo = new UserAdditionalContactInfoBO();
        contactInfo.setEmail("email@example.com");
        contactInfo.setPhone(PhoneUtils.parse("1234567890"));
        contactInfo.setContactName("Test Contact");
        Mockito.when(contactInfoService.getContactInfo(userEntity)).thenReturn(contactInfo);
        InputStream logo = null;
        BolPdfDocumentParameter bolPdfDocumentParameter = new BolPdfDocumentParameter(load, userEntity, logo,
                false, true, true);
        setPaymentMethod("Prepaid");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(defaultLogo.getInputStream()).thenReturn(
                new ClassPathResource("/images/logo.jpg").getInputStream());
        Mockito.when(defaultGoShipLogo.getInputStream())
                .thenReturn(new ClassPathResource("/images/logo-goship.png")
                        .getInputStream());
        //bolPdfDocumentWriter.setDefaultEmail("email@example.com <|> 1234567890");
        bolPdfDocumentWriter.write(bolPdfDocumentParameter, outputStream);
        List<String> pdfContent = getPdfContent(outputStream);
        TestUtils.checkPdfContent(content, pdfContent, load.getModification().getCreatedDate(), "MM/dd/yy", null);
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

    private void setPaymentMethod(String payMethod) {
        content[45] = payMethod;
    }

    private LoadEntity fillLoadInfo() {
        LoadEntity load = new LoadEntity();
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        origin.setAddress(new AddressEntity());
        origin.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        destination.setAddress(new AddressEntity());
        destination.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        load.addLoadDetails(origin);
        load.addLoadDetails(destination);
        load.setFreightBillPayTo(new FreightBillPayToEntity());
        load.getFreightBillPayTo().setAddress(new AddressEntity());

        load.setBillTo(fillBillToData());
        load.setPayTerms(PaymentTerms.PREPAID);
        load.getBillTo().setId((long) (Math.random() * 100));
        LoadNumbersEntity number = new LoadNumbersEntity();
        number.setBolNumber("123456");
        number.setPoNumber("135246");
        number.setSoNumber("654321");
        number.setRefNumber("010101");
        number.setCarrierQuoteNumber("CARRQTN");
        load.setNumbers(number);
        CustomerEntity orgn = new CustomerEntity();
        orgn.setId(54140L);
        load.setOrganization(orgn);
        
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<>();
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);
        costDetail.setPricingProfileDetailId(999111L);
        activeCostDetails.add(costDetail );
        load.setActiveCostDetails(activeCostDetails);
        
        return load;
    }
    private BillToEntity fillBillToData() {
        BillToEntity billTo = new BillToEntity();

        InvoiceSettingsEntity invoiceSettings = new InvoiceSettingsEntity();
        invoiceSettings.setInvoiceType(InvoiceType.TRANSACTIONAL);

        billTo.setInvoiceSettings(invoiceSettings);
        return billTo;
    }
}
