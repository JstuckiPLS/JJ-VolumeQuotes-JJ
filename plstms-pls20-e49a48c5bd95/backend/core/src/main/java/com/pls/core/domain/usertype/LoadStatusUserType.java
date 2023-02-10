package com.pls.core.domain.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.engine.spi.SessionImplementor;

import com.pls.core.domain.enums.ShipmentStatus;

/**
 * Hibernate user type for persisting and loading proper status in LoadEntity.getStatus(),
 * ShipmentHistoryEntity.getShipmentStatus() and ShipmentHistoryEntity#getPreviousShipmentStatus().
 * 
 * @author Aleksandr Leshchenko
 */
public class LoadStatusUserType extends BaseUserType {
    private static final Map<String, ShipmentStatus> PLS1_TO_PLS2_STATUS_MAP = new HashMap<String, ShipmentStatus>();
    private static final Map<ShipmentStatus, String> PLS2_TO_PLS1_STATUS_MAP = new HashMap<ShipmentStatus, String>();

    static {
        PLS1_TO_PLS2_STATUS_MAP.put("A", ShipmentStatus.IN_TRANSIT);
        PLS1_TO_PLS2_STATUS_MAP.put("AD", ShipmentStatus.DISPATCHED);
        PLS1_TO_PLS2_STATUS_MAP.put("AU", null);
        PLS1_TO_PLS2_STATUS_MAP.put("C", ShipmentStatus.CANCELLED);
        PLS1_TO_PLS2_STATUS_MAP.put("CD", ShipmentStatus.DELIVERED);
        PLS1_TO_PLS2_STATUS_MAP.put("CE", null);
        PLS1_TO_PLS2_STATUS_MAP.put("CO", null);
        PLS1_TO_PLS2_STATUS_MAP.put("DA", ShipmentStatus.OUT_FOR_DELIVERY);
        PLS1_TO_PLS2_STATUS_MAP.put("FB", null);
        PLS1_TO_PLS2_STATUS_MAP.put("FP", null);
        PLS1_TO_PLS2_STATUS_MAP.put("GA", ShipmentStatus.DISPATCHED);
        PLS1_TO_PLS2_STATUS_MAP.put("LD", ShipmentStatus.DISPATCHED);
        PLS1_TO_PLS2_STATUS_MAP.put("ML", ShipmentStatus.DISPATCHED);
        PLS1_TO_PLS2_STATUS_MAP.put("O", ShipmentStatus.OPEN);
        PLS1_TO_PLS2_STATUS_MAP.put("PA", ShipmentStatus.BOOKED);
        PLS1_TO_PLS2_STATUS_MAP.put("PC", null);
        PLS1_TO_PLS2_STATUS_MAP.put("PO", ShipmentStatus.OPEN);
        PLS1_TO_PLS2_STATUS_MAP.put("PP", ShipmentStatus.DISPATCHED);
        PLS1_TO_PLS2_STATUS_MAP.put("PT", null);
        PLS1_TO_PLS2_STATUS_MAP.put("T", ShipmentStatus.OPEN);
        PLS1_TO_PLS2_STATUS_MAP.put("X", null);
        PLS1_TO_PLS2_STATUS_MAP.put("PE", ShipmentStatus.PENDING_PAYMENT);

        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.OPEN, "PO");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.BOOKED, "PA");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.DISPATCHED, "PP");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.IN_TRANSIT, "A");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.DELIVERED, "CD");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.CANCELLED, "C");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.OUT_FOR_DELIVERY, "DA");
        PLS2_TO_PLS1_STATUS_MAP.put(ShipmentStatus.PENDING_PAYMENT, "PE");
    }

    /**
     * Get {@link String} representation of PLS1 status from PLS2 status.<br>
     * Should be used only for native SQL.
     * 
     * @param status
     *            PLS2 status
     * @return PLS1 status
     */
    public static String getPLS1StatusForNativeSQL(ShipmentStatus status) {
        return PLS2_TO_PLS1_STATUS_MAP.get(status);
    }

    /**
     * Get PLS 2.0 status from PLS 1.0 status.
     * 
     * @param pls1Status
     *            PLS 1.0 status.
     * @return corresponding PLS 2.0 status.
     */
    public static ShipmentStatus getStatusFromPLS1Status(String pls1Status) {
        return PLS1_TO_PLS2_STATUS_MAP.get(pls1Status);
    }

    /**
     * Get {@link String} representation of PLS1 statuses list from PLS2 statuses list.<br>
     * Should be used only for native SQL.
     *
     * @param statuses
     *            list of PLS2 statuses
     * @return list of PLS1 status
     */
    public static List<String> getPLS1StatusesForNativeSQL(List<ShipmentStatus> statuses) {
        List<String> resultList = new ArrayList<String>(statuses.size());
        for (ShipmentStatus status : statuses) {
            resultList.add(PLS2_TO_PLS1_STATUS_MAP.get(status));
        }
        return resultList;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return ShipmentStatus.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        String statusString = rs.getString(names[0]);
        return PLS1_TO_PLS2_STATUS_MAP.get(statusString);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws SQLException {
        ShipmentStatus status = (ShipmentStatus) value;
        String pls1Status = PLS2_TO_PLS1_STATUS_MAP.get(status);
        if (status == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, pls1Status);
        }
    }

}