package com.pls.extint.shared;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * VO to hold the information for the load from the tracking API responses.
 * 
 * @author Pavani Challa
 */
public class TrackingResponseVO extends ApiResponseVO {

    private static final long serialVersionUID = 2137456581459854743L;

    private String carrierRefNum;

    private String bol;

    private Date pickupDate;

    private Date deliveryDate;

    private Long weight;

    private Integer pieces;

    private String currentStatus;

    private String trailer;

    private String freightBillNumber;

    private Date estimatedDeliveryDate;

    private String note;

    private String dateFormat;

    private String timeFormat;

    private String statusHist;

    private BigDecimal pickupTz;

    private BigDecimal deliveryTz;

    /**
     * Constructor that sets the properties like loadId, apiTypeId from request object.
     * 
     * @param requestVO
     *            request object from which properties are set.
     */
    public TrackingResponseVO(TrackingRequestVO requestVO) {
        super(requestVO);
        setOrgId(requestVO.getCarrierOrgId());
        this.carrierRefNum = requestVO.getCarrierRefNum();
        this.weight = requestVO.getWeight();
        this.pieces = requestVO.getPieces();
    }

    public String getCarrierRefNum() {
        return carrierRefNum;
    }

    public void setCarrierRefNum(String carrierRefNum) {
        this.carrierRefNum = carrierRefNum;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the current status of the load.
     * 
     * @param currentStatus
     *            current status of the load
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
        this.statusHist = currentStatus;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getFreightBillNumber() {
        return freightBillNumber;
    }

    public void setFreightBillNumber(String freightBillNumber) {
        this.freightBillNumber = freightBillNumber;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void setStatusHist(String statusHist) {
        this.statusHist = statusHist;
    }

    /**
     * Sets the time for pickup date. Some of the API responses have time sent separately. Pickup date must be set before setting the time.
     * 
     * @param time
     *            time to be set.
     */
    public void setPickupTime(Date time) {
        if (pickupDate != null && time != null) {
            pickupDate = setTime(pickupDate, time);
        }
    }

    /**
     * Sets the time for delivery date. Some of the API responses have time sent separately. Delivery date must be set before setting the time.
     * 
     * @param time
     *            time to be set.
     */
    public void setDeliveryTime(Date time) {
        if (deliveryDate != null && time != null) {
            deliveryDate = setTime(deliveryDate, time);
        }
    }

    public BigDecimal getPickupTz() {
        return pickupTz;
    }

    public void setPickupTz(BigDecimal pickupTz) {
        this.pickupTz = pickupTz;
    }

    public BigDecimal getDeliveryTz() {
        return deliveryTz;
    }

    public void setDeliveryTz(BigDecimal deliveryTz) {
        this.deliveryTz = deliveryTz;
    }

    /**
     * Some of the carriers do not send the pickup and delivery date. They instead send it as part of the status. The date set here is based on the
     * status.
     * 
     * @param value
     *            date to be set
     * @throws ParseException
     *             exception if the date can not be parsed.
     */
    public void setDate(String value) throws ParseException {
        if (!StringUtils.isEmpty(dateFormat) && !StringUtils.isEmpty(currentStatus) && !StringUtils.isEmpty(value)) {
            Date date = new SimpleDateFormat(dateFormat, Locale.getDefault()).parse(value.trim());

            if (ShipmentStatus.IN_TRANSIT.getCode().equals(statusHist)) {
                setPickupDate(date);
            } else if (ShipmentStatus.DELIVERED.getCode().equals(statusHist)) {
                setDeliveryDate(date);
            }
        }
    }

    /**
     * Some of the carriers do not send the pickup and delivery time. They instead send it as part of the status. The time set here is based on the
     * status. Date must be set before setting the time.
     * 
     * @param value
     *            date to be set
     * @throws ParseException
     *             exception if the date can not be parsed.
     */
    public void setTime(String value) throws ParseException {
        if (!StringUtils.isEmpty(timeFormat) && !StringUtils.isEmpty(currentStatus) && !StringUtils.isEmpty(value)) {
            Date time = new SimpleDateFormat(timeFormat, Locale.getDefault()).parse(value.trim());

            if (ShipmentStatus.IN_TRANSIT.getCode().equals(statusHist)) {
                setPickupTime(time);
            } else if (ShipmentStatus.DELIVERED.getCode().equals(statusHist)) {
                setDeliveryTime(time);
            }
        }
    }

    private Date setTime(Date date, Date time) {
        if (date != null && time != null) {
            Calendar calDate = Calendar.getInstance();
            calDate.setTime(date);

            Calendar calTime = Calendar.getInstance();
            calTime.setTime(time);

            calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
            calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
            return calDate.getTime();
        }
        return date;
    }
}
