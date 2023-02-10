package com.pls.shipment.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.shipment.dao.LtlProductDao;
import com.pls.shipment.domain.LtlProductEntity;

/**
 * Implementation of {@link com.pls.shipment.dao.LtlProductDao}.
 *
 * @author Maxim Medvedev
 */
@Repository
@Transactional
public class LtlProductDaoImpl extends AbstractDaoImpl<LtlProductEntity, Long> implements LtlProductDao {
    @Override
    public boolean checkProductExists(Long customerId, Long productId) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_COUNT_PRODUCTS_BY_ID);
        query.setParameter("customerId", customerId);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        return !Long.valueOf(0).equals(query.uniqueResult());
    }

    @Override
    public void markAsInactive(Long productId, Long modifiedBy, Date modifiedDate) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_ARCHIVE_PRODUCT);
        query.setParameter("productId", productId);
        query.setParameter("modifiedBy", modifiedBy);
        query.setParameter("modifiedDate", modifiedDate);
        query.executeUpdate();
    }

    @Override
    public LtlProductEntity save(LtlProductEntity product) {
        getCurrentSession().saveOrUpdate(product);
        return product;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlProductEntity> findRecentProducts(Long orgId, Long userId, Integer count, CommodityClass commodityClass, boolean hazmat) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_RECENT_PRODUCTS);

        query.setLong("orgId", orgId);
        query.setParameter("personId", userId);
        query.setParameter("hazmat", hazmat);
        query.setParameter("commodityClass", commodityClass);

        return query.setMaxResults(count).setResultTransformer(new ResultTransformer() {
            private static final long serialVersionUID = -5205532176196681137L;

            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                return tuple[0];
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List transformList(List collection) {
                return collection;
            }
        }).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlProductEntity> findProducts(Long orgId, Long userId, String filter, CommodityClass commodityClass, boolean hazmat, Integer count) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_PRODUCTS_BY_FILTER);

        query.setLong("orgId", orgId);
        query.setParameter("personId", userId);
        query.setParameter("hazmat", hazmat);
        query.setParameter("commodityClass", commodityClass);
        query.setParameter("filter", StringUtils.isBlank(filter) ? null : '%' + filter.trim().toUpperCase() + '%');

        return query.setMaxResults(count).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlProductEntity> getProductList(Long orgId, Long userId) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_PRODUCT_LIST);
        query.setParameter("customerId", orgId);
        query.setParameter("personId", userId, LongType.INSTANCE);
        return query.list();
    }

    @Override
    public boolean isProductUnique(Long orgId, Long productId, Long personId, String description, CommodityClass commodityClass) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_IS_PRODUCT_UNIQUE);
        query.setParameter("customerId", orgId);
        query.setParameter("productId", productId, LongType.INSTANCE);
        query.setParameter("personId", personId, LongType.INSTANCE);
        query.setParameter("description", description);
        query.setParameter("commodityClass", commodityClass);
        return (Long) query.uniqueResult() == 0;
    }

    @Override
    public LtlProductEntity findProductByDescAndName(String productCode, String description) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_PRODUCT_BY_CODE_AND_NAME);
        query.setParameter("productCode", productCode);
        query.setParameter("description", description);
        LtlProductEntity result = null;
        if (!query.list().isEmpty()) {
            result = (LtlProductEntity) query.list().get(0);
        }
        return result;
    }


    @Override
    public LtlProductEntity findProductByInfo(String description, Long orgId, boolean hazmat, CommodityClass commodityClass) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_PRODUCT_BY_INFO);
        query.setParameter("description", description);
        query.setParameter("hazmat", hazmat);
        query.setParameter("commodityClass", commodityClass);
        query.setParameter("orgId", orgId);
        LtlProductEntity result = null;
        if (!query.list().isEmpty()) {
            result = (LtlProductEntity) query.list().get(0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LtlProductEntity findUniqueProductByInfo(Long orgId, CommodityClass commodityClass, String productCode) {
        Query query = getCurrentSession().getNamedQuery(LtlProductEntity.Q_GET_PRODUCT_BY_CLASS_AND_SKU);
        query.setParameter("commodityClass", commodityClass);
        query.setParameter("productCode", productCode);
        query.setParameter("orgId", orgId);
        LtlProductEntity result = null;
        List<LtlProductEntity> list = query.list();
        if (list.size() == 1) {
            result = (LtlProductEntity) list.get(0);
        }
        return result;
    }
}
