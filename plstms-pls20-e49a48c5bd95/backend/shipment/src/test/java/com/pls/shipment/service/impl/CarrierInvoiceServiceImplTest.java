package com.pls.shipment.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.shipment.dao.impl.CarrierInvoiceDetailsDaoImpl;
import com.pls.shipment.domain.bo.CarrierInvoiceDetailsListItemBO;

/**
 * Test for {@link CarrierInvoiceServiceImpl}.
 * 
 * @author Dmitriy Davydenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CarrierInvoiceServiceImplTest {

    @InjectMocks
    private CarrierInvoiceServiceImpl sut;

    @Mock
    private CarrierInvoiceDetailsDaoImpl dao;

    @Test
    public void shouldGetUnmatched() {
        List<CarrierInvoiceDetailsListItemBO> expected = new ArrayList<CarrierInvoiceDetailsListItemBO>();
        Mockito.when(dao.getUnmatched()).thenReturn(expected);

        List<CarrierInvoiceDetailsListItemBO> actual = sut.getUnmatched();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
        Mockito.verify(dao).getUnmatched();
    }
    @Test
    public void shouldGetArchived() {
        List<CarrierInvoiceDetailsListItemBO> expected = new ArrayList<CarrierInvoiceDetailsListItemBO>();
        Mockito.when(dao.getArchived()).thenReturn(expected);

        List<CarrierInvoiceDetailsListItemBO> actual = sut.getArchived();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
        Mockito.verify(dao).getArchived();
    }
}
