package com.pls.shipment.service.impl.dictionary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.PackageTypeDao;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.service.dictionary.PackageTypeDictionaryService;

/**
 * {@link PackageTypeDictionaryService} implementation.
 * @author Sergey Kirichenko
 */
@Service
@Transactional
public class PackageTypeDictionaryServiceImpl implements PackageTypeDictionaryService {

    @Autowired
    private PackageTypeDao packageTypeDao;

    @Override
    public List<PackageTypeEntity> getAllPackageTypes() {
        return packageTypeDao.getAll();
    }

    @Override
    public PackageTypeEntity getById(String id) {
        return packageTypeDao.getById(id);
    }
}
