package com.pls.shipment.service.validation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.ManualBolStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolJobNumberEntity;
import com.pls.shipment.service.impl.validation.ManualBolValidator;


/**
 * Manual Bol Validator test cases.
 * 
 * @author Artem Arapov
 *
 */
public class ManualBolValidatorIT extends BaseServiceITClass {

    private static final Long ORG_ID = SecurityTestUtils.DEFAULT_ORGANIZATION_ID;

    private static final Long PERSON_ID = SecurityTestUtils.DEFAULT_PERSON_ID;

    private static final int MAX_PO_NUMBER_LENGTH = 50;

    private ManualBolEntity minEntity;

    @Autowired
    private ManualBolValidator sut;

    @Before
    public void setUp() {
        SecurityTestUtils.logout();

        minEntity = new ManualBolEntity();
        minEntity.setBillTo(new BillToEntity());
        CustomerEntity org = new CustomerEntity();
        org.setId(ORG_ID);
        minEntity.setOrganization(org);
        minEntity.setStatus(ManualBolStatus.CUSTOMER_TRUCK);
        minEntity.setLocation(new OrganizationLocationEntity());
        minEntity.getLocation().setId(1L);
        minEntity.getNumbers().setPoNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setPuNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setRefNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setBolNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setSoNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setGlNumber(String.valueOf(Math.random()));
        minEntity.getNumbers().setTrailerNumber(String.valueOf(Math.random()));
        Set<ManualBolJobNumberEntity> jobNumbers = new HashSet<ManualBolJobNumberEntity>();
        ManualBolJobNumberEntity jobNumber = new ManualBolJobNumberEntity();
        jobNumber.setJobNumber("BB2015");
        jobNumbers.add(jobNumber);
        minEntity.getNumbers().setJobNumbers(jobNumbers);
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
