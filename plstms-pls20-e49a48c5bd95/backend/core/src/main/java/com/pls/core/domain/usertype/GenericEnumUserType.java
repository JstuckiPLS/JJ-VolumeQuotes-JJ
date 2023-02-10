package com.pls.core.domain.usertype;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * {@link UserType} for enums.
 * 
 * @author Denis Zhupinsky
 */
public class GenericEnumUserType implements UserType, ParameterizedType {

    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";

    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

    private Class<? extends Enum<?>> enumClass;

    private Method identifierMethod;

    private Method valueOfMethod;

    private AbstractSingleColumnStandardBasicType<?> type;

    private int[] sqlTypeValues;

    @Override
    public int[] sqlTypes() {
        return sqlTypeValues;
    }

    @Override
    public Class<?> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(Object x, Object y) {
        return ObjectUtils.equals(x, y);
    }

    @Override
    public int hashCode(Object x) {
        return ObjectUtils.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {

        if (ArrayUtils.isEmpty(names)) {
            return null;
        }

        Object identifier = type.get(rs, names[0], session);

        if (rs.wasNull()) {
            return null;
        }

        try {
            return valueOfMethod.invoke(enumClass, identifier);
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking valueOf method '" + valueOfMethod.getName()
                    + "' of enumeration class '" + enumClass.getName() + "' ", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {

        try {
            if (value == null) {
                st.setNull(index, ((AbstractSingleColumnStandardBasicType<?>) type).sqlType());
            } else {
                Object identifier = identifierMethod.invoke(value);
                type.nullSafeSet(st, identifier, index, session);
            }
        } catch (Exception e) {
            throw new HibernateException("Exception while invoking identifier method '" + identifierMethod.getName()
                    + "' of enumeration class '" + enumClass.getName() + "' ", e);
        }
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = (Class<? extends Enum<?>>) Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("Enum class " + enumClassName + " not found", e);
        }

        String identifierMethodName = parameters.getProperty("identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);
        Class<?> identifierType;
        try {
            identifierMethod = enumClass.getMethod(identifierMethodName);
            identifierType = identifierMethod.getReturnType();
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain identifier method", e);
        }

        type =
                (AbstractSingleColumnStandardBasicType<?>) new TypeResolver().heuristicType(identifierType.getName(),
                        parameters);

        if (type == null) {
            throw new HibernateException("Unsupported identifier type " + identifierType.getName());
        }

        sqlTypeValues = new int[] {type.sqlType()};

        String valueOfMethodName = parameters.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);
        try {
            valueOfMethod = enumClass.getMethod(valueOfMethodName, identifierType);
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain valueOf method", e);
        }
    }

    public void setEnumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    public void setIdentifierMethod(Method identifierMethod) {
        this.identifierMethod = identifierMethod;
    }

    public void setValueOfMethod(Method valueOfMethod) {
        this.valueOfMethod = valueOfMethod;
    }
}
