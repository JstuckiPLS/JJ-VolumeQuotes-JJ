/**
 * 
 */
package com.pls.core.dao;

import com.pls.core.domain.bo.XmlDetailsAxBO;

/**
 * Customer details when passing the same to AX system.
 * 
 * @author Alexander Nalapko
 * 
 */
public interface XmlDetailsAxDao {

    /**
     * Get details for AX system.
     * 
     * @param billToId
     *            billing id
     * @return XmlDetailsAxBO
     */
    XmlDetailsAxBO getDetails(Long billToId);

}
