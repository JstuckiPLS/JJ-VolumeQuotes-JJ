package com.pls.core.domain.user;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Application user.
 *
 * @author Denis Zhupinsky
 */
@Entity
@Table(name = "USER_VERIFICATION")
public class UserVerificationEntity implements Identifiable<Long> {

	/** Default	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PERSON_ID")
	private Long id;
	
	@Column(name = "VERIFICATION_CODE")
	private Integer verificationCode;
	
	@Column(name = "CODE_EXPIRATION_DATE")
	private Date expirationDate;
	
	public Long getPersonId() {
	    return id;
	}
	
	public void setPersonId(Long id) {
	    this.id = id;
	}
	
	public Integer getVerificationCode() {
	    return verificationCode;
	}
	
	public void setVerificationCode(Integer verificationCode) {
	    this.verificationCode = verificationCode;
	}
	
	public Date getCodeExpirationDate() {
	    return expirationDate;
	}
	
	public void setCodeExpirationDate(Date expirationDate) {
	    this.expirationDate = expirationDate;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}





