package com.pls.core.domain.user;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * Promo codes entity.
 * 
 * @author Nalapko Alexander
 *
 */
@Entity
@Table(name = "PROMO_CODE_AE")
public class PromoCodeEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -5883267362513620595L;

    public static final String Q_FIND_BY_CODE = "com.pls.shipment.domain.PromoCodeEntity.Q_FIND_BY_CODE";
    public static final String Q_IS_PROMO_CODE_UNIQUE = "com.pls.shipment.domain.PromoCodeEntity.Q_IS_PROMO_CODE_UNIQUE";
    public static final String Q_FIND_BY_USER = "com.pls.shipment.domain.PromoCodeEntity.Q_FIND_BY_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_code_sequence")
    @SequenceGenerator(name = "promo_code_sequence", sequenceName = "PROMO_CODE_AE_SEQ", allocationSize = 1)
    @Column(name = "PROMO_CODE_AE_ID")
    private Long id;

    @Column(name = "PERSON_ID", nullable = false, insertable = false, updatable = false)
    private Long accountExecutiveId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private UserEntity accountExecutive;

    @Column(name = "PROMO_CODE")
    private String code;

    @Column(name = "PERCENTAGE")
    private BigDecimal percentage;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "TERMS_AND_CONDITIONS_VERSION")
    private Long termsAndConditionsVersion;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(UserEntity accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getAccountExecutiveId() {
        return accountExecutiveId;
    }

    public void setAccountExecutiveId(Long accountExecutiveId) {
        this.accountExecutiveId = accountExecutiveId;
    }

    public Long getTermsAndConditionsVersion() {
        return termsAndConditionsVersion;
    }

    public void setTermsAndConditionsVersion(Long termsAndConditionsVersion) {
        this.termsAndConditionsVersion = termsAndConditionsVersion;
    }
}
