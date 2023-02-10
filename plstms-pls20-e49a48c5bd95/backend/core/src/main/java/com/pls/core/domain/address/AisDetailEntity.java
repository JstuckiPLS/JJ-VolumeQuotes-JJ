package com.pls.core.domain.address;

import com.pls.core.domain.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AIS Detail.
 * 
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "AIS_DETAIL_SEARCH")
public class AisDetailEntity implements Identifiable<String> {

    private static final long serialVersionUID = -4993211876555221624L;

    @Id
    @Column(name = "SEARCH_TEXT")
    private String searchText;

    @Override
    public String getId() {
        return searchText;
    }

    @Override
    public void setId(String id) {
        this.searchText = id;
    }

}
