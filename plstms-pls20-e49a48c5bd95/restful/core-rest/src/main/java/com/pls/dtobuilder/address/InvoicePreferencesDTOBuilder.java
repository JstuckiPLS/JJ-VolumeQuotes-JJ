package com.pls.dtobuilder.address;

import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.enums.InvoiceProcessingType;
import com.pls.core.domain.enums.InvoiceType;
import com.pls.core.domain.organization.InvoiceSettingsEntity;
import com.pls.dto.address.InvoicePreferencesDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO Builder for conversion {@link InvoiceSettingsEntity} to {@link InvoicePreferencesDTO} and vice versa.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class InvoicePreferencesDTOBuilder extends AbstractDTOBuilder<InvoiceSettingsEntity, InvoicePreferencesDTO> {

    private final DataProvider dataProvider;
    private final TimezoneDTOBuilder timezoneDTOBuilder = new TimezoneDTOBuilder();

    /**
     * Constructor.
     *
     * @param dataProvider data provider of existing data to update
     */
    public InvoicePreferencesDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public InvoicePreferencesDTO buildDTO(final InvoiceSettingsEntity entity) {
        InvoicePreferencesDTO dto = new InvoicePreferencesDTO();
        dto.setId(entity.getId());

        dto.setInvoiceType(entity.getInvoiceType());
        dto.setProcessingDayOfWeek(entity.getProcessingDayOfWeek());
        if (entity.getProcessingTime() != null) {
            dto.setProcessingTimeInMinutes(entity.getProcessingTime());
        }

        TimeZoneEntity processingTimeTimezone = entity.getProcessingTimeTimezone();
        if (processingTimeTimezone != null) {
            dto.setProcessingTimezone(timezoneDTOBuilder.buildDTO(processingTimeTimezone));
        }
        dto.setProcessingType(entity.getProcessingType());
        dto.setProcessingPeriod(entity.getProcessingPeriod());
        dto.setSortType(entity.getSortType());
        dto.setDocuments(entity.getDocuments());
        dto.setGainshareOnly(entity.isGainshareOnly());
        dto.setEdiInvoice(entity.isEdiInvoice());
        dto.setNotSplitRecipients(entity.isNotSplitRecipients());
        dto.setNoInvoiceDocument(entity.isNoInvoiceDocument());
        dto.setCbiInvoiceType(entity.getCbiInvoiceType());
        dto.setReleaseDayOfWeek(entity.getReleaseDayOfWeek());

        return dto;
    }

    @Override
    public InvoiceSettingsEntity buildEntity(InvoicePreferencesDTO dto) {
        InvoiceSettingsEntity entity;
        if (dataProvider != null && dataProvider.getInvoiceSettings(dto.getId()) != null) {
            entity = dataProvider.getInvoiceSettings(dto.getId());
        } else {
            entity = new InvoiceSettingsEntity();
        }
        entity.setInvoiceType(dto.getInvoiceType());
        entity.setGainshareOnly(dto.isGainshareOnly());
        entity.setEdiInvoice(dto.isEdiInvoice());
        entity.setProcessingDayOfWeek(dto.getProcessingDayOfWeek());
        if (dto.getInvoiceType() == InvoiceType.TRANSACTIONAL) {
            entity.setProcessingType(InvoiceProcessingType.AUTOMATIC);
        } else {
            entity.setProcessingType(dto.getProcessingType());
        }
        entity.setProcessingTime(dto.getProcessingTimeInMinutes());
        entity.setProcessingPeriod(dto.getProcessingPeriod());
        entity.setSortType(dto.getSortType());
        entity.setDocuments(dto.getDocuments());
        entity.setNotSplitRecipients(dto.isNotSplitRecipients());
        entity.setNoInvoiceDocument(dto.isNoInvoiceDocument());
        entity.setCbiInvoiceType(dto.getCbiInvoiceType());
        entity.setReleaseDayOfWeek(dto.getReleaseDayOfWeek());

        if (dto.getProcessingTimezone() != null) {
            Byte processingTimezoneLocalOffset = dto.getProcessingTimezone().getLocalOffset();
            TimeZoneEntity processingTimeTimezone = new TimeZoneEntity();
            processingTimeTimezone.setLocalOffset(processingTimezoneLocalOffset);
            entity.setProcessingTimeTimezone(processingTimeTimezone);
        }

        return entity;
    }

    /**
     * Data provider for update.
     */
    public interface DataProvider {
        /**
         * Get existing invoice settings.
         * @param id of invoice settings
         *
         * @return {@link InvoiceSettingsEntity}
         */
        InvoiceSettingsEntity getInvoiceSettings(Long id);
    }
}
