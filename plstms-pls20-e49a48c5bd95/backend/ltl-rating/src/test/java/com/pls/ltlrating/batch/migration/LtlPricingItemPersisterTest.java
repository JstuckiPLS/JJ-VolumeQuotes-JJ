package com.pls.ltlrating.batch.migration;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.processor.PriceItemProcessable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link LtlPricingItemPersister}.
 *
 * @author Alex Kyrychenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LtlPricingItemPersisterTest {

    @InjectMocks
    private LtlPricingItemPersister persister;

    @Mock
    private PriceItemProcessable processor1;

    @Mock
    private PriceItemProcessable processor2;

    @Before
    public void setUp() {
        persister.setItemProcessors(Arrays.asList(processor1, processor2));
        doNothing().when(processor1).persist();
        doNothing().when(processor2).persist();
    }

    @Test
    public void shouldBeProcessedByFirstProcessor() throws Exception {
        when(processor1.processItem(any(LtlPricingItem.class))).thenReturn(Boolean.TRUE);
        when(processor2.processItem(any(LtlPricingItem.class))).thenReturn(Boolean.FALSE);

        persister.write(Collections.singletonList(new LtlPricingItem()));

        verify(processor1).processItem(any(LtlPricingItem.class));
        verify(processor2, times(0)).processItem(any(LtlPricingItem.class));

        verify(processor1).persist();
        verify(processor2).persist();
    }

    @Test
    public void shouldBeProcessedBySecondProcessor() throws Exception {
        when(processor1.processItem(any(LtlPricingItem.class))).thenReturn(Boolean.FALSE);
        when(processor2.processItem(any(LtlPricingItem.class))).thenReturn(Boolean.TRUE);

        persister.write(Collections.singletonList(new LtlPricingItem()));

        verify(processor1).processItem(any(LtlPricingItem.class));
        verify(processor2).processItem(any(LtlPricingItem.class));

        verify(processor1).persist();
        verify(processor2).persist();
    }

}
