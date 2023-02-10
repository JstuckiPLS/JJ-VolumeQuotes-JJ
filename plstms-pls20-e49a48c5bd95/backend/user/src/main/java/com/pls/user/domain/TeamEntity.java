package com.pls.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;

/**
 * Entity for teams.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Immutable
@Table(name = "TEAM")
public class TeamEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = -4883119424003312186L;

    public static final String Q_GET_NAMES = "com.pls.user.domain.TeamEntity.Q_GET_NAMES";

    @Id
    @Column(name = "TEAM_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    private PlainModificationObject modification = new PlainModificationObject();

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

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

}
