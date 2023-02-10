package com.pls.core.service.validation;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Job Number validator.
 * 
 * @author Alexander Nalapko
 */
@Component
public class JobNumberValidator extends AbstractValidator<String> {
    private static final Pattern JOB_NUMBER_PATTERN = Pattern
            .compile("((^(BB|BI|BM|BN|BO|BP|BS|FB|FI|FM|FN|FO|FP|FS|W|WM|WME|WMP|WMS|WS)[0-9]{3,4}$)|(^[0-9]{5}[A-Z]{1}$))|(^R[0-9]{4}$)");

    @Override
    protected void validateImpl(String jobNumber) {
        asserts.notNull(jobNumber, "jobNumber");
        asserts.isTrue(JOB_NUMBER_PATTERN.matcher(jobNumber).matches(), "jobNumber", ValidationError.FORMAT);
    }
}
