package com.pls.core.service.address;

import com.pls.core.domain.TimeZoneEntity;

import java.util.List;

/**
 * Service for time zones handling.
 *
 * @author Denis Zhupinsky (Team International)
 */
public interface TimeZoneService {
    /**
     * Find all timezones.
     *
     * @return list of {@link TimeZoneEntity}
     */
    List<TimeZoneEntity> findAll();
}
