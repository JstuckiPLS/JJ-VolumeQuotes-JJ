package com.pls.dtobuilder.shipment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.pls.dto.shipment.ShipmentEventDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.shipment.domain.bo.LoadTrackingBO;

/**
 * DTO builder for shipment tracking.
 *
 * @author Mikhail Boldinov, 11/03/14
 */
public class ShipmentTrackingDTOBuilder extends AbstractDTOBuilder<LoadTrackingBO, ShipmentEventDTO> {
    public static final String DEFAULT_TIMEZONE_CODE = "ET";

    @Override
    public ShipmentEventDTO buildDTO(LoadTrackingBO entity) {
        ShipmentEventDTO dto = new ShipmentEventDTO();
        dto.setDate(LocalDateTime.ofInstant(entity.getTrackingDate().toInstant(), ZoneId.systemDefault()));
        dto.setTimezoneCode(StringUtils.defaultString(entity.getTimezoneCode(), DEFAULT_TIMEZONE_CODE));
        if (entity.getSource() != null) {
            dto.setEvent(getEventMessage(entity));
        }
        dto.setFirstName(entity.getUserFirstName());
        dto.setLastName(entity.getUserLastName());
        dto.setShipmentId(entity.getShipmentId());
        dto.setCity(entity.getCity());
        dto.setStateCode(entity.getState());
        dto.setCountryCode(entity.getCountry());
        dto.setCarrierName(entity.getCarrierName());
        return dto;
    }

    private String getEventMessage(LoadTrackingBO entity) {
        switch (entity.getSource().intValue()) {
        case 204:
            return get204EDIMessage(entity);
        case 214:
            return get214EDIMessage(entity);
        case 990:
            return entity.getFreeMessage();
        case 997:
            return entity.getFreeMessage();
        case 211:
            return get211EDIMessage(entity);
        case 44:
            return getLtlLifecycleMessage(entity);
        default:
            return getDefaultEDIMessage(entity);
        }
    }

    private String getLtlLifecycleMessage(LoadTrackingBO entity) {
        return getDefaultEDIMessage(entity) + (entity.getFreeMessage() != null ? " @ " + entity.getFreeMessage() : "");
    }

    private String get214EDIMessage(LoadTrackingBO entity) {
        String defaultMessage = getDefaultEDIMessage(entity);
        if (!StringUtils.equalsIgnoreCase("AG", entity.getStatusCode()) || entity.getDepartureTime() == null) {
            return defaultMessage;
        }
        StringJoiner result = new StringJoiner(" ");
        result.add(defaultMessage);
        result.add(DateFormatUtils.format(entity.getDepartureTime(), "MM/dd/yyyy h:mm a"));
        return result.toString();
    }

    private String get204EDIMessage(LoadTrackingBO entity) {
        StringBuilder msg = new StringBuilder(18);
        if (StringUtils.isNotEmpty(entity.getFreeMessage())) {
            msg.append(entity.getFreeMessage()).append(" @ ");
        } else if (StringUtils.isNotEmpty(entity.getStatusDescription())) {
            msg.append(entity.getStatusDescription()).append(" @ ");
        } else {
            msg.append("EDI 204 created @ ");
        }
        msg.append(DateFormatUtils.format(entity.getTrackingDate(), "MM/dd/yyyy h:mm a"));

        return msg.toString();
    }

    private String getDefaultEDIMessage(LoadTrackingBO entity) {
        StringBuilder msg = new StringBuilder();
        if (entity.getCarrierName() != null) {
            msg.append('(').append(entity.getCarrierName()).append(") ");
        }
        if (entity.getCity() != null && entity.getState() != null) {
            msg.append(entity.getCity()).append(", ").append(entity.getState()).append(' ');
        }
        if (msg.length() > 0) {
            msg.append("- ");
        }
        msg.append(entity.getStatusDescription());

        return msg.toString();
    }

    private String get211EDIMessage(LoadTrackingBO entity) {
        StringBuilder msg = new StringBuilder(18);
        if (StringUtils.isNotEmpty(entity.getFreeMessage())) {
           msg.append(entity.getFreeMessage()).append(" @ ");
        } else if (StringUtils.isNotEmpty(entity.getStatusDescription())) {
            msg.append(entity.getStatusDescription()).append(" @ ");
        } else {
            msg.append("EDI 211 created @ ");
        }
        msg.append(DateFormatUtils.format(entity.getTrackingDate(), "MM/dd/yyyy h:mm a"));

        return msg.toString();
    }

    /**
     * This method throws {@link UnsupportedOperationException}.
     *
     * @param shipmentEventDTO
     *            {@link ShipmentEventDTO}
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public LoadTrackingBO buildEntity(ShipmentEventDTO shipmentEventDTO) {
        throw new UnsupportedOperationException();
    }
}
