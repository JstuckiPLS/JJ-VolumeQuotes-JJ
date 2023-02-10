package com.pls.core.domain.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import com.pls.core.domain.enums.CommodityClass;

/**
 * Custom implementation of {@link UserType} to map {@link CommodityClass} enum.
 * 
 * @author Aleksandr Leshchenko
 */
public class CommodityClassUserType extends BaseUserType {

    /**
     * Static mapping of commodity classes.
     */
    private static final Map<String, CommodityClass> INDEX = new HashMap<String, CommodityClass>(18, 1f);
    static {
        for (CommodityClass value : CommodityClass.values()) {
            INDEX.put(value.getDbCode(), value);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.CHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return CommodityClass.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        String commodityClassString = rs.getString(names[0]);
        return INDEX.get(commodityClassString);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {
        CommodityClass commodityClass = (CommodityClass) value;
        if (commodityClass == null) {
            st.setNull(index, Types.CHAR);
        } else {
            st.setString(index, commodityClass.getDbCode());
        }
    }
}