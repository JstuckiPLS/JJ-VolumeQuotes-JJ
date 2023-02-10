package com.pls.quote.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CarrierDao;
import com.pls.core.dao.CustomerDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.quote.dao.SavedQuoteDao;
import com.pls.shipment.domain.LtlProductHazmatInfo;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.SavedQuoteAccessorialEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;
import com.pls.shipment.domain.bo.SavedQuoteBO;

/**
 * Test cases for {@link SavedQuoteDaoImpl} class.
 *
 * @author Mikhail Boldinov, 27/03/13
 */
public class SavedQuoteDaoImplIT extends AbstractDaoTest {

    private static final Long ORG_ID = 1L;

    private static final Long PERSON_ID = 1L;

    private static final Long QUOTE_ID = 3L;

    @Autowired
    private SavedQuoteDao sut;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CarrierDao carrierDao;

    @SuppressWarnings("deprecation")
    @Test
    public void testFindSavedQuotes() throws ApplicationException {
        //get the saved quotes for org with id 1 between date range Jan 7 2023 - Dec 29 2023
        List<SavedQuoteBO> savedQuotes = sut.findSavedQuotes(ORG_ID, PERSON_ID, new Date(123, 0, 7), new Date(123, 11, 29));
        Assert.assertNotNull(savedQuotes);
        Assert.assertFalse(savedQuotes.isEmpty());
        Assert.assertEquals(savedQuotes.size(), 19);
    }

    @Test
    public void testUpdateSavedQuotesStatus() throws EntityNotFoundException {
        SavedQuoteEntity savedQuote = sut.find(QUOTE_ID);
        Assert.assertNotNull(savedQuote);
        Assert.assertEquals(Status.ACTIVE, savedQuote.getStatus());
        sut.updateStatus(QUOTE_ID, Status.INACTIVE);
        flushAndClearSession();

        savedQuote = sut.find(QUOTE_ID);
        Assert.assertNotNull(savedQuote);
        Assert.assertEquals(Status.INACTIVE, savedQuote.getStatus());
    }

    @Test
    public void testSaveQuote() {
        SavedQuoteEntity entity = buildSavedQuote();
        Long entityId = entity.getId();
        Assert.assertNull(entityId);
        sut.saveOrUpdate(entity);
        flushAndClearSession();
        entityId = entity.getId();
        Assert.assertNotNull(entityId);
        entity = sut.find(entityId);
        Assert.assertNotNull(entity);
    }

    private SavedQuoteEntity buildSavedQuote() {
        SavedQuoteEntity entity = new SavedQuoteEntity();
        entity.setCustomer(customerDao.find(1L));
        entity.setCarrier(carrierDao.find());
        entity.setQuoteReferenceNumber("12345");
        entity.setSpecialMessage("Special Message");
        entity.setPoNum("159753");
        entity.setBol("423432432");
        entity.setSoNumber("34234242");
        entity.setGlNumber("5675463");
        entity.setTrailer("34564565464");
        entity.setPickupNum("258456");
        entity.setStatus(Status.ACTIVE);
        entity.setCarrierReferenceNumber("11111111");
        entity.setPickupDate(new Date());

        RouteEntity route = new RouteEntity();
        route.setCreatedBy(2L);
        route.setCreatedDate(new Date());
        route.setOriginZip("12345");
        route.setOriginCountry("USA");
        route.setOriginState("NY");
        route.setOriginCity("SCHENECTADY");
        route.setDestZip("43210");
        route.setDestCountry("USA");
        route.setDestState("OH");
        route.setDestCity("COLUMBUS");
        entity.setRoute(route);

        SavedQuoteCostDetailsEntity costDetailsEntity = new SavedQuoteCostDetailsEntity();
        costDetailsEntity.setQuote(entity);
        costDetailsEntity.setServiceType(LtlServiceType.DIRECT);
        costDetailsEntity.setTotalCost(BigDecimal.valueOf(123.45));
        costDetailsEntity.setEstimatedTransitTime(10L);
        costDetailsEntity.setEstTransitDate(new Date());
        costDetailsEntity.setNewLiability(BigDecimal.valueOf(10.5));
        costDetailsEntity.setUsedLiability(BigDecimal.valueOf(50.1));
        entity.setCostDetails(costDetailsEntity);

        Set<SavedQuoteMaterialEntity> materials = new HashSet<SavedQuoteMaterialEntity>();
        SavedQuoteMaterialEntity material = new SavedQuoteMaterialEntity();
        material.setQuote(entity);
        material.setWeight(BigDecimal.valueOf(12));
        material.setLength(BigDecimal.valueOf(10));
        material.setWidth(BigDecimal.valueOf(8));
        material.setHeight(BigDecimal.valueOf(6));
        material.setProductCode("ABCD");
        material.setProductDescription("abcd");
        material.setCommodityClass(CommodityClass.CLASS_50);
        material.setNmfc("nmfc_50");
        material.setHazmat(true);
        LtlProductHazmatInfo hazmatInfo = new LtlProductHazmatInfo();
        hazmatInfo.setHazmatClass("1");
        hazmatInfo.setPackingGroup("PG");
        hazmatInfo.setUnNumber("101010");
        hazmatInfo.setInstructions("EmergencyResponseInstructions");
        hazmatInfo.setEmergencyCompany("EmergencyResponseCompany");
        hazmatInfo.setEmergencyContract("999999");
        PhoneEmbeddableObject emergencyPhone = new PhoneEmbeddableObject();
        emergencyPhone.setCountryCode("1");
        emergencyPhone.setAreaCode("123");
        emergencyPhone.setNumber("1234567");
        hazmatInfo.setEmergencyPhone(emergencyPhone);
        material.setHazmatInfo(hazmatInfo);
        PackageTypeEntity packageType = new PackageTypeEntity();
        packageType.setId("ENV");
        packageType.setDescription("Envelopes");
        material.setPackageType(packageType);
        material.setPieces(5L);
        material.setStackable(true);
        materials.add(material);
        entity.setMaterials(materials);

        Set<SavedQuoteAccessorialEntity> accessorials = new HashSet<SavedQuoteAccessorialEntity>();
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.INSIDE_DELIVERY));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.LIFT_GATE_DELIVERY));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.RESIDENTIAL_DELIVERY));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.SORT_SEGREGATE_DELIVERY));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.NOTIFY_DELIVERY));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.INSIDE_PICKUP));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.LIFT_GATE_PICKUP));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.RESIDENTIAL_PICKUP));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.OVER_DIMENSION));
        accessorials.add(buildAccessorialEntity(entity, LtlAccessorialType.BLIND_BOL));

        entity.setAccessorials(accessorials);

        return entity;
    }

    private SavedQuoteAccessorialEntity buildAccessorialEntity(SavedQuoteEntity entity, LtlAccessorialType accessorialType) {
        SavedQuoteAccessorialEntity accessorial = new SavedQuoteAccessorialEntity();
        accessorial.setSavedQuote(entity);
        accessorial.setAccessorialType(new AccessorialTypeEntity(accessorialType.getCode()));
        return accessorial;
    }
}
