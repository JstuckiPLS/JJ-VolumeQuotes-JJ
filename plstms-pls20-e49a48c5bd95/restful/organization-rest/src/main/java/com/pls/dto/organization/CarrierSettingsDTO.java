package com.pls.dto.organization;

import java.util.List;

import com.pls.dto.KeyValueDTO;

/**
 * Carrier Data Transfer Object.
 * 
 * @author Artem Arapov
 *
 */
public class CarrierSettingsDTO {

    private Long id;

    private String scac;
    
    private String actualScac;

    private String name;

    private OrgServiceDTO orgService;

    private List<KeyValueDTO> rejectedCustomers;

    private PaperworkDTO paperwork;

    public Long getId() {
        return id;
    }

    public String getScac() {
        return scac;
    }
    
    public String getActualScac() {
        return actualScac;
    }

    public void setActualScac(String actualScac) {
        this.actualScac = actualScac;
    }

    public String getName() {
        return name;
    }

    public OrgServiceDTO getOrgService() {
        return orgService;
    }

    public List<KeyValueDTO> getRejectedCustomers() {
        return rejectedCustomers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrgService(OrgServiceDTO orgServiceDto) {
        this.orgService = orgServiceDto;
    }

    public void setRejectedCustomers(List<KeyValueDTO> rejectedCustomers) {
        this.rejectedCustomers = rejectedCustomers;
    }

    public PaperworkDTO getPaperwork() {
        return paperwork;
    }

    public void setPaperwork(PaperworkDTO paperwork) {
        this.paperwork = paperwork;
    }
}
