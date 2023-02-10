package com.pls.dtobuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.bo.GLCodeBO;

/**
 * DTO Builder for {@link LookupValueEntity}.
 * 
 * @author Brichak Aleksandr
 */
public class GLCodesDTOBuilder extends AbstractDTOBuilder<LookupValueEntity, GLCodeBO> {

    @Override
    public GLCodeBO buildDTO(LookupValueEntity bo) {
        GLCodeBO gLCode = new GLCodeBO();
        gLCode.setDescription(bo.getDescription());
        gLCode.setGroup(bo.getLookupGroup().name());
        gLCode.setValue(bo.getLookupValue());
        return gLCode;
    }

    /**
     * The method creates BO without {@link GLCodeBO#getValue()} field. This is necessary in the case when
     * there is no need to transfer all fields to UI.
     * 
     * @param bo
     *            {@link LookupValueEntity}
     * @return {@link GLCodeBO}
     */
    public GLCodeBO buildDTOWithoutValueField(LookupValueEntity bo) {
        GLCodeBO gLCode = buildDTO(bo);
        gLCode.setValue(StringUtils.EMPTY);
        return gLCode;
    }

    /**
     * Builds java.util.List which contains collection of DTO objects without {@link GLCodeBO#getValue()}
     * field.
     * 
     * @param boList
     *            collection of entities to store in ListDTO.
     * @return collection of DTO objects.
     */

    public List<GLCodeBO> buildListWithoutValueField(Collection<LookupValueEntity> boList) {
        List<GLCodeBO> dtoList = new ArrayList<GLCodeBO>(boList.size());
        for (LookupValueEntity bo : boList) {
            dtoList.add(this.buildDTOWithoutValueField(bo));
        }
        return dtoList;
    }

    /**
     * Method throws {@link UnsupportedOperationException}.
     * 
     * @param dto
     *            {@link GLCodeBO}
     * @return {@link LookupValueEntity}
     */
    @Override
    public LookupValueEntity buildEntity(GLCodeBO dto) {
        throw new UnsupportedOperationException("Unsupported method");
    }
}