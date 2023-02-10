package com.pls.dtobuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for builders converting between DTO and corresponding BO objects.
 *
 * @param <BO> type of entity to convert.
 * @param <DTO> type of DTO to convert.
 *
 * @author Gleb Zgonikov
 */
public abstract class AbstractDTOBuilder<BO, DTO> {

    protected static final String NULL_DTO = "Can't build entity - dto is NULL.";
    protected static final String NULL_ENTITY = "Can't build dto - entity is NULL.";

    /**
     * Build DTO from BO.
     *
     * @param bo bo to convert.
     * @return DTO result DTO.
     */
    public abstract DTO buildDTO(BO bo);

    /**
     * Build BO from DTO.
     *
     * @param dto dto to convert.
     * @return BO result BO.
     */
    public abstract BO buildEntity(DTO dto);

    /**
     * Builds java.util.List which contains collection of DTO objects.
     * @param boList collection of entities to store in ListDTO.
     * @return collection of DTO objects.
     */
    public List<DTO> buildList(Collection<BO> boList) {
        List<DTO> dtoList = new ArrayList<DTO>(boList.size());
        for (BO bo : boList) {
            dtoList.add(this.buildDTO(bo));
        }
        return dtoList;
    }

    /**
     * Builds java.util.List which contains collection of BO objects.
     * 
     * @param dtoList
     *            collection of DTO's to convert.
     * @return collection of BO objects.
     */
    public List<BO> buildEntityList(Collection<DTO> dtoList) {
        List<BO> boList = new ArrayList<BO>(dtoList.size());
        for (DTO dto : dtoList) {
            boList.add(this.buildEntity(dto));
        }
        return boList;
    }
}
