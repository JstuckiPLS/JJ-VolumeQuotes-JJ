package com.pls.ltlrating.batch.migration.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemCostType;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemMarginType;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemServiceType;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemStatus;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.domain.enums.MoveType;

/**
 * Export price item.
 *
 * @author Alex Krychenko.
 */
public class LtlPricingItem implements Serializable {
    private static final long serialVersionUID = 7386144332460059331L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LtlPricingItem.class);

    private static final String COMMODITY_CLASS_PREFIX = "CLASS_";
    private static final String AC_PREFIX = "ac";
    private static final int FAK_PROP_BEGIN_IND = 2;
    private static final String CC_77 = "77";
    private static final String CC_92 = "92";
    private static final String CC_SFX = "_5";

    private Long profileId;
    private Long profileDetailId;
    private String rateName;
    private String profilePriceType;
    private String profileDetailType;
    private String scac;
    private String smc3TariffName;
    private Long itemId;
    private LtlPricingItemType itemType;
    private String itemName;
    private LtlPricingItemCostType costType;
    private String customer;
    private BigDecimal unitCost;
    private BigDecimal costApplMinWt;
    private BigDecimal costApplMaxWt;
    private BigDecimal costApplMinDist;
    private BigDecimal costApplMaxDist;
    private BigDecimal minCost;
    private BigDecimal maxCost;
    private LtlPricingItemMarginType marginType;
    private BigDecimal unitMargin;
    private BigDecimal marginPercent;
    private BigDecimal marginDollarAmt;
    private Date effectiveFrom;
    private Date effectiveTo;
    private LtlPricingItemServiceType serviceType;
    private MoveType movementType;
    private String origin;
    private String destination;
    private BigDecimal rowNum;
    private LtlPricingItemStatus status = LtlPricingItemStatus.ACTIVE;
    private String fak;
    private Long ac50;
    private Long ac55;
    private Long ac60;
    private Long ac65;
    private Long ac70;
    private Long ac77;
    private Long ac85;
    private Long ac92;
    private Long ac100;
    private Long ac110;
    private Long ac125;
    private Long ac150;
    private Long ac175;
    private Long ac200;
    private Long ac250;
    private Long ac300;
    private Long ac400;
    private Long ac500;
    private boolean valid = true;
    private Exception error;
    private String[] origColumns;
    private Long geoServiceId;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(final BigInteger profileId) {
        this.profileId = profileId != null ? profileId.longValue() : null;
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(final BigInteger profileDetailId) {
        this.profileDetailId = profileDetailId != null ? profileDetailId.longValue() : null;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(final BigInteger itemId) {
        this.itemId = itemId != null ? itemId.longValue() : null;
    }

    public LtlPricingItemType getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType == null ? null : LtlPricingItemType.valueOf(itemType);
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(final String rateName) {
        this.rateName = rateName;
    }

    public String getProfilePriceType() {
        return profilePriceType;
    }

    public void setProfilePriceType(final String profilePriceType) {
        this.profilePriceType = profilePriceType;
    }

    public String getProfileDetailType() {
        return profileDetailType;
    }

    public void setProfileDetailType(final String profileDetailType) {
        this.profileDetailType = profileDetailType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(final String scac) {
        this.scac = scac;
    }

    public String getSmc3TariffName() {
        return smc3TariffName;
    }

    public void setSmc3TariffName(final String smc3TariffName) {
        this.smc3TariffName = smc3TariffName;
    }

    public LtlPricingItemCostType getCostType() {
        return costType;
    }

    public void setCostType(final String costType) {
        this.costType = costType == null ? null : LtlPricingItemCostType.valueOf(costType);
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(final BigDecimal unitCost) {
        this.unitCost = roundBigDecimal(unitCost);
    }

    public BigDecimal getCostApplMinWt() {
        return costApplMinWt;
    }

    public void setCostApplMinWt(final BigDecimal costApplMinWt) {
        this.costApplMinWt = roundBigDecimal(costApplMinWt);
    }

    public BigDecimal getCostApplMaxWt() {
        return costApplMaxWt;
    }

    public void setCostApplMaxWt(final BigDecimal costApplMaxWt) {
        this.costApplMaxWt = roundBigDecimal(costApplMaxWt);
    }

    public BigDecimal getCostApplMinDist() {
        return costApplMinDist;
    }

    public void setCostApplMinDist(final BigDecimal costApplMinDist) {
        this.costApplMinDist = roundBigDecimal(costApplMinDist);
    }

    public BigDecimal getCostApplMaxDist() {
        return costApplMaxDist;
    }

    public void setCostApplMaxDist(final BigDecimal costApplMaxDist) {
        this.costApplMaxDist = roundBigDecimal(costApplMaxDist);
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(final BigDecimal minCost) {
        this.minCost = roundBigDecimal(minCost);
    }

    public LtlPricingItemMarginType getMarginType() {
        return marginType;
    }

    public void setMarginType(String marginType) {
        this.marginType = marginType == null ? null : LtlPricingItemMarginType.valueOf(marginType);
    }

    public BigDecimal getUnitMargin() {
        return unitMargin;
    }

    public void setUnitMargin(final BigDecimal unitMargin) {
        this.unitMargin = roundBigDecimal(unitMargin);
    }

    public BigDecimal getMarginPercent() {
        return marginPercent;
    }

    public void setMarginPercent(final BigDecimal marginPercent) {
        this.marginPercent = roundBigDecimal(marginPercent);
    }

    public BigDecimal getMarginDollarAmt() {
        return marginDollarAmt;
    }

    public void setMarginDollarAmt(final BigDecimal marginDollarAmt) {
        this.marginDollarAmt = roundBigDecimal(marginDollarAmt);
    }

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(final Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Date getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(final Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public LtlPricingItemServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null ? null : LtlPricingItemServiceType.valueOf(serviceType);
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(final String movementType) {
        this.movementType = movementType == null ? null : MoveType.valueOf(movementType);
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public BigDecimal getRowNum() {
        return rowNum;
    }

    public void setRowNum(final BigDecimal rowNum) {
        this.rowNum = rowNum;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(final BigDecimal maxCost) {
        this.maxCost = roundBigDecimal(maxCost);
    }

    public String getFak() {
        return fak;
    }

    /**
     * Set fak.
     *
     * @param fak
     *            fak to set
     */
    public void setFak(final String fak) {
        this.fak = fak;
        if (StringUtils.contains(fak, ';')) {
            for (String fakPairString : StringUtils.split(fak, ';')) {
                processFakPair(fakPairString);
            }
        }
    }

    public Long getAc50() {
        return ac50;
    }

    public void setAc50(final Long ac50) {
        this.ac50 = ac50;
    }

    public Long getAc55() {
        return ac55;
    }

    public void setAc55(final Long ac55) {
        this.ac55 = ac55;
    }

    public Long getAc60() {
        return ac60;
    }

    public void setAc60(final Long ac60) {
        this.ac60 = ac60;
    }

    public Long getAc65() {
        return ac65;
    }

    public void setAc65(final Long ac65) {
        this.ac65 = ac65;
    }

    public Long getAc70() {
        return ac70;
    }

    public void setAc70(final Long ac70) {
        this.ac70 = ac70;
    }

    public Long getAc77() {
        return ac77;
    }

    public void setAc77(final Long ac77) {
        this.ac77 = ac77;
    }

    public Long getAc85() {
        return ac85;
    }

    public void setAc85(final Long ac85) {
        this.ac85 = ac85;
    }

    public Long getAc92() {
        return ac92;
    }

    public void setAc92(final Long ac92) {
        this.ac92 = ac92;
    }

    public Long getAc100() {
        return ac100;
    }

    public void setAc100(final Long ac100) {
        this.ac100 = ac100;
    }

    public Long getAc110() {
        return ac110;
    }

    public void setAc110(final Long ac110) {
        this.ac110 = ac110;
    }

    public Long getAc125() {
        return ac125;
    }

    public void setAc125(final Long ac125) {
        this.ac125 = ac125;
    }

    public Long getAc150() {
        return ac150;
    }

    public void setAc150(final Long ac150) {
        this.ac150 = ac150;
    }

    public Long getAc175() {
        return ac175;
    }

    public void setAc175(final Long ac175) {
        this.ac175 = ac175;
    }

    public Long getAc200() {
        return ac200;
    }

    public void setAc200(final Long ac200) {
        this.ac200 = ac200;
    }

    public Long getAc250() {
        return ac250;
    }

    public void setAc250(final Long ac250) {
        this.ac250 = ac250;
    }

    public Long getAc300() {
        return ac300;
    }

    public void setAc300(final Long ac300) {
        this.ac300 = ac300;
    }

    public Long getAc400() {
        return ac400;
    }

    public void setAc400(final Long ac400) {
        this.ac400 = ac400;
    }

    public Long getAc500() {
        return ac500;
    }

    public void setAc500(final Long ac500) {
        this.ac500 = ac500;
    }

    public LtlPricingItemStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : LtlPricingItemStatus.valueOf(status);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    public Exception getError() {
        return error;
    }

    /**
     * Setter for error.
     *
     * @param error
     *            error to set.
     */
    public void setError(final Exception error) {
        this.error = error;
        valid = false;
    }

    public String[] getOrigColumns() {
        return origColumns;
    }

    /**
     * Setter for origin columns.
     *
     * @param origColumns
     *            origin columns to set.
     */
    public void setOrigColumns(final String[] origColumns) {
        if (origColumns != null) {
            this.origColumns = Arrays.copyOf(origColumns, origColumns.length);
        }
    }

    public Long getGeoServiceId() {
        return geoServiceId;
    }

    public void setGeoServiceId(final BigInteger geoServiceId) {
        this.geoServiceId = geoServiceId != null ? geoServiceId.longValue() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LtlPricingItem that = (LtlPricingItem) o;

        return new EqualsBuilder()
                .append(profileId, that.profileId)
                .append(profileDetailId, that.profileDetailId)
                .append(itemType, that.itemType)
                .append(itemName, that.itemName)
                .append(costType, that.costType)
                .append(unitCost, that.unitCost)
                .append(costApplMinWt, that.costApplMinWt)
                .append(costApplMaxWt, that.costApplMaxWt)
                .append(costApplMinDist, that.costApplMinDist)
                .append(costApplMaxDist, that.costApplMaxDist)
                .append(minCost, that.minCost)
                .append(maxCost, that.maxCost)
                .append(marginType, that.marginType)
                .append(unitMargin, that.unitMargin)
                .append(marginPercent, that.marginPercent)
                .append(marginDollarAmt, that.marginDollarAmt)
                .append(effectiveFrom, that.effectiveFrom)
                .append(effectiveTo, that.effectiveTo)
                .append(serviceType, that.serviceType)
                .append(movementType, that.movementType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(profileId)
                .append(profileDetailId)
                .append(itemType)
                .append(itemName)
                .append(costType)
                .append(unitCost)
                .append(costApplMinWt)
                .append(costApplMaxWt)
                .append(costApplMinDist)
                .append(costApplMaxDist)
                .append(minCost)
                .append(maxCost)
                .append(marginType)
                .append(unitMargin)
                .append(marginPercent)
                .append(marginDollarAmt)
                .append(effectiveFrom)
                .append(effectiveTo)
                .append(serviceType)
                .append(movementType)
                .toHashCode();
    }

    /**
     * Generate string with FAK mapping in format ACTUAL_CLASS:MAPPINNG_CLASS;.
     * Example: CLASS_50:CLASS_65;CLASS_77_5:CLASS_92_5
     *
     * @return generated string.
     */
    public String toFakMap() {
        return Arrays.stream(LtlPricingItem.class.getDeclaredFields()).filter(this::filterFAKPropertiesOnly)
                     .sorted((f1, f2) -> Integer.valueOf(f1.getName().substring(FAK_PROP_BEGIN_IND)).compareTo(Integer.valueOf(f2.getName().substring(
                             FAK_PROP_BEGIN_IND))))
                     .map(this::toCommodityMapString).collect(Collectors.joining(";"));
    }

    private void processFakPair(final String fakPairString) {
        if (fakPairString.contains(":")) {
            final String[] fakPair = StringUtils.split(fakPairString, ':');
            if (fakPair.length == FAK_PROP_BEGIN_IND) {
                try {
                    getFakProperty(fakPair[0]).set(this, convertToCommodityClassLong(fakPair[1]));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    LOGGER.debug("Cant set FAK mapping[{}]", fakPair, e);
                }
            }
        }
    }

    private Long convertToCommodityClassLong(final String commodityClass) {
        return Long.valueOf(toCommodityValue(commodityClass));
    }

    private String toCommodityValue(final String commodityClass) {
        return StringUtils.substringBefore(StringUtils.remove(commodityClass, COMMODITY_CLASS_PREFIX), "_");
    }

    private Field getFakProperty(final String fakClass) throws NoSuchFieldException, IllegalAccessException {
        final String propertyName = AC_PREFIX + toCommodityValue(fakClass);
        return LtlPricingItem.class.getDeclaredField(propertyName);
    }

    private boolean filterFAKPropertiesOnly(final Field field) {
        try {
            return field.getName().startsWith(AC_PREFIX) && field.get(this) != null;
        } catch (IllegalAccessException e) {
            LOGGER.warn("Exception on performing filtering FAK properties", e);
        }
        return false;
    }

    private String toCommodityMapString(final Field field) {
        try {
            return toCommodityClass(field.getName().substring(FAK_PROP_BEGIN_IND)) + ":" + toCommodityClass(String.valueOf(field.get(this)));
        } catch (IllegalAccessException e) {
            LOGGER.warn("Exception on converting FAK properties values to string", e);
        }
        return "";
    }

    private String toCommodityClass(final String value) {
        return COMMODITY_CLASS_PREFIX + value + ((CC_77.equals(value) || CC_92.equals(value)) ? CC_SFX : "");
    }

    private BigDecimal roundBigDecimal(BigDecimal unitCost) {
        return unitCost != null ? unitCost.setScale(FAK_PROP_BEGIN_IND, BigDecimal.ROUND_HALF_EVEN) : null;
    }
}
