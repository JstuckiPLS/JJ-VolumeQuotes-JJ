package com.pls.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.Identifiable;

@Entity
@Table(name = "USER_SETTINGS")
public class UserSettingsEntity implements Identifiable<Long> {

    private static final long serialVersionUID = -9114774165694494469L;
    
    public static final String Q_GET_BY_PERSON_ID = "com.pls.user.domain.UserSettingsEntity.Q_GET_BY_PERSON_ID";
    public static final String Q_GET_BY_PERSON_ID_AND_KEY = "com.pls.user.domain.UserSettingsEntity.Q_GET_BY_PERSON_ID_AND_KEY";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SETTINGS_ID_SEQUENCE")
    @SequenceGenerator(name = "USER_SETTINGS_ID_SEQUENCE", sequenceName = "USER_SETTINGS_ID_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name = "PERSON_ID")
    private Long personId;
    
    @Column(name = "KEY")
    private String key;
    
    @Column(name = "VALUE")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long userId) {
        this.personId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
