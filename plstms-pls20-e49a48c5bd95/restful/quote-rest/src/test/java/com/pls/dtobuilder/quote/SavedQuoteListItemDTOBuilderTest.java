package com.pls.dtobuilder.quote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;

import org.junit.Test;

import com.pls.dto.quote.SavedQuoteListItemDTO;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Test cases for {@link com.pls.dtobuilder.savedquote.SavedQuoteListItemDTOBuilder}.
 * 
 * @author Ivan Shapovalov.
 * @author Aleksandr Leshchenko
 */
public class SavedQuoteListItemDTOBuilderTest {

    private SavedQuoteListItemDTOBuilder builder = new SavedQuoteListItemDTOBuilder();

    @Test
    public void testBuildDTO() {
        SavedQuoteBO bo = createSavedQuoteEntity();

        SavedQuoteListItemDTO dto = builder.buildDTO(bo);

        assertSame(bo.getId(), dto.getId());
        assertSame(bo.getQuoteId(), dto.getQuoteId());

        assertSame(bo.getOriginZip(), dto.getOrigin().getZip());
        assertSame(bo.getOriginState(), dto.getOrigin().getState());
        assertSame(bo.getOriginCity(), dto.getOrigin().getCity());

        assertSame(bo.getDestZip(), dto.getDestination().getZip());
        assertSame(bo.getDestState(), dto.getDestination().getState());
        assertSame(bo.getDestCity(), dto.getDestination().getCity());

        assertSame(bo.getCarrierName(), dto.getCarrierName());
        assertSame(bo.getCustomerName(), dto.getCustomerName());

        assertSame(bo.getEstimatedTransitTime(), dto.getEstimatedTransitTime());
        assertSame(bo.getCarrierCost(), dto.getCarrierCost());
        assertSame(bo.getCustomerRevenue(), dto.getCustomerRevenue());
        assertSame(bo.getShipperBaseRate(), dto.getShipperBaseRate());

        assertEquals(bo.getWeight(), dto.getWeight());
        assertSame(SavedQuoteListItemDTOBuilder.MULTIPLE_COMMODITY_CLASSES_LABEL, dto.getCommodityClass());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBuildEntity() {
        builder.buildEntity(new SavedQuoteListItemDTO());
    }

    private SavedQuoteBO createSavedQuoteEntity() {
        SavedQuoteBO bo = new SavedQuoteBO();
        bo.setOriginCity("originCity" + Math.random());
        bo.setOriginState("originState" + Math.random());
        bo.setOriginZip("originZip" + Math.random());
        bo.setDestCity("destCity" + Math.random());
        bo.setDestState("destState" + Math.random());
        bo.setDestZip("destZip" + Math.random());
        bo.setCarrierName("name" + Math.random());
        bo.setCustomerName("name" + Math.random());
        bo.setCarrierCost(BigDecimal.valueOf(Math.random()));
        bo.setCustomerRevenue(BigDecimal.valueOf(Math.random()));
        bo.setEstimatedTransitTime((long) (Math.random() * 100));
        bo.setShipperBaseRate(BigDecimal.valueOf(Math.random()));
        bo.setWeight(BigDecimal.TEN);
        bo.setCommodityClass("Multi");
        return bo;
    }

}
