package com.pls.core.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * Test cases for {@link CarrierServiceImpl}.
 * 
 * @author Dmitro Nefedchenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CarrierServiceImplTest {

    private static final Long CARRIER_ID = 1L;

    @Mock private CarrierDao carrierDao;

    @InjectMocks private CarrierServiceImpl testee;

    @After
    public void proveZeroInterations() {
        verifyZeroInteractions(carrierDao);
    }

    @Test
    public void shouldFindCarrierById() {
        testee.findCarrierById(CARRIER_ID);

        verify(carrierDao, times(1)).find(CARRIER_ID);
    }

    @Test
    public void shouldFindCarrierByName() {
        testee.findCarrierByName("", 0, 0);

        verify(carrierDao, times(1)).findCarrierByName("", 0, 0);
    }

    @Test
    public void shouldFindCarrierByScac() {
        testee.findByScac("");

        verify(carrierDao, times(1)).findByScac("");
    }

    @Test
    public void shouldFindCarrierByMcNumber() {
        testee.findByMcNumber("");

        verify(carrierDao, times(1)).findByMcNumber("");
    }

    @Test
    public void shouldFindByScacAndMc() {
        testee.findByScacAndMC("", "");

        verify(carrierDao, times(1)).findByScacAndMC("", "");
    }

    @Test
    public void shouldGetDefaultCarrier() {
        testee.getDefaultCarrier();

        verify(carrierDao, times(1)).getDefaultCarrier();
    }

    @Test
    public void shouldSaveCarrierEntity() {
        CarrierEntity entity = new CarrierEntity();
        testee.save(entity);

        verify(carrierDao).saveOrUpdate(entity);
    }
}
