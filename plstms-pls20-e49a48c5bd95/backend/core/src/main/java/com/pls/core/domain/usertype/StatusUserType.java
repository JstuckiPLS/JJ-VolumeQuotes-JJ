package com.pls.core.domain.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.pls.core.shared.Status;

/**
 * Custom implementation of {@link UserType} to map {@link Status} enum.
 * 
 * @author Maxim Medvedev
 */
public class StatusUserType extends BaseUserType {

    /**
     * Static mapping of statuses.
     */
    private static final Map<String, Status> INDEX = new HashMap<String, Status>(3, 1f);
    static {
        for (Status value : Status.values()) {
            INDEX.put(value.getCode(), value);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.CHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Status.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        String statusString = rs.getString(names[0]);
        return INDEX.get(statusString);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {
        Status status = (Status) value;
        if (status == null) {
            st.setNull(index, Types.CHAR);
        } else {
            st.setString(index, status.getCode());
        }
    }
}