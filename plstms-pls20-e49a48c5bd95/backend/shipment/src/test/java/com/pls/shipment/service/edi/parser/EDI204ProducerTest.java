package com.pls.shipment.service.edi.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pb.x12.FormatException;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.EDIValidationException;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.edi.EDIFile;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.domain.enums.ShipmentOperationType;
import com.pls.shipment.service.impl.LoadTenderServiceImpl;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;
import com.pls.shipment.service.impl.edi.parser.EDI204Producer;
import com.pls.shipment.service.impl.edi.utils.EDIUtils;

/**
 * Test cases for {@link EDI204Producer} class.
 *
 * @author Mikhail Boldinov, 25/10/13
 */
@RunWith(MockitoJUnitRunner.class)
public class EDI204ProducerTest {

    private static final String EDI_FILE_NAME = "EDI204.txt";
    private static final Pattern ISA_SEGMENT_PATTERN = Pattern.compile("ISA[*][^*]{2}[*][^*]{10}[*][^*]{2}[*][^*]{10}[*][^*]{2}[*][^*]{15}[*]"
            + "[^*]{2}[*][^*]{15}[*](\\d{6}[*]\\d{4})[*][^*][*][^*]{5}[*][^*]+[*][^*]+[*][^*]+[*][\\^]");
    private static final Pattern GS_SEGMENT_PATTERN = Pattern.compile("GS[*][^*]*[*][^*]*[*][^*]*[*](\\d{8}[*]\\d{4})[*][^*]*[*][^*]*[*][^*]*\\n");

    private static final Long LOAD_ID = 1L;
    private static final Long CUSTOMER_ID = 42L;
    private static final String BOL = "20131025-1";
    private static final String INVALID_BOL = "20131025-1  '''  ";
    private static final String REF = "20131025-2";
    private static final String PO = "20131025-3";
    private static final String PU = "20131025-4";
    private static final String EDI = "9355606527";
    private static final Integer WEIGHT = 1234;
    private static final Integer PIECES = 25;
    private static final String SPECIAL_INSTRUCTIONS = "Load pickup notes";
    private static final String DELIVERY_NOTES = "Load delivery notes";
    private static final String FREIGHT_BILL_PAY_TO_NAME = "Bill to";
    private static final String FREIGHT_BILL_PAY_TO_ADDRESS1 = "Bill to address 1";
    private static final String FREIGHT_BILL_PAY_TO_ADDRESS2 = "Bill to address 2";
    private static final String FREIGHT_BILL_PAY_TO_CITY = "Pittsburgh";
    private static final String FREIGHT_BILL_PAY_TO_COUNTRY_CODE = "USA";
    private static final String FREIGHT_BILL_PAY_TO_COUNTRY_NAME = "United States of America";
    private static final String FREIGHT_BILL_PAY_TO_STATE_CODE = "PA";
    private static final String FREIGHT_BILL_PAY_TO_STATE_NAME = "Pennsylvania";
    private static final String FREIGHT_BILL_PAY_TO_ZIP = "15106";
    private static final String FREIGHT_BILL_PAY_TO_PHONE_COUNTRY_CODE = "1";
    private static final String FREIGHT_BILL_PAY_TO_PHONE_AREA_CODE = "412";
    private static final String FREIGHT_BILL_PAY_TO_PHONE_NUMBER = "1234567";
    private static final String FREIGHT_BILL_PAY_TO_EMAIL = "billTo@gmail.com";
    private static final String FREIGHT_BILL_PAY_TO_ACCOUNTNUM = "PITT01";
    private static final String FREIGHT_BILL_PAY_TO_CONTACT_NAME = "Jim Russel";
    private static final String ORIGIN_CONTACT = "Origin";
    private static final String ORIGIN_CONTACT_NAME = "Mark Binelli";
    private static final String ORIGIN_ADDRESS1 = "Origin address 1";
    private static final String ORIGIN_ADDRESS2 = null;
    private static final String ORIGIN_CITY = "Detroit";
    private static final String ORIGIN_STATE_CODE = "MI";
    private static final String ORIGIN_STATE_NAME = "Michigan";
    private static final String ORIGIN_COUNTRY_CODE = "USA";
    private static final String ORIGIN_COUNTRY_NAME = "United States of America";
    private static final String ORIGIN_ZIP = "48222";
    private static final String ORIGIN_PHONE_COUNTRY_CODE = "1";
    private static final String ORIGIN_CONTACT_PHONE = "+1 (313) 9876543";
    private static final String ORIGIN_CONTACT_EMAIL = "origin@gmail.com";
    private static final String ORIGIN_ARRIVAL_WINDOW_START = "20130901080000";
    private static final String ORIGIN_ARRIVAL_WINDOW_END = "20130901120000";
    private static final String DESTINATION_CONTACT = "Destination";
    private static final String DESTINATION_CONTACT_NAME = "Shin Hinerman";
    private static final String DESTINATION_ADDRESS1 = "Destination Address 1";
    private static final String DESTINATION_ADDRESS2 = "";
    private static final String DESTINATION_CITY = "Seattle";
    private static final String DESTINATION_STATE_CODE = "WA";
    private static final String DESTINATION_STATE_NAME = "Washington";
    private static final String DESTINATION_COUNTRY_CODE = "USA";
    private static final String DESTINATION_COUNTRY_NAME = "United States of America";
    private static final String DESTINATION_ZIP = "98101";
    private static final String DESTINATION_PHONE_COUNTRY_CODE = "1";
    private static final String DESTINATION_CONTACT_PHONE = "+1 (206) 6543210";
    private static final String DESTINATION_CONTACT_EMAIL = "destination@gmail.com";
    private static final String DESTINATION_DEPARTURE = "20131005053000";
    private static final String PRODUCT1_DESCRIPTION = "Bread";
    private static final CommodityClass PRODUCT1_COMMODITY_CLASS = CommodityClass.CLASS_100;
    private static final String PRODUCT1_QUANTITY = "1";
    private static final BigDecimal PRODUCT1_WEIGHT = new BigDecimal(200);
    private static final boolean PRODUCT1_HAZMAT = false;
    private static final String PRODUCT2_DESCRIPTION = "Butter";
    private static final CommodityClass PRODUCT2_COMMODITY_CLASS = CommodityClass.CLASS_110;
    private static final String PRODUCT2_QUANTITY = "2";
    private static final BigDecimal PRODUCT2_WEIGHT = new BigDecimal(650);
    private static final boolean PRODUCT2_HAZMAT = true;
    private static final BigDecimal TOTAL_COST = new BigDecimal(1654.1).setScale(2, RoundingMode.HALF_UP);

    @Mock
    private CarrierEntity carrier;

    @Mock
    private AbstractEDIParser.DataProvider dataProvider;

    @InjectMocks
    private EDI204Producer sut;

    @Test(expected = UnsupportedOperationException.class)
    public void testParseEDI204() throws IOException, FormatException, EDIValidationException {
        sut.parse(new EDIFile());
    }

    @Test
    public void testCreateEDI204() throws Exception {
        List<LoadEntity> entities = new ArrayList<LoadEntity>();
        LoadEntity load = getLoad();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY, ShipmentOperationType.TENDER);
        params.put(LoadTenderServiceImpl.PRODUCTION_MODE_KEY, Boolean.FALSE);
        entities.add(load);
        EDIFile ediFile = sut.create(entities, params);
        Assert.assertNotNull(ediFile);
        Assert.assertTrue(ediFile.getName().contains(BOL));
        Assert.assertEquals(EDITransactionSet._204, ediFile.getTransactionSet());
        assertFilesContentIdenticalIgnoreCreationDate(getEdiFile(), ediFile.getNewFileContent());
    }

    @Test
    public void testCreateEDI204WithPackageTypeNst() throws Exception {
        List<LoadEntity> entities = new ArrayList<LoadEntity>();
        LoadEntity load = getLoad();
        load.getOrigin().getLoadMaterials().iterator().next().getPackageType().setId("NST");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY, ShipmentOperationType.TENDER);
        params.put(LoadTenderServiceImpl.PRODUCTION_MODE_KEY, Boolean.FALSE);
        entities.add(load);
        EDIFile ediFile = sut.create(entities, params);
        Assert.assertNotNull(ediFile);
        Assert.assertTrue(ediFile.getName().contains(BOL));
        Assert.assertEquals(EDITransactionSet._204, ediFile.getTransactionSet());
        assertFilesContentIdenticalIgnoreCreationDate(getEdiFile(), ediFile.getNewFileContent());
    }

    @Test
    public void testCheckFileName() throws Exception {
        List<LoadEntity> entities = new ArrayList<LoadEntity>();
        LoadEntity load = getLoad();
        load.getNumbers().setBolNumber(INVALID_BOL);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LoadTenderServiceImpl.EDI_OPERATION_TYPE_KEY, ShipmentOperationType.TENDER);
        params.put(LoadTenderServiceImpl.PRODUCTION_MODE_KEY, Boolean.FALSE);
        entities.add(load);
        EDIFile ediFile = sut.create(entities, params);
        Assert.assertNotNull(ediFile);
        Assert.assertFalse(ediFile.getName().contains(" "));
        Assert.assertFalse(ediFile.getName().contains("'"));
    }

    private static void assertFilesContentIdenticalIgnoreCreationDate(InputStream expectedContent, InputStream actualContent) throws IOException {
        try {
            String expectedContentString = replaceDateTimeData(IOUtils.toString(expectedContent));
            String actualContentString = replaceDateTimeData(IOUtils.toString(actualContent));
            Assert.assertEquals(expectedContentString, actualContentString);
        } finally {
            IOUtils.closeQuietly(expectedContent);
            IOUtils.closeQuietly(actualContent);
        }
    }


    private static String replaceDateTimeData(String ediContent) {
        ediContent = replaceDateTimeData(ediContent, ISA_SEGMENT_PATTERN);
        ediContent = replaceDateTimeData(ediContent, GS_SEGMENT_PATTERN);
        return ediContent;
    }

    private static String replaceDateTimeData(String ediContent, Pattern pattern) {
        Matcher matcher = pattern.matcher(ediContent);
        if (matcher.find()) {
            ediContent = ediContent.replace(matcher.group(1), "*");
        }
        return ediContent;
    }

    private LoadEntity getLoad() throws ParseException {
        LoadEntity load = new LoadEntity();
        load.setId(LOAD_ID);
        load.setStatus(ShipmentStatus.DISPATCHED);
        load.setPayTerms(PaymentTerms.PREPAID);

        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        customer.setEdiAccount(EDI);
        load.setOrganization(customer);

        load.getNumbers().setBolNumber(BOL);
        load.getNumbers().setRefNumber(REF);
        load.getNumbers().setPoNumber(PO);
        load.getNumbers().setPuNumber(PU);

        FreightBillPayToEntity freightBillPayTo = new FreightBillPayToEntity();
        freightBillPayTo.setCompany(FREIGHT_BILL_PAY_TO_NAME);
        freightBillPayTo.setAddress(getAddressEntity(FREIGHT_BILL_PAY_TO_ADDRESS1, FREIGHT_BILL_PAY_TO_ADDRESS2, FREIGHT_BILL_PAY_TO_CITY,
                FREIGHT_BILL_PAY_TO_STATE_CODE, FREIGHT_BILL_PAY_TO_STATE_NAME, FREIGHT_BILL_PAY_TO_COUNTRY_CODE, FREIGHT_BILL_PAY_TO_COUNTRY_NAME,
                FREIGHT_BILL_PAY_TO_ZIP, FREIGHT_BILL_PAY_TO_PHONE_COUNTRY_CODE));
        freightBillPayTo.setPhone(getPhoneEntity(FREIGHT_BILL_PAY_TO_PHONE_COUNTRY_CODE, FREIGHT_BILL_PAY_TO_PHONE_AREA_CODE,
                FREIGHT_BILL_PAY_TO_PHONE_NUMBER));
        freightBillPayTo.setContactName(FREIGHT_BILL_PAY_TO_CONTACT_NAME);
        freightBillPayTo.setEmail(FREIGHT_BILL_PAY_TO_EMAIL);
        freightBillPayTo.setAccountNum(FREIGHT_BILL_PAY_TO_ACCOUNTNUM);
        load.setFreightBillPayTo(freightBillPayTo);

        load.setWeight(WEIGHT);
        load.setPieces(PIECES);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setContact(ORIGIN_CONTACT);
        origin.setContactName(ORIGIN_CONTACT_NAME);
        origin.setAddress(getAddressEntity(ORIGIN_ADDRESS1, ORIGIN_ADDRESS2, ORIGIN_CITY, ORIGIN_STATE_CODE, ORIGIN_STATE_NAME, ORIGIN_COUNTRY_CODE,
                ORIGIN_COUNTRY_NAME, ORIGIN_ZIP, ORIGIN_PHONE_COUNTRY_CODE));
        origin.setContactPhone(ORIGIN_CONTACT_PHONE);
        origin.setContactEmail(ORIGIN_CONTACT_EMAIL);
        origin.setArrivalWindowStart(EDIUtils.toDateTime(ORIGIN_ARRIVAL_WINDOW_START));
        origin.setArrivalWindowEnd(EDIUtils.toDateTime(ORIGIN_ARRIVAL_WINDOW_END));
        load.addLoadDetails(origin);

        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destination.setContact(DESTINATION_CONTACT);
        destination.setContactName(DESTINATION_CONTACT_NAME);
        destination.setAddress(getAddressEntity(DESTINATION_ADDRESS1, DESTINATION_ADDRESS2, DESTINATION_CITY, DESTINATION_STATE_CODE,
                DESTINATION_STATE_NAME, DESTINATION_COUNTRY_CODE, DESTINATION_COUNTRY_NAME, DESTINATION_ZIP, DESTINATION_PHONE_COUNTRY_CODE));
        destination.setContactPhone(DESTINATION_CONTACT_PHONE);
        destination.setContactEmail(DESTINATION_CONTACT_EMAIL);
        destination.setDeparture(EDIUtils.toDateTime(DESTINATION_DEPARTURE));
        load.addLoadDetails(destination);

        Set<LoadMaterialEntity> materials = new HashSet<LoadMaterialEntity>();
        materials.add(getLoadMaterialEntity(PRODUCT1_DESCRIPTION, PRODUCT1_COMMODITY_CLASS, PRODUCT1_QUANTITY,
                PRODUCT1_WEIGHT, PRODUCT1_HAZMAT));
        materials.add(getLoadMaterialEntity(PRODUCT2_DESCRIPTION, PRODUCT2_COMMODITY_CLASS, PRODUCT2_QUANTITY,
                PRODUCT2_WEIGHT, PRODUCT2_HAZMAT));
        load.getOrigin().setLoadMaterials(materials);

        load.setSpecialInstructions(SPECIAL_INSTRUCTIONS);
        load.setDeliveryNotes(DELIVERY_NOTES);

        Set<LoadCostDetailsEntity> loadCostDetails = new HashSet<LoadCostDetailsEntity>();
        loadCostDetails.add(getLoadCostDetailsEntity(TOTAL_COST));
        load.setCostDetails(loadCostDetails);

        return load;
    }

    private PhoneEntity getPhoneEntity(String countryCode, String areaCode, String phoneNumber) {
        PhoneEntity phone = new PhoneEntity();
        phone.setType(PhoneType.VOICE);
        phone.setCountryCode(countryCode);
        phone.setAreaCode(areaCode);
        phone.setNumber(phoneNumber);
        return phone;
    }

    private AddressEntity getAddressEntity(String address1, String address2, String city, String stateCode, String stateName, String countryCode,
                                           String countryName, String zip, String phoneCountryCode) {
        AddressEntity address = new AddressEntity();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setState(getStateEntity(countryCode, stateCode, stateName));
        address.setZip(zip);
        address.setCountry(getCountryEntity(countryCode, countryName, phoneCountryCode));
        return address;
    }

    private StateEntity getStateEntity(String countryCode, String stateCode, String stateName) {
        StateEntity state = new StateEntity();
        state.setStateName(stateName);
        StatePK statePK = new StatePK();
        statePK.setStateCode(stateCode);
        statePK.setCountryCode(countryCode);
        state.setStatePK(statePK);
        return state;
    }

    private CountryEntity getCountryEntity(String id, String name, String phoneCode) {
        CountryEntity country = new CountryEntity();
        country.setId(id);
        country.setName(name);
        country.setPhoneCode(phoneCode);
        country.setStatus(Status.ACTIVE);
        return country;
    }

    private LoadMaterialEntity getLoadMaterialEntity(String description, CommodityClass commodityClass, String quantity,
                                                     BigDecimal weight, boolean hazmat) {
        LoadMaterialEntity material = new LoadMaterialEntity();
        material.setProductDescription(description);
        material.setPackageType(getPackageTypeEntity());
        material.setCommodityClass(commodityClass);
        material.setQuantity(quantity);
        material.setWeight(weight);
        material.setHazmat(hazmat);
        return material;
    }

    private LoadCostDetailsEntity getLoadCostDetailsEntity(BigDecimal totalCost) {
        LoadCostDetailsEntity loadCostDetails = new LoadCostDetailsEntity();
        loadCostDetails.setStatus(Status.ACTIVE);
        loadCostDetails.setTotalCost(totalCost);
        return loadCostDetails;
    }

    private InputStream getEdiFile() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("edi" + File.separator + EDI_FILE_NAME);
        Assert.assertNotNull(inputStream);
        return inputStream;
    }

    private PackageTypeEntity getPackageTypeEntity() {
        PackageTypeEntity entity = new PackageTypeEntity();
        entity.setId("PLT");
        entity.setDescription("Pallet");
        return entity;
    }
}

