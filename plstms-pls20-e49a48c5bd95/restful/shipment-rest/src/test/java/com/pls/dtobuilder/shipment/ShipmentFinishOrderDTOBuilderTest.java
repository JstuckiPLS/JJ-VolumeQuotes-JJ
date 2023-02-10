package com.pls.dtobuilder.shipment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.dto.ShipmentNotificationsDTO;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.PickupAndDeliveryWindowDTO;
import com.pls.dto.shipment.JobNumberDTO;
import com.pls.dto.shipment.ShipmentFinishOrderDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.shipment.ShipmentFinishOrderDTOBuilder.DataProvider;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.ShipmentRequestedByNoteEntity;

/**
 * Test cases for {@link ShipmentFinishOrderDTOBuilder}. Here We used Mocks for each inner mapper. Some test
 * cases need to be added, just to cover some logic cases, such as {@link ShipmentProposalBO} with any
 * status, except BOOKED, etc.
 * 
 * @author Viacheslav Vasianovych
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentFinishOrderDTOBuilderTest {
    @Mock
    private DataProvider dataProvider;

    @InjectMocks
    private ShipmentFinishOrderDTOBuilder sut;

    @Test
    public void buildDTONormalCase() {
        LoadEntity entity = buildLoadEntity();

        ShipmentFinishOrderDTO dto = sut.buildDTO(entity);

        checkLoadNotifications(entity, dto);
        assertSame(entity.getOrigin().getEarlyScheduledArrival(), dto.getPickupDate());
        checkPickupWindow(entity, dto);

        assertSame(entity.getNumbers().getPoNumber(), dto.getPoNumber());
        assertSame(entity.getNumbers().getPuNumber(), dto.getPuNumber());
        assertDtoJobNumbers(entity.getNumbers().getJobNumbers(), dto.getJobNumbers());

        assertSame(entity.getDeliveryNotes(), dto.getDeliveryNotes());
        assertSame(entity.getSpecialInstructions(), dto.getPickupNotes());
        assertSame(entity.getBolInstructions(), dto.getShippingLabelNotes());

        assertSame(entity.getRequestedBy().getNote(), dto.getRequestedBy());

        assertEquals(entity.getDestination().getId(), dto.getDestinationDetailsId());
        assertEquals(entity.getOrigin().getId(), dto.getOriginDetailsId());
        assertEquals(entity.getLoadNotifications().size(), dto.getShipmentNotifications().size());

        assertSame(entity.getOrigin().getLoadMaterials().size(), dto.getQuoteMaterials().size());
    }

    @Test
    public void buildEntityNormalCase() {
        ShipmentFinishOrderDTO dto = buildShipmentFinishOrderDTO();
        dto.getQuoteMaterials().get(1).setHazmat(true);

        when(dataProvider.getLoadEntity()).thenReturn(new LoadEntity());
        final AddressBookEntryDTO originAddress = new AddressBookEntryDTO();
        when(dataProvider.getOriginAddress()).thenReturn(originAddress);
        final AddressBookEntryDTO destinationAddress = new AddressBookEntryDTO();
        when(dataProvider.getDestinationAddress()).thenReturn(destinationAddress);

        LoadEntity entity = sut.buildEntity(dto);

        verify(dataProvider).getOriginAddress();
        verify(dataProvider).getDestinationAddress();
        verify(dataProvider).getLoadEntity();

        checkLoadNotifications(entity, dto);
        assertEquals(prepareDate(dto.getPickupDate(), dto.getPickupWindowFrom()), entity.getOrigin().getArrivalWindowStart());
        assertEquals(prepareDate(dto.getPickupDate(), dto.getPickupWindowTo()), entity.getOrigin().getArrivalWindowEnd());
        assertEquals(prepareDate(dto.getPickupDate(), dto.getPickupWindowFrom()), entity.getOrigin().getEarlyScheduledArrival());
        assertEquals(prepareDate(dto.getPickupDate(), dto.getPickupWindowTo()), entity.getOrigin().getScheduledArrival());
        checkPickupWindow(entity, dto);

        assertSame(dto.getPoNumber(), entity.getNumbers().getPoNumber());
        assertSame(dto.getPuNumber(), entity.getNumbers().getPuNumber());
        assertSame(entity.getRequestedBy().getNote(), dto.getRequestedBy());

        assertSame(dto.getDeliveryNotes(), entity.getDeliveryNotes());
        assertSame(dto.getPickupNotes(), entity.getSpecialInstructions());
        assertSame(dto.getShippingLabelNotes(), entity.getBolInstructions());

        assertEquals(dto.getDestinationDetailsId(), entity.getDestination().getId());

        assertEquals(dto.getOriginDetailsId(), entity.getOrigin().getId());

        assertEquals(dto.getQuoteMaterials().size(), entity.getOrigin().getLoadMaterials().size());
        assertTrue(entity.getHazmat());
        assertEquals(dto.getShipmentNotifications().size(), entity.getLoadNotifications().size());

        BigDecimal weight = BigDecimal.ZERO;
        int pieces = 0;
        for (ShipmentMaterialDTO quoteMaterial : dto.getQuoteMaterials()) {
            weight = weight.add(quoteMaterial.getWeight());
            if (StringUtils.isNotBlank(quoteMaterial.getQuantity())) {
                pieces += Integer.parseInt(quoteMaterial.getQuantity());
            }
        }
        assertEquals(weight.setScale(0, RoundingMode.CEILING).intValue(), entity.getWeight().intValue());

        assertEquals(pieces, entity.getPieces().intValue());
    }

    @Test
    public void shouldAddJobNumbersToEmptyEntity() {
        ShipmentFinishOrderDTO dto = buildShipmentFinishOrderDTO();
        dto.setJobNumbers(buildJobNumbersDtoList(true));
        when(dataProvider.getLoadEntity()).thenReturn(new LoadEntity());

        LoadEntity actual = sut.buildEntity(dto);
        Assert.assertNotNull(actual);
        assertLoadJobNumbers(dto.getJobNumbers(), actual.getNumbers().getJobNumbers());
    }

    @Test
    public void shouldRemoveUnuserJobNumberFromEntity() {
        ShipmentFinishOrderDTO dto = buildShipmentFinishOrderDTO();
        dto.setJobNumbers(buildJobNumbersDtoList(false));
        Set<LoadJobNumbersEntity> jobNumbers = getJobNumbersEntitiesFromDtoList(dto.getJobNumbers());
        LoadEntity load = new LoadEntity();
        load.getNumbers().setJobNumbers(jobNumbers);
        when(dataProvider.getLoadEntity()).thenReturn(load);
        dto.getJobNumbers().remove(0);
        dto.getJobNumbers().add(new JobNumberDTO(null, String.valueOf(Math.random())));

        LoadEntity actual = sut.buildEntity(dto);
        Assert.assertNotNull(actual);
    }

    private Date prepareDate(Date pickupDate, PickupAndDeliveryWindowDTO pickupWindow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pickupDate);
        calendar.set(Calendar.HOUR, pickupWindow.getHours() == 12 ? 0 : pickupWindow.getHours());
        calendar.set(Calendar.MINUTE, pickupWindow.getMinutes());
        calendar.set(Calendar.AM_PM, pickupWindow.getAm() ? Calendar.AM : Calendar.PM);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void checkPickupWindow(LoadEntity entity, ShipmentFinishOrderDTO dto) {
        assertTrue(dto.getPickupWindowFrom().getAm());
        assertFalse(dto.getPickupWindowTo().getAm());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(entity.getOrigin().getArrivalWindowStart());
        assertSame(calendar.get(Calendar.HOUR), dto.getPickupWindowFrom().getHours());
        assertSame(calendar.get(Calendar.MINUTE), dto.getPickupWindowFrom().getMinutes());
        assertSame(calendar.get(Calendar.AM_PM), Calendar.AM);
        calendar.setTime(entity.getOrigin().getArrivalWindowEnd());
        assertSame(calendar.get(Calendar.HOUR), dto.getPickupWindowTo().getHours());
        assertSame(calendar.get(Calendar.AM_PM), Calendar.PM);
    }

    private void checkLoadNotifications(LoadEntity entity, ShipmentFinishOrderDTO dto) {
        assertEquals(entity.getLoadNotifications().size(), dto.getShipmentNotifications().size());

        Iterator<LoadNotificationsEntity> it = entity.getLoadNotifications().iterator();
        LoadNotificationsEntity notification;
        for (int i = 0; i < entity.getLoadNotifications().size(); i++) {
            notification = it.next();
            assertSame(notification.getEmailAddress(), dto.getShipmentNotifications().get(i).getEmailAddress());
        }
    }

    private ShipmentFinishOrderDTO buildShipmentFinishOrderDTO() {
        ShipmentFinishOrderDTO dto = new ShipmentFinishOrderDTO();
        dto.setDeliveryNotes("DELIVERY_NOTES");
        dto.setShipmentNotifications(buildShipmentNotificationsDTOList());
        dto.setPickupDate(new Date());
        dto.setPickupNotes("PICKUP_NOTES");
        dto.setPickupWindowFrom(new PickupAndDeliveryWindowDTO(1, 0, true));
        dto.setPickupWindowTo(new PickupAndDeliveryWindowDTO(2, 0, false));
        dto.setPoNumber("PO");
        dto.setPuNumber("PU");
        dto.setSoNumber("SO");
        dto.setGlNumber("GL");
        dto.setRequestedBy("Requested by");
        dto.setTrailerNumber("Trailer");
        dto.setQuoteMaterials(buildQuoteMaterialList());
        dto.setShippingLabelNotes("SHIPPNGLABELNOTES");
        dto.setRequestedBy("Requested By");
        dto.setActualPickupDate(new Date());
        dto.setJobNumbers(buildJobNumbersDtoList(false));
        return dto;
    }

    private List<ShipmentNotificationsDTO> buildShipmentNotificationsDTOList() {
        ArrayList<ShipmentNotificationsDTO> shipmentNotificationsDTOs = new ArrayList<ShipmentNotificationsDTO>();
        ShipmentNotificationsDTO shipmentNotificationsDTO = new ShipmentNotificationsDTO();
        shipmentNotificationsDTO.setEmailAddress("test@email.com");
        shipmentNotificationsDTO.setNotificationType("PICK_UP");
        shipmentNotificationsDTOs.add(shipmentNotificationsDTO);

        shipmentNotificationsDTO = new ShipmentNotificationsDTO();
        shipmentNotificationsDTO.setEmailAddress("test@email.com");
        shipmentNotificationsDTO.setNotificationType("DELIVERED");
        shipmentNotificationsDTOs.add(shipmentNotificationsDTO);

        shipmentNotificationsDTO = new ShipmentNotificationsDTO();
        shipmentNotificationsDTO.setEmailAddress("test@email.com");
        shipmentNotificationsDTO.setNotificationType("DETAILS");
        shipmentNotificationsDTOs.add(shipmentNotificationsDTO);

        return shipmentNotificationsDTOs;
    }

    private List<ShipmentMaterialDTO> buildQuoteMaterialList() {
        List<ShipmentMaterialDTO> list = new ArrayList<ShipmentMaterialDTO>();
        list.add(buildShipmentMaterial());
        list.add(buildShipmentMaterial());
        return list;
    }

    private ShipmentMaterialDTO buildShipmentMaterial() {
        ShipmentMaterialDTO dto = new ShipmentMaterialDTO();
        dto.setWeight(BigDecimal.TEN);
        return dto;
    }

    private LoadEntity buildLoadEntity() {
        LoadEntity entity = new LoadEntity();
        entity.setBillTo(new BillToEntity());
        entity.getNumbers().setBolNumber("BOL");
        entity.setBolInstructions("BOL_INSTRUCTIONS");
        entity.setCommodity("COMMODITY");
        entity.setCostDetails(buildLoadCostDetailsList());

        HashSet<LoadNotificationsEntity> loadNotifications = new LinkedHashSet<LoadNotificationsEntity>();
        loadNotifications.add(getLoadNotificationEntity(entity));
        loadNotifications.add(getLoadNotificationEntity(entity));
        entity.setLoadNotifications(loadNotifications);

        entity.setId(1L);
        entity.addLoadDetails(buildLoadDetail(true));
        entity.addLoadDetails(buildLoadDetail(false));
        entity.setOrganization(new CustomerEntity());
        entity.setPersonId(1L);
        entity.setPieces(1);
        entity.getNumbers().setPoNumber("PO");
        entity.getNumbers().setProNumber("PRO");
        entity.getNumbers().setPuNumber("PU");
        entity.getNumbers().setRefNumber("REF");
        entity.getNumbers().setSoNumber("SO");
        entity.getNumbers().setGlNumber("GL");
        entity.getNumbers().setTrailerNumber("Trailer");
        entity.getNumbers().setJobNumbers(buildJobNumbersForEntity(entity.getId()));
        entity.setRequestedBy(new ShipmentRequestedByNoteEntity());
        entity.setRoute(new RouteEntity());
        entity.setSourceInd("SOURCE_IND");
        entity.setSpecialInstructions("SPEC_INSTR");
        entity.setDeliveryNotes("SPEC_MESSAGE");
        entity.setStatus(ShipmentStatus.DISPATCHED);
        entity.setWeight(123);
        return entity;
    }

    private LoadNotificationsEntity getLoadNotificationEntity(LoadEntity load) {
        LoadNotificationsEntity loadNotification = new LoadNotificationsEntity();
        loadNotification.setId((long) (Math.random() * 100));
        loadNotification.setEmailAddress("emailAddress" + Math.random());

        NotificationTypeEntity notificationType = new NotificationTypeEntity();
        notificationType.setId("notificationTypes" + Math.random());
        notificationType.setDescription("notificationTypes" + Math.random());
        loadNotification.setNotificationType(notificationType);

        loadNotification.setLoad(load);
        return loadNotification;
    }

    private Set<LoadCostDetailsEntity> buildLoadCostDetailsList() {
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        Set<LoadCostDetailsEntity> costDetailSet = new HashSet<LoadCostDetailsEntity>();
        costDetailSet.add(costDetail);
        return costDetailSet;
    }

    private LoadDetailsEntity buildLoadDetail(boolean origin) {
        LoadDetailsEntity loadDetail = new LoadDetailsEntity(
                origin ? LoadAction.PICKUP : LoadAction.DELIVERY, origin ? PointType.ORIGIN
                        : PointType.DESTINATION);

        loadDetail.setLoadMaterials(buildLoadMaterials());

        Calendar calendar = Calendar.getInstance();
        loadDetail.setScheduledArrival(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        loadDetail.setArrivalWindowStart(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        loadDetail.setArrivalWindowEnd(calendar.getTime());
        return loadDetail;
    }

    private Set<LoadMaterialEntity> buildLoadMaterials() {
        Set<LoadMaterialEntity> materials = new LinkedHashSet<LoadMaterialEntity>();
        materials.add(buildLoadMaterial());
        materials.add(buildLoadMaterial());
        return materials;
    }

    private LoadMaterialEntity buildLoadMaterial() {
        LoadMaterialEntity loadMaterial = new LoadMaterialEntity();
        return loadMaterial;
    }

    private Set<LoadJobNumbersEntity> buildJobNumbersForEntity(Long loadId) {
        return Sets.newHashSet(buildJobNumber(loadId), buildJobNumber(loadId));
    }

    private LoadJobNumbersEntity buildJobNumber(Long loadId) {
        LoadJobNumbersEntity entity = new LoadJobNumbersEntity();
        entity.setId((long) Math.random() * 100);
        entity.setLoadId(loadId);
        entity.setJobNumber(String.valueOf(Math.random()));

        return entity;
    }

    private List<JobNumberDTO> buildJobNumbersDtoList(boolean isNewItems) {
        return isNewItems ? Lists.newArrayList(new JobNumberDTO(null, String.valueOf(Math.random())),
                new JobNumberDTO(null, String.valueOf(Math.random())), new JobNumberDTO(null, String.valueOf(Math.random()))) : Lists
                .newArrayList(new JobNumberDTO(Math.round(Math.random() * 1000), String.valueOf(Math.random())),
                        new JobNumberDTO(Math.round(Math.random() * 1000), String.valueOf(Math.random())),
                        new JobNumberDTO(Math.round(Math.random() * 1000), String.valueOf(Math.random())));
    }

    private void assertDtoJobNumbers(Set<LoadJobNumbersEntity> expectedSet, List<JobNumberDTO> actualList) {
        Assert.assertNotNull(expectedSet);
        Assert.assertEquals(expectedSet.size(), actualList.size());

        LoadJobNumbersEntity expectedEntity = expectedSet.iterator().next();
        JobNumberDTO actualDTO = actualList.get(0);

        Assert.assertNotNull(actualDTO);
        Assert.assertEquals(expectedEntity.getId(), actualDTO.getId());
        Assert.assertEquals(expectedEntity.getJobNumber(), actualDTO.getJobNumber());
    }

    private void assertLoadJobNumbers(List<JobNumberDTO> expected, Set<LoadJobNumbersEntity> actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());

        for (JobNumberDTO dto : expected) {
            LoadJobNumbersEntity entity = findLoadJobNumbersEntityByName(dto.getJobNumber(), actual);
            Assert.assertNotNull(entity);
            Assert.assertEquals(dto.getId(), entity.getId());
            Assert.assertEquals(dto.getJobNumber(), entity.getJobNumber());
        }
    }

    private LoadJobNumbersEntity findLoadJobNumbersEntityByName(final String jobNumber, Set<LoadJobNumbersEntity> list) {
        LoadJobNumbersEntity obj = Iterables.find(list, new Predicate<LoadJobNumbersEntity>() {
            @Override
            public boolean apply(LoadJobNumbersEntity input) {
                return input.getJobNumber().equalsIgnoreCase(jobNumber);
            }
        });

        return obj;
    }

    private Set<LoadJobNumbersEntity> getJobNumbersEntitiesFromDtoList(List<JobNumberDTO> dtos) {
        Set<LoadJobNumbersEntity> entities = new HashSet<LoadJobNumbersEntity>(dtos.size());
        for (JobNumberDTO dto : dtos) {
            LoadJobNumbersEntity entity = new LoadJobNumbersEntity();
            entity.setId(dto.getId());
            entity.setJobNumber(dto.getJobNumber());

            entities.add(entity);
        }

        return entities;
    }
}
