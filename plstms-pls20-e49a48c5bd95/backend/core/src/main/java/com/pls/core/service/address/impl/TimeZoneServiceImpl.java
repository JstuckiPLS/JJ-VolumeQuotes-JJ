package com.pls.core.service.address.impl;

import com.pls.core.dao.TimeZoneDao;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.service.address.TimeZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link TimeZoneService}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
public class TimeZoneServiceImpl implements TimeZoneService {
    @Autowired
    private TimeZoneDao timeZoneDao;

    @Override
    public List<TimeZoneEntity> findAll() {
        return timeZoneDao.getAll();
    }
}
