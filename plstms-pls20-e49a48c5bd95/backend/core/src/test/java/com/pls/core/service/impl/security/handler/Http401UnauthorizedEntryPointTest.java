package com.pls.core.service.impl.security.handler;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link Http401UnauthorizedEntryPoint} class.
 * 
 * @author Maxim Medvedev
 */
public class Http401UnauthorizedEntryPointTest {

    @Test
    public void testCommence() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Http401UnauthorizedEntryPoint sut = new Http401UnauthorizedEntryPoint();

        sut.commence(null, response, null);

        Mockito.verify(response).sendError(401, "Unauthorized");
    }

}
