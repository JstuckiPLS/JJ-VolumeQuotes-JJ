package com.pls.ltlrating.shared;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlAccGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlAccGeoServicesEntity;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.enums.DistanceUOM;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.domain.enums.LtlCostType;
import com.pls.ltlrating.domain.enums.WeightUOM;
import com.pls.ltlrating.service.impl.GeoHelper;
//import com.pls.core.domain.PlainModificationObject;

/**
 * Test values.
 *
 * @author Somebody One
 *
 */
public final class TestDataHelper {

    private TestDataHelper() {

    }

    public static final Long LTL_ACCESSORIAL_ID_1 = 1L;
    public static final Long LTL_ACCESSORIAL_ID_2 = 2L;

    public static final Long INACTIVE_LTL_ACCESSORIAL_ID_11 = 11L;
    public static final Long INACTIVE_LTL_ACCESSORIAL_ID_12 = 12L;

    public static final Long LTL_PRIC_PROF_DETAIL_ID_1 = 1000L;
    public static final Long LTL_PRIC_PROF_DETAIL_ID_2 = 1001L;

    public static final String ACCESSORIAL_TYPE_LIFT_GATE = "LFT";
    public static final String ACCESSSORIAL_LIFT_GATE_DESC = "Lift-gate";
    public static final String ACCESSORIAL_TYPE_RESIDENTIAL = "REP";
    public static final String ACCESSORIAL_TYPE_INSIDE_DELIVERY = "IDL";
    public static final String ACCESSORIAL_TYPE_INSIDE_PICKUP = "IPU";
    public static final String ACCESSORIAL_TYPE_APPOINTMENT = "APT";

    public static final String BLOCKED_YES = "Y";
    public static final String BLOCKED_NO = "N";

    public static final LtlCostType COST_TYPE_FLAT_FEE = LtlCostType.FL;
    public static final LtlCostType COST_TYPE_PER_100_WT = LtlCostType.CW;
    public static final LtlCostType COST_TYPE_PER_PIECE = LtlCostType.PE;
    public static final LtlCostType COST_TYPE_PER_MILE = LtlCostType.MI;

    public static final BigDecimal UNIT_COST = new BigDecimal("10.50");

    public static final Long COST_APPL_MIN_WT = 100L;
    public static final Long COST_APPL_MAX_WT = 500L;
    public static final WeightUOM COST_APPL_WT_UOM_LB = WeightUOM.LB;
    public static final WeightUOM COST_APPL_WT_UOM_KG = WeightUOM.KG;

    public static final Long COST_APPL_MIN_DIST = 50L;
    public static final Long COST_APPL_MAX_DIST = 250L;
    public static final DistanceUOM COST_APPL_DIST_UOM_MI = DistanceUOM.ML;
    public static final DistanceUOM COST_APPL_DIST_UOM_KM = DistanceUOM.KM;

    public static final String MARGIN_TYPE_FLAT_FEE = "FL";
    public static final String MARGIN_TYPE_PER_100_WT = "CW";
    public static final String MARGIN_TYPE_PER_MILE = "MI";
    public static final String MARGIN_TYPE_PERCENT = "PC";

    public static final BigDecimal UNIT_MARGIN = new BigDecimal("2.50");
    public static final BigDecimal MARGIN_PERCENT = new BigDecimal("7.50");

    public static final BigDecimal MARGIN_DOLLAR_AMT = new BigDecimal("150.50");
    public static final BigDecimal MIN_COST = new BigDecimal("100.50");
    public static final Date EFF_DATE = new Date();
    public static final Date EXP_DATE = new Date();

    public static final String NOTES = "This is test notes for accessorials";
    public static final Status STATUS_ACTIVE = Status.ACTIVE;
    public static final Status STATUS_INACTIVE = Status.INACTIVE;

    public static final Long CURRENT_USER_PERSON_ID = 1L;
    public static final Long MODIFIED_USER_PERSON_ID = 1L;

    public static final AccessorialTypeEntity LIFT_GATE_ACCESSORIAL = createAccessorialType();

    /**
     * Create LTL Accessorial.
     * @return LtlAccessorialsEntity object
     */
    public static LtlAccessorialsEntity createLTLAccessorial() {
        LtlAccessorialsEntity entity = new LtlAccessorialsEntity();

        entity.setLtlPricProfDetailId(LTL_PRIC_PROF_DETAIL_ID_1);
        entity.setAccessorialType(LIFT_GATE_ACCESSORIAL.getId());
        entity.setAccessorialTypeEntity(LIFT_GATE_ACCESSORIAL);
        entity.setBlocked(BLOCKED_NO);
        entity.setCostType(COST_TYPE_FLAT_FEE);
        entity.setUnitCost(UNIT_COST);
        entity.setCostApplMinWt(COST_APPL_MIN_WT);
        entity.setCostApplMaxWt(COST_APPL_MAX_WT);
        entity.setCostApplWtUom(COST_APPL_WT_UOM_LB);
        entity.setCostApplMinDist(COST_APPL_MIN_DIST);
        entity.setCostApplMaxDist(COST_APPL_MAX_DIST);
        entity.setCostApplDistUom(COST_APPL_DIST_UOM_MI);
        entity.setMarginType(MARGIN_TYPE_FLAT_FEE);
        entity.setUnitMargin(UNIT_MARGIN);
        entity.setMarginPercent(MARGIN_PERCENT);
        entity.setMarginDollarAmt(MARGIN_DOLLAR_AMT);
        entity.setMinCost(MIN_COST);
        entity.setEffDate(EFF_DATE);
        entity.setExpDate(null);
        entity.setNotes(NOTES);
        entity.setStatus(STATUS_ACTIVE);

        entity.setLtlAccGeoServicesEntities(createLTLAccGeoServices());

        return entity;
    }

    private static AccessorialTypeEntity createAccessorialType() {
        AccessorialTypeEntity accessorial = new AccessorialTypeEntity(ACCESSORIAL_TYPE_LIFT_GATE);
        accessorial.setDescription(ACCESSSORIAL_LIFT_GATE_DESC);

        return accessorial;
    }

    /**
     * Creates geo service entities for tests.
     *
     * @return list of LtlAccGeoServicesEntity
     */
    public static List<LtlAccGeoServicesEntity> createLTLAccGeoServices() {
        List<LtlAccGeoServicesEntity> accGeoServices = new ArrayList<LtlAccGeoServicesEntity>();

        LtlAccGeoServicesEntity entity1 = new LtlAccGeoServicesEntity();
        entity1.setOrigin("IL,617,61761-61736");
        entity1.setDestination("15237-15200,PA");
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity1, "IL", GeoType.ORIGIN, 6, "IL"));
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity1, "617", GeoType.ORIGIN, 2, GeoHelper.getGeoServType("617")
                        .getRight()));
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity1, "61761-61736", GeoType.ORIGIN, 3, GeoHelper.getGeoServType(
                        "61761-61736").getRight()));
        entity1.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity1, "15237-15200", GeoType.DESTINATION, 3, GeoHelper
                        .getGeoServType("15237-15200").getRight()));
        entity1.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity1, "PA", GeoType.DESTINATION, 6, "PA"));
        accGeoServices.add(entity1);

        LtlAccGeoServicesEntity entity2 = new LtlAccGeoServicesEntity();
        entity2.setOrigin("07620");
        entity2.setDestination("94027");
        entity2.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity2, "07620", GeoType.ORIGIN, 1, GeoHelper
                        .getGeoServType("07620").getRight()));
        entity2.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity2, "94027", GeoType.DESTINATION, 1, GeoHelper.getGeoServType(
                        "94027").getRight()));
        accGeoServices.add(entity2);

        LtlAccGeoServicesEntity entity3 = new LtlAccGeoServicesEntity();
        entity3.setOrigin("NY");
        entity3.setDestination("CA");
        entity3.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity3, "NY", GeoType.ORIGIN, 6, "NY"));
        entity3.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity3, "CA", GeoType.DESTINATION, 6, "CA"));
        accGeoServices.add(entity3);

        LtlAccGeoServicesEntity entity4 = new LtlAccGeoServicesEntity();
        entity4.setOrigin("IL,52531");
        entity4.setDestination("15237-15200,PA");
        entity4.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity4, "IL", GeoType.ORIGIN, 6, "IL"));
        entity4.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity4, "15237-15200", GeoType.DESTINATION, 3, GeoHelper
                        .getGeoServType("15237-15200").getRight()));
        entity4.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity4, "PA", GeoType.DESTINATION, 6, "PA"));
        accGeoServices.add(entity4);

        LtlAccGeoServicesEntity entity5 = new LtlAccGeoServicesEntity();
        entity5.setOrigin("IA");
        entity5.setDestination("15237-15200,PA");
        entity5.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity5, "IA", GeoType.ORIGIN, 6, "IA"));
        entity5.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity5, "15237-15200", GeoType.DESTINATION, 3, GeoHelper
                        .getGeoServType("15237-15200").getRight()));
        entity5.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity5, "PA", GeoType.DESTINATION, 6, "PA"));
        accGeoServices.add(entity5);

        LtlAccGeoServicesEntity entity6 = new LtlAccGeoServicesEntity();
        entity6.setOrigin("IL,617,61761-61736");
        entity6.setDestination("MT, 59007");
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity6, "IL", GeoType.ORIGIN, 6, "IL"));
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity6, "617", GeoType.ORIGIN, 2, GeoHelper.getGeoServType("617")
                        .getRight()));
        entity1.getLtlAccOriginGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity6, "61761-61736", GeoType.ORIGIN, 3, GeoHelper.getGeoServType(
                        "61761-61736").getRight()));
        entity1.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity6, "59007", GeoType.DESTINATION, 1, GeoHelper.getGeoServType(
                        "59007").getRight()));
        entity1.getLtlAccDestGeoServiceDetails().add(
                new LtlAccGeoServiceDetailsEntity(entity6, "MT", GeoType.DESTINATION, 6, "MT"));
        accGeoServices.add(entity6);

        // TODO: Remove after creating hibernate hooks that sets modification object
        PlainModificationObject modification = new PlainModificationObject();
        modification.setCreatedBy(1L);
        modification.setCreatedDate(new Date());
        for (LtlAccGeoServicesEntity e : accGeoServices) {
            e.setModification(modification);
        }

        return accGeoServices;
    }

}
