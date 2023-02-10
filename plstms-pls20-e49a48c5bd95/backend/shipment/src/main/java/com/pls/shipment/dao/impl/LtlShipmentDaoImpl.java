package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.usertype.LoadStatusUserType;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.extint.shared.TrackingResponseVO;
import com.pls.organization.domain.bo.PaperworkEmailBO;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.bo.LocationDetailsReportBO;
import com.pls.shipment.domain.bo.LocationLoadDetailsReportBO;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;

/**
 * {@link LtlShipmentDao} implementation.
 * 
 * @author Alexey Tarasyuk
 * @author Viacheslav Krot
 */
@Repository
@Transactional
public class LtlShipmentDaoImpl extends AbstractDaoImpl<LoadEntity, Long> implements LtlShipmentDao {

    @Override
    public LoadEntity getShipmentWithAllDependencies(Long shipmentId) {
        return (LoadEntity) getCurrentSession().getNamedQuery(LoadEntity.Q_LOAD_BY_ID).setParameter("id", shipmentId).uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentListItemBO> findLastNShipments(Long organizationId, Long personId, int count) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_LAST_N_LOADS_BY_USER);
        query.setLong("customerId", organizationId);
        query.setMaxResults(count);
        query.setResultTransformer(new ShipmentListItemBOResultTransformer());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentListItemBO> findShipmentByEstimatedAndActualDate(RegularSearchQueryBO search, List<ShipmentStatus> statuses, Long userId) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_FIND_SHIPMENT_INFO);
        query.setParameter("orgId", search.getCustomer(), LongType.INSTANCE);
        query.setParameterList("statuses", statuses);
        query.setParameter("dateFrom", search.getFromDate(), DateType.INSTANCE);
        query.setParameter("dateTo", search.getToDate(), DateType.INSTANCE);
        query.setParameter("carrierId", search.getCarrier(), LongType.INSTANCE);
        query.setParameter("bol", search.getBol(), StringType.INSTANCE);
        query.setParameter("pro", search.getPro(), StringType.INSTANCE);
        query.setParameter("originZip", search.getOriginZip(), StringType.INSTANCE);
        query.setParameter("destinationZip", search.getDestinationZip(), StringType.INSTANCE);
        query.setParameter("loadId", search.getLoadId(), LongType.INSTANCE);
        query.setParameter("dateSearchField", search.getDateSearchField(), StringType.INSTANCE);
        query.setParameter("userId", userId, LongType.INSTANCE);
        query.setParameter("job", search.getJob(), StringType.INSTANCE);
        query.setParameter("accountExecutive", search.getAccountExecutive(), LongType.INSTANCE);
        query.setParameter("po", search.getPo(), StringType.INSTANCE);
        query.setResultTransformer(new ShipmentListItemBOResultTransformer());
        return query.list();
    }

    @Override
    public List<ShipmentListItemBO> findShipmentInfo(RegularSearchQueryBO search, Long userId, ShipmentStatus... statuses) {
        return findShipmentByEstimatedAndActualDate(search, Arrays.asList(statuses), userId);
    }

    @Override
    public List<ShipmentListItemBO> findAllShipments(RegularSearchQueryBO search, Long userId) {
        return findShipmentByEstimatedAndActualDate(search, Arrays.asList(ShipmentStatus.values()), userId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CalendarDayBO> getOrganizationCalendarActivity(Long customerId, Date fromDate, Date toDate, List<ShipmentStatus> statuses,
            ShipmentStatus groupByStatus) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        Query query;
        if (groupByStatus == ShipmentStatus.BOOKED) {
            query = getCurrentSession().getNamedQuery(LoadEntity.Q_ACTIVITY_BOOKED);
        } else if (groupByStatus == ShipmentStatus.DELIVERED || groupByStatus == ShipmentStatus.DISPATCHED) {
            query = getCurrentSession().getNamedQuery(LoadEntity.Q_ACTIVITY);
            LoadAction loadDetailsType = groupByStatus == ShipmentStatus.DISPATCHED ? LoadAction.PICKUP : LoadAction.DELIVERY;
            parameters.put("loadDetailsType", loadDetailsType.getLoadAction());
        } else {
            throw new IllegalArgumentException("Can not getUserCalendarActivity. Wrong group by status=" + groupByStatus);
        }
        parameters.put("customerId", customerId);
        parameters.put("fromDate", fromDate);
        parameters.put("toDate", toDate);
        parameters.put("statuses", LoadStatusUserType.getPLS1StatusesForNativeSQL(statuses));

        query.setProperties(parameters).setResultTransformer(Transformers.aliasToBean(CalendarDayBO.class));
        return query.list();
    }

    @Override
    public List<ShipmentTrackingBoardListItemBO> findOpenShipments(Long userId, RegularSearchQueryBO search) {
        return getTrackingBoardListItemBOs(userId, null, search.getFromDate(), search.getToDate(), ShipmentStatus.OPEN);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentTrackingBoardBookedListItemBO> findBookedShipments(Long userId) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_TRACKING_BOARD_BOOKED_AND_PEN_PAY);
        query.setParameter("userId", userId, LongType.INSTANCE);
        query.setParameterList("statuses",
                new ShipmentStatus[] { ShipmentStatus.BOOKED, ShipmentStatus.PENDING_PAYMENT });
        return query
                .setResultTransformer(new AliasToBeanResultTransformer(ShipmentTrackingBoardBookedListItemBO.class)).list();
    }

    @Override
    public List<ShipmentTrackingBoardListItemBO> findUndeliveredShipments() {
        return getTrackingBoardListItemBOs(SecurityUtils.getCurrentPersonId(), null, null, null, ShipmentStatus.DISPATCHED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY);
    }

    @SuppressWarnings("unchecked")
    private List<ShipmentTrackingBoardListItemBO> getTrackingBoardListItemBOs(Long userId, String bol, Date fromDate, Date toDate,
                                                                              ShipmentStatus... statuses) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_TRACKING_BOARD_BOS_BY_CRITERIA);
        query.setParameter("userId", userId, LongType.INSTANCE);
        query.setParameter("bol", bol, StringType.INSTANCE);
        query.setParameter("fromDate", fromDate, DateType.INSTANCE);
        query.setParameter("toDate", toDate, DateType.INSTANCE);
        query.setParameterList("statuses", statuses);

        return query.setResultTransformer(new AliasToBeanResultTransformer(ShipmentTrackingBoardListItemBO.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentListItemBO> findUnbilledShipments(Long userId) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_UNBILLED_LOADS_BY_AE);
        query.setLong("userId", userId);
        query.setParameter("status", ShipmentStatus.DELIVERED);
        query.setResultTransformer(new ShipmentListItemBOResultTransformer());

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoadEntity> findShipmentsByScacAndBolNumber(String scac, String bol) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL);
        query.setParameter("scac", StringUtils.upperCase(scac));
        query.setParameter("bol", StringUtils.upperCase(bol));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadEntity> findAllShipmentsByScacAndProNumber(String scac, String proNumber) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_SCAC_AND_PRO);
        query.setParameter("scac", StringUtils.upperCase(scac));
        query.setParameter("pro", StringUtils.upperCase(proNumber));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadEntity> findShipmentByScacAndBolNumberAndZip(String bol, String scac, String originZip, String destinationZip) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ZIP);
        query.setParameter("bol", StringUtils.upperCase(bol));
        query.setParameter("scac", StringUtils.upperCase(scac));
        query.setParameter("originZip", originZip);
        query.setParameter("destinationZip", destinationZip);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadEntity> findShipmentByScacAndBolNumberAndCityAndState(String bol, String scac, String originCity, String originState,
            String destCity, String destState) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_SCAC_AND_BOL_AND_ADDRESSES);
        query.setParameter("bol", StringUtils.upperCase(bol));
        query.setParameter("scac", StringUtils.upperCase(scac));
        query.setParameter("originCity", StringUtils.upperCase(originCity));
        query.setParameter("originState", StringUtils.upperCase(originState));
        query.setParameter("destinationCity", StringUtils.upperCase(destCity));
        query.setParameter("destinationState", StringUtils.upperCase(destState));
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentListItemBO> findMatchedShipmentsInfo(String bol, String pro,
                String originZip, String destinationZip, Long carrierId, Date actualPickupDate) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_MATCHED_LOAD_INFO);
        query.setParameter("status", ShipmentStatus.DELIVERED);
        query.setParameter("bol", bol, StandardBasicTypes.STRING);
        query.setParameter("pro", pro, StandardBasicTypes.STRING);
        query.setParameter("originZip", originZip, StandardBasicTypes.STRING);
        query.setParameter("destinationZip", destinationZip, StandardBasicTypes.STRING);
        query.setParameter("carrierId", carrierId, StandardBasicTypes.LONG);
        query.setParameter("actualPickupDate", actualPickupDate, StandardBasicTypes.DATE);
        query.setResultTransformer(new ShipmentListItemBOResultTransformer());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentListItemBO> findShipmentsInfo(Long customerId, ShipmentStatus dateRangeStatus, DateRangeQueryBO dateRange,
            ShipmentStatus... statuses) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_FIND_SHIPMENT_INFO_BY_DATE_RANGE);
        query.setParameter("orgId", customerId, LongType.INSTANCE);
        query.setParameterList("statuses", Arrays.asList(statuses));

        boolean booked = dateRangeStatus == ShipmentStatus.BOOKED;
        boolean pickedUp = Arrays.asList(ShipmentStatus.DISPATCHED, ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY).
                contains(dateRangeStatus);
        boolean delivered = dateRangeStatus == ShipmentStatus.DELIVERED;
        boolean cancelled = dateRangeStatus == ShipmentStatus.CANCELLED;

        Date dateFrom = dateRange != null ? dateRange.getFromDate() : null;
        setDateParameter(query, "bookDateFrom", dateFrom, booked);
        setDateParameter(query, "pickupDateFrom", dateFrom, pickedUp);
        setDateParameter(query, "deliveryDateFrom", dateFrom, delivered);
        setDateParameter(query, "scheduledDeliveryDateFrom", dateFrom, cancelled);

        Date dateTo = dateRange != null ? dateRange.getToDate() : null;
        setDateParameter(query, "bookDateTo", dateTo, booked);
        setDateParameter(query, "pickupDateTo", dateTo, pickedUp);
        setDateParameter(query, "deliveryDateTo", dateTo, delivered);
        setDateParameter(query, "scheduledDeliveryDateTo", dateTo, cancelled);

        query.setResultTransformer(new ShipmentListItemBOResultTransformer());
        return query.list();
    }

    private void setDateParameter(Query query, String paramName, Date value, boolean set) {
        query.setParameter(paramName, set ? value : null, TimestampType.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentMissingPaperworkBO> getShipmentsWithMissingReqPaperwork(Long shipmentId) {
        StringBuilder sql = new StringBuilder("select distinct l.load_id \"loadId\", l.shipper_reference_number \"shipperRefNum\", ")
                .append("l.carrier_reference_number \"carrierRefNum\", l.bol \"bol\", corg.org_id \"carrierOrgId\", ")
                .append("l.org_id \"shipperOrgId\", corg.scac \"carrierScac\", r.orig_zip \"originZip\", ")
                .append("r.dest_zip \"destZip\", orig.departure \"pickupDate\" from loads l inner join organizations corg ")
                .append("on l.award_carrier_org_id = corg.org_id inner join load_details orig on l.load_id = orig.load_id ")
                .append("and orig.load_action = 'P' and orig.point_type = 'O' inner join routes r on r.route_id = l.route_id ")
                .append("inner join org_services os on os.org_id = l.award_carrier_org_id and os.imaging = 'API' ")
                .append("inner join BILL_TO_REQ_DOC rd on rd.BILL_TO_ID = l.BILL_TO and rd.status = 'A' and ")
                .append("rd.shipper_req_type in ('REQUIRED', 'ON_AVAIL') left join image_metadata im on im.load_id = l.load_id ")
                .append("and im.status = 'A' and im.document_type = rd.document_type ")
                .append("where l.container_cd = 'VANLTL' and l.ORIGINATING_SYSTEM in ('PLS2_LT', 'GS') and l.load_status = 'CD' ")
                .append("and ( l.cust_req_doc_recv_flag = 'N' or l.cust_req_doc_recv_flag is null) ")
                .append("and ((l.finalization_status = 'NF' or l.finalization_status is null) ")
                .append("or (l.finalization_status = 'ABH' and l.frt_bill_recv_flag = 'Y')) and im.image_meta_id is null ");

        if (shipmentId != null) {
            sql.append(" and l.load_id = ").append(shipmentId);
        }

        Query query = getCurrentSession().createSQLQuery(sql.toString()).addScalar("loadId", StandardBasicTypes.LONG)
                .addScalar("carrierOrgId", StandardBasicTypes.LONG).addScalar("shipperOrgId", StandardBasicTypes.LONG)
                .addScalar("pickupDate", StandardBasicTypes.DATE).addScalar("shipperRefNum", StandardBasicTypes.STRING)
                .addScalar("carrierRefNum", StandardBasicTypes.STRING).addScalar("bol", StandardBasicTypes.STRING)
                .addScalar("carrierScac", StandardBasicTypes.STRING).addScalar("originZip", StandardBasicTypes.STRING)
                .addScalar("destZip", StandardBasicTypes.STRING).setResultTransformer(Transformers.aliasToBean(ShipmentMissingPaperworkBO.class));
        return (List<ShipmentMissingPaperworkBO>) query.list();
    }

    @Override
    public ShipmentStatus getShipmentStatus(Long shipmentId) {
        return (ShipmentStatus) getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_STATUS).setParameter("shipmentId", shipmentId).uniqueResult();
    }

    @Override
    public LoadEntity saveOrUpdate(LoadEntity entity) {
        LoadEntity updatedEntity = super.saveOrUpdate(entity);
        getCurrentSession().flush();
        getCurrentSession().refresh(updatedEntity);
        return updatedEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentTrackingBO> getShipmentsToTrack() {
        StringBuilder sql = new StringBuilder("select distinct l.load_id \"loadId\", l.shipper_reference_number \"shipperRefNum\", ")
                .append("l.carrier_reference_number \"carrierRefNum\", l.bol \"bol\", corg.org_id \"carrierOrgId\", r.orig_zip \"originZip\", ")
                .append("r.dest_zip \"destZip\", orig.early_scheduled_arrival \"pickupDate\", ")
                .append("l.org_id \"shipperOrgId\", corg.scac \"carrierScac\", l.weight \"weight\", l.pieces \"pieces\", ")
                .append("l.load_status \"loadStatus\" from loads l inner join organizations corg on l.award_carrier_org_id = corg.org_id ")
                .append("inner join load_details orig on l.load_id = orig.load_id ")
                .append("and orig.load_action = 'P' and orig.point_type = 'O' inner join routes r on r.route_id = l.route_id ")
                .append("inner join org_services os on os.org_id = l.award_carrier_org_id and os.tracking = 'API' ")
                .append("where l.container_cd = 'VANLTL' and l.ORIGINATING_SYSTEM in ('PLS2_LT', 'GS')  ")
                .append("and l.load_status in ('PP', 'A', 'GA', 'AD', 'DA', 'LD')");

        Query query = getCurrentSession().createSQLQuery(sql.toString()).addScalar("loadId", StandardBasicTypes.LONG)
                .addScalar("carrierOrgId", StandardBasicTypes.LONG).addScalar("shipperOrgId", StandardBasicTypes.LONG)
                .addScalar("shipperRefNum", StandardBasicTypes.STRING).addScalar("weight", StandardBasicTypes.LONG)
                .addScalar("carrierRefNum", StandardBasicTypes.STRING).addScalar("bol", StandardBasicTypes.STRING)
                .addScalar("carrierScac", StandardBasicTypes.STRING).addScalar("pieces", StandardBasicTypes.INTEGER)
                .addScalar("loadStatus", StandardBasicTypes.STRING).addScalar("originZip", StandardBasicTypes.STRING)
                .addScalar("destZip", StandardBasicTypes.STRING).addScalar("pickupDate", StandardBasicTypes.DATE)
                .setResultTransformer(Transformers.aliasToBean(ShipmentTrackingBO.class));

        return (List<ShipmentTrackingBO>) query.list();
    }

    @Override
    public void confirmDelivery(TrackingResponseVO load, Long personId) {
        updateLoad(load.getLoadId(), "CD", load.getCarrierRefNum(), personId);

        updateLoadDetails(load.getLoadId(), load.getDeliveryDate(), personId, false);
    }

    @Override
    public void confirmPickup(TrackingResponseVO load, Long personId) {
        updateLoad(load.getLoadId(), "A", load.getCarrierRefNum(), personId);

        updateLoadDetails(load.getLoadId(), load.getPickupDate(), personId, true);
    }

    @Override
    public CarrierEntity getShipmentCarrier(Long shipmentId) {
        return (CarrierEntity) getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_CARRIER).setParameter("shipmentId", shipmentId).uniqueResult();
    }

    @Override
    public void updateStatus(Long loadId, ShipmentStatus status) {
        getCurrentSession().getNamedQuery(LoadEntity.Q_UPDATE_STATUS).setParameter("newStatus", status)
        .setParameter("modifiedBy", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE).setParameter("loadId", loadId)
                .executeUpdate();
    }

    @Override
    public String getLoadProNumber(Long loadId) {
        return (String) getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_PRO_NUMBER).setParameter("loadId", loadId).uniqueResult();
    };

    private void updateLoad(Long loadId, String loadStatus, String carrierRefNum, Long personId) {
        StringBuilder sql = new StringBuilder("UPDATE LOADS SET LOAD_STATUS = :loadStatus, CARRIER_REFERENCE_NUMBER = ")
                .append("(CASE WHEN CARRIER_REFERENCE_NUMBER IS NULL THEN :carrierRefNum ELSE CARRIER_REFERENCE_NUMBER END), ")
                .append("DATE_MODIFIED = LOCALTIMESTAMP, MODIFIED_BY = :currentUser where LOAD_ID = :loadId");
        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("currentUser", personId).setParameter("loadId", loadId).setParameter("loadStatus", loadStatus)
                .setParameter("carrierRefNum", carrierRefNum);
        query.executeUpdate();
    }

    private void updateLoadDetails(Long loadId, Date departure, Long personId, boolean origin) {
        StringBuilder sql = new StringBuilder("UPDATE LOAD_DETAILS SET DEPARTURE = :departure, arrival = ")
                .append("(case when arrival is null then :departure else arrival end), DATE_MODIFIED = LOCALTIMESTAMP, ")
                .append(" MODIFIED_BY = :currentUser where LOAD_ID = :loadId ");
        if (origin) {
            sql.append(" and LOAD_ACTION = 'P' and POINT_TYPE = 'O'");
        } else {
            sql.append(" and LOAD_ACTION = 'D' and POINT_TYPE = 'D'");
        }

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("departure", departure == null ? new Date() : departure)
                .setParameter("currentUser", personId).setParameter("loadId", loadId);
        query.executeUpdate();
    }

    private class ShipmentListItemBOResultTransformer extends AliasToBeanResultTransformer {
        /**
         * Constructor.
         */
        ShipmentListItemBOResultTransformer() {
            super(ShipmentListItemBO.class);
        }

        private static final long serialVersionUID = 167672060062754106L;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            ShipmentListItemBO listItemBO = (ShipmentListItemBO) super.transformTuple(tuple, aliases);
            listItemBO.init();
            return listItemBO;
        }
    }

    @Override
    public QuotedBO getPrimaryLoadCostDetail(Long loadId) {
        return (QuotedBO) getCurrentSession().getNamedQuery(LoadEntity.Q_GET_PRIMARY_LOAD_COST_DETAIL)
                .setParameter("loadId", loadId).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoadEntity> getLoadForMatchedVendorBill() {
        return (List<LoadEntity>) getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOADS_FOR_MATCHED_VENDOR_BILL)
                .list();
    }

    @Override
    public void updateLoadFinancialStatuses(AuditReasonBO auditReason, ShipmentFinancialStatus loadStatus) {
        if (auditReason.getLoadId() != null) {
            Query loadsQuery = getCurrentSession().getNamedQuery(LoadEntity.Q_UPDATE_LOADS_FINALIZATION_STATUS);
            loadsQuery.setParameterList("loadIds", Collections.singletonList(auditReason.getLoadId()));
            loadsQuery.setParameter("status", loadStatus);
            loadsQuery.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);

            loadsQuery.executeUpdate();
            getCurrentSession().flush();
        }
    }

    @Override
    public LoadEntity findShipmentByShipmentNumber(
            Long customerOrgId, String shipmentNo) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_ORG_ID_AND_SHIPMENT_NO);
        query.setParameter("id", customerOrgId);
        query.setParameter("ref", shipmentNo);
        return (LoadEntity) query.uniqueResult();
    }
    
    @Override
    public List<LoadEntity> findShipmentsByShipmentNumber(
            Long customerOrgId, String shipmentNo) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_ORG_ID_AND_SHIPMENT_NO);
        query.setParameter("id", customerOrgId);
        query.setParameter("ref", shipmentNo);
        return query.list();
    }

    @Override
    public LoadEntity findShipmentByBolAndShipmentNumber(Long customerOrgId, String shipmentNo, String bol) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOAD_BY_REF_NO_AND_BOL);
        query.setParameter("id", customerOrgId);
        query.setParameter("ref", shipmentNo);
        query.setParameter("bol", bol);
        return (LoadEntity) query.uniqueResult();
    }

    @Override
    public Long getShipmentBillTo(Long shipmentId) {
        Preconditions.checkArgument(shipmentId != null);

        Query query = getCurrentSession().getNamedQuery(LoadEntity.GET_SHIPMENT_BILL_TO);
        query.setLong("id", shipmentId);

        return (Long) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigDecimal> findMatchedLoadsByProAndOrgId(String proNum, Long orgId) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_GET_MATCHED_LOADS_BY_PRO_AND_ORG_ID);
        query.setParameter("pronum", proNum);
        query.setParameter("orgId", orgId);
        return query.list();
    }

    @Override
    public InvoiceType getLoadInvoiceType(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.GET_LOAD_INVOICE_TYPE);
        query.setParameter("loadId", loadId);
        return (InvoiceType) query.uniqueResult();
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentListItemBO> findHoldShipments() {
        Query query = getCurrentSession().getNamedQuery(LoadEntity.Q_FIND_HOLD_SHIPMENT);
        query.setParameter("userId", SecurityUtils.getCurrentPersonId(), LongType.INSTANCE);
        query.setParameter("status", ShipmentFinancialStatus.FINANCE_HOLD);
        return query.setResultTransformer(new AliasToBeanResultTransformer(ShipmentListItemBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PaperworkEmailBO> getPaperworkEmails(int days) {
        return getCurrentSession().getNamedQuery(LoadEntity.GET_PAPERWORK_EMAILS)
                .setParameter("days", days).setResultTransformer(new AliasToBeanResultTransformer(PaperworkEmailBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LocationDetailsReportBO> getLocationDetails(Long personId) {
        return getCurrentSession().getNamedQuery(LoadEntity.Q_GET_PICKUPS_AND_DELIVERIES)
                .setParameter("pickupStatus", ShipmentStatus.DISPATCHED)
                .setParameterList("deliveryStatuses", Arrays.asList(ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY))
                .setParameter("userId", personId)
                .setResultTransformer(new AliasToBeanResultTransformer(LocationDetailsReportBO.class))
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LocationLoadDetailsReportBO> getLocationLoadDetails(String zip, String city, Boolean origin, int dateType, Long personId) {
        List<ShipmentStatus> statuses = new ArrayList<>();
        if (zip == null || BooleanUtils.isTrue(origin)) {
            statuses.add(ShipmentStatus.DISPATCHED);
        }
        if (zip == null || BooleanUtils.isFalse(origin)) {
            statuses.add(ShipmentStatus.IN_TRANSIT);
            statuses.add(ShipmentStatus.OUT_FOR_DELIVERY);
        }
        return getCurrentSession().getNamedQuery(LoadEntity.Q_GET_LOADS_PICKUPS_AND_DELIVERIES)
                .setParameterList("statuses", statuses)
                .setParameter("userId", personId, LongType.INSTANCE)
                .setParameter("dateType", dateType, IntegerType.INSTANCE)
                .setParameter("zip", zip, StringType.INSTANCE)
                .setParameter("city", city, StringType.INSTANCE)
                .setParameter("pickupStatus", ShipmentStatus.DISPATCHED)
                .setParameterList("deliveryStatuses", Arrays.asList(ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY))
                .setResultTransformer(new AliasToBeanResultTransformer(LocationLoadDetailsReportBO.class))
                .list();
    }
}
