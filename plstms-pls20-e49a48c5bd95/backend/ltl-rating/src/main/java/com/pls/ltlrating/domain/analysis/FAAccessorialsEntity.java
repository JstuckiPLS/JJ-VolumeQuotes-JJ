package com.pls.ltlrating.domain.analysis;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

/**
 * Accessorials from imported file.
 *
 * @author Svetlana Kulish
 */
@Entity
@Table(name = "FA_ACCESSORIALS")
public class FAAccessorialsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 2140299107178627991L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_ACCESSORIALS_SEQUENCE")
    @SequenceGenerator(name = "FA_ACCESSORIALS_SEQUENCE", sequenceName = "FA_ACCESSORIALS_SEQ", allocationSize = 1)
    @Column(name = "ACCESSORIAL_ID")
    private Long id;

    @Column(name = "ACCESSORIAL_TYPE")
    private String accessorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_DETAIL_ID", nullable = false)
    private FAInputDetailsEntity inputDetails;

    @Column(name = "SEQ_NUMBER", nullable = false)
    private Integer seq;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessorial() {
        return accessorial;
    }

    public void setAccessorial(String accessorial) {
        this.accessorial = accessorial;
    }

    public FAInputDetailsEntity getInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(FAInputDetailsEntity inputDetails) {
        this.inputDetails = inputDetails;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
