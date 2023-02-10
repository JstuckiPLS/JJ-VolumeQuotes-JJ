package com.pls.core.dao.impl.hibernate;

import java.lang.reflect.AnnotatedElement;

import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * This method wraps {@link TransactionAttribute}s to roll back transaction for every exceptions (checked and
 * unchecked).
 * 
 * @author Maxim Medvedev
 */
public class RollbackForAllAnnotationTransactionAttributeSource extends AnnotationTransactionAttributeSource {
    private static final long serialVersionUID = -8340137048678506856L;

    @Override
    protected TransactionAttribute determineTransactionAttribute(AnnotatedElement ae) {
        TransactionAttribute result = null;
        TransactionAttribute target = super.determineTransactionAttribute(ae);
        if (target != null) {
            result = new DelegatingTransactionAttribute(target) {
                private static final long serialVersionUID = -4736030990372142580L;

                @Override
                public boolean rollbackOn(Throwable ex) {
                    return true;
                }
            };
        }

        return result;
    }

}
