package com.pls.restful.address;

import com.pls.core.service.address.TimeZoneService;
import com.pls.dto.address.TimezoneDTO;
import com.pls.dtobuilder.address.TimezoneDTOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * REST resource for handling request for timezones.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/timezone")
public class TimezoneResource {
    private static final TimezoneDTOBuilder TIMEZONE_DTO_BUILDER = new TimezoneDTOBuilder();

    @Autowired
    private TimeZoneService timeZoneService;

    /**
     * Get all available timezones.
     *
     * @return list of {@link TimezoneDTO}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<TimezoneDTO> getAllTimezones() {
        return TIMEZONE_DTO_BUILDER.buildList(timeZoneService.findAll());
    }
}
