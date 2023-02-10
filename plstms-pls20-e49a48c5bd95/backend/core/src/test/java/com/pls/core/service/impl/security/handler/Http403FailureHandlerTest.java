package com.pls.core.service.impl.security.handler;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

/**
 * Test cases for {@link Http403FailureHandler} class.
 * 
 * @author Maxim Medvedev
 */
public class Http403FailureHandlerTest {

    @Test
    public void testOnAuthenticationFailure() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Http403FailureHandler sut = new Http403FailureHandler();

        sut.onAuthenticationFailure(null, response, new AuthenticationCredentialsNotFoundException(
                "TestMessage"));

        Mockito.verify(response).sendError(403, "Authentication Failed: TestMessage");
    }

}
