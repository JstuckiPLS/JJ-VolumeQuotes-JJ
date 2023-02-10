package com.pls.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pls.core.domain.enums.LookupGroup;

/**
 * Lookup value entity.
 *
 * @author Sergey Vovchuk
 */
@Entity
@Table(name = "LOOKUP_VALUE")
public class LookupValueEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 1526671942132421693L;

    public static final String Q_FIND_FOR_LOOKUP_GROUP = "com.pls.core.domain.LookupValueEntity.Q_FIND_FOR_LOOKUP_GROUP";

    @Id
    @Column(name = "LOOKUP_VALUE_ID")
    private Long id;

    @Column(name = "LOOKUP_VALUE", nullable = false)
    private String lookupValue;

    @Column(name = "LOOKUP_GROUP", nullable = false)
    @Enumerated(EnumType.STRING)
    private LookupGroup lookupGroup;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "LOOKUP_VALUE_ORDER")
    private Integer order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
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

    public LookupGroup getLookupGroup() {
        return lookupGroup;
    }

    public void setLookupGroup(LookupGroup lookupGroup) {
        this.lookupGroup = lookupGroup;
    }
}
