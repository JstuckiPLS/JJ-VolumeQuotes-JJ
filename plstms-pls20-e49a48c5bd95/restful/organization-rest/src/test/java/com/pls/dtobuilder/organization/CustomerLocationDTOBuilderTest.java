package com.pls.dtobuilder.organization;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.organization.AccountExecutiveEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.dto.organization.CustomerLocationDTO;
import com.pls.dtobuilder.organization.CustomerLocationDTOBuilder.DataProvider;

/**
 * Test cases for {@link CustomerLocationDTOBuilder}.
 * 
 * @author Artem Arapov
 *
 */
public class CustomerLocationDTOBuilderTest {

    private static final Calendar START_CALENDAR = Calendar.getInstance();
    private static final Calendar END_CALENDAR = Calendar.getInstance();
    static {
        START_CALENDAR.set(Calendar.YEAR, 2010);
        END_CALENDAR.set(Calendar.YEAR, 3000);
    }

    private static final long EXPECTED_PERSON_ID = (long) (Math.random() * 100);
    private static final long EXPECTED_LOCATION_ID = (long) (Math.random() * 100);
    private static final String EXPECTED_LOCATION_NAME = String.valueOf(Math.random());
    private static final Boolean EXPECTED_DEFAULT_NODE = Boolean.FALSE;
    private static final long EXPECTED_ACC_EXEC_ID = (long) (Math.random() * 100);
    private static final Date EXPECTED_EFFECTIVE_DATE = START_CALENDAR.getTime();
    private static final Date EXPECTED_EXPIRED_DATE = END_CALENDAR.getTime();
    private static final long EXPECTED_BILL_TO_ID = (long) (Math.random() * 100);

    private CustomerLocationDTOBuilder sut = new CustomerLocationDTOBuilder(new DataProvider() {

        @Override
        public OrganizationLocationEntity getOrganizationLocation(Long id) {
            OrganizationLocationEntity location = buildEmptyLocationEntity(EXPECTED_LOCATION_ID);

            OrganizationEntity organizationEntity = new OrganizationEntity();
            organizationEntity.setId((long) (Math.random() * 100));
            location.setOrganization(organizationEntity);

            return location;
        }

    });

    @Test
    public void testBuildDTO() {
        OrganizationLocationEntity entity = buildOrganizationLocationEntity(EXPECTED_LOCATION_ID);

        CustomerLocationDTO dto = sut.buildDTO(entity);
        Assert.assertNotNull(dto);
        Assert.assertEquals(entity.getId(), dto.getId());
        Assert.assertEquals(entity.getLocationName(), dto.getName());
        Assert.assertEquals(entity.getDefaultNode(), dto.isDefaultNode());

        AccountExecutiveEntity accExecEntity = entity.getActiveAccountExecutive();
        Assert.assertEquals(accExecEntity.getUser().getId(), dto.getAccExecPersonId());
        Assert.assertEquals(accExecEntity.getEffectiveDate(), dto.getAccExecStartDate());
        Assert.assertEquals(accExecEntity.getExpirationDate(), dto.getAccExecEndDate());

        BillToEntity billToEntity = entity.getBillTo();
        Assert.assertNotNull(billToEntity);
        Assert.assertEquals(billToEntity.getId(), dto.getBillToId());
    }

    @Test
    public void testBuildEntity() {
        CustomerLocationDTO dto = buildCustomerLocationDTO();

        OrganizationLocationEntity entity = sut.buildEntity(dto);
        Assert.assertNotNull(entity);
        Assert.assertEquals(dto.getId(), entity.getId());
        Assert.assertEquals(dto.getName(), entity.getLocationName());
        Assert.assertEquals(dto.isDefaultNode(), entity.getDefaultNode());

        AccountExecutiveEntity accExecEntity = entity.getActiveAccountExecutive();
        Assert.assertEquals(dto.getAccExecPersonId(), accExecEntity.getUser().getId());
        Assert.assertEquals(dto.getAccExecStartDate(), accExecEntity.getEffectiveDate());
        Assert.assertEquals(dto.getAccExecEndDate(), accExecEntity.getExpirationDate());

        BillToEntity billToEntity = entity.getBillTo();
        Assert.assertEquals(dto.getBillToId(), billToEntity.getId());
    }

    private OrganizationLocationEntity buildEmptyLocationEntity(Long locationId) {
        OrganizationLocationEntity locationEntity = new OrganizationLocationEntity();
        locationEntity.setId(locationId);
        locationEntity.setDefaultNode(EXPECTED_DEFAULT_NODE);

        return locationEntity;
    }

    private OrganizationLocationEntity buildOrganizationLocationEntity(Long locationId) {
        OrganizationEntity organizationEntity = new OrganizationEntity();
        organizationEntity.setId((long) (Math.random() * 100));

        OrganizationLocationEntity locationEntity = buildEmptyLocationEntity(locationId);
        locationEntity.setOrganization(organizationEntity);
        locationEntity.setLocationName(EXPECTED_LOCATION_NAME);
        locationEntity.setDefaultNode(EXPECTED_DEFAULT_NODE);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(EXPECTED_PERSON_ID);
        AccountExecutiveEntity accExecutiveEntity = new AccountExecutiveEntity(locationEntity, userEntity);
        accExecutiveEntity.setId(EXPECTED_ACC_EXEC_ID);
        accExecutiveEntity.setEffectiveDate(EXPECTED_EFFECTIVE_DATE);
        accExecutiveEntity.setExpirationDate(EXPECTED_EXPIRED_DATE);
        locationEntity.getAccountExecutives().add(accExecutiveEntity);

        BillToEntity billToEntity = new BillToEntity();
        billToEntity.setId(EXPECTED_BILL_TO_ID);
        locationEntity.setBillTo(billToEntity);

        return locationEntity;
    }

    private CustomerLocationDTO buildCustomerLocationDTO() {
        CustomerLocationDTO dto = new CustomerLocationDTO();
        dto.setId(EXPECTED_LOCATION_ID);
        dto.setName(EXPECTED_LOCATION_NAME);
        dto.setAccExecPersonId(EXPECTED_ACC_EXEC_ID);
        dto.setAccExecStartDate(EXPECTED_EFFECTIVE_DATE);
        dto.setAccExecEndDate(EXPECTED_EXPIRED_DATE);
        dto.setDefaultNode(EXPECTED_DEFAULT_NODE);
        dto.setBillToId(EXPECTED_BILL_TO_ID);

        return dto;
    }
}
