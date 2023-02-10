package com.pls.shipment.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.pls.core.common.utils.DateUtility;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
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
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;


/**
 * Test for ConsigneeInvoicePdfDocumentWriter.
 * 
 * @author Sergii Belodon
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsigneeInvoicePdfDocumentWriterTest {
    private String[] content = {
            "PLS Pro Number:",
            "Invoice Date:",
            "Payment Terms:",
            "Due Date:",
            "Freight Amount:",
            "1",
            "%DATE%",
            "15 days",
            "%DATE2%",
            "$10.70",
            "Invoice To:",
            "TEST BILL TO",
            "TEST ADDRESS",
            "BROCCOLI, PA, 11111",
            "Remit To:",
            "PLS Logistics Services",
            "3120 Unionville Road",
            "Cranberry TWP, PA 16066",
            "Shipment Number:",
            "General Ledger Number:",
            "Job #:",
            "PO Number:",
            "2012-po-12",
            "Ship Date:",
            "Feb 25, 2013",
            "Origin:",
            "contactO",
            "ADDRESS1",
            "CITY, STATECODE 1111",
            "Destination:",
            "contactD",
            "ADDRESS1",
            "CITYD, STATECODED 1111D",
            "BOL Commodity:",
            "productDescription1 , 1LB",
            "productDescription2 , 1LB",
            "productDescription3 , 1LB",
            "Carrier Name:",
            "carrier",
            "Miles:",
            "10",
            "Freight Amount:",
            "$10.70",
            "For proper credit, please reference our invoice number in your payment.",
            "If you have questions related to this invoice, please send an email to",
            "accountsreceivable@plslogistics.com",
            "3120 Unionville Road, Building 110, Cranberry Township, PA, 16066,",
            "www.plslogistics.com"
            };
    @InjectMocks
    private ConsigneeInvoicePdfDocumentWriter consigneeInvoicePdfDocumentWriter;

    @Test
    public void shouldGeneratePdf() throws Exception {
        TestUtils.instantiateClassPathResource("defaultLogo", consigneeInvoicePdfDocumentWriter);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //FileOutputStream outputStream = new FileOutputStream(new File("E:/annex/out.pdf"));
        ConsigneeInvoiceDocumentParameter documentParameter = new ConsigneeInvoiceDocumentParameter(getLoad());
        consigneeInvoicePdfDocumentWriter.write(documentParameter, outputStream);
        List<String> pdfContent = getPdfContent(outputStream);
        Date currentDate = Calendar.getInstance().getTime();
        Date dueDate = DateUtility.addDays(currentDate, 15);
        TestUtils.checkPdfContent(content, pdfContent, currentDate, "MM/dd/yy", dueDate);
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
        load.setMileage(10);

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

        HashSet<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        loadMaterials.add(getProduct(load, "1"));
        loadMaterials.add(getProduct(load, "2"));
        loadMaterials.add(getProduct(load, "3"));
        loadMaterials.add(getProduct(load, "1"));
        load.getOrigin().setLoadMaterials(loadMaterials);

        load.setCustomsBroker("broker");
        load.setCustomsBrokerPhone("+7150325948");

        BillToEntity billTo = new BillToEntity();
        billTo.setName("TEST BILL TO");
        billTo.setBillingInvoiceNode(new BillingInvoiceNodeEntity());
        billTo.getBillingInvoiceNode().setBillTo(billTo);
        billTo.getBillingInvoiceNode().setAddress(buildAddress());

        load.setBillTo(billTo);
        load.setLoadAdditionalInfo(new LoadAdditionalInfoEntity());
        load.getLoadAdditionalInfo().setMarkup(7L);
        return load;
    }
    private AddressEntity buildAddress() {
        AddressEntity addressForBillTo = new AddressEntity();
        StateEntity stateForBillTo = new StateEntity();

        StatePK pk = new StatePK();
        pk.setStateCode("PA");
        stateForBillTo.setStatePK(pk);

        addressForBillTo.setAddress1("TEST ADDRESS");
        addressForBillTo.setState(stateForBillTo);
        addressForBillTo.setZip("11111");
        addressForBillTo.setCity("Broccoli");
        return addressForBillTo;
    }
    private LoadMaterialEntity getProduct(LoadEntity load, String index) {
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setWeight(BigDecimal.ONE);
        loadMaterialEntity.setWidth(BigDecimal.ZERO);
        loadMaterialEntity.setHeight(BigDecimal.ZERO);
        loadMaterialEntity.setLength(BigDecimal.ONE);
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_100);
        loadMaterialEntity.setQuantity("11");
        loadMaterialEntity.setPieces(17);
        loadMaterialEntity.setNmfc("nmfc");
        loadMaterialEntity.setProductDescription("productDescription" + index);
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setDescription("description");
        loadMaterialEntity.setPackageType(packageType);
        load.getOrigin().getLoadMaterials().add(loadMaterialEntity);
        return loadMaterialEntity;
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
}
