package com.pls.dtobuilder.shipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.pls.core.service.util.PhoneUtils;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;
import com.pls.shipment.domain.ManualBolAddressEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;

/**
 * DTO builder for manual BOL tooltip.<br/>
 *
 * @author Alexander Nalapko
 */
public class ManualBolGridTooltipDTOBuilder extends AbstractDTOBuilder<ManualBolEntity, ShipmentGridTooltipDTO> {
    private static final String DIMENSION_FORMAT = "%sx%sx%s inch";

    private static final AddressBookEntryDTOBuilder ADDRESS_BOOK_ENTRY_DTO_BUILDER = new AddressBookEntryDTOBuilder();

    @Override
    public ShipmentGridTooltipDTO buildDTO(ManualBolEntity entity) {
        ShipmentGridTooltipDTO dto = new ShipmentGridTooltipDTO();
        if (entity.getCarrier() != null) {
            dto.setCarrier(entity.getCarrier().getName());
            dto.setLogoPath(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()));
        }
        if (entity.getOrigin() != null) {
            dto.setOrigin(prepareAddress(entity.getOrigin()));
            dto.setProducts(prepareProducts(entity.getMaterials()));
        }
        if (entity.getDestination() != null) {
            dto.setDestination(prepareAddress(entity.getDestination()));
        }
        if (entity.getOrganization() != null) {
            dto.setCustomerName(entity.getOrganization().getName());
        }
        return dto;
    }

    @Override
    public ManualBolEntity buildEntity(ShipmentGridTooltipDTO shipmentGridTooltipDTO) {
        throw new UnsupportedOperationException("Data is not enough for entity creation");
    }

    private List<String> prepareProducts(Set<ManualBolMaterialEntity> materials) {
        List<String> products = new ArrayList<String>();
        for (ManualBolMaterialEntity material : materials) {
            products.add(buildProductInfo(material));
        }
        return products;
    }

    private String buildProductInfo(ManualBolMaterialEntity material) {
        String productInfo = buildProductDescription(material);
        String dimensions = "";

        if (material.getLength() != null || material.getWidth() != null || material.getHeight() != null) {
            String width = ObjectUtils.toString(material.getWidth(), "0");
            String height = ObjectUtils.toString(material.getHeight(), "0");
            String length = ObjectUtils.toString(material.getLength(), "0");
            dimensions = String.format(DIMENSION_FORMAT, length, width, height);
        }

        String commodityClass = material.getCommodityClass().getDbCode();
        String weight = ObjectUtils.toString(material.getWeight(), "0");

        StringBuilder result = new StringBuilder();
        result.append(productInfo);
        if (commodityClass != null) {
            result.append(String.format(" %s Class,", commodityClass));
        }
        if (dimensions.contains("inch")) {
            result.append(String.format(" %s Lbs, ", weight));
            result.append(dimensions);
        } else {
            result.append(String.format(" %s Lbs ", weight));
        }
        if (material.isHazmat()) {
            result.append(", Hazmat");
        }

        return result.toString();
    }

    private String buildProductDescription(ManualBolMaterialEntity material) {
        String prodDescription = material.getProductDescription();
        StringBuilder result = new StringBuilder();
        result.append(prodDescription != null ? String.format("%s,", prodDescription) : "");
        String output = result.toString();
        if (output.endsWith(", ")) {
            output = output.substring(0, output.length() - 2);
        }
        return output;
    }

    private AddressBookEntryDTO prepareAddress(ManualBolAddressEntity loadDetails) {
        AddressBookEntryDTO result = null;
        if (loadDetails != null) {
            result = ADDRESS_BOOK_ENTRY_DTO_BUILDER.buildDTO(loadDetails.getAddress());
            result.setContactName(loadDetails.getContactName());
            result.setAddressName(loadDetails.getContact());
            result.setAddressCode(loadDetails.getAddressCode());
            result.setPhone(PhoneUtils.parse(loadDetails.getContactPhone()));
        }
        return result;
    }
}
