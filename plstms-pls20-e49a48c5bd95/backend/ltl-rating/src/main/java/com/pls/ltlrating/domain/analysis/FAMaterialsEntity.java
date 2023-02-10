package com.pls.ltlrating.domain.analysis;

import java.math.BigDecimal;

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

import org.hibernate.annotations.Type;

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.enums.CommodityClass;

/**
 * Input material details for Freight Analysis.
 * 
 * @author Svetlana Kulish
 */
@Entity
@Table(name = "FA_MATERIALS")
public class FAMaterialsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1944418236438016902L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_MATERIALS_SEQUENCE")
    @SequenceGenerator(name = "FA_MATERIALS_SEQUENCE", sequenceName = "FA_MATERIALS_SEQ", allocationSize = 1)
    @Column(name = "MATERIAL_ID")
    private Long id;

    @Column(name = "COMMODITY_CLASS_CODE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.CommodityClassUserType")
    private CommodityClass commodityClass;

    @Column(name = "WEIGHT", nullable = false)
    private BigDecimal weight;

    @Column(name = "SEQ_NUMBER", nullable = false)
    private Integer seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INPUT_DETAIL_ID", nullable = false)
    private FAInputDetailsEntity inputDetails;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CommodityClass getCommodityClass() {
        return commodityClass;
    }

    public void setCommodityClass(CommodityClass commodityClass) {
        this.commodityClass = commodityClass;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public FAInputDetailsEntity getInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(FAInputDetailsEntity inputDetails) {
        this.inputDetails = inputDetails;
    }

}
