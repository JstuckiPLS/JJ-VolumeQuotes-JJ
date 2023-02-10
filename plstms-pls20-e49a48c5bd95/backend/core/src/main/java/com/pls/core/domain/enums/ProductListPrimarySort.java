package com.pls.core.domain.enums;

/**
 * Enumeration which defines primary sorting order of Product list.
 *
 * @author Mikhail Boldinov, 28/01/13
 */
public enum ProductListPrimarySort {
    PRODUCT_DESCRIPTION("description"), SKU_PRODUCT_CODE("productCode");

    ProductListPrimarySort(String fieldName) {
        this.fieldName = fieldName;
    }

    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }
}
