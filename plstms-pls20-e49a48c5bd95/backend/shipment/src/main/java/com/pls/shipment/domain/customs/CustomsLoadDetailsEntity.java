package com.pls.shipment.domain.customs;

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
import com.pls.shipment.domain.LoadEntity;

/**
 * Ltl lookup uom values for a load.
 *
 */
@Entity
@Table(name = "CUSTOMS_LOAD_DETAILS", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMS_LOAD_DETAILS_ID"}))
public class CustomsLoadDetailsEntity implements Identifiable<Long>, HasModificationInfo {
    private static final long serialVersionUID = 1526671942132421693L;
    
    public static final String Q_GET_DETAILS = "com.pls.shipment.domain.customs.Q_GET_DETAILS";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customs_load_details_sequence")
    @SequenceGenerator(name = "customs_load_details_sequence", sequenceName = "customs_load_details_seq", allocationSize = 1)
    @Column(name = "CUSTOMS_LOAD_DETAILS_ID")
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_ID", nullable = false)
    private LoadEntity load;

    @Column(name = "LOAD_ID", insertable=false, updatable = false)
    private Long loadId;
    
    @Column(name = "WEIGHT", nullable = false)
    private String weight;
    
    @Column(name = "SIZE", nullable = false)
    private String size;
    
    @Column(name = "IS_TRANS_COUNTRY", nullable = false)
    private Boolean isTransCountry;
    
    @Column(name = "USE_GS_CUSTOMS_BROKER", nullable = false)
    private Boolean useGSCustomsBroker;
    
    @Column(name = "TERMS_OF_SALE", nullable = false)
    private String termsOfSale;
    
    @Column(name = "CREATE_CUSTOMS_INVOICE", nullable = false)
    private Boolean createCustomsInvoice;

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
   
	public Long getLoadId() {
		return loadId;
	}

	public void setLoadId(Long loadId) {
		this.loadId = loadId;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Boolean getIsTransCountry() {
		return isTransCountry;
	}

	public void setIsTransCountry(Boolean isTransCountry) {
		this.isTransCountry = isTransCountry;
	}
	
	public Boolean getUseGSCustomsBroker() {
		return useGSCustomsBroker;
	}

	public void setUseGSCustomsBroker(Boolean useGSCustomsBroker) {
		this.useGSCustomsBroker = useGSCustomsBroker;
	}

	public String getTermsOfSale() {
		return termsOfSale;
	}

	public void setTermsOfSale(String termsOfSale) {
		this.termsOfSale = termsOfSale;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public LoadEntity getLoad() {
		return load;
	}

	public void setLoad(LoadEntity load) {
		this.load = load;
	}

	public Boolean getCreateCustomsInvoice() {
		return createCustomsInvoice;
	}

	public void setCreateCustomsInvoice(Boolean createCustomsInvoice) {
		this.createCustomsInvoice = createCustomsInvoice;
	}

	@Override
	public PlainModificationObject getModification() {
		return modification;
	}
	
}
