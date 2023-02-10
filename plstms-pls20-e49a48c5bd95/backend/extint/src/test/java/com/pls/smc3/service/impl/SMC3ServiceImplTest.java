package com.pls.smc3.service.impl;

import java.util.List;

import com.pls.extint.shared.AvailableCarrierRequestVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.extint.shared.AvailableCarrierVO;
import com.pls.extint.shared.DataModuleVO;
import com.pls.smc3.service.SMC3Service;

/**
 * Test class for {@link SMC3Service}.
 * 
 * @author Pavani Challa
 * 
 */

public class SMC3ServiceImplTest extends BaseServiceITClass {

    @Autowired
    private SMC3Service service;

    @Test
    public void testGetAvailableTariffs() throws Exception {
        List<DataModuleVO> actualTariffs = service.getAvailableTariffs();
        Assert.assertNotNull(actualTariffs);
        Assert.assertFalse(actualTariffs.isEmpty());
        assertListOfDataModuleVO(actualTariffs);
    }

    @Test
    public void testGetAvailableCarrier() throws Exception {
        AvailableCarrierRequestVO availableCarrierRequestVO = new AvailableCarrierRequestVO();
        availableCarrierRequestVO.setScac("scac");
        List<AvailableCarrierVO> carriers = service.getAvailableCarriers(availableCarrierRequestVO);
        Assert.assertNotNull(carriers);
    }

    private void assertListOfDataModuleVO(List<DataModuleVO> actualList) {
        for (DataModuleVO item : actualList) {
            assertTariffNameField(item);
            assertDescriptionField(item);
        }
    }

    private void assertTariffNameField(DataModuleVO actual) {
        String[] nameArray = actual.getTariffName().split("_");
        Assert.assertTrue(nameArray.length == 3);
        String name = nameArray[0];
        Assert.assertFalse(name.isEmpty());
        String actualEffectiveDate = nameArray[1];
        Assert.assertEquals(actual.getEffectiveDate(), actualEffectiveDate);
        String productNumber = nameArray[2];
        Assert.assertEquals(actual.getProductNumber(), productNumber);
    }

    private void assertDescriptionField(DataModuleVO actual) {
        String[] descriptionArray = actual.getDescription().split("_");
        Assert.assertTrue(descriptionArray.length == 2);
        String description = descriptionArray[0];
        Assert.assertFalse(description.isEmpty());
        String actualEffectiveDate = descriptionArray[1];
        Assert.assertEquals(actual.getEffectiveDate(), actualEffectiveDate);
    }
}
