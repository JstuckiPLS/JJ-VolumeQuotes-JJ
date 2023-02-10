package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.BillToDao;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.BillToRequiredFieldEntity;
import com.pls.core.shared.BillToRequiredField;

/**
 * Basic test.
 * @author Dmitriy Nefedchenko
 *
 */
public class BillToDaoImplIT extends AbstractDaoTest {
    @Autowired private BillToDao sut;

    @Test
    public void testFindById() {
        assertNotNull(sut.find(1L));
    }

    @Test
    public void shouldNotUpdateVersionOfEDISettingsForEnumListToStringUserType() {
        BillToEntity billTo = sut.find(1L);
        billTo.getEdiSettings().setEdiStatus(Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DELIVERED));
        billTo.getEdiSettings().setEdiType(Arrays.asList(EdiType.EDI_211, EdiType.EDI_214));
        getSession().flush();
        getSession().clear();

        billTo = sut.find(1L);
        Integer version = billTo.getEdiSettings().getVersion();

        getSession().flush();
        getSession().clear();
        billTo = sut.find(1L);
        Assert.assertEquals(version, billTo.getEdiSettings().getVersion());
    }

    @Test
    public void shouldNotUpdateVersionOfEDISettingsForEnumListToStringUserTypeWithNullValues() {
        BillToEntity billTo = sut.find(1L);
        billTo.getEdiSettings().setEdiStatus(null);
        billTo.getEdiSettings().setEdiType(null);
        getSession().flush();
        getSession().clear();

        billTo = sut.find(1L);
        Integer version = billTo.getEdiSettings().getVersion();

        getSession().flush();
        getSession().clear();
        billTo = sut.find(1L);
        Assert.assertEquals(version, billTo.getEdiSettings().getVersion());
    }

    @Test
    public void testFindByOrgIdAndCurrencyCode() {
        List<BillToEntity> billTo = sut.findBillTo(1L, Currency.CAD);
        assertNotNull(billTo);
        assertFalse(billTo.isEmpty());
        assertEquals(5, billTo.size());

        billTo = sut.findBillTo(1L, Currency.USD);
        assertNotNull(billTo);
        assertFalse(billTo.isEmpty());

        billTo = sut.findBillTo(1L, null);
        assertNotNull(billTo);
        assertFalse(billTo.isEmpty());
    }

    @Test
    public void sohuldPassValidationWithNonExistentName() {
        String nonExistentName = "some non existent name";
        boolean exists = sut.validateDuplicateName(nonExistentName, 1L);

        assertFalse(exists);
    }

    @Test
    public void shouldFailValidationWithExistentName() {
        String exsistentName = "Haynes International, INC.";
        boolean exists = sut.validateDuplicateName(exsistentName, 1L);

        assertTrue(exists);
    }

    @Test
    public void shouldFindKeyValueByOrgId() {
        List<KeyValueBO> actualList = sut.getIdAndNameByOrgId(1L);
        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());
    }

    @Test
    public void shouldFindEntityByNameAndOrgId() {
        String name = "Seacom";
        Long orgId = 1L;
        Long billToId = 10L;
        BillToEntity billTo = sut.findByCustomerAndBillToName(name, orgId);
        Assert.assertEquals(billTo.getId(), billToId);
        Assert.assertNotNull(billTo);
    }

    //TODO: Uncomment code inside test and change it on setBillTo(BillToEntity entity) method.
    @Ignore
    @Test
    public void shouldSaveBillToWithRequiredFields() {
        BillToEntity billTo = new BillToEntity();
        BillToRequiredFieldEntity billToRequiredFieldEntity = new BillToRequiredFieldEntity();
//        billToRequiredFieldEntity.setBillToId(1L);
        billToRequiredFieldEntity.setFieldName(BillToRequiredField.BOL);
        billTo.setBillToRequiredField(new HashSet<BillToRequiredFieldEntity>());
        billTo.getBillToRequiredField().add(billToRequiredFieldEntity);
        BillToEntity billToSaved = sut.saveOrUpdate(billTo);
        assertNotNull(billToSaved.getId());
        BillToEntity billTo2 = sut.find(billToSaved.getId());
        assertEquals(billTo2.getBillToRequiredField().iterator().next().getFieldName(), BillToRequiredField.BOL);
    }
}
