package com.pls.core.service.impl.security.handler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link Http200LogoutSuccessHandler} class.
 * 
 * @author Maxim Medvedev
 */
public class Http200LogoutSuccessHandlerTest {

    @Test
    public void testOnLogoutSuccess() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        Http200LogoutSuccessHandler sut = new Http200LogoutSuccessHandler();

        sut.onLogoutSuccess(null, response, null);

        Mockito.verify(response).setStatus(200);
    }
}
