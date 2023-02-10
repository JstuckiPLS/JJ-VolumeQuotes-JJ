package com.pls.dto.organization;

import com.pls.core.domain.organization.PaperworkEmailEntity;

/**
 * DTO for {@link PaperworkEmailEntity}.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class PaperworkDTO {

    private boolean dontRequestPaperwork;

    private String email;

    public boolean isDontRequestPaperwork() {
        return dontRequestPaperwork;
    }

    public void setDontRequestPaperwork(boolean dontRequestPaperwork) {
        this.dontRequestPaperwork = dontRequestPaperwork;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
