package com.pls.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Ltl lookup value entity.
 *
 * @author Mikhail Boldinov, 22/02/13
 * @author Denis Zhupinsky
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "LTL_LOOKUP_VALUE", uniqueConstraints = @UniqueConstraint(columnNames = {"LTL_LOOKUP_GROUP", "LTL_LOOKUP_VALUE"}))
public class LtlLookupValueEntity implements Identifiable<Long> {
    private static final long serialVersionUID = 1526671942132421693L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_look_up_sequence")
    @SequenceGenerator(name = "ltl_look_up_sequence", sequenceName = "LTL_LOOKUP_VALUE_SEQ", allocationSize = 1)
    @Column(name = "LTL_LOOKUP_VALUE_ID")
    private Long id;

    @Column(name = "LTL_LOOKUP_VALUE", nullable = false)
    private String ltlLookupValue;

    @Column(name = "LTL_LOOKUP_GROUP", nullable = false)
    private String ltlLookupGroup;

    @Column(nullable = false)
    private String description;

    @Column(name = "LTL_LOOKUP_VALUE_ORDER")
    private Integer order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLtlLookupValue() {
        return ltlLookupValue;
    }

    public void setLtlLookupValue(String ltlLookupValue) {
        this.ltlLookupValue = ltlLookupValue;
    }

    public String getLtlLookupGroup() {
        return ltlLookupGroup;
    }

    public void setLtlLookupGroup(String ltlLookupGroup) {
        this.ltlLookupGroup = ltlLookupGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


}
