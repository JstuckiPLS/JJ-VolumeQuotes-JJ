package com.pls.dtobuilder.util;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * DTO builder for {@link PhoneObject}.
 * 
 * @author Aleksandr Leshchenko
 * 
 * @param <P>
 *            Subclass of {@link PhoneObject}.
 */
public abstract class PhoneFaxDTOBuilder<P extends PhoneNumber> extends AbstractDTOBuilder<P, PhoneBO> {
    private P instance;

    /**
     * Constructor.
     * 
     * @param instance
     *            of {@link PhoneObject}
     */
    protected PhoneFaxDTOBuilder(P instance) {
        this.instance = instance;
    }

    @Override
    public PhoneBO buildDTO(P bo) {
        PhoneBO dto = new PhoneBO();
        dto.setCountryCode(bo.getCountryCode());
        dto.setAreaCode(bo.getAreaCode());
        dto.setNumber(bo.getNumber());
        dto.setExtension(bo.getExtension());
        return dto;
    }

    @Override
    public P buildEntity(PhoneBO dto) {
        if (dto != null) {
            instance.setCountryCode(dto.getCountryCode());
            instance.setAreaCode(dto.getAreaCode());
            instance.setNumber(dto.getNumber());
            instance.setExtension(dto.getExtension());
        }
        return instance;
    }

    /**
     * DTO builder for {@link PhoneEmbeddableObject}.
     * 
     * @author Aleksandr Leshchenko
     */
    public static final class PhoneDTOBuilder extends PhoneFaxDTOBuilder<PhoneEmbeddableObject> {
        /**
         * Default constructor.<br>
         * Instance of this DTO Builder can be used only once for building entity.<br>
         * I.E. {@link PhoneDTOBuilder#buildEntity(PhoneDTO)} can be called only once.
         */
        public PhoneDTOBuilder() {
            super(new PhoneEmbeddableObject());
        }
    }

}
