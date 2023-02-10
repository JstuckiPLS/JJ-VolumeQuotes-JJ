package com.pls.core.domain.organization;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Type;

import com.pls.core.domain.enums.ProductListPrimarySort;
import com.pls.core.domain.user.CustomerUserEntity;

/**
 * Customer.
 * 
 * @author Denis Zhupinsky
 */
@Entity
@DiscriminatorValue("SHIPPER")
public class CustomerEntity extends OrganizationEntity {

    public static final String Q_BY_NAME = "com.pls.core.domain.organization.CustomerEntity.Q_BY_NAME";
    public static final String Q_BY_NAME_WITHOUT_ID = "com.pls.core.domain.organization.CustomerEntity.Q_BY_NAME_WITHOUT_ID";
    public static final String Q_BY_FEDERAL_TAX_ID = "com.pls.core.domain.organization.CustomerEntity.Q_BY_FEDERAL_TAX_ID";
    public static final String Q_COUNT_CUSTOMERS_BY_ID = "com.pls.core.domain.organization.CustomerEntity.Q_COUNT_CUSTOMERS_BY_ID";
    public static final String Q_GET_PRODUCT_LIST_PRIMARY_SORT = "com.pls.core.domain.organization.CustomerEntity.Q_GET_PRODUCT_LIST_PRIMARY_SORT";
    public static final String Q_UPDATE_PRODUCT_LIST_PRIMARY_SORT =
            "com.pls.core.domain.organization.CustomerEntity.Q_UPDATE_PRODUCT_LIST_PRIMARY_SORT";
    public static final String Q_BY_STATUS_AND_NAME = "com.pls.core.domain.organization.CustomerEntity.Q_BY_STATUS_AND_NAME";
    public static final String Q_GET_ASSOCIATED_WITH_USER_BY_NAME =
            "com.pls.core.domain.organization.CustomerEntity.Q_GET_ASSOCIATED_WITH_USER_BY_NAME";
    public static final String Q_FIND_CUSTOMERS_BY_NAME = "com.pls.core.domain.organization.CustomerEntity.Q_FIND_CUSTOMERS_BY_NAME";
    public static final String Q_GET_CREDIT_INFO = "com.pls.core.domain.organization.CustomerEntity.Q_GET_CREDIT_INFO";
    public static final String Q_GET_CREDIT_LIMIT = "com.pls.core.domain.organization.CustomerEntity.Q_GET_CREDIT_LIMIT";
    public static final String Q_IS_CHANGED = "com.pls.core.domain.organization.CustomerEntity.Q_IS_CHANGED";
    public static final String Q_GET_ASSOCIATED_UNASSIGNED_BY_NAME =
            "com.pls.core.domain.organization.CustomerEntity.Q_GET_ASSOCIATED_UNASSIGNED_BY_NAME";
    public static final String Q_GET_ASSOCIATED_UNASSIGNED_BY_AE =
            "com.pls.core.domain.organization.CustomerEntity.Q_GET_ASSOCIATED_UNASSIGNED_BY_AE";
    public static final String Q_CHECK_CUSTOMER_BY_ACTIVE_STATUS =
            "com.pls.core.domain.organization.CustomerEntity.Q_CHECK_CUSTOMER_BY_ACTIVE_STATUS";
    public static final String Q_FIND_CUSTOMER_BY_EDI_NUMBER = "com.pls.core.domain.organization.CustomerEntity.Q_FIND_CUSTOMER_BY_EDI_NUMBER";
    public static final String Q_CHECK_EDI_NUMBER_EXISTS = "com.pls.core.domain.organization.CustomerEntity.Q_CHECK_EDI_NUMBER_EXISTS";
    public static final String Q_GET_ALL_ACCOUNT_EXECUTIVES = "com.pls.core.domain.organization.CustomerEntity.Q_GET_ALL_ACCOUNT_EXECUTIVES";
    public static final String Q_GET_ASSIGNED_TO_USER = "com.pls.core.domain.organization.CustomerEntity.Q_GET_ASSIGNED_TO_USER";
    public static final String Q_GET_LOCATIONS_FOR_ASSOCIATED_CUSTOMER =
            "com.pls.core.domain.organization.CustomerEntity.Q_GET_LOCATIONS_FOR_ASSOCIATED_CUSTOMER";
    public static final String Q_GET_CREDIT_LIMIT_REQUIRED = "com.pls.core.domain.organization.CustomerEntity.Q_GET_CREDIT_LIMIT_REQUIRED";
    public static final String Q_GET_INTERNAL_NOTE = "com.pls.core.domain.organization.CustomerEntity.Q_GET_INTERNAL_NOTE";
    public static final String Q_IS_PRINT_BARCODE = "com.pls.core.domain.organization.CustomerEntity.Q_IS_PRINT_BARCODE";

    private static final long serialVersionUID = 3414060424886346814L;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @OrderBy("modification.createdDate DESC")
    private List<CustomerNoteEntity> notes;

    /**
     * List of user that bound with customer.
     */
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerUserEntity> customerUsers;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUCT_LIST_PRIMARY_SORT", nullable = true)
    private ProductListPrimarySort productListPrimarySort = ProductListPrimarySort.PRODUCT_DESCRIPTION;

    @Column(name = "FROM_VENDOR_BILLS")
    @Type(type = "yes_no")
    private Boolean createOrdersFromVendorBills;

    @Column(name = "GENERATE_CONSIGNEE_INVOICE")
    @Type(type = "yes_no")
    private Boolean generateConsigneeInvoice;

    @Column(name = "DISPLAY_LOGO_ON_BOL")
    @Type(type = "yes_no")
    private Boolean displayLogoOnBol;

    @Column(name = "DISPLAY_LOGO_ON_SHIP_LABEL")
    @Type(type = "yes_no")
    private Boolean displayLogoOnShipLabel;

    @Column(name = "INTERNAL_NOTE")
    private String internalNote;

    @Column(name = "PRINT_BARCODE")
    @Type(type = "yes_no")
    private Boolean printBarcode;

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public List<CustomerNoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(List<CustomerNoteEntity> notes) {
        this.notes = notes;
    }

    /**
     * Get list of user that bound with customer.
     * 
     * @return list of user that bound with customer
     */
    public List<CustomerUserEntity> getCustomerUsers() {
        return customerUsers;
    }

    /**
     * Set list of user that bound with customer.
     * 
     * @param customerUsers
     *            list of user that bound with customer
     */
    public void setCustomerUsers(List<CustomerUserEntity> customerUsers) {
        this.customerUsers = customerUsers;
    }

    public ProductListPrimarySort getProductListPrimarySort() {
        return productListPrimarySort;
    }

    public void setProductListPrimarySort(ProductListPrimarySort productListPrimarySort) {
        this.productListPrimarySort = productListPrimarySort;
    }

    public Boolean getDisplayLogoOnBol() {
        return displayLogoOnBol;
    }

    public void setDisplayLogoOnBol(Boolean displayLogoOnBol) {
        this.displayLogoOnBol = displayLogoOnBol;
    }

    public Boolean getDisplayLogoOnShipLabel() {
        return displayLogoOnShipLabel;
    }

    public void setDisplayLogoOnShipLabel(Boolean displayLogoOnShipLabel) {
        this.displayLogoOnShipLabel = displayLogoOnShipLabel;
    }

    public Boolean getCreateOrdersFromVendorBills() {
        return createOrdersFromVendorBills;
    }

    public void setCreateOrdersFromVendorBills(Boolean createOrdersFromVendorBills) {
        this.createOrdersFromVendorBills = createOrdersFromVendorBills;
    }

    public Boolean isGenerateConsigneeInvoice() {
        return generateConsigneeInvoice;
    }

    public void setGenerateConsigneeInvoice(Boolean generateConsigneeInvoice) {
        this.generateConsigneeInvoice = generateConsigneeInvoice;
    }

    public Boolean isPrintBarcode() {
        return printBarcode;
    }

    public void setPrintBarcode(Boolean printBarcode) {
        this.printBarcode = printBarcode;
    }
}
