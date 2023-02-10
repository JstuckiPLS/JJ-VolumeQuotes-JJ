package com.pls.dtobuilder.shipment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillingInvoiceNodeEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.BillToDTO;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.CustomsBrokerDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.enums.CommodityClassDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentDetailsDTO;
import com.pls.dto.shipment.ShipmentFinishOrderDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.shipment.ShipmentDTOBuilder.DataProvider;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.enums.CommodityCd;
import com.pls.shipment.domain.enums.ShipmentSourceIndicator;
import com.pls.shipment.service.ShipmentService;

/**
 * Test cases for {@link ShipmentDTOBuilder}.
 * 
 * @author Andrey Kachur
 * @author Aleksandr Leshchenko
 */
public class ShipmentDTOBuilderTest {
    private final ShipmentDTOBuilder shipmentDTOBuilder = new ShipmentDTOBuilder(new DataProvider() {
        @Override
        public BillToEntity getBillTo(Long id) {
            return null;
        }

        @Override
        public AddressEntity getAddress(Long id) {
            return null;
        }

        @Override
        public LoadEntity getLoadById(Long id) {
            if (id == null) {
                return new LoadEntity();
            }
            LoadEntity loadEntity = new LoadEntity();
            loadEntity.setId(id);
            return loadEntity;
        }

        @Override
        public FreightBillPayToEntity getDefaultFreightBillPayTo() {
            FreightBillPayToEntity freightBillPayTo = new FreightBillPayToEntity();
            AddressEntity address = new AddressEntity();
            address.setCountry(new CountryEntity());
            freightBillPayTo.setAddress(address);
            return freightBillPayTo;
        }

        @Override
        public PackageTypeEntity findPackageType(String id) {
            PackageTypeEntity entity = new PackageTypeEntity();
            entity.setId(id);
            return entity;
        }

        @Override
        public QuotedBO getPrimaryLoadCostDetail(Long id) {
            return null;
        }

        @Override
        public Boolean isPrintBarcode(Long customerId) {
            return false;
        }

        @Override
        public LtlPricingProfileEntity getProfileById(Long pricingProfileDetailId) {
            return null;
        }
    });

    @Test(expected = IllegalArgumentException.class)
    public void test() {
        shipmentDTOBuilder.buildDTO(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2() {
        shipmentDTOBuilder.buildEntity(null);
    }

    @Test
    public void shouldBuildDTO() {
        LoadEntity entity = getEntity();

        ShipmentDTO dto = shipmentDTOBuilder.buildDTO(entity);

        assertNotNull(dto);
        assertSame(entity.getId(), dto.getId());
        assertSame(entity.getVersion(), dto.getVersion());
        assertSame(entity.getOrganization().getId(), dto.getOrganizationId());
        assertSame(entity.getOrganization().getName(), dto.getCustomerName());
        assertSame(entity.getOrganization().getProductListPrimarySort(), dto.getProductListPrimarySort());
        assertSame(entity.getStatus(), dto.getStatus());
        assertSame(entity.getNumbers().getBolNumber(), dto.getBolNumber());
        assertSame(entity.getNumbers().getProNumber(), dto.getProNumber());
        assertNotNull(dto.getSelectedProposition());
        assertNotNull(dto.getFinishOrder());
        assertNotNull(dto.getBillTo());
        assertSame(entity.getBillTo().getId(), dto.getBillTo().getId());
        assertSame(entity.getLocationId(), dto.getLocation().getId());
        assertSame(entity.getLocation().getLocationName(), dto.getLocation().getName());
        assertSame(entity.getLocation().getBillTo().getId(), dto.getLocation().getBillToId());
        assertNotNull(dto.getOriginDetails().getAddress());
        LoadDetailsEntity originLoadDetails = getLoadDetailsByPointType(entity.getLoadDetails(), PointType.ORIGIN);
        assertNotNull(dto.getDestinationDetails().getAddress());
        LoadDetailsEntity destinationLoadDetails = getLoadDetailsByPointType(entity.getLoadDetails(), PointType.DESTINATION);

        for (LtlLoadAccessorialEntity accessorial : entity.getLtlAccessorials()) {
            ShipmentDetailsDTO details = accessorial.getAccessorial().getAccessorialGroup() == LtlAccessorialGroup.PICKUP
                    ? dto.getOriginDetails() : dto.getDestinationDetails();
            assertTrue(details.getAccessorials().contains(accessorial.getAccessorial().getId()));
        }

        assertSame(originLoadDetails.getAddress().getCity(), dto.getOriginDetails().getZip().getCity());
        assertSame(originLoadDetails.getAddress().getCountry().getId(), dto.getOriginDetails().getZip().getCountry().getId());
        assertSame(originLoadDetails.getAddress().getState().getStatePK().getStateCode(), dto.getOriginDetails().getZip().getState());
        assertSame(originLoadDetails.getAddress().getZip(), dto.getOriginDetails().getZip().getZip());
        assertSame(originLoadDetails.getNotes(), dto.getOriginDetails().getAddress().getPickupNotes());
        assertNull(dto.getOriginDetails().getAddress().getDeliveryNotes());

        assertSame(destinationLoadDetails.getAddress().getCity(), dto.getDestinationDetails().getZip().getCity());
        assertSame(destinationLoadDetails.getAddress().getCountry().getId(), dto.getDestinationDetails().getZip().getCountry().getId());
        assertSame(destinationLoadDetails.getAddress().getState().getStatePK().getStateCode(), dto.getDestinationDetails().getZip().getState());
        assertSame(destinationLoadDetails.getAddress().getZip(), dto.getDestinationDetails().getZip().getZip());
        assertSame(destinationLoadDetails.getNotes(), dto.getDestinationDetails().getAddress().getDeliveryNotes());
        assertNull(dto.getDestinationDetails().getAddress().getPickupNotes());
        assertSame(entity.getModification().getCreatedDate(), dto.getCreatedDate());

        assertSame(entity.getMileage(), dto.getSelectedProposition().getMileage());
        assertSame(entity.getCarrier().getId(), dto.getSelectedProposition().getCarrier().getId());
        assertSame(entity.getCarrier().getName(), dto.getSelectedProposition().getCarrier().getName());
        assertEquals(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()), dto.getSelectedProposition().getCarrier().getLogoPath());

        assertSame(originLoadDetails.getArrival(), dto.getSelectedProposition().getEstimatedTransitDate());
        assertSame(entity.getTravelTime(), dto.getSelectedProposition().getEstimatedTransitTime());
        assertSame(entity.getNumbers().getRefNumber(), dto.getFinishOrder().getRef());

        LoadCostDetailsEntity activeCostDetail = entity.getActiveCostDetail();
        assertSame(activeCostDetail.getId(), dto.getSelectedProposition().getId());
        assertSame(activeCostDetail.getVersion(), dto.getSelectedProposition().getVersion());
        assertSame(activeCostDetail.getNewLiability(), dto.getSelectedProposition().getNewLiability());
        assertSame(activeCostDetail.getUsedLiability(), dto.getSelectedProposition().getUsedLiability());
        assertSame(activeCostDetail.getProhibitedCommodities(), dto.getSelectedProposition().getProhibited());
        assertSame(activeCostDetail.getServiceType(), dto.getSelectedProposition().getServiceType());
        assertSame(activeCostDetail.getPricingProfileDetailId(), dto.getSelectedProposition().getPricingProfileId());

        List<CostDetailItemBO> costDetailItemsDTO = dto.getSelectedProposition().getCostDetailItems();
        Collections.sort(costDetailItemsDTO, new Comparator<CostDetailItemBO>() {
            @Override
            public int compare(CostDetailItemBO o1, CostDetailItemBO o2) {
                int result = 0;
                if (o1.getGuaranteedBy() == null && o2.getGuaranteedBy() != null) {
                    result = -1;
                } else if (o1.getGuaranteedBy() != null && o2.getGuaranteedBy() == null) {
                    result = 1;
                } else if (o1.getGuaranteedBy() != null && o2.getGuaranteedBy() != null) {
                    result = o1.getGuaranteedBy().compareTo(o2.getGuaranteedBy());
                }
                return result;
            }
        });

        assertSame(activeCostDetail.getGuaranteedBy(), dto.getGuaranteedBy());
        assertNull(costDetailItemsDTO.get(0).getGuaranteedBy());
        assertNull(costDetailItemsDTO.get(1).getGuaranteedBy());
        assertEquals(activeCostDetail.getGuaranteedBy(), costDetailItemsDTO.get(2).getGuaranteedBy());
        assertEquals(activeCostDetail.getGuaranteedBy(), costDetailItemsDTO.get(3).getGuaranteedBy());

        Set<CostDetailItemEntity> costDetailItems = activeCostDetail.getCostDetailItems();
        assertEquals(costDetailItems.size(), costDetailItemsDTO.size());
        for (int i = 0; i < costDetailItems.size(); i++) {
            CostDetailItemBO itemDTO = costDetailItemsDTO.get(i);
            CostDetailItemEntity itemEntity = getCostDetailByTypeAndOwner(costDetailItems, itemDTO);
            assertNotNull(itemEntity);
            assertSame(itemEntity.getSubtotal(), itemDTO.getSubTotal());
        }
    }

    @Test
    public void shouldBuildDTOWithoutCarrierAndCostDetails() {
        LoadEntity entity = getEntity();
        entity.setCostDetails(null);
        entity.setCarrier(null);
        entity.getLocation().setBillTo(null);

        ShipmentDTO dto = shipmentDTOBuilder.buildDTO(entity);

        assertNotNull(dto);
        assertSame(entity.getId(), dto.getId());
        assertSame(entity.getStatus(), dto.getStatus());
        assertSame(entity.getOrganization().getId(), dto.getOrganizationId());
        assertSame(entity.getOrganization().getName(), dto.getCustomerName());
        assertSame(entity.getOrganization().getProductListPrimarySort(), dto.getProductListPrimarySort());
        assertSame(entity.getNumbers().getBolNumber(), dto.getBolNumber());
        assertSame(entity.getNumbers().getProNumber(), dto.getProNumber());
        assertNotNull(dto.getSelectedProposition());
        assertNull(dto.getSelectedProposition().getCarrier());
        assertNull(dto.getGuaranteedBy());
        assertNotNull(dto.getFinishOrder());
        assertNotNull(dto.getBillTo());
        assertSame(entity.getBillTo().getId(), dto.getBillTo().getId());
        assertSame(entity.getLocation().getId(), dto.getLocation().getId());
        assertSame(entity.getLocation().getLocationName(), dto.getLocation().getName());
        assertNull(dto.getLocation().getBillToId());
        assertNotNull(dto.getOriginDetails().getAddress());
        assertNull(dto.getOriginDetails().getAddress().getId());
        assertNotNull(dto.getDestinationDetails().getAddress());
        assertNull(dto.getDestinationDetails().getAddress().getId());

        for (LtlLoadAccessorialEntity accessorial : entity.getLtlAccessorials()) {
            ShipmentDetailsDTO details = accessorial.getAccessorial().getAccessorialGroup() == LtlAccessorialGroup.PICKUP
                    ? dto.getOriginDetails() : dto.getDestinationDetails();
            assertTrue(details.getAccessorials().contains(accessorial.getAccessorial().getId()));
        }

        assertSame(entity.getOrigin().getAddress().getCity(), dto.getOriginDetails().getZip().getCity());
        assertSame(entity.getOrigin().getAddress().getCountry().getId(), dto.getOriginDetails().getZip().getCountry().getId());
        assertSame(entity.getOrigin().getAddress().getStateCode(), dto.getOriginDetails().getZip().getState());
        assertSame(entity.getOrigin().getAddress().getZip(), dto.getOriginDetails().getZip().getZip());
        assertSame(entity.getOrigin().getNotes(), dto.getOriginDetails().getAddress().getPickupNotes());
        assertNull(dto.getOriginDetails().getAddress().getDeliveryNotes());

        assertSame(entity.getDestination().getAddress().getCity(), dto.getDestinationDetails().getZip().getCity());
        assertSame(entity.getDestination().getAddress().getCountry().getId(), dto.getDestinationDetails().getZip().getCountry().getId());
        assertSame(entity.getDestination().getAddress().getStateCode(), dto.getDestinationDetails().getZip().getState());
        assertSame(entity.getDestination().getAddress().getZip(), dto.getDestinationDetails().getZip().getZip());
        assertSame(entity.getDestination().getNotes(), dto.getDestinationDetails().getAddress().getDeliveryNotes());
        assertNull(dto.getDestinationDetails().getAddress().getPickupNotes());

        assertSame(entity.getModification().getCreatedDate(), dto.getCreatedDate());
    }

    @Test
    public void shouldBuildEntity() {
        final long costDetailId = (long) (Math.random() * 100);
        final int costDetailVersion = (int) (Math.random() * 100);
        ShipmentDTO dto = getDTO();
        final ShipmentDTOBuilder shipmentDTOBuilder = new ShipmentDTOBuilder(new DataProvider() {
            @Override
            public BillToEntity getBillTo(Long id) {
                BillToEntity billToEntity = new BillToEntity();
                billToEntity.setId(id);
                return billToEntity;
            }

            @Override
            public AddressEntity getAddress(Long id) {
                return null;
            }

            @Override
            public LoadEntity getLoadById(Long id) {
                if (id == null) {
                    return new LoadEntity();
                }
                LoadEntity loadEntity = new LoadEntity();
                loadEntity.setId(id);
                loadEntity.setCostDetails(getCostDetails());
                loadEntity.getActiveCostDetail().setId(costDetailId);
                loadEntity.getActiveCostDetail().setVersion(costDetailVersion);
                return loadEntity;
            }

            @Override
            public FreightBillPayToEntity getDefaultFreightBillPayTo() {
                return new FreightBillPayToEntity();
            }

            @Override
            public PackageTypeEntity findPackageType(String id) {
                PackageTypeEntity entity = new PackageTypeEntity();
                entity.setId(id);
                return entity;
            }

            @Override
            public QuotedBO getPrimaryLoadCostDetail(Long id) {
                return null;
            }

            @Override
            public Boolean isPrintBarcode(Long customerId) {
                return false;
            }

            @Override
            public LtlPricingProfileEntity getProfileById(Long pricingProfileDetailId) {
                return null;
            }
        });
        LoadEntity entity = shipmentDTOBuilder.buildEntity(dto);

        assertNotNull(entity);
        assertSame(dto.getId(), entity.getId());
        assertSame(dto.getVersion(), entity.getVersion());
        assertSame(CommodityCd.MISC.name(), entity.getCommodity());
        assertSame("VANLTL", entity.getContainer());
        assertSame(ShipmentSourceIndicator.SYS.name(), entity.getSourceInd());
        assertSame(ShipmentStatus.valueOf(dto.getStatus().name()), entity.getStatus());
        assertNull(entity.getPersonId());
        assertSame(dto.getProNumber(), entity.getNumbers().getProNumber());
        assertSame(dto.getBolNumber(), entity.getNumbers().getBolNumber());
        assertSame(dto.getLocation().getId(), entity.getLocationId());
        assertNotNull(entity.getOrganization());
        assertSame(dto.getOrganizationId(), entity.getOrganization().getId());

        for (final String accessorial : dto.getOriginDetails().getAccessorials()) {
            assertTrue(CollectionUtils.exists(entity.getLtlAccessorials(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ((LtlLoadAccessorialEntity) object).getAccessorial().getId().equals(accessorial);
                }
            }));
        }
        for (final String accessorial : dto.getDestinationDetails().getAccessorials()) {
            assertTrue(CollectionUtils.exists(entity.getLtlAccessorials(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ((LtlLoadAccessorialEntity) object).getAccessorial().getId().equals(accessorial);
                }
            }));
        }

        assertNull(entity.getRoute());

        assertSame(dto.getBillTo().getId(), entity.getBillTo().getId());
        assertSame(dto.getCustomsBroker().getName(), entity.getCustomsBroker());
        assertEquals(PhoneUtils.format(dto.getCustomsBroker().getPhone()),
                entity.getCustomsBrokerPhone());

        assertSame(dto.getFinishOrder().getRef(), entity.getNumbers().getRefNumber());
        assertSame(costDetailId, entity.getActiveCostDetail().getId());
        assertSame(costDetailVersion, entity.getActiveCostDetail().getVersion());

        assertSame(dto.getOriginDetails().getAddress().getPickupNotes(), entity.getOrigin().getNotes());
        assertSame(dto.getDestinationDetails().getAddress().getDeliveryNotes(), entity.getDestination().getNotes());

        // these fields are filled on service layer
        assertNull(entity.getTravelTime());
        assertEquals(1, entity.getCostDetails().size());
    }

    private ShipmentDTO getDTO() {
        ShipmentDTO result = new ShipmentDTO();
        result.setId((long) (Math.random() * 100));
        result.setVersion((int) (Math.random() * 100));
        result.setFinishOrder(getFinishOrderDTO());
        result.setStatus(ShipmentStatus.values()[(int) (Math.random() * (ShipmentStatus.values().length - 1))]);
        result.setProNumber("proNumber" + Math.random());
        result.setBolNumber("bolNumber" + Math.random());
        result.setOriginDetails(getDetailsDTO());
        result.setDestinationDetails(getDetailsDTO());
        result.setSelectedProposition(getProposal());
        result.setBillTo(getBillToDTO());
        result.setCustomsBroker(getCustomesBrokerDTO());
        ShipmentLocationBO location = new ShipmentLocationBO();
        location.setId((long) (Math.random() * 100));
        result.setLocation(location);
        ShipmentProposalBO proposition = new ShipmentProposalBO();
        proposition.setEstimatedTransitDate(new Date());
        proposition.setEstimatedTransitTime((long) (Math.random() + 100));
        result.setSelectedProposition(proposition);
        return result;
    }

    private AddressBookEntryDTO getAddress() {
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        ZipDTO zip = new ZipDTO();
        zip.setCountry(new CountryDTO());
        address.setZip(zip);
        address.setPickupNotes("pickupNotes" + Math.random());
        address.setDeliveryNotes("deliveryNotes" + Math.random());
        return address;
    }

    private CustomsBrokerDTO getCustomesBrokerDTO() {
        CustomsBrokerDTO result = new CustomsBrokerDTO();
        result.setName("customsBroker" + Math.random());
        result.setPhone(getPhoneBO());
        return result;
    }

    private PhoneBO getPhoneBO() {
        PhoneBO phone = new PhoneBO();
        phone.setCountryCode("countryCode" + Math.random());
        phone.setAreaCode("areaCode" + Math.random());
        phone.setNumber("number" + Math.random());
        return phone;
    }

    private BillToDTO getBillToDTO() {
        BillToDTO result = new BillToDTO();
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        address.setId((long) (Math.random() * 100));
        result.setAddress(address);
        return result;
    }

    private ShipmentProposalBO getProposal() {
        ShipmentProposalBO result = new ShipmentProposalBO();
        CarrierDTO carrier = new CarrierDTO();
        carrier.setId((long) (Math.random() * 100));
        result.setCarrier(carrier);
        return result;
    }

    private ShipmentDetailsDTO getDetailsDTO() {
        ShipmentDetailsDTO result = new ShipmentDetailsDTO();
        List<String> accessorials = new ArrayList<String>();
        for (int i = 0; i < Math.random() * 10; i++) {
            accessorials.add(String.valueOf(Math.random()));
        }
        result.setAccessorials(accessorials);

        result.setZip(getZip());
        result.setAddress(getAddress());
        return result;
    }

    private ZipDTO getZip() {
        ZipDTO result = new ZipDTO();
        result.setCity("city" + Math.random());
        result.setCountry(new CountryDTO("country" + Math.random()));
        result.setState("state" + Math.random());
        result.setZip("zip" + Math.random());
        return result;
    }

    private ShipmentFinishOrderDTO getFinishOrderDTO() {
        ShipmentFinishOrderDTO result = new ShipmentFinishOrderDTO();
        result.setQuoteMaterials(Arrays.asList(getShipmentMaterialDTO()));
        result.setRef("ref" + Math.random());
        return result;
    }

    private ShipmentMaterialDTO getShipmentMaterialDTO() {
        ShipmentMaterialDTO result = new ShipmentMaterialDTO();
        result.setCommodityClass(CommodityClassDTO.values()[(int) (Math.random() * (CommodityClassDTO.values().length - 1))]);
        result.setHazmat(Math.random() > 0.5);
        result.setWeight(BigDecimal.TEN);
        return result;
    }

    private LoadEntity getEntity() {
        LoadEntity result = new LoadEntity();
        result.setId((long) (Math.random() * 100));
        result.setVersion((int) (Math.random() * 100));
        result.getModification().setCreatedBy((long) (Math.random() * 100));
        result.setPersonId((long) (Math.random() * 100 + 101));
        result.setOrganization(getCustomerEntity());
        result.setLocation(getLocationEntity());
        result.setLocationId(result.getLocation().getId());
        result.setStatus(ShipmentStatus.values()[(int) (Math.random() * (ShipmentStatus.values().length - 1))]);
        result.getNumbers().setBolNumber("bol" + Math.random());
        result.getNumbers().setProNumber("pro" + Math.random());
        result.setMileage((int) (Math.random() * 100));
        result.setBillTo(getBillToEntity());
        result.addLoadDetails(getLoadDetailsEntity(PointType.ORIGIN, LoadAction.PICKUP));
        result.addLoadDetails(getLoadDetailsEntity(PointType.DESTINATION, LoadAction.DELIVERY));
        Set<LtlLoadAccessorialEntity> accessorials = new HashSet<LtlLoadAccessorialEntity>();
        for (int i = 0; i < Math.random() * 10; i++) {
            accessorials.add(buildAccessorialEntity());
        }
        result.setLtlAccessorials(accessorials);

        result.setCostDetails(getCostDetails());
        result.setCarrier(getCarrier());

        result.setRoute(getRouteEntity());
        result.setCustomsBroker("customsBroker" + Math.random());
        result.setCustomsBrokerPhone(createPhone());
        result.getModification().setCreatedDate(new Date((long) (Math.random() * 1000)));
        return result;
    }

    private CustomerEntity getCustomerEntity() {
        CustomerEntity organization = new CustomerEntity();
        organization.setId((long) (Math.random() * 100));
        organization.setName("name" + Math.random());
        organization.setProductListPrimarySort(Math.random() > 0.5 ? ProductListPrimarySort.PRODUCT_DESCRIPTION
                : ProductListPrimarySort.SKU_PRODUCT_CODE);
        return organization;
    }

    private OrganizationLocationEntity getLocationEntity() {
        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setId((long) (Math.random() * 100));
        location.setLocationName("locationName" + Math.random());
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        location.setBillTo(billTo);
        return location;
    }

    private CarrierEntity getCarrier() {
        CarrierEntity result = new CarrierEntity();
        result.setId((long) (Math.random() * 100));
        result.setName("name" + Math.random());
        return result;
    }

    private Set<LoadCostDetailsEntity> getCostDetails() {
        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setId((long) (Math.random() * 100));
        costDetail.setVersion((int) (Math.random() * 100));
        costDetail.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetailItems.add(getCostDetailItem(CostDetailOwner.C));
        costDetailItems.add(getCostDetailItem(CostDetailOwner.S));
        CostDetailItemEntity item = getCostDetailItem(CostDetailOwner.C);
        item.setAccessorialType(ShipmentService.GUARANTEED_SERVICE_REF_TYPE);
        costDetailItems.add(item);
        item = getCostDetailItem(CostDetailOwner.S);
        item.setAccessorialType(ShipmentService.GUARANTEED_SERVICE_REF_TYPE);
        costDetailItems.add(item);
        costDetail.setCostDetailItems(costDetailItems);
        costDetail.setGuaranteedBy((long) (Math.random() * 100));
        costDetail.setPricingProfileDetailId((long) (Math.random() * 100));
        costDetail.setRevenueOverride(StatusYesNo.NO);
        costDetail.setCostOverride(StatusYesNo.NO);
        costDetails.add(costDetail);
        return costDetails;
    }

    private CostDetailItemEntity getCostDetailItem(CostDetailOwner owner) {
        CostDetailItemEntity item = new CostDetailItemEntity();
        item.setAccessorialType("accessorialType" + Math.random());
        item.setOwner(owner);
        item.setSubtotal(BigDecimal.valueOf(Math.random()));
        return item;
    }

    private LtlLoadAccessorialEntity buildAccessorialEntity() {
        LtlLoadAccessorialEntity accessorial = new LtlLoadAccessorialEntity();

        AccessorialTypeEntity accessorialType = new AccessorialTypeEntity(String.valueOf(Math.random()));
        accessorialType.setAccessorialGroup(Math.random() > 0.5 ? LtlAccessorialGroup.DELIVERY : LtlAccessorialGroup.PICKUP);

        accessorial.setAccessorial(accessorialType);
        return accessorial;
    }

    private BillToEntity getBillToEntity() {
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        BillingInvoiceNodeEntity billingInvoiceNode = new BillingInvoiceNodeEntity();
        billingInvoiceNode.setAddress(getAddressEntity());
        billTo.setBillingInvoiceNode(billingInvoiceNode);
        billTo.setAuditPrefReq(false);
        return billTo;
    }

    private String createPhone() {
        return (int) (Math.random() * 999) + "-" + (int) (Math.random() * 999) + "-" + (int) (Math.random() * 9999999);
    }

    private RouteEntity getRouteEntity() {
        RouteEntity result = new RouteEntity();
        result.setOriginCity("originCity" + Math.random());
        result.setOriginCountry("originCountry" + Math.random());
        result.setOriginState("originState" + Math.random());
        result.setOriginZip("originZip" + Math.random());
        result.setDestCity("originCity" + Math.random());
        result.setDestCountry("originCountry" + Math.random());
        result.setDestState("originState" + Math.random());
        result.setDestZip("originZip" + Math.random());
        return result;
    }

    private LoadDetailsEntity getLoadDetailsEntity(PointType pointType, LoadAction action) {
        LoadDetailsEntity result = new LoadDetailsEntity(action, pointType);
        if (pointType == PointType.ORIGIN) {
            result.setLoadMaterials(new HashSet<LoadMaterialEntity>());
        }
        result.setAddress(getAddressEntity());
        result.setNotes("notes" + Math.random());
        return result;
    }

    private AddressEntity getAddressEntity() {
        AddressEntity result = new AddressEntity();
        result.setCity("city" + Math.random());
        CountryEntity country = new CountryEntity();
        country.setId("id" + Math.random());
        result.setCountry(country);
        StateEntity state = new StateEntity();
        StatePK statePK = new StatePK();
        statePK.setStateCode("stateCode" + Math.random());
        state.setStatePK(statePK);
        result.setState(state);
        result.setZip("zip" + Math.random());
        return result;
    }

    private LoadDetailsEntity getLoadDetailsByPointType(Set<LoadDetailsEntity> loadDetails, PointType pointType) {
        for (LoadDetailsEntity details : loadDetails) {
            if (details.getPointType() == pointType) {
                return details;
            }
        }
        return null;
    }

    private CostDetailItemEntity getCostDetailByTypeAndOwner(Set<CostDetailItemEntity> items, CostDetailItemBO origItem) {
        for (CostDetailItemEntity item : items) {
            if (StringUtils.equals(origItem.getRefType(), item.getAccessorialType()) && origItem.getCostDetailOwner() == item.getOwner()) {
                return item;
            }
        }
        return null;
    }
}
