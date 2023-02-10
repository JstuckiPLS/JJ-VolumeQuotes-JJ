package com.pls.restful.rating;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.service.rating.RatingAccessorialTypeService;
import com.pls.core.shared.Status;
import com.pls.dto.ValueLabelDTO;
/**
 *  Test case for {@link AccessorialTypeResource} class.
 * 
 * @author Alexander Nalapko, Dmitriy Nefedchenko
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AccessorialTypeResourceTest {
    @InjectMocks private AccessorialTypeResource testee;

    @Mock private RatingAccessorialTypeService accessorialTypeService;

    @Test
    public void shouldGetAllAccessorialTypes() {
        testee.getAllAccessorialTypes();

        verify(accessorialTypeService, times(1)).getAllAccessorialType();
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldGetAllActiveAccessorialTypes() {
        testee.getActiveAccessorialType();

        verify(accessorialTypeService, times(1)).getAllAccessorialTypeByStatus(Status.ACTIVE);
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldGetAllInactiveAccessorialTypes() {
        testee.getInactiveAccessorialType();

        verify(accessorialTypeService, times(1)).getAllAccessorialTypeByStatus(Status.INACTIVE);
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldGetAccessorialTypeByCode() {
        testee.getAccessorialByCode("some code");

        verify(accessorialTypeService, times(1)).getByCode("some code");
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldGetAccessorialTypesApplicableTo() {
        List<ValueLabelDTO> applicables = testee.getAccessorialTypesApplicableTo();

        assertFalse(applicables.isEmpty());
    }

    @Test
    public void shouldSaveAccessorial() {
        AccessorialTypeEntity accessorial = new AccessorialTypeEntity("code");

        testee.saveAccessorial(accessorial);

        verify(accessorialTypeService, times(1)).saveAccessorialType(accessorial);
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldDeactivateAccessorial() {
        List<String> accessorialTypeCodes = new ArrayList<String>();

        testee.inactivateAccessorialTypes(accessorialTypeCodes);

        verify(accessorialTypeService, times(1)).updateStatus(accessorialTypeCodes, Status.INACTIVE);
        verify(accessorialTypeService, times(1)).getAllAccessorialType();
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldActivateAccessorial() {
        List<String> accessorialTypeCodes = new ArrayList<String>();

        testee.activateAccessorialTypes(accessorialTypeCodes);

        verify(accessorialTypeService, times(1)).updateStatus(accessorialTypeCodes, Status.ACTIVE);
        verify(accessorialTypeService, times(1)).getAllAccessorialType();
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldCheckAccessorialTypeExists() {
        String accessorialTypeCode = "HCD";

        testee.checkAccessorialCodeExists(accessorialTypeCode);

        verify(accessorialTypeService, times(1)).checkAccessorialCodeExists(accessorialTypeCode);
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldListAccessorialTypesByGroup() {
        String group = "Pickup";

        testee.listAccessorialsByGroup(group);

        verify(accessorialTypeService, times(1)).listAccessorialTypesByGroup(group);
        verifyNoMoreInteractions(accessorialTypeService);
    }

    @Test
    public void shouldVerifyAccessorialTypeCodeUniqueness() {
        String accessorialTypeCode = "HCD";

        testee.isAccessorialTypeIdUnique(accessorialTypeCode);

        verify(accessorialTypeService, times(1)).isAccessorialTypeUnique(accessorialTypeCode);
        verifyNoMoreInteractions(accessorialTypeService);
    }
}
