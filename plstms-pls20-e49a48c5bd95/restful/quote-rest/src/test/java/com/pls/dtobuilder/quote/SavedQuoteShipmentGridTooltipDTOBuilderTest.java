package com.pls.dtobuilder.quote;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.shipment.domain.SavedQuoteAccessorialEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsItemEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;

/**
 * Test cases for {@link com.pls.dtobuilder.savedquote.SavedQuoteShipmentGridTooltipDTOBuilder}.
 *
 * @author Ivan Shapovalov.
 * @author Aleksandr Leshchenko
 */
public class SavedQuoteShipmentGridTooltipDTOBuilderTest {
    private static final String TEST_CARRIER_NAME = "Test carrier name";

    private static final int TEST_PRODUCTS_SIZE = 1;

    private static final BigDecimal TOTAL_COST = new BigDecimal(100);

    private static final BigDecimal TOTAL_REVENUE = new BigDecimal(99);

    private static final String TEST_MATERIAL = "PrDesc, 100 Class, 4 Lbs, 3x1x2 inch, Hazmat";

    private static final String TEST_CUSTOMER_NAME = "Test customer name";

    private SavedQuoteShipmentGridTooltipDTOBuilder sut = new SavedQuoteShipmentGridTooltipDTOBuilder();

    @Test
    public void testBuildDTO() {
        SavedQuoteEntity entity = createLoadEntity();

        ShipmentGridTooltipDTO result = sut.buildDTO(entity);

        Assert.assertNotNull(result);

        Assert.assertSame(TEST_CARRIER_NAME, result.getCarrier());

        Assert.assertSame(TEST_CUSTOMER_NAME, result.getCustomerName());

        Assert.assertNotNull(result.getOrigin());
        Assert.assertNotNull(result.getDestination());

        Assert.assertNotNull(result.getProducts());
        Assert.assertEquals(1, result.getProducts().size());
        Assert.assertEquals(TEST_MATERIAL, result.getProducts().get(0));

        Assert.assertEquals(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()), result.getLogoPath());
        Assert.assertEquals(TEST_PRODUCTS_SIZE, result.getProducts().size());

        Assert.assertSame(TOTAL_REVENUE, result.getTotalRevenue());
        Assert.assertNull(result.getTotalCost());
    }

    @Test
    public void testFillPlsUserRelatedData() {
        SavedQuoteEntity entity = createLoadEntity();

        ShipmentGridTooltipDTO result = new ShipmentGridTooltipDTO();
        sut.fillPlsUserRelatedData(result, entity);
        Assert.assertSame(TOTAL_COST, result.getTotalCost());
    }

    private SavedQuoteEntity createLoadEntity() {
        SavedQuoteEntity entity = new SavedQuoteEntity();
        CustomerEntity customer = new CustomerEntity();
        customer.setName(TEST_CUSTOMER_NAME);
        entity.setCustomer(customer);

        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        carrier.setName(TEST_CARRIER_NAME);
        entity.setCarrier(carrier);

        entity.setRoute(createRoute());

        SavedQuoteCostDetailsEntity savedQuoteCostDetailsEntity = new SavedQuoteCostDetailsEntity();
        savedQuoteCostDetailsEntity.setStatus(Status.ACTIVE);
        Set<SavedQuoteCostDetailsItemEntity> costDetailItems = new HashSet<SavedQuoteCostDetailsItemEntity>();

        savedQuoteCostDetailsEntity.setCostDetailsItems(costDetailItems);

        savedQuoteCostDetailsEntity.setTotalCost(TOTAL_COST);
        savedQuoteCostDetailsEntity.setTotalRevenue(TOTAL_REVENUE);

        entity.setCostDetails(savedQuoteCostDetailsEntity);

        SavedQuoteAccessorialEntity savedQuotePickupTypeAccessorialEntity = new SavedQuoteAccessorialEntity();
        savedQuotePickupTypeAccessorialEntity.setSavedQuote(entity);

        AccessorialTypeEntity pickupAccessorialType = new AccessorialTypeEntity("REP");
        savedQuotePickupTypeAccessorialEntity.setAccessorialType(pickupAccessorialType);
        pickupAccessorialType.setDescription("Residential pickup");
        pickupAccessorialType.setAccessorialGroup(LtlAccessorialGroup.PICKUP);
        pickupAccessorialType.setApplicableTo(ApplicableToUnit.LTL);
        pickupAccessorialType.setStatus(Status.ACTIVE);

        SavedQuoteAccessorialEntity savedQuoteDeliveryTypeAccessorial = new SavedQuoteAccessorialEntity();
        savedQuoteDeliveryTypeAccessorial.setSavedQuote(entity);

        AccessorialTypeEntity deliveryAccessorialType = new AccessorialTypeEntity("IDL");
        savedQuoteDeliveryTypeAccessorial.setAccessorialType(deliveryAccessorialType);
        deliveryAccessorialType.setDescription("Inside Delivery");
        deliveryAccessorialType.setAccessorialGroup(LtlAccessorialGroup.DELIVERY);
        deliveryAccessorialType.setApplicableTo(ApplicableToUnit.LTL);
        deliveryAccessorialType.setStatus(Status.ACTIVE);

        Set<SavedQuoteAccessorialEntity> accessorials = new HashSet<SavedQuoteAccessorialEntity>();
        accessorials.add(savedQuotePickupTypeAccessorialEntity);
        accessorials.add(savedQuoteDeliveryTypeAccessorial);
        entity.setAccessorials(accessorials);

        entity.setMaterials(new HashSet<SavedQuoteMaterialEntity>());
        entity.getMaterials().add(createMaterial());

        return entity;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBuildEntity() {
        sut.buildEntity(new ShipmentGridTooltipDTO());
    }

    private SavedQuoteMaterialEntity createMaterial() {
        SavedQuoteMaterialEntity material = new SavedQuoteMaterialEntity();
        material.setProductCode("PrCode");
        material.setProductDescription("PrDesc");
        material.setCommodityClass(CommodityClass.CLASS_100);
        material.setWidth(new BigDecimal(1));
        material.setHeight(new BigDecimal(2));
        material.setLength(new BigDecimal(3));
        material.setWeight(new BigDecimal(4));
        material.setHazmat(Boolean.TRUE);
        material.setId((long) (Math.random() * 100));
        return material;
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
