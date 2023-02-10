package com.pls.organization.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pls.core.exception.ApplicationException;
import com.pls.core.service.OrganizationService;
import com.pls.organization.email.service.OrganizationEmailSender;
import com.pls.organization.utils.PLSCryptoHelper;

/**
 * REST service for organization unsubscribe emails.
 * 
 * @author Dmitriy Davydenko
 *
 */

@Controller
@Transactional(readOnly = true)
@RequestMapping("/unsubscribe")
public class OrganizationUnsubscribeResource {

    @Autowired
    private OrganizationService service;

    @Autowired
    private OrganizationEmailSender sender;

    @Value("${pls.encryptionKey.key}")
    private String encryptionKey;

    /**
     * Unsubscribe from missing documents emails.
     * 
     * @param encryptedOrgId - carrier Id.
     * @throws Exception - exception which occured during unsubscribe process.
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/{orgId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribeFromEmails(@PathVariable("orgId") String encryptedOrgId)  throws Exception {
        Long orgId = Long.parseLong(PLSCryptoHelper.decrypt(encryptionKey, encryptedOrgId));
        Boolean unsubscribeSuccess = service.unsubscribeFromPaperworkEmails(orgId);
        if (unsubscribeSuccess) {
            String orgName = service.getOrganizationNameByOrgId(orgId);
            sender.sendUnsubscribeSuccessEmail(orgName);
        } else {
            throw new ApplicationException("Organization is absent or is unable to unsubscribe");
        }
    }
}
