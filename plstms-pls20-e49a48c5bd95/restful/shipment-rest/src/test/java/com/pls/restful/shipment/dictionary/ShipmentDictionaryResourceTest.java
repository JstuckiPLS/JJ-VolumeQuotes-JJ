package com.pls.restful.shipment.dictionary;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.shared.BillToRequiredField;
import com.pls.dto.ValueLabelDTO;



/**
 * Tests for {@link ShipmentDictionaryResource}.
 * 
 * @author Dmitriy Davydenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentDictionaryResourceTest {

    private static final int ENUM_SIZE = BillToRequiredField.values().length;

    @InjectMocks
    private ShipmentDictionaryResource sut;

    @Test
    public void testGetBillToRequiredField() {
        List<ValueLabelDTO> actualList = sut.getBillToRequiredField();
        Assert.assertNotNull(actualList);

        int expectedLength = actualList.size();
        Assert.assertTrue("Length of BillToRequiredField enum list shold be equal to 9", ENUM_SIZE == expectedLength);
    }

}
