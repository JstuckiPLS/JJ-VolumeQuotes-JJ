/**
 * 
 */
package com.pls.goship.pls30.dao.impl;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.goship.pls30.dao.UserPLS30CustomerReferenceEntityDao;
import com.pls.goship.pls30.domain.UserPLS30CustomerReferenceEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kircicegi Korkmaz
 *
 */
@Repository
@Transactional(noRollbackFor=Exception.class)

public class UserPLS30CustomerReferenceEntityDaoImpl extends AbstractDaoImpl<UserPLS30CustomerReferenceEntity, Long>
        implements UserPLS30CustomerReferenceEntityDao {

}
