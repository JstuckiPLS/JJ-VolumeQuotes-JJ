package com.pls.shipment.service.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.service.impl.validation.ShipmentValidator;

/**
 * Shipment validator integration test.
 * 
 * @author Andrey Kachur
 */
public class ShipmentValidationIT extends BaseServiceITClass {

    private static final Long ORG_ID = SecurityTestUtils.DEFAULT_ORGANIZATION_ID;

    private static final Long PERSON_ID = SecurityTestUtils.DEFAULT_PERSON_ID;

    private static final int MAX_PO_NUMBER_LENGTH = 50;

    private LoadEntity minEntity;

    @Autowired
    private ShipmentValidator sut;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();

        minEntity = new LoadEntity();
        CustomerEntity org = new CustomerEntity();
        org.setId(ORG_ID);
        minEntity.setOrganization(org);
        BillToEntity billTo = new BillToEntity();
        billTo.setOrganization(org);
        minEntity.setBillTo(billTo);
        minEntity.setStatus(ShipmentStatus.OPEN);
        minEntity.setPersonId(PERSON_ID);
        minEntity.setLocationId(1L);
        minEntity.getNumbers().setPoNumber("SomeValue");
        minEntity.getNumbers().setPuNumber("SomeValue");
        minEntity.getNumbers().setRefNumber("SomeValue");
        minEntity.getNumbers().setBolNumber("SomeValue");
        minEntity.getNumbers().setSoNumber("SomeValue");
        minEntity.getNumbers().setGlNumber("SomeValue");
        minEntity.getNumbers().setTrailerNumber("SomeValue");
        Set<LoadJobNumbersEntity> jobNumbers = new HashSet<LoadJobNumbersEntity>();
        LoadJobNumbersEntity jobNumber = new LoadJobNumbersEntity();
        jobNumber.setJobNumber("BB2015");
        jobNumbers.add(jobNumber);
        minEntity.getNumbers().setJobNumbers(jobNumbers);
        LoadDetailsEntity originLoadDetails = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originLoadDetails.setArrivalWindowStart(new Date());
        originLoadDetails.setArrivalWindowEnd(new Date(System.currentTimeMillis() + (1000 * 60 * 30)));
        originLoadDetails.setContactEmail("test@gmail.com");
        originLoadDetails.setContactPhone("+1 (313) 9876543");
        originLoadDetails.setAddress(getAddressEntity());
        Calendar calendar = Calendar.getInstance();
        originLoadDetails.setScheduledArrival(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        originLoadDetails.setEarlyScheduledArrival(new Date());
        originLoadDetails.setDeparture(DateUtils.truncate(new Date(), Calendar.DATE));
        minEntity.addLoadDetails(originLoadDetails);
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

    /*@Test(expected = ValidationException.class)
    public void testWithWrongArrivalWindowDate() throws ValidationException {
        minEntity.getOrigin().setArrivalWindowStart(new Date());
        minEntity.getOrigin().setArrivalWindowEnd(new Date(System.currentTimeMillis() + (1000 * 60 * 10)));
        sut.validate(minEntity);
    }

    @Test
    public void testMinimumInfoValidation() throws ValidationException {
        sut.validate(minEntity);
    }

    @Test
    public void testWithoutRequiredFieldsAndWithoutPermissions() throws ValidationException {
        minEntity.getNumbers().setPoNumber(null);
        minEntity.getNumbers().setPuNumber(null);
        minEntity.getNumbers().setRefNumber(null);
        minEntity.getNumbers().setBolNumber(null);
        minEntity.getNumbers().setSoNumber(null);
        minEntity.getNumbers().setGlNumber(null);
        minEntity.getNumbers().setTrailerNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentPO() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_PO.name());

        minEntity.getNumbers().setPoNumber(null);

        sut.validate(minEntity);
    }
*/
    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentPU() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_PU.name());

        minEntity.getNumbers().setPuNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentRef() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_REF.name());

        minEntity.getNumbers().setRefNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentBol() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_BOL.name());

        minEntity.getNumbers().setBolNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentSo() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_SO.name());

        minEntity.getNumbers().setSoNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentGl() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_GL.name());

        minEntity.getNumbers().setGlNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void testWithoutRequireShipmentTrailer() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_TRAILER.name());

        minEntity.getNumbers().setTrailerNumber(null);

        sut.validate(minEntity);
    }

    @Test(expected = ValidationException.class)
    public void shouldFailWithLongPONumber() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_TRAILER.name());

        minEntity.getNumbers().setPoNumber(RandomStringUtils.random(MAX_PO_NUMBER_LENGTH + 1));

        sut.validate(minEntity);
    }

    @Test
    public void shouldAcceptWithShortPONumber() throws ValidationException {
        SecurityTestUtils.login("user", PERSON_ID, Capabilities.REQUIRE_SHIPMENT_TRAILER.name());

        minEntity.getNumbers().setPoNumber(RandomStringUtils.random(MAX_PO_NUMBER_LENGTH));

        sut.validate(minEntity);
    }
}
