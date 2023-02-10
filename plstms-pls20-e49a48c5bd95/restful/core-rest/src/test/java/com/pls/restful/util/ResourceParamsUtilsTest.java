package com.pls.restful.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.exception.ApplicationException;

/**
 * 
 * Test cases for {@link ResourceParamsUtils} class.
 * 
 * @author Brichak Aleksandr
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceParamsUtilsTest {

    private static String validParam0 = "1";
    private static String validParam1 = "12";
    private static String validParam2 = "*1a3*";
    private static String validParam3 = "*   *";
    private static String validParam4 = "   ";
    private static String inValidParam0 = "*1";
    private static String inValidParam1 = "12*";
    private static String inValidParam2 = "*1*";

    @Test
    public void shouldCheckAndPrepareValidWildCardSearchParameter() throws ApplicationException {
        String result = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(validParam0);
        Assert.assertNotNull(result);
        Assert.assertEquals(validParam0, result);

        result = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(validParam1);
        Assert.assertNotNull(result);
        Assert.assertEquals(validParam1, result);

        result = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(validParam2);
        Assert.assertNotNull(result);
        Assert.assertEquals("%1a3%", result);

        result = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(validParam3);
        Assert.assertNotNull(result);
        Assert.assertEquals("%   %", result);

        result = ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(validParam4);
        Assert.assertNotNull(result);
        Assert.assertEquals(validParam4, result);
    }

    @Test(expected = ApplicationException.class)
    public void shouldCheckAndPrepareInvalidWildCardSearchParameter0() throws ApplicationException {
        ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(inValidParam0);
    }

    @Test(expected = ApplicationException.class)
    public void shouldCheckAndPrepareInvalidWildCardSearchParameter1() throws ApplicationException {
        ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(inValidParam1);
    }

    @Test(expected = ApplicationException.class)
    public void shouldCheckAndPrepareInvalidWildCardSearchParameter2() throws ApplicationException {
        ResourceParamsUtils.checkAndPrepareWildCardSearchParameter(inValidParam2);
    }
}
