package com.pls.dtobuilder.organization;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.EdiRejectedCustomerEntity;
import com.pls.core.domain.organization.OrgServiceEntity;
import com.pls.dto.KeyValueDTO;
import com.pls.dto.organization.CarrierSettingsDTO;
import com.pls.dto.organization.OrgServiceDTO;
import com.pls.dto.organization.PaperworkDTO;
import com.pls.dtobuilder.organization.CarrierSettingsDTOBuilder.DataProvider;

/**
 * Test Cases for {@link CarrierSettingsDTOBuilder}.
 * 
 * @author Artem Arapov
 *
 */
public class CarrierSettingsDTOBuilderTest {

    private int randomIndex;

    @Test
    public void shouldBuildCarrierDTO() {
        CarrierEntity entity = buildRandomCarrierEntity();

        CarrierSettingsDTO dto = CarrierSettingsDTOBuilder.builder().buildDTO(entity);

        Assert.assertNotNull(dto);
        Assert.assertEquals(entity.getId(), dto.getId());
        Assert.assertEquals(entity.getName(), dto.getName());
        Assert.assertEquals(entity.getScac(), dto.getScac());
        Assert.assertNotNull(dto.getOrgService());
        Assert.assertNotNull(dto.getRejectedCustomers());
        Assert.assertEquals(entity.getRejectedCustomers().size(), dto.getRejectedCustomers().size());
    }

    @Test
    public void shouldBuildCarrierEntityAddNew() {
        CarrierSettingsDTO dto = buildRandomCarrierDTO();

        CarrierEntity carrier = new CarrierEntity();
        carrier.setId(dto.getId());

        DataProvider dataProvider = Mockito.mock(DataProvider.class);
        Mockito.when(dataProvider.getCarrierEntity(Mockito.anyLong())).thenReturn(carrier);

        CarrierEntity entity = CarrierSettingsDTOBuilder.builder().addDataProvider(dataProvider).buildEntity(dto);

        Assert.assertNotNull(entity);
        Assert.assertEquals(dto.getId(), entity.getId());
        Assert.assertNotNull(entity.getRejectedCustomers());
        Assert.assertEquals(dto.getRejectedCustomers().size(), entity.getRejectedCustomers().size());
    }

    @Test
    public void shouldBuildCarrierEntityShuffle() {
        CarrierSettingsDTO dto = buildRandomCarrierDTO();

        CarrierEntity carrier = new CarrierEntity();
        carrier.setId(dto.getId());
        carrier.setRejectedCustomers(Sets.newHashSet());

        for (KeyValueDTO item : dto.getRejectedCustomers()) {
            EdiRejectedCustomerEntity customer = new EdiRejectedCustomerEntity();
            customer.setCustomerId(item.getId());
            carrier.getRejectedCustomers().add(customer);
        }

        dto.getRejectedCustomers().addAll(buildRandomRejectedCustomersDTOList());

        DataProvider dataProvider = Mockito.mock(DataProvider.class);
        Mockito.when(dataProvider.getCarrierEntity(Mockito.anyLong())).thenReturn(carrier);

        CarrierEntity entity = CarrierSettingsDTOBuilder.builder().addDataProvider(dataProvider).buildEntity(dto);

        Assert.assertNotNull(entity);
        Assert.assertEquals(dto.getId(), entity.getId());
        Assert.assertNotNull(entity.getRejectedCustomers());
        Assert.assertEquals(dto.getRejectedCustomers().size(), entity.getRejectedCustomers().size());
    }

    private CarrierSettingsDTO buildRandomCarrierDTO() {
        CarrierSettingsDTO dto = new CarrierSettingsDTO();
        dto.setId(random());
        dto.setName(String.valueOf(Math.random()));
        dto.setScac(String.valueOf(Math.random()));
        dto.setOrgService(buildOrgServiceDTO());
        dto.setRejectedCustomers(buildRandomRejectedCustomersDTOList());
        dto.setPaperwork(buildPaperworkDTO());

        return dto;
    }

    private OrgServiceDTO buildOrgServiceDTO() {
        OrgServiceDTO dto = new OrgServiceDTO();
        dto.setImaging("EDI");
        dto.setInvoice("EDI");
        dto.setManualTypeEmail("a@a.com");
        dto.setPickup("EDI");
        dto.setTracking("EDI");

        return dto;
    }

    private PaperworkDTO buildPaperworkDTO() {
        PaperworkDTO dto = new PaperworkDTO();
        dto.setDontRequestPaperwork(true);
        dto.setEmail("testcarrier@mail.com");

        return dto;
    }

    private List<KeyValueDTO> buildRandomRejectedCustomersDTOList() {
        List<KeyValueDTO> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            list.add(buildRandomrejectedCustomerDTO());
        }

        return list;
    }

    private KeyValueDTO buildRandomrejectedCustomerDTO() {
        return new KeyValueDTO(random(), String.valueOf(Math.random()));
    }

    private CarrierEntity buildRandomCarrierEntity() {
        CarrierEntity entity = new CarrierEntity();
        entity.setId(random());
        entity.setName(String.valueOf(Math.random()));
        entity.setScac(String.valueOf(Math.random()));

        OrgServiceEntity orgService = new OrgServiceEntity();
        entity.setOrgServiceEntity(orgService);
        entity.setRejectedCustomers(buildRandomSetOfRejectedCustomers());

        return entity;
    }

    private Set<EdiRejectedCustomerEntity> buildRandomSetOfRejectedCustomers() {
        Set<EdiRejectedCustomerEntity> set = Sets.newHashSet();

        for (int i = 0; i < 10; i++) {
            set.add(buildRandomEdiRejectedCustomer());
        }

        return set;
    }

    private EdiRejectedCustomerEntity buildRandomEdiRejectedCustomer() {
        EdiRejectedCustomerEntity entity = new EdiRejectedCustomerEntity();
        entity.setId(random());
        entity.setCustomer(buildRandomCustomer());

        return entity;
    }

    private CustomerEntity buildRandomCustomer() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(random());
        entity.setName(String.valueOf(Math.random()));

        return entity;
    }

    private Long random() {
        return ((long) (Math.random() * 100)) + randomIndex * 100 + randomIndex++;
    }
}
