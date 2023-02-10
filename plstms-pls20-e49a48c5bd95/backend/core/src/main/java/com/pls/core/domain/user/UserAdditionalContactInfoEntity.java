package com.pls.core.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PlainModificationObject;

/**
 * Additional Contact Information of specified User.
 * 
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "USER_ADDL_CONTACT_INFO")
public class UserAdditionalContactInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -7392883260970771502L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_addl_sequence")
    @SequenceGenerator(name = "usr_addl_sequence", sequenceName = "USER_ADDL_CONTACT_INFO_SEQ", allocationSize = 1)
    @Column(name = "USR_ADDL_CNT_INFO_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID")
    private UserEntity user;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @Column(name = "CONTACT_TYPE")
    private String contactType;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    public PhoneEntity getPhone() {
        return phone;
    }

    public String getContactType() {
        return contactType;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(PhoneEntity phone) {
        this.phone = phone;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }
}
