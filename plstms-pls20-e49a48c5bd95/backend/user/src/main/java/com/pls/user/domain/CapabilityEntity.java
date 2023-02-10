package com.pls.user.domain;

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
 * Capabilities (CAPABILITIES) entity. This table holds all the permissions/capabilities that can be assigned
 * to a user either directly or through role for restricting the access to certain features of application.
 * 
 * @author Pavani Challa
 */
@Entity
@Table(name = "CAPABILITIES")
public class CapabilityEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -9211257805470723350L;

    public static final String Q_GET_USERS_WITH_CAPABILITY = "com.pls.user.domain.CapabilityEntity.Q_GET_USERS_WITH_CAPABILITY";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAP_SEQUENCE")
    @SequenceGenerator(name = "CAP_SEQUENCE", sequenceName = "CAP_SEQ", allocationSize = 1)
    @Column(name = "CAPABILITY_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "SYS_20", nullable = false)
    private String sys20;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY")
    private CapCategoryTypeEntity category;

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

    public String getSys20() {
        return sys20;
    }

    public void setSys20(String sys20) {
        this.sys20 = sys20;
    }

    public CapCategoryTypeEntity getCategory() {
        return category;
    }

    public void setCategory(CapCategoryTypeEntity category) {
        this.category = category;
    }
}
