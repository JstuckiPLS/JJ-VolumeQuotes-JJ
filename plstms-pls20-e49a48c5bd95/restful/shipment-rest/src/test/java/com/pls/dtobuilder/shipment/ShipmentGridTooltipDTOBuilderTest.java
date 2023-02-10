package com.pls.dtobuilder.shipment;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;

/**
 * Test cases for {@link ShipmentGridTooltipDTOBuilder}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class ShipmentGridTooltipDTOBuilderTest {

    private static final boolean HAZMAT = true;

    private static final String TEST_CARRIER_NAME = "Test carrier name";

    private static final int TEST_PRODUCTS_SIZE = 1;

    private static final String ORIGIN_CONTACT_NAME = "Origin contact name";

    private static final String ORIGIN_ADDRESS_NAME = "Origin address name";

    private static final String ORIGIN_ADDRESS_CODE = "Origin address code";

    private static final String DESTINATION_CONTACT_NAME = "Destination contact name";

    private static final String DESTINATION_ADDRESS_NAME = "Destination address name";

    private static final String DESTINATION_ADDRESS_CODE = "Destination address code";

    private static final String TEST_COUNTRY_NAME = "Ukraine";

    private static final BigDecimal TOTAL_COST = new BigDecimal(100);

    private static final BigDecimal TOTAL_REVENUE = new BigDecimal(99);

    private static final String TEST_CUSTOMER_NAME = "Test customer name";

    private final ShipmentGridTooltipDTOBuilder sut = new ShipmentGridTooltipDTOBuilder();

    @Test
    public void testBuildDTO() {
        LoadEntity entity = createLoadEntity();

        ShipmentGridTooltipDTO result = sut.buildDTO(entity);

        Assert.assertNotNull(result);

        Assert.assertSame(TEST_CARRIER_NAME, result.getCarrier());

        Assert.assertNotNull(result.getOrigin());
        Assert.assertNotNull(result.getDestination());
        Assert.assertSame(ORIGIN_ADDRESS_NAME, result.getOrigin().getAddressName());
        Assert.assertSame(ORIGIN_ADDRESS_CODE, result.getOrigin().getAddressCode());
        Assert.assertSame(ORIGIN_CONTACT_NAME, result.getOrigin().getContactName());
        Assert.assertSame(DESTINATION_ADDRESS_NAME, result.getDestination().getAddressName());
        Assert.assertSame(DESTINATION_ADDRESS_CODE, result.getDestination().getAddressCode());
        Assert.assertSame(DESTINATION_CONTACT_NAME, result.getDestination().getContactName());

        Assert.assertEquals(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()), result.getLogoPath());
        Assert.assertEquals(TEST_PRODUCTS_SIZE, result.getProducts().size());

        Assert.assertSame(TOTAL_REVENUE, result.getTotalRevenue());
        Assert.assertSame(TEST_CUSTOMER_NAME, result.getCustomerName());
        Assert.assertNull(result.getTotalCost());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBuildEntity() {
        new ShipmentGridTooltipDTOBuilder().buildEntity(new ShipmentGridTooltipDTO());
    }

    @Test
    public void testFillPlsUserRelatedData() {
        LoadEntity entity = createLoadEntity();

        ShipmentGridTooltipDTO result = new ShipmentGridTooltipDTO();
        new ShipmentGridTooltipDTOBuilder().fillPlsUserRelatedData(result, entity);
        Assert.assertSame(TOTAL_COST, result.getTotalCost());
    }

    private LoadEntity createLoadEntity() {
        LoadEntity entity = new LoadEntity();
        CustomerEntity organization = new CustomerEntity();
        organization.setName(TEST_CUSTOMER_NAME);
        entity.setOrganization(organization);

        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        carrier.setName(TEST_CARRIER_NAME);
        entity.setCarrier(carrier);

        LoadDetailsEntity originDetail = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        originDetail.setAddress(prepareAddress("Odessa", "65053"));
        originDetail.setContactName(ORIGIN_CONTACT_NAME);
        originDetail.setContact(ORIGIN_ADDRESS_NAME);
        originDetail.setAddressCode(ORIGIN_ADDRESS_CODE);
        originDetail.setLoadMaterials(prepareLoadMaterials());
        entity.addLoadDetails(originDetail);


        LoadDetailsEntity destinationDetail = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destinationDetail.setAddress(prepareAddress("Kharkov", "654123"));
        destinationDetail.setContactName(DESTINATION_CONTACT_NAME);
        destinationDetail.setContact(DESTINATION_ADDRESS_NAME);
        destinationDetail.setAddressCode(DESTINATION_ADDRESS_CODE);
        entity.addLoadDetails(destinationDetail);

        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        LoadCostDetailsEntity loadCostDetailsEntity = new LoadCostDetailsEntity();
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();

        loadCostDetailsEntity.setCostDetailItems(costDetailItems);
        loadCostDetailsEntity.setStatus(Status.ACTIVE);
        loadCostDetailsEntity.setTotalCost(TOTAL_COST);
        loadCostDetailsEntity.setTotalRevenue(TOTAL_REVENUE);

        activeCostDetails.add(loadCostDetailsEntity);
        entity.setCostDetails(activeCostDetails);

        entity.setStatus(ShipmentStatus.IN_TRANSIT);

        return entity;
    }

    private AddressEntity prepareAddress(String city, String zip) {
        AddressEntity result = new AddressEntity();
        result.setCity(city);
        result.setZip(zip);
        CountryEntity country = new CountryEntity();
        country.setName(TEST_COUNTRY_NAME);
        result.setCountry(country);
        StateEntity state = new StateEntity();
        state.setStateName("Ukraine");
        state.setStatePK(new StatePK());
        state.getStatePK().setStateCode("UKR");
        result.setState(state);
        return result;
    }

    private Set<LoadMaterialEntity> prepareLoadMaterials() {
        Set<LoadMaterialEntity> loadMaterials = new HashSet<LoadMaterialEntity>();
        LoadMaterialEntity material = new LoadMaterialEntity();
        material.setHazmat(HAZMAT);
        material.setCommodityClass(CommodityClass.CLASS_50);
        material.setHeight(BigDecimal.valueOf(20.5));
        material.setWidth(BigDecimal.valueOf(30.5));
        material.setLength(BigDecimal.valueOf(40.4));
        material.setWeight(BigDecimal.valueOf(125.6));
        loadMaterials.add(material);
        return loadMaterials;
    }
}
