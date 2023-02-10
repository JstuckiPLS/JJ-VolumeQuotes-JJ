package com.pls.core.domain.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

/**
 * Custom implementation of {@link UserType} to allow save empty string as whitespace.
 * 
 * @author Artem Arapov
 *
 */
public class StringUserType extends BaseUserType {

    private static final String DEFAULT_VALUE = " ";

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.VARCHAR};
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = (String) StringType.INSTANCE.nullSafeGet(rs, names[0], session, owner);
        return StringUtils.defaultString(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else if (((String) value).trim().isEmpty()) {
            st.setString(index, DEFAULT_VALUE);
        } else {
            StringType.INSTANCE.nullSafeSet(st, value, index, session);
        }
    }
}
