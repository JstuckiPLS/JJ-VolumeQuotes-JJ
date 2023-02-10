package com.pls.dtobuilder.pricing;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.pls.dto.LtlPricingNotesDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * Builder for conversion between {@link LtlPricingNotesDTO} and {@link LtlPricingNotesEntity}.
 * 
 * @author Artem Arapov
 *
 */
public class LtlPricingNotesDTOBuilder extends AbstractDTOBuilder<LtlPricingNotesEntity, LtlPricingNotesDTO> {

    private UserInfoDataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param userInfoDataProvider data provider for pricing notes builder.
     */
    public LtlPricingNotesDTOBuilder(UserInfoDataProvider userInfoDataProvider) {
        this.dataProvider = userInfoDataProvider;
    }

    @Override
    public LtlPricingNotesDTO buildDTO(LtlPricingNotesEntity bo) {
        LtlPricingNotesDTO dto = new LtlPricingNotesDTO();
        dto.setProfileId(bo.getPricingProfileId());
        dto.setNote(bo.getNotes());
        dto.setCreatedDate(ZonedDateTime.ofInstant(bo.getCreatedDate().toInstant(), ZoneOffset.UTC));
        dto.setCreatedBy(dataProvider.findUserNameByPersonId(bo.getCreatedBy()));

        return dto;
    }

    @Override
    public LtlPricingNotesEntity buildEntity(LtlPricingNotesDTO dto) {
        LtlPricingNotesEntity entity = new LtlPricingNotesEntity();
        entity.setPricingProfileId(dto.getProfileId());
        entity.setNotes(dto.getNote());

        return entity;
    }

    /**
     * User Info Data Provider.
     */
    public interface UserInfoDataProvider {
        /**
         * Find user name by specified person id.
         * 
         * @param personId - id of user.
         * @return - full name of user
         */
        String findUserNameByPersonId(Long personId);
    }
}
