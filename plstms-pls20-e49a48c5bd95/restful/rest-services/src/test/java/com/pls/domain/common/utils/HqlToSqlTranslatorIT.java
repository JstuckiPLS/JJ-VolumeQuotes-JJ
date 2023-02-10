package com.pls.domain.common.utils;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;

/**
 * Test to print SQL query string from named HQL query.
 * 
 * @author Aleksandr Leshchenko
 */
public class HqlToSqlTranslatorIT extends AbstractDaoTest {
    private String toSql(String hqlQueryText) {
        if (hqlQueryText != null && hqlQueryText.trim().length() > 0) {
            QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
            QueryTranslator translator = translatorFactory.createQueryTranslator(hqlQueryText, hqlQueryText, Collections.EMPTY_MAP,
                    (SessionFactoryImpl) getSession().getSessionFactory(), null);
            translator.compile(Collections.EMPTY_MAP, false);
            String sqlString = translator.getSQLString();
            if (StringUtils.isBlank(sqlString)) {
                sqlString = StringUtils.join(translator.collectSqlStrings(), ';');
            }
            return sqlString;
        }
        return null;
    }

    @Test
    public void shouldPrintSQLForNamedQuery() {
        Query query = getSession().getNamedQuery("com.pls.core.domain.organization.BillToEntity.Q_GET_EMAILS");
        String queryString = query.getQueryString();
        System.out.println(queryString);
        System.out.println(toSql(queryString));
    }

}