package com.pls.shipment.dao.impl;

import com.pls.shipment.dao.LaneDataDao;
import com.pls.shipment.domain.LaneDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dao implementation for Lane data.
 * 
 * @author Viacheslav Vasyanovich
 * 
 */
@Repository
@Transactional
public class LaneDataDaoImpl implements LaneDataDao {
    private static final String WHERE_CLAUSE_TAG = "{whereClause}";
    private static final String ORDER_BY = "Order by";
    private static final String WHITESPACE = " ";
    private static final String COMMA = ",";
    /**
     * Column names should be the same, as fields in DTO object (or even PropertyAccess class). The idea is
     * that UI (gxt grid) now provides information about sorting that mapped on the field names of DTO object.
     * So, from the UI grid we can get column name and sort direction. If we want to sort results as on the UI
     * - we need to convert dto field names to the sql query fields, or keep it the same in both situations.
     * This column name will be the same, as the name of DTO object. That's why this constants must be the
     * same, as fields in DTO object. This is bad situation, because current DAO layer must to know about UI
     * layer. So, better solution would be map this fields on the restful layer (only for keeping this code in
     * the architecture borders).
     */
    private static final String BOL_FIELD = "bol";
    private static final String CARRIER_FIELD = "carrier";
    private static final String INVOICEDATE_FIELD = "invoiceDate";
    private static final String PICKUPDATE_FIELD = "pickupDate";
    private static final String ORIGINZIP_FIELD = "originZip";
    private static final String DESTINATIONZIP_FIELD = "destinationZip";
    private static final String COMMODITYCLASS1_FIELD = "class1";
    private static final String WEIGHT1_FIELD = "weight1";
    private static final String FUEL_FIELD = "fuel";
    private static final String COST_FIELD = "cost";
    private static final String ACCESSORIALS_FIELD = "accessorials";
    private static final String TOTAL_FIELD = "total";

    private static final String ALL_LANE_DATA_WHERE_CLAUSE = "l.org_id=:customerId";
    private static final String DATE_RANGER_LANE_DATA_WHERE_CLAUSE = "l.org_id=:customerId and ld.departure "
            + "between :startDate and (:endDate + 1) ";

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("dataSource")
    void setDatasource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private static final String BASE_QUERY = " "
            + "        with subq as                                                                         "
            + "        (                                                                                    "
            + "          select                                                                             "
            + "            row_number() over (partition by l.load_id  order by                              "
            + "             lm.load_material_id,ld.load_detail_id) as rn,                                   "
            + "            l.bol as bol, car_org.scac as carrier, l.gl_date as invoiceDate, ld.departure    "
            + "             as pickupDate, route.orig_zip as originZip,                                     "
            + "          route.dest_zip as destinationZip,                                                  "
            + "            lm.commodity_class_code, lm.weight,                                              "
            + "            (select sum(subtotal) from cost_detail_items where cost_detail_id in             "
            + "              (select cost_detail_id from load_cost_details where status = 'A'               "
            + "             and load_id = l.load_id)                                                        "
            + "              and ref_type = 'FS') as fuel,                                                  "
            + "            (select subtotal from cost_detail_items where cost_detail_id in                  "
            + "              (select cost_detail_id from load_cost_details where                            "
            + "                 load_id = l.load_id and status = 'A')                                       "
            + "              and ref_type = 'SRA') as cost,                                                 "
            + "            (select sum(subtotal) from cost_detail_items where cost_detail_id in             "
            + "              (select cost_detail_id from load_cost_details where status = 'A'               "
            + "                 and load_id = l.load_id)                                                    "
            + "              and ref_type not in ('SRA', 'CRA','BRA','FS')) as accessorials,                "
            + "            (select total_revenue from load_cost_details where load_id = l.load_id and       "
            + "             status = 'A' and rownum = 1) as total from loads l                              "
            + "               inner join load_proposals lp on lp.load_id=l.load_id and lp.status='BOOKED'   "
            + "               inner join organizations car_org on car_org.org_id = lp.carrier_id            "
            + "            left outer join load_details ld on ld.load_id = l.load_id and ld.point_type = 'O'"
            + "            left outer join routes route on route.route_id = l.route_id                      "
            + "            left outer join load_materials lm on lm.load_detail_id = ld.load_detail_id       "
            + "          where gl_date is not null and                                                      "
            + WHERE_CLAUSE_TAG
            + "        )"
            + "        select bol, carrier, invoiceDate, pickupDate, originZip, destinationZip, fuel, cost, "
            + "           accessorials, total,"
            + "          max(class1) as class1,"
            + "          max(weight1) as weight1,"
            + "          max(commodity_class_2) as commodity_class_2,"
            + "          max(weight_2) as weight_2,"
            + "          max(commodity_class_3) as commodity_class_3,"
            + "          max(weight_3) as weight_3,"
            + "          max(commodity_class_4) as commodity_class_4,"
            + "          max(weight_4) as weight_4,"
            + "          max(commodity_class_5) as commodity_class_5,"
            + "          max(weight_5) as weight_5,"
            + "          max(commodity_class_6) as commodity_class_6,"
            + "          max(weight_6) as weight_6,"
            + "          max(commodity_class_7) as commodity_class_7,"
            + "          max(weight_7) as weight_7"
            + "          from (    "
            + "            select bol, carrier, invoiceDate, pickupDate, originZip, destinationZip, "
            + "coalesce(fuel, 0) fuel, coalesce(cost, 0) cost, coalesce(accessorials, 0) accessorials, coalesce(total, 0) total,"
            + "              decode(rn, 1, commodity_class_code, null) as class1, "
            + "              decode(rn, 1, weight, null) as weight1,"
            + "              decode(rn, 2, commodity_class_code, null) as commodity_class_2, "
            + "              decode(rn, 2, weight, null) as weight_2,"
            + "              decode(rn, 3, commodity_class_code, null) as commodity_class_3,"
            + "              decode(rn, 3, weight, null) as weight_3,"
            + "              decode(rn, 4, commodity_class_code, null) as commodity_class_4,"
            + "              decode(rn, 4, weight, null) as weight_4,"
            + "              decode(rn, 5, commodity_class_code, null) as commodity_class_5,"
            + "              decode(rn, 5, weight, null) as weight_5,"
            + "              decode(rn, 6, commodity_class_code, null) as commodity_class_6,"
            + "              decode(rn, 6, weight, null) as weight_6,"
            + "              decode(rn, 7, commodity_class_code, null) as commodity_class_7,"
            + "              decode(rn, 7, weight, null) as weight_7"
            + "            from subq where rn < 8             "
            + ") group by bol, carrier, invoiceDate, pickupDate, originZip, destinationZip,"
            + " fuel, cost, accessorials, total ";

    @Override
    public List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate,
            Map<String, String> sortInfo) {
        String queryString = null;
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("customerId", customerId);

        if (startDate != null && endDate != null) {
            queryString = BASE_QUERY.replace(WHERE_CLAUSE_TAG, DATE_RANGER_LANE_DATA_WHERE_CLAUSE);
            parameters.put("startDate", startDate);
            parameters.put("endDate", endDate);
        } else {
            queryString = BASE_QUERY.replace(WHERE_CLAUSE_TAG, ALL_LANE_DATA_WHERE_CLAUSE);
        }
        if (!sortInfo.isEmpty()) {
            StringBuilder orderByBuilder = new StringBuilder();
            orderByBuilder.append(queryString);
            orderByBuilder.append(ORDER_BY);
            orderByBuilder.append(WHITESPACE);
            Iterator<String> columnIterator = sortInfo.keySet().iterator();
            while (columnIterator.hasNext()) {
                String column = columnIterator.next();
                String direction = sortInfo.get(column);
                orderByBuilder.append(column);
                orderByBuilder.append(WHITESPACE);
                orderByBuilder.append(direction);
                if (columnIterator.hasNext()) {
                    orderByBuilder.append(COMMA);
                }
            }
            queryString = orderByBuilder.toString();
        }
        return jdbcTemplate.query(queryString, parameters, new LaneDataRowMapper());
    }

    @Override
    public List<LaneDataEntity> getLaneDataByPeriod(Long customerId, Date startDate, Date endDate) {
        return getLaneDataByPeriod(customerId, startDate, endDate, new HashMap<String, String>());
    }

    /**
     * Result mapper for Query, that provides lane data entity as a result.
     * 
     * @author Viacheslav Vasianovych
     * 
     */
    private static class LaneDataRowMapper implements RowMapper<LaneDataEntity> {

        @Override
        public LaneDataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            LaneDataEntity entity = new LaneDataEntity();
            entity.setBol(rs.getString(BOL_FIELD));
            entity.setCarrier(rs.getString(CARRIER_FIELD));
            entity.setInvoiceDate(rs.getDate(INVOICEDATE_FIELD));
            entity.setPickupDate(rs.getDate(PICKUPDATE_FIELD));
            entity.setOriginZip(rs.getString(ORIGINZIP_FIELD));
            entity.setDestinationZip(rs.getString(DESTINATIONZIP_FIELD));
            entity.setClass1(rs.getBigDecimal(COMMODITYCLASS1_FIELD));
            entity.setWeight1(rs.getBigDecimal(WEIGHT1_FIELD));
            entity.setFuel(rs.getBigDecimal(FUEL_FIELD));
            entity.setCost(rs.getBigDecimal(COST_FIELD));
            entity.setAccessorials(rs.getBigDecimal(ACCESSORIALS_FIELD));
            entity.setTotal(rs.getBigDecimal(TOTAL_FIELD));
            return entity;
        }
    }
}
