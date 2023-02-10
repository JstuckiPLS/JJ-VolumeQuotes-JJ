package com.pls.core.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ReflectionUtils;

/**
 * The Class TestUtils.
 * 
 * @author Sergii Belodon
 */
public final class TestUtils {

    private static final String DATE_MARKER = "%DATE%";

    private static final String DATE_MARKER2 = "%DATE2%";

    private TestUtils() {
    }
    /**
     * Instantiate class path resource.
     *
     * @param fieldName the field name
     * @param object the object
     */
    public static void instantiateClassPathResource(String fieldName, Object object) {
        Field resourceField = ReflectionUtils.findField(object.getClass(), fieldName);
        String resourcePath = resourceField.getAnnotation(Value.class).value();
        resourceField.setAccessible(true);
        ReflectionUtils.setField(resourceField, object, new ClassPathResource(resourcePath));
    }

    /**
     * Instantiate field.
     *
     * @param fieldName the field name
     * @param object the object
     * @param value the value
     */
    public static void instantiateField(String fieldName, Object object, Object value) {
        Field recipientsField = ReflectionUtils.findField(object.getClass(), fieldName);
        recipientsField.setAccessible(true);
        ReflectionUtils.setField(recipientsField, object, value);
    }

    /**
     * Check pdf content.
     *
     * @param expectedContent the expected content
     * @param pdfContent the pdf content
     * @param createdDate the created date
     * @param dateFormat the date format
     * @param customDate the custom date
     */
    public static void checkPdfContent(String[] expectedContent, List<String> pdfContent, Date createdDate, String dateFormat, Date customDate) {
        assertEquals("sizes are not equal!", pdfContent.size(), expectedContent.length);
        for (int i = 0; i < pdfContent.size(); i++) {
            String excpected = expectedContent[i];
            if (excpected.contains(DATE_MARKER)) {
                String dateString = new SimpleDateFormat(dateFormat, Locale.US).format(createdDate);
                excpected = excpected.replace(DATE_MARKER, dateString);
            }
            if (excpected.contains(DATE_MARKER2) && customDate != null) {
                String dateString = new SimpleDateFormat(dateFormat, Locale.US).format(customDate);
                excpected = excpected.replace(DATE_MARKER2, dateString);
            }
            assertEquals("content are different at index " + i, excpected, pdfContent.get(i));
        }
    }

}
