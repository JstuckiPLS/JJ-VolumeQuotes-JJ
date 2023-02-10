package com.pls.core.dao.impl.model;

import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Test cases to check the compliance of data between the "COMMODITY_CLASS_CODES" database table and
 * {@link com.pls.core.domain.enums.CommodityClass} enum.
 * 
 * @author Maxim Medvedev
 */
public class CommodityClassMappingIT extends AbstractDaoTest {

    @Test
    public void checkEnumNumbers() {
        Assert.assertEquals(CommodityClass.values().length, extractDbClassCodes().size());
    }

    @Test
    public void checkEnumFields() {
        List<String> dbData = extractDbClassCodes();

        for (CommodityClass enumItem : CommodityClass.values()) {

            Assert.assertTrue("DB resord for '" + enumItem + "' enum item was not found",
                    dbData.contains(enumItem.getDbCode()));
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> extractDbClassCodes() {
        Query q = getSession().createSQLQuery(
                "SELECT cast(CCC.CLASS_CODE as varchar) AS CLASS_CODE FROM COMMODITY_CLASS_CODES CCC");
        return q.list();
    }

    @Test
    public void checkDbRows() {
        for (String dbClassCode : extractDbClassCodes()) {
            CommodityClass enumItem = CommodityClass.convertFromDbCode(dbClassCode);

            Assert.assertNotNull("Java enum item for '" + dbClassCode + "' was not found", enumItem);
        }
    }
}
