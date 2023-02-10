package com.pls.ltlrating.dao.impl;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.PersistentLabeledEnum;
import com.pls.ltlrating.dao.LtlImportExportPricesDao;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;

/**
 * LtlImportExportPricesDaoImpl class javadoc.
 *
 * @author Alex Krychenko.
 */
@SuppressWarnings("unchecked")
@Transactional
@Repository
public class LtlImportExportPricesDaoImpl implements LtlImportExportPricesDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(LtlImportExportPricesDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<LtlPricingItem> exportPrices(final int pageNumber, final int pageSize) {
        LOGGER.info("Call export prices for page[{}], page size[{}]", pageNumber, pageSize);
        return exportLtlPricingItems(pageNumber, pageSize, LtlPricingProfileEntity.Q_GET_PRICES_FOR_EXPORT);
    }

    @Override
    public List<LtlPricingItem> exportAccessorials(final int pageNumber, final int pageSize) {
        LOGGER.info("Call export accesorial for page[{}], page size[{}]", pageNumber, pageSize);
        return exportLtlPricingItems(pageNumber, pageSize, LtlPricingProfileEntity.Q_GET_ACCESSORIALS_FOR_EXPORT);
    }

    @Override
    public List<LtlPricingItem> exportFuel(final int pageNumber, final int pageSize) {
        LOGGER.info("Call export fuel surcharges for page[{}], page size[{}]", pageNumber, pageSize);
        return exportLtlPricingItems(pageNumber, pageSize, LtlPricingProfileEntity.Q_GET_FUEL_FOR_EXPORT);
    }

    @Override
    public List<LtlPricingItem> exportPaletPrices(final int pageNumber, final int pageSize) {
        LOGGER.info("Call export pallet prices for page[{}], page size[{}]", pageNumber, pageSize);
        return exportLtlPricingItems(pageNumber, pageSize, LtlPricingProfileEntity.Q_GET_PALLET_PRICES_FOR_EXPORT);
    }

    @Override
    public LtlPricingDetailsEntity findPricingDetailByPricingItem(final LtlPricingItem item) {
        Criteria criteria = getCurrentSession().createCriteria(LtlPricingDetailsEntity.class);
        criteria.add(Restrictions.eqOrIsNull("smcTariff", item.getItemName()));
        criteria.add(Restrictions.eqOrIsNull("costApplMinWt", item.getCostApplMinWt()));
        criteria.add(Restrictions.eqOrIsNull("costApplMaxWt", item.getCostApplMaxWt()));
        criteria.add(Restrictions.eqOrIsNull("costApplMinDist", item.getCostApplMinDist()));
        criteria.add(Restrictions.eqOrIsNull("costApplMaxDist", item.getCostApplMaxDist()));
        addPersistentEnumToCriteria(criteria, item.getMarginType(), "marginType");
        criteria.add(Restrictions.eqOrIsNull("marginAmount", item.getUnitMargin()));
        criteria.add(Restrictions.eqOrIsNull("minMarginAmount", item.getMarginDollarAmt()));
        setupCriteriaToSearchByPricingItem(item, criteria);

        LtlPricingDetailsEntity pricingDetailsEntity = null;
        List<LtlPricingDetailsEntity> list = criteria.list();
        if (CollectionUtils.isNotEmpty(list)) {
            pricingDetailsEntity = list.stream().filter(priceDetailEntity -> validateFAK(priceDetailEntity, item)).findFirst().orElse(null);
        }
        return pricingDetailsEntity;
    }

    @Override
    public LtlAccessorialsEntity findAccessorialByPricingItem(final LtlPricingItem item) {
        Criteria criteria = getCurrentSession().createCriteria(LtlAccessorialsEntity.class);
        criteria.add(Restrictions.eqOrIsNull("accessorialType", item.getItemName()));
        criteria.add(Restrictions.eqOrIsNull("maxCost", item.getMaxCost()));
        criteria.add(Restrictions.eqOrIsNull("costApplMinWt", getLongValue(item.getCostApplMinWt())));
        criteria.add(Restrictions.eqOrIsNull("costApplMaxWt", getLongValue(item.getCostApplMaxWt())));
        criteria.add(Restrictions.eqOrIsNull("costApplMinDist", getLongValue(item.getCostApplMinDist())));
        criteria.add(Restrictions.eqOrIsNull("costApplMaxDist", getLongValue(item.getCostApplMaxDist())));
        populateMarginTypeString(item, criteria);
        setupCriteriaToSearchByPricingItem(item, criteria);
        criteria.add(Restrictions.eqOrIsNull("marginDollarAmt", item.getMarginDollarAmt()));
        criteria.add(Restrictions.eqOrIsNull("unitMargin", item.getUnitMargin()));
        criteria.add(Restrictions.eqOrIsNull("marginPercent", item.getMarginPercent()));

        LtlAccessorialsEntity entity = null;
        List<LtlAccessorialsEntity> list = criteria.list();
        if (CollectionUtils.isNotEmpty(list)) {
            entity = list.get(0);
        }
        return entity;
    }

    @Override
    public LtlPalletPricingDetailsEntity findPalletByPricingItem(final LtlPricingItem item) {
        Criteria criteria = getCurrentSession().createCriteria(LtlPalletPricingDetailsEntity.class);
        criteria.add(Restrictions.eq("profileDetailId", item.getProfileDetailId()));
        criteria.add(Restrictions.eqOrIsNull("minQuantity", getLongValue(item.getMinCost())));
        criteria.add(Restrictions.eqOrIsNull("maxQuantity", getLongValue(item.getMaxCost())));
        addPersistentEnumToCriteria(criteria, item.getCostType(), "costType");
        criteria.add(Restrictions.eqOrIsNull("unitCost", item.getUnitCost()));
        criteria.add(Restrictions.eqOrIsNull("costApplMinWt", item.getCostApplMinWt()));
        criteria.add(Restrictions.eqOrIsNull("costApplMaxWt", item.getCostApplMaxWt()));
        criteria.add(Restrictions.eqOrIsNull("marginPercent", item.getMarginPercent()));
        criteria.add(Restrictions.eqOrIsNull("effDate", item.getEffectiveFrom()));
        criteria.add(Restrictions.eqOrIsNull("expDate", item.getEffectiveTo()));
        addPersistentEnumToCriteria(criteria, item.getServiceType(), "serviceType");
        criteria.add(Restrictions.eqOrIsNull("movementType", item.getMovementType()));
        criteria.add(Restrictions.eqOrIsNull("transitTime", getLongValue(item.getUnitMargin())));

        LtlPalletPricingDetailsEntity entity = null;
        List<LtlPalletPricingDetailsEntity> list = criteria.list();
        if (CollectionUtils.isNotEmpty(list)) {
            entity = list.get(0);
        }
        return entity;
    }

    private List<LtlPricingItem> exportLtlPricingItems(final int pageNumber, final int pageSize, final String queryName) {
        Query namedQuery = getCurrentSession().getNamedQuery(queryName);
        namedQuery.setFirstResult(pageSize * pageNumber);
        namedQuery.setMaxResults(pageSize);
        namedQuery.setReadOnly(true);
        namedQuery.setResultTransformer(new CaseInsensitiveAliasToBeanResultTransformer(LtlPricingItem.class));
        return namedQuery.list();
    }

    private void populateMarginTypeString(final LtlPricingItem item, final Criteria criteria) {
        criteria.add(
                Restrictions.eqOrIsNull("marginType", item.getMarginType() != null ? item.getMarginType().getPersistentEnum().name() : null));
    }

    private Long getLongValue(final BigDecimal origValue) {
        if (origValue != null) {
            return origValue.longValue();
        }
        return null;
    }

    private boolean validateFAK(final LtlPricingDetailsEntity priceDetailEntity, final LtlPricingItem item) {
        String fakMapEntityString = "";
        if (CollectionUtils.isNotEmpty(priceDetailEntity.getFakMapping())) {
            fakMapEntityString =
                    priceDetailEntity.getFakMapping().stream().sorted((o1, o2) -> o1.getActualClass().compareTo(o2.getActualClass()))
                                     .map(fakEntry -> fakEntry.getActualClass().name() + ":" + fakEntry.getMappingClass())
                                     .collect(Collectors.joining(";"));
        }
        return StringUtils.equalsIgnoreCase(fakMapEntityString, item.toFakMap());
    }

    private void setupCriteriaToSearchByPricingItem(final LtlPricingItem item, final Criteria criteria) {
        criteria.add(Restrictions.eq("ltlPricProfDetailId", item.getProfileDetailId()));
        addPersistentEnumToCriteria(criteria, item.getCostType(), "costType");
        criteria.add(Restrictions.eqOrIsNull("unitCost", item.getUnitCost()));
        criteria.add(Restrictions.eqOrIsNull("minCost", item.getMinCost()));
        criteria.add(Restrictions.eqOrIsNull("effDate", item.getEffectiveFrom()));
        criteria.add(Restrictions.eqOrIsNull("expDate", item.getEffectiveTo()));
        addPersistentEnumToCriteria(criteria, item.getServiceType(), "serviceType");
        criteria.add(Restrictions.eqOrIsNull("movementType", item.getMovementType()));
    }

    private void addPersistentEnumToCriteria(final Criteria criteria, final PersistentLabeledEnum persistentLabeledEnum, final String propertyName) {
        Enum<?> persistentEnum = null;
        if (persistentLabeledEnum != null) {
            persistentEnum = persistentLabeledEnum.getPersistentEnum();
        }
        criteria.add(Restrictions.eqOrIsNull(propertyName, persistentEnum));
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    private static class CaseInsensitiveAliasToBeanResultTransformer extends AliasToBeanResultTransformer {
        private Class resultClass;
        String[] correctedAliases;

        CaseInsensitiveAliasToBeanResultTransformer(final Class resultClass) {
            super(resultClass);
            this.resultClass = resultClass;
        }

        @Override
        public Object transformTuple(final Object[] tuple, final String[] aliases) {
            LtlPricingItem transformedObject = null;
            try {
                transformedObject = (LtlPricingItem) super.transformTuple(tuple, correctAliases(aliases));
            } catch (Exception e) {
                LOGGER.error(String.format("Exception during transforming tupple: [%s] to pricing item", Arrays.deepToString(tuple)), e);
            }
            return transformedObject;
        }

        private String[] correctAliases(final String[] aliases) {
            if (correctedAliases == null) {
                createCorrectedAliases(aliases);
            }
            return correctedAliases;
        }

        private void createCorrectedAliases(final String[] aliases) {
            correctedAliases = new String[aliases.length];
            PropertyDescriptor[] propertyDescriptors = new BeanWrapperImpl(resultClass).getPropertyDescriptors();
            int index = 0;
            for (String alias : aliases) {
                String correctedAlias = alias;
                if ("ROWNUM_".equals(alias)) {
                    correctedAlias = "rowNum";
                } else {
                    for (PropertyDescriptor propDescr : propertyDescriptors) {
                        if (propDescr.getName().equalsIgnoreCase(alias)) {
                            correctedAlias = propDescr.getName();
                            break;
                        }
                    }
                }
                correctedAliases[index++] = correctedAlias;
            }

        }
    }
}
