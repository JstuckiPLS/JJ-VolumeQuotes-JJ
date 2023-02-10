package com.pls.user.restful.dtobuilder;

import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.user.domain.AnnouncementEntity;
import com.pls.user.domain.enums.AnnouncementStatus;
import com.pls.user.restful.dto.AnnouncementDTO;

/**
 * DTO builder for {@link AnnouncementDTO}.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class AnnouncementDTOBuilder extends AbstractDTOBuilder<AnnouncementEntity, AnnouncementDTO> {

    private AnnouncementDataProvider dataProvider;

    /**
     * Constructor.
     *
     * @param dataProvider
     *            data provider for customer builder.
     */
    public AnnouncementDTOBuilder(AnnouncementDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public AnnouncementDTO buildDTO(AnnouncementEntity entity) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(entity.getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setTheme(entity.getTheme());
        dto.setText(entity.getText());
        return dto;
    }

    @Override
    public AnnouncementEntity buildEntity(AnnouncementDTO dto) {
        AnnouncementEntity result = null;
        if (dto.getId() != null) {
            result = dataProvider.getAnnouncementById(dto.getId());
        } else {
            result = new AnnouncementEntity();
        }
        result.setStatus(AnnouncementStatus.UNPUBLISHED);
        result.setStartDate(dto.getStartDate());
        result.setEndDate(dto.getEndDate());
        result.setTheme(dto.getTheme());
        result.setText(dto.getText());
        return result;
    }

    /**
     * Announcement data provider.
     */
    public interface AnnouncementDataProvider {

        /**
         * Ger announcement by id.
         * 
         * @param announcementId
         *            id of announcement
         * @return {@link AnnouncementEntity}
         */
        AnnouncementEntity getAnnouncementById(Long announcementId);
    }
}
