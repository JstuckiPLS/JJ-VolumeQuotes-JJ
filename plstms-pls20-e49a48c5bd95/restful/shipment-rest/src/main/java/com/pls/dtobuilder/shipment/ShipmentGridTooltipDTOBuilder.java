package com.pls.dtobuilder.shipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.pls.core.domain.address.RouteEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;

/**
 * DTO builder for {@link ShipmentGridTooltipDTO}.<br/>
 *
 * @author Denis Zhupinsky (Team International)
 */
public class ShipmentGridTooltipDTOBuilder extends AbstractDTOBuilder<LoadEntity, ShipmentGridTooltipDTO> {
    private static final String DIMENSION_FORMAT = "%sx%sx%s inch";

    private static final AddressBookEntryDTOBuilder ADDRESS_BOOK_ENTRY_DTO_BUILDER = new AddressBookEntryDTOBuilder();

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    @Override
    public ShipmentGridTooltipDTO buildDTO(LoadEntity entity) {
        ShipmentGridTooltipDTO dto = new ShipmentGridTooltipDTO();
        if (entity.getCarrier() != null) {
            dto.setCarrier(entity.getCarrier().getName());
            dto.setLogoPath(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()));
        }
        if (entity.getOrigin() != null) {
            dto.setOrigin(prepareAddress(entity.getOrigin()));
            dto.setProducts(prepareProducts(entity.getOrigin().getLoadMaterials()));
        }
        if (entity.getDestination() != null) {
            dto.setDestination(prepareAddress(entity.getDestination()));
        }

        if (entity.getRoute() != null) {
            if (dto.getOrigin() == null) {
                dto.setOrigin(prepareAddress(entity.getRoute(), true));
            }
            if (dto.getDestination() == null) {
                dto.setDestination(prepareAddress(entity.getRoute(), false));
            }
        }

        fillCostDetailsData(dto, entity, false);
        dto.setCustomerName(entity.getOrganization().getName());

        return dto;
    }

    @Override
    public LoadEntity buildEntity(ShipmentGridTooltipDTO shipmentGridTooltipDTO) {
        throw new UnsupportedOperationException("Data is not enough for entity creation");
    }

    /**
     * Fill {@link ShipmentGridTooltipDTO} dto with data that should be shown to pls user only like carrier cost details and customer name.
     *
     * @param dto dto to fill
     * @param entity shipment to get data from
     */
    public void fillPlsUserRelatedData(ShipmentGridTooltipDTO dto, LoadEntity entity) {
        fillCostDetailsData(dto, entity, true);
    }

    private List<String> prepareProducts(Set<LoadMaterialEntity> loadMaterials) {
        List<String> products = new ArrayList<String>();
        for (LoadMaterialEntity material : loadMaterials) {
            products.add(buildProductInfo(material));
        }
        return products;
    }

    private String buildProductDescription(LoadMaterialEntity material) {
        String prodDescription = material.getProductDescription();
        StringBuilder result = new StringBuilder();
        result.append(prodDescription != null ? String.format("%s,", prodDescription) : "");
        String output = result.toString();
        if (output.endsWith(", ")) {
            output = output.substring(0, output.length() - 2);
        }
        return output;
    }

    private String buildProductInfo(LoadMaterialEntity material) {
        String productInfo = buildProductDescription(material);
        String dimensions = "";
        if (material.getLength() != null || material.getWidth() != null || material.getHeight() != null) {

        String length = ObjectUtils.toString(material.getLength(), "0");
        String width = ObjectUtils.toString(material.getWidth(), "0");
        String height = ObjectUtils.toString(material.getHeight(), "0");

            dimensions = String.format(DIMENSION_FORMAT, length, width, height);
        }

        String weight = ObjectUtils.toString(material.getWeight(), "0");
        String productClass = material.getCommodityClass().getDbCode();

        StringBuilder result = new StringBuilder();
        result.append(productInfo);
        if (productClass != null) {
            result.append(String.format(" %s Class,", productClass));
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

    private AddressBookEntryDTO prepareAddress(LoadDetailsEntity loadDetails) {
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

    private AddressBookEntryDTO prepareAddress(RouteEntity route, boolean origin) {
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        ZipDTO zipDTO = zipDTOBuilder.buildDTO(origin ? route.getOriginZipCode() : route.getDestinationZipCode());
        address.setZip(zipDTO);
        address.setCountry(zipDTO.getCountry());
        return address;
    }

    private void fillCostDetailsData(ShipmentGridTooltipDTO dto, LoadEntity entity, final boolean carrierCostDetails) {
        if (entity.getActiveCostDetail() == null || entity.getActiveCostDetail().getCostDetailItems() == null) {
            return;
        }

        if (carrierCostDetails) {
            dto.setTotalCost(entity.getActiveCostDetail().getTotalCost());
        } else {
            dto.setTotalRevenue(entity.getActiveCostDetail().getTotalRevenue());
        }
    }
}
