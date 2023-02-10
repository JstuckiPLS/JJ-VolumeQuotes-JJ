package com.pls.user.restful.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for user customer locations and notifications.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerLocationUserDTO {
    private Long locationId;
    private final List<String> notifications = new ArrayList<String>();

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public List<String> getNotifications() {
        return notifications;
    }
}
