package com.pls.shipment.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.StaleObjectStateException;
import org.hibernate.type.StandardBasicTypes;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;

/**
 * Test cases for {@link com.pls.shipment.dao.impl.LtlShipmentDaoImpl} class.
 *
 * @author Alexey Tarasyuk
 */
public class LtlShipmentDaoImplIT extends AbstractDaoTest {

    private static final Long ACCOUNT_EXECUTIVE = 1L;

    private static final Long ORG_ID = 1L;

    private static final Long SHIPMENT_ID = 56L;

    private static final Long EXPECTED_BILL_TO_ID = 4L;

    @Autowired
    private LtlShipmentDao sut;

    @Test
    public void testFindLastNShipments() {
        List<ShipmentListItemBO> lastNShipments = sut.findLastNShipments(ORG_ID, 2L, 25);
        Assert.assertNotNull(lastNShipments);
        Assert.assertEquals(25, lastNShipments.size());
    }

    @Test
    public void shouldGetShipmentWithAllDependencies() {
        LoadEntity shipment = sut.getShipmentWithAllDependencies(SHIPMENT_ID);
        Assert.assertNotNull(shipment);
        Assert.assertEquals(shipment.getId(), SHIPMENT_ID);

        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin().getLoadMaterials()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin().getLoadMaterials().iterator().next().getPackageType()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin().getAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin().getAddress().getCountry()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrigin().getAddress().getState()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getDestination()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getDestination().getAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getDestination().getAddress().getCountry()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getDestination().getAddress().getState()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLocation()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode().getAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode().getAddress().getCountry()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode().getAddress().getState()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode().getPhone()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getBillingInvoiceNode().getFax()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getInvoiceSettings()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getBillTo().getInvoiceSettings().getProcessingTimeTimezone()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLtlAccessorials()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLtlAccessorials().iterator().next().getAccessorial()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getActiveCostDetails()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getActiveCostDetail().getCostDetailItems()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getVendorBillDetails().getCarrierInvoiceDetails()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next()
                .getOriginAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next()
                .getDestinationAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getAllFinancialAccessorials()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getAllFinancialAccessorials().iterator().next().getCostDetailItems()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getSpecialMessage()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrganization()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrganization().getPhone()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getOrganization().getFax()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getCarrier()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getCarrier().getOrgServiceEntity()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getCarrier().getPhone()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getCarrier().getFax()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLoadNotifications()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLoadNotifications().iterator().next().getNotificationType()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getFreightBillPayTo()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getFreightBillPayTo().getAddress()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getFreightBillPayTo().getAddress().getCountry()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getFreightBillPayTo().getAddress().getState()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getFreightBillPayTo().getPhone()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getLoadAdditionalInfo()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getNumbers().getJobNumbers()));
        Assert.assertTrue(Hibernate.isInitialized(shipment.getNumbers().getJobNumbers().iterator().next()));
    }

    @Test
    public void shouldNotIncrementVersionNumberOnUpdateOfClearEntity() {
        LoadEntity shipment = sut.find(SHIPMENT_ID);
        Assert.assertEquals(new Integer(1), shipment.getVersion());

        getSession().update(shipment);

        flushAndClearSession();

        shipment = sut.find(SHIPMENT_ID);
        Assert.assertEquals(new Integer(1), shipment.getVersion());
    }

    @Test
    public void shouldIncrementVersionNumberOnUpdateOfDirtyEntity() {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("test");

        LoadEntity shipment = sut.find(SHIPMENT_ID);
        shipment.getNumbers().setBolNumber("some new value");
        Assert.assertEquals(new Integer(1), shipment.getVersion());

        getSession().update(shipment);

        flushAndClearSession();

        shipment = sut.find(SHIPMENT_ID);
        Assert.assertEquals(new Integer(2), shipment.getVersion());
    }

    @Test(expected = StaleObjectStateException.class)
    public void shouldFailUpdatingLoadWithOldVersion() {
        LoadEntity shipment = sut.find(SHIPMENT_ID);
        Assert.assertEquals(new Integer(1), shipment.getVersion());
        shipment.setVersion(0);

        getSession().update(shipment);

        flushAndClearSession();
    }

    @Test
    public void shouldGetCalendarActivityForOneDay() {
        Calendar calendar = Calendar.getInstance(Locale.US);

        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date fromDate = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, 24 * 60 * 60 * 1000 - 1);
        Date toDate = calendar.getTime();

        List<ShipmentStatus> statuses = Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, ShipmentStatus.OUT_FOR_DELIVERY);

        List<CalendarDayBO> userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate,
                statuses, ShipmentStatus.DELIVERED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(1, userCalendarActivity.size());
        Assert.assertEquals(new Long(4), userCalendarActivity.get(0).getTotalCount());
        Assert.assertEquals(new BigDecimal("544.55"), userCalendarActivity.get(0).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(0).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(0).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(0).getExactDate());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.DISPATCHED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(1, userCalendarActivity.size());
        Assert.assertEquals(new Long(7), userCalendarActivity.get(0).getTotalCount());
        Assert.assertEquals(new BigDecimal("742.61"), userCalendarActivity.get(0).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(0).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(0).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(0).getExactDate());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.BOOKED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(1, userCalendarActivity.size());
        Assert.assertEquals(new Long(7), userCalendarActivity.get(0).getTotalCount());
        Assert.assertEquals(new BigDecimal("742.61"), userCalendarActivity.get(0).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(0).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(0).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(0).getExactDate());
    }

    @Test
    public void shouldGetCalendarActivityForTwoDays() {
        Calendar calendar = Calendar.getInstance(Locale.US);

        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date fromDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date secondDay = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, 24 * 60 * 60 * 1000 - 1);
        Date toDate = calendar.getTime();

        List<ShipmentStatus> statuses = Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, ShipmentStatus.OUT_FOR_DELIVERY);

        List<CalendarDayBO> userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate,
                statuses, ShipmentStatus.DELIVERED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(2, userCalendarActivity.size());
        int index = secondDay.equals(userCalendarActivity.get(1).getExactDate()) ? 1 : 0;
        Assert.assertEquals(new Long(4), userCalendarActivity.get(index).getTotalCount());
        Assert.assertEquals(new BigDecimal("544.55"), userCalendarActivity.get(index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(index).getWeeklyTotal());
        Assert.assertEquals(secondDay, userCalendarActivity.get(index).getExactDate());

        Assert.assertEquals(new Long(6), userCalendarActivity.get(1 - index).getTotalCount());
        Assert.assertEquals(new BigDecimal("370.20"), userCalendarActivity.get(1 - index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(1 - index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(1 - index).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(1 - index).getExactDate());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.DISPATCHED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(2, userCalendarActivity.size());
        index = secondDay.equals(userCalendarActivity.get(1).getExactDate()) ? 1 : 0;
        Assert.assertEquals(new Long(7), userCalendarActivity.get(index).getTotalCount());
        Assert.assertEquals(new BigDecimal("742.61"), userCalendarActivity.get(index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(index).getWeeklyTotal());
        Assert.assertEquals(secondDay, userCalendarActivity.get(index).getExactDate());

        Assert.assertEquals(new Long(7), userCalendarActivity.get(1 - index).getTotalCount());
        Assert.assertEquals(new BigDecimal("439.53"), userCalendarActivity.get(1 - index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(1 - index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(1 - index).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(1 - index).getExactDate());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.BOOKED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(2, userCalendarActivity.size());
        index = secondDay.equals(userCalendarActivity.get(1).getExactDate()) ? 1 : 0;
        Assert.assertEquals(new Long(7), userCalendarActivity.get(index).getTotalCount());
        Assert.assertEquals(new BigDecimal("742.61"), userCalendarActivity.get(index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(index).getWeeklyTotal());
        Assert.assertEquals(secondDay, userCalendarActivity.get(index).getExactDate());

        Assert.assertEquals(new Long(37), userCalendarActivity.get(1 - index).getTotalCount());
        Assert.assertEquals(new BigDecimal("2139.53"), userCalendarActivity.get(1 - index).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(1 - index).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(1 - index).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(1 - index).getExactDate());
    }

    @Test
    public void shouldNotGetCalendarActivityIfNoLoadsAtThatDay() {
        Calendar calendar = Calendar.getInstance(Locale.US);

        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date fromDate = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, 2 * 24 * 60 * 60 * 1000 - 1);
        Date toDate = calendar.getTime();

        List<ShipmentStatus> statuses = Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, ShipmentStatus.OUT_FOR_DELIVERY);

        List<CalendarDayBO> userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate,
                statuses, ShipmentStatus.DELIVERED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertTrue(userCalendarActivity.isEmpty());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.DISPATCHED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertTrue(userCalendarActivity.isEmpty());

        userCalendarActivity = sut.getOrganizationCalendarActivity(1L, fromDate, toDate, statuses,
                ShipmentStatus.BOOKED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertTrue(userCalendarActivity.isEmpty());
    }

    @Test
    public void testUpdateActiveCostDetailsCascade() {
        long shipmentId = 3612282L;
        LoadEntity load = sut.find(shipmentId);
        Assert.assertNotNull(load);
        Assert.assertNotNull(load.getActiveCostDetail());
        Assert.assertTrue(load.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty());
        ArrayList<CarrierInvoiceDetailsEntity> listCarrierInvoiceDetails = new ArrayList<CarrierInvoiceDetailsEntity>();
        CarrierInvoiceDetailsEntity carrierInvoiceDetails = getCarrierInvoiceDetailsEntity();
        listCarrierInvoiceDetails.add(carrierInvoiceDetails);
        load.getVendorBillDetails().setCarrierInvoiceDetails(listCarrierInvoiceDetails);
        sut.saveOrUpdate(load);
        flushAndClearSession();

        load = sut.find(shipmentId);
        Assert.assertNotNull(load);
        Assert.assertNotNull(load.getActiveCostDetail());
        Assert.assertNotNull(load.getVendorBillDetails().getCarrierInvoiceDetails());
    }

    @Test
    public void shouldFindBookedShipment() {
        List<ShipmentTrackingBoardBookedListItemBO> bookedShipments = sut.findBookedShipments(1L);
        assertNotNull(bookedShipments);
        assertFalse(bookedShipments.isEmpty());
        assertEquals(151, bookedShipments.size());
    }

    private CarrierInvoiceDetailsEntity getCarrierInvoiceDetailsEntity() {
        CarrierInvoiceDetailsEntity entity = new CarrierInvoiceDetailsEntity();
        entity.setCarrier(getCarrier());
        entity.setInvoiceNumber("111");
        entity.setInvoiceDate(new Date());
        entity.setReferenceNumber("222");
        entity.setPaymentTerms(PaymentTerms.PREPAID);
        entity.setNetAmount(BigDecimal.valueOf(42));
        entity.setDeliveryDate(new Date());
        entity.setEstDeliveryDate(new Date());
        entity.setBolNumber("333");
        entity.setPoNumber("444");
        entity.setShipperRefNumber("555");
        entity.setProNumber("666");
        entity.setActualPickupDate(new Date());
        entity.setTotalWeight(new BigDecimal("123.45"));
        entity.setTotalCharges(new BigDecimal("100.0"));
        entity.setTotalQuantity(24);
        entity.setStatus(Status.ACTIVE);
        entity.getModification().setCreatedBy(0L);
        return entity;
    }

    @Test
    public void testFindMatchedShipmentsInfoStatuses() {
        List<ShipmentListItemBO> shipmentsInfo = sut.findMatchedShipmentsInfo(null, null, null, null, null, null);
        assertNotNull(shipmentsInfo);
        assertFalse(shipmentsInfo.isEmpty());
        List<ShipmentStatus> statuses = Arrays.asList(ShipmentStatus.OPEN, ShipmentStatus.CANCELLED);
        for (ShipmentListItemBO shipmentListItemBO : shipmentsInfo) {
            assertFalse(statuses.contains(shipmentListItemBO.getStatus()));
        }
    }

    @Test
    public void testFindMatchedShipmentsInfoFinancialStatuses() {
        List<ShipmentListItemBO> shipmentsInfo = sut.findMatchedShipmentsInfo(null, null, null, null, null, null);
        assertNotNull(shipmentsInfo);
        assertFalse(shipmentsInfo.isEmpty());
        List<Long> loadIds = new ArrayList<Long>(shipmentsInfo.size());
        for (ShipmentListItemBO shipmentListItemBO : shipmentsInfo) {
            loadIds.add(shipmentListItemBO.getShipmentId());
        }
        String queryString = "select count(*) from LoadEntity l where l.finalizationStatus = '"
                            + ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE.getStatusCode() + "' and l.id in (:loadIds)";
        Query query = getSession().createQuery(queryString);
        query.setParameterList("loadIds", loadIds);
        Long loadsCount = (Long) query.uniqueResult();
        assertEquals((long) loadsCount, 0);
    }

    @Test
    public void testFindMatchedShipmentsInfoFull() {
        String bolKeyword = "00000028";
        String proKeyword = "00000028";

        Long carrierId = 10L;

        List<ShipmentListItemBO> shipmentsInfo = sut.findMatchedShipmentsInfo(bolKeyword, proKeyword, null, null, carrierId, null);
        assertNotNull(shipmentsInfo);
        assertFalse(shipmentsInfo.isEmpty());

        for (ShipmentListItemBO shipmentListItemBO : shipmentsInfo) {
            assertTrue((shipmentListItemBO.getProNumber() != null && shipmentListItemBO.getProNumber().contains(proKeyword)) || (
                    shipmentListItemBO.getBolNumber() != null && shipmentListItemBO.getBolNumber().contains(bolKeyword))
                    || shipmentListItemBO.getCarrierId() == 10);
        }
    }

    @Test
    public void testFindShipmentInfo() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setDateSearchField("PICKUP");
        search.setCustomer(ORG_ID);

        List<ShipmentListItemBO> result = sut.findShipmentInfo(search, 1L, ShipmentStatus.DISPATCHED);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        for (ShipmentListItemBO load : result) {
            Assert.assertEquals(ORG_ID, load.getCustomerId());
            Assert.assertEquals(ShipmentStatus.DISPATCHED, load.getStatus());
        }
    }

    @Test
    public void testFindAllShipments() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setDateSearchField("PICKUP");
        search.setCustomer(ORG_ID);

        List<ShipmentListItemBO> result = sut.findAllShipments(search, 1L);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        for (ShipmentListItemBO load : result) {
            Assert.assertEquals(ORG_ID, load.getCustomerId());
        }
    }

    @Test
    public void testFindShipmentInfoByDateRange() {
        DateRangeQueryBO dateRange = new DateRangeQueryBO();

        Calendar dateFrom = Calendar.getInstance();
        dateFrom.set(2011, Calendar.JANUARY, 1);
        dateRange.setFromDate(dateFrom.getTime());
        Calendar dateTo = Calendar.getInstance();
        dateTo.set(2014, Calendar.DECEMBER, 31);
        dateRange.setToDate(dateTo.getTime());

        ShipmentStatus dateRangeStatus = ShipmentStatus.DISPATCHED;

        List<ShipmentListItemBO> result = sut.findShipmentsInfo(ORG_ID, dateRangeStatus, dateRange, ShipmentStatus.DISPATCHED);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        System.out.println(result.size());

        for (ShipmentListItemBO load : result) {
            Assert.assertEquals(ORG_ID, load.getCustomerId());
            Assert.assertEquals(ShipmentStatus.DISPATCHED, load.getStatus());
        }
    }

    @Test
    public void testFindUnbilledAccountExecutiveShipments() {
        List<ShipmentListItemBO> result = sut.findUnbilledShipments(ACCOUNT_EXECUTIVE);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testFindUnbilledAccountExecutiveShipmentsWithActiveOrInActiveOrganization() {
        updateOrganizationStatus(1L, false);
        List<ShipmentListItemBO> result = sut.findUnbilledShipments(ACCOUNT_EXECUTIVE);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        updateOrganizationStatus(1L, true);
        result = sut.findUnbilledShipments(ACCOUNT_EXECUTIVE);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void shouldGetShipmentsWithMissingReqPaperwork() {
        getSession().createSQLQuery("insert into FLATBED.org_services(ORG_SERVICE_ID,ORG_ID,imaging,DATE_CREATED,CREATED_BY,VERSION) "
                + "values (0, 17, 'API', current_date, 0, 1)").executeUpdate();
        getSession().createSQLQuery("update loads set carrier_reference_number = 'test' where load_id = 132").executeUpdate();
        Date pickupDate = (Date) getSession().createSQLQuery("select orig.departure pickupDate from loads l inner join load_details orig "
                + "on l.load_id = orig.load_id and orig.load_action = 'P' and orig.point_type = 'O' where l.load_id = 132")
                .addScalar("pickupDate", StandardBasicTypes.DATE).list().get(0);

        List<ShipmentMissingPaperworkBO> shipmentsWithMissingReqPaperwork = sut.getShipmentsWithMissingReqPaperwork(132L);

        Assert.assertNotNull(shipmentsWithMissingReqPaperwork);
        ShipmentMissingPaperworkBO bo = shipmentsWithMissingReqPaperwork.get(0);
        Assert.assertEquals(new Long(132), bo.getLoadId());
        Assert.assertEquals("20121003-2-1", bo.getBol());
        Assert.assertEquals("test", bo.getCarrierRefNum());
        Assert.assertEquals("20121003-2-5", bo.getShipperRefNum());
        Assert.assertEquals(new Long(17), bo.getCarrierOrgId());
        Assert.assertEquals(new Long(1), bo.getShipperOrgId());
        Assert.assertEquals("FXFE", bo.getCarrierScac());
        Assert.assertEquals("91801", bo.getOriginZip());
        Assert.assertEquals("48071", bo.getDestZip());
        Assert.assertEquals(pickupDate, bo.getPickupDate());
    }


    @Test
    public void shouldSaveLoadNotificationsCorrectly() {
        LoadEntity shipment = sut.find(1L);
        shipment.getLoadNotifications().add(getNotification(shipment, "email1", "DISPATCHED"));
        shipment.getLoadNotifications().add(getNotification(shipment, "email2", "DISPATCHED"));
        shipment.getLoadNotifications().add(getNotification(shipment, "email3", "DISPATCHED"));

        getSession().save(shipment);
        getSession().flush();
        getSession().clear();

        shipment = sut.find(1L);
        Assert.assertEquals(3, shipment.getLoadNotifications().size());

        shipment.getLoadNotifications().clear();
        shipment.getLoadNotifications().add(getNotification(shipment, "email4", "DISPATCHED"));
        shipment.getLoadNotifications().add(getNotification(shipment, "email5", "DISPATCHED"));

        getSession().save(shipment);
        getSession().flush();
        getSession().clear();

        shipment = sut.find(1L);
        Assert.assertEquals(2, shipment.getLoadNotifications().size());
    }

    @Test
    public void shouldFindUndelivered() {
        SecurityTestUtils.login("sysadmin");
        List<ShipmentTrackingBoardListItemBO> itemBOs = sut.findUndeliveredShipments();
        assertNotNull(itemBOs);
        assertFalse(itemBOs.isEmpty());
        assertEquals(305, itemBOs.size());
    }

    @Test
    public void shouldFindAllShipments() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setDateSearchField("PICKUP");
        search.setCustomer(ORG_ID);
        List<ShipmentListItemBO> result = sut.findAllShipments(search, 1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(673, result.size());

        int invoiceAuditCount = 0;
        int priceAuditCount = 0;
        int invoiceErrorCount = 0;
        int waitingForVendorBillCount = 0;
        int invoicedCount = 0;
        int transactionalCount = 0;
        int consolidatedCount = 0;
        int noneCount = 0;

        for (ShipmentListItemBO load : result) {
            switch (load.getBillingStatus()) {
            case "Invoice Audit":
                invoiceAuditCount++;
                break;

            case "Billing Hold":
                priceAuditCount++;
                break;

            case "Invoice Error":
                invoiceErrorCount++;
                break;

            case "Waiting for Vendor Bill":
                waitingForVendorBillCount++;
                break;

            case "Invoiced":
                invoicedCount++;
                break;

            case "Transactional":
                transactionalCount++;
                break;

            case "Consolidated":
                consolidatedCount++;
                break;

            case "NONE":
                noneCount++;
                break;

            default:
                throw new AssertionError("Wrong Billing status!");
            }
            Assert.assertEquals(ORG_ID, load.getCustomerId());
        }

        Assert.assertEquals(27, invoiceAuditCount);
        Assert.assertEquals(0, priceAuditCount);
        Assert.assertEquals(2, invoiceErrorCount);
        Assert.assertEquals(139, waitingForVendorBillCount);
        Assert.assertEquals(19, invoicedCount);
        Assert.assertEquals(14, transactionalCount);
        Assert.assertEquals(4, consolidatedCount);
        Assert.assertEquals(468, noneCount);
    }

    @Test
    public void shouldFindOpenRecords() {
        List<ShipmentTrackingBoardListItemBO> openShipments = sut.findOpenShipments(1L, new RegularSearchQueryBO());
        assertNotNull(openShipments);
        assertFalse(openShipments.isEmpty());
        assertEquals(21, openShipments.size());

        Calendar fromDate = Calendar.getInstance();
        fromDate.set(2008, 4, 1);
        Calendar toDate = Calendar.getInstance();
        toDate.set(2008, 4, 30);
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setFromDate(fromDate.getTime());
        search.setToDate(toDate.getTime());
        openShipments = sut.findOpenShipments(1L, search);
        assertNotNull(openShipments);
        assertFalse(openShipments.isEmpty());
        assertEquals(8, openShipments.size());
    }

    @Test
    public void shouldFindShipmentByScacAndProNumber() {
        List<LoadEntity> loads = sut.findAllShipmentsByScacAndProNumber("AMAP", "6018");
        Assert.assertEquals(1, loads.size());
        Assert.assertEquals(new Long(4911779), loads.get(0).getId());
        Assert.assertEquals("AMAP", loads.get(0).getCarrier().getScac());
        Assert.assertEquals("6018", loads.get(0).getNumbers().getProNumber());
    }

    @Test
    public void shouldFindShipmentByShipperRefAndCustomerOrgId() {
        LoadEntity load = sut.findShipmentByShipmentNumber(1L, "00000004");
        Assert.assertNotNull(load);
        Assert.assertEquals(new Long(5), load.getId());
    }

    @Test
    public void shouldFindShipmentByBolAndShipperRef() {
        LoadEntity load = sut.findShipmentByBolAndShipmentNumber(1L, "00000004", "00000004");
        Assert.assertNotNull(load);
        Assert.assertEquals(new Long(5), load.getId());
    }

    @Test
    public void shouldFindMatchingShipmentForCarrierInvoice() {
        getSession().createSQLQuery("update loads set CARRIER_REFERENCE_NUMBER = null").executeUpdate();

        List<LoadEntity> loads = sut.findShipmentByScacAndBolNumberAndZip("1001120058", "AMAP", "46168", "45805");
        Assert.assertEquals(1, loads.size());
        Assert.assertEquals(new Long(4911779), loads.get(0).getId());
        Assert.assertEquals("1001120058", loads.get(0).getNumbers().getBolNumber());
        Assert.assertEquals("AMAP", loads.get(0).getCarrier().getScac());
        Assert.assertEquals("46168", loads.get(0).getOrigin().getAddress().getZip());
        Assert.assertEquals("45805", loads.get(0).getDestination().getAddress().getZip());
    }

    @Test
    public void shouldFindBookedShipmentWithActiveOrInActiveOrganization() {
        updateOrganizationStatus(1L, false);
        List<ShipmentTrackingBoardBookedListItemBO> bookedShipments = sut.findBookedShipments(1L);
        assertNotNull(bookedShipments);
        assertFalse(bookedShipments.isEmpty());
        assertEquals(151, bookedShipments.size());
        updateOrganizationStatus(1L, true);
        bookedShipments = sut.findBookedShipments(1L);
        assertNotNull(bookedShipments);
        assertFalse(bookedShipments.isEmpty());
        assertEquals(151, bookedShipments.size());
    }

    @Test
    public void testUpdateStatus() throws EntityNotFoundException {
        LoadEntity loadEntity = sut.find(1L);
        Assert.assertFalse("Load with ID 1 does not pass for test. It's status already X",
                ShipmentStatus.DISPATCHED.equals(loadEntity.getStatus()));

        sut.updateStatus(1L, ShipmentStatus.DISPATCHED);
        flushAndClearSession();

        LoadEntity result = sut.find(1L);
        Assert.assertNotSame(loadEntity, result);
        Assert.assertEquals(ShipmentStatus.DISPATCHED, result.getStatus());
    }

    @Test
    public void testGetLoadProNumber() {
        String actualResult = sut.getLoadProNumber(SHIPMENT_ID);
        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
    }

    @Test
    public void shouldFindShipmentsByJobNumber() {
        String jobNumber = "10";
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setJob(jobNumber);
        search.setCustomer(ORG_ID);
        List<ShipmentListItemBO> result = sut.findAllShipments(search, 1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

        for (ShipmentListItemBO bo : result) {
            Assert.assertEquals(jobNumber, bo.getJobNumber());
        }
    }

    @Test
    public void shouldFindShipmentsByLoadId() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setLoadId(56L);
        search.setCustomer(ORG_ID);
        List<ShipmentListItemBO> result = sut.findAllShipments(search, 1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetLoadForMatchedVendorBill() {
        LoadEntity loadEntity = sut.find(1L);
        loadEntity.setStatus(ShipmentStatus.DELIVERED);
        loadEntity.setFinalizationStatus(ShipmentFinancialStatus.NONE);

        List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails = buildCarrierInvoiceDetailsEntity();
        LoadDetailsEntity loadDetails = loadEntity.getDestination();
        loadDetails.setDeparture(new Date());
        loadEntity.addLoadDetails(loadDetails);
        loadEntity.getVendorBillDetails().setCarrierInvoiceDetails(carrierInvoiceDetails);
        getSession().saveOrUpdate(loadDetails);
        getSession().saveOrUpdate(carrierInvoiceDetails.iterator().next());
        getSession().saveOrUpdate(loadEntity);
        flushAndClearSession();
        List<LoadEntity> actualResult = sut.getLoadForMatchedVendorBill();
        Assert.assertNotNull(actualResult);
        Assert.assertTrue(actualResult.isEmpty());

        loadEntity = sut.find(1L);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -8);
        LoadDetailsEntity destination = loadEntity.getDestination();
        destination.setDeparture(c.getTime());
        getSession().saveOrUpdate(loadEntity);
        getSession().saveOrUpdate(destination);
        flushAndClearSession();
        actualResult = sut.getLoadForMatchedVendorBill();
        Assert.assertNotNull(actualResult);
        Assert.assertFalse(actualResult.isEmpty());
        Assert.assertEquals(actualResult.size(), 1);
        destination = actualResult.iterator().next().getDestination();
        Long loadId = actualResult.iterator().next().getId();
        Long loadDestinationId = actualResult.iterator().next().getDestination().getId();
        Assert.assertEquals(1L, (long) loadId);
        Assert.assertEquals(loadEntity.getStatus(), ShipmentStatus.DELIVERED);
        Assert.assertEquals(2L, (long) loadDestinationId);
    }

    @Test
    public void shouldUpdateLoadFinancialStatus() {
        SecurityTestUtils.login("USER");
        LoadEntity load = (LoadEntity) getSession().get(LoadEntity.class, 5L);
        assertNotNull(load);
        assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING, load.getFinalizationStatus());

        sut.updateLoadFinancialStatuses(new AuditReasonBO(5L), ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        flushAndClearSession();

        load = (LoadEntity) getSession().get(LoadEntity.class, 5L);
        assertNotNull(load);
        assertEquals(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE, load.getFinalizationStatus());
    }

    @Test
    public void shouldGetBillToByShipmentId() {
        Long actualBillToId = sut.getShipmentBillTo(SHIPMENT_ID);
        Assert.assertEquals(EXPECTED_BILL_TO_ID, actualBillToId);
    }

    @Test
    public void shouldFindMatchedLoadsByProAndScac() {
        List<BigDecimal> result = sut.findMatchedLoadsByProAndOrgId("4111", 7L);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    private LoadNotificationsEntity getNotification(LoadEntity shipment, String emailAddress, String notificationTypeCode) {
        LoadNotificationsEntity notification = new LoadNotificationsEntity();
        notification.setLoad(shipment);
        notification.setEmailAddress(emailAddress);
        NotificationTypeEntity notificationType = new NotificationTypeEntity();
        notificationType.setId(notificationTypeCode);
        notification.setNotificationType(notificationType);
        return notification;
    }


    private CarrierEntity getCarrier() {
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId(7L);
        return carrier;
    }

    private List<CarrierInvoiceDetailsEntity> buildCarrierInvoiceDetailsEntity() {
        List<CarrierInvoiceDetailsEntity> carrierInvoiceDetails = new ArrayList<CarrierInvoiceDetailsEntity>();
        CarrierInvoiceDetailsEntity entity = new CarrierInvoiceDetailsEntity();
        entity.setCarrier(getCarrier());
        entity.setInvoiceNumber("INV#1");
        entity.setInvoiceDate(new Date());
        entity.setReferenceNumber("REF#1");
        entity.setPaymentTerms(PaymentTerms.COLLECT);
        entity.setNetAmount(new BigDecimal(100.42));
        entity.setDeliveryDate(new Date());
        entity.setEstDeliveryDate(new Date());
        entity.setBolNumber("BOL1");
        entity.setPoNumber("PO1");
        entity.setShipperRefNumber("SHIP_NUM");
        entity.setProNumber("PRO_NUM");
        entity.setActualPickupDate(new Date());
        entity.setTotalWeight(new BigDecimal("123.45"));
        entity.setTotalCharges(new BigDecimal("80"));
        entity.setTotalQuantity(437);
        entity.setStatus(Status.ACTIVE);
        entity.setEdi(Boolean.TRUE);
        entity.setMatchedLoadId(1L);
        carrierInvoiceDetails.add(entity);
        return carrierInvoiceDetails;
    }

}
