package com.pls.core.shared;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link FieldDescriptor} class.
 * 
 * @author Maxim Medvedev
 */
public class FieldDescriptorTest {

    @Test
    public void testGetFieldsWithEmpty() {
        FieldDescriptor sut = new FieldDescriptor("   ");

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(0, sut.getFields().length);
        Assert.assertFalse(sut.isFilterable());
        Assert.assertFalse(sut.isSortable());
    }

    @Test
    public void testGetFieldsWithMultiple() {
        FieldDescriptor sut = new FieldDescriptor("some.testId,some2.testId");

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(2, sut.getFields().length);
        Assert.assertEquals("some.testId", sut.getFields()[0]);
        Assert.assertEquals("some2.testId", sut.getFields()[1]);
    }

    @Test
    public void testGetFieldsWithMultipleNonTrimmed() {
        FieldDescriptor sut = new FieldDescriptor("     some.testId  ,  some2.testId  ");

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(2, sut.getFields().length);
        Assert.assertEquals("some.testId", sut.getFields()[0]);
        Assert.assertEquals("some2.testId", sut.getFields()[1]);
    }

    @Test
    public void testGetFieldsWithNull() {
        FieldDescriptor sut = new FieldDescriptor(null);

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(0, sut.getFields().length);
        Assert.assertFalse(sut.isFilterable());
        Assert.assertFalse(sut.isSortable());
    }

    @Test
    public void testGetFieldsWithSingle() {
        FieldDescriptor sut = new FieldDescriptor("some.testId");

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(1, sut.getFields().length);
        Assert.assertEquals("some.testId", sut.getFields()[0]);
    }

    @Test
    public void testGetFieldsWithSingleNonTrimmed() {
        FieldDescriptor sut = new FieldDescriptor("     some.testId    ");

        Assert.assertNotNull(sut.getFields());
        Assert.assertEquals(1, sut.getFields().length);
        Assert.assertEquals("some.testId", sut.getFields()[0]);
    }

}
