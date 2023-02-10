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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Saved Quote accessorial entity.
 *
 * @author Mikhail Boldinov, 26/03/13
 */
@Entity
@Table(name = "SV_QT_LTL_ACCESSORIALS")
public class SavedQuoteAccessorialEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 7859817577634032473L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quote_accessorials_sequence")
    @SequenceGenerator(name = "saved_quote_accessorials_sequence", sequenceName = "SAVED_QUOTE_ACCESSORIALS_SEQ", allocationSize = 1)
    @Column(name = "ACCESSORIAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private SavedQuoteEntity savedQuote;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESSORIAL_TYPE_CODE", nullable = false)
    private AccessorialTypeEntity accessorialType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public SavedQuoteEntity getSavedQuote() {
        return savedQuote;
    }

    public void setSavedQuote(SavedQuoteEntity savedQuote) {
        this.savedQuote = savedQuote;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public AccessorialTypeEntity getAccessorialType() {
        return accessorialType;
    }

    public void setAccessorialType(AccessorialTypeEntity accessorialType) {
        this.accessorialType = accessorialType;
    }
}
