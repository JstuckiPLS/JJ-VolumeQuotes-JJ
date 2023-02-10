package com.pls.shipment.domain.enums;

import java.util.HashMap;

/**
 * Event types of Load.
 * 
 * @author Artem Arapov
 *
 */
public enum LoadEventType {

    PREAWARD("PREAWARD", 0),
    EM_AWARD("EM.AWARD", 1),
    EM_UNAWARD("EM.UNAWARD", 1),
    SCH_DO("SCH.DO", 2),
    SCH_REDO("SCH.REDO", 2),
    SCH_UNDO("SCH.UNDO", 0),
    SCH_ADSP("SCH.ADSP", 0),
    TRUCKTR("TRUCKTR", 1),
    LD_MS("LD.MS", 0),
    LD_UNMS("LD.UNMS", 0),
    SRR("SRR", 1),
    ASN("ASN", 0),
    SP_UNSET("SP.UNSET", 0),
    SP_SET("SP.SET", 0),
    EM_DECLINE("EM.DECLINE", 1),
    CUSTTRKCHG("CUSTTRKCHG", 5),
    DEST_ARRVL("DEST.ARRVL", 1),
    TE_NEWMKT("TE.NEWMKT", 1),
    TE_NEWPLAN("TE.NEWPLAN", 0),
    TE_NEW_ERR("TE.NEW_ERR", 0),
    LOADCHG("LOADCHG", 3),
    NOTAWARD("NOTAWARD", 1),
    LTL_IAB("LTL.IAB", 2),
    LTL_IAC("LTL.IAC", 2),
    LTL_IAS("LTL.IAS", 2),
    LTL_IRB("LTL.IRB", 2),
    LTL_IRC("LTL.IRC", 2),
    LTL_IRS("LTL.IRS", 2),
    LTL_PRO("LTL.PRO", 0),
    LTL_NOMAT("LTL.NOMAT", 0),
    LTL_ACCT("LTL.ACCT", 0),
    LTL_CC("LTL.CC", 0),
    LTL_PRICE("LTL.PRICE", 0),
    LTL_RATE("LTL.RATE", 0),
    FBH_MP("FBH.MP", 0),
    FBH_MFB("FBH.MFB", 0),
    FBH_DED("FBH.DED", 0),
    EDI_IGNORE("EDI_IGNORE", 1),
    LD_HM("LD.HM", 1),
    LD_BSU("LD.BSU", 2),
    LD_RSN("LD.RSN", 1),
    LD_CUSTCHG("LD.CUSTCHG", 2),
    INV_SHIP("INV.SHIP", 0),
    INV_CARR("INV.CARR", 0),
    LD_AC("LD.AC", 1),
    CARTRKNOTE("CARTRKNOTE", 1),
    LD_AWD("LD.AWD", 2),
    LD_SRC("LD.SRC", 1),
    EDI204_IGNORE("204.IGNORE", 0),
    TRK_EMAIL("TRK.EMAIL", 1),
    LD_MV("LD.MV", 1),
    LD_MV_RSN("LD.MV_RSN", 3),
    LD_ATT("LD.VB_ATT", 0),
    LD_DET("LD.VB_DET", 0),
    DU_MARGIN("DU.MARGIN", 2),
    DU_AMOUNT("DU.AMOUNT", 2),
    DU_COST_DIFF("DU.C.DIFF", 1),
    FS_HOLD("FS.HOLD", 0),
    FS_ALLOW("FS.ALLOW", 0),
    SAVED("SAVED", 1),
    DELETED("DELETED", 1),
    LD_DIS_ERR("LD.DIS_ERR", 2),
    LD_DIS_OK("LD.DIS_OK", 1),
    LD_TRK_ERR("LD.TRK_ERR", 2),
    LD_TRK_OK("LD.TRK_OK", 1),
    LD_CNC_ERR("LD.CNC_ERR", 2),
    LD_CNC_OK("LD.CNC_OK", 1),
    LD_QUOTENUM("LD.QN", 1),
    ;
    
    private static HashMap<String, LoadEventType> dbCodeLookup = new HashMap<>();
    static {
        for (LoadEventType eventType : LoadEventType.values()) {
            dbCodeLookup.put(eventType.getDbCode(), eventType);
        }
    }

    private String dbCode;

    private int requiredFields;

    LoadEventType(String dbCode, int requiredFields) {
        this.dbCode = dbCode;
        this.requiredFields = requiredFields;
    }

    public String getDbCode() {
        return dbCode;
    }

    public int getRequiredFields() {
        return requiredFields;
    }

    public void setDbCode(String dbCode) {
        this.dbCode = dbCode;
    }

    public void setRequiredFields(int requiredFields) {
        this.requiredFields = requiredFields;
    }
    
    public static LoadEventType ofDBCode(String dbCode) {
        return dbCodeLookup.get(dbCode);
    }
}
