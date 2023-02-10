package com.pls.dtobuilder.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.pls.core.domain.address.RouteEntity;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.ZipDTO;
import com.pls.dto.shipment.ShipmentGridTooltipDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;

/**
 * Saved Quote Tooltip DTO Builder.
 *
 * @author Ivan Shapovalov
 * @author Aleksandr Leshchenko
 */
public class SavedQuoteShipmentGridTooltipDTOBuilder extends AbstractDTOBuilder<SavedQuoteEntity, ShipmentGridTooltipDTO> {
    private static final String DIMENSION_FORMAT = "%sx%sx%s inch";

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    @Override
    public ShipmentGridTooltipDTO buildDTO(SavedQuoteEntity entity) {
        ShipmentGridTooltipDTO dto = new ShipmentGridTooltipDTO();

        dto.setCarrier(entity.getCarrier().getName());
        dto.setLogoPath(CarrierDTOBuilder.getCarrierLogoPath(entity.getCarrier().getId()));

        dto.setProducts(prepareProducts(entity.getMaterials()));

        if (entity.getRoute() != null) {
            dto.setOrigin(prepareAddress(entity.getRoute(), true));
            dto.setDestination(prepareAddress(entity.getRoute(), false));
        }

        fillCostDetailsData(dto, entity, false);
        dto.setCustomerName(entity.getCustomer().getName());

        return dto;
    }

    /**
     * Method is not supported.
     *
     * @param dto dto
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public SavedQuoteEntity buildEntity(ShipmentGridTooltipDTO dto) {
        throw new UnsupportedOperationException("SavedQuoteEntity cannot be built from the ShipmentGridTooltipDTO");
    }

    /**
     * Fill {@link ShipmentGridTooltipDTO} dto with data that should be shown to pls user only like carrier cost details and customer name.
     *
     * @param dto dto to fill
     * @param entity shipment to get data from
     */
    public void fillPlsUserRelatedData(ShipmentGridTooltipDTO dto, SavedQuoteEntity entity) {
        fillCostDetailsData(dto, entity, true);
    }

    private String buildProductDescription(SavedQuoteMaterialEntity savedQuoteMaterial) {
        String prodDescription = savedQuoteMaterial.getProductDescription();
        StringBuilder result = new StringBuilder();
        result.append(prodDescription != null ? String.format("%s,", prodDescription) : "");
        String output = result.toString();
        if (output.endsWith(", ")) {
            output = output.substring(0, output.length() - 2);
        }
        return output;
    }

    private String buildProductInfo(SavedQuoteMaterialEntity savedQuoteMaterial) {
        String productInfo = buildProductDescription(savedQuoteMaterial);

        String length = ObjectUtils.toString(savedQuoteMaterial.getLength(), "0");
        String width = ObjectUtils.toString(savedQuoteMaterial.getWidth(), "0");
        String height = ObjectUtils.toString(savedQuoteMaterial.getHeight(), "0");

        String dimensions = String.format(DIMENSION_FORMAT, length, width, height);

        String weight = ObjectUtils.toString(savedQuoteMaterial.getWeight(), "0");
        String productClass = savedQuoteMaterial.getCommodityClass().getDbCode();

        StringBuilder result = new StringBuilder();
        result.append(productInfo);
        if (productClass != null) {
            result.append(String.format(" %s Class,", productClass));
        }
        result.append(String.format(" %s Lbs, ", weight));

        result.append(dimensions);

        if (BooleanUtils.isTrue(savedQuoteMaterial.getHazmat())) {
            result.append(", Hazmat");
        }

        return result.toString();
    }

    private List<String> prepareProducts(Set<SavedQuoteMaterialEntity> loadMaterials) {
        List<String> products = new ArrayList<String>();
        for (SavedQuoteMaterialEntity material : loadMaterials) {
            products.add(buildProductInfo(material));
        }
        return products;
    }

    private AddressBookEntryDTO prepareAddress(RouteEntity route, boolean origin) {
        AddressBookEntryDTO address = new AddressBookEntryDTO();
        ZipDTO zipDTO = zipDTOBuilder.buildDTO(origin ? route.getOriginZipCode() : route.getDestinationZipCode());
        address.setZip(zipDTO);
        address.setCountry(zipDTO.getCountry());
        return address;
    }

    private void fillCostDetailsData(ShipmentGridTooltipDTO dto, SavedQuoteEntity entity, final boolean carrierCostDetails) {
        SavedQuoteCostDetailsEntity costDetails = entity.getCostDetails();
        if (costDetails == null || costDetails.getCostDetailsItems() == null) {
            return;
        }

        if (carrierCostDetails) {
            dto.setTotalCost(costDetails.getTotalCost());
        } else {
            dto.setTotalRevenue(costDetails.getTotalRevenue());
        }
    }
}
