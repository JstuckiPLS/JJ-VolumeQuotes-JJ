package com.pls.shipment.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.user.PromoCodeEntity;

/**
 * Promo codes load entity.
 * 
 * @author Nalapko Alexander
 *
 */
@Entity
@Table(name = "PROMO_CODE_LOAD")
public class PromoCodeLoadEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 7994164820554928026L;

    public static final String Q_GET_LOADS_BY_CODE_AND_USER = "com.pls.shipment.domain.PromoCodeLoadEntity.Q_GET_LOADS_BY_CODE_AND_USER";
    public static final String Q_GET_LOADS_BY_USER = "com.pls.shipment.domain.PromoCodeLoadEntity.Q_GET_LOADS_BY_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_code_load_sequence")
    @SequenceGenerator(name = "promo_code_load_sequence", sequenceName = "PROMO_CODE_LOAD_SEQ", allocationSize = 1)
    @Column(name = "PROMO_CODE_LOAD_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROMO_CODE_AE_ID")
    private PromoCodeEntity promoCode;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID")
    private LoadEntity load;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PromoCodeEntity getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(PromoCodeEntity promoCode) {
        this.promoCode = promoCode;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

}
