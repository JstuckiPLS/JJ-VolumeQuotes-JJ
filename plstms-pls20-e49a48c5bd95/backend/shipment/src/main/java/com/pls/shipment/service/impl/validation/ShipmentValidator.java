package com.pls.shipment.service.impl.validation;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.validation.JobNumberValidator;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadJobNumbersEntity;
import com.pls.shipment.domain.LoadMaterialEntity;

/**
 * LTL Load entity validator.
 *
 * @author Andrey Kachur
 */
@Component
public class ShipmentValidator extends AbstractValidator<LoadEntity> {
    private static final String DETAILS_STR = "loadDetails.";
    private static final String MATERIAL_STR = DETAILS_STR + "loadMaterials.";
    private static final String PO_CONSTRAINT = "po";
    private static final int PO_MAX_LENGTH = 50;
    private static final Long SAFWAY_ORG_ID = 206962L;
    private static final int JOB_NUMBER_MAX_LENGTH = 30;

    @Autowired
    private UserPermissionsService permissionsService;

    @Autowired
    JobNumberValidator jobValidator;

    @Override
    protected void validateImpl(LoadEntity entity) {
        validateTopLevelEntity(entity);
        validateLoadDetails(entity.getLoadDetails());
    }

    private void validateLoadDetails(Set<LoadDetailsEntity> details) {
        asserts.notNull(details, "details");
        if (details == null) {
            return;
        }
        for (LoadDetailsEntity detail : details) {
            asserts.notNull(detail.getAddress(), "address");
            validateMaterials(detail.getLoadMaterials());
        }
    }

    private void validateLoadNumbers(LoadEntity entity) {
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getPoNumber(), Capabilities.REQUIRE_SHIPMENT_PO)) {
            asserts.fail("po", ValidationError.IS_EMPTY);
        }
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getPuNumber(), Capabilities.REQUIRE_SHIPMENT_PU)) {
            asserts.fail("pu", ValidationError.IS_EMPTY);
        }
        if (isFieldNotRequiredOrPresent(entity.getNumbers().getRefNumber(), Capabilities.REQUIRE_SHIPMENT_REF)) {
            asserts.fail("ref", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getBolNumber(), Capabilities.REQUIRE_SHIPMENT_BOL)) {
            asserts.fail("bol", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getSoNumber(), Capabilities.REQUIRE_SHIPMENT_SO)) {
            asserts.fail("soNumber", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getGlNumber(), Capabilities.REQUIRE_SHIPMENT_GL)) {
            asserts.fail("glNumber", ValidationError.IS_EMPTY);
        }

        if (isFieldNotRequiredOrPresent(entity.getNumbers().getTrailerNumber(), Capabilities.REQUIRE_SHIPMENT_TRAILER)) {
            asserts.fail("trailer", ValidationError.IS_EMPTY);
        }
    }

    private void validateJobNumbers(Set<LoadJobNumbersEntity> jobs, Long orgId) {
        if (SAFWAY_ORG_ID.equals(orgId)) {
            for (LoadJobNumbersEntity job : jobs) {
                validateComponent(jobValidator, job.getJobNumber(), "jobNumber");
            }
        } else {
            for (LoadJobNumbersEntity job : jobs) {
                if (job.getJobNumber().length() > JOB_NUMBER_MAX_LENGTH) {
                    asserts.fail("jobNumber", ValidationError.FORMAT);
                }
            }
        }
    }

    private boolean isFieldNotRequiredOrPresent(String fieldValue, Capabilities capability) {
        return StringUtils.isBlank(fieldValue) && permissionsService.hasCapability(capability.name());
    }

    private void validateArrivalWindowDate(LoadDetailsEntity entity) {
        Date start = entity.getArrivalWindowStart();
        Date end = entity.getArrivalWindowEnd();

        long millisDifference = end.getTime() - start.getTime();
        long minutesDifference = millisDifference / DateUtils.MILLIS_PER_MINUTE;
        asserts.isTrue(minutesDifference >= 30, "arrivalShipmentDate", ValidationError.LESS_THAN);
    }

    private void validateMaterials(Set<LoadMaterialEntity> materials) {
        if (materials == null) {
            return;
        }

        for (LoadMaterialEntity material : materials) {
            asserts.notNull(material.getCommodityClass(), MATERIAL_STR + "commodityClass");
            asserts.notNull(material.getWeight(), MATERIAL_STR + "weight");

            if (material.isHazmat()) {
                asserts.notEmpty(material.getUnNumber(), "unNumber");
                asserts.notEmpty(material.getHazmatClass(), "hazmatClass");
            }
        }
    }

    private void validateTopLevelEntity(LoadEntity entity) {
        asserts.notNull(entity.getBillTo(), "billTo");
        asserts.notNull(entity.getStatus(), "status");
        asserts.notNull(entity.getOrganization(), "organization");
        asserts.notNull(entity.getOrganization().getId(), "organization");
        if (entity.getBillTo() != null && entity.getOrganization() != null) {
            asserts.isTrue(ObjectUtils.equals(entity.getBillTo().getOrganization().getId(), entity.getOrganization().getId()),
                    "billToOrganization", ValidationError.GREATER_THAN);
        }
        asserts.notNull(entity.getLocationId(), "location");
        asserts.notNull(entity.getPersonId(), "personId");
        validatePONumberLength(entity.getNumbers().getPoNumber());
        validateLoadNumbers(entity);
        if (entity.getNumbers().getJobNumbers() != null) {
            validateJobNumbers(entity.getNumbers().getJobNumbers(), entity.getOrganization().getId());
        }

        asserts.notNull(entity.getOrigin(), "loadDetailsEntity");
        validateArrivalWindowDate(entity.getOrigin());

        String country = null;
        boolean customsBrokerInformationRequired = false;
        for (LoadDetailsEntity detail : entity.getLoadDetails()) {
            if (country == null) {
                country = detail.getAddress().getCountry().getId();
            } else if (country != null && !country.equalsIgnoreCase(detail.getAddress().getCountry().getId())) {
                customsBrokerInformationRequired = true;
                break;
            }
        }
        if (customsBrokerInformationRequired) {
            asserts.notNull(entity.getCustomsBroker(), "customsBroker");
            asserts.notNull(entity.getCustomsBrokerPhone(), "customsBrokerPhone");
        }
    }

    private void validatePONumberLength(String poNumber) {
        if (poNumber != null && poNumber.length() > PO_MAX_LENGTH) {
            asserts.fail(PO_CONSTRAINT, ValidationError.GREATER_THAN);
        }
    }
}
