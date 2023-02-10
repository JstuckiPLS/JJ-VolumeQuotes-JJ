package com.pls.user.restful;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import com.pls.core.domain.enums.UserStatus;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.AuthService;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.user.restful.dto.UserCredentialsDTO;
import com.pls.user.restful.dto.UserDTO;
import com.pls.user.service.UserService;
import com.pls.user.service.exc.PasswordsDoNotMatchException;

/**
 * Test cases for {@link UserResource} class.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

    private static final Long PERSON_ID = -1L;

    @Mock
    private UserPermissionsService permissionsService;

    @InjectMocks
    private UserResource sut;

    @Mock
    private ContactInfoService contactInfoService;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Test
    public void testChangeStatusWithNormalCase() throws EntityNotFoundException {
        Long currentPersonId = PERSON_ID + 1L;
        SecurityTestUtils.login("Test", currentPersonId);
        Mockito.when(userService.findByPersonId(PERSON_ID)).thenReturn(new UserEntity());

        sut.changeStatus(PERSON_ID, true);

        Mockito.verify(userService).changeStatus(PERSON_ID, UserStatus.ACTIVE, currentPersonId);
    }

    @Test(expected = AccessDeniedException.class)
    public void testChangeStatusWithoutCapability() throws EntityNotFoundException {
        Mockito.doThrow(new AccessDeniedException("")).when(permissionsService)
                .checkCapability(Matchers.notNull(String.class));

        sut.changeStatus(PERSON_ID, true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testChangeStatusWithoutUser() throws EntityNotFoundException {
        SecurityTestUtils.login("Test", PERSON_ID);
        Mockito.doThrow(new EntityNotFoundException(""))
                .when(userService)
                .changeStatus((Long) Matchers.anyObject(), (UserStatus) Matchers.anyObject(),
                        (Long) Matchers.anyObject());

        sut.changeStatus(PERSON_ID, true);
    }

    @Test
    public void testGetUserDetailsByIdWithNormalCase() throws EntityNotFoundException {
        SecurityTestUtils.login("Test", PERSON_ID);
        UserEntity user = new UserEntity();
        Mockito.when(userService.findByPersonId(PERSON_ID)).thenReturn(user);
        UserAdditionalContactInfoBO contactInfo = new UserAdditionalContactInfoBO();
        Mockito.when(contactInfoService.getContactInfo(user)).thenReturn(contactInfo);
        UserAdditionalContactInfoBO contactInfo2 = new UserAdditionalContactInfoBO();
        Mockito.when(contactInfoService.getContactInfo(null)).thenReturn(contactInfo2);

        UserDTO result = sut.getUserDetailsById(PERSON_ID);

        Assert.assertNotNull(result);
        Assert.assertSame(contactInfo, result.getAdditionalInfo());
        Assert.assertSame(contactInfo2, result.getDefaultInfo());
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetUserDetailsByIdWithoutCapability() throws EntityNotFoundException {
        SecurityTestUtils.login("Test", 1L);
        Mockito.doThrow(new AccessDeniedException("")).when(permissionsService)
                .checkCapability(Matchers.notNull(String.class));

        sut.getUserDetailsById(PERSON_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetUserDetailsByIdWithoutUser() throws EntityNotFoundException {
        SecurityTestUtils.login("Test", PERSON_ID);
        Mockito.when(userService.findByPersonId(PERSON_ID)).thenReturn(null);

        sut.getUserDetailsById(PERSON_ID);
    }

    @Test(expected = PasswordsDoNotMatchException.class)
    public void testChangeUserPassword() throws EntityNotFoundException, PasswordsDoNotMatchException, ValidationException {
        SecurityTestUtils.login("Test", PERSON_ID);
        Mockito.doThrow(new PasswordsDoNotMatchException()).when(userService).changePassword(PERSON_ID, "123Qwerty", "12345");
        UserCredentialsDTO credentials = new UserCredentialsDTO();
        credentials.setCurrentPassword("123Qwerty");
        credentials.setNewPassword("12345");
        sut.changeUserPassword(credentials);
    }

    @Test
    public void testGetActiveNetworks() {
        SecurityTestUtils.login("Test", PERSON_ID);
        sut.getActiveNetworks();
        Mockito.verify(userService).getUserActiveNetworks(Mockito.eq(PERSON_ID));
    }
}
