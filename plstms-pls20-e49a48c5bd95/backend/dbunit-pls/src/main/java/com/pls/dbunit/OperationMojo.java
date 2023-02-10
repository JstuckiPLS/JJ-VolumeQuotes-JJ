package com.pls.dbunit;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.ant.Operation;
import org.dbunit.database.IDatabaseConnection;

/**
 * Execute DbUnit's Database Operation with an external dataset file.
 *
 * @goal operation
 * @author <a href="mailto:dantran@gmail.com">Dan Tran</a>
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id: OperationMojo.java 9179 2009-03-08 23:21:28Z david $
 *
 */
public class OperationMojo extends AbstractDbUnitMojo {
    /**
     * Type of Database operation to perform. Supported types are UPDATE, INSERT, DELETE, DELETE_ALL, REFRESH,
     * CLEAN_INSERT, MSSQL_INSERT, MSSQL_REFRESH, MSSQL_CLEAN_INSERT.
     *
     * @parameter type="type"
     * @required
     */
    protected String type;

    /**
     * When true, place the entired operation in one transaction.
     * 
     * @parameter transaction="transaction" default-value="false"
     */
    protected boolean transaction;

    /**
     * DataSet file.
     *
     * @parameter src="src"
     * @required
     */
    protected File src;

    /**
     * Dataset file format type. Valid types are: flat, xml, csv, and dtd.
     *
     * @parameter format="format" default-value="xml";
     * @required
     */
    protected String format;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            this.getLog().info("Skip operation: " + type + " execution");
            return;
        }

        super.execute();

        try {
            IDatabaseConnection connection = createConnection();
            try {
                disableConstraints(connection.getConnection());

                File[] filesToOperate;
                if (src.isDirectory()) {
                    filesToOperate = src.listFiles();

                    if (filesToOperate == null) {
                        throw new IllegalStateException("Source directory do not contains files to operate");
                    }
                } else {
                    filesToOperate = new File[]{src};
                }

                Arrays.sort(filesToOperate, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                for (File file : filesToOperate) {
                    long time = System.currentTimeMillis();

                    Operation op = new Operation();
                    op.setFormat(format);
                    op.setSrc(file);
                    op.setTransaction(transaction);
                    op.setType(type);
                    op.execute(connection);

                    logProgress(file.getName(), time);
                }
                enableConstraints(connection.getConnection());
                applyDotRegionFuelScript(connection.getConnection());
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            throw new MojoExecutionException(
                    "Error executing database operation: " + type, e);
        }
    }

    private static final BigDecimal[] FUEL_CHARGES = new BigDecimal[] {
        new BigDecimal("3.053"),
        new BigDecimal("3.134"),
        new BigDecimal("3.195"),
        new BigDecimal("3.239"),
        new BigDecimal("3.039"),
        new BigDecimal("3.010"),
        new BigDecimal("2.964"),
        new BigDecimal("3.027"),
        new BigDecimal("3.126"),
        new BigDecimal("2.978"),
        new BigDecimal("3.250")
    };

    private void applyDotRegionFuelScript(Connection connection) throws SQLException {
        connection.prepareStatement("delete from FLATBED.dot_region_fuel").executeUpdate();
        String insertSQL = "insert into flatbed.dot_region_fuel (DOT_REGION_FUEL_ID,DOT_REGION_ID,FUEL_CHARGE,EFF_DATE,EXP_DATE,STATUS,DATE_CREATED,"
                + "CREATED_BY,DATE_MODIFIED,MODIFIED_BY,VERSION) values "
                + "(nextval('flatbed.DOT_REGION_FUEL_SEQ'),?,?,?,?,'A',current_date,0,current_date,0,1)";
        PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
        Calendar effDate = Calendar.getInstance();
        effDate.add(Calendar.DAY_OF_YEAR, -14);
        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DAY_OF_YEAR, -13);

        for (int numberOfDays = 0; numberOfDays < 100; numberOfDays++) {
            for (int i = 0; i < FUEL_CHARGES.length; i++) {
                insertStatement.setInt(1, i + 1);
                insertStatement.setBigDecimal(2, FUEL_CHARGES[i]);
                insertStatement.setDate(3, new Date(effDate.getTimeInMillis()));
                insertStatement.setDate(4, new Date(expDate.getTimeInMillis()));
                insertStatement.executeUpdate();
            }
            effDate.add(Calendar.DAY_OF_YEAR, 1);
            expDate.add(Calendar.DAY_OF_YEAR, 1);
        }
        this.getLog().info("dot_region_fuel changes applied");
    }

    private void disableConstraints(Connection connection) throws MojoExecutionException {
        try {
            java.sql.CallableStatement statement = connection.prepareCall("{call FLATBED.DISABLE_CONSTRAINTS}");
            statement.execute();
        } catch (Exception e) {
            throw new MojoExecutionException("Error constraints not disable ", e);
        }
    }

    private void enableConstraints(Connection connection) throws MojoExecutionException {
        try {
            java.sql.CallableStatement statement = connection.prepareCall("{call FLATBED.ENABLE_CONSTRAINTS}");
            statement.execute();
        } catch (Exception e) {
            throw new MojoExecutionException("Error constraints not enable  ", e);
        }
    }

    private void logProgress(String fileName, long time) {
        StringBuilder info = new StringBuilder();
        info.append(" --- ").append(fileName).append(' ');
        while (info.length() < 50) {
            info.append('.');
        }
        info.append(' ').append(System.currentTimeMillis() - time).append("ms ---");
        this.getLog().info(info);
    }
}
