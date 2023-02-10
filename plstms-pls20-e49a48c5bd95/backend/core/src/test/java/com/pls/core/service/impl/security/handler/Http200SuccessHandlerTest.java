package com.pls.core.service.impl.security.handler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link Http200SuccessHandler} class.
 * 
 * @author Maxim Medvedev
 */
public class Http200SuccessHandlerTest {

    @Test
    public void testOnAuthenticationSuccess() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        Http200SuccessHandler sut = new Http200SuccessHandler();

        sut.onAuthenticationSuccess(null, response, null);

        Mockito.verify(response).setStatus(200);
    }
}
