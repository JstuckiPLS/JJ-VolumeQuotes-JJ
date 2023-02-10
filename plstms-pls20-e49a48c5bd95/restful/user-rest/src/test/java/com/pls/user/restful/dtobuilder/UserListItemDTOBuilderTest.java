package com.pls.user.restful.dtobuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.pls.user.domain.bo.UserListItemBO;
import com.pls.user.restful.dto.UserListItemDTO;

/**
 * Test cases for {@link UserListItemDTOBuilder} class.
 * 
 * @author Maxim Medvedev
 */
public class UserListItemDTOBuilderTest {

    private final UserListItemDTOBuilder sut = new UserListItemDTOBuilder();

    @Test
    public void testUserListItemDTO() {
        UserListItemBO entity = createEntity();

        UserListItemDTO result = sut.buildDTO(entity);

        Assert.assertNotNull(result);
        Assert.assertEquals(entity.getEmail(), result.getEmail());
        Assert.assertNotNull(result.getFullName());
        Assert.assertEquals(entity.getUserId(), result.getUserId());
        Assert.assertEquals(entity.getPersonId(), result.getPersonId());
        Assert.assertEquals(entity.getParentOrgId(), result.getParentOrgId());
        Assert.assertEquals(entity.getParentOrgName(), result.getParentOrgName());
    }

    private UserListItemBO createEntity() {
        UserListItemBO result = Mockito.mock(UserListItemBO.class);
        Mockito.when(result.getEmail()).thenReturn(RandomStringUtils.randomAlphabetic(10));
        Mockito.when(result.getUserId()).thenReturn(RandomStringUtils.randomAlphabetic(10));
        Mockito.when(result.getPersonId()).thenReturn(System.nanoTime());
        Mockito.when(result.getFirstName()).thenReturn(RandomStringUtils.randomAlphabetic(10));
        Mockito.when(result.getLastName()).thenReturn(RandomStringUtils.randomAlphabetic(10));
        Mockito.when(result.getParentOrgId()).thenReturn(System.nanoTime());
        Mockito.when(result.getParentOrgName()).thenReturn(RandomStringUtils.randomAlphabetic(10));
        return result;
    }

}
