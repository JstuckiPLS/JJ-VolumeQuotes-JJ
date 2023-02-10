package com.pls.user.restful;

import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.domain.user.Capabilities;
import com.pls.core.service.UserPermissionsService;
import com.pls.user.service.TermsAndConditionsService;

/**
 * Terms and Conditions REST resource.
 *
 * @author Brichak Aleksandr
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/termsAndConditions")
public class TermsAndConditionsResource {

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @Autowired
    private UserPermissionsService permissionsService;

    private static final String ACCEPTED_MSG = "You have already accepted Terms and Conditions.";

    /**
     * Apply Terms and Conditions.
     * 
     * @throws ValidationException
     *             if Terms and Conditions already accepted.
     * 
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void applyTermsAndConditions() throws ValidationException {
        if (isTermsAndConditionsApplied()) {
            throw new ValidationException(ACCEPTED_MSG);
        }
        termsAndConditionsService.applyTermsAndConditions();
    }

    /**
     * Check that Terms and Conditions is applied for logged user.
     * 
     * @return <code>true</code> if Terms and Conditions already applied otherwise <code>false</code>
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public boolean isTermsAndConditionsApplied() {
        permissionsService.checkCapability(Capabilities.ACCOUNT_EXECUTIVE.name());
        return termsAndConditionsService.isTermsAndConditionsAccepted();
    }
}
