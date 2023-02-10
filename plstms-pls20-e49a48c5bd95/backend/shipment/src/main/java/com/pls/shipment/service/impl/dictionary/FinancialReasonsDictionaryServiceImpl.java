package com.pls.shipment.service.impl.dictionary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.FinancialReasonsDao;
import com.pls.shipment.domain.FinancialReasonsEntity;
import com.pls.shipment.service.dictionary.FinancialReasonsDictionaryService;

/**
 * {@link FinancialReasonsDictionaryService} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class FinancialReasonsDictionaryServiceImpl implements FinancialReasonsDictionaryService {
    @Autowired
    private FinancialReasonsDao financialReasonsDao;

    @Override
    public List<FinancialReasonsEntity> getFinancialReasonsForAdjustments() {
        return financialReasonsDao.getFinancialReasonsForAdjustments();
    }
}
