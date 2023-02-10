package com.pls.core.domain.address;

import com.pls.core.domain.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * AisDetailsSearch Entity.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "AIS_DETAIL_SEARCH_AIS_DETAIL")
public class AisDetailSearchEntity implements Identifiable<String> {

    private static final long serialVersionUID = -2336925485996813431L;

    @Id
    @Column(name = "SEARCH_TEXT")
    private String searchText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DETAIL_ID", columnDefinition = "number")
    private AisDetailEntity ais;

    @Override
    public String getId() {
        return searchText;
    }

    @Override
    public void setId(String id) {
        this.searchText = id;
    }

    public AisDetailEntity getAis() {
        return ais;
    }

    public void setAis(AisDetailEntity ais) {
        this.ais = ais;
    }
}
