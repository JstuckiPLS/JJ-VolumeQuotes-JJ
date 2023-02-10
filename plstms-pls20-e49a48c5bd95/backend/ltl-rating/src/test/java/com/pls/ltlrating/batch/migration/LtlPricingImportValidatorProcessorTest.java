package com.pls.ltlrating.batch.migration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.exception.fileimport.ImportException;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.TariffGeoType;

/**
 * Test cases for {@link LtlPricingImportValidatorProcessor}.
 *
 * @author Alex Kyrychenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingImportValidatorProcessorTest {

    public static final String USA = "USA";
    @Mock
    private TariffMatcher tariffMatcher;

    @InjectMocks
    private LtlPricingImportValidatorProcessor processor;

    @Before
    public void setup() throws ImportException {
        Mockito.when(tariffMatcher.mapToSmc3TariffName(Mockito.anyString(), Mockito.any(TariffGeoType.class))).thenReturn("TEST_TARIFF");
    }

    @Test
    public void shouldProcessItem() throws Exception {
        LtlPricingItem pricingItem = getPricingItem();
        LtlPricingItem processedItem = processor.process(pricingItem);
        assertNotNull(processedItem);
        assertSame(pricingItem, processedItem);
    }

    @Test(expected = ImportException.class)
    public void shouldFailedInvalidImportItem() throws Exception {
        LtlPricingItem pricingItem = getPricingItem();
        pricingItem.setError(new ImportException("test"));
        processor.process(pricingItem);
        fail("Shoul get exeception");
    }

    @Test(expected = ImportException.class)
    public void shouldFailedItemWOProfileId() throws Exception {
        LtlPricingItem pricingItem = getPricingItem();
        pricingItem.setProfileId(null);
        processor.process(pricingItem);
        fail("Shoul get exeception");
    }

    private LtlPricingItem getPricingItem() {
        LtlPricingItem item = new LtlPricingItem();
        item.setProfileId(BigInteger.valueOf(1L));
        item.setProfileDetailId(BigInteger.valueOf(1L));
        item.setValid(true);
        item.setOrigin(USA);
        item.setDestination(USA);
        return item;
    }

}
