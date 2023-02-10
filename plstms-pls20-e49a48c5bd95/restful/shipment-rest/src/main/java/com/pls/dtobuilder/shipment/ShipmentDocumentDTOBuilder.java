package com.pls.dtobuilder.shipment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.dto.shipment.ShipmentDocumentDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;

/**
 * Entity to transform {@link ShipmentDocumentInfoBO} to {@link ShipmentDocumentDTO}.
 * 
 * @author Maxim Medvedev
 */
public class ShipmentDocumentDTOBuilder extends AbstractDTOBuilder<ShipmentDocumentInfoBO, ShipmentDocumentDTO> {

    @Override
    public ShipmentDocumentDTO buildDTO(ShipmentDocumentInfoBO bo) {
        StringBuilder createdByName = new StringBuilder("");
        // currently we generate only BOL document, also we do not have appropriate way to define if document was generated
        if ("BOL".equalsIgnoreCase(bo.getDocName()) || "Shipping label".equalsIgnoreCase(bo.getDocName())
                || "Consignee Invoice".equalsIgnoreCase(bo.getDocName())) {
            createdByName.append("Auto-generated");
        } else {
            if (StringUtils.isNotBlank(bo.getCreatedByFirstName())) {
                createdByName.append(bo.getCreatedByFirstName());
            }

            if (StringUtils.isNotBlank(bo.getCreatedByLastName())) {
                if (createdByName.length() > 0) {
                    createdByName.append(' ');
                }
                createdByName.append(bo.getCreatedByLastName());
            }
        }

        String fileName = bo.getDocName() + "." + FilenameUtils.getExtension(bo.getFileName());

        return new ShipmentDocumentDTO(bo.getId(), bo.getDocName(), bo.getModifiedDate(), bo.getShipmentId(),
                createdByName.toString(), bo.getDocFileType(), bo.getCreatedDate(), fileName);
    }

    /**
     * Method is not supported.
     * 
     * @param dto
     *            dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public ShipmentDocumentInfoBO buildEntity(ShipmentDocumentDTO dto) {
        throw new UnsupportedOperationException();
    }
}
