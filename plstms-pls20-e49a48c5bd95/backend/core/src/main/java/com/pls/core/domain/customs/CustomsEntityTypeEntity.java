package com.pls.core.domain.customs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.pls.core.domain.Identifiable;

/**
 * Ltl lookup uom values for a load.
 *
 */
@Entity
@Table(name = "CUSTOMS_ENTITY_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMS_ENTITY_TYPE_ID"}))
public class CustomsEntityTypeEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1526671942132421693L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customs_entity_type_sequence")
    @SequenceGenerator(name = "customs_entity_type_sequence", sequenceName = "CUSTOMS_ENTITY_TYPE_SEQ", allocationSize = 1)
    @Column(name = "customs_entity_type_id")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
