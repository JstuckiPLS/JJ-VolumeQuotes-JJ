package com.pls.ltlrating.service.analysis.fileimport;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.exception.fileimport.ImportFileInvalidDataException;
import com.pls.core.service.fileimport.parser.core.Field;
import com.pls.core.service.fileimport.parser.core.Record;
import com.pls.core.service.fileimport.parser.core.RecordParser;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.analysis.FAAccessorialsEntity;
import com.pls.ltlrating.domain.analysis.FAInputDetailsEntity;
import com.pls.ltlrating.domain.analysis.FAMaterialsEntity;
import com.pls.ltlrating.domain.analysis.FAOutputDetailsEntity;


/**
 * Implementation of {@link RecordParser}.
 *
 * Records parser for freight analysis.
 *
 * @author Svetlana Kulish
 *
 */
public class AnalysisRecordParser extends RecordParser<FAInputDetailsEntity, AnalysisFieldsDescription> {

    public static final String ACCESSORIAL = "ACCESSORIAL";
    public static final String CLASS = "CLASS";
    public static final String WEIGHT = "WEIGHT";

    @Override
    public FAInputDetailsEntity parseRecord(Record record) {
        FAInputDetailsEntity entity = new FAInputDetailsEntity();

        entity.setUserSeq(readFieldValue(record, AnalysisFieldsDescription.SEQ, entity, f -> f.getLong()));
        entity.setCarrierScac(readFieldValue(record, AnalysisFieldsDescription.SCAC, entity, f -> f.getString()));

        entity.setCalculateFSC(readFieldValue(record, AnalysisFieldsDescription.CALCULATE_FSC, entity,
                f -> "Y".equalsIgnoreCase(f.getString()) ? StatusYesNo.YES : StatusYesNo.NO));

        entity.setShipmentDate(readFieldValue(record, AnalysisFieldsDescription.SHIPDATE, entity, f -> f.getDate()));

        entity.setOriginCity(readFieldValue(record, AnalysisFieldsDescription.ORIGIN_CITY, entity, f -> f.getString()));
        entity.setOriginCountry(readFieldValue(record, AnalysisFieldsDescription.ORIGIN_COUNTRY, entity, f -> f.getString()));
        entity.setOriginOverrideZip(readFieldValue(record, AnalysisFieldsDescription.ORIGIN_OVERRIDE_ZIP, entity, f -> f.getString()));
        entity.setOriginState(readFieldValue(record, AnalysisFieldsDescription.ORIGIN_STATE, entity, f -> f.getString()));
        entity.setOriginZip(readFieldValue(record, AnalysisFieldsDescription.ORIGIN_ZIP, entity, f -> f.getString()));
        entity.setDestCity(readFieldValue(record, AnalysisFieldsDescription.DEST_CITY, entity, f -> f.getString()));
        entity.setDestOverrideZip(readFieldValue(record, AnalysisFieldsDescription.DEST_OVERRIDE_ZIP, entity, f -> f.getString()));
        entity.setDestCountry(readFieldValue(record, AnalysisFieldsDescription.DEST_COUNTRY, entity, f -> f.getString()));
        entity.setDestState(readFieldValue(record, AnalysisFieldsDescription.DEST_STATE, entity, f -> f.getString()));
        entity.setDestZip(readFieldValue(record, AnalysisFieldsDescription.DEST_ZIP, entity, f -> f.getString()));

        entity.setPieces(readFieldValue(record, AnalysisFieldsDescription.PIECES, entity, f -> f.getLong()));
        entity.setPallet(readFieldValue(record, AnalysisFieldsDescription.PALLET, entity, f -> f.getLong()));

        parseAccessorials(record, entity);
        parseMaterials(record, entity);
        return entity;
    }

    private void parseMaterials(Record record, FAInputDetailsEntity entity) {
        Set<FAMaterialsEntity> materials = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            parseMaterial(record, entity, materials, i);
        }
        entity.setMaterials(materials);
    }

    private void parseMaterial(Record record, FAInputDetailsEntity entity, Set<FAMaterialsEntity> materials, int index) {
        AnalysisFieldsDescription fieldClass = AnalysisFieldsDescription.getByName(CLASS + index);
        AnalysisFieldsDescription fieldWeight = AnalysisFieldsDescription.getByName(WEIGHT + index);
        CommodityClass productClass = readFieldValue(record, fieldClass, entity, f -> convertCommodityClass(f));
        BigDecimal productWeight = readFieldValue(record, fieldWeight, entity, f -> f.getBigDecimal());
        if (productWeight != null && productClass != null) {
            FAMaterialsEntity material = new FAMaterialsEntity();
            material.setCommodityClass(productClass);
            material.setWeight(productWeight);
            material.setSeq(index);
            materials.add(material);
        }
    }

    private void parseAccessorials(Record record, FAInputDetailsEntity entity) {
        Set<FAAccessorialsEntity> accessorials = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            AnalysisFieldsDescription field = AnalysisFieldsDescription.getByName(ACCESSORIAL + i);
            String accessorial = readFieldValue(record, field, entity, f -> f.getString());
            if (StringUtils.isNotBlank(accessorial)) {
                FAAccessorialsEntity accessorialEntity = new FAAccessorialsEntity();
                accessorialEntity.setAccessorial(accessorial);
                accessorialEntity.setSeq(i);
                accessorials.add(accessorialEntity);
            }
        }
        entity.setAccessorials(accessorials);
    }

    private CommodityClass convertCommodityClass(Field f) throws ImportFileInvalidDataException {
        return StringUtils.isBlank(f.getString()) ? null : CommodityClass.convertFromDbCode(f.getString());
    }

    private <T> T readFieldValue(Record record, AnalysisFieldsDescription field, FAInputDetailsEntity entity, CheckedFunction<Field, T> s) {
        try {
            Field readField = getColumnData(record, field);
            return s.apply(readField);
        } catch (Exception e) {
            createErrorOutputDetails(entity, e.getMessage());
            return null;
        }
    }

    private void createErrorOutputDetails(FAInputDetailsEntity entity, String message) {
        if (entity.getOutputDetails() == null) {
            entity.setOutputDetails(new HashSet<FAOutputDetailsEntity>());
        }
        FAOutputDetailsEntity out = new FAOutputDetailsEntity();
        out.setErrorMessage(message);
        entity.getOutputDetails().add(out);
    }

    @Override
    protected AnalysisFieldsDescription parseHeaderColumn(String headerString) {
        String header = headerString.replace("\n", "");
        return AnalysisFieldsDescription.getByHeaderText(header);
    }

    @FunctionalInterface
    private interface CheckedFunction<T, R> {
        R apply(T t) throws ImportFileInvalidDataException;
    }
}
