package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



/**
 * BOL entity.
 * 
 * @author James Blackson
 */
@Entity
@Table(name = "upcharge_dialog_config")
public class UpChargeConfigurationEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1L;
    
    public static final String Q_CONFIGURATIONS = "com.pls.shipment.domain.UpChargeConfigurationEntity.Q_CONFIGURATIONS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upcharge_sequence")
    @SequenceGenerator(name = "upcharge_sequence", sequenceName = "UPCHARGE_SEQ", allocationSize = 1)
    @Column(name = "upcharge_config_id", nullable = false)
    private Long id;
    
    
    @Column(name = "is_this_config_enabled", nullable = false)
    private Boolean isThisConfigEnabled;
    
    @Column(name = "is_close_button_enabled", nullable = false)
    private Boolean isCloseButtonEnabled;
    
    @Column(name = "is_close_button_shown", nullable = false)
    private Boolean isCloseButtonShown;
    
    @Column(name = "can_user_select_charges", nullable = false)
    private Boolean canUserSelectCharges;
    
    @Column(name = "select_all_charges_enabled", nullable = false)
    private Boolean selectAllChargesEnabled;
    
    @Column(name = "page_name", nullable = false)
    private String pageName;
    
    @Column(name = "select_all_charges_automatically", nullable = false) 
    private Boolean selectAllChargesAutomatically; 
    
    @Column(name = "nag_user_on_this_page", nullable = false)
    private Boolean nagUserOnThisPage;  
    
    @Column(name = "block_user_on_session_cnt", nullable = false)
    private Boolean blockUserOnSessionCnt;  
    
    @Column(name = "block_user_on_global_cnt", nullable = false)
    private Boolean blockUserOnGlobalCnt;  
    
    @Column(name = "advise_user_on_this_page", nullable = false)
    private Boolean adviseUserOnThisPage;  
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

	public Boolean getIsCloseButtonEnabled() {
		return isCloseButtonEnabled;
	}

	public void setIsCloseButtonEnabled(Boolean isCloseButtonEnabled) {
		this.isCloseButtonEnabled = isCloseButtonEnabled;
	}

	public Boolean getIsCloseButtonShown() {
		return isCloseButtonShown;
	}

	public void setIsCloseButtonShown(Boolean isCloseButtonShown) {
		this.isCloseButtonShown = isCloseButtonShown;
	}

	public Boolean getCanUserSelectCharges() {
		return canUserSelectCharges;
	}

	public void setCanUserSelectCharges(Boolean canUserSelectCharges) {
		this.canUserSelectCharges = canUserSelectCharges;
	}

	public Boolean getSelectAllChargesEnabled() {
		return selectAllChargesEnabled;
	}

	public void setSelectAllChargesEnabled(Boolean selectAllChargesEnabled) {
		this.selectAllChargesEnabled = selectAllChargesEnabled;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public Boolean getIsThisConfigEnabled() {
		return isThisConfigEnabled;
	}

	public void setIsThisConfigEnabled(Boolean isThisConfigEnabled) {
		this.isThisConfigEnabled = isThisConfigEnabled;
	}

	public Boolean getSelectAllChargesAutomatically() {
		return selectAllChargesAutomatically;
	}

	public void setSelectAllChargesAutomatically(Boolean selectAllChargesAutomatically) {
		this.selectAllChargesAutomatically = selectAllChargesAutomatically;
	}

	public Boolean getNagUserOnThisPage() {
		return nagUserOnThisPage;
	}

	public void setNagUserOnThisPage(Boolean nagUserOnThisPage) {
		this.nagUserOnThisPage = nagUserOnThisPage;
	}

	public Boolean getBlockUserOnSessionCnt() {
		return blockUserOnSessionCnt;
	}

	public void setBlockUserOnSessionCnt(Boolean blockUserOnSessionCnt) {
		this.blockUserOnSessionCnt = blockUserOnSessionCnt;
	}

	public Boolean getBlockUserOnGlobalCnt() {
		return blockUserOnGlobalCnt;
	}

	public void setBlockUserOnGlobalCnt(Boolean blockUserOnGlobalCnt) {
		this.blockUserOnGlobalCnt = blockUserOnGlobalCnt;
	}

	public Boolean getAdviseUserOnThisPage() {
		return adviseUserOnThisPage;
	}

	public void setAdviseUserOnThisPage(Boolean adviseUserOnThisPage) {
		this.adviseUserOnThisPage = adviseUserOnThisPage;
	}
	
}
