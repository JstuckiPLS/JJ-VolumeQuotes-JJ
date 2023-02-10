package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.pls.core.domain.LostSavOppRptDataEntity;
import com.pls.core.domain.bo.LostSavingsMaterialsBO;
import com.pls.core.domain.bo.LostSavingsReportBO;
import com.pls.core.domain.bo.ReportsBO;
import com.pls.shipment.dao.ShipmentReportDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.AccessorialReportBO;
import com.pls.shipment.domain.bo.AuditReasonReportBO;
import com.pls.shipment.domain.bo.CreationReportBO;
import com.pls.shipment.domain.bo.FreightAnalysisReportBO;
import com.pls.shipment.domain.bo.ProductReportBO;
import com.pls.shipment.domain.enums.DateTypeOption;

/**
 * Implementation of {@link ShipmentReportDao}.
 * 
 * @author Brichak Aleksandr
 */
@Repository
@Transactional
public class ShipmentReportDaoImpl implements ShipmentReportDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    @Override
    public List<FreightAnalysisReportBO> getCarrierActivityReport(Long carrierId, Long customerId, Date startDate, Date endDate, DateTypeOption dateType) {
        List<FreightAnalysisReportBO> reports = executeCarrierActivityReport(carrierId, customerId, startDate, endDate, dateType);
        
        List<AuditReasonReportBO> reasons = getReasonsForCarrierActivityReport(customerId, null, carrierId, startDate, endDate);

        List<ProductReportBO> allProducts = getAllProductsForActivityReport(customerId, null, carrierId, startDate, endDate);

        List<AccessorialReportBO> allAcc = getAllAccessorialsForActivityReport(customerId, null, carrierId, startDate, endDate);

        for (FreightAnalysisReportBO report : reports) {
            report.setAuditReasons(reasons.stream().filter(it->it.getLoadId().equals(report.getLoadId())).collect(Collectors.toList()));
            report.setProducts(getProductsByLoad(report.getLoadId(), allProducts));
            report.setAccessorials(getAccessorialsByLoad(report.getLoadId(), allAcc));
        }
        return reports;
    }

    @Override
    public List<FreightAnalysisReportBO> getActivityReport(Long customerId, Long networkId, Date startDate, Date endDate, DateTypeOption dateType) {
        return getFreightAnalysisReports(customerId, networkId, startDate, endDate, false, dateType);
    }

    @Override
    public List<FreightAnalysisReportBO> getSavingsReport(Long customerId, Long networkId, Date startDate, Date endDate) {
        return getFreightAnalysisReports(customerId, networkId, startDate, endDate, true, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CreationReportBO> getCreationReport(Long customerId, Long networkId, Date startDate, Date endDate,
            boolean invoicedShipmentsOnly) {
        return getCurrentSession().getNamedQuery(LoadEntity.GET_CREATION_REPORT_DATA)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("invoicedOnly", invoicedShipmentsOnly)
                .setResultTransformer(new AliasToBeanResultTransformer(CreationReportBO.class)).list();
    }

    private List<FreightAnalysisReportBO> getFreightAnalysisReports(Long customerId, Long networkId, Date startDate, Date endDate,
            boolean isSavingsReport, DateTypeOption dateType) {

        List<FreightAnalysisReportBO> reports = getReports(customerId, networkId, startDate, endDate, isSavingsReport, dateType);

        List<ProductReportBO> allProducts = isSavingsReport
                ? getAllProductsForSavingsReport(customerId, networkId, null, startDate, endDate)
                : getAllProductsForActivityReport(customerId, networkId, null, startDate, endDate);

        List<AccessorialReportBO> allAcc = isSavingsReport
                ? getAllAccessorialsForSavingsReport(customerId, networkId, null, startDate, endDate)
                : getAllAccessorialsForActivityReport(customerId, networkId, null, startDate, endDate);

        for (FreightAnalysisReportBO report : reports) {
            report.setProducts(getProductsByLoad(report.getLoadId(), allProducts));
            report.setAccessorials(getAccessorialsByLoad(report.getLoadId(), allAcc));
        }
        return reports;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReportsBO> getUnbilledReport(Long customerId, String companyCode, Date endDate) {
        return getCurrentSession().getNamedQuery(LoadEntity.Q_UNBILLED_REPORT)
                .setParameter("customerId", customerId, LongType.INSTANCE)
                .setParameter("companyCode", companyCode, StringType.INSTANCE)
                .setParameter("endDate", endDate, DateType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ReportsBO.class))
                .list();
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public List<LostSavingsReportBO> getLostSavingsReport(Long customerId, Long networkId, Date startDate, Date endDate,
            String sortOrder) throws ParseException {

        Long jobId = (Long) getCurrentSession().getNamedQuery(LostSavOppRptDataEntity.Q_PREPARE_DATA)
                .setParameter("customerId", customerId, LongType.INSTANCE)
                .setParameter("networkId", networkId, LongType.INSTANCE)
                .setParameter("startDate", startDate, DateType.INSTANCE)
                .setParameter("endDate", endDate, DateType.INSTANCE)
                .uniqueResult();

        List<Object[]> list = getCurrentSession().getNamedQuery(LostSavOppRptDataEntity.GET_LOST_SAVINGS_OPP_REPORT_QUERY)
                .setParameter("sortOrder", sortOrder)
                .setParameter("jobId", jobId)
                .list();
        return updateLostSavingsReportBO(list);
    }

    private List<LostSavingsReportBO> updateLostSavingsReportBO(List<Object[]> list) throws ParseException {
        List<Object[]> subList = new ArrayList<Object[]>(list);
        List<Integer> processedLoads = new ArrayList<Integer>();
        List<LostSavingsReportBO> lsrBoList = new ArrayList<LostSavingsReportBO>();
        int loadId = 0;
        for (ListIterator<Object[]> itr = list.listIterator(); itr.hasNext();) {
            Object[] result = itr.next();
            if (!processedLoads.contains(((BigInteger) result[28]).intValue())) {
                loadId = 0;
                LostSavingsReportBO lsoReportBO = new LostSavingsReportBO();
                lsoReportBO = updateLostSavRptBo(lsoReportBO, result);
                if (result[28] != null) {
                    loadId = ((BigInteger) result[28]).intValue();
                }
                Pair<Boolean, List<LostSavingsReportBO>> processedList = processMaterialsAndAcc(lsrBoList, subList, lsoReportBO, loadId);
                if (processedList.getLeft()) {
                    processedLoads.add(loadId);
                }
                lsrBoList = processedList.getRight();
            }
        }
        return lsrBoList;
    }

    private Pair<Boolean, List<LostSavingsReportBO>> processMaterialsAndAcc(List<LostSavingsReportBO> lsrBoList,
            List<Object[]> subList, LostSavingsReportBO lsoReportBO, int loadId) {

        boolean processed = false;
        List<LostSavingsMaterialsBO> lsrMaterialsList = new ArrayList<LostSavingsMaterialsBO>();
        List<String> accessorialsList = new ArrayList<String>();
        List<Long> processedMaterials = new ArrayList<Long>();
        List<Long> processedAcc = new ArrayList<Long>();
        for (ListIterator<Object[]> subListItr = subList.listIterator(); subListItr.hasNext();) {
            Object[] res = subListItr.next();
            if (res[28] == null || ((BigInteger) res[28]).intValue() != loadId) {
                continue;
            }
            if (res[30] != null && !processedMaterials.contains(((BigDecimal) res[30]).longValue())) {
                lsrMaterialsList.add(processLostSavingsMaterial(res[13], res[14], res[32]));
                processedMaterials.add(((BigDecimal) res[30]).longValue());
            }
            if (res[31] != null && !processedAcc.contains(((BigDecimal) res[31]).longValue()) && res[19] != null) {
                accessorialsList.add(res[19].toString());
                processedAcc.add(((BigDecimal) res[31]).longValue());
            }
            processed = true;
        }
        lsoReportBO.setLostSavingsMaterials(lsrMaterialsList);
        lsoReportBO.setAccessorials(accessorialsList);
        lsrBoList.add(lsoReportBO);
        return Pair.of(processed, lsrBoList);
    }

    private LostSavingsMaterialsBO processLostSavingsMaterial(Object weight, Object commodityClassType,
            Object productDesc) {
        LostSavingsMaterialsBO lostSavingsMaterial = new LostSavingsMaterialsBO();
        if (weight != null) {
            lostSavingsMaterial.setWeight(((BigDecimal) weight).doubleValue());
        }
        if (commodityClassType != null) {
            lostSavingsMaterial.setClassType(commodityClassType.toString());
        }
        if (productDesc != null) {
            lostSavingsMaterial.setProductDescription(productDesc.toString());
        }
        return lostSavingsMaterial;
    }

    private LostSavingsReportBO updateLostSavRptBo(LostSavingsReportBO lsoReportBO, Object[] result) throws ParseException {
        LostSavingsReportBO lostSavOppRptBO = updateLoadDtlsForLostSavRptBo(lsoReportBO, result);
        if (result[4] != null) {
            lostSavOppRptBO.setShipperRefNum(result[4].toString());
        }
        if (result[5] != null) {
            lostSavOppRptBO.setShipperName(result[5].toString());
        }
        if (result[20] != null) {
            lostSavOppRptBO.setCarrSelected(result[20].toString());
        }
        if (result[22] != null) {
            lostSavOppRptBO.setCarrAmt(((BigDecimal) result[22]).doubleValue());
        }
        if (result[23] != null) {
            lostSavOppRptBO.setCarrTransitTime(((Integer) result[23]).intValue());
        }
        if (result[26] != null) {
            lostSavOppRptBO.setPotentialSavings(((BigDecimal) result[26]).doubleValue());
        }
        if (result[27] != null) {
            lostSavOppRptBO.setPotSavingsPerc(((Float) result[27]));
        }
        return updateDatesForLostSavRptBo(
                updateOriginDestForLostSavRptBo(updateLeastCostCarrDtls(lostSavOppRptBO, result), result), result);
    }

    private LostSavingsReportBO updateLeastCostCarrDtls(LostSavingsReportBO lostSavOppRptBO, Object[] result) {
        if (result[21] != null) {
            lostSavOppRptBO.setLeastCostCarr(result[21].toString());
        }
        if (result[24] != null) {
            lostSavOppRptBO.setLeastCostAmt(((BigDecimal) result[24]).doubleValue());
        }
        if (result[25] != null) {
            lostSavOppRptBO.setLeastCostTransitTime(((Integer) result[25]));
        }
        return lostSavOppRptBO;
    }

    private LostSavingsReportBO updateLoadDtlsForLostSavRptBo(LostSavingsReportBO lsoReportBO, Object[] result) {
        if (result[0] != null) {
            lsoReportBO.setUserName(result[0].toString());
        }
        if (result[1] != null) {
            lsoReportBO.setBol(result[1].toString());
        }
        if (result[2] != null) {
            lsoReportBO.setPoNum(result[2].toString());
        }
        if (result[3] != null) {
            lsoReportBO.setSoNum(result[3].toString());
        }
        if (result[9] != null) {
            lsoReportBO.setConsigneeName(result[9].toString());
        }
        if (result[15] != null) {
            lsoReportBO.setTotalWeight(((BigDecimal) result[15]).doubleValue());
        }
        return lsoReportBO;
    }

    private LostSavingsReportBO updateOriginDestForLostSavRptBo(LostSavingsReportBO lsoReportBO, Object[] result) {
        if (result[6] != null) {
            lsoReportBO.setOriginState(result[6].toString());
        }
        if (result[7] != null) {
            lsoReportBO.setOriginCity(result[7].toString());
        }
        if (result[8] != null) {
            lsoReportBO.setOriginZip(result[8].toString());
        }
        if (result[10] != null) {
            lsoReportBO.setDestState(result[10].toString());
        }
        if (result[11] != null) {
            lsoReportBO.setDestCity(result[11].toString());
        }
        if (result[12] != null) {
            lsoReportBO.setDestZip(result[12].toString());
        }
        return lsoReportBO;
    }

    private LostSavingsReportBO updateDatesForLostSavRptBo(LostSavingsReportBO lsoReportBO, Object[] result) throws ParseException {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
        input.setLenient(false);
        SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        output.setLenient(false);
        if (result[16] != null) {
            lsoReportBO.setLoadCreatedDate(output.parse(output.format(input.parse(result[16].toString()))));
            lsoReportBO.setLoadCreatedDay(new SimpleDateFormat("EE", Locale.US).format(input.parse(result[16]
                    .toString())));
        }
        if (result[17] != null) {
            lsoReportBO.setEstPickupDate(output.parse(output.format(input.parse(result[17].toString()))));
        }
        if (result[18] != null) {
            lsoReportBO.setPickupDate(output.parse(output.format(input.parse(result[18].toString()))));
        }
        if (result[29] != null) {
            lsoReportBO.setDateCreated(output.parse(output.format(input.parse(result[29].toString()))));
        }
        return lsoReportBO;
    }

    @SuppressWarnings("unchecked")
    private List<AccessorialReportBO> getAllAccessorialsForActivityReport(Long customerId, Long networkId, Long carrierId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_ACCESSORIAL_FOR_ACTIVITY_REPORT)
                .setParameter("carrierId", carrierId)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(AccessorialReportBO.class)).list();
    }
    
    @SuppressWarnings("unchecked")
    private List<AccessorialReportBO> getAllAccessorialsForSavingsReport(Long customerId, Long networkId, Long carrierId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_ACCESSORIAL_FOR_SAVINGS_REPORT)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(AccessorialReportBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    private List<ProductReportBO> getAllProductsForActivityReport(Long customerId, Long networkId, Long carrierId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_PRODUCTS_FOR_ACTIVITY_REPORT)
                .setParameter("carrierId", carrierId)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductReportBO.class)).list();
    }
    
    @SuppressWarnings("unchecked")
    private List<ProductReportBO> getAllProductsForSavingsReport(Long customerId, Long networkId, Long carrierId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_PRODUCTS_FOR_SAVINGS_REPORT)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(ProductReportBO.class)).list();
    }
    
    @SuppressWarnings("unchecked")
    private List<AuditReasonReportBO> getReasonsForCarrierActivityReport(Long customerId, Long networkId, Long carrierId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_REASONS_FOR_CARRIER_ACTIVITY_REPORT)
                .setParameter("carrierId", carrierId)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(AuditReasonReportBO.class)).list();
    }

    private List<FreightAnalysisReportBO> getReports(Long customerId, Long networkId, Date startDate, Date endDate,
            boolean isSavingsReport, DateTypeOption dateType) {
        return isSavingsReport ? executeSavingsReport(customerId, networkId, startDate, endDate)
                : executeActivityReport(customerId, networkId, startDate, endDate, dateType);
    }

    @SuppressWarnings("unchecked")
    private List<FreightAnalysisReportBO> executeActivityReport(Long customerId, Long networkId, Date startDate, Date endDate, DateTypeOption dateType) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_LOADS_FOR_ACTIVITY_REPORT)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("dateType", dateType.name())
                .setResultTransformer(new AliasToBeanResultTransformer(FreightAnalysisReportBO.class)).list();
    }
    
    @SuppressWarnings("unchecked")
    private List<FreightAnalysisReportBO> executeCarrierActivityReport(Long carrierId, Long customerId, Date startDate, Date endDate, DateTypeOption dateType) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_LOADS_FOR_CARRIER_ACTIVITY_REPORT)
                .setParameter("customerId", customerId)
                .setParameter("carrierId", carrierId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("dateType", dateType.name())
                .setResultTransformer(new AliasToBeanResultTransformer(FreightAnalysisReportBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    private List<FreightAnalysisReportBO> executeSavingsReport(Long customerId, Long networkId, Date startDate, Date endDate) {
        return getCurrentSession()
                .getNamedQuery(LoadEntity.GET_LOADS_FOR_SAVINGS_REPORT)
                .setParameter("customerId", customerId)
                .setParameter("networkId", networkId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setResultTransformer(new AliasToBeanResultTransformer(FreightAnalysisReportBO.class)).list();
    }

    private List<AccessorialReportBO> getAccessorialsByLoad(final Long loadId, List<AccessorialReportBO> allAcc) {
        Predicate<AccessorialReportBO> accPredicate = new Predicate<AccessorialReportBO>() {
            @Override
            public boolean apply(AccessorialReportBO acc) {
                return loadId.equals(acc.getLoadId());
            }
        };
        Collection<AccessorialReportBO> accessorials = Collections2.filter(allAcc, accPredicate);
        return new ArrayList<AccessorialReportBO>(accessorials);
    }

    private List<ProductReportBO> getProductsByLoad(final Long loadId, List<ProductReportBO> allProducts) {
        Predicate<ProductReportBO> productsPredicate = new Predicate<ProductReportBO>() {
            @Override
            public boolean apply(ProductReportBO product) {
                return loadId.equals(product.getLoadId());
            }
        };
        Collection<ProductReportBO> products = Collections2.filter(allProducts, productsPredicate);
        return new ArrayList<ProductReportBO>(products);
    }

}
