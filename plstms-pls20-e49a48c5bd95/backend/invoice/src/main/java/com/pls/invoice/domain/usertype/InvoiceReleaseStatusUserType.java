package com.pls.invoice.domain.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.pls.core.domain.usertype.BaseUserType;
import com.pls.invoice.domain.bo.enums.InvoiceReleaseStatus;


/**
 * Custom implementation of {@link UserType} to map {@link InvoiceReleaseStatus} enum.
 * 
 * @author Aleksandr Leshchenko
 */
public class InvoiceReleaseStatusUserType extends BaseUserType {

    /**
     * Static mapping of invoice release statuses.
     */
    private static final Map<String, InvoiceReleaseStatus> INDEX = new HashMap<String, InvoiceReleaseStatus>(3, 1f);
    static {
        for (InvoiceReleaseStatus value : InvoiceReleaseStatus.values()) {
            INDEX.put(value.getStatusCode(), value);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.CHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return InvoiceReleaseStatus.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        String invoiceStatusString = rs.getString(names[0]);
        return INDEX.get(invoiceStatusString);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {
        InvoiceReleaseStatus invoiceStatus = (InvoiceReleaseStatus) value;
        if (invoiceStatus == null) {
            st.setNull(index, Types.CHAR);
        } else {
            st.setString(index, invoiceStatus.getStatusCode());
        }
    }
}