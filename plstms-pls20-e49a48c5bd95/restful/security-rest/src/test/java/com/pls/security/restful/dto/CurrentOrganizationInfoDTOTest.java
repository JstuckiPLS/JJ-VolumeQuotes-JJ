package com.pls.security.restful.dto;

import org.junit.Assert;
import org.junit.Test;

import com.pls.dto.enums.CustomerStatusReason;

/**
 * Test cases for {@link CurrentOrganizationInfoDTO} class.
 * 
 * @author Maxim Medvedev
 */
public class CurrentOrganizationInfoDTOTest {

    @Test
    public void testConstructorWithNormalCase() {
        final long orgId = (long) (Math.random() * 100);
        final String name = "name" + (Math.random() * 100);
        final CustomerStatusReason reason = CustomerStatusReason.values()[(int) ((CustomerStatusReason.values().length - 1) * Math.random())];
        CurrentOrganizationInfoDTO sut = new CurrentOrganizationInfoDTO(orgId, name, reason.getValue());

        Assert.assertSame(name, sut.getName());
        Assert.assertSame(orgId, sut.getOrgId());
        Assert.assertSame(reason, sut.getStatusReason());
    }

    @Test
    public void testConstructorWithNull() {
        CurrentOrganizationInfoDTO sut = new CurrentOrganizationInfoDTO(null, null, null);

        Assert.assertNull(sut.getName());
        Assert.assertNull(sut.getOrgId());
        Assert.assertNull(sut.getStatusReason());
    }

}
