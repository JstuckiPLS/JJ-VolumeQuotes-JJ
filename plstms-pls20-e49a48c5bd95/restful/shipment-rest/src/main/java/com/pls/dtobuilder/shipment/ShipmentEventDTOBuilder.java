package com.pls.dtobuilder.shipment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pls.dto.shipment.ShipmentEventDTO;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * DTO Builder for shipment event.
 * 
 * @author Gleb Zgonikov
 */
public class ShipmentEventDTOBuilder {

    /**
     * Build list of business objects with shipment events to list of data transfer objects.
     * 
     * @param bos
     *            {@link List} of business objects
     * @return {@link Collection} of data transfer objects
     */
    public Collection<ShipmentEventDTO> buildList(List<ShipmentEventBO> bos) {
        Map<Long, ShipmentEventDTO> resultMap = new HashMap<Long, ShipmentEventDTO>();
        for (ShipmentEventBO bo : bos) {
            ShipmentEventDTO dto;
            if (resultMap.containsKey(bo.getEventId())) {
                dto = resultMap.get(bo.getEventId());
            } else {
                dto = buildShipmentEventDTO(bo);
                resultMap.put(bo.getEventId(), dto);
            }
            if (bo.getOrdinal() != null && bo.getData() != null) {
                dto.setEvent(replaceDataPlaceholder(dto.getEvent(), bo.getOrdinal(), bo.getData()));
            }
        }
        return resultMap.values();
    }

    private ShipmentEventDTO buildShipmentEventDTO(ShipmentEventBO bo) {
        ShipmentEventDTO dto = new ShipmentEventDTO();
        dto.setDate(LocalDateTime.ofInstant(bo.getCreatedDate().toInstant(), ZoneId.systemDefault()));
        dto.setTimezoneCode(ShipmentTrackingDTOBuilder.DEFAULT_TIMEZONE_CODE);
        dto.setEvent(bo.getDescription());
        dto.setFirstName(bo.getFirstName());
        dto.setLastName(bo.getLastName());
        dto.setShipmentId(bo.getLoadId());
        return dto;
    }

    private String replaceDataPlaceholder(String event, byte ordinal, String data) {
        StringBuilder placeHolder = new StringBuilder("{").append(ordinal).append("}");
        return event.replace(placeHolder.toString(), data);
    }
}
