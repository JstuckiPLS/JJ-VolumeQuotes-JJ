package com.pls.shipment.service.audit;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.pls.shipment.domain.LoadEntity;

/**
 * Test for ({@link LoadTrackingFields}.
 * @author Dmitriy Davydenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadTrackingFieldsTest {

    @Test
    public void allTrackingFieldsShouldBePresent() {
        for (LoadTrackingFields loadTrackingField : LoadTrackingFields.values()) {
            String enumName = loadTrackingField.getName();
            runTest(LoadEntity.class, loadTrackingField, enumName);
        }
    }
    private void runTest(Class<?> classToLookIn, LoadTrackingFields loadTrackingField, String enumName) {
        Optional<String> embeddedClassName = Optional.fromNullable(loadTrackingField.getEmbeddedClassName());
        if (embeddedClassName.isPresent() && fieldIsPresentInTheClass(classToLookIn, embeddedClassName.get())) {
            Field[] fields = classToLookIn.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(embeddedClassName.get())) {
                    runTest(field.getType(), loadTrackingField, enumName);
                }
            }
        } else {
            assertTrue(fieldIsPresentInTheClass(classToLookIn, enumName));
        }
    }
    public boolean fieldIsPresentInTheClass(Class<?> classToLookIn, String name) {
        boolean fieldIsPresent = false;
        Field[] fields = classToLookIn.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return true;
            }
        }
        return fieldIsPresent;
    }
}

