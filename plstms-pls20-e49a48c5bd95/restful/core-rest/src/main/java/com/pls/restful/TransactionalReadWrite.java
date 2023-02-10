package com.pls.restful;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describe read/write transactional attribute on a method or class with rollback for checked exception.
 *
 * @author Sergey Kirichenko
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional(readOnly = false, rollbackFor = Exception.class)
public @interface TransactionalReadWrite {
}
