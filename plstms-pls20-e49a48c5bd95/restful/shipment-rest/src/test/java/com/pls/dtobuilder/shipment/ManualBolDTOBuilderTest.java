package com.pls.dtobuilder.shipment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.ManualBolStatus;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.dto.FreightBillPayToDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.enums.PaymentTermsDTO;
import com.pls.dto.shipment.JobNumberDTO;
import com.pls.dto.shipment.ManualBolAddressDTO;
import com.pls.dto.shipment.ManualBolDTO;
import com.pls.dto.shipment.ManualBolMaterialDTO;
import com.pls.dtobuilder.shipment.ManualBolDTOBuilder.DataProvider;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolJobNumberEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.domain.ManualBolNumbersEntity;
import com.pls.shipment.domain.ManualBolRequestedByNoteEntity;
import com.pls.shipment.domain.PackageTypeEntity;
/**
 * Test cases for {@link ManualBolDTOBuilder} class.
 * 
 * @author Alexander Nalapko
 * 
 *
 */
public class ManualBolDTOBuilderTest {

    private ManualBolDTOBuilder dtoBuilder = new ManualBolDTOBuilder(new DataProvider() {

        @Override
        public PackageTypeEntity findPackageType(String id) {
            return null;
        }

        @Override
        public ManualBolEntity getManualBol(Long id) {
            return null;
        }

        @Override
        public FreightBillPayToEntity getFreightBillPayTo(Long id) {
            return null;
        }

        @Override
        public FreightBillPayToEntity getDefaultFreightBillPayTo() {
            FreightBillPayToEntity bo = new FreightBillPayToEntity();
            AddressEntity address = new AddressEntity();
            address.setCountry(new CountryEntity());
            bo.setAddress(address);
            return bo;
        }

        @Override
        public CarrierEntity getCarrier(Long id) {
            CarrierEntity bo = new CarrierEntity();
            bo.setId(id);
            bo.setName("name");
            return bo;
        }

        @Override
        public BillToEntity getBillTo(Long id) {
            BillToEntity bo = new BillToEntity();
            bo.setId(id);
            BillingInvoiceNodeEntity billingInvoiceNode = new BillingInvoiceNodeEntity();
            billingInvoiceNode.setAddress(getAddressEntity());
            bo.setBillingInvoiceNode(billingInvoiceNode);
            bo.setAuditPrefReq(false);
            return bo;
        }

        @Override
        public AddressEntity getAddress(Long id) {
            return null;
        }

        @Override
        public Boolean isPrintBarcode(Long customerId) {
            return false;
        }
    });

    @Test
    public void shouldBuildDTO() {
        ManualBolEntity bo = createManualBolEntity();
        ManualBolDTO dto = dtoBuilder.buildDTO(bo);

        assertNotNull(dto);
        assertSame(bo.getId(), dto.getId());
        assertSame(bo.getStatus(), dto.getStatus());
        assertSame(bo.getOrganization().getId(), dto.getOrganizationId());
        assertSame(bo.getOrganization().getName(), dto.getCustomerName());
        assertSame(bo.getNumbers().getBolNumber(), dto.getBol());
        assertSame(bo.getNumbers().getProNumber(), dto.getPro());
        assertSame(bo.getNumbers().getPuNumber(), dto.getPu());
        assertSame(bo.getNumbers().getRefNumber(), dto.getRef());
        assertSame(bo.getNumbers().getSoNumber(), dto.getSoNumber());
        assertSame(bo.getNumbers().getTrailerNumber(), dto.getTrailer());
        assertNotNull(dto.getBillTo());
        assertSame(bo.getBillTo().getId(), dto.getBillTo().getId());
        assertSame(bo.getLocation().getId(), dto.getLocation().getId());
        assertSame(bo.getLocation().getLocationName(), dto.getLocation().getName());
        assertSame(bo.getLocation().getBillTo().getId(), dto.getLocation().getBillToId());

        assertSame(bo.getOrigin().getAddress().getCity(), dto.getOrigin().getZip().getCity());
        assertSame(bo.getOrigin().getAddress().getCountry().getId(), dto.getOrigin().getZip().getCountry().getId());
        assertSame(bo.getOrigin().getAddress().getStateCode(), dto.getOrigin().getZip().getState());
        assertSame(bo.getOrigin().getAddress().getZip(), dto.getOrigin().getZip().getZip());

        assertSame(bo.getRequestedBy().getNote(), dto.getRequestedBy());
        assertSame(bo.getDestination().getAddress().getCity(), dto.getDestination().getZip().getCity());
        assertSame(bo.getDestination().getAddress().getCountry().getId(), dto.getDestination().getZip().getCountry()
                .getId());
        assertSame(bo.getDestination().getAddress().getStateCode(), dto.getDestination().getZip().getState());
        assertSame(bo.getDestination().getAddress().getZip(), dto.getDestination().getZip().getZip());

        assertNotNull(dto.getJobNumbers());
        Assert.assertFalse(dto.getJobNumbers().isEmpty());
    }

    @Test
    public void buildEntity() {
        ManualBolDTO dto = createManualBolDTO();
        ManualBolEntity bo = dtoBuilder.buildEntity(dto);

        assertNotNull(dto);
        assertSame(bo.getId(), dto.getId());
        assertSame(bo.getVersion(), dto.getVersion());
        assertSame(bo.getOrganization().getId(), dto.getOrganizationId());
        assertSame(bo.getStatus(), dto.getStatus());
        assertSame(bo.getNumbers().getBolNumber(), dto.getBol());
        assertSame(bo.getNumbers().getProNumber(), dto.getPro());
        assertSame(bo.getNumbers().getPuNumber(), dto.getPu());
        assertSame(bo.getNumbers().getRefNumber(), dto.getRef());
        assertSame(bo.getNumbers().getSoNumber(), dto.getSoNumber());
        assertSame(bo.getNumbers().getTrailerNumber(), dto.getTrailer());
        assertNotNull(dto.getBillTo());
        assertSame(bo.getBillTo().getId(), dto.getBillTo().getId());

        assertSame(bo.getLocation().getId(), dto.getLocation().getId());

        assertNotNull(dto.getOrigin().getAddressId());
        assertNotNull(dto.getDestination().getAddressId());
        assertSame(dto.getOrigin().getContactName(), bo.getOrigin().getContactName());
        assertSame(dto.getDestination().getContactName(), bo.getDestination().getContactName());

        assertSame(bo.getCarrier().getId(), dto.getCarrier().getId());
        assertSame(bo.getCarrier().getName(), dto.getCarrier().getName());

        assertSame(bo.getMaterials().size(), dto.getMaterials().size());
        assertSame(bo.getRequestedBy().getNote(), dto.getRequestedBy());
        assertSame(bo.getOrganization().getId(), dto.getOrganizationId());

        assertSame(bo.getCustomsBroker(), dto.getCustomsBroker());
        assertSame(bo.getPickupDate(), dto.getPickupDate());

        assertNotNull(bo.getNumbers().getJobNumbers());
        Assert.assertFalse(bo.getNumbers().getJobNumbers().isEmpty());
    }

    @Test
    public void buildEntityWithoutFreightBillPayTo() {
        ManualBolDTO dto = createManualBolDTO();
        dto.setFreightBillPayTo(null);
        ManualBolEntity bo = dtoBuilder.buildEntity(dto);
        assertNotNull(bo);
        assertNotNull(bo.getFreightBillPayTo());
    }

    @Test
    public void buildEntityWithoutFreightBillPayToId() {
        ManualBolDTO dto = createManualBolDTO();
        dto.getFreightBillPayTo().setId(null);
        ManualBolEntity bo = dtoBuilder.buildEntity(dto);
        assertNotNull(bo);
        assertNotNull(bo.getFreightBillPayTo());
    }

    private ManualBolEntity createManualBolEntity() {
        ManualBolEntity bo = new ManualBolEntity();
        bo.setBillTo(getBillToEntity());
        bo.setCarrier(getCarrierEntity());
        bo.setCustomsBroker("customsBroker" + Math.random());
        bo.setCustomsBrokerPhone(createPhone());
        bo.setDeliveryNotes("deliveryNotes" + Math.random());
        bo.setDeliveryWindowFrom(new Date());
        bo.setDeliveryWindowTo(new Date());
        bo.setId((long) (Math.random() * 100));
        bo.setLocation(getLocationEntity());
        bo.setMaterials(getMaterialEntity());
        bo.setNumbers(getNumbersEntity());
        bo.setOrganization(getCustomerEntity());
        bo.setPayTerms(PaymentTerms.PREPAID);
        bo.setRequestedBy(new ManualBolRequestedByNoteEntity());
        bo.setPickupDate(new Date());
        bo.setPickupNotes("pickupNotes" + Math.random());
        bo.setPickupWindowFrom(new Date());
        bo.setPickupWindowTo(new Date());
        bo.setShippingNotes("shippingNotes" + Math.random());
        bo.setStatus(ManualBolStatus.CUSTOMER_TRUCK);
        bo.setVersion((int) (Math.random() * 100));
        bo.addAddress(getManualBolAddressEntity(PointType.ORIGIN));
        bo.addAddress(getManualBolAddressEntity(PointType.DESTINATION));
        return bo;
    }

    private Set<ManualBolMaterialEntity> getMaterialEntity() {
        Set<ManualBolMaterialEntity> list = new HashSet<ManualBolMaterialEntity>();
        for (int i = 0; i < Math.random() * 10; i++) {
            ManualBolMaterialEntity bo = new ManualBolMaterialEntity();
            bo.setCommodityClass(CommodityClass.values()[(int) (Math.random() * (CommodityClass.values().length - 1))]);
            bo.setHazmat(Math.random() > 0.5);
            bo.setWeight(BigDecimal.ONE);
            list.add(bo);
        }
        return list;
    }

    private String createPhone() {
        return (int) (Math.random() * 999) + "-" + (int) (Math.random() * 999) + "-" + (int) (Math.random() * 9999999);
    }

    private CustomerEntity getCustomerEntity() {
        CustomerEntity bo = new CustomerEntity();
        bo.setId((long) (Math.random() * 100));
        bo.setName("name" + Math.random());
        bo.setProductListPrimarySort(Math.random() > 0.5 ? ProductListPrimarySort.PRODUCT_DESCRIPTION
                : ProductListPrimarySort.SKU_PRODUCT_CODE);
        return bo;
    }

    private ManualBolNumbersEntity getNumbersEntity() {
        ManualBolNumbersEntity bo = new ManualBolNumbersEntity();
        bo.setBolNumber("bol" + Math.random());
        bo.setGlNumber("gl" + Math.random());
        bo.setPoNumber("po" + Math.random());
        bo.setProNumber("pro" + Math.random());
        bo.setPuNumber("pu" + Math.random());
        bo.setRefNumber("ref" + Math.random());
        bo.setSoNumber("so" + Math.random());
        bo.setTrailerNumber("trailer" + Math.random());
        bo.setJobNumbers(Sets.newHashSet(new ManualBolJobNumberEntity((long) Math.random() * 100, String.valueOf(Math.random())),
                new ManualBolJobNumberEntity((long) Math.random() * 100, String.valueOf(Math.random()))));
        return bo;
    }

    private OrganizationLocationEntity getLocationEntity() {
        OrganizationLocationEntity bo = new OrganizationLocationEntity();
        bo.setId((long) (Math.random() * 100));
        bo.setLocationName("locationName" + Math.random());
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        bo.setBillTo(billTo);
        return bo;
    }

    private CarrierEntity getCarrierEntity() {
        CarrierEntity bo = new CarrierEntity();
        bo.setId((long) (Math.random() * 100));
        bo.setName("name" + Math.random());
        return bo;
    }

    private BillToEntity getBillToEntity() {
        BillToEntity bo = new BillToEntity();
        bo.setId((long) (Math.random() * 100));
        BillingInvoiceNodeEntity billingInvoiceNode = new BillingInvoiceNodeEntity();
        billingInvoiceNode.setAddress(getAddressEntity());
        bo.setBillingInvoiceNode(billingInvoiceNode);
        bo.setAuditPrefReq(false);
        return bo;
    }

    private ManualBolAddressEntity getManualBolAddressEntity(PointType type) {
        ManualBolAddressEntity bo = new ManualBolAddressEntity(type);
        bo.setAddress(getAddressEntity());
        bo.setContactName("contactName" + Math.random());
        bo.setId((long) (Math.random() * 100));
        return bo;
    }

    private AddressEntity getAddressEntity() {
        AddressEntity bo = new AddressEntity();
        bo.setCity("city" + Math.random());
        CountryEntity country = new CountryEntity();
        country.setId("id" + Math.random());
        bo.setCountry(country);
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("stateCode" + Math.random());
        state.setStatePK(statePK);
        bo.setState(state);
        bo.setZip("zip" + Math.random());
        return bo;
    }

    private ManualBolDTO createManualBolDTO() {
        ManualBolDTO dto = new ManualBolDTO();
        dto.setBillTo(getBillToDTO());
        dto.setBol("bol" + Math.random());
        dto.setCarrier(getCarrierDTO());
        dto.setCustomsBroker("customsBroker" + Math.random());
        dto.setCustomsBrokerPhone(getPhoneBO());
        dto.setDeliveryNotes("deliveryNotes" + Math.random());
        dto.setDestination(getManualBolAddressDTO());
        dto.setFreightBillPayTo(getFreightBillPayToDTO());
        dto.setGlNumber("gl" + Math.random());
        dto.setLocation(getShipmentLocationBO());
        dto.setMaterials(getMaterialDTO());
        dto.setOrganizationId(1L);
        dto.setOrigin(getManualBolAddressDTO());
        dto.setPaymentTerms(PaymentTermsDTO.COLLECT);
        dto.setPickupDate(new Date());
        dto.setPickupNotes("pickupNotes" + Math.random());
        dto.setPo("po" + Math.random());
        dto.setPro("pro" + Math.random());
        dto.setPu("pu" + Math.random());
        dto.setRef("ref" + Math.random());
        dto.setShippingNotes("shippingNotes");
        dto.setSoNumber("so" + Math.random());
        dto.setStatus(ManualBolStatus.CUSTOMER_TRUCK);
        dto.setTrailer("trailer" + Math.random());
        dto.setVersion((int) (Math.random() * 100));
        dto.setDeliveryWindowFrom(getPickupAndDeliveryWindowDTO());
        dto.setDeliveryWindowTo(getPickupAndDeliveryWindowDTO());
        dto.setJobNumbers(buildJobNumbersDtoList());
        dto.setRequestedBy("Requested by");
        return dto;
    }

    private List<JobNumberDTO> buildJobNumbersDtoList() {
        return Lists.newArrayList(new JobNumberDTO(null, String.valueOf(Math.random())), new JobNumberDTO(null, String.valueOf(Math.random())),
                new JobNumberDTO(null, String.valueOf(Math.random())));
    }

    private PickupAndDeliveryWindowDTO getPickupAndDeliveryWindowDTO() {
        return new PickupAndDeliveryWindowDTO((int) (Math.random()), (int) (Math.random()), true);
    }

    private ShipmentLocationBO getShipmentLocationBO() {
        ShipmentLocationBO bo = new ShipmentLocationBO();
        bo.setName("name" + Math.random());
        bo.setBillToId((long) (Math.random() * 100));
        return bo;
    }

    private FreightBillPayToDTO getFreightBillPayToDTO() {
        FreightBillPayToDTO dto = new FreightBillPayToDTO();
        dto.setAccountNum("accountNum" + Math.random());
        dto.setAddress(getAddressBookDTO());
        dto.setCompany("company" + Math.random());
        dto.setContactName("contactName" + Math.random());
        dto.setEmail("email" + Math.random() + "@mail.com");
        dto.setId((long) (Math.random() * 100));
        dto.setPhone(getPhoneBO());
        return dto;
    }

    private CarrierDTO getCarrierDTO() {
        CarrierDTO dto = new CarrierDTO();
        dto.setId((long) (Math.random() * 100));
        dto.setScac("scac" + Math.random());
        dto.setName("name");
        dto.setCurrencyCode(Currency.USD);
        return dto;
    }

    private ManualBolAddressDTO getManualBolAddressDTO() {
        ManualBolAddressDTO dto = new ManualBolAddressDTO();
        dto.setZip(getZip());
        dto.setPickupNotes("pickupNotes" + Math.random());
        dto.setDeliveryNotes("deliveryNotes" + Math.random());
        dto.setAddressId((long) (Math.random() * 100));
        return dto;
    }

    private PhoneBO getPhoneBO() {
        PhoneBO dto = new PhoneBO();
        dto.setCountryCode("countryCode" + Math.random());
        dto.setAreaCode("areaCode" + Math.random());
        dto.setNumber("number" + Math.random());
        return dto;
    }

    private BillToDTO getBillToDTO() {
        BillToDTO dto = new BillToDTO();
        dto.setAddress(getAddressBookDTO());
        dto.setId((long) (Math.random() * 100));
        return dto;
    }

    private AddressBookEntryDTO getAddressBookDTO() {
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        address.setAddressId((long) (Math.random() * 100));
        address.setZip(getZip());
        address.setPickupNotes("pickupNotes" + Math.random());
        address.setDeliveryNotes("deliveryNotes" + Math.random());
        return address;
    }

    private List<ManualBolMaterialDTO> getMaterialDTO() {
        List<ManualBolMaterialDTO> list = new ArrayList<ManualBolMaterialDTO>();
        for (int i = 0; i < Math.random() * 10; i++) {
            ManualBolMaterialDTO dto = new ManualBolMaterialDTO();
            dto.setCommodityClass(CommodityClassDTO.values()[(int) (Math.random() * (CommodityClassDTO.values().length - 1))]);
            dto.setHazmat(Math.random() > 0.5);
            dto.setWeight(BigDecimal.ONE);
            list.add(dto);
        }
        return list;
    }

    private ZipDTO getZip() {
        ZipDTO result = new ZipDTO();
        result.setCity("city" + Math.random());
        result.setCountry(new CountryDTO("country" + Math.random()));
        result.setState("state" + Math.random());
        result.setZip("zip" + Math.random());
        return result;
    }
}
