package com.pls.invoice.service.xml.finance;

import static com.pls.core.util.SqlAssert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.util.xml.adapter.DateXmlAdapter;
import com.pls.core.service.xml.JaxbService;
import com.pls.invoice.domain.xml.finance.salesorder.SalesLine;
import com.pls.invoice.domain.xml.finance.salesorder.SalesOrder;
import com.pls.invoice.domain.xml.finance.vendinvoice.VendInvoiceInfoLine;
import com.pls.invoice.domain.xml.finance.vendinvoice.VendInvoiceInfoTable;

/**
 * Integration tests for {@link com.pls.core.service.xml.JaxbService}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Ignore
public class FinanceXmlSerializationIT extends BaseServiceITClass {
    @Autowired
    private JaxbService jaxbService;

    @Test
    public void testArSerialization() throws IOException, XmlSerializationException {

        SalesOrder order = new SalesOrder();
        order.setOperation("CREATE");
        order.setCurrency("USD");
        order.setCustAccount("AK606072-13002");
        order.setShipmentNo("8601881");
        order.setBusinessUnit("N1");
        order.setCostCenter("Pitt1");
        order.setDepartment("SL");
        order.setAddend("1");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        Date date = calendar.getTime();
        order.setAdjDate(date);
        order.setAdjType("DK");
        order.setBenchmarkRate(500.00);
        order.setBillToLoc("13002");
        order.setBillToName("SOUTH OP BILL TO");
        order.setBillToId("302");
        order.setBusinessPartnerName("BPNAME");
        order.setBusinessPartner("PARTNER");
        order.setCarrierName("ALCO EXPRESS");
        order.setCarrierPremium(123.23);
        order.setScac("AEXQ");
        order.setCommodity("COIL-C");

        order.setDestAddr1("1600 NADEAU RD");
        order.setDestAddr2("TWO");
        order.setDestCity("MONROE");
        order.setDestLoc("39441");
        order.setDestName("CHRYSLER GROUP LLC");
        order.setDestNode("000243");
        order.setDestState("MI");
        order.setDestZip("48162");
        order.setDestCountry("USA");

        order.setDiscount(new BigDecimal(12));
        order.setEquipmentType("SIDEKT");
        order.setEquipmentDesc("EQUIP");
        order.setGlNumber("LEDGER");
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        Date glDate = calendar.getTime();
        order.setGlDate(glDate);
        order.setHazmat("N");
        order.setHeight(12.00);
        order.setInbOtb("O");
        order.setInvoiceNo("FTI004647");
        order.setTotalStops(12);
        order.setJobNumbers("1,2,3");
        order.setJobPercents("10,20,30");
        order.setLocationName("MIDDLETOWN");
        order.setLength(12.00);
        order.setMargin(35.0);
        order.setMiles(31);
        order.setNetworkId("0");
        order.setOneTimeRate(1L);
        order.setOpBol("41516");
        order.setCustomerOrgId(25L);
        order.setLocationId(330L);

        order.setOrigAddr1("36253 MICHIGAN AVE");
        order.setOrigAddr2("SECOND ONE");
        order.setOrigCity("WAYNE");
        order.setOrigLoc("68901");
        order.setOrigName("AK STEEL CORPORATION C/O WAYNE INDUSTRIES INC.");
        order.setOrigNode("074212");
        order.setOrigState("MI");
        order.setOrigZip("48184");
        order.setOrigCountry("USA");

        order.setOutPeriod("1");
        order.setPartNum("1004039");
        order.setPieces(1);
        order.setProNumber("150429");
        order.setProse1("41516");
        order.setProse2("10301263");
        order.setProtectedCarrier("N");
        order.setSalesOrderType("1");
        order.setShipCarr("S");
        order.setShipWith(2);
        order.setShortPay("Yes");
        order.setSplitComb("1");
        order.setPayTerms("PPD");
        order.setTrailerNo("12");
        order.setWeight(56860.00);
        order.setWidth(100.00);
        order.setLoadNumber("9999980");

        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 16);
        Date dates = calendar.getTime();
        order.setDeliveryDate(dates);
        order.setPickupDate(dates);
        order.setBillerType("BILLER1");
        order.setLoadId(10627392L);
        order.setFaaDetailId("7435263");
        order.setRequestId("213425");
        List<SalesLine> salesLines = new ArrayList<SalesLine>();

        SalesLine salesLine1 = new SalesLine();
        salesLine1.setItemType("OW");
        salesLine1.setUnitCost(new BigDecimal(25.00));
        salesLine1.setQuantity(1L);
        salesLine1.setUnitType("FL");
        salesLine1.setTotal(new BigDecimal(25.00));
        salesLine1.setComments("Accessorial");
        salesLines.add(salesLine1);

        SalesLine salesLine2 = new SalesLine();
        salesLine2.setItemType("LH");
        salesLine2.setUnitCost(new BigDecimal(525.00));
        salesLine2.setQuantity(1L);
        salesLine2.setUnitType("FL");
        salesLine2.setTotal(new BigDecimal(525.00));
        salesLine2.setComments("Linehaul");
        salesLines.add(salesLine2);

        order.setSalesLines(salesLines);

        StringWriter writer = new StringWriter();

        Resource financeArXmlResource = loadResource("FinanceAR.xml");

        IOUtils.copy(financeArXmlResource.getInputStream(), writer);
        String expectedXml = writer.toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        jaxbService.write(order, outputStream);
        String result = new String(outputStream.toByteArray());
        assertNotNull(result);
        assertEquals(expectedXml, result);
    }

    @Test
    public void testApSerialization() throws Exception {

        VendInvoiceInfoTable vendInvoiceInfoTable = new VendInvoiceInfoTable();

        vendInvoiceInfoTable.setOperation("CREATE");
        vendInvoiceInfoTable.setApproved("Yes");
        vendInvoiceInfoTable.setCurrency("USD");
        vendInvoiceInfoTable.setBusinessUnit("FS");
        vendInvoiceInfoTable.setCostCenter("Dall1");
        vendInvoiceInfoTable.setDepartment("SL");

        DateXmlAdapter frtBillRecvDate = new DateXmlAdapter();
        vendInvoiceInfoTable.setFrtBillRecvDate(frtBillRecvDate.unmarshal("2015-05-01"));

        vendInvoiceInfoTable.setShipmentNo("79999999");
        vendInvoiceInfoTable.setLoadNumber("89999999");
        vendInvoiceInfoTable.setApTerms("NET28");

        DateXmlAdapter adjDate = new DateXmlAdapter();
        vendInvoiceInfoTable.setAdjDate(adjDate.unmarshal("2015-02-20"));

        vendInvoiceInfoTable.setScac("GWDT");
        vendInvoiceInfoTable.setDestCity("New York");
        vendInvoiceInfoTable.setDestState("NY");
        vendInvoiceInfoTable.setDestCountry("USA");

        DateXmlAdapter glDate = new DateXmlAdapter();
        vendInvoiceInfoTable.setGlDate(glDate.unmarshal("2015-02-20"));

        vendInvoiceInfoTable.setInvoiceNum("89999999");
        vendInvoiceInfoTable.setNetworkId("A1");
        vendInvoiceInfoTable.setOriginCity("Pittsburgh");
        vendInvoiceInfoTable.setOriginState("PA");
        vendInvoiceInfoTable.setOriginCountry("USA");
        vendInvoiceInfoTable.setPoNumber("CUST-123456");
        vendInvoiceInfoTable.setProNumber("89999999");

        DateXmlAdapter shipDate = new DateXmlAdapter();
        vendInvoiceInfoTable.setShipDate(shipDate.unmarshal("2015-02-20"));

        vendInvoiceInfoTable.setCarrierName("GREATWIDE DEDICATED TRANSPORT");
        vendInvoiceInfoTable.setBillerType("BILLER1");
        vendInvoiceInfoTable.setLoadId(10627392L);
        vendInvoiceInfoTable.setFaaDetailId("7435263");
        vendInvoiceInfoTable.setRequestId("213425");

        List<VendInvoiceInfoLine> vendInvoiceInfoLines = new ArrayList<VendInvoiceInfoLine>();

        VendInvoiceInfoLine vendInvoiceInfoLine1 = new VendInvoiceInfoLine();
        vendInvoiceInfoLine1.setItemType("LH");
        vendInvoiceInfoLine1.setUnitCost(new BigDecimal(234));
        vendInvoiceInfoLine1.setTotal(new BigDecimal(234));
        vendInvoiceInfoLine1.setUnitType("FL");
        vendInvoiceInfoLine1.setQuantity(1L);
        vendInvoiceInfoLine1.setComments("Linehaul");
        vendInvoiceInfoLines.add(vendInvoiceInfoLine1);

        VendInvoiceInfoLine vendInvoiceInfoLine2 = new VendInvoiceInfoLine();
        vendInvoiceInfoLine2.setItemType("FS");
        vendInvoiceInfoLine2.setUnitCost(new BigDecimal(234));
        vendInvoiceInfoLine2.setTotal(new BigDecimal(234));
        vendInvoiceInfoLine2.setUnitType("FL");
        vendInvoiceInfoLine2.setQuantity(1L);
        vendInvoiceInfoLine2.setComments("Accessorial");
        vendInvoiceInfoLines.add(vendInvoiceInfoLine2);

        vendInvoiceInfoTable.setVendInvoiceInfoLines(vendInvoiceInfoLines);

        StringWriter writer = new StringWriter();

        Resource financeArXmlResource = loadResource("FinanceAP.xml");

        IOUtils.copy(financeArXmlResource.getInputStream(), writer);
        String expectedXml = writer.toString();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        jaxbService.write(vendInvoiceInfoTable, outputStream);
        String result = new String(outputStream.toByteArray());
        assertNotNull(result);
        assertEquals(expectedXml, result);
    }
}
