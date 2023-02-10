package com.pls.shipment.service.audit;

import java.util.List;

import org.fest.util.Arrays;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventDataEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadVendorBillEntity;
import com.pls.shipment.domain.enums.LoadEventType;

/**
 * Test cases of {@link LoadEventBuilder}.
 * 
 * @author Artem Arapov
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadEventBuilderTest {

    private static final Long EXPECTED_LOAD_ID = (long) Math.random() * 100;

    @Mock
    private Session session;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException() {
        LoadEventType expectedType = LoadEventType.LD_SRC;
        String[] values = Arrays.array(String.valueOf(Math.random()), String.valueOf(Math.random()));
        LoadEventBuilder.buildEventEntity(EXPECTED_LOAD_ID, expectedType, values);
    }

    @Test
    public void shouldCreateLoadEventEntityWithSingleValue() {
        LoadEventType expectedType = LoadEventType.LD_SRC;
        String[] values = Arrays.array(String.valueOf(Math.random()));
        LoadEventEntity result = LoadEventBuilder.buildEventEntity(EXPECTED_LOAD_ID, expectedType, values);
        assertLoadEventEntity(result, EXPECTED_LOAD_ID, expectedType, values);
    }

    @Test
    public void shouldCreateLoadEventEntityWithMultipleValues() {
        LoadEventType expectedType = LoadEventType.CUSTTRKCHG;
        String[] values = Arrays.array(String.valueOf(Math.random()), String.valueOf(Math.random()), String.valueOf(Math.random()),
                String.valueOf(Math.random()), String.valueOf(Math.random()));
        LoadEventEntity result = LoadEventBuilder.buildEventEntity(EXPECTED_LOAD_ID, expectedType, values);
        assertLoadEventEntity(result, EXPECTED_LOAD_ID, expectedType, values);
    }

    @Test
    public void shouldBuildEventForVendorBillChange() {
        //Attach Vendor Bill
        assertChangeVendorBillStateEvent(false, LoadEventType.LD_ATT);
        //Detach Vendor Bill
        assertChangeVendorBillStateEvent(true, LoadEventType.LD_DET);
    }

    @Test
    public void shouldBuildEventForCustomerField() {
        LoadEntity loadEntity = new LoadEntity();
        loadEntity.setId((long) Math.random() * 100);
        CustomerEntity oldValue = createRandomCustomerEntity();
        CustomerEntity newValue = createRandomCustomerEntity();

        Mockito.when(session.get(CustomerEntity.class, newValue.getId())).thenReturn(newValue);

        List<LoadEventEntity> optionalEvent =
                LoadEventBuilder.buildLoadEventList(loadEntity, session, LoadTrackingFields.ORGANIZATION, oldValue, newValue);
        Assert.assertNotNull(optionalEvent);
        Assert.assertTrue(!optionalEvent.isEmpty());
        List<LoadEventEntity> event = optionalEvent;
        Assert.assertEquals(LoadEventType.LOADCHG.getDbCode(), event.get(0).getEventTypeCode());
        Assert.assertEquals(LoadEventType.LOADCHG.getRequiredFields(), event.get(0).getData().size());
        for (LoadEventDataEntity dataEntity : event.get(0).getData()) {
            if (dataEntity.getEventDataPK().getOrdinal() == 0) {
                Assert.assertEquals(LoadTrackingFields.ORGANIZATION.getDescription(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 1) {
                Assert.assertEquals(oldValue.getName(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 2) {
                Assert.assertEquals(newValue.getName(), dataEntity.getData());
            }
        }
    }

    @Test
    public void shouldBuildEventForCarrierField() {
        LoadEntity loadEntity = new LoadEntity();
        loadEntity.setId((long) Math.random() * 100);
        CarrierEntity oldValue = createRandomCarrierEntity();
        CarrierEntity newValue = createRandomCarrierEntity();

        List<LoadEventEntity> optionalEvent =
                LoadEventBuilder.buildLoadEventList(loadEntity, session, LoadTrackingFields.CARRIER, oldValue, newValue);
        Assert.assertNotNull(optionalEvent);
        Assert.assertTrue(!optionalEvent.isEmpty());
        List<LoadEventEntity> event = optionalEvent;
        Assert.assertEquals(LoadEventType.LOADCHG.getDbCode(), event.get(0).getEventTypeCode());
        Assert.assertEquals(LoadEventType.LOADCHG.getRequiredFields(), event.get(0).getData().size());
        for (LoadEventDataEntity dataEntity : event.get(0).getData()) {
            if (dataEntity.getEventDataPK().getOrdinal() == 0) {
                Assert.assertEquals(LoadTrackingFields.CARRIER.getDescription(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 1) {
                Assert.assertEquals(oldValue.getName(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 2) {
                Assert.assertEquals(newValue.getName(), dataEntity.getData());
            }
        }
    }

    @Test
    public void shouldBuildEventForBillToField() {
        LoadEntity loadEntity = new LoadEntity();
        loadEntity.setId((long) Math.random() * 100);
        BillToEntity oldValue = createRandomBillToEntity();
        BillToEntity newValue = createRandomBillToEntity();

        List<LoadEventEntity> optionalEvent =
                LoadEventBuilder.buildLoadEventList(loadEntity, session, LoadTrackingFields.BILL_TO, oldValue, newValue);
        Assert.assertNotNull(optionalEvent);
        Assert.assertTrue(!optionalEvent.isEmpty());
        List<LoadEventEntity> event = optionalEvent;
        Assert.assertEquals(LoadEventType.LOADCHG.getDbCode(), event.get(0).getEventTypeCode());
        Assert.assertEquals(LoadEventType.LOADCHG.getRequiredFields(), event.get(0).getData().size());
        for (LoadEventDataEntity dataEntity : event.get(0).getData()) {
            if (dataEntity.getEventDataPK().getOrdinal() == 0) {
                Assert.assertEquals(LoadTrackingFields.BILL_TO.getDescription(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 1) {
                Assert.assertEquals(oldValue.getName(), dataEntity.getData());
            } else if (dataEntity.getEventDataPK().getOrdinal() == 2) {
                Assert.assertEquals(newValue.getName(), dataEntity.getData());
            }
        }
    }

    private CustomerEntity createRandomCustomerEntity() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId((long) Math.random() * 100);
        entity.setName(String.valueOf(Math.random()));

        return entity;
    }

    private CarrierEntity createRandomCarrierEntity() {
        CarrierEntity entity = new CarrierEntity();
        entity.setId((long) Math.random() * 100);
        entity.setName(String.valueOf(Math.random()));

        return entity;
    }

    private BillToEntity createRandomBillToEntity() {
        BillToEntity entity = new BillToEntity();
        entity.setId((long) Math.random() * 100);
        entity.setName(String.valueOf(Math.random()));

        return entity;
    }

    private void assertChangeVendorBillStateEvent(boolean initialVendorBillFlagState, LoadEventType type) {
        LoadEntity loadEntity = new LoadEntity();
        loadEntity.getVendorBillDetails().setFrtBillRecvFlag(initialVendorBillFlagState);

        LoadVendorBillEntity vendorBillEntityOld = loadEntity.getVendorBillDetails();
        LoadVendorBillEntity vendorBillEntityNew = new LoadVendorBillEntity();
        vendorBillEntityNew.setFrtBillRecvFlag(!initialVendorBillFlagState);

        loadEntity.setVendorBillDetails(vendorBillEntityNew);

       List<LoadEventEntity> eventList = LoadEventBuilder.buildLoadEventList(loadEntity, session, LoadTrackingFields.VENDOR_BILL_DETAILS,
                                           vendorBillEntityOld, vendorBillEntityNew);

        Assert.assertNotNull(eventList);
        Assert.assertTrue(!eventList.isEmpty());

        List<LoadEventEntity> newEventList = eventList;
        Assert.assertEquals(type.getDbCode(), newEventList.get(0).getEventTypeCode());
    }

    private void assertLoadEventEntity(LoadEventEntity expectedEntity, Long expectedLoadId, LoadEventType expectedType, String[] expectedValues) {
        Assert.assertNotNull(expectedEntity);
        Assert.assertEquals(expectedLoadId, expectedEntity.getLoadId());
        Assert.assertEquals(expectedType.getDbCode(), expectedEntity.getEventTypeCode());
        Assert.assertEquals(expectedType.getRequiredFields(), expectedEntity.getData().size());

        for (int i = 0; i < expectedEntity.getData().size(); i++) {
            LoadEventDataEntity expectedDataEntity = expectedEntity.getData().get(i);
            Assert.assertNotNull(expectedDataEntity);
            Assert.assertEquals(i, (int) expectedDataEntity.getEventDataPK().getOrdinal());
            Assert.assertEquals(expectedValues[i], expectedDataEntity.getData());
        }
    }
}
