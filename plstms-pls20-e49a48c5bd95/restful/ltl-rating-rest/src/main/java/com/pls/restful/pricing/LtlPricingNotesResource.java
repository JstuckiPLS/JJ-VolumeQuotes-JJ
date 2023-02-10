package com.pls.restful.pricing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.common.utils.UserNameBuilder;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.service.validation.ValidationException;
import com.pls.dto.LtlPricingNotesDTO;
import com.pls.dtobuilder.pricing.LtlPricingNotesDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;
import com.pls.ltlrating.service.LtlPricingNotesService;
import com.pls.user.service.UserService;

/**
 * Resource for Pricing Notes.
 * 
 * @author Artem Arapov
 * 
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/profile/{profileId}/notes")
public class LtlPricingNotesResource {

    @Autowired
    private LtlPricingNotesService service;

    @Autowired
    private UserService userService;

    private final LtlPricingNotesDTOBuilder dtoBuilder = new LtlPricingNotesDTOBuilder(new LtlPricingNotesDTOBuilder.UserInfoDataProvider() {

        @Override
        public String findUserNameByPersonId(Long personId) {
            UserEntity userInfo = userService.findByPersonId(personId);
            return UserNameBuilder.buildFullName(userInfo.getFirstName(), userInfo.getLastName());
        }
    });

    /**
     * Get {@link LtlPricingNotesEntity} by specified id.
     * 
     * @param noteId
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlPricingNotesEntity} if it was found, otherwise return <code>null</code>
     */
    @RequestMapping(value = "/{noteId}", method = RequestMethod.GET)
    @ResponseBody
    public LtlPricingNotesEntity getNotesById(@PathVariable("noteId") Long noteId) {
        return service.getNotesById(noteId);
    }

    /**
     * Get list of {@link LtlPricingNotesEntity} by specified profile id.
     * 
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link LtlPricingNotesDTO} if it was found, otherwise return empty
     *         {@link List}.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<LtlPricingNotesDTO> getNotesByProfileId(@PathVariable("profileId") Long profileId) {
        return dtoBuilder.buildList(service.getNotesByProfileId(profileId));
    }

    /**
     * Save Pricing Notes.
     * 
     * @param dto
     *            Not <code>null</code> instance of {@link LtlPricingNotesDTO}.
     * @throws ValidationException
     *             when user validation fails.
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveNotes(@RequestBody LtlPricingNotesDTO dto) throws ValidationException {
        LtlPricingNotesEntity entity = dtoBuilder.buildEntity(dto);
        service.saveNotes(entity);
    }
}
