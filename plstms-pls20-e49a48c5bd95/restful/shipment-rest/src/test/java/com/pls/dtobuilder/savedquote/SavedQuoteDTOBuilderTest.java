package com.pls.dtobuilder.savedquote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;
import com.pls.dto.address.CountryDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentDetailsDTO;
import com.pls.dto.shipment.ShipmentFinishOrderDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.SavedQuoteAccessorialEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsItemEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;
import com.pls.shipment.service.ShipmentService;

/**
 * Test for {@link SavedQuoteDTOBuilder}.
 * 
 * @author Aleksandr Leshchenko
 */
public class SavedQuoteDTOBuilderTest {

    private SavedQuoteDTOBuilder.DataProvider dataProvider = new SavedQuoteDTOBuilder.DataProvider() {
        @Override
        public PackageTypeEntity findPackageType(String id) {
            PackageTypeEntity entity = new PackageTypeEntity();
            entity.setId(id);
            return entity;
        }

        @Override
        public SavedQuoteEntity findSavedQuoteById(Long id) {
            return null;
        }
    };

    @Test
    public void shouldBuildDTO() {
        SavedQuoteEntity entity = prepareSavedQuoteEntity();

        ShipmentDTO dto = new SavedQuoteDTOBuilder(dataProvider).buildDTO(entity);

        assertNull(dto.getId());
        assertNull(dto.getFinishOrder().getOriginDetailsId());
        assertNull(dto.getFinishOrder().getDestinationDetailsId());
        assertSame(entity.getId(), dto.getQuoteId());
        assertSame(entity.getCustomer().getId(), dto.getOrganizationId());
        assertSame(ShipmentStatus.OPEN, dto.getStatus());
        assertSame(entity.getCarrierReferenceNumber(), dto.getProNumber());
        assertSame(entity.getModification().getCreatedDate(), dto.getCreatedDate());

        for (SavedQuoteAccessorialEntity accessorial : entity.getAccessorials()) {
            ShipmentDetailsDTO details = accessorial.getAccessorialType().getAccessorialGroup() == LtlAccessorialGroup.PICKUP
                    ? dto.getOriginDetails() : dto.getDestinationDetails();
            assertTrue(details.getAccessorials().contains(accessorial.getAccessorialType().getId()));
        }

        assertNull(dto.getOriginDetails().getAddress());
        assertNull(dto.getDestinationDetails().getAddress());

        assertSame(entity.getRoute().getOriginCountry(), dto.getOriginDetails().getZip().getCountry().getId());
        assertSame(entity.getRoute().getOriginZip(), dto.getOriginDetails().getZip().getZip());
        assertSame(entity.getRoute().getOriginState(), dto.getOriginDetails().getZip().getState());
        assertSame(entity.getRoute().getOriginCity(), dto.getOriginDetails().getZip().getCity());

        assertSame(entity.getRoute().getDestCountry(), dto.getDestinationDetails().getZip().getCountry().getId());
        assertSame(entity.getRoute().getDestZip(), dto.getDestinationDetails().getZip().getZip());
        assertSame(entity.getRoute().getDestState(), dto.getDestinationDetails().getZip().getState());
        assertSame(entity.getRoute().getDestCity(), dto.getDestinationDetails().getZip().getCity());

        assertSame(entity.getPickupDate(), dto.getFinishOrder().getPickupDate());
        assertSame(entity.getPoNum(), dto.getFinishOrder().getPoNumber());
        assertSame(entity.getPickupNum(), dto.getFinishOrder().getPuNumber());

        assertSame(entity.getBol(), dto.getBolNumber());
        assertSame(entity.getSoNumber(), dto.getFinishOrder().getSoNumber());
        assertSame(entity.getGlNumber(), dto.getFinishOrder().getGlNumber());
        assertSame(entity.getTrailer(), dto.getFinishOrder().getTrailerNumber());
        assertSame(entity.getQuoteReferenceNumber(), dto.getFinishOrder().getRef());

        assertEquals(entity.getMaterials().size(), dto.getFinishOrder().getQuoteMaterials().size());

        assertSame(entity.getMileage(), dto.getSelectedProposition().getMileage());
        assertSame(entity.getCarrier().getId(), dto.getSelectedProposition().getCarrier().getId());
        assertSame(entity.getSpecialMessage(), dto.getSelectedProposition().getCarrier().getSpecialMessage());

        SavedQuoteCostDetailsEntity costDetails = entity.getCostDetails();
        assertSame(costDetails.getServiceType(), dto.getSelectedProposition().getServiceType());
        assertSame(costDetails.getEstimatedTransitTime(), dto.getSelectedProposition().getEstimatedTransitTime());
        assertSame(costDetails.getEstTransitDate(), dto.getSelectedProposition().getEstimatedTransitDate());
        assertSame(costDetails.getNewLiability(), dto.getSelectedProposition().getNewLiability());
        assertSame(costDetails.getUsedLiability(), dto.getSelectedProposition().getUsedLiability());
        assertSame(costDetails.getProhibitedCommodities(), dto.getSelectedProposition().getProhibited());
        assertSame(costDetails.getPricingProfileDetailId(), dto.getSelectedProposition().getPricingProfileId());

        assertEquals(costDetails.getGuaranteedBy(), dto.getGuaranteedBy());
        assertNull(dto.getSelectedProposition().getCostDetailItems().get(0).getGuaranteedBy());
        assertNull(dto.getSelectedProposition().getCostDetailItems().get(1).getGuaranteedBy());
        assertEquals(costDetails.getGuaranteedBy(), dto.getSelectedProposition().getCostDetailItems().get(2).getGuaranteedBy());
        assertEquals(costDetails.getGuaranteedBy(), dto.getSelectedProposition().getCostDetailItems().get(3).getGuaranteedBy());

        List<SavedQuoteCostDetailsItemEntity> costDetailItems = new ArrayList<SavedQuoteCostDetailsItemEntity>(
                costDetails.getCostDetailsItems());
        List<CostDetailItemBO> costDetailItemsDTO = dto.getSelectedProposition().getCostDetailItems();
        assertEquals(costDetailItems.size(), costDetailItemsDTO.size());
        for (int i = 0; i < costDetailItems.size(); i++) {
            assertEquals(costDetailItems.get(i).getOwner().name(), costDetailItemsDTO.get(i).getCostDetailOwner().name());
            assertSame(costDetailItems.get(i).getRefType(), costDetailItemsDTO.get(i).getRefType());
            if (costDetailItemsDTO.get(i).getGuaranteedBy() != null) {
                assertSame(costDetails.getGuaranteedBy(), costDetailItemsDTO.get(i).getGuaranteedBy());
            }
            assertSame(costDetailItems.get(i).getSubTotal(), costDetailItemsDTO.get(i).getSubTotal());
        }
    }

    @Test
    public void shouldBuildEntity() {
        ShipmentDTO dto = prepareDTO();

        SavedQuoteEntity entity = new SavedQuoteDTOBuilder(dataProvider).buildEntity(dto);

        assertNull("We don't need id to be set, because we shouldn't update existing saved quotes.", entity.getId());

        assertSame(dto.getFinishOrder().getPickupDate(), entity.getPickupDate());

        assertNotNull(entity.getRoute().getCreatedDate());
        assertSame(dto.getOriginDetails().getZip().getZip(), entity.getRoute().getOriginZip());
        assertSame(dto.getOriginDetails().getZip().getCountry().getId(), entity.getRoute().getOriginCountry());
        assertSame(dto.getOriginDetails().getZip().getState(), entity.getRoute().getOriginState());
        assertSame(dto.getOriginDetails().getZip().getCity(), entity.getRoute().getOriginCity());

        assertSame(dto.getDestinationDetails().getZip().getZip(), entity.getRoute().getDestZip());
        assertSame(dto.getDestinationDetails().getZip().getCountry().getId(), entity.getRoute().getDestCountry());
        assertSame(dto.getDestinationDetails().getZip().getState(), entity.getRoute().getDestState());
        assertSame(dto.getDestinationDetails().getZip().getCity(), entity.getRoute().getDestCity());

        assertSame(dto.getFinishOrder().getRef(), entity.getQuoteReferenceNumber());
        assertSame(dto.getFinishOrder().getPoNumber(), entity.getPoNum());
        assertSame(dto.getFinishOrder().getPuNumber(), entity.getPickupNum());

        assertSame(dto.getBolNumber(), entity.getBol());
        assertSame(dto.getFinishOrder().getSoNumber(), entity.getSoNumber());
        assertSame(dto.getFinishOrder().getGlNumber(), entity.getGlNumber());
        assertSame(dto.getFinishOrder().getTrailerNumber(), entity.getTrailer());

        assertEquals(Status.ACTIVE, entity.getStatus());
        assertSame(dto.getProNumber(), entity.getCarrierReferenceNumber());
        assertEquals(dto.getFinishOrder().getQuoteMaterials().size(), entity.getMaterials().size());

        for (final String accessorial : dto.getOriginDetails().getAccessorials()) {
            assertTrue(CollectionUtils.exists(entity.getAccessorials(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ((SavedQuoteAccessorialEntity) object).getAccessorialType().getId().equals(accessorial);
                }
            }));
        }
        for (final String accessorial : dto.getDestinationDetails().getAccessorials()) {
            assertTrue(CollectionUtils.exists(entity.getAccessorials(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ((SavedQuoteAccessorialEntity) object).getAccessorialType().getId().equals(accessorial);
                }
            }));
        }
    }

    private ShipmentDTO prepareDTO() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setProNumber("proNumber" + Math.random());
        dto.setBolNumber("bolNumber" + Math.random());
        dto.setSelectedProposition(createSelectedProposition());
        dto.setFinishOrder(buildFinishOrder());
        dto.setOriginDetails(buildDetails());
        dto.setDestinationDetails(buildDetails());
        return dto;
    }

    private ShipmentDetailsDTO buildDetails() {
        ShipmentDetailsDTO dto = new ShipmentDetailsDTO();
        ZipDTO zip = new ZipDTO();
        zip.setCity("city" + Math.random());
        zip.setCountry(new CountryDTO());
        zip.setState("state" + Math.random());
        zip.setZip("zip" + Math.random());
        dto.setZip(zip);
        List<String> accessorials = new ArrayList<String>();
        for (int i = 0; i < Math.random() * 10; i++) {
            accessorials.add(String.valueOf(Math.random()));
        }
        dto.setAccessorials(accessorials);
        return dto;
    }

    private ShipmentFinishOrderDTO buildFinishOrder() {
        ShipmentFinishOrderDTO dto = new ShipmentFinishOrderDTO();
        dto.setOriginDetailsId((long) (Math.random() * 100));
        dto.setDestinationDetailsId((long) (Math.random() * 100));
        dto.setPickupDate(new Date());
        dto.setRef("ref" + Math.random());
        dto.setPoNumber("poNumber" + Math.random());
        dto.setPuNumber("puNumber" + Math.random());
        dto.setSoNumber("soNumber" + Math.random());
        dto.setGlNumber("glNumber" + Math.random());
        dto.setTrailerNumber("trailerNumber" + Math.random());
        dto.setQuoteMaterials(new ArrayList<ShipmentMaterialDTO>());
        dto.getQuoteMaterials().add(new ShipmentMaterialDTO());
        dto.getQuoteMaterials().add(new ShipmentMaterialDTO());
        return dto;
    }

    private ShipmentProposalBO createSelectedProposition() {
        ShipmentProposalBO dto = new ShipmentProposalBO();
        dto.setEstimatedTransitDate(new Date());
        return dto;
    }

    private SavedQuoteEntity prepareSavedQuoteEntity() {
        SavedQuoteEntity entity = new SavedQuoteEntity();
        entity.setId((long) (Math.random() * 100));
        entity.setCarrierReferenceNumber("carrierReferenceNumber" + Math.random());
        entity.setQuoteReferenceNumber("quoteReferenceNumber" + Math.random());
        entity.setPoNum("poNum" + Math.random());
        entity.setPickupNum("pickupNum" + Math.random());
        entity.setBol("bol" + Math.random());
        entity.setGlNumber("glNum" + Math.random());
        entity.setSoNumber("soNum" + Math.random());
        entity.setTrailer("trailer" + Math.random());
        entity.setSpecialMessage("specialMessage" + Math.random());
        entity.setMileage((int) (Math.random() * 100));
        entity.setPickupDate(new Date());

        entity.setCarrier(prepareCarrier());
        entity.setCustomer(prepareCustomer());

        entity.setCostDetails(prepareCostDetailEntity());

        entity.setRoute(createRoute());

        Set<SavedQuoteAccessorialEntity> accessorials = new HashSet<SavedQuoteAccessorialEntity>();
        for (int i = 0; i < Math.random() * 10; i++) {
            accessorials.add(buildAccessorialEntity());
        }
        entity.setAccessorials(accessorials);

        entity.setMaterials(new HashSet<SavedQuoteMaterialEntity>());
        entity.getMaterials().add(new SavedQuoteMaterialEntity());
        entity.getMaterials().add(new SavedQuoteMaterialEntity());

        return entity;
    }

    private SavedQuoteAccessorialEntity buildAccessorialEntity() {
        SavedQuoteAccessorialEntity accessorial = new SavedQuoteAccessorialEntity();

        AccessorialTypeEntity accessorialType = new AccessorialTypeEntity(String.valueOf(Math.random()));
        accessorialType.setAccessorialGroup(Math.random() > 0.5 ? LtlAccessorialGroup.DELIVERY : LtlAccessorialGroup.PICKUP);

        accessorial.setAccessorialType(accessorialType);
        return accessorial;
    }

    private CarrierEntity prepareCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        return carrier;
    }

    private CustomerEntity prepareCustomer() {
        CustomerEntity carrier = new CustomerEntity();
        carrier.setId((long) (Math.random() * 100));
        return carrier;
    }

    private SavedQuoteCostDetailsEntity prepareCostDetailEntity() {
        SavedQuoteCostDetailsEntity costDetail = new SavedQuoteCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);
        costDetail.setServiceType(LtlServiceType.BOTH);
        costDetail.setEstimatedTransitTime((long) (Math.random() * 100));
        costDetail.setEstTransitDate(new Date());
        costDetail.setNewLiability(BigDecimal.valueOf(Math.random()));
        costDetail.setUsedLiability(BigDecimal.valueOf(Math.random()));
        costDetail.setProhibitedCommodities("prohibitedCommodities" + Math.random());
        costDetail.setPricingProfileDetailId((long) (Math.random() * 100));
        List<SavedQuoteCostDetailsItemEntity> costDetailsItems = new ArrayList<SavedQuoteCostDetailsItemEntity>();
        costDetailsItems.add(getCostDetailItem(CostDetailOwner.C));
        costDetailsItems.add(getCostDetailItem(CostDetailOwner.S));
        costDetailsItems.add(getCostDetailItem(CostDetailOwner.C));
        costDetailsItems.add(getCostDetailItem(CostDetailOwner.S));
        costDetailsItems.get(2).setRefType(ShipmentService.GUARANTEED_SERVICE_REF_TYPE);
        costDetailsItems.get(3).setRefType(ShipmentService.GUARANTEED_SERVICE_REF_TYPE);
        costDetail.setCostDetailsItems(new LinkedHashSet<SavedQuoteCostDetailsItemEntity>(costDetailsItems));
        costDetail.setGuaranteedBy((long) (Math.random() * 100));
        return costDetail;
    }

    private SavedQuoteCostDetailsItemEntity getCostDetailItem(CostDetailOwner owner) {
        SavedQuoteCostDetailsItemEntity item = new SavedQuoteCostDetailsItemEntity();
        item.setRefType("refType" + Math.random());
        item.setOwner(owner);
        item.setSubTotal(BigDecimal.valueOf(Math.random()));
        return item;
    }

    private RouteEntity createRoute() {
        RouteEntity route = new RouteEntity();
        route.setOriginCountry("originCountry" + Math.random());
        route.setOriginCity("originCity" + Math.random());
        route.setOriginState("originState" + Math.random());
        route.setOriginZip("originZip" + Math.random());
        route.setDestCountry("destCountry" + Math.random());
        route.setDestCity("destCity" + Math.random());
        route.setDestState("destState" + Math.random());
        route.setDestZip("destZip" + Math.random());
        return route;
    }
}
