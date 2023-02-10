package com.pls.core.util;

import java.util.Locale;

import org.junit.Assert;

/**
 * Helper class to compare SQL expressions.
 * 
 * @author Maxim Medvedev
 */
public final class SqlAssert {

    /**
     * Compare two SQL string ignoring non printable characters.
     * 
     * @param expectedSql
     *            Expected SQL statement.
     * @param actualSql
     *            Actual SQL statement.
     */
    public static void assertEquals(String expectedSql, String actualSql) {
        Assert.assertEquals(SqlAssert.normalize(expectedSql), SqlAssert.normalize(actualSql));
    }

    private static String normalize(String sql) {
        return sql == null ? "" : sql.trim().replaceAll("[\\s]+", " ").replaceAll("[\\s]*(=|>=|<=|>|<)[\\s]*", "$1")
                .replaceAll("[\\s]*[)]", ")").replaceAll("[(][\\s]*", "(").toLowerCase(Locale.ENGLISH);
    }

    private SqlAssert() {
    }
}
