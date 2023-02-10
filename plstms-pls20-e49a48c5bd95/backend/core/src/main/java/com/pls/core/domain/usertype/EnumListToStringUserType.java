package com.pls.core.domain.usertype;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.ParameterizedType;

/**
 * Hibernate User Type for mapping {@link List} of {@link Enum} to coma separated {@link String}.
 * 
 * @author Brichak Aleksandr
 * @author Aleksandr Leshchenko
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumListToStringUserType extends BaseUserType implements ParameterizedType {

    private static final String SEPARATOR = ",";

    private Class<? extends Enum<?>> enumClass;

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.VARCHAR};
    }

    @Override
    public Class<?> returnedClass() {
        return List.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = (String) StringType.INSTANCE.nullSafeGet(rs, names[0], session, owner);
        List type = null;
        if (value != null) {
            String[] values = StringUtils.split(value, SEPARATOR);
            if (values.length > 0) {
                type = new ArrayList(values.length);
                try {
                    for (String item : values) {
                        type.add(enumClass.getMethod("valueOf", String.class).invoke(enumClass, item));
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                        | SecurityException e) {
                    throw new HibernateException("Exception while invoking identifier method 'valueOf' of enumeration class '"
                            + enumClass.getName() + "' ", e);
                }
            }
        }

        return type;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        List type = (List) value;
        String stringType = StringUtils.join(type, SEPARATOR);
        StringType.INSTANCE.nullSafeSet(st, stringType, index, session);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value != null ? new ArrayList((List) value) : null;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null && y == null) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else if (x.equals(y)) {
            return true;
        } else {
            if (x instanceof ArrayList<?> && y instanceof ArrayList<?>) {
                ArrayList one = (ArrayList) (x);
                ArrayList two = (ArrayList) (y);
                return one.containsAll(two) && two.containsAll(one);
            } else {
                return false;
            }
        }
    }

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = (Class<? extends Enum<?>>) Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("Enum class " + enumClassName + " not found", e);
        }
    }

    public void setEnumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }
}
