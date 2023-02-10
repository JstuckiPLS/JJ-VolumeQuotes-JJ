package com.pls.shipment.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InvalidArgumentException;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.ManualBolMaterialEntity;
import com.pls.shipment.service.ManualBolDocumentService;
import com.pls.shipment.service.pdf.BolPdfDocumentParameter;
import com.pls.shipment.service.pdf.BolPdfDocumentWriter;
import com.pls.shipment.service.pdf.Printable;
import com.pls.shipment.service.pdf.ShippingLabelsDocumentParameter;
import com.pls.shipment.service.pdf.ShippingLabelsPdfDocumentWriter;

/**
 * Implementation of {@link ManualBolDocumentService}.
 * 
 * @author Artem Arapov
 *
 */
@Service
@Transactional
public class ManualBolDocumentServiceImpl extends AbstractDocumentService implements ManualBolDocumentService {

    @Autowired
    private BolPdfDocumentWriter bolPdfDocumentWriter;

    @Autowired
    private ShippingLabelsPdfDocumentWriter shippingLabelsPdfDocumentWriter;

    @Override
    public List<ShipmentDocumentInfoBO> getDocumentsList(Long manualBolId) throws InvalidArgumentException, EntityNotFoundException {
        if (manualBolId == null) {
            throw new InvalidArgumentException();
        }

        return documentDao.getDocumentsInfoForManualBol(manualBolId);
    }

    @Override
    public void createShippingLabelDocument(ManualBolEntity manualBol) throws PDFGenerationException {
        LoadEntity load = manualBolToLoadConverter(manualBol);

        createPdfDocument(manualBol.getId(), DocumentTypes.SHIPPING_LABELS, shippingLabelsPdfDocumentWriter,
                new ShippingLabelsDocumentParameter(load, load.getId(),
                        getCustomerLogoForShipLabel(load.getOrganization().getId()), Printable.DEFAULT_TEMPLATE));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createBolDocument(ManualBolEntity manualBol, boolean hideCreatedTime) throws PDFGenerationException {
        LoadEntity load = manualBolToLoadConverter(manualBol);

        createPdfDocument(manualBol.getId(), DocumentTypes.BOL, bolPdfDocumentWriter,
                new BolPdfDocumentParameter(load, getCurrentUser(), getCustomerLogoForBol(load.getOrganization().getId()),
                        false, hideCreatedTime, true));
    }

    @Override
    protected LoadDocumentEntity prepareLoadDocument(Long documentId, DocumentTypes documentType) {
        String docType = documentType.getDbValue();
        List<LoadDocumentEntity> documentsList = documentDao.findDocumentsForManualBol(documentId, docType);

        LoadDocumentEntity document;
        if (!documentsList.isEmpty()) {
            // in fact there should be only one row, however it might be more records from DB perspective
            document = documentsList.get(0);
        } else {
            document = new LoadDocumentEntity();
            document.setDocumentType(docType);
            document.setManualBol(documentId);
        }

        String documentPath = docFileNamesResolver.buildManualBolPath(document);
        String docFileName = docFileNamesResolver.buildManualBolDocumentFileName(document);

        document.setDocumentPath(documentPath);
        document.setDocFileName(docFileName);

        return document;
    }

    private LoadEntity manualBolToLoadConverter(ManualBolEntity manualBol) {
        LoadEntity load = new LoadEntity();
        load.setId(manualBol.getId());
        load.setOrganization(manualBol.getOrganization());
        load.setCarrier(manualBol.getCarrier());
        load.setFreightBillPayTo(manualBol.getFreightBillPayTo());

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setAddress(manualBol.getOrigin().getAddress());
        origin.setContact(manualBol.getOrigin().getContact());
        origin.setContactName(manualBol.getOrigin().getContactName());
        origin.setContactPhone(manualBol.getOrigin().getContactPhone());
        origin.setEarlyScheduledArrival(manualBol.getPickupDate());
        origin.setArrivalWindowStart(manualBol.getPickupWindowFrom());
        origin.setArrivalWindowEnd(manualBol.getPickupWindowTo());
        origin.setLoadMaterials(converToLoadMaterials(manualBol, origin));
        origin.setNotes(manualBol.getOrigin().getPickupNotes());
        origin.setLoad(load);

        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destination.setAddress(manualBol.getDestination().getAddress());
        destination.setContact(manualBol.getDestination().getContact());
        destination.setContactName(manualBol.getDestination().getContactName());
        destination.setContactPhone(manualBol.getDestination().getContactPhone());
        destination.setContactEmail(manualBol.getDestination().getContactEmail());
        destination.setNotes(manualBol.getDestination().getDeliveryNotes());
        destination.setArrivalWindowStart(manualBol.getDeliveryWindowFrom());
        destination.setArrivalWindowEnd(manualBol.getDeliveryWindowTo());
        destination.setLoad(load);

        load.addLoadDetails(origin);
        load.addLoadDetails(destination);

        load.setSpecialInstructions(manualBol.getPickupNotes());
        load.setDeliveryNotes(manualBol.getDeliveryNotes());
        load.getNumbers().setRefNumber(manualBol.getNumbers().getRefNumber());
        load.getNumbers().setBolNumber(manualBol.getNumbers().getBolNumber());
        load.getNumbers().setPoNumber(manualBol.getNumbers().getPoNumber());
        load.getNumbers().setPuNumber(manualBol.getNumbers().getPuNumber());
        load.getNumbers().setSoNumber(manualBol.getNumbers().getSoNumber());
        load.getNumbers().setProNumber(manualBol.getNumbers().getProNumber());
        load.getNumbers().setGlNumber(manualBol.getNumbers().getGlNumber());
        load.getNumbers().setTrailerNumber(manualBol.getNumbers().getTrailerNumber());
        load.setBolInstructions(manualBol.getShippingNotes());

        load.setPayTerms(manualBol.getPayTerms());

        load.getModification().setCreatedBy(manualBol.getModification().getCreatedBy());
        load.getModification().setCreatedDate(manualBol.getModification().getCreatedDate());

        load.setCustomsBroker(manualBol.getCustomsBroker());
        load.setCustomsBrokerPhone(manualBol.getCustomsBrokerPhone());

        return load;
    }

    private Set<LoadMaterialEntity> converToLoadMaterials(ManualBolEntity manualBol, LoadDetailsEntity loadDetail) {
        Set<LoadMaterialEntity> materials = new HashSet<LoadMaterialEntity>();
        for (ManualBolMaterialEntity item : manualBol.getMaterials()) {
            LoadMaterialEntity material = new LoadMaterialEntity();
            material.setProductDescription(item.getProductDescription());
            material.setWeight(item.getWeight());
            material.setQuantity(item.getQuantity() != null ? String.valueOf(item.getQuantity()) : null);
            material.setLength(item.getLength());
            material.setWidth(item.getWidth());
            material.setHeight(item.getHeight());
            material.setCommodityClass(item.getCommodityClass());
            material.setPackageType(item.getPackageType());
            material.setPieces(item.getPieces());
            material.setNmfc(item.getNmfc());
            material.setHazmat(item.isHazmat());
            material.setHazmatClass(item.getHazmatClass());
            material.setHazmatInstruction(item.getHazmatInstruction());
            material.setUnNumber(item.getUnNumber());
            material.setPackingGroup(item.getPackingGroup());
            material.setEmergencyCompany(item.getEmergencyCompany());
            material.setEmergencyContract(item.getEmergencyContract());
            material.setEmergencyPhone(item.getEmergencyPhone());
            material.setProductCode(item.getProductCode());
            material.setLoadDetail(loadDetail);
            materials.add(material);
        }

        return materials;
    }
}
