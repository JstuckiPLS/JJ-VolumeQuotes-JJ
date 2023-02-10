package com.pls.core.dao.customer;

import java.util.List;

import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.organization.PlsCustomerTermsEntity;

/**
 * DAO for {@link PlsCustomerTermsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface PlsCustomerTermsDao {

    /**
     * Get customer payment terms.
     * 
     * @return customer payment terms
     */
    List<KeyValueBO> getCustomerPayTerms();

}
