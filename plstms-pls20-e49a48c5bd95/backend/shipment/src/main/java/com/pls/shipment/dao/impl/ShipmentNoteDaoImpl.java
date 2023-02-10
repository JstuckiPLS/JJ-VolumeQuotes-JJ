package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.ShipmentNoteEntity;

/**
 * Implementation of {@link com.pls.shipment.dao.ShipmentNoteDao}.
 *
 * @author Sergey Kirichenko
 */
@Transactional
@Repository
public class ShipmentNoteDaoImpl extends AbstractDaoImpl<ShipmentNoteEntity, Integer> implements ShipmentNoteDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentNoteEntity> findShipmentNotes(Long id) {
        Query query = getCurrentSession().getNamedQuery(ShipmentNoteEntity.GET_SHIPMENT_NOTES_BY_LOAD_ID);
        query.setParameter("id", id);
        return query.list();
    }

    @Override
    public void batchNotesInsert(List<ShipmentNoteEntity> notes) {
        for (int i = 0; i < notes.size(); i++) {
            getCurrentSession().saveOrUpdate(notes.get(i));

            if (i != 0 && i % 20 == 0) {
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
        }
    }
}
