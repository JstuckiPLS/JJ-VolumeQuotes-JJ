package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.NetworkUserDao;
import com.pls.core.domain.user.NetworkUserEntity;

/**
 * {@link com.pls.core.dao.NetworkUserDao} implementation.
 * 
 * @author Sergey Vovchuk
 */
@Transactional
@Repository
public class NetworkUserDaoImpl extends AbstractDaoImpl<NetworkUserEntity, Long> implements NetworkUserDao {

}