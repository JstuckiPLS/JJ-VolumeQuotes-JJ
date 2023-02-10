package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.StateEntity;
import com.pls.core.domain.address.StatePK;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CountryEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.shipment.dao.ManualBolDao;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolJobNumberEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.bo.ManualBolListItemBO;

/**
 * Test cases for {@link ManualBolDaoImpl}.
 * 
 * @author Artem Arapov
 *
 */
public class ManualBolDaoImplIT extends AbstractDaoTest {

    private static final Long CUSTOMER_ID = 1L;

    @Autowired
    private ManualBolDao sut;

    @Test
    public void shouldInsertNewEntity() {
        ManualBolEntity newEntity = createRandomManualBol();
        ManualBolEntity actualEntity = sut.saveOrUpdate(newEntity);

        flushAndClearSession();

        Assert.assertNotNull(actualEntity);
        Assert.assertNotNull(actualEntity.getMaterials());
        Assert.assertEquals(1L, actualEntity.getMaterials().size());
        Assert.assertEquals(1L, actualEntity.getDocuments().size());
        Assert.assertEquals(2L, actualEntity.getAddresses().size());
        Assert.assertEquals(1L, actualEntity.getNumbers().getJobNumbers().size());
    }

    @Test
    public void shouldFindAllShipments() {
        RegularSearchQueryBO search = new RegularSearchQueryBO();
        search.setCustomer(CUSTOMER_ID);
        List<ManualBolListItemBO> listBO = sut.findAll(search, 1L);
        Assert.assertNotNull(listBO);
    }

    private ManualBolEntity createRandomManualBol() {
        ManualBolEntity entity = new ManualBolEntity();

        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);

        CarrierEntity carrier = new CarrierEntity();
        carrier.setId(12L);

        entity.setOrganization(customer);
        entity.setCarrier(carrier);

        entity.addAddress(createManualBolAddress(entity, PointType.ORIGIN));
        entity.addAddress(createManualBolAddress(entity, PointType.DESTINATION));

        BillToEntity billTo = new BillToEntity();
        billTo.setId(1L);
        entity.setBillTo(billTo);

        OrganizationLocationEntity location = new OrganizationLocationEntity();
        location.setId(2L);
        entity.setLocation(location);

        entity.getNumbers().setBolNumber(String.valueOf(Math.random()));
        entity.getNumbers().setPoNumber(String.valueOf(Math.random()));
        entity.getNumbers().setProNumber(String.valueOf(Math.random()));
        entity.getNumbers().setPuNumber(String.valueOf(Math.random()));
        entity.getNumbers().setRefNumber(String.valueOf(Math.random()));
        entity.getNumbers().setSoNumber(String.valueOf(Math.random()));
        entity.getNumbers().setTrailerNumber(String.valueOf(Math.random()));
        entity.getNumbers().setGlNumber(String.valueOf(Math.random()));
        entity.getNumbers().setJobNumbers(Sets.newHashSet(new ManualBolJobNumberEntity(null, String.valueOf(Math.random()))));

        entity.setPickupDate(new Date());

        Set<ManualBolMaterialEntity> materials = Sets.newHashSet(createRandomMaterials(entity));
        List<LoadDocumentEntity> documents = Lists.newArrayList(createRandomDocument(entity));

        entity.setMaterials(materials);
        entity.setDocuments(documents);

        return entity;
    }

    private ManualBolMaterialEntity createRandomMaterials(ManualBolEntity parent) {
        ManualBolMaterialEntity entity = new ManualBolMaterialEntity();
        entity.setCommodityClass(CommodityClass.CLASS_50);
        entity.setHazmat(false);

        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setId("BAG");

        entity.setPackageType(packageType);
        entity.setReferencedProductId(1L);
        entity.setProductCode("SEM-MR-SC-12-MN");
        entity.setManualBol(parent);
        entity.setWeight(new BigDecimal(1111));
        entity.setWidth(new BigDecimal(1111));

        return entity;
    }

    private LoadDocumentEntity createRandomDocument(ManualBolEntity parent) {
        LoadDocumentEntity entity = new LoadDocumentEntity();
        entity.setManualBol(parent.getId());
        LoadDocumentTypeEntity docType = new LoadDocumentTypeEntity();
        docType.setId(1L);
        docType.setDocTypeString("BOL");
        entity.setDocumentTypeEntity(docType);
        entity.setDocumentType("BOL");
        return entity;
    }

    private ManualBolAddressEntity createManualBolAddress(ManualBolEntity parent, PointType pointType) {
        ManualBolAddressEntity entity = new ManualBolAddressEntity(pointType);
        entity.setManualBol(parent);
        entity.setAddress(createRandomAddress());

        return entity;
    }

    private AddressEntity createRandomAddress() {
        AddressEntity entity = new AddressEntity();
        entity.setAddress1(String.valueOf(Math.random()));
        entity.setAddress2(String.valueOf(Math.random()));
        entity.setCity(String.valueOf(Math.random()));
        entity.setZip(StringUtils.left(String.valueOf(Math.random()), 10));

        CountryEntity country = new CountryEntity();
        country.setId("USA");
        entity.setCountry(country);

        StateEntity stateEntity = new StateEntity();
        StatePK statepk = new StatePK();
        statepk.setStateCode("FL");
        statepk.setCountryCode("USA");
        stateEntity.setStatePK(statepk);
        entity.setState(stateEntity);

        return entity;
    }
}
