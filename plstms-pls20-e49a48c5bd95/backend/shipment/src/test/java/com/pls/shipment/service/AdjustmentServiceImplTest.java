package com.pls.shipment.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.StaleObjectStateException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.FinancialAccessorialsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.FinancialAccessorialsEntity;
import com.pls.shipment.domain.FinancialReasonsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.bo.AdjustmentBO;
import com.pls.shipment.domain.bo.AdjustmentLoadInfoBO;
import com.pls.shipment.domain.enums.AdjustmentReason;
import com.pls.shipment.domain.enums.LoadEventType;
import com.pls.shipment.service.dictionary.FinancialReasonsDictionaryService;
import com.pls.shipment.service.impl.AdjustmentServiceImpl;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;


/**
 * Test cases for {@link AdjustmentServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AdjustmentServiceImplTest {
    private static final Long LOAD_ID = (long) (Math.random() * 100);
    private static final Long ID1 = (long) (Math.random() * 100);
    private static final Long ID2 = (long) (Math.random() * 100) + 101;
    private static final Long ID3 = (long) (Math.random() * 100) + 202;

    @Mock
    private LtlShipmentDao ltlShipmentDao;
    @Mock
    private FinancialAccessorialsDao financialAccessorialsDao;
    @Mock
    private FinancialReasonsDictionaryService frdService;
    @Mock
    private ShipmentEventDao eventDao;
    @Mock
    private BillToDao billToDao;
    @Mock
    private FinancialReasonsDictionaryService reasonsService;
    @InjectMocks
    private AdjustmentServiceImpl service;

    @Test(expected = ApplicationException.class)
    public void shouldValidateThatLoadHasBeenDelivered() throws Exception {
        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.IN_TRANSIT);
        LoadCostDetailsEntity inactiveCostDetails = getCostDetails();
        inactiveCostDetails.setStatus(Status.INACTIVE);
        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(inactiveCostDetails));
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(getCostDetails()));
        load.setCostDetails(Collections.unmodifiableSet(costDetails));
        load.setAllFinancialAccessorials(new HashSet<FinancialAccessorialsEntity>());

        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldValidateThatLoadHasBeenInvoicedAndGLDateSet() throws Exception {
        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.DELIVERED);
        LoadCostDetailsEntity inactiveCostDetails = getCostDetails();
        inactiveCostDetails.setStatus(Status.INACTIVE);
        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(inactiveCostDetails));
        LoadCostDetailsEntity activeCostDetails = getCostDetails();
        activeCostDetails.setGeneralLedgerDate(null);
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(activeCostDetails));
        load.setCostDetails(Collections.unmodifiableSet(costDetails));
        load.setAllFinancialAccessorials(new HashSet<FinancialAccessorialsEntity>());

        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldValidateThatLoadHasBeenInvoicedAndInvoiceNumberSet() throws Exception {
        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = new LoadEntity();
        load.setStatus(ShipmentStatus.DELIVERED);
        LoadCostDetailsEntity inactiveCostDetails = getCostDetails();
        inactiveCostDetails.setStatus(Status.INACTIVE);
        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(inactiveCostDetails));
        LoadCostDetailsEntity activeCostDetails = getCostDetails();
        activeCostDetails.setInvoiceNumber(null);
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(activeCostDetails));
        load.setCostDetails(Collections.unmodifiableSet(costDetails));
        load.setAllFinancialAccessorials(new HashSet<FinancialAccessorialsEntity>());

        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test
    public void shouldRemoveAdjustments() throws Exception {
        FinancialAccessorialsEntity financialAccessorial1 = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial1, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial3 = getFinancialAccessorial(ID3);
        addCostItem(financialAccessorial3, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial1);
        adjustment1.setRefType("SRA");
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial2);
        adjustment2.setRefType("SRA");
        final AdjustmentBO adjustment3 = getAdjustment(financialAccessorial3);
        adjustment3.setRefType("SRA");

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustment1);
        adjustmentsToRemove.add(adjustment3);

        financialAccessorial1.setInvoiceNumber("invoiceNumber"); // shouldn't check invoice number
        financialAccessorial1.setVersion(financialAccessorial1.getVersion() + 1);
        financialAccessorial3.setVersion(financialAccessorial3.getVersion() + 1);

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial1);
        load.getAllFinancialAccessorials().add(financialAccessorial2);
        load.getAllFinancialAccessorials().add(financialAccessorial3);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);
        Mockito.when(financialAccessorialsDao.update(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) argument;
                return financialAccessorials.getStatus() != Status.INACTIVE
                        || !(financialAccessorials.getId().equals(adjustment1.getFinancialAccessorialsId())
                                && financialAccessorials.getVersion().equals(adjustment1.getVersion())
                                || financialAccessorials.getId().equals(adjustment2.getFinancialAccessorialsId())
                                && financialAccessorials.getVersion().equals(adjustment2.getVersion())
                                || financialAccessorials.getId().equals(adjustment3.getFinancialAccessorialsId())
                                && financialAccessorials.getVersion().equals(adjustment3.getVersion()));
            }
        }))).thenThrow(new IllegalArgumentException()); // only active adjustments can be updated and valid version must be set!

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);

        Mockito.verify(financialAccessorialsDao).update(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) argument;
                return financialAccessorials.getStatus() == Status.INACTIVE
                        && financialAccessorials.getId().equals(adjustment1.getFinancialAccessorialsId());
            }
        }));
        Mockito.verify(financialAccessorialsDao).update(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) argument;
                return financialAccessorials.getStatus() == Status.INACTIVE
                        && financialAccessorials.getId().equals(adjustment2.getFinancialAccessorialsId());
            }
        }));
        Mockito.verify(financialAccessorialsDao).update(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) argument;
                return financialAccessorials.getStatus() == Status.INACTIVE
                        && financialAccessorials.getId().equals(adjustment3.getFinancialAccessorialsId());
            }
        }));
        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                FinancialAccessorialsEntity financialAccessorials = (FinancialAccessorialsEntity) argument;
                return financialAccessorials.getStatus() == Status.ACTIVE && financialAccessorials.getId() == null
                        && financialAccessorials.getRevision() == 1;
            }
        }));
        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
        validateCostItems(load.getActiveCostDetail().getCostDetailItems(), load.getCarrier().getId());
    }

    @Test(expected = StaleObjectStateException.class)
    public void shouldFailRemovingAdjustmentsUpdatedByAnotherUserCheckFinancialStatus() throws Exception {
        FinancialAccessorialsEntity financialAccessorial1 = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial1, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial3 = getFinancialAccessorial(ID3);
        addCostItem(financialAccessorial3, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial1);
        adjustment1.setRefType("SRA");
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial2);
        adjustment2.setRefType("SRA");
        final AdjustmentBO adjustment3 = getAdjustment(financialAccessorial3);
        adjustment3.setRefType("SRA");

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustment1);
        adjustmentsToRemove.add(adjustment3);

        financialAccessorial3.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        financialAccessorial3.setVersion(financialAccessorial3.getVersion() + 1);

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial1);
        load.getAllFinancialAccessorials().add(financialAccessorial2);
        load.getAllFinancialAccessorials().add(financialAccessorial3);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = StaleObjectStateException.class)
    public void shouldFailRemovingAdjustmentsUpdatedByAnotherUserCheckGLDate() throws Exception {
        FinancialAccessorialsEntity financialAccessorial1 = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial1, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial3 = getFinancialAccessorial(ID3);
        addCostItem(financialAccessorial3, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial1);
        adjustment1.setRefType("SRA");
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial2);
        adjustment2.setRefType("SRA");
        final AdjustmentBO adjustment3 = getAdjustment(financialAccessorial3);
        adjustment3.setRefType("SRA");

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustment1);
        adjustmentsToRemove.add(adjustment3);

        financialAccessorial3.setGeneralLedgerDate(new Date());
        financialAccessorial3.setVersion(financialAccessorial3.getVersion() + 1);

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial1);
        load.getAllFinancialAccessorials().add(financialAccessorial2);
        load.getAllFinancialAccessorials().add(financialAccessorial3);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldFailRemovingInvoicedAdjustmentsCheckFinancialStatus() throws Exception {
        FinancialAccessorialsEntity financialAccessorial1 = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial1, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial3 = getFinancialAccessorial(ID3);
        addCostItem(financialAccessorial3, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial1);
        adjustment1.setRefType("SRA");
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial2);
        adjustment2.setRefType("SRA");
        final AdjustmentBO adjustment3 = getAdjustment(financialAccessorial3);
        adjustment3.setRefType("SRA");

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustment1);
        adjustmentsToRemove.add(adjustment3);

        financialAccessorial3.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial1);
        load.getAllFinancialAccessorials().add(financialAccessorial2);
        load.getAllFinancialAccessorials().add(financialAccessorial3);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldFailRemovingInvoicedAdjustmentsCheckGLDate() throws Exception {
        FinancialAccessorialsEntity financialAccessorial1 = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial1, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        FinancialAccessorialsEntity financialAccessorial3 = getFinancialAccessorial(ID3);
        addCostItem(financialAccessorial3, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial1);
        adjustment1.setRefType("SRA");
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial2);
        adjustment2.setRefType("SRA");
        final AdjustmentBO adjustment3 = getAdjustment(financialAccessorial3);
        adjustment3.setRefType("SRA");

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustment1);
        adjustmentsToRemove.add(adjustment3);

        financialAccessorial3.setGeneralLedgerDate(new Date());

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial1);
        load.getAllFinancialAccessorials().add(financialAccessorial2);
        load.getAllFinancialAccessorials().add(financialAccessorial3);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test
    public void shouldNotUpdateUnchangedAdjustment() throws Exception {
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        CostDetailItemEntity item1 = addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        CostDetailItemEntity item2 = addCostItem(financialAccessorial, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        CostDetailItemEntity item3 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        CostDetailItemEntity item4 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial.getCostDetailItems()));

        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(item1.getSubtotal());
        adjustment1.setCost(item2.getSubtotal());
        adjustment1.setReason(ID3);
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(item3.getSubtotal());
        adjustment2.setCost(item4.getSubtotal());
        adjustment2.setReason(ID2);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial));
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);

        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
    }

    @Test(expected = StaleObjectStateException.class)
    public void shouldFailUpdateAdjustmentChangedByAnotherUser() throws Exception {
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        CostDetailItemEntity item1 = addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        CostDetailItemEntity item2 = addCostItem(financialAccessorial, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        CostDetailItemEntity item3 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        CostDetailItemEntity item4 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial.getCostDetailItems()));

        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(item1.getSubtotal());
        adjustment1.setCost(item2.getSubtotal());
        adjustment1.setReason(ID3);
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(item3.getSubtotal());
        adjustment2.setCost(item4.getSubtotal());
        adjustment2.setReason(ID2);
        adjustment2.setVersion(adjustment2.getVersion() + 1);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial));
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldFailOnMissingAdjustmentIfFinancialStatusIsNotSet() throws Exception {
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        CostDetailItemEntity item1 = addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        CostDetailItemEntity item2 = addCostItem(financialAccessorial, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        CostDetailItemEntity item3 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        CostDetailItemEntity item4 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial.getCostDetailItems()));
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        financialAccessorial2.setGeneralLedgerDate(new Date());
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        addCostItem(financialAccessorial2, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial2.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial2.getCostDetailItems()));

        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(item1.getSubtotal());
        adjustment1.setCost(item2.getSubtotal());
        adjustment1.setReason(ID3);
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(item3.getSubtotal());
        adjustment2.setCost(item4.getSubtotal());
        adjustment2.setReason(ID2);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial));
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial2));
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldFailOnMissingAdjustmentIfGLDateIsNotSet() throws Exception {
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        CostDetailItemEntity item1 = addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        CostDetailItemEntity item2 = addCostItem(financialAccessorial, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        CostDetailItemEntity item3 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        CostDetailItemEntity item4 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial.getCostDetailItems()));
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        financialAccessorial2.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        addCostItem(financialAccessorial2, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial2.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial2.getCostDetailItems()));

        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(item1.getSubtotal());
        adjustment1.setCost(item2.getSubtotal());
        adjustment1.setReason(ID3);
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(item3.getSubtotal());
        adjustment2.setCost(item4.getSubtotal());
        adjustment2.setReason(ID2);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial));
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial2));
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);
    }

    @Test
    public void shouldIgnoreInvoicedAdjustments() throws Exception {
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        CostDetailItemEntity item1 = addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        CostDetailItemEntity item2 = addCostItem(financialAccessorial, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        CostDetailItemEntity item3 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        CostDetailItemEntity item4 = addCostItem(financialAccessorial, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial.getCostDetailItems()));
        FinancialAccessorialsEntity financialAccessorial2 = getFinancialAccessorial(ID2);
        financialAccessorial2.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        financialAccessorial2.setGeneralLedgerDate(new Date());
        addCostItem(financialAccessorial2, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        addCostItem(financialAccessorial2, "CRA", new BigDecimal(Math.random()), CostDetailOwner.C, ID2, ID3);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.S, ID3, ID2);
        addCostItem(financialAccessorial2, "FS", new BigDecimal(Math.random()), CostDetailOwner.C, ID3, ID2);
        financialAccessorial2.setCostDetailItems(Collections.unmodifiableSet(financialAccessorial2.getCostDetailItems()));

        final AdjustmentBO adjustment1 = getAdjustment(financialAccessorial);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(item1.getSubtotal());
        adjustment1.setCost(item2.getSubtotal());
        adjustment1.setReason(ID3);
        final AdjustmentBO adjustment2 = getAdjustment(financialAccessorial);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(item3.getSubtotal());
        adjustment2.setCost(item4.getSubtotal());
        adjustment2.setReason(ID2);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial));
        load.getAllFinancialAccessorials().add((FinancialAccessorialsEntity) createUnmodifiableObject(financialAccessorial2));
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);

        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
    }

    @Test
    public void shouldCreateValidAdjustments() throws Exception {
        AdjustmentBO adjustment = new AdjustmentBO();
        adjustment.setFinancialAccessorialsId(ID1);
        adjustment.setVersion((int) (Math.random() * 100));
        adjustment.setRefType("SRA");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(new BigDecimal(Math.random()));
        adjustment.setReason(ID3);
        final AdjustmentBO adjustment1 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        adjustment = new AdjustmentBO();
        adjustment.setFinancialAccessorialsId(ID1);
        adjustment.setVersion((int) (Math.random() * 100));
        adjustment.setRefType("FS");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(new BigDecimal(Math.random()));
        adjustment.setReason(ID2);
        final AdjustmentBO adjustment2 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        final LoadEntity load = getLoad();
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        Mockito.when(financialAccessorialsDao.saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return !isValidEntity((FinancialAccessorialsEntity) argument, load, null, null, adjustment1, adjustment2);
            }
        }))).thenThrow(new IllegalArgumentException());

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);

        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito
                .argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                        return isValidEntity((FinancialAccessorialsEntity) argument, load, null, null, adjustment1, adjustment2);
            }
        }));
        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
        validateCostItems(load.getActiveCostDetail().getCostDetailItems(),
                load.getCarrier().getId());
    }

    @Test
    public void shouldCreateRebillShipperAdjustment() throws Exception {
        AdjustmentBO adjustment1 = new AdjustmentBO();
        adjustment1.setFinancialAccessorialsId(-1L);
        adjustment1.setRefType("SRA");
        adjustment1.setRevenue(new BigDecimal(Math.random()));
        adjustment1.setCost(BigDecimal.ZERO);
        adjustment1.setReason(Long.valueOf(AdjustmentReason.REBILL_SHIPPER.getReason()));

        final AdjustmentBO adjustment2 = new AdjustmentBO();
        adjustment2.setFinancialAccessorialsId(-1L);
        adjustment2.setRefType("FS");
        adjustment2.setRevenue(new BigDecimal(Math.random()));
        adjustment2.setCost(BigDecimal.ZERO);
        adjustment2.setReason(Long.valueOf(AdjustmentReason.REBILL_SHIPPER.getReason()));

        AdjustmentBO adjustment = new AdjustmentBO();
        adjustment.setRefType("SRA");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(BigDecimal.ZERO);
        adjustment.setReason(Long.valueOf(AdjustmentReason.REBILL_SHIPPER.getReason()));
        final AdjustmentBO adjustment3 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        adjustment = new AdjustmentBO();
        adjustment.setRefType("FS");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(BigDecimal.ZERO);
        adjustment.setReason(Long.valueOf(AdjustmentReason.REBILL_SHIPPER.getReason()));
        final AdjustmentBO adjustment4 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        adjustmentsToSave.add(adjustment3);
        adjustmentsToSave.add(adjustment4);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        final LoadEntity load = getLoadForRebill();
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        final long oldBillToId = load.getBillTo().getId();
        final long newBillToId = (long) (Math.random() * 100) + 101;

        BillToEntity billTo = new BillToEntity();
        billTo.setId(newBillToId);
        Mockito.when(billToDao.find(newBillToId)).thenReturn(billTo);
        final String oldBolNumber = load.getNumbers().getBolNumber();
        final String bolNumber = "bolNumber" + Math.random();

        Mockito.when(financialAccessorialsDao.saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return !isValidEntity((FinancialAccessorialsEntity) argument, load, oldBillToId, oldBolNumber, adjustment1, adjustment2)
                        && !isValidEntity((FinancialAccessorialsEntity) argument, load, newBillToId, bolNumber, adjustment3, adjustment4);
            }
        }))).thenThrow(new IllegalArgumentException());

        List<LoadMaterialEntity> materials = new ArrayList<>();
        LoadMaterialEntity material = new LoadMaterialEntity();
        material.setWeight(new BigDecimal(Math.random() * 100));
        material.setQuantity(String.valueOf((int) (Math.random() * 100)));
        material.setHazmat(true);
        materials.add(material);
        AdjustmentLoadInfoBO loadInfo = new AdjustmentLoadInfoBO();
        loadInfo.setBillToId(newBillToId);
        loadInfo.setBolNumber(bolNumber);
        loadInfo.setPoNumber("poNumber" + Math.random());
        loadInfo.setRefNumber("refNumber" + Math.random());
        loadInfo.setSoNumber("soNumber" + Math.random());
        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, loadInfo, materials);

        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return isValidEntity((FinancialAccessorialsEntity) argument, load, oldBillToId, oldBolNumber, adjustment1, adjustment2)
                        && isValidRollbackInfo((FinancialAccessorialsEntity) argument);
            }

            private boolean isValidRollbackInfo(FinancialAccessorialsEntity argument) {
                return argument.getRollbackInfo().size() == 1 && argument.getAdjProductInfo().size() == 2;
            }
        }));
        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return isValidEntity((FinancialAccessorialsEntity) argument, load, newBillToId, bolNumber, adjustment3, adjustment4)
                        && CollectionUtils.isEmpty(((FinancialAccessorialsEntity) argument).getRollbackInfo())
                        && CollectionUtils.isEmpty(((FinancialAccessorialsEntity) argument).getAdjProductInfo());
            }
        }));
        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
        validateCostItems(load.getActiveCostDetail().getCostDetailItems(), load.getCarrier().getId());

        ArgumentCaptor<LoadEntity> argumentCaptor = ArgumentCaptor.forClass(LoadEntity.class);
        Mockito.verify(ltlShipmentDao).saveOrUpdate(argumentCaptor.capture());
        LoadEntity capturedArgument = argumentCaptor.getValue();
        Assert.assertEquals(loadInfo.getBolNumber(), capturedArgument.getNumbers().getBolNumber());
        Assert.assertEquals(loadInfo.getPoNumber(), capturedArgument.getNumbers().getPoNumber());
        Assert.assertEquals(loadInfo.getSoNumber(), capturedArgument.getNumbers().getSoNumber());
        Assert.assertEquals(loadInfo.getRefNumber(), capturedArgument.getNumbers().getRefNumber());
        Assert.assertEquals(loadInfo.getBillToId(), capturedArgument.getBillTo().getId());
        Assert.assertEquals(1, capturedArgument.getOrigin().getLoadMaterials().size());
        Assert.assertEquals(material, capturedArgument.getOrigin().getLoadMaterials().iterator().next());
        Assert.assertTrue(capturedArgument.getHazmat());
        Assert.assertEquals(Integer.valueOf(material.getWeight().setScale(0, RoundingMode.CEILING).intValue()), capturedArgument.getWeight());
        Assert.assertEquals(Integer.valueOf(Integer.parseInt(material.getQuantity())), capturedArgument.getPieces());
    }

    @Test
    public void shouldSetValidRevisionNumber() throws Exception {
        AdjustmentBO adjustment = new AdjustmentBO();
        adjustment.setFinancialAccessorialsId(ID1);
        adjustment.setVersion((int) (Math.random() * 100));
        adjustment.setRefType("SRA");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(new BigDecimal(Math.random()).multiply(new BigDecimal(-1)));
        adjustment.setReason(ID3);
        final AdjustmentBO adjustment1 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        // second adjustment (with different Not Invoice Flag)
        adjustment = new AdjustmentBO();
        adjustment.setFinancialAccessorialsId(ID1);
        adjustment.setVersion((int) (Math.random() * 100));
        adjustment.setRefType("DS");
        adjustment.setRevenue(new BigDecimal(Math.random()));
        adjustment.setCost(new BigDecimal(Math.random()));
        adjustment.setReason(ID3);
        adjustment.setNotInvoice(true);
        final AdjustmentBO adjustment2 = (AdjustmentBO) createUnmodifiableObject(adjustment);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustment1);
        adjustmentsToSave.add(adjustment2);
        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();

        final LoadEntity load = getLoad();
        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID2);
        financialAccessorial.setGeneralLedgerDate(new Date());
        financialAccessorial.setInvoiceNumber("invoiceNumer");
        financialAccessorial.setFinancialStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        financialAccessorial.setRevision(9);
        load.getAllFinancialAccessorials().add(financialAccessorial);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, adjustmentsToRemove, null, null);

        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return ((FinancialAccessorialsEntity) argument).getRevision() == 10;
            }
        }));
        Mockito.verify(financialAccessorialsDao).saveOrUpdate(Mockito.argThat(new ArgumentMatcher<FinancialAccessorialsEntity>() {
            @Override
            public boolean matches(Object argument) {
                return ((FinancialAccessorialsEntity) argument).getRevision() == 11;
            }
        }));
        Mockito.verifyNoMoreInteractions(financialAccessorialsDao);
        validateCostItems(load.getActiveCostDetail().getCostDetailItems(), load.getCarrier().getId());
    }

    @Test
    public void shouldSaveTrackingForAdjustment() throws Exception {
        AdjustmentBO adjustmentSave = new AdjustmentBO();
        adjustmentSave.setFinancialAccessorialsId(ID1);
        adjustmentSave.setVersion((int) (Math.random() * 100));
        adjustmentSave.setRefType("SRA");
        adjustmentSave.setRevenue(new BigDecimal(Math.random()));
        adjustmentSave.setCost(new BigDecimal(Math.random()).multiply(new BigDecimal(-1)));
        adjustmentSave.setReason(ID3);

        List<AdjustmentBO> adjustmentsToSave = new ArrayList<AdjustmentBO>();
        adjustmentsToSave.add(adjustmentSave);

        final LoadEntity load = getLoad();
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        String reason = "reason" + Math.random();
        FinancialReasonsEntity financialReasonsEntity = new FinancialReasonsEntity();
        financialReasonsEntity.setId(ID3);
        financialReasonsEntity.setDescription(reason);
        Mockito.when(reasonsService.getFinancialReasonsForAdjustments()).thenReturn(Arrays.asList(financialReasonsEntity));

        service.saveAdjustments(LOAD_ID, adjustmentsToSave, new ArrayList<AdjustmentBO>(), null, null);

        ArgumentCaptor<LoadEventEntity> argumentCaptor = ArgumentCaptor.forClass(LoadEventEntity.class);
        Mockito.verify(eventDao).persist(argumentCaptor.capture());
        LoadEventEntity capturedArgument = argumentCaptor.getValue();
        Assert.assertSame(LoadEventType.SAVED.name(), capturedArgument.getEventTypeCode());
        Assert.assertEquals(1, capturedArgument.getData().size());
        Assert.assertEquals(reason, capturedArgument.getData().get(0).getData());
    };

    @Test
    public void shouldAdjustmentDeletedTracking() throws Exception {

        FinancialAccessorialsEntity financialAccessorial = getFinancialAccessorial(ID1);
        addCostItem(financialAccessorial, "SRA", new BigDecimal(Math.random()), CostDetailOwner.S, ID2, ID3);
        final AdjustmentBO adjustmentRemove = getAdjustment(financialAccessorial);
        adjustmentRemove.setRefType("SRA");
        adjustmentRemove.setReason(null);

        List<AdjustmentBO> adjustmentsToRemove = new ArrayList<AdjustmentBO>();
        adjustmentsToRemove.add(adjustmentRemove);

        final LoadEntity load = getLoad();
        load.getAllFinancialAccessorials().add(financialAccessorial);
        Mockito.when(ltlShipmentDao.find(LOAD_ID)).thenReturn(load);

        String reason = "reason" + Math.random();
        FinancialReasonsEntity financialReasonsEntity = new FinancialReasonsEntity();
        financialReasonsEntity.setId(ID3);
        financialReasonsEntity.setDescription(reason);
        Mockito.when(reasonsService.getFinancialReasonsForAdjustments()).thenReturn(Arrays.asList(financialReasonsEntity));

        service.saveAdjustments(LOAD_ID, new ArrayList<AdjustmentBO>(), adjustmentsToRemove, null, null);

        ArgumentCaptor<LoadEventEntity> argumentCaptor = ArgumentCaptor.forClass(LoadEventEntity.class);
        Mockito.verify(eventDao).persist(argumentCaptor.capture());
        LoadEventEntity capturedArgument = argumentCaptor.getValue();
        Assert.assertSame(LoadEventType.DELETED.name(), capturedArgument.getEventTypeCode());
        Assert.assertEquals(1, capturedArgument.getData().size());
        Assert.assertEquals(reason, capturedArgument.getData().get(0).getData());
    };

    private void validateCostItems(Set<CostDetailItemEntity> costDetailItems, Long carrierId) {
        for (CostDetailItemEntity costItem : costDetailItems) {
            Assert.assertSame(costItem.getSubtotal(), costItem.getUnitCost());
            Assert.assertSame(carrierId, costItem.getCarrierId());
        }
    }

    private boolean isValidEntity(FinancialAccessorialsEntity argument, LoadEntity load, Long billToId, String bol, AdjustmentBO... adjustments) {
        boolean loadValid = argument.getLoad() == load;
        boolean financialStatusValid = argument.getFinancialStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL;
        boolean doNotInvoiceValid = argument.getShortPay() == adjustments[0].isNotInvoice();
        boolean costItemsSizeValid = argument.getCostDetailItems().size() == adjustments.length * 2;
        boolean bolValid = ObjectUtils.equals(argument.getBol(), bol == null ? load.getNumbers().getBolNumber() : bol);
        if (!loadValid || !financialStatusValid || !doNotInvoiceValid || !costItemsSizeValid || argument.getRevision() < 1 || !bolValid) {
            return false;
        }
        List<CostDetailItemEntity> costDetailItems = new ArrayList<CostDetailItemEntity>(argument.getCostDetailItems());
        Long pBillToId = billToId == null ? load.getBillToId() : billToId;
        for (int i = 0; i < adjustments.length; i++) {
            if (!isValidCostItem(costDetailItems.get(i * 2), adjustments[i], argument, load.getActiveCostDetail(), pBillToId)
                    || !isValidCostItem(costDetailItems.get(i * 2 + 1), adjustments[i], argument, load.getActiveCostDetail(), pBillToId)) {
                return false;
            }
        }
        return isValidTotal(argument, adjustments);
    }

    private boolean isValidTotal(FinancialAccessorialsEntity argument, AdjustmentBO[] adjustments) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (AdjustmentBO adjustment : adjustments) {
            totalCost = totalCost.add(adjustment.getCost());
            totalRevenue = totalRevenue.add(adjustment.getRevenue());
        }
        return totalCost.equals(argument.getTotalCost()) && totalRevenue.equals(argument.getTotalRevenue());
    }

    private boolean isValidCostItem(CostDetailItemEntity costItem, AdjustmentBO adjustmentBO, FinancialAccessorialsEntity argument,
            LoadCostDetailsEntity activeCostDetail, long billToId) {
        if (costItem.getCostDetails() != activeCostDetail || costItem.getFinancialAccessorials() != argument
                || costItem.getBillTo().getId() != billToId
                || costItem.getReason().getId() != adjustmentBO.getReason()) {
            return false;
        }
        if (costItem.getOwner() == CostDetailOwner.C) {
            return costItem.getSubtotal().equals(adjustmentBO.getCost())
                    && (costItem.getAccessorialType().equals(adjustmentBO.getRefType()) || ("CRA".equals(costItem.getAccessorialType()) && "SRA"
                            .equals(adjustmentBO.getRefType())));
        }
        if (costItem.getOwner() == CostDetailOwner.S) {
            return costItem.getSubtotal().equals(adjustmentBO.getRevenue())
                    && (costItem.getAccessorialType().equals(adjustmentBO.getRefType()) || ("SRA".equals(costItem.getAccessorialType()) && "CRA"
                            .equals(adjustmentBO.getRefType())));
        }
        return false;
    }

    private Object createUnmodifiableObject(Object object) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(object.getClass());
        factory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method method) {
                return true;
            }
        });

        return factory.create(new Class<?>[0], new Object[0], new UnmodifiableWrapper(object));
    }

    private CostDetailItemEntity addCostItem(FinancialAccessorialsEntity financialAccessorial, String accessorialType, BigDecimal subtotal,
            CostDetailOwner owner, Long billToId, Long reasonId) throws Exception {
        CostDetailItemEntity item = new CostDetailItemEntity();
        item.setAccessorialType(accessorialType);
        item.setSubtotal(subtotal);
        item.setOwner(owner);
        item.setBillTo(new BillToEntity());
        item.getBillTo().setId(billToId);
        item.setReason(new FinancialReasonsEntity());
        item.getReason().setId(reasonId);
        financialAccessorial.getCostDetailItems().add(item);
        return item;
    }

    private AdjustmentBO getAdjustment(FinancialAccessorialsEntity financialAccessorial) {
        AdjustmentBO adjustment = new AdjustmentBO();
        adjustment.setFinancialAccessorialsId(financialAccessorial.getId());
        adjustment.setVersion(financialAccessorial.getVersion());
        adjustment.setReason(new Long(3));
        return adjustment;
    }

    private FinancialAccessorialsEntity getFinancialAccessorial(long id) {
        FinancialAccessorialsEntity financialAccessorial = new FinancialAccessorialsEntity();
        financialAccessorial.setId(id);
        financialAccessorial.setVersion((int) (Math.random() * 100));
        return financialAccessorial;
    }


    private LoadEntity getLoad() throws Exception {
        LoadEntity load = getModifiableLoad();
        return (LoadEntity) createUnmodifiableObject(load);
    }

    private LoadEntity getLoadForRebill() throws Exception {
        LoadEntity load = getModifiableLoad();
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        load.addLoadDetails(origin);
        origin.setLoadMaterials(new HashSet<>());
        origin.getLoadMaterials().add(new LoadMaterialEntity());
        origin.getLoadMaterials().add(new LoadMaterialEntity());
        return load;
    }

    private LoadEntity getModifiableLoad() throws Exception {
        LoadEntity load = new LoadEntity();
        CarrierEntity carrier = new CarrierEntity();
        carrier.setId((long) (Math.random() * 100));
        load.setCarrier(carrier);
        load.setStatus(ShipmentStatus.DELIVERED);
        load.getNumbers().setBolNumber(Math.random() + "bolNumber");
        BillToEntity billTo = new BillToEntity();
        billTo.setId((long) (Math.random() * 100));
        load.setBillTo(billTo);
        LoadCostDetailsEntity inactiveCostDetails = getCostDetails();
        inactiveCostDetails.setStatus(Status.INACTIVE);
        Set<LoadCostDetailsEntity> costDetails = new HashSet<LoadCostDetailsEntity>();
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(inactiveCostDetails));
        LoadCostDetailsEntity activeCostDetails = getCostDetails();
        activeCostDetails.setLoad(load);
        costDetails.add((LoadCostDetailsEntity) createUnmodifiableObject(activeCostDetails));
        load.setCostDetails(Collections.unmodifiableSet(costDetails));
        load.setAllFinancialAccessorials(new HashSet<FinancialAccessorialsEntity>());
        load.setFinalizationStatus(ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE);
        return load;
    }

    private LoadCostDetailsEntity getCostDetails() {
        LoadCostDetailsEntity costDetail = new LoadCostDetailsEntity();
        costDetail.setStatus(Status.ACTIVE);
        costDetail.setGeneralLedgerDate(new Date());
        costDetail.setInvoiceNumber("invoiceNumber" + Math.random());
        return costDetail;
    }

    private class UnmodifiableWrapper implements MethodHandler {
        private Object target;

        /**
         * Constructor.
         * 
         * @param target
         */
        UnmodifiableWrapper(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            if (thisMethod.getName().startsWith("set")) {
                throw new UnsupportedOperationException("Setter method " + thisMethod.getName() + " shoudn't be called on "
                        + target.getClass().getName());
            }
            return thisMethod.invoke(target, args);
        }
    }
}
