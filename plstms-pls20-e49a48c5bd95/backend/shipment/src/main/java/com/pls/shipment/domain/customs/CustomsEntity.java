package com.pls.shipment.domain.customs;

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
import javax.persistence.UniqueConstraint;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.customs.CustomsEntityTypeEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Customs Entity (Broker, Shipper, Vendor, ...)
 *
 */
@Entity
@Table(name = "CUSTOMS_ENTITY", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMS_ENTITY_ID"}))
public class CustomsEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 1526671942132421693L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customs_entity_sequence")
    @SequenceGenerator(name = "customs_entity_sequence", sequenceName = "CUSTOMS_ENTITY_SEQ", allocationSize = 1)
    @Column(name = "CUSTOMS_ENTITY_ID")
    private Long id;
    
    @Column(name = "IS_GS_CUSTOMS_BROKER", nullable = false)
    private Boolean isGsCustomsBroker;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;
    
    @Column(name = "COMPANY_NAME")
    private String companyName;
    
    @Column(name = "CONTACT_NAME", nullable = false)
    private String contactName;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity addressEntity;
    
    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;
    
    @Column(name = "fax")
    private String fax;
    
    @Column(name = "email")
    private String email;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CUSTOMS_ENTITY_TYPE_ID")
    private CustomsEntityTypeEntity customsEntityType;
    
    @Column(name = "VERSION", nullable = false)
    private Integer version;
    
    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	
	public Boolean getIsGsCustomsBroker() {
		return isGsCustomsBroker;
	}

	public void setIsGsCustomsBroker(Boolean isGsCustomsBroker) {
		this.isGsCustomsBroker = isGsCustomsBroker;
	}

	public LoadEntity getLoad() {
		return load;
	}

	public void setLoad(LoadEntity load) {
		this.load = load;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public AddressEntity getAddressEntity() {
		return addressEntity;
	}

	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CustomsEntityTypeEntity getCustomsEntityType() {
		return customsEntityType;
	}

	public void setCustomsEntityType(CustomsEntityTypeEntity customsEntityType) {
		this.customsEntityType = customsEntityType;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public PlainModificationObject getModification() {
		return modification;
	}

}
