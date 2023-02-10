package com.pls.invoice.service.impl.processing;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.InvoiceSortType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.organization.PlsCustomerTermsEntity;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.shared.AccessorialType;
import com.pls.core.shared.Status;
import com.pls.invoice.service.processing.InvoiceService;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.bo.LoadAdjustmentBO;

/**
 * Test cases for EDI invoice proccessing service.
 *
 * @author Jasmin Dhamelia
 */

@RunWith(MockitoJUnitRunner.class)
public class EDIInvoiceProcessingServiceImplTest {

    @InjectMocks
    private EDIInvoiceProcessingServiceImpl ediInvoiceProcessingServiceImpl;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Mock
    private AccessorialTypeEntity accessorialTypeEntity;

    @Mock
    private CostDetailItemEntity costDetailItemEntity;

    @Test
    public void testTransactionalInvoicesWithoutAdjustment() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(true, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                getLoadAdjustmentBOList(true, false));
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(1)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testTransactionalInvoicesWithAdjustment() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(true, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                getLoadAdjustmentBOList(true, true));
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(1)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testCBIInvoicesWithAdjustment() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(true, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                getLoadAdjustmentBOList(false, true));
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(1)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testCBIInvoicesWithoutAdjustment() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(true, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                getLoadAdjustmentBOList(false, false));
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(1)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(1)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testDoNotInvoice() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(true, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                new ArrayList<LoadAdjustmentBO>());
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(1)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(0)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    @Test
    public void testIsCustomerEDIEnabled() throws Exception {
        Long invoiceId = (long) (Math.random() * 100);
        BillToEntity billTo = getBillTo(false, InvoiceSortType.LOAD_ID);
        Mockito.when(invoiceService.getSortedInvoices(invoiceId)).thenReturn(
                new ArrayList<LoadAdjustmentBO>());
        ediInvoiceProcessingServiceImpl.sendInvoiceViaEDI(invoiceId, billTo);
        Mockito.verify(invoiceService, times(0)).getSortedInvoices(invoiceId);
        Mockito.verify(sterlingMessageProducer, times(0)).publishMessage((SterlingIntegrationMessageBO) anyObject());
    }

    public List<LoadAdjustmentBO> getLoadAdjustmentBOList(Boolean isTransactional, Boolean isAdjustment) {
        List<LoadAdjustmentBO> list = new ArrayList<LoadAdjustmentBO>();
        if (!isAdjustment) {
            if (isTransactional) {
                LoadAdjustmentBO loadAdjustmentBO = new LoadAdjustmentBO(getLoad());
                list.add(loadAdjustmentBO);
            } else {
                LoadAdjustmentBO loadAdjustmentBO = new LoadAdjustmentBO(getLoad());
                list.add(loadAdjustmentBO);
                list.add(loadAdjustmentBO);
            }
        } else {
            if (isTransactional) {
                LoadAdjustmentBO loadAdjustmentBO = new LoadAdjustmentBO(getFinancialAccessorials());
                list.add(loadAdjustmentBO);
            } else {
                LoadAdjustmentBO loadAdjustmentBO = new LoadAdjustmentBO(getFinancialAccessorials());
                list.add(loadAdjustmentBO);
                list.add(loadAdjustmentBO);
            }
        }

        return list;
    }

    private FinancialAccessorialsEntity getFinancialAccessorials() {
        FinancialAccessorialsEntity financialAccessorialsEntity = new FinancialAccessorialsEntity();
        financialAccessorialsEntity.setLoad(getLoad());
        financialAccessorialsEntity.setGeneralLedgerDate(new Date());
        financialAccessorialsEntity.setTotalRevenue(BigDecimal.valueOf((Math.random() * 100)));
        financialAccessorialsEntity.setTotalCost(BigDecimal.valueOf((Math.random() * 100)));
        financialAccessorialsEntity.setInvoiceNumber("C-0007034-1");

        return financialAccessorialsEntity;
    }

    private LoadEntity getLoad() {
        LoadEntity load = new LoadEntity();
        load.setActiveCostDetails(getActiveCostDetails());
        load.setOrganization(getCustomerEntity());
        load.setCarrier(getCarrierEntity());
        load.getNumbers().setRefNumber("4545");
        load.getNumbers().setProNumber("43234");
        load.setShipmentDirection(ShipmentDirection.OUTBOUND);
        load.setId((long) (Math.random() * 100));
        load.setPayTerms(PaymentTerms.PREPAID);
        load.getNumbers().setGlNumber("4545");
        load.getNumbers().setBolNumber("12312");
        load.getNumbers().setPoNumber("12312456");
        load.getNumbers().setPuNumber("2423424");
        load.getNumbers().setSoNumber("54645");
        load.getNumbers().setTrailerNumber("345345");
        load.setMileage(2324);
        load.setOrganization(new CustomerEntity());
        load.getOrganization().setId((long) (Math.random() * 100));
        load.setLocation(getOrganizationLocationEntity());
        load.addLoadDetails(getOriginLoadDetails(LoadAction.PICKUP, PointType.ORIGIN));
        load.addLoadDetails(getDestinationLoadDetails(LoadAction.DELIVERY, PointType.DESTINATION));
        load.setBillTo(getBillToEntity());
        load.setPayTerms(PaymentTerms.PREPAID);
        load.setMileage((int) (Math.random() * 10));
        load.setWeight((int) (Math.random() * 10));
        load.setPieces((int) (Math.random() * 10));

        return load;
    }

    private OrganizationLocationEntity getOrganizationLocationEntity() {
        OrganizationLocationEntity organizationLocationEntity = new OrganizationLocationEntity();
        organizationLocationEntity.setId((long) (Math.random() * 100));

        return organizationLocationEntity;
    }

    private CarrierEntity getCarrierEntity() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        carrier.setScac("RLCA");

        return carrier;
    }

    private CustomerEntity getCustomerEntity() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName("PLS SHIPPER");
        customerEntity.setId((long) (Math.random() * 100));
        customerEntity.setEdiAccount("12321");

        return customerEntity;
    }

    private Set<LoadCostDetailsEntity> getActiveCostDetails() {
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        loadCostDetailsEntity.setInvoiceNumber("T-7020-0000");
        ZonedDateTime glDate = ZonedDateTime.parse("2015-07-20T17:00:00Z[UTC]");
        loadCostDetailsEntity.setGeneralLedgerDate(Date.from(glDate.toInstant()));
        loadCostDetailsEntity.setTotalRevenue(BigDecimal.valueOf((Math.random() * 100)));
        loadCostDetailsEntity.setTotalCost(BigDecimal.valueOf((Math.random() * 100)));
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setCostDetailItems(getCostDetailItemList());
        activeCostDetails.add(loadCostDetailsEntity);

        return activeCostDetails;
    }

    private Set<CostDetailItemEntity> getCostDetailItemList() {
        Set<CostDetailItemEntity> costDetailItemEntityList = new HashSet<CostDetailItemEntity>();
        costDetailItemEntityList.add(getCostDetailItemEntity(AccessorialType.CARRIER_BASE_RATE, CostDetailOwner.C));
        costDetailItemEntityList.add(getCostDetailItemEntity(AccessorialType.SHIPPER_BASE_RATE, CostDetailOwner.S));
        costDetailItemEntityList.add(getAccessorialDetailItem(AccessorialType.FUEL_SURCHARGE, CostDetailOwner.S));
        costDetailItemEntityList.add(getAccessorialDetailItem(AccessorialType.FUEL_SURCHARGE, CostDetailOwner.C));
        return costDetailItemEntityList;
    }

    private CostDetailItemEntity getCostDetailItemEntity(AccessorialType accessorialType, CostDetailOwner costDetailOwner) {
        CostDetailItemEntity costDetailItemEntity = new CostDetailItemEntity();
        costDetailItemEntity.setAccessorialType(accessorialType.getType());
        costDetailItemEntity.setOwner(costDetailOwner);
        costDetailItemEntity.setNote("Notes");
        costDetailItemEntity.setUnitCost(BigDecimal.valueOf((Math.random() * 100)));
        costDetailItemEntity.setUnitType("FL");
        costDetailItemEntity.setQuantity((long) (Math.random() * 100));
        costDetailItemEntity.setSubtotal(BigDecimal.valueOf((Math.random() * 100)));

        return costDetailItemEntity;
    }

    private CostDetailItemEntity getAccessorialDetailItem(AccessorialType accessorialType, CostDetailOwner costDetailOwner) {
        CostDetailItemEntity costDetailItemEntity = getCostDetailItemEntity(accessorialType, costDetailOwner);

        try {
            // reflection usage to set AccessorialDictionary field value
            AccessorialTypeEntity accessorialTypeEntity = new AccessorialTypeEntity();
            accessorialTypeEntity.setId(accessorialType.getType());
            accessorialTypeEntity.setDescription("FUEL SURCHARGE");
            accessorialTypeEntity.setAccessorialGroup(LtlAccessorialGroup.DELIVERY);
            Field carrierField = costDetailItemEntity.getClass().getDeclaredField("accessorialDictionary");
            carrierField.setAccessible(true);
            carrierField.set(costDetailItemEntity, accessorialTypeEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costDetailItemEntity;
    }

    private LoadDetailsEntity getOriginLoadDetails(LoadAction loadAction, PointType pointType) {
        LoadDetailsEntity originLoadDetailsEntity = setLoadMaterials(loadAction, pointType);
        setAddress(originLoadDetailsEntity);
        return originLoadDetailsEntity;
    }

    private LoadDetailsEntity getDestinationLoadDetails(LoadAction loadAction, PointType pointType) {
        LoadDetailsEntity destinationLoadDetailsEntity = new LoadDetailsEntity(loadAction, pointType);
        setAddress(destinationLoadDetailsEntity);
        return destinationLoadDetailsEntity;
    }

    private void setAddress(LoadDetailsEntity loadDetailsEntity) {
        loadDetailsEntity.setContact("Myesha Varn");
        loadDetailsEntity.setAddressCode("LT-00000003");
        loadDetailsEntity.setContactName("Rey Eicher");
        loadDetailsEntity.setContactEmail("steel.corporation@test.com");
        loadDetailsEntity.setContactFax("+1 (58) 4540214");
        loadDetailsEntity.setContactPhone("+1 (1) 3841215");
        loadDetailsEntity.setAddress(getAddressEntity());
        loadDetailsEntity.setDeparture(new Date());
        loadDetailsEntity.setScheduledArrival(new Date());
    }

    private BillToEntity getBillToEntity() {
        BillToEntity billToEntity = new BillToEntity();
        billToEntity.setId((long) (Math.random() * 100));
        billToEntity.setCurrency(Currency.USD);
        billToEntity.setBillingInvoiceNode(getBillingInvoiceNode());

        return billToEntity;

    }

    private BillingInvoiceNodeEntity getBillingInvoiceNode() {
        BillingInvoiceNodeEntity billingInvoiceNodeEntity = new BillingInvoiceNodeEntity();
        billingInvoiceNodeEntity.setAddress(getAddressEntity());
        billingInvoiceNodeEntity.setContactName("DAN MULLINS");
        billingInvoiceNodeEntity.setEmail("DTMULLINT@test.com");
        billingInvoiceNodeEntity.setPhone(getOrganizationVoicePhoneEntity());
        billingInvoiceNodeEntity.setFax(getOrganizationFaxPhoneEntity());

        return billingInvoiceNodeEntity;
    }

    private PhoneEntity getOrganizationFaxPhoneEntity() {
        PhoneEntity faxEntity = new PhoneEntity();
        faxEntity.setCountryCode("1");
        faxEntity.setAreaCode("276");
        faxEntity.setNumber("1231231234");

        return faxEntity;
    }

    private PhoneEntity getOrganizationVoicePhoneEntity() {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setCountryCode("1");
        phoneEntity.setAreaCode("276");
        phoneEntity.setNumber("1231231234");

        return phoneEntity;

    }

    private AddressEntity getAddressEntity() {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress1("C/O BAR PROCESSING");
        addressEntity.setAddress2("1000 WINDHAM RD");
        addressEntity.setCity("NEWTON FALLS");
        addressEntity.setStateCode("OH");
        addressEntity.setZip("44444");
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setId("USA");
        addressEntity.setCountry(countryEntity);

        return addressEntity;
    }

    private LoadDetailsEntity setLoadMaterials(LoadAction loadAction, PointType pointType) {
        LoadDetailsEntity originLoadDetailsEntity = new LoadDetailsEntity(loadAction, pointType);
        Set<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        LoadMaterialEntity loadMaterialEntity = new LoadMaterialEntity();
        loadMaterialEntity.setWeight(BigDecimal.valueOf((Math.random() * 100)));
        loadMaterialEntity.setQuantity(String.valueOf((int) (Math.random() * 10)));
        loadMaterialEntity.setPieces((int) (Math.random() * 10));
        loadMaterialEntity.setCommodityClass(CommodityClass.CLASS_50);
        loadMaterialEntity.setProductCode("7A12345-WHT-XL");
        loadMaterialEntity.setProductDescription("Miller");
        loadMaterialEntity.setLength(BigDecimal.valueOf((Math.random() * 10)));
        loadMaterialEntity.setWeight(BigDecimal.valueOf((Math.random() * 10)));
        loadMaterialEntity.setHeight(BigDecimal.valueOf((Math.random() * 10)));
        loadMaterialEntity.setQuantity("3");
        loadMaterialEntity.setStackable(Boolean.TRUE);
        loadMaterialEntity.setNmfc("15500-100");
        loadMaterialEntity.setPackageType(getPackageTypeEntity());
        loadMaterialEntity.setHazmat(Boolean.TRUE);
        loadMaterialEntity.setUnNumber("63055934");
        loadMaterialEntity.setPackingGroup("CMVX");
        loadMaterialEntity.setEmergencyCompany("Chesapeake Energy Corporation ");
        loadMaterialEntity.setEmergencyPhone(getPhoneEmbeddableObject());
        loadMaterialEntity.setEmergencyContract("195114405");
        loadMaterialEntity.setHazmatInstruction("Be carful with material");
        loadMaterialEntity.setHazmatClass("9");
        loadMaterials.add(loadMaterialEntity);
        originLoadDetailsEntity.setLoadMaterials(loadMaterials);
        return originLoadDetailsEntity;
    }

    private PackageTypeEntity getPackageTypeEntity() {
        PackageTypeEntity packageTypeEntity = new PackageTypeEntity();
        packageTypeEntity.setId("BOX");
        packageTypeEntity.setDescription("Boxes");

        return packageTypeEntity;
    }

    private PhoneEmbeddableObject getPhoneEmbeddableObject() {
        PhoneEmbeddableObject phoneEmbeddableObject = new PhoneEmbeddableObject();
        phoneEmbeddableObject.setCountryCode("1");
        phoneEmbeddableObject.setAreaCode("724");
        phoneEmbeddableObject.setNumber("7099000");

        return phoneEmbeddableObject;
    }

    private BillToEntity getBillTo(Boolean bool, InvoiceSortType invoiceSortType) {
        BillToEntity billToEntity = new BillToEntity();
        InvoiceSettingsEntity invoiceSettingsEntity = new InvoiceSettingsEntity();
        invoiceSettingsEntity.setEdiInvoice(bool);
        invoiceSettingsEntity.setSortType(invoiceSortType);
        billToEntity.setInvoiceSettings(invoiceSettingsEntity);

        PlsCustomerTermsEntity plsCustomerTermsEntity = new PlsCustomerTermsEntity();
        plsCustomerTermsEntity.setTermName("Net 5 days");
        plsCustomerTermsEntity.setDueDays(5);
        billToEntity.setPlsCustomerTerms(plsCustomerTermsEntity);

        return billToEntity;
    }
}
