package com.pls.ltlrating.batch.analysis;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.ltlrating.dao.analysis.FAOutputDetailsDao;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;

/**
 * Item writer for Freight Analysis batch job.
 *
 * @author Aleksandr Leshchenko
 */
public class AnalysisItemPersister implements ItemWriter<List<FAOutputDetailsEntity>> {

    @Autowired
    private FAOutputDetailsDao dao;

    @Override
    public void write(List<? extends List<FAOutputDetailsEntity>> items) throws Exception {
        List<FAOutputDetailsEntity> outputDetails = items.stream().flatMap(l -> l.stream()).collect(Collectors.toList());
        dao.saveOrUpdateBatch(outputDetails);
    }

}
