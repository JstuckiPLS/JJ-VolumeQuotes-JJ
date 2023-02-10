package com.pls.shipment.domain;

import com.pls.core.domain.Identifiable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * BOL entity.
 * 
 * @author Gleb Zgonikov
 */
//@Entity
@Table(name = "BOL")
public class BolEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 646117909329539170L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bol_sequence")
    @SequenceGenerator(name = "bol_sequence", sequenceName = "BOL_SEQ", allocationSize = 1)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
