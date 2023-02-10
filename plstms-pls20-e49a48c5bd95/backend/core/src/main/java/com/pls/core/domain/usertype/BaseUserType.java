package com.pls.core.domain.usertype;

import java.io.Serializable;

import org.hibernate.usertype.UserType;

/**
 * Base class for custom {@link UserType} implementations.
 * 
 * @author Maxim Medvedev
 */
public abstract class BaseUserType implements UserType {

    @Override
    public boolean equals(Object x, Object y) {
        if (x == null && y == null) {
            return true;
        } else if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return Boolean.FALSE;
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
}