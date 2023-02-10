package com.pls.core.service.impl.rating;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
/**
 * Test case for {@link RatingAccessorialTypeServiceImpl} class.
 * 
 * @author Alexander Nalapko, Dmitriy Nefedchenko
 *  *
 */
@RunWith(MockitoJUnitRunner.class)
public class RatingAccessorialTypeServiceImplTest {
    @InjectMocks private RatingAccessorialTypeServiceImpl testee;

    @Mock private AccessorialTypeDao accessorialTypeDao;

    @Test
    public void shouldGetAllAccessorialTypes() {
        testee.getAllAccessorialType();

        verify(accessorialTypeDao, times(1)).getAll();
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldGetAllAccessorialTypesByStatus() {
        testee.getAllAccessorialTypeByStatus(Status.ACTIVE);
        testee.getAllAccessorialTypeByStatus(Status.INACTIVE);

        verify(accessorialTypeDao, times(1)).findAccessorialTypesByStatus(Status.ACTIVE);
        verify(accessorialTypeDao, times(1)).findAccessorialTypesByStatus(Status.INACTIVE);
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldGetAccessorialTypesByCode() {
        testee.getByCode(Matchers.anyString());

        verify(accessorialTypeDao, times(1)).find(anyString());
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldSaveAccessorialType() {
        testee.saveAccessorialType((AccessorialTypeEntity) anyObject());

        verify(accessorialTypeDao, times(1)).saveOrUpdate((AccessorialTypeEntity) anyObject());
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldUpdateStatus() {
        List<String> accessorialTypeCodes = Arrays.asList("HCD");
        testee.updateStatus(accessorialTypeCodes, Status.ACTIVE);

        verify(accessorialTypeDao, times(1)).updateStatus(accessorialTypeCodes, Status.ACTIVE, SecurityUtils.getCurrentPersonId());
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldCheckAccessorialTypeCodeExists() {
        testee.checkAccessorialCodeExists(anyString());

        verify(accessorialTypeDao, times(1)).checkAccessorialCodeExists(anyString());
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldListAccessorialTypesByGroup() {
        testee.listAccessorialTypesByGroup(anyString());

        verify(accessorialTypeDao, times(1)).listPickupAccessorialTypes(anyString());
        verifyNoMoreInteractions(accessorialTypeDao);
    }

    @Test
    public void shouldCheckAccessorialTypeUniqueness() {
        testee.isAccessorialTypeUnique(anyString());

        verify(accessorialTypeDao, times(1)).isAccessorialTypeUnique(anyString());
        verifyNoMoreInteractions(accessorialTypeDao);
    }
}
