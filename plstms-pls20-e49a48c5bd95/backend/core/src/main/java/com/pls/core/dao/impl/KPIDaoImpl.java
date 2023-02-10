package com.pls.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.KPIDao;
import com.pls.core.domain.bo.dashboard.CarrierSummaryReportBO;
import com.pls.core.domain.bo.dashboard.ClassSummaryReportBO;
import com.pls.core.domain.bo.dashboard.CustomerSummaryReportBO;
import com.pls.core.domain.bo.dashboard.DailyLoadActivityBO;
import com.pls.core.domain.bo.dashboard.DestinationReportBO;
import com.pls.core.domain.bo.dashboard.FreightSpendAnalysisReportBO;
import com.pls.core.domain.bo.dashboard.GeographicSummaryReportBO;
import com.pls.core.domain.bo.dashboard.KPIReportFiltersBO;
import com.pls.core.domain.bo.dashboard.ScacBO;
import com.pls.core.domain.bo.dashboard.SeasonalityReportBO;
import com.pls.core.domain.bo.dashboard.ShipmentOverviewReportBO;
import com.pls.core.domain.bo.dashboard.VendorSummaryReportBO;
import com.pls.core.domain.bo.dashboard.WeightAnalysisReportBO;

/**
 * Report DAO impl.
 * 
 * @author Alexander Nalapko
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class KPIDaoImpl implements KPIDao {
    private static final String SEPARATOR = ",";
    private static final String PACKAGE = "PLS_KPI";

    private static final String ORG_ID = "P_ORG_ID";
    private static final String FREIGHT_SPEND_ANALYSIS = "freight_spend_analysis_proc";
    private static final String WEIGHT_ANALYSIS = "weight_analysis_proc";
    private static final String L_REF_CURSOR = "l_ref_cursor";

    @Autowired
    private DataSource dataSource;

    @Override
    public List<DestinationReportBO> getDestinationReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("destination_report_proc")
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<DestinationReportBO>() {

                            @Override
                            public DestinationReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                DestinationReportBO been = new DestinationReportBO();
                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setOrgName(resultSet.getString("customer_name"));
                                been.setDestState(resultSet.getString("dest_state"));
                                been.setOrigState(resultSet.getString("orig_state"));
                                been.setlCount(resultSet.getLong("l_count"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_DEST_STATE", StringUtils.join(filters.getDest(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<DestinationReportBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public List<DailyLoadActivityBO> getDailyLoadActivityReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("daily_load_activity_proc")
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<DailyLoadActivityBO>() {

                            @Override
                            public DailyLoadActivityBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                DailyLoadActivityBO been = new DailyLoadActivityBO();

                                been.setPickup(resultSet.getDate("pickup_date"));
                                been.setWeekday(resultSet.getString("weekday_str"));
                                been.setScac(resultSet.getString("scac"));
                                been.setBound(resultSet.getString("I_O"));
                                been.setClassCode(resultSet.getBigDecimal("class"));

                                been.setOrigState(resultSet.getString("Orig_state"));
                                been.setDestState(resultSet.getString("Dest_state"));
                                been.setCustomer(resultSet.getString("customer_group"));
                                been.setTotal(resultSet.getBigDecimal("total"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue("P_ORG_ID", orgId)
                .addValue("P_PICKUP_DATE", "").addValue("P_SHIP_DATE_MONTH", "")
                .addValue("P_DEST_STATE", StringUtils.join(filters.getDest(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_WEEKDAY", StringUtils.join(filters.getWeekday(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<DailyLoadActivityBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public KPIReportFiltersBO getFilterValues(Long orgId) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("get_filter_values_proc")
                .declareParameters(
                        new SqlOutParameter("l_scac_ref_cursor", Types.REF_CURSOR, new RowMapper<ScacBO>() {
                            @Override
                            public ScacBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                return new ScacBO(resultSet.getString("scac"), resultSet.getString("scac_load_cnt"));
                            }
                        }),
                        new KPISqlOutParameter("l_class_ref_cursor", "commodity_class_code"),
                        new KPISqlOutParameter("l_orig_state_ref_cursor", "orig_state"),
                        new KPISqlOutParameter("l_dest_state_ref_cursor", "dest_state"),
                        new KPISqlOutParameter("l_I_O_ref_cursor", "inbound_outbound_flg"),
                        new KPISqlOutParameter("l_weekday_ref_cursor", "wd_num"),
                        new KPISqlOutParameter("l_year_ref_cursor", "year_num"),
                        new KPISqlOutParameter("l_weight_ref_cursor", "weight"),
                        new KPISqlOutParameter("l_ship_date_month", "ship_date_month"));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId);
        Map<String, Object> resultMap = call.execute(in);

        KPIReportFiltersBO result = new KPIReportFiltersBO();
        result.setScac((List<ScacBO>) resultMap.get("l_scac_ref_cursor"));
        result.setClassCode((List<String>) resultMap.get("l_class_ref_cursor"));
        result.setOrig((List<String>) resultMap.get("l_orig_state_ref_cursor"));
        result.setDest((List<String>) resultMap.get("l_dest_state_ref_cursor"));
        result.setIOFlag((List<String>) resultMap.get("l_I_O_ref_cursor"));
        result.setWeekday((List<String>) resultMap.get("l_weekday_ref_cursor"));
        result.setYear((List<String>) resultMap.get("l_year_ref_cursor"));
        result.setWeight((List<String>) resultMap.get("l_weight_ref_cursor"));
        result.setMonth((List<String>) resultMap.get("l_ship_date_month"));
        return result;
    }

    @Override
    public List<FreightSpendAnalysisReportBO> getFreightSpendAnalysisReports(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall freightProcedureCall = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName(FREIGHT_SPEND_ANALYSIS)
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR,
                                new RowMapper<FreightSpendAnalysisReportBO>() {
                                    @Override
                                    public FreightSpendAnalysisReportBO mapRow(ResultSet resultSet, int rowNum)
                                            throws SQLException {
                                        FreightSpendAnalysisReportBO report = new FreightSpendAnalysisReportBO();
                                        report.setClassCode(resultSet.getBigDecimal("class"));
                                        report.setLoadCount(resultSet.getLong("count(load_id)"));
                                        report.setSummaryCost(resultSet.getBigDecimal("sum(cost)"));
                                        report.setAvarageCost(resultSet.getBigDecimal("avg_cost"));
                                        return report;
                                    }
                                }));
        SqlParameterSource inputParameters = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);
        Map<String, Object> resultMap = freightProcedureCall.execute(inputParameters);
        return (List<FreightSpendAnalysisReportBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public List<WeightAnalysisReportBO> getWeightAnalysisReports(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall weightAnalysisProcedureCall = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName(WEIGHT_ANALYSIS)
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<WeightAnalysisReportBO>() {
                            @Override
                            public WeightAnalysisReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                WeightAnalysisReportBO report = new WeightAnalysisReportBO();
                                report.setLoadId(resultSet.getLong("load_id"));
                                report.setWeight(resultSet.getBigDecimal("weight"));
                                return report;
                            }
                        }));
        SqlParameterSource inputParameters = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_WEIGHT", StringUtils.join(filters.getWeight(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);
        Map<String, Object> resultMap = weightAnalysisProcedureCall.execute(inputParameters);
        return (List<WeightAnalysisReportBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public List<ShipmentOverviewReportBO> getShipmentOverviewReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("shipment_overview_proc")
                .declareParameters(
                        new SqlOutParameter("l_ref_cursor", Types.REF_CURSOR,
                                new RowMapper<ShipmentOverviewReportBO>() {

                                    @Override
                                    public ShipmentOverviewReportBO mapRow(ResultSet resultSet, int rowNum)
                                            throws SQLException {
                                        ShipmentOverviewReportBO been = new ShipmentOverviewReportBO();
                                        been.setOrgID(resultSet.getLong("org_id"));
                                        been.setBound(resultSet.getString("inbound_outbound_flg"));
                                        been.setShipDate(resultSet.getDate("ship_date"));
                                        been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                        been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                        been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                        been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                        been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                        been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                        been.setSavings(resultSet.getBigDecimal("savings"));
                                        been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                        been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                        been.setlCount(resultSet.getLong("lcount"));
                                        return been;
                                    }
                                }));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_DEST_STATE", StringUtils.join(filters.getDest(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<ShipmentOverviewReportBO>) resultMap.get("l_ref_cursor");
    }

    @Override
    public List<GeographicSummaryReportBO> getGeographicSummaryReports(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("geographic_summary_proc")
                .declareParameters(
                        new SqlOutParameter("l_ref_cursor", Types.REF_CURSOR,
                                new RowMapper<GeographicSummaryReportBO>() {

                                    @Override
                                    public GeographicSummaryReportBO mapRow(ResultSet resultSet, int rowNum)
                                            throws SQLException {
                                        GeographicSummaryReportBO summary = new GeographicSummaryReportBO();
                                        summary.setCustomerId(resultSet.getLong("org_id"));
                                        summary.setDestination(resultSet.getString("dest_state"));
                                        summary.setOrigin(resultSet.getString("orig_state"));
                                        summary.setLoadCount(resultSet.getLong("lcount"));
                                        summary.setAverageWeight(resultSet.getBigDecimal("avg_weight"));
                                        summary.setLinehaulRevenue(resultSet.getBigDecimal("lh_rev"));
                                        summary.setFuelRevenue(resultSet.getBigDecimal("fuel_rev"));
                                        summary.setAccessorialRevenue(resultSet.getBigDecimal("acc_rev"));
                                        summary.setSummaryTotal(resultSet.getBigDecimal("sum_total"));
                                        summary.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                        summary.setSavings(resultSet.getBigDecimal("savings"));
                                        summary.setSummaryTotalShipment(resultSet.getBigDecimal("sum_total_shipm"));
                                        return summary;
                                    }
                                }));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_DEST_STATE", StringUtils.join(filters.getDest(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<GeographicSummaryReportBO>) resultMap.get("l_ref_cursor");
    }

    @Override
    public List<CarrierSummaryReportBO> getCarrierSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("carrier_summary_proc")
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<CarrierSummaryReportBO>() {

                            @Override
                            public CarrierSummaryReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                CarrierSummaryReportBO been = new CarrierSummaryReportBO();
                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setScac(resultSet.getString("scac"));
                                been.setShipDate(resultSet.getDate("ship_date"));
                                been.setSavings(resultSet.getBigDecimal("savings"));
                                been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                been.setlCount(resultSet.getLong("lcount"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue("P_ORG_ID", orgId)
                .addValue("P_DEST_STATE", StringUtils.join(filters.getDest(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_SHIP_DATE_MONTH", StringUtils.join(filters.getMonth(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<CarrierSummaryReportBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public List<ClassSummaryReportBO> getClassSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("class_summary_proc")
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<ClassSummaryReportBO>() {

                            @Override
                            public ClassSummaryReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                ClassSummaryReportBO been = new ClassSummaryReportBO();

                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setClassCode(resultSet.getBigDecimal("class"));
                                been.setShipDate(resultSet.getDate("ship_date"));
                                been.setSavings(resultSet.getBigDecimal("savings"));
                                been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                been.setlCount(resultSet.getLong("lcount"));
                                been.setPercentLCount(resultSet.getBigDecimal("percent_of_total"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue("P_ORG_ID", orgId)
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<ClassSummaryReportBO>) resultMap.get(L_REF_CURSOR);
    }

    @Override
    public List<CustomerSummaryReportBO> getCustomerSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("customer_summary_outbound_proc")
                .declareParameters(
                        new SqlOutParameter(L_REF_CURSOR, Types.REF_CURSOR, new RowMapper<CustomerSummaryReportBO>() {

                            @Override
                            public CustomerSummaryReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                CustomerSummaryReportBO been = new CustomerSummaryReportBO();

                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setCustomer(resultSet.getString("customer_name"));
                                been.setDestState(resultSet.getString("dest_state"));
                                been.setPercentLCount(resultSet.getBigDecimal("percent_of_total"));
                                been.setSavings(resultSet.getBigDecimal("savings"));
                                been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                been.setlCount(resultSet.getLong("lcount"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue("P_ORG_ID", orgId)
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_ORIG_STATE", StringUtils.join(filters.getOrig(), SEPARATOR))
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> m = call.execute(in);
        return (List<CustomerSummaryReportBO>) m.get(L_REF_CURSOR);
    }

    @Override
    public List<VendorSummaryReportBO> getVendorSummaryReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("vendor_summary_inbound_proc")
                .declareParameters(
                        new SqlOutParameter("l_ref_cursor", Types.REF_CURSOR, new RowMapper<VendorSummaryReportBO>() {

                            @Override
                            public VendorSummaryReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                VendorSummaryReportBO been = new VendorSummaryReportBO();

                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setOrigName(resultSet.getString("orig_name"));
                                been.setOrigState(resultSet.getString("orig_state"));
                                been.setOrigCity(resultSet.getString("orig_city"));
                                been.setPercentLCount(resultSet.getBigDecimal("percent_of_total"));
                                been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                been.setSavings(resultSet.getBigDecimal("savings"));
                                been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                been.setlCount(resultSet.getLong("lcount"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_SCAC", StringUtils.join(filters.getScac(), SEPARATOR))
                .addValue("P_CLASS", StringUtils.join(filters.getClassCode(), SEPARATOR))
                .addValue("P_I_O_FLAG", StringUtils.join(filters.getIoFlag(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<VendorSummaryReportBO>) resultMap.get("l_ref_cursor");
    }

    @Override
    public List<SeasonalityReportBO> getSeasonalityReport(Long orgId, KPIReportFiltersBO filters) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE)
                .withProcedureName("shipment_seasonality_proc")
                .declareParameters(
                        new SqlOutParameter("l_ref_cursor", Types.REF_CURSOR, new RowMapper<SeasonalityReportBO>() {

                            @Override
                            public SeasonalityReportBO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                SeasonalityReportBO been = new SeasonalityReportBO();

                                been.setOrgID(resultSet.getLong("org_id"));
                                been.setShipDateMonth(resultSet.getInt("ship_date_month"));
                                been.setDestState(resultSet.getString("dest_state"));
                                been.setPercentLCount(resultSet.getBigDecimal("percent_of_total"));
                                been.setWeight(resultSet.getBigDecimal("avg_weight"));
                                been.setSavings(resultSet.getBigDecimal("savings"));
                                been.setLhRev(resultSet.getBigDecimal("lh_rev"));
                                been.setSumTotal(resultSet.getBigDecimal("sum_total"));
                                been.setAccRev(resultSet.getBigDecimal("acc_rev"));
                                been.setFuelRev(resultSet.getBigDecimal("fuel_rev"));
                                been.setBmSavingsPercent(resultSet.getBigDecimal("bm_savings_percent"));
                                been.setShipperBench(resultSet.getBigDecimal("shipper_bench"));
                                been.setSumTotalShipm(resultSet.getBigDecimal("sum_total_shipm"));
                                been.setlCount(resultSet.getLong("lcount"));
                                return been;
                            }
                        }));

        SqlParameterSource in = new MapSqlParameterSource().addValue(ORG_ID, orgId)
                .addValue("P_YEAR", StringUtils.join(filters.getYear(), SEPARATOR))
                .addValue("P_STATUS", StringUtils.join(filters.getStatus(), SEPARATOR))
                .addValue("P_SEPARATOR", SEPARATOR);

        Map<String, Object> resultMap = call.execute(in);
        return (List<SeasonalityReportBO>) resultMap.get("l_ref_cursor");
    }

    private class KPISqlOutParameter extends SqlOutParameter {
        /**
         * Constructor.
         *
         * @param parameterName
         *            name of the parameter, as used in input and output maps
         * @param resultSetColumnName
         *            name of column in the result set
         */
        KPISqlOutParameter(String parameterName, final String resultSetColumnName) {
            super(parameterName, Types.REF_CURSOR, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                    return resultSet.getString(resultSetColumnName);
                }
            });
        }
    }
}
